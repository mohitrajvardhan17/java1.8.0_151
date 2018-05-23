package java.security.spec;

import java.math.BigInteger;

public class RSAOtherPrimeInfo
{
  private BigInteger prime;
  private BigInteger primeExponent;
  private BigInteger crtCoefficient;
  
  public RSAOtherPrimeInfo(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
  {
    if (paramBigInteger1 == null) {
      throw new NullPointerException("the prime parameter must be non-null");
    }
    if (paramBigInteger2 == null) {
      throw new NullPointerException("the primeExponent parameter must be non-null");
    }
    if (paramBigInteger3 == null) {
      throw new NullPointerException("the crtCoefficient parameter must be non-null");
    }
    prime = paramBigInteger1;
    primeExponent = paramBigInteger2;
    crtCoefficient = paramBigInteger3;
  }
  
  public final BigInteger getPrime()
  {
    return prime;
  }
  
  public final BigInteger getExponent()
  {
    return primeExponent;
  }
  
  public final BigInteger getCrtCoefficient()
  {
    return crtCoefficient;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\RSAOtherPrimeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */