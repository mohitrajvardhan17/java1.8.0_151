package java.security.spec;

import java.math.BigInteger;

public class ECPrivateKeySpec
  implements KeySpec
{
  private BigInteger s;
  private ECParameterSpec params;
  
  public ECPrivateKeySpec(BigInteger paramBigInteger, ECParameterSpec paramECParameterSpec)
  {
    if (paramBigInteger == null) {
      throw new NullPointerException("s is null");
    }
    if (paramECParameterSpec == null) {
      throw new NullPointerException("params is null");
    }
    s = paramBigInteger;
    params = paramECParameterSpec;
  }
  
  public BigInteger getS()
  {
    return s;
  }
  
  public ECParameterSpec getParams()
  {
    return params;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\ECPrivateKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */