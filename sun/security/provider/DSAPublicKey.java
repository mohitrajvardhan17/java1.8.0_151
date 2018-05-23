package sun.security.provider;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.BitArray;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgIdDSA;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509Key;

public class DSAPublicKey
  extends X509Key
  implements java.security.interfaces.DSAPublicKey, Serializable
{
  private static final long serialVersionUID = -2994193307391104133L;
  private BigInteger y;
  
  public DSAPublicKey() {}
  
  public DSAPublicKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
    throws InvalidKeyException
  {
    y = paramBigInteger1;
    algid = new AlgIdDSA(paramBigInteger2, paramBigInteger3, paramBigInteger4);
    try
    {
      byte[] arrayOfByte = new DerValue((byte)2, paramBigInteger1.toByteArray()).toByteArray();
      setKey(new BitArray(arrayOfByte.length * 8, arrayOfByte));
      encode();
    }
    catch (IOException localIOException)
    {
      throw new InvalidKeyException("could not DER encode y: " + localIOException.getMessage());
    }
  }
  
  public DSAPublicKey(byte[] paramArrayOfByte)
    throws InvalidKeyException
  {
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
  
  public BigInteger getY()
  {
    return y;
  }
  
  public String toString()
  {
    return "Sun DSA Public Key\n    Parameters:" + algid + "\n  y:\n" + Debug.toHexString(y) + "\n";
  }
  
  protected void parseKeyBits()
    throws InvalidKeyException
  {
    try
    {
      DerInputStream localDerInputStream = new DerInputStream(getKey().toByteArray());
      y = localDerInputStream.getBigInteger();
    }
    catch (IOException localIOException)
    {
      throw new InvalidKeyException("Invalid key: y value\n" + localIOException.getMessage());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\DSAPublicKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */