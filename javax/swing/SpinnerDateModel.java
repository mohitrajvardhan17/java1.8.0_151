package javax.swing;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class SpinnerDateModel
  extends AbstractSpinnerModel
  implements Serializable
{
  private Comparable start;
  private Comparable end;
  private Calendar value;
  private int calendarField;
  
  private boolean calendarFieldOK(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
      return true;
    }
    return false;
  }
  
  public SpinnerDateModel(Date paramDate, Comparable paramComparable1, Comparable paramComparable2, int paramInt)
  {
    if (paramDate == null) {
      throw new IllegalArgumentException("value is null");
    }
    if (!calendarFieldOK(paramInt)) {
      throw new IllegalArgumentException("invalid calendarField");
    }
    if (((paramComparable1 != null) && (paramComparable1.compareTo(paramDate) > 0)) || ((paramComparable2 != null) && (paramComparable2.compareTo(paramDate) < 0))) {
      throw new IllegalArgumentException("(start <= value <= end) is false");
    }
    value = Calendar.getInstance();
    start = paramComparable1;
    end = paramComparable2;
    calendarField = paramInt;
    value.setTime(paramDate);
  }
  
  public SpinnerDateModel()
  {
    this(new Date(), null, null, 5);
  }
  
  public void setStart(Comparable paramComparable)
  {
    if (paramComparable == null ? start != null : !paramComparable.equals(start))
    {
      start = paramComparable;
      fireStateChanged();
    }
  }
  
  public Comparable getStart()
  {
    return start;
  }
  
  public void setEnd(Comparable paramComparable)
  {
    if (paramComparable == null ? end != null : !paramComparable.equals(end))
    {
      end = paramComparable;
      fireStateChanged();
    }
  }
  
  public Comparable getEnd()
  {
    return end;
  }
  
  public void setCalendarField(int paramInt)
  {
    if (!calendarFieldOK(paramInt)) {
      throw new IllegalArgumentException("invalid calendarField");
    }
    if (paramInt != calendarField)
    {
      calendarField = paramInt;
      fireStateChanged();
    }
  }
  
  public int getCalendarField()
  {
    return calendarField;
  }
  
  public Object getNextValue()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(value.getTime());
    localCalendar.add(calendarField, 1);
    Date localDate = localCalendar.getTime();
    return (end == null) || (end.compareTo(localDate) >= 0) ? localDate : null;
  }
  
  public Object getPreviousValue()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(value.getTime());
    localCalendar.add(calendarField, -1);
    Date localDate = localCalendar.getTime();
    return (start == null) || (start.compareTo(localDate) <= 0) ? localDate : null;
  }
  
  public Date getDate()
  {
    return value.getTime();
  }
  
  public Object getValue()
  {
    return value.getTime();
  }
  
  public void setValue(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof Date))) {
      throw new IllegalArgumentException("illegal value");
    }
    if (!paramObject.equals(value.getTime()))
    {
      value.setTime((Date)paramObject);
      fireStateChanged();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SpinnerDateModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */