package java.security.spec;

import java.math.BigInteger;

public class DSAPublicKeySpec
  implements KeySpec
{
  private BigInteger y;
  private BigInteger p;
  private BigInteger q;
  private BigInteger g;
  
  public DSAPublicKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
  {
    y = paramBigInteger1;
    p = paramBigInteger2;
    q = paramBigInteger3;
    g = paramBigInteger4;
  }
  
  public BigInteger getY()
  {
    return y;
  }
  
  public BigInteger getP()
  {
    return p;
  }
  
  public BigInteger getQ()
  {
    return q;
  }
  
  public BigInteger getG()
  {
    return g;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\DSAPublicKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */