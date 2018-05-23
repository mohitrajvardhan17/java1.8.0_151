package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class MonthDV
  extends AbstractDateTimeDV
{
  public MonthDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "gMonth" });
    }
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString)
    throws SchemaDateTimeException
  {
    AbstractDateTimeDV.DateTimeData localDateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    year = 2000;
    day = 1;
    if ((paramString.charAt(0) != '-') || (paramString.charAt(1) != '-')) {
      throw new SchemaDateTimeException("Invalid format for gMonth: " + paramString);
    }
    int j = 4;
    month = parseInt(paramString, 2, j);
    if ((paramString.length() >= j + 2) && (paramString.charAt(j) == '-') && (paramString.charAt(j + 1) == '-')) {
      j += 2;
    }
    if (j < i)
    {
      if (!isNextCharUTCSign(paramString, j, i)) {
        throw new SchemaDateTimeException("Error in month parsing: " + paramString);
      }
      getTimeZone(paramString, localDateTimeData, j, i);
    }
    validateDateTime(localDateTimeData);
    saveUnnormalized(localDateTimeData);
    if ((utc != 0) && (utc != 90)) {
      normalize(localDateTimeData);
    }
    position = 1;
    return localDateTimeData;
  }
  
  protected String dateToString(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    StringBuffer localStringBuffer = new StringBuffer(5);
    localStringBuffer.append('-');
    localStringBuffer.append('-');
    append(localStringBuffer, month, 2);
    append(localStringBuffer, (char)utc, 0);
    return localStringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    return datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, unNormMonth, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, paramDateTimeData.hasTimeZone() ? timezoneHr * 60 + timezoneMin : Integer.MIN_VALUE);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\MonthDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */