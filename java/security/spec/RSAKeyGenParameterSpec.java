package java.security.spec;

import java.math.BigInteger;

public class RSAKeyGenParameterSpec
  implements AlgorithmParameterSpec
{
  private int keysize;
  private BigInteger publicExponent;
  public static final BigInteger F0 = BigInteger.valueOf(3L);
  public static final BigInteger F4 = BigInteger.valueOf(65537L);
  
  public RSAKeyGenParameterSpec(int paramInt, BigInteger paramBigInteger)
  {
    keysize = paramInt;
    publicExponent = paramBigInteger;
  }
  
  public int getKeysize()
  {
    return keysize;
  }
  
  public BigInteger getPublicExponent()
  {
    return publicExponent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\RSAKeyGenParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */