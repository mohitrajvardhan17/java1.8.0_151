package javax.swing.text;

import java.text.DateFormat;
import java.text.DateFormat.Field;
import java.text.Format;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DateFormatter
  extends InternationalFormatter
{
  public DateFormatter()
  {
    this(DateFormat.getDateInstance());
  }
  
  public DateFormatter(DateFormat paramDateFormat)
  {
    super(paramDateFormat);
    setFormat(paramDateFormat);
  }
  
  public void setFormat(DateFormat paramDateFormat)
  {
    super.setFormat(paramDateFormat);
  }
  
  private Calendar getCalendar()
  {
    Format localFormat = getFormat();
    if ((localFormat instanceof DateFormat)) {
      return ((DateFormat)localFormat).getCalendar();
    }
    return Calendar.getInstance();
  }
  
  boolean getSupportsIncrement()
  {
    return true;
  }
  
  Object getAdjustField(int paramInt, Map paramMap)
  {
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (((localObject instanceof DateFormat.Field)) && ((localObject == DateFormat.Field.HOUR1) || (((DateFormat.Field)localObject).getCalendarField() != -1))) {
        return localObject;
      }
    }
    return null;
  }
  
  Object adjustValue(Object paramObject1, Map paramMap, Object paramObject2, int paramInt)
    throws BadLocationException, ParseException
  {
    if (paramObject2 != null)
    {
      if (paramObject2 == DateFormat.Field.HOUR1) {
        paramObject2 = DateFormat.Field.HOUR0;
      }
      int i = ((DateFormat.Field)paramObject2).getCalendarField();
      Calendar localCalendar = getCalendar();
      if (localCalendar != null)
      {
        localCalendar.setTime((Date)paramObject1);
        int j = localCalendar.get(i);
        try
        {
          localCalendar.add(i, paramInt);
          paramObject1 = localCalendar.getTime();
        }
        catch (Throwable localThrowable)
        {
          paramObject1 = null;
        }
        return paramObject1;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\DateFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */