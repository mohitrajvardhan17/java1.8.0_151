package sun.util.calendar;

import java.util.TimeZone;

public class Gregorian
  extends BaseCalendar
{
  Gregorian() {}
  
  public String getName()
  {
    return "gregorian";
  }
  
  public Date getCalendarDate()
  {
    return getCalendarDate(System.currentTimeMillis(), newCalendarDate());
  }
  
  public Date getCalendarDate(long paramLong)
  {
    return getCalendarDate(paramLong, newCalendarDate());
  }
  
  public Date getCalendarDate(long paramLong, CalendarDate paramCalendarDate)
  {
    return (Date)super.getCalendarDate(paramLong, paramCalendarDate);
  }
  
  public Date getCalendarDate(long paramLong, TimeZone paramTimeZone)
  {
    return getCalendarDate(paramLong, newCalendarDate(paramTimeZone));
  }
  
  public Date newCalendarDate()
  {
    return new Date();
  }
  
  public Date newCalendarDate(TimeZone paramTimeZone)
  {
    return new Date(paramTimeZone);
  }
  
  static class Date
    extends BaseCalendar.Date
  {
    protected Date() {}
    
    protected Date(TimeZone paramTimeZone)
    {
      super();
    }
    
    public int getNormalizedYear()
    {
      return getYear();
    }
    
    public void setNormalizedYear(int paramInt)
    {
      setYear(paramInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\calendar\Gregorian.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */