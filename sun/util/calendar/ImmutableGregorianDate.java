package sun.util.calendar;

import java.util.Locale;
import java.util.TimeZone;

class ImmutableGregorianDate
  extends BaseCalendar.Date
{
  private final BaseCalendar.Date date;
  
  ImmutableGregorianDate(BaseCalendar.Date paramDate)
  {
    if (paramDate == null) {
      throw new NullPointerException();
    }
    date = paramDate;
  }
  
  public Era getEra()
  {
    return date.getEra();
  }
  
  public CalendarDate setEra(Era paramEra)
  {
    unsupported();
    return this;
  }
  
  public int getYear()
  {
    return date.getYear();
  }
  
  public CalendarDate setYear(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addYear(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public boolean isLeapYear()
  {
    return date.isLeapYear();
  }
  
  void setLeapYear(boolean paramBoolean)
  {
    unsupported();
  }
  
  public int getMonth()
  {
    return date.getMonth();
  }
  
  public CalendarDate setMonth(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addMonth(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public int getDayOfMonth()
  {
    return date.getDayOfMonth();
  }
  
  public CalendarDate setDayOfMonth(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addDayOfMonth(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public int getDayOfWeek()
  {
    return date.getDayOfWeek();
  }
  
  public int getHours()
  {
    return date.getHours();
  }
  
  public CalendarDate setHours(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addHours(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public int getMinutes()
  {
    return date.getMinutes();
  }
  
  public CalendarDate setMinutes(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addMinutes(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public int getSeconds()
  {
    return date.getSeconds();
  }
  
  public CalendarDate setSeconds(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addSeconds(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public int getMillis()
  {
    return date.getMillis();
  }
  
  public CalendarDate setMillis(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addMillis(int paramInt)
  {
    unsupported();
    return this;
  }
  
  public long getTimeOfDay()
  {
    return date.getTimeOfDay();
  }
  
  public CalendarDate setDate(int paramInt1, int paramInt2, int paramInt3)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addDate(int paramInt1, int paramInt2, int paramInt3)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate setTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    unsupported();
    return this;
  }
  
  public CalendarDate addTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    unsupported();
    return this;
  }
  
  protected void setTimeOfDay(long paramLong)
  {
    unsupported();
  }
  
  public boolean isNormalized()
  {
    return date.isNormalized();
  }
  
  public boolean isStandardTime()
  {
    return date.isStandardTime();
  }
  
  public void setStandardTime(boolean paramBoolean)
  {
    unsupported();
  }
  
  public boolean isDaylightTime()
  {
    return date.isDaylightTime();
  }
  
  protected void setLocale(Locale paramLocale)
  {
    unsupported();
  }
  
  public TimeZone getZone()
  {
    return date.getZone();
  }
  
  public CalendarDate setZone(TimeZone paramTimeZone)
  {
    unsupported();
    return this;
  }
  
  public boolean isSameDate(CalendarDate paramCalendarDate)
  {
    return paramCalendarDate.isSameDate(paramCalendarDate);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ImmutableGregorianDate)) {
      return false;
    }
    return date.equals(date);
  }
  
  public int hashCode()
  {
    return date.hashCode();
  }
  
  public Object clone()
  {
    return super.clone();
  }
  
  public String toString()
  {
    return date.toString();
  }
  
  protected void setDayOfWeek(int paramInt)
  {
    unsupported();
  }
  
  protected void setNormalized(boolean paramBoolean)
  {
    unsupported();
  }
  
  public int getZoneOffset()
  {
    return date.getZoneOffset();
  }
  
  protected void setZoneOffset(int paramInt)
  {
    unsupported();
  }
  
  public int getDaylightSaving()
  {
    return date.getDaylightSaving();
  }
  
  protected void setDaylightSaving(int paramInt)
  {
    unsupported();
  }
  
  public int getNormalizedYear()
  {
    return date.getNormalizedYear();
  }
  
  public void setNormalizedYear(int paramInt)
  {
    unsupported();
  }
  
  private void unsupported()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\calendar\ImmutableGregorianDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */