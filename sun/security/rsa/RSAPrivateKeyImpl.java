package sun.security.rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public final class RSAPrivateKeyImpl
  extends PKCS8Key
  implements RSAPrivateKey
{
  private static final long serialVersionUID = -33106691987952810L;
  private final BigInteger n;
  private final BigInteger d;
  
  RSAPrivateKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
    throws InvalidKeyException
  {
    n = paramBigInteger1;
    d = paramBigInteger2;
    RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), null);
    algid = RSAPrivateCrtKeyImpl.rsaId;
    try
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      localDerOutputStream.putInteger(0);
      localDerOutputStream.putInteger(paramBigInteger1);
      localDerOutputStream.putInteger(0);
      localDerOutputStream.putInteger(paramBigInteger2);
      localDerOutputStream.putInteger(0);
      localDerOutputStream.putInteger(0);
      localDerOutputStream.putInteger(0);
      localDerOutputStream.putInteger(0);
      localDerOutputStream.putInteger(0);
      DerValue localDerValue = new DerValue((byte)48, localDerOutputStream.toByteArray());
      key = localDerValue.toByteArray();
    }
    catch (IOException localIOException)
    {
      throw new InvalidKeyException(localIOException);
    }
  }
  
  public String getAlgorithm()
  {
    return "RSA";
  }
  
  public BigInteger getModulus()
  {
    return n;
  }
  
  public BigInteger getPrivateExponent()
  {
    return d;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\rsa\RSAPrivateKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */