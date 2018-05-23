package com.sun.security.sasl.gsskerb;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.MessageProp;

final class GssKrb5Client
  extends GssKrb5Base
  implements SaslClient
{
  private static final String MY_CLASS_NAME = GssKrb5Client.class.getName();
  private boolean finalHandshake = false;
  private boolean mutual = false;
  private byte[] authzID;
  
  GssKrb5Client(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    super(paramMap, MY_CLASS_NAME);
    String str = paramString2 + "@" + paramString3;
    logger.log(Level.FINE, "KRB5CLNT01:Requesting service name: {0}", str);
    try
    {
      GSSManager localGSSManager = GSSManager.getInstance();
      GSSName localGSSName = localGSSManager.createName(str, GSSName.NT_HOSTBASED_SERVICE, KRB5_OID);
      GSSCredential localGSSCredential = null;
      Object localObject;
      if (paramMap != null)
      {
        localObject = paramMap.get("javax.security.sasl.credentials");
        if ((localObject != null) && ((localObject instanceof GSSCredential)))
        {
          localGSSCredential = (GSSCredential)localObject;
          logger.log(Level.FINE, "KRB5CLNT01:Using the credentials supplied in javax.security.sasl.credentials");
        }
      }
      secCtx = localGSSManager.createContext(localGSSName, KRB5_OID, localGSSCredential, Integer.MAX_VALUE);
      if (localGSSCredential != null) {
        secCtx.requestCredDeleg(true);
      }
      if (paramMap != null)
      {
        localObject = (String)paramMap.get("javax.security.sasl.server.authentication");
        if (localObject != null) {
          mutual = "true".equalsIgnoreCase((String)localObject);
        }
      }
      secCtx.requestMutualAuth(mutual);
      secCtx.requestConf(true);
      secCtx.requestInteg(true);
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("Failure to initialize security context", localGSSException);
    }
    if ((paramString1 != null) && (paramString1.length() > 0)) {
      try
      {
        authzID = paramString1.getBytes("UTF8");
      }
      catch (IOException localIOException)
      {
        throw new SaslException("Cannot encode authorization ID", localIOException);
      }
    }
  }
  
  public boolean hasInitialResponse()
  {
    return true;
  }
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte)
    throws SaslException
  {
    if (completed) {
      throw new IllegalStateException("GSSAPI authentication already complete");
    }
    if (finalHandshake) {
      return doFinalHandshake(paramArrayOfByte);
    }
    try
    {
      byte[] arrayOfByte = secCtx.initSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
      if (logger.isLoggable(Level.FINER))
      {
        traceOutput(MY_CLASS_NAME, "evaluteChallenge", "KRB5CLNT02:Challenge: [raw]", paramArrayOfByte);
        traceOutput(MY_CLASS_NAME, "evaluateChallenge", "KRB5CLNT03:Response: [after initSecCtx]", arrayOfByte);
      }
      if (secCtx.isEstablished())
      {
        finalHandshake = true;
        if (arrayOfByte == null) {
          return EMPTY;
        }
      }
      return arrayOfByte;
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("GSS initiate failed", localGSSException);
    }
  }
  
  private byte[] doFinalHandshake(byte[] paramArrayOfByte)
    throws SaslException
  {
    try
    {
      if (logger.isLoggable(Level.FINER)) {
        traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT04:Challenge [raw]:", paramArrayOfByte);
      }
      if (paramArrayOfByte.length == 0) {
        return EMPTY;
      }
      byte[] arrayOfByte1 = secCtx.unwrap(paramArrayOfByte, 0, paramArrayOfByte.length, new MessageProp(0, false));
      if (logger.isLoggable(Level.FINE))
      {
        if (logger.isLoggable(Level.FINER)) {
          traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT05:Challenge [unwrapped]:", arrayOfByte1);
        }
        logger.log(Level.FINE, "KRB5CLNT06:Server protections: {0}", new Byte(arrayOfByte1[0]));
      }
      byte b = findPreferredMask(arrayOfByte1[0], qop);
      if (b == 0) {
        throw new SaslException("No common protection layer between client and server");
      }
      if ((b & 0x4) != 0)
      {
        privacy = true;
        integrity = true;
      }
      else if ((b & 0x2) != 0)
      {
        integrity = true;
      }
      int i = networkByteOrderToInt(arrayOfByte1, 1, 3);
      sendMaxBufSize = (sendMaxBufSize == 0 ? i : Math.min(sendMaxBufSize, i));
      rawSendSize = secCtx.getWrapSizeLimit(0, privacy, sendMaxBufSize);
      if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "KRB5CLNT07:Client max recv size: {0}; server max recv size: {1}; rawSendSize: {2}", new Object[] { new Integer(recvMaxBufSize), new Integer(i), new Integer(rawSendSize) });
      }
      int j = 4;
      if (authzID != null) {
        j += authzID.length;
      }
      byte[] arrayOfByte2 = new byte[j];
      arrayOfByte2[0] = b;
      if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "KRB5CLNT08:Selected protection: {0}; privacy: {1}; integrity: {2}", new Object[] { new Byte(b), Boolean.valueOf(privacy), Boolean.valueOf(integrity) });
      }
      intToNetworkByteOrder(recvMaxBufSize, arrayOfByte2, 1, 3);
      if (authzID != null)
      {
        System.arraycopy(authzID, 0, arrayOfByte2, 4, authzID.length);
        logger.log(Level.FINE, "KRB5CLNT09:Authzid: {0}", authzID);
      }
      if (logger.isLoggable(Level.FINER)) {
        traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT10:Response [raw]", arrayOfByte2);
      }
      arrayOfByte1 = secCtx.wrap(arrayOfByte2, 0, arrayOfByte2.length, new MessageProp(0, false));
      if (logger.isLoggable(Level.FINER)) {
        traceOutput(MY_CLASS_NAME, "doFinalHandshake", "KRB5CLNT11:Response [after wrap]", arrayOfByte1);
      }
      completed = true;
      return arrayOfByte1;
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("Final handshake failed", localGSSException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\gsskerb\GssKrb5Client.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */