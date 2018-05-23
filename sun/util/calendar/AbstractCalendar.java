package sun.util.calendar;

import java.util.TimeZone;

public abstract class AbstractCalendar
  extends CalendarSystem
{
  static final int SECOND_IN_MILLIS = 1000;
  static final int MINUTE_IN_MILLIS = 60000;
  static final int HOUR_IN_MILLIS = 3600000;
  static final int DAY_IN_MILLIS = 86400000;
  static final int EPOCH_OFFSET = 719163;
  private Era[] eras;
  
  protected AbstractCalendar() {}
  
  public Era getEra(String paramString)
  {
    if (eras != null) {
      for (int i = 0; i < eras.length; i++) {
        if (eras[i].equals(paramString)) {
          return eras[i];
        }
      }
    }
    return null;
  }
  
  public Era[] getEras()
  {
    Era[] arrayOfEra = null;
    if (eras != null)
    {
      arrayOfEra = new Era[eras.length];
      System.arraycopy(eras, 0, arrayOfEra, 0, eras.length);
    }
    return arrayOfEra;
  }
  
  public void setEra(CalendarDate paramCalendarDate, String paramString)
  {
    if (eras == null) {
      return;
    }
    for (int i = 0; i < eras.length; i++)
    {
      Era localEra = eras[i];
      if ((localEra != null) && (localEra.getName().equals(paramString)))
      {
        paramCalendarDate.setEra(localEra);
        return;
      }
    }
    throw new IllegalArgumentException("unknown era name: " + paramString);
  }
  
  protected void setEras(Era[] paramArrayOfEra)
  {
    eras = paramArrayOfEra;
  }
  
  public CalendarDate getCalendarDate()
  {
    return getCalendarDate(System.currentTimeMillis(), newCalendarDate());
  }
  
  public CalendarDate getCalendarDate(long paramLong)
  {
    return getCalendarDate(paramLong, newCalendarDate());
  }
  
  public CalendarDate getCalendarDate(long paramLong, TimeZone paramTimeZone)
  {
    CalendarDate localCalendarDate = newCalendarDate(paramTimeZone);
    return getCalendarDate(paramLong, localCalendarDate);
  }
  
  public CalendarDate getCalendarDate(long paramLong, CalendarDate paramCalendarDate)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    long l = 0L;
    TimeZone localTimeZone = paramCalendarDate.getZone();
    if (localTimeZone != null)
    {
      int[] arrayOfInt = new int[2];
      if ((localTimeZone instanceof ZoneInfo))
      {
        j = ((ZoneInfo)localTimeZone).getOffsets(paramLong, arrayOfInt);
      }
      else
      {
        j = localTimeZone.getOffset(paramLong);
        arrayOfInt[0] = localTimeZone.getRawOffset();
        arrayOfInt[1] = (j - arrayOfInt[0]);
      }
      l = j / 86400000;
      i = j % 86400000;
      k = arrayOfInt[1];
    }
    paramCalendarDate.setZoneOffset(j);
    paramCalendarDate.setDaylightSaving(k);
    l += paramLong / 86400000L;
    i += (int)(paramLong % 86400000L);
    if (i >= 86400000)
    {
      i -= 86400000;
      l += 1L;
    }
    else
    {
      while (i < 0)
      {
        i += 86400000;
        l -= 1L;
      }
    }
    l += 719163L;
    getCalendarDateFromFixedDate(paramCalendarDate, l);
    setTimeOfDay(paramCalendarDate, i);
    paramCalendarDate.setLeapYear(isLeapYear(paramCalendarDate));
    paramCalendarDate.setNormalized(true);
    return paramCalendarDate;
  }
  
  public long getTime(CalendarDate paramCalendarDate)
  {
    long l1 = getFixedDate(paramCalendarDate);
    long l2 = (l1 - 719163L) * 86400000L + getTimeOfDay(paramCalendarDate);
    int i = 0;
    TimeZone localTimeZone = paramCalendarDate.getZone();
    if (localTimeZone != null)
    {
      if (paramCalendarDate.isNormalized()) {
        return l2 - paramCalendarDate.getZoneOffset();
      }
      int[] arrayOfInt = new int[2];
      if (paramCalendarDate.isStandardTime())
      {
        if ((localTimeZone instanceof ZoneInfo))
        {
          ((ZoneInfo)localTimeZone).getOffsetsByStandard(l2, arrayOfInt);
          i = arrayOfInt[0];
        }
        else
        {
          i = localTimeZone.getOffset(l2 - localTimeZone.getRawOffset());
        }
      }
      else if ((localTimeZone instanceof ZoneInfo)) {
        i = ((ZoneInfo)localTimeZone).getOffsetsByWall(l2, arrayOfInt);
      } else {
        i = localTimeZone.getOffset(l2 - localTimeZone.getRawOffset());
      }
    }
    l2 -= i;
    getCalendarDate(l2, paramCalendarDate);
    return l2;
  }
  
  protected long getTimeOfDay(CalendarDate paramCalendarDate)
  {
    long l = paramCalendarDate.getTimeOfDay();
    if (l != Long.MIN_VALUE) {
      return l;
    }
    l = getTimeOfDayValue(paramCalendarDate);
    paramCalendarDate.setTimeOfDay(l);
    return l;
  }
  
  public long getTimeOfDayValue(CalendarDate paramCalendarDate)
  {
    long l = paramCalendarDate.getHours();
    l *= 60L;
    l += paramCalendarDate.getMinutes();
    l *= 60L;
    l += paramCalendarDate.getSeconds();
    l *= 1000L;
    l += paramCalendarDate.getMillis();
    return l;
  }
  
  public CalendarDate setTimeOfDay(CalendarDate paramCalendarDate, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    boolean bool = paramCalendarDate.isNormalized();
    int i = paramInt;
    int j = i / 3600000;
    i %= 3600000;
    int k = i / 60000;
    i %= 60000;
    int m = i / 1000;
    i %= 1000;
    paramCalendarDate.setHours(j);
    paramCalendarDate.setMinutes(k);
    paramCalendarDate.setSeconds(m);
    paramCalendarDate.setMillis(i);
    paramCalendarDate.setTimeOfDay(paramInt);
    if ((j < 24) && (bool)) {
      paramCalendarDate.setNormalized(bool);
    }
    return paramCalendarDate;
  }
  
  public int getWeekLength()
  {
    return 7;
  }
  
  protected abstract boolean isLeapYear(CalendarDate paramCalendarDate);
  
  public CalendarDate getNthDayOfWeek(int paramInt1, int paramInt2, CalendarDate paramCalendarDate)
  {
    CalendarDate localCalendarDate = (CalendarDate)paramCalendarDate.clone();
    normalize(localCalendarDate);
    long l1 = getFixedDate(localCalendarDate);
    long l2;
    if (paramInt1 > 0) {
      l2 = 7 * paramInt1 + getDayOfWeekDateBefore(l1, paramInt2);
    } else {
      l2 = 7 * paramInt1 + getDayOfWeekDateAfter(l1, paramInt2);
    }
    getCalendarDateFromFixedDate(localCalendarDate, l2);
    return localCalendarDate;
  }
  
  static long getDayOfWeekDateBefore(long paramLong, int paramInt)
  {
    return getDayOfWeekDateOnOrBefore(paramLong - 1L, paramInt);
  }
  
  static long getDayOfWeekDateAfter(long paramLong, int paramInt)
  {
    return getDayOfWeekDateOnOrBefore(paramLong + 7L, paramInt);
  }
  
  public static long getDayOfWeekDateOnOrBefore(long paramLong, int paramInt)
  {
    long l = paramLong - (paramInt - 1);
    if (l >= 0L) {
      return paramLong - l % 7L;
    }
    return paramLong - CalendarUtils.mod(l, 7L);
  }
  
  protected abstract long getFixedDate(CalendarDate paramCalendarDate);
  
  protected abstract void getCalendarDateFromFixedDate(CalendarDate paramCalendarDate, long paramLong);
  
  public boolean validateTime(CalendarDate paramCalendarDate)
  {
    int i = paramCalendarDate.getHours();
    if ((i < 0) || (i >= 24)) {
      return false;
    }
    i = paramCalendarDate.getMinutes();
    if ((i < 0) || (i >= 60)) {
      return false;
    }
    i = paramCalendarDate.getSeconds();
    if ((i < 0) || (i >= 60)) {
      return false;
    }
    i = paramCalendarDate.getMillis();
    return (i >= 0) && (i < 1000);
  }
  
  int normalizeTime(CalendarDate paramCalendarDate)
  {
    long l1 = getTimeOfDay(paramCalendarDate);
    long l2 = 0L;
    if (l1 >= 86400000L)
    {
      l2 = l1 / 86400000L;
      l1 %= 86400000L;
    }
    else if (l1 < 0L)
    {
      l2 = CalendarUtils.floorDivide(l1, 86400000L);
      if (l2 != 0L) {
        l1 -= 86400000L * l2;
      }
    }
    if (l2 != 0L) {
      paramCalendarDate.setTimeOfDay(l1);
    }
    paramCalendarDate.setMillis((int)(l1 % 1000L));
    l1 /= 1000L;
    paramCalendarDate.setSeconds((int)(l1 % 60L));
    l1 /= 60L;
    paramCalendarDate.setMinutes((int)(l1 % 60L));
    paramCalendarDate.setHours((int)(l1 / 60L));
    return (int)l2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\calendar\AbstractCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */