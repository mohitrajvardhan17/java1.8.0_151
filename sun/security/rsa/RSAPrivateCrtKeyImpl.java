package sun.security.rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public final class RSAPrivateCrtKeyImpl
  extends PKCS8Key
  implements RSAPrivateCrtKey
{
  private static final long serialVersionUID = -1326088454257084918L;
  private BigInteger n;
  private BigInteger e;
  private BigInteger d;
  private BigInteger p;
  private BigInteger q;
  private BigInteger pe;
  private BigInteger qe;
  private BigInteger coeff;
  static final AlgorithmId rsaId = new AlgorithmId(AlgorithmId.RSAEncryption_oid);
  
  public static RSAPrivateKey newKey(byte[] paramArrayOfByte)
    throws InvalidKeyException
  {
    RSAPrivateCrtKeyImpl localRSAPrivateCrtKeyImpl = new RSAPrivateCrtKeyImpl(paramArrayOfByte);
    if (localRSAPrivateCrtKeyImpl.getPublicExponent().signum() == 0) {
      return new RSAPrivateKeyImpl(localRSAPrivateCrtKeyImpl.getModulus(), localRSAPrivateCrtKeyImpl.getPrivateExponent());
    }
    return localRSAPrivateCrtKeyImpl;
  }
  
  RSAPrivateCrtKeyImpl(byte[] paramArrayOfByte)
    throws InvalidKeyException
  {
    decode(paramArrayOfByte);
    RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(), e);
  }
  
  RSAPrivateCrtKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8)
    throws InvalidKeyException
  {
    n = paramBigInteger1;
    e = paramBigInteger2;
    d = paramBigInteger3;
    p = paramBigInteger4;
    q = paramBigInteger5;
    pe = paramBigInteger6;
    qe = paramBigInteger7;
    coeff = paramBigInteger8;
    RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), paramBigInteger2);
    algid = rsaId;
    try
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      localDerOutputStream.putInteger(0);
      localDerOutputStream.putInteger(paramBigInteger1);
      localDerOutputStream.putInteger(paramBigInteger2);
      localDerOutputStream.putInteger(paramBigInteger3);
      localDerOutputStream.putInteger(paramBigInteger4);
      localDerOutputStream.putInteger(paramBigInteger5);
      localDerOutputStream.putInteger(paramBigInteger6);
      localDerOutputStream.putInteger(paramBigInteger7);
      localDerOutputStream.putInteger(paramBigInteger8);
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
  
  public BigInteger getPublicExponent()
  {
    return e;
  }
  
  public BigInteger getPrivateExponent()
  {
    return d;
  }
  
  public BigInteger getPrimeP()
  {
    return p;
  }
  
  public BigInteger getPrimeQ()
  {
    return q;
  }
  
  public BigInteger getPrimeExponentP()
  {
    return pe;
  }
  
  public BigInteger getPrimeExponentQ()
  {
    return qe;
  }
  
  public BigInteger getCrtCoefficient()
  {
    return coeff;
  }
  
  protected void parseKeyBits()
    throws InvalidKeyException
  {
    try
    {
      DerInputStream localDerInputStream1 = new DerInputStream(key);
      DerValue localDerValue = localDerInputStream1.getDerValue();
      if (tag != 48) {
        throw new IOException("Not a SEQUENCE");
      }
      DerInputStream localDerInputStream2 = data;
      int i = localDerInputStream2.getInteger();
      if (i != 0) {
        throw new IOException("Version must be 0");
      }
      n = localDerInputStream2.getPositiveBigInteger();
      e = localDerInputStream2.getPositiveBigInteger();
      d = localDerInputStream2.getPositiveBigInteger();
      p = localDerInputStream2.getPositiveBigInteger();
      q = localDerInputStream2.getPositiveBigInteger();
      pe = localDerInputStream2.getPositiveBigInteger();
      qe = localDerInputStream2.getPositiveBigInteger();
      coeff = localDerInputStream2.getPositiveBigInteger();
      if (data.available() != 0) {
        throw new IOException("Extra data available");
      }
    }
    catch (IOException localIOException)
    {
      throw new InvalidKeyException("Invalid RSA private key", localIOException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\rsa\RSAPrivateCrtKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */