package java.security.interfaces;

import java.math.BigInteger;

public abstract interface DSAParams
{
  public abstract BigInteger getP();
  
  public abstract BigInteger getQ();
  
  public abstract BigInteger getG();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\interfaces\DSAParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */