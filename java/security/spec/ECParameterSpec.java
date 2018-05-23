package java.security.spec;

import java.math.BigInteger;

public class ECParameterSpec
  implements AlgorithmParameterSpec
{
  private final EllipticCurve curve;
  private final ECPoint g;
  private final BigInteger n;
  private final int h;
  
  public ECParameterSpec(EllipticCurve paramEllipticCurve, ECPoint paramECPoint, BigInteger paramBigInteger, int paramInt)
  {
    if (paramEllipticCurve == null) {
      throw new NullPointerException("curve is null");
    }
    if (paramECPoint == null) {
      throw new NullPointerException("g is null");
    }
    if (paramBigInteger == null) {
      throw new NullPointerException("n is null");
    }
    if (paramBigInteger.signum() != 1) {
      throw new IllegalArgumentException("n is not positive");
    }
    if (paramInt <= 0) {
      throw new IllegalArgumentException("h is not positive");
    }
    curve = paramEllipticCurve;
    g = paramECPoint;
    n = paramBigInteger;
    h = paramInt;
  }
  
  public EllipticCurve getCurve()
  {
    return curve;
  }
  
  public ECPoint getGenerator()
  {
    return g;
  }
  
  public BigInteger getOrder()
  {
    return n;
  }
  
  public int getCofactor()
  {
    return h;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\ECParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */