package java.security.spec;

import java.math.BigInteger;

public class DSAPrivateKeySpec
  implements KeySpec
{
  private BigInteger x;
  private BigInteger p;
  private BigInteger q;
  private BigInteger g;
  
  public DSAPrivateKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
  {
    x = paramBigInteger1;
    p = paramBigInteger2;
    q = paramBigInteger3;
    g = paramBigInteger4;
  }
  
  public BigInteger getX()
  {
    return x;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\DSAPrivateKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */