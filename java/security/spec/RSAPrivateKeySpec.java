package java.security.spec;

import java.math.BigInteger;

public class RSAPrivateKeySpec
  implements KeySpec
{
  private BigInteger modulus;
  private BigInteger privateExponent;
  
  public RSAPrivateKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    modulus = paramBigInteger1;
    privateExponent = paramBigInteger2;
  }
  
  public BigInteger getModulus()
  {
    return modulus;
  }
  
  public BigInteger getPrivateExponent()
  {
    return privateExponent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\RSAPrivateKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */