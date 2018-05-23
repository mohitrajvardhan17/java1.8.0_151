package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateTimeDV
  extends AbstractDateTimeDV
{
  public DateTimeDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "dateTime" });
    }
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString)
    throws SchemaDateTimeException
  {
    AbstractDateTimeDV.DateTimeData localDateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    int j = indexOf(paramString, 0, i, 'T');
    int k = getDate(paramString, 0, j, localDateTimeData);
    getTime(paramString, j + 1, i, localDateTimeData);
    if (k != j) {
      throw new RuntimeException(paramString + " is an invalid dateTime dataype value. Invalid character(s) seprating date and time values.");
    }
    validateDateTime(localDateTimeData);
    saveUnnormalized(localDateTimeData);
    if ((utc != 0) && (utc != 90)) {
      normalize(localDateTimeData);
    }
    return localDateTimeData;
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    return datatypeFactory.newXMLGregorianCalendar(BigInteger.valueOf(unNormYear), unNormMonth, unNormDay, unNormHour, unNormMinute, (int)unNormSecond, unNormSecond != 0.0D ? getFractionalSecondsAsBigDecimal(paramDateTimeData) : null, paramDateTimeData.hasTimeZone() ? timezoneHr * 60 + timezoneMin : Integer.MIN_VALUE);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\DateTimeDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */