package sun.security.provider;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgIdDSA;
import sun.security.x509.AlgorithmId;

public final class DSAPrivateKey
  extends PKCS8Key
  implements java.security.interfaces.DSAPrivateKey, Serializable
{
  private static final long serialVersionUID = -3244453684193605938L;
  private BigInteger x;
  
  public DSAPrivateKey() {}
  
  public DSAPrivateKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
    throws InvalidKeyException
  {
    x = paramBigInteger1;
    algid = new AlgIdDSA(paramBigInteger2, paramBigInteger3, paramBigInteger4);
    try
    {
      key = new DerValue((byte)2, paramBigInteger1.toByteArray()).toByteArray();
      encode();
    }
    catch (IOException localIOException)
    {
      InvalidKeyException localInvalidKeyException = new InvalidKeyException("could not DER encode x: " + localIOException.getMessage());
      localInvalidKeyException.initCause(localIOException);
      throw localInvalidKeyException;
    }
  }
  
  public DSAPrivateKey(byte[] paramArrayOfByte)
    throws InvalidKeyException
  {
    clearOldKey();
    decode(paramArrayOfByte);
  }
  
  public DSAParams getParams()
  {
    try
    {
      if ((algid instanceof DSAParams)) {
        return (DSAParams)algid;
      }
      AlgorithmParameters localAlgorithmParameters = algid.getParameters();
      if (localAlgorithmParameters == null) {
        return null;
      }
      DSAParameterSpec localDSAParameterSpec = (DSAParameterSpec)localAlgorithmParameters.getParameterSpec(DSAParameterSpec.class);
      return localDSAParameterSpec;
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
    return null;
  }
  
  public BigInteger getX()
  {
    return x;
  }
  
  private void clearOldKey()
  {
    int i;
    if (encodedKey != null) {
      for (i = 0; i < encodedKey.length; i++) {
        encodedKey[i] = 0;
      }
    }
    if (key != null) {
      for (i = 0; i < key.length; i++) {
        key[i] = 0;
      }
    }
  }
  
  protected void parseKeyBits()
    throws InvalidKeyException
  {
    try
    {
      DerInputStream localDerInputStream = new DerInputStream(key);
      x = localDerInputStream.getBigInteger();
    }
    catch (IOException localIOException)
    {
      InvalidKeyException localInvalidKeyException = new InvalidKeyException(localIOException.getMessage());
      localInvalidKeyException.initCause(localIOException);
      throw localInvalidKeyException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\DSAPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */