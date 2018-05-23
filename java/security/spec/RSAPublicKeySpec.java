package java.security.spec;

import java.math.BigInteger;

public class RSAPublicKeySpec
  implements KeySpec
{
  private BigInteger modulus;
  private BigInteger publicExponent;
  
  public RSAPublicKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    modulus = paramBigInteger1;
    publicExponent = paramBigInteger2;
  }
  
  public BigInteger getModulus()
  {
    return modulus;
  }
  
  public BigInteger getPublicExponent()
  {
    return publicExponent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\RSAPublicKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */