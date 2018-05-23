package org.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;
import java.security.Signature;
import java.security.SignatureException;

public class SignerOutputStream
  extends ByteArrayOutputStream
{
  private final Signature sig;
  
  public SignerOutputStream(Signature paramSignature)
  {
    sig = paramSignature;
  }
  
  public void write(int paramInt)
  {
    super.write(paramInt);
    try
    {
      sig.update((byte)paramInt);
    }
    catch (SignatureException localSignatureException)
    {
      throw new RuntimeException(localSignatureException);
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super.write(paramArrayOfByte, paramInt1, paramInt2);
    try
    {
      sig.update(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (SignatureException localSignatureException)
    {
      throw new RuntimeException(localSignatureException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\SignerOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */