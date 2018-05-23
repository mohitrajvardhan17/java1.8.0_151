package com.sun.security.sasl.digest;

import javax.security.sasl.SaslException;

abstract interface SecurityCtx
{
  public abstract byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;
  
  public abstract byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\digest\SecurityCtx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */