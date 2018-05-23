package java.security.interfaces;

import java.math.BigInteger;

public abstract interface RSAPrivateCrtKey
  extends RSAPrivateKey
{
  public static final long serialVersionUID = -5682214253527700368L;
  
  public abstract BigInteger getPublicExponent();
  
  public abstract BigInteger getPrimeP();
  
  public abstract BigInteger getPrimeQ();
  
  public abstract BigInteger getPrimeExponentP();
  
  public abstract BigInteger getPrimeExponentQ();
  
  public abstract BigInteger getCrtCoefficient();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\interfaces\RSAPrivateCrtKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */