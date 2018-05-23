package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class PlainClient
  implements SaslClient
{
  private boolean completed = false;
  private byte[] pw;
  private String authorizationID;
  private String authenticationID;
  private static byte SEP = 0;
  
  PlainClient(String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws SaslException
  {
    if ((paramString2 == null) || (paramArrayOfByte == null)) {
      throw new SaslException("PLAIN: authorization ID and password must be specified");
    }
    authorizationID = paramString1;
    authenticationID = paramString2;
    pw = paramArrayOfByte;
  }
  
  public String getMechanismName()
  {
    return "PLAIN";
  }
  
  public boolean hasInitialResponse()
  {
    return true;
  }
  
  public void dispose()
    throws SaslException
  {
    clearPassword();
  }
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte)
    throws SaslException
  {
    if (completed) {
      throw new IllegalStateException("PLAIN authentication already completed");
    }
    completed = true;
    try
    {
      Object localObject = authorizationID != null ? authorizationID.getBytes("UTF8") : null;
      byte[] arrayOfByte1 = authenticationID.getBytes("UTF8");
      byte[] arrayOfByte2 = new byte[pw.length + arrayOfByte1.length + 2 + (localObject == null ? 0 : localObject.length)];
      int i = 0;
      if (localObject != null)
      {
        System.arraycopy(localObject, 0, arrayOfByte2, 0, localObject.length);
        i = localObject.length;
      }
      arrayOfByte2[(i++)] = SEP;
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, i, arrayOfByte1.length);
      i += arrayOfByte1.length;
      arrayOfByte2[(i++)] = SEP;
      System.arraycopy(pw, 0, arrayOfByte2, i, pw.length);
      clearPassword();
      return arrayOfByte2;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new SaslException("Cannot get UTF-8 encoding of ids", localUnsupportedEncodingException);
    }
  }
  
  public boolean isComplete()
  {
    return completed;
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (completed) {
      throw new SaslException("PLAIN supports neither integrity nor privacy");
    }
    throw new IllegalStateException("PLAIN authentication not completed");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (completed) {
      throw new SaslException("PLAIN supports neither integrity nor privacy");
    }
    throw new IllegalStateException("PLAIN authentication not completed");
  }
  
  public Object getNegotiatedProperty(String paramString)
  {
    if (completed)
    {
      if (paramString.equals("javax.security.sasl.qop")) {
        return "auth";
      }
      return null;
    }
    throw new IllegalStateException("PLAIN authentication not completed");
  }
  
  private void clearPassword()
  {
    if (pw != null)
    {
      for (int i = 0; i < pw.length; i++) {
        pw[i] = 0;
      }
      pw = null;
    }
  }
  
  protected void finalize()
  {
    clearPassword();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\PlainClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */