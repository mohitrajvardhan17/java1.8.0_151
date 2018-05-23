package java.security.interfaces;

import java.math.BigInteger;
import java.security.spec.RSAOtherPrimeInfo;

public abstract interface RSAMultiPrimePrivateCrtKey
  extends RSAPrivateKey
{
  public static final long serialVersionUID = 618058533534628008L;
  
  public abstract BigInteger getPublicExponent();
  
  public abstract BigInteger getPrimeP();
  
  public abstract BigInteger getPrimeQ();
  
  public abstract BigInteger getPrimeExponentP();
  
  public abstract BigInteger getPrimeExponentQ();
  
  public abstract BigInteger getCrtCoefficient();
  
  public abstract RSAOtherPrimeInfo[] getOtherPrimeInfo();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\interfaces\RSAMultiPrimePrivateCrtKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */