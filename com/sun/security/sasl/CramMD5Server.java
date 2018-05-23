package com.sun.security.sasl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class CramMD5Server
  extends CramMD5Base
  implements SaslServer
{
  private String fqdn;
  private byte[] challengeData = null;
  private String authzid;
  private CallbackHandler cbh;
  
  CramMD5Server(String paramString1, String paramString2, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    if (paramString2 == null) {
      throw new SaslException("CRAM-MD5: fully qualified server name must be specified");
    }
    fqdn = paramString2;
    cbh = paramCallbackHandler;
  }
  
  public byte[] evaluateResponse(byte[] paramArrayOfByte)
    throws SaslException
  {
    if (completed) {
      throw new IllegalStateException("CRAM-MD5 authentication already completed");
    }
    if (aborted) {
      throw new IllegalStateException("CRAM-MD5 authentication previously aborted due to error");
    }
    try
    {
      if (challengeData == null)
      {
        if (paramArrayOfByte.length != 0)
        {
          aborted = true;
          throw new SaslException("CRAM-MD5 does not expect any initial response");
        }
        Random localRandom = new Random();
        long l1 = localRandom.nextLong();
        long l2 = System.currentTimeMillis();
        localObject = new StringBuffer();
        ((StringBuffer)localObject).append('<');
        ((StringBuffer)localObject).append(l1);
        ((StringBuffer)localObject).append('.');
        ((StringBuffer)localObject).append(l2);
        ((StringBuffer)localObject).append('@');
        ((StringBuffer)localObject).append(fqdn);
        ((StringBuffer)localObject).append('>');
        String str2 = ((StringBuffer)localObject).toString();
        logger.log(Level.FINE, "CRAMSRV01:Generated challenge: {0}", str2);
        challengeData = str2.getBytes("UTF8");
        return (byte[])challengeData.clone();
      }
      if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "CRAMSRV02:Received response: {0}", new String(paramArrayOfByte, "UTF8"));
      }
      int i = 0;
      for (int j = 0; j < paramArrayOfByte.length; j++) {
        if (paramArrayOfByte[j] == 32)
        {
          i = j;
          break;
        }
      }
      if (i == 0)
      {
        aborted = true;
        throw new SaslException("CRAM-MD5: Invalid response; space missing");
      }
      String str1 = new String(paramArrayOfByte, 0, i, "UTF8");
      logger.log(Level.FINE, "CRAMSRV03:Extracted username: {0}", str1);
      NameCallback localNameCallback = new NameCallback("CRAM-MD5 authentication ID: ", str1);
      PasswordCallback localPasswordCallback = new PasswordCallback("CRAM-MD5 password: ", false);
      cbh.handle(new Callback[] { localNameCallback, localPasswordCallback });
      char[] arrayOfChar = localPasswordCallback.getPassword();
      if ((arrayOfChar == null) || (arrayOfChar.length == 0))
      {
        aborted = true;
        throw new SaslException("CRAM-MD5: username not found: " + str1);
      }
      localPasswordCallback.clearPassword();
      Object localObject = new String(arrayOfChar);
      for (int k = 0; k < arrayOfChar.length; k++) {
        arrayOfChar[k] = '\000';
      }
      pw = ((String)localObject).getBytes("UTF8");
      String str3 = HMAC_MD5(pw, challengeData);
      logger.log(Level.FINE, "CRAMSRV04:Expecting digest: {0}", str3);
      clearPassword();
      byte[] arrayOfByte = str3.getBytes("UTF8");
      int m = paramArrayOfByte.length - i - 1;
      if (arrayOfByte.length != m)
      {
        aborted = true;
        throw new SaslException("Invalid response");
      }
      int n = 0;
      for (int i1 = i + 1; i1 < paramArrayOfByte.length; i1++) {
        if (arrayOfByte[(n++)] != paramArrayOfByte[i1])
        {
          aborted = true;
          throw new SaslException("Invalid response");
        }
      }
      AuthorizeCallback localAuthorizeCallback = new AuthorizeCallback(str1, str1);
      cbh.handle(new Callback[] { localAuthorizeCallback });
      if (localAuthorizeCallback.isAuthorized())
      {
        authzid = localAuthorizeCallback.getAuthorizedID();
      }
      else
      {
        aborted = true;
        throw new SaslException("CRAM-MD5: user not authorized: " + str1);
      }
      logger.log(Level.FINE, "CRAMSRV05:Authorization id: {0}", authzid);
      completed = true;
      return null;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      aborted = true;
      throw new SaslException("UTF8 not available on platform", localUnsupportedEncodingException);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      aborted = true;
      throw new SaslException("MD5 algorithm not available on platform", localNoSuchAlgorithmException);
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      aborted = true;
      throw new SaslException("CRAM-MD5 authentication failed", localUnsupportedCallbackException);
    }
    catch (SaslException localSaslException)
    {
      throw localSaslException;
    }
    catch (IOException localIOException)
    {
      aborted = true;
      throw new SaslException("CRAM-MD5 authentication failed", localIOException);
    }
  }
  
  public String getAuthorizationID()
  {
    if (completed) {
      return authzid;
    }
    throw new IllegalStateException("CRAM-MD5 authentication not completed");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\CramMD5Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */