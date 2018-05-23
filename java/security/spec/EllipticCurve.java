package java.security.spec;

import java.math.BigInteger;

public class EllipticCurve
{
  private final ECField field;
  private final BigInteger a;
  private final BigInteger b;
  private final byte[] seed;
  
  private static void checkValidity(ECField paramECField, BigInteger paramBigInteger, String paramString)
  {
    if ((paramECField instanceof ECFieldFp))
    {
      BigInteger localBigInteger = ((ECFieldFp)paramECField).getP();
      if (localBigInteger.compareTo(paramBigInteger) != 1) {
        throw new IllegalArgumentException(paramString + " is too large");
      }
      if (paramBigInteger.signum() < 0) {
        throw new IllegalArgumentException(paramString + " is negative");
      }
    }
    else if ((paramECField instanceof ECFieldF2m))
    {
      int i = ((ECFieldF2m)paramECField).getM();
      if (paramBigInteger.bitLength() > i) {
        throw new IllegalArgumentException(paramString + " is too large");
      }
    }
  }
  
  public EllipticCurve(ECField paramECField, BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    this(paramECField, paramBigInteger1, paramBigInteger2, null);
  }
  
  public EllipticCurve(ECField paramECField, BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfByte)
  {
    if (paramECField == null) {
      throw new NullPointerException("field is null");
    }
    if (paramBigInteger1 == null) {
      throw new NullPointerException("first coefficient is null");
    }
    if (paramBigInteger2 == null) {
      throw new NullPointerException("second coefficient is null");
    }
    checkValidity(paramECField, paramBigInteger1, "first coefficient");
    checkValidity(paramECField, paramBigInteger2, "second coefficient");
    field = paramECField;
    a = paramBigInteger1;
    b = paramBigInteger2;
    if (paramArrayOfByte != null) {
      seed = ((byte[])paramArrayOfByte.clone());
    } else {
      seed = null;
    }
  }
  
  public ECField getField()
  {
    return field;
  }
  
  public BigInteger getA()
  {
    return a;
  }
  
  public BigInteger getB()
  {
    return b;
  }
  
  public byte[] getSeed()
  {
    if (seed == null) {
      return null;
    }
    return (byte[])seed.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof EllipticCurve))
    {
      EllipticCurve localEllipticCurve = (EllipticCurve)paramObject;
      if ((field.equals(field)) && (a.equals(a)) && (b.equals(b))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return field.hashCode() << 6 + (a.hashCode() << 4) + (b.hashCode() << 2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\EllipticCurve.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */