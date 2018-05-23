package java.security.spec;

import java.math.BigInteger;
import java.security.interfaces.DSAParams;

public class DSAParameterSpec
  implements AlgorithmParameterSpec, DSAParams
{
  BigInteger p;
  BigInteger q;
  BigInteger g;
  
  public DSAParameterSpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
  {
    p = paramBigInteger1;
    q = paramBigInteger2;
    g = paramBigInteger3;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\DSAParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */