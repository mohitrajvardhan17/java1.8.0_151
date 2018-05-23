package com.sun.security.sasl.gsskerb;

import com.sun.security.sasl.util.AbstractSaslImpl;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.SaslException;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

abstract class GssKrb5Base
  extends AbstractSaslImpl
{
  private static final String KRB5_OID_STR = "1.2.840.113554.1.2.2";
  protected static Oid KRB5_OID;
  protected static final byte[] EMPTY = new byte[0];
  protected GSSContext secCtx = null;
  protected static final int JGSS_QOP = 0;
  
  protected GssKrb5Base(Map<String, ?> paramMap, String paramString)
    throws SaslException
  {
    super(paramMap, paramString);
  }
  
  public String getMechanismName()
  {
    return "GSSAPI";
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (!completed) {
      throw new IllegalStateException("GSSAPI authentication not completed");
    }
    if (!integrity) {
      throw new IllegalStateException("No security layer negotiated");
    }
    try
    {
      MessageProp localMessageProp = new MessageProp(0, privacy);
      byte[] arrayOfByte = secCtx.unwrap(paramArrayOfByte, paramInt1, paramInt2, localMessageProp);
      if (logger.isLoggable(Level.FINEST))
      {
        traceOutput(myClassName, "KRB501:Unwrap", "incoming: ", paramArrayOfByte, paramInt1, paramInt2);
        traceOutput(myClassName, "KRB502:Unwrap", "unwrapped: ", arrayOfByte, 0, arrayOfByte.length);
      }
      return arrayOfByte;
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("Problems unwrapping SASL buffer", localGSSException);
    }
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (!completed) {
      throw new IllegalStateException("GSSAPI authentication not completed");
    }
    if (!integrity) {
      throw new IllegalStateException("No security layer negotiated");
    }
    try
    {
      MessageProp localMessageProp = new MessageProp(0, privacy);
      byte[] arrayOfByte = secCtx.wrap(paramArrayOfByte, paramInt1, paramInt2, localMessageProp);
      if (logger.isLoggable(Level.FINEST))
      {
        traceOutput(myClassName, "KRB503:Wrap", "outgoing: ", paramArrayOfByte, paramInt1, paramInt2);
        traceOutput(myClassName, "KRB504:Wrap", "wrapped: ", arrayOfByte, 0, arrayOfByte.length);
      }
      return arrayOfByte;
    }
    catch (GSSException localGSSException)
    {
      throw new SaslException("Problem performing GSS wrap", localGSSException);
    }
  }
  
  public void dispose()
    throws SaslException
  {
    if (secCtx != null)
    {
      try
      {
        secCtx.dispose();
      }
      catch (GSSException localGSSException)
      {
        throw new SaslException("Problem disposing GSS context", localGSSException);
      }
      secCtx = null;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    dispose();
  }
  
  static
  {
    try
    {
      KRB5_OID = new Oid("1.2.840.113554.1.2.2");
    }
    catch (GSSException localGSSException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\gsskerb\GssKrb5Base.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */