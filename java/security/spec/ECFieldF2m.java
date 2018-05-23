package java.security.spec;

import java.math.BigInteger;
import java.util.Arrays;

public class ECFieldF2m
  implements ECField
{
  private int m;
  private int[] ks;
  private BigInteger rp;
  
  public ECFieldF2m(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("m is not positive");
    }
    m = paramInt;
    ks = null;
    rp = null;
  }
  
  public ECFieldF2m(int paramInt, BigInteger paramBigInteger)
  {
    m = paramInt;
    rp = paramBigInteger;
    if (paramInt <= 0) {
      throw new IllegalArgumentException("m is not positive");
    }
    int i = rp.bitCount();
    if ((!rp.testBit(0)) || (!rp.testBit(paramInt)) || ((i != 3) && (i != 5))) {
      throw new IllegalArgumentException("rp does not represent a valid reduction polynomial");
    }
    BigInteger localBigInteger = rp.clearBit(0).clearBit(paramInt);
    ks = new int[i - 2];
    for (int j = ks.length - 1; j >= 0; j--)
    {
      int k = localBigInteger.getLowestSetBit();
      ks[j] = k;
      localBigInteger = localBigInteger.clearBit(k);
    }
  }
  
  public ECFieldF2m(int paramInt, int[] paramArrayOfInt)
  {
    m = paramInt;
    ks = ((int[])paramArrayOfInt.clone());
    if (paramInt <= 0) {
      throw new IllegalArgumentException("m is not positive");
    }
    if ((ks.length != 1) && (ks.length != 3)) {
      throw new IllegalArgumentException("length of ks is neither 1 nor 3");
    }
    for (int i = 0; i < ks.length; i++)
    {
      if ((ks[i] < 1) || (ks[i] > paramInt - 1)) {
        throw new IllegalArgumentException("ks[" + i + "] is out of range");
      }
      if ((i != 0) && (ks[i] >= ks[(i - 1)])) {
        throw new IllegalArgumentException("values in ks are not in descending order");
      }
    }
    rp = BigInteger.ONE;
    rp = rp.setBit(paramInt);
    for (i = 0; i < ks.length; i++) {
      rp = rp.setBit(ks[i]);
    }
  }
  
  public int getFieldSize()
  {
    return m;
  }
  
  public int getM()
  {
    return m;
  }
  
  public BigInteger getReductionPolynomial()
  {
    return rp;
  }
  
  public int[] getMidTermsOfReductionPolynomial()
  {
    if (ks == null) {
      return null;
    }
    return (int[])ks.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ECFieldF2m)) {
      return (m == m) && (Arrays.equals(ks, ks));
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = m << 5;
    i += (rp == null ? 0 : rp.hashCode());
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\ECFieldF2m.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */