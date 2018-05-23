package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

class YearMonthDurationDV
  extends DurationDV
{
  YearMonthDurationDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString, 1);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "yearMonthDuration" });
    }
  }
  
  protected Duration getDuration(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    int i = 1;
    if ((year < 0) || (month < 0)) {
      i = -1;
    }
    return datatypeFactory.newDuration(i == 1, year != Integer.MIN_VALUE ? BigInteger.valueOf(i * year) : null, month != Integer.MIN_VALUE ? BigInteger.valueOf(i * month) : null, null, null, null, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\YearMonthDurationDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */