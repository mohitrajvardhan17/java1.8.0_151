package java.security.spec;

import java.math.BigInteger;

public class RSAPrivateCrtKeySpec
  extends RSAPrivateKeySpec
{
  private final BigInteger publicExponent;
  private final BigInteger primeP;
  private final BigInteger primeQ;
  private final BigInteger primeExponentP;
  private final BigInteger primeExponentQ;
  private final BigInteger crtCoefficient;
  
  public RSAPrivateCrtKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8)
  {
    super(paramBigInteger1, paramBigInteger3);
    publicExponent = paramBigInteger2;
    primeP = paramBigInteger4;
    primeQ = paramBigInteger5;
    primeExponentP = paramBigInteger6;
    primeExponentQ = paramBigInteger7;
    crtCoefficient = paramBigInteger8;
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\RSAPrivateCrtKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */