package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class TimeDV
  extends AbstractDateTimeDV
{
  public TimeDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "time" });
    }
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString)
    throws SchemaDateTimeException
  {
    AbstractDateTimeDV.DateTimeData localDateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    year = 2000;
    month = 1;
    day = 15;
    getTime(paramString, 0, i, localDateTimeData);
    validateDateTime(localDateTimeData);
    saveUnnormalized(localDateTimeData);
    if ((utc != 0) && (utc != 90)) {
      normalize(localDateTimeData);
    }
    position = 2;
    return localDateTimeData;
  }
  
  protected String dateToString(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    StringBuffer localStringBuffer = new StringBuffer(16);
    append(localStringBuffer, hour, 2);
    localStringBuffer.append(':');
    append(localStringBuffer, minute, 2);
    localStringBuffer.append(':');
    append(localStringBuffer, second);
    append(localStringBuffer, (char)utc, 0);
    return localStringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    return datatypeFactory.newXMLGregorianCalendar(null, Integer.MIN_VALUE, Integer.MIN_VALUE, unNormHour, unNormMinute, (int)unNormSecond, unNormSecond != 0.0D ? getFractionalSecondsAsBigDecimal(paramDateTimeData) : null, paramDateTimeData.hasTimeZone() ? timezoneHr * 60 + timezoneMin : Integer.MIN_VALUE);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\TimeDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */