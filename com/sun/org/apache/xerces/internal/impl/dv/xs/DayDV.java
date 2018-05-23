package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DayDV
  extends AbstractDateTimeDV
{
  private static final int DAY_SIZE = 5;
  
  public DayDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "gDay" });
    }
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString)
    throws SchemaDateTimeException
  {
    AbstractDateTimeDV.DateTimeData localDateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    if ((paramString.charAt(0) != '-') || (paramString.charAt(1) != '-') || (paramString.charAt(2) != '-')) {
      throw new SchemaDateTimeException("Error in day parsing");
    }
    year = 2000;
    month = 1;
    day = parseInt(paramString, 3, 5);
    if (5 < i)
    {
      if (!isNextCharUTCSign(paramString, 5, i)) {
        throw new SchemaDateTimeException("Error in day parsing");
      }
      getTimeZone(paramString, localDateTimeData, 5, i);
    }
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
    StringBuffer localStringBuffer = new StringBuffer(6);
    localStringBuffer.append('-');
    localStringBuffer.append('-');
    localStringBuffer.append('-');
    append(localStringBuffer, day, 2);
    append(localStringBuffer, (char)utc, 0);
    return localStringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    return datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, Integer.MIN_VALUE, unNormDay, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, paramDateTimeData.hasTimeZone() ? timezoneHr * 60 + timezoneMin : Integer.MIN_VALUE);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\DayDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */