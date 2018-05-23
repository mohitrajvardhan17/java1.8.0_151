package java.security.spec;

import java.math.BigInteger;

public class ECPoint
{
  private final BigInteger x;
  private final BigInteger y;
  public static final ECPoint POINT_INFINITY = new ECPoint();
  
  private ECPoint()
  {
    x = null;
    y = null;
  }
  
  public ECPoint(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    if ((paramBigInteger1 == null) || (paramBigInteger2 == null)) {
      throw new NullPointerException("affine coordinate x or y is null");
    }
    x = paramBigInteger1;
    y = paramBigInteger2;
  }
  
  public BigInteger getAffineX()
  {
    return x;
  }
  
  public BigInteger getAffineY()
  {
    return y;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (this == POINT_INFINITY) {
      return false;
    }
    if ((paramObject instanceof ECPoint)) {
      return (x.equals(x)) && (y.equals(y));
    }
    return false;
  }
  
  public int hashCode()
  {
    if (this == POINT_INFINITY) {
      return 0;
    }
    return x.hashCode() << 5 + y.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\ECPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */