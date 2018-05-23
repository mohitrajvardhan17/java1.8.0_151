package com.sun.security.sasl.ntlm;

import com.sun.security.ntlm.NTLMException;
import com.sun.security.ntlm.Server;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class NTLMServer
  implements SaslServer
{
  private static final String NTLM_VERSION = "com.sun.security.sasl.ntlm.version";
  private static final String NTLM_DOMAIN = "com.sun.security.sasl.ntlm.domain";
  private static final String NTLM_HOSTNAME = "com.sun.security.sasl.ntlm.hostname";
  private static final String NTLM_RANDOM = "com.sun.security.sasl.ntlm.random";
  private final Random random;
  private final Server server;
  private byte[] nonce;
  private int step = 0;
  private String authzId;
  private final String mech;
  private String hostname;
  private String target;
  
  NTLMServer(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, final CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    mech = paramString1;
    String str1 = null;
    String str2 = null;
    Random localRandom = null;
    if (paramMap != null)
    {
      str2 = (String)paramMap.get("com.sun.security.sasl.ntlm.domain");
      str1 = (String)paramMap.get("com.sun.security.sasl.ntlm.version");
      localRandom = (Random)paramMap.get("com.sun.security.sasl.ntlm.random");
    }
    random = (localRandom != null ? localRandom : new Random());
    if (str1 == null) {
      str1 = System.getProperty("ntlm.version");
    }
    if (str2 == null) {
      str2 = paramString3;
    }
    if (str2 == null) {
      throw new SaslException("Domain must be provided as the serverName argument or in props");
    }
    try
    {
      server = new Server(str1, str2)
      {
        public char[] getPassword(String paramAnonymousString1, String paramAnonymousString2)
        {
          try
          {
            RealmCallback localRealmCallback = (paramAnonymousString1 == null) || (paramAnonymousString1.isEmpty()) ? new RealmCallback("Domain: ") : new RealmCallback("Domain: ", paramAnonymousString1);
            NameCallback localNameCallback = new NameCallback("Name: ", paramAnonymousString2);
            PasswordCallback localPasswordCallback = new PasswordCallback("Password: ", false);
            paramCallbackHandler.handle(new Callback[] { localRealmCallback, localNameCallback, localPasswordCallback });
            char[] arrayOfChar = localPasswordCallback.getPassword();
            localPasswordCallback.clearPassword();
            return arrayOfChar;
          }
          catch (IOException localIOException)
          {
            return null;
          }
          catch (UnsupportedCallbackException localUnsupportedCallbackException) {}
          return null;
        }
      };
    }
    catch (NTLMException localNTLMException)
    {
      throw new SaslException("NTLM: server creation failure", localNTLMException);
    }
    nonce = new byte[8];
  }
  
  public String getMechanismName()
  {
    return mech;
  }
  
  public byte[] evaluateResponse(byte[] paramArrayOfByte)
    throws SaslException
  {
    try
    {
      step += 1;
      if (step == 1)
      {
        random.nextBytes(nonce);
        return server.type2(paramArrayOfByte, nonce);
      }
      String[] arrayOfString = server.verify(paramArrayOfByte, nonce);
      authzId = arrayOfString[0];
      hostname = arrayOfString[1];
      target = arrayOfString[2];
      return null;
    }
    catch (NTLMException localNTLMException)
    {
      throw new SaslException("NTLM: generate response failure", localNTLMException);
    }
  }
  
  public boolean isComplete()
  {
    return step >= 2;
  }
  
  public String getAuthorizationID()
  {
    if (!isComplete()) {
      throw new IllegalStateException("authentication not complete");
    }
    return authzId;
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    throw new IllegalStateException("Not supported yet.");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    throw new IllegalStateException("Not supported yet.");
  }
  
  public Object getNegotiatedProperty(String paramString)
  {
    if (!isComplete()) {
      throw new IllegalStateException("authentication not complete");
    }
    switch (paramString)
    {
    case "javax.security.sasl.qop": 
      return "auth";
    case "javax.security.sasl.bound.server.name": 
      return target;
    case "com.sun.security.sasl.ntlm.hostname": 
      return hostname;
    }
    return null;
  }
  
  public void dispose()
    throws SaslException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\ntlm\NTLMServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */