package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;

class DurationDayTimeImpl
  extends DurationImpl
{
  public DurationDayTimeImpl(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigDecimal paramBigDecimal)
  {
    super(paramBoolean, null, null, paramBigInteger1, paramBigInteger2, paramBigInteger3, paramBigDecimal);
    convertToCanonicalDayTime();
  }
  
  public DurationDayTimeImpl(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramBoolean, wrap(paramInt1), wrap(paramInt2), wrap(paramInt3), paramInt4 != Integer.MIN_VALUE ? new BigDecimal(String.valueOf(paramInt4)) : null);
  }
  
  protected DurationDayTimeImpl(String paramString)
  {
    super(paramString);
    if ((getYears() > 0) || (getMonths() > 0)) {
      throw new IllegalArgumentException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"" + paramString + "\", data model requires a format PnDTnHnMnS.");
    }
    convertToCanonicalDayTime();
  }
  
  protected DurationDayTimeImpl(long paramLong)
  {
    super(paramLong);
    convertToCanonicalDayTime();
    years = null;
    months = null;
  }
  
  public float getValue()
  {
    float f = seconds == null ? 0.0F : seconds.floatValue();
    return ((getDays() * 24 + getHours()) * 60 + getMinutes()) * 60 + f;
  }
  
  private void convertToCanonicalDayTime()
  {
    while (getSeconds() >= 60)
    {
      seconds = seconds.subtract(BigDecimal.valueOf(60L));
      minutes = BigInteger.valueOf(getMinutes()).add(BigInteger.ONE);
    }
    while (getMinutes() >= 60)
    {
      minutes = minutes.subtract(BigInteger.valueOf(60L));
      hours = BigInteger.valueOf(getHours()).add(BigInteger.ONE);
    }
    while (getHours() >= 24)
    {
      hours = hours.subtract(BigInteger.valueOf(24L));
      days = BigInteger.valueOf(getDays()).add(BigInteger.ONE);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\datatype\DurationDayTimeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */