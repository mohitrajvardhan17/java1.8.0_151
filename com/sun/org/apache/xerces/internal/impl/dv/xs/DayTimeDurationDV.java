package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

class DayTimeDurationDV
  extends DurationDV
{
  DayTimeDurationDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString, 2);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "dayTimeDuration" });
    }
  }
  
  protected Duration getDuration(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    int i = 1;
    if ((day < 0) || (hour < 0) || (minute < 0) || (second < 0.0D)) {
      i = -1;
    }
    return datatypeFactory.newDuration(i == 1, null, null, day != Integer.MIN_VALUE ? BigInteger.valueOf(i * day) : null, hour != Integer.MIN_VALUE ? BigInteger.valueOf(i * hour) : null, minute != Integer.MIN_VALUE ? BigInteger.valueOf(i * minute) : null, second != -2.147483648E9D ? new BigDecimal(String.valueOf(i * second)) : null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\DayTimeDurationDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */