package sun.util.calendar;

import java.util.Locale;
import java.util.TimeZone;

public abstract class CalendarDate
  implements Cloneable
{
  public static final int FIELD_UNDEFINED = Integer.MIN_VALUE;
  public static final long TIME_UNDEFINED = Long.MIN_VALUE;
  private Era era;
  private int year;
  private int month;
  private int dayOfMonth;
  private int dayOfWeek = Integer.MIN_VALUE;
  private boolean leapYear;
  private int hours;
  private int minutes;
  private int seconds;
  private int millis;
  private long fraction;
  private boolean normalized;
  private TimeZone zoneinfo;
  private int zoneOffset;
  private int daylightSaving;
  private boolean forceStandardTime;
  private Locale locale;
  
  protected CalendarDate()
  {
    this(TimeZone.getDefault());
  }
  
  protected CalendarDate(TimeZone paramTimeZone)
  {
    zoneinfo = paramTimeZone;
  }
  
  public Era getEra()
  {
    return era;
  }
  
  public CalendarDate setEra(Era paramEra)
  {
    if (era == paramEra) {
      return this;
    }
    era = paramEra;
    normalized = false;
    return this;
  }
  
  public int getYear()
  {
    return year;
  }
  
  public CalendarDate setYear(int paramInt)
  {
    if (year != paramInt)
    {
      year = paramInt;
      normalized = false;
    }
    return this;
  }
  
  public CalendarDate addYear(int paramInt)
  {
    if (paramInt != 0)
    {
      year += paramInt;
      normalized = false;
    }
    return this;
  }
  
  public boolean isLeapYear()
  {
    return leapYear;
  }
  
  void setLeapYear(boolean paramBoolean)
  {
    leapYear = paramBoolean;
  }
  
  public int getMonth()
  {
    return month;
  }
  
  public CalendarDate setMonth(int paramInt)
  {
    if (month != paramInt)
    {
      month = paramInt;
      normalized = false;
    }
    return this;
  }
  
  public CalendarDate addMonth(int paramInt)
  {
    if (paramInt != 0)
    {
      month += paramInt;
      normalized = false;
    }
    return this;
  }
  
  public int getDayOfMonth()
  {
    return dayOfMonth;
  }
  
  public CalendarDate setDayOfMonth(int paramInt)
  {
    if (dayOfMonth != paramInt)
    {
      dayOfMonth = paramInt;
      normalized = false;
    }
    return this;
  }
  
  public CalendarDate addDayOfMonth(int paramInt)
  {
    if (paramInt != 0)
    {
      dayOfMonth += paramInt;
      normalized = false;
    }
    return this;
  }
  
  public int getDayOfWeek()
  {
    if (!isNormalized()) {
      dayOfWeek = Integer.MIN_VALUE;
    }
    return dayOfWeek;
  }
  
  public int getHours()
  {
    return hours;
  }
  
  public CalendarDate setHours(int paramInt)
  {
    if (hours != paramInt)
    {
      hours = paramInt;
      normalized = false;
    }
    return this;
  }
  
  public CalendarDate addHours(int paramInt)
  {
    if (paramInt != 0)
    {
      hours += paramInt;
      normalized = false;
    }
    return this;
  }
  
  public int getMinutes()
  {
    return minutes;
  }
  
  public CalendarDate setMinutes(int paramInt)
  {
    if (minutes != paramInt)
    {
      minutes = paramInt;
      normalized = false;
    }
    return this;
  }
  
  public CalendarDate addMinutes(int paramInt)
  {
    if (paramInt != 0)
    {
      minutes += paramInt;
      normalized = false;
    }
    return this;
  }
  
  public int getSeconds()
  {
    return seconds;
  }
  
  public CalendarDate setSeconds(int paramInt)
  {
    if (seconds != paramInt)
    {
      seconds = paramInt;
      normalized = false;
    }
    return this;
  }
  
  public CalendarDate addSeconds(int paramInt)
  {
    if (paramInt != 0)
    {
      seconds += paramInt;
      normalized = false;
    }
    return this;
  }
  
  public int getMillis()
  {
    return millis;
  }
  
  public CalendarDate setMillis(int paramInt)
  {
    if (millis != paramInt)
    {
      millis = paramInt;
      normalized = false;
    }
    return this;
  }
  
  public CalendarDate addMillis(int paramInt)
  {
    if (paramInt != 0)
    {
      millis += paramInt;
      normalized = false;
    }
    return this;
  }
  
  public long getTimeOfDay()
  {
    if (!isNormalized()) {
      return fraction = Long.MIN_VALUE;
    }
    return fraction;
  }
  
  public CalendarDate setDate(int paramInt1, int paramInt2, int paramInt3)
  {
    setYear(paramInt1);
    setMonth(paramInt2);
    setDayOfMonth(paramInt3);
    return this;
  }
  
  public CalendarDate addDate(int paramInt1, int paramInt2, int paramInt3)
  {
    addYear(paramInt1);
    addMonth(paramInt2);
    addDayOfMonth(paramInt3);
    return this;
  }
  
  public CalendarDate setTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setHours(paramInt1);
    setMinutes(paramInt2);
    setSeconds(paramInt3);
    setMillis(paramInt4);
    return this;
  }
  
  public CalendarDate addTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    addHours(paramInt1);
    addMinutes(paramInt2);
    addSeconds(paramInt3);
    addMillis(paramInt4);
    return this;
  }
  
  protected void setTimeOfDay(long paramLong)
  {
    fraction = paramLong;
  }
  
  public boolean isNormalized()
  {
    return normalized;
  }
  
  public boolean isStandardTime()
  {
    return forceStandardTime;
  }
  
  public void setStandardTime(boolean paramBoolean)
  {
    forceStandardTime = paramBoolean;
  }
  
  public boolean isDaylightTime()
  {
    if (isStandardTime()) {
      return false;
    }
    return daylightSaving != 0;
  }
  
  protected void setLocale(Locale paramLocale)
  {
    locale = paramLocale;
  }
  
  public TimeZone getZone()
  {
    return zoneinfo;
  }
  
  public CalendarDate setZone(TimeZone paramTimeZone)
  {
    zoneinfo = paramTimeZone;
    return this;
  }
  
  public boolean isSameDate(CalendarDate paramCalendarDate)
  {
    return (getDayOfWeek() == paramCalendarDate.getDayOfWeek()) && (getMonth() == paramCalendarDate.getMonth()) && (getYear() == paramCalendarDate.getYear()) && (getEra() == paramCalendarDate.getEra());
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CalendarDate)) {
      return false;
    }
    CalendarDate localCalendarDate = (CalendarDate)paramObject;
    if (isNormalized() != localCalendarDate.isNormalized()) {
      return false;
    }
    int i = zoneinfo != null ? 1 : 0;
    int j = zoneinfo != null ? 1 : 0;
    if (i != j) {
      return false;
    }
    if ((i != 0) && (!zoneinfo.equals(zoneinfo))) {
      return false;
    }
    return (getEra() == localCalendarDate.getEra()) && (year == year) && (month == month) && (dayOfMonth == dayOfMonth) && (hours == hours) && (minutes == minutes) && (seconds == seconds) && (millis == millis) && (zoneOffset == zoneOffset);
  }
  
  public int hashCode()
  {
    long l = (((year - 1970L) * 12L + (month - 1)) * 30L + dayOfMonth) * 24L;
    l = (((l + hours) * 60L + minutes) * 60L + seconds) * 1000L + millis;
    l -= zoneOffset;
    int i = isNormalized() ? 1 : 0;
    int j = 0;
    Era localEra = getEra();
    if (localEra != null) {
      j = localEra.hashCode();
    }
    int k = zoneinfo != null ? zoneinfo.hashCode() : 0;
    return (int)l * (int)(l >> 32) ^ j ^ i ^ k;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    CalendarUtils.sprintf0d(localStringBuilder, year, 4).append('-');
    CalendarUtils.sprintf0d(localStringBuilder, month, 2).append('-');
    CalendarUtils.sprintf0d(localStringBuilder, dayOfMonth, 2).append('T');
    CalendarUtils.sprintf0d(localStringBuilder, hours, 2).append(':');
    CalendarUtils.sprintf0d(localStringBuilder, minutes, 2).append(':');
    CalendarUtils.sprintf0d(localStringBuilder, seconds, 2).append('.');
    CalendarUtils.sprintf0d(localStringBuilder, millis, 3);
    if (zoneOffset == 0)
    {
      localStringBuilder.append('Z');
    }
    else if (zoneOffset != Integer.MIN_VALUE)
    {
      int i;
      char c;
      if (zoneOffset > 0)
      {
        i = zoneOffset;
        c = '+';
      }
      else
      {
        i = -zoneOffset;
        c = '-';
      }
      i /= 60000;
      localStringBuilder.append(c);
      CalendarUtils.sprintf0d(localStringBuilder, i / 60, 2);
      CalendarUtils.sprintf0d(localStringBuilder, i % 60, 2);
    }
    else
    {
      localStringBuilder.append(" local time");
    }
    return localStringBuilder.toString();
  }
  
  protected void setDayOfWeek(int paramInt)
  {
    dayOfWeek = paramInt;
  }
  
  protected void setNormalized(boolean paramBoolean)
  {
    normalized = paramBoolean;
  }
  
  public int getZoneOffset()
  {
    return zoneOffset;
  }
  
  protected void setZoneOffset(int paramInt)
  {
    zoneOffset = paramInt;
  }
  
  public int getDaylightSaving()
  {
    return daylightSaving;
  }
  
  protected void setDaylightSaving(int paramInt)
  {
    daylightSaving = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\calendar\CalendarDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */