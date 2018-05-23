package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class ExternalClient
  implements SaslClient
{
  private byte[] username;
  private boolean completed = false;
  
  ExternalClient(String paramString)
    throws SaslException
  {
    if (paramString != null) {
      try
      {
        username = paramString.getBytes("UTF8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new SaslException("Cannot convert " + paramString + " into UTF-8", localUnsupportedEncodingException);
      }
    } else {
      username = new byte[0];
    }
  }
  
  public String getMechanismName()
  {
    return "EXTERNAL";
  }
  
  public boolean hasInitialResponse()
  {
    return true;
  }
  
  public void dispose()
    throws SaslException
  {}
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte)
    throws SaslException
  {
    if (completed) {
      throw new IllegalStateException("EXTERNAL authentication already completed");
    }
    completed = true;
    return username;
  }
  
  public boolean isComplete()
  {
    return completed;
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (completed) {
      throw new SaslException("EXTERNAL has no supported QOP");
    }
    throw new IllegalStateException("EXTERNAL authentication Not completed");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (completed) {
      throw new SaslException("EXTERNAL has no supported QOP");
    }
    throw new IllegalStateException("EXTERNAL authentication not completed");
  }
  
  public Object getNegotiatedProperty(String paramString)
  {
    if (completed) {
      return null;
    }
    throw new IllegalStateException("EXTERNAL authentication not completed");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\ExternalClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */