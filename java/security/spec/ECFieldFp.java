package java.security.spec;

import java.math.BigInteger;

public class ECFieldFp
  implements ECField
{
  private BigInteger p;
  
  public ECFieldFp(BigInteger paramBigInteger)
  {
    if (paramBigInteger.signum() != 1) {
      throw new IllegalArgumentException("p is not positive");
    }
    p = paramBigInteger;
  }
  
  public int getFieldSize()
  {
    return p.bitLength();
  }
  
  public BigInteger getP()
  {
    return p;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ECFieldFp)) {
      return p.equals(p);
    }
    return false;
  }
  
  public int hashCode()
  {
    return p.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\ECFieldFp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */