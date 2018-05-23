package java.security.spec;

import java.math.BigInteger;

public class RSAMultiPrimePrivateCrtKeySpec
  extends RSAPrivateKeySpec
{
  private final BigInteger publicExponent;
  private final BigInteger primeP;
  private final BigInteger primeQ;
  private final BigInteger primeExponentP;
  private final BigInteger primeExponentQ;
  private final BigInteger crtCoefficient;
  private final RSAOtherPrimeInfo[] otherPrimeInfo;
  
  public RSAMultiPrimePrivateCrtKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8, RSAOtherPrimeInfo[] paramArrayOfRSAOtherPrimeInfo)
  {
    super(paramBigInteger1, paramBigInteger3);
    if (paramBigInteger1 == null) {
      throw new NullPointerException("the modulus parameter must be non-null");
    }
    if (paramBigInteger2 == null) {
      throw new NullPointerException("the publicExponent parameter must be non-null");
    }
    if (paramBigInteger3 == null) {
      throw new NullPointerException("the privateExponent parameter must be non-null");
    }
    if (paramBigInteger4 == null) {
      throw new NullPointerException("the primeP parameter must be non-null");
    }
    if (paramBigInteger5 == null) {
      throw new NullPointerException("the primeQ parameter must be non-null");
    }
    if (paramBigInteger6 == null) {
      throw new NullPointerException("the primeExponentP parameter must be non-null");
    }
    if (paramBigInteger7 == null) {
      throw new NullPointerException("the primeExponentQ parameter must be non-null");
    }
    if (paramBigInteger8 == null) {
      throw new NullPointerException("the crtCoefficient parameter must be non-null");
    }
    publicExponent = paramBigInteger2;
    primeP = paramBigInteger4;
    primeQ = paramBigInteger5;
    primeExponentP = paramBigInteger6;
    primeExponentQ = paramBigInteger7;
    crtCoefficient = paramBigInteger8;
    if (paramArrayOfRSAOtherPrimeInfo == null)
    {
      otherPrimeInfo = null;
    }
    else
    {
      if (paramArrayOfRSAOtherPrimeInfo.length == 0) {
        throw new IllegalArgumentException("the otherPrimeInfo parameter must not be empty");
      }
      otherPrimeInfo = ((RSAOtherPrimeInfo[])paramArrayOfRSAOtherPrimeInfo.clone());
    }
  }
  
  public BigInteger getPublicExponent()
  {
    return publicExponent;
  }
  
  public BigInteger getPrimeP()
  {
    return primeP;
  }
  
  public BigInteger getPrimeQ()
  {
    return primeQ;
  }
  
  public BigInteger getPrimeExponentP()
  {
    return primeExponentP;
  }
  
  public BigInteger getPrimeExponentQ()
  {
    return primeExponentQ;
  }
  
  public BigInteger getCrtCoefficient()
  {
    return crtCoefficient;
  }
  
  public RSAOtherPrimeInfo[] getOtherPrimeInfo()
  {
    if (otherPrimeInfo == null) {
      return null;
    }
    return (RSAOtherPrimeInfo[])otherPrimeInfo.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\RSAMultiPrimePrivateCrtKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */