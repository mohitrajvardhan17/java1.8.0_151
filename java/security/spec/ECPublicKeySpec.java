package java.security.spec;

public class ECPublicKeySpec
  implements KeySpec
{
  private ECPoint w;
  private ECParameterSpec params;
  
  public ECPublicKeySpec(ECPoint paramECPoint, ECParameterSpec paramECParameterSpec)
  {
    if (paramECPoint == null) {
      throw new NullPointerException("w is null");
    }
    if (paramECParameterSpec == null) {
      throw new NullPointerException("params is null");
    }
    if (paramECPoint == ECPoint.POINT_INFINITY) {
      throw new IllegalArgumentException("w is ECPoint.POINT_INFINITY");
    }
    w = paramECPoint;
    params = paramECParameterSpec;
  }
  
  public ECPoint getW()
  {
    return w;
  }
  
  public ECParameterSpec getParams()
  {
    return params;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\ECPublicKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */