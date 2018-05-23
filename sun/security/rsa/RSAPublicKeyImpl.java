package sun.security.rsa;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyRep;
import java.security.KeyRep.Type;
import java.security.interfaces.RSAPublicKey;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.X509Key;

public final class RSAPublicKeyImpl
  extends X509Key
  implements RSAPublicKey
{
  private static final long serialVersionUID = 2644735423591199609L;
  private BigInteger n;
  private BigInteger e;
  
  public RSAPublicKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
    throws InvalidKeyException
  {
    n = paramBigInteger1;
    e = paramBigInteger2;
    RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), paramBigInteger2);
    algid = RSAPrivateCrtKeyImpl.rsaId;
    try
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      localDerOutputStream.putInteger(paramBigInteger1);
      localDerOutputStream.putInteger(paramBigInteger2);
      byte[] arrayOfByte = new DerValue((byte)48, localDerOutputStream.toByteArray()).toByteArray();
      setKey(new BitArray(arrayOfByte.length * 8, arrayOfByte));
    }
    catch (IOException localIOException)
    {
      throw new InvalidKeyException(localIOException);
    }
  }
  
  public RSAPublicKeyImpl(byte[] paramArrayOfByte)
    throws InvalidKeyException
  {
    decode(paramArrayOfByte);
    RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(), e);
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
  
  protected void parseKeyBits()
    throws InvalidKeyException
  {
    try
    {
      DerInputStream localDerInputStream1 = new DerInputStream(getKey().toByteArray());
      DerValue localDerValue = localDerInputStream1.getDerValue();
      if (tag != 48) {
        throw new IOException("Not a SEQUENCE");
      }
      DerInputStream localDerInputStream2 = data;
      n = localDerInputStream2.getPositiveBigInteger();
      e = localDerInputStream2.getPositiveBigInteger();
      if (data.available() != 0) {
        throw new IOException("Extra data available");
      }
    }
    catch (IOException localIOException)
    {
      throw new InvalidKeyException("Invalid RSA public key", localIOException);
    }
  }
  
  public String toString()
  {
    return "Sun RSA public key, " + n.bitLength() + " bits\n  modulus: " + n + "\n  public exponent: " + e;
  }
  
  protected Object writeReplace()
    throws ObjectStreamException
  {
    return new KeyRep(KeyRep.Type.PUBLIC, getAlgorithm(), getFormat(), getEncoded());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\rsa\RSAPublicKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */