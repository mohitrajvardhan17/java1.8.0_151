package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.BaseCalendar.Date;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Gregorian;

public class SimpleTimeZone
  extends TimeZone
{
  private int startMonth;
  private int startDay;
  private int startDayOfWeek;
  private int startTime;
  private int startTimeMode;
  private int endMonth;
  private int endDay;
  private int endDayOfWeek;
  private int endTime;
  private int endTimeMode;
  private int startYear;
  private int rawOffset;
  private boolean useDaylight = false;
  private static final int millisPerHour = 3600000;
  private static final int millisPerDay = 86400000;
  private final byte[] monthLength = staticMonthLength;
  private static final byte[] staticMonthLength = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  private static final byte[] staticLeapMonthLength = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  private int startMode;
  private int endMode;
  private int dstSavings;
  private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
  private transient long cacheYear;
  private transient long cacheStart;
  private transient long cacheEnd;
  private static final int DOM_MODE = 1;
  private static final int DOW_IN_MONTH_MODE = 2;
  private static final int DOW_GE_DOM_MODE = 3;
  private static final int DOW_LE_DOM_MODE = 4;
  public static final int WALL_TIME = 0;
  public static final int STANDARD_TIME = 1;
  public static final int UTC_TIME = 2;
  static final long serialVersionUID = -403250971215465050L;
  static final int currentSerialVersion = 2;
  private int serialVersionOnStream = 2;
  private static final int MAX_RULE_NUM = 6;
  
  public SimpleTimeZone(int paramInt, String paramString)
  {
    rawOffset = paramInt;
    setID(paramString);
    dstSavings = 3600000;
  }
  
  public SimpleTimeZone(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
  {
    this(paramInt1, paramString, paramInt2, paramInt3, paramInt4, paramInt5, 0, paramInt6, paramInt7, paramInt8, paramInt9, 0, 3600000);
  }
  
  public SimpleTimeZone(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
  {
    this(paramInt1, paramString, paramInt2, paramInt3, paramInt4, paramInt5, 0, paramInt6, paramInt7, paramInt8, paramInt9, 0, paramInt10);
  }
  
  public SimpleTimeZone(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12)
  {
    setID(paramString);
    rawOffset = paramInt1;
    startMonth = paramInt2;
    startDay = paramInt3;
    startDayOfWeek = paramInt4;
    startTime = paramInt5;
    startTimeMode = paramInt6;
    endMonth = paramInt7;
    endDay = paramInt8;
    endDayOfWeek = paramInt9;
    endTime = paramInt10;
    endTimeMode = paramInt11;
    dstSavings = paramInt12;
    decodeRules();
    if (paramInt12 <= 0) {
      throw new IllegalArgumentException("Illegal daylight saving value: " + paramInt12);
    }
  }
  
  public void setStartYear(int paramInt)
  {
    startYear = paramInt;
    invalidateCache();
  }
  
  public void setStartRule(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    startMonth = paramInt1;
    startDay = paramInt2;
    startDayOfWeek = paramInt3;
    startTime = paramInt4;
    startTimeMode = 0;
    decodeStartRule();
    invalidateCache();
  }
  
  public void setStartRule(int paramInt1, int paramInt2, int paramInt3)
  {
    setStartRule(paramInt1, paramInt2, 0, paramInt3);
  }
  
  public void setStartRule(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramBoolean) {
      setStartRule(paramInt1, paramInt2, -paramInt3, paramInt4);
    } else {
      setStartRule(paramInt1, -paramInt2, -paramInt3, paramInt4);
    }
  }
  
  public void setEndRule(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    endMonth = paramInt1;
    endDay = paramInt2;
    endDayOfWeek = paramInt3;
    endTime = paramInt4;
    endTimeMode = 0;
    decodeEndRule();
    invalidateCache();
  }
  
  public void setEndRule(int paramInt1, int paramInt2, int paramInt3)
  {
    setEndRule(paramInt1, paramInt2, 0, paramInt3);
  }
  
  public void setEndRule(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramBoolean) {
      setEndRule(paramInt1, paramInt2, -paramInt3, paramInt4);
    } else {
      setEndRule(paramInt1, -paramInt2, -paramInt3, paramInt4);
    }
  }
  
  public int getOffset(long paramLong)
  {
    return getOffsets(paramLong, null);
  }
  
  int getOffsets(long paramLong, int[] paramArrayOfInt)
  {
    int i = rawOffset;
    if (useDaylight)
    {
      synchronized (this)
      {
        if ((cacheStart != 0L) && (paramLong >= cacheStart) && (paramLong < cacheEnd))
        {
          i += dstSavings;
          break label165;
        }
      }
      ??? = paramLong >= -12219292800000L ? gcal : (BaseCalendar)CalendarSystem.forName("julian");
      BaseCalendar.Date localDate = (BaseCalendar.Date)((BaseCalendar)???).newCalendarDate(TimeZone.NO_TIMEZONE);
      ((BaseCalendar)???).getCalendarDate(paramLong + rawOffset, localDate);
      int j = localDate.getNormalizedYear();
      if (j >= startYear)
      {
        localDate.setTimeOfDay(0, 0, 0, 0);
        i = getOffset((BaseCalendar)???, localDate, j, paramLong);
      }
    }
    label165:
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = rawOffset;
      paramArrayOfInt[1] = (i - rawOffset);
    }
    return i;
  }
  
  public int getOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if ((paramInt1 != 1) && (paramInt1 != 0)) {
      throw new IllegalArgumentException("Illegal era " + paramInt1);
    }
    int i = paramInt2;
    if (paramInt1 == 0) {
      i = 1 - i;
    }
    if (i >= 292278994) {
      i = 2800 + i % 2800;
    } else if (i <= -292269054) {
      i = (int)CalendarUtils.mod(i, 28L);
    }
    int j = paramInt3 + 1;
    Object localObject = gcal;
    BaseCalendar.Date localDate = (BaseCalendar.Date)((BaseCalendar)localObject).newCalendarDate(TimeZone.NO_TIMEZONE);
    localDate.setDate(i, j, paramInt4);
    long l = ((BaseCalendar)localObject).getTime(localDate);
    l += paramInt6 - rawOffset;
    if (l < -12219292800000L)
    {
      localObject = (BaseCalendar)CalendarSystem.forName("julian");
      localDate = (BaseCalendar.Date)((BaseCalendar)localObject).newCalendarDate(TimeZone.NO_TIMEZONE);
      localDate.setNormalizedDate(i, j, paramInt4);
      l = ((BaseCalendar)localObject).getTime(localDate) + paramInt6 - rawOffset;
    }
    if ((localDate.getNormalizedYear() != i) || (localDate.getMonth() != j) || (localDate.getDayOfMonth() != paramInt4) || (paramInt5 < 1) || (paramInt5 > 7) || (paramInt6 < 0) || (paramInt6 >= 86400000)) {
      throw new IllegalArgumentException();
    }
    if ((!useDaylight) || (paramInt2 < startYear) || (paramInt1 != 1)) {
      return rawOffset;
    }
    return getOffset((BaseCalendar)localObject, localDate, i, l);
  }
  
  private int getOffset(BaseCalendar paramBaseCalendar, BaseCalendar.Date paramDate, int paramInt, long paramLong)
  {
    synchronized (this)
    {
      if (cacheStart != 0L)
      {
        if ((paramLong >= cacheStart) && (paramLong < cacheEnd)) {
          return rawOffset + dstSavings;
        }
        if (paramInt == cacheYear) {
          return rawOffset;
        }
      }
    }
    long l1 = getStart(paramBaseCalendar, paramDate, paramInt);
    long l2 = getEnd(paramBaseCalendar, paramDate, paramInt);
    int i = rawOffset;
    if (l1 <= l2)
    {
      if ((paramLong >= l1) && (paramLong < l2)) {
        i += dstSavings;
      }
      synchronized (this)
      {
        cacheYear = paramInt;
        cacheStart = l1;
        cacheEnd = l2;
      }
    }
    else
    {
      if (paramLong < l2)
      {
        l1 = getStart(paramBaseCalendar, paramDate, paramInt - 1);
        if (paramLong >= l1) {
          i += dstSavings;
        }
      }
      else if (paramLong >= l1)
      {
        l2 = getEnd(paramBaseCalendar, paramDate, paramInt + 1);
        if (paramLong < l2) {
          i += dstSavings;
        }
      }
      if (l1 <= l2) {
        synchronized (this)
        {
          cacheYear = (startYear - 1L);
          cacheStart = l1;
          cacheEnd = l2;
        }
      }
    }
    return i;
  }
  
  private long getStart(BaseCalendar paramBaseCalendar, BaseCalendar.Date paramDate, int paramInt)
  {
    int i = startTime;
    if (startTimeMode != 2) {
      i -= rawOffset;
    }
    return getTransition(paramBaseCalendar, paramDate, startMode, paramInt, startMonth, startDay, startDayOfWeek, i);
  }
  
  private long getEnd(BaseCalendar paramBaseCalendar, BaseCalendar.Date paramDate, int paramInt)
  {
    int i = endTime;
    if (endTimeMode != 2) {
      i -= rawOffset;
    }
    if (endTimeMode == 0) {
      i -= dstSavings;
    }
    return getTransition(paramBaseCalendar, paramDate, endMode, paramInt, endMonth, endDay, endDayOfWeek, i);
  }
  
  private long getTransition(BaseCalendar paramBaseCalendar, BaseCalendar.Date paramDate, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    paramDate.setNormalizedYear(paramInt2);
    paramDate.setMonth(paramInt3 + 1);
    switch (paramInt1)
    {
    case 1: 
      paramDate.setDayOfMonth(paramInt4);
      break;
    case 2: 
      paramDate.setDayOfMonth(1);
      if (paramInt4 < 0) {
        paramDate.setDayOfMonth(paramBaseCalendar.getMonthLength(paramDate));
      }
      paramDate = (BaseCalendar.Date)paramBaseCalendar.getNthDayOfWeek(paramInt4, paramInt5, paramDate);
      break;
    case 3: 
      paramDate.setDayOfMonth(paramInt4);
      paramDate = (BaseCalendar.Date)paramBaseCalendar.getNthDayOfWeek(1, paramInt5, paramDate);
      break;
    case 4: 
      paramDate.setDayOfMonth(paramInt4);
      paramDate = (BaseCalendar.Date)paramBaseCalendar.getNthDayOfWeek(-1, paramInt5, paramDate);
    }
    return paramBaseCalendar.getTime(paramDate) + paramInt6;
  }
  
  public int getRawOffset()
  {
    return rawOffset;
  }
  
  public void setRawOffset(int paramInt)
  {
    rawOffset = paramInt;
  }
  
  public void setDSTSavings(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Illegal daylight saving value: " + paramInt);
    }
    dstSavings = paramInt;
  }
  
  public int getDSTSavings()
  {
    return useDaylight ? dstSavings : 0;
  }
  
  public boolean useDaylightTime()
  {
    return useDaylight;
  }
  
  public boolean observesDaylightTime()
  {
    return useDaylightTime();
  }
  
  public boolean inDaylightTime(Date paramDate)
  {
    return getOffset(paramDate.getTime()) != rawOffset;
  }
  
  public Object clone()
  {
    return super.clone();
  }
  
  public synchronized int hashCode()
  {
    return startMonth ^ startDay ^ startDayOfWeek ^ startTime ^ endMonth ^ endDay ^ endDayOfWeek ^ endTime ^ rawOffset;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SimpleTimeZone)) {
      return false;
    }
    SimpleTimeZone localSimpleTimeZone = (SimpleTimeZone)paramObject;
    return (getID().equals(localSimpleTimeZone.getID())) && (hasSameRules(localSimpleTimeZone));
  }
  
  public boolean hasSameRules(TimeZone paramTimeZone)
  {
    if (this == paramTimeZone) {
      return true;
    }
    if (!(paramTimeZone instanceof SimpleTimeZone)) {
      return false;
    }
    SimpleTimeZone localSimpleTimeZone = (SimpleTimeZone)paramTimeZone;
    return (rawOffset == rawOffset) && (useDaylight == useDaylight) && ((!useDaylight) || ((dstSavings == dstSavings) && (startMode == startMode) && (startMonth == startMonth) && (startDay == startDay) && (startDayOfWeek == startDayOfWeek) && (startTime == startTime) && (startTimeMode == startTimeMode) && (endMode == endMode) && (endMonth == endMonth) && (endDay == endDay) && (endDayOfWeek == endDayOfWeek) && (endTime == endTime) && (endTimeMode == endTimeMode) && (startYear == startYear)));
  }
  
  public String toString()
  {
    return getClass().getName() + "[id=" + getID() + ",offset=" + rawOffset + ",dstSavings=" + dstSavings + ",useDaylight=" + useDaylight + ",startYear=" + startYear + ",startMode=" + startMode + ",startMonth=" + startMonth + ",startDay=" + startDay + ",startDayOfWeek=" + startDayOfWeek + ",startTime=" + startTime + ",startTimeMode=" + startTimeMode + ",endMode=" + endMode + ",endMonth=" + endMonth + ",endDay=" + endDay + ",endDayOfWeek=" + endDayOfWeek + ",endTime=" + endTime + ",endTimeMode=" + endTimeMode + ']';
  }
  
  private synchronized void invalidateCache()
  {
    cacheYear = (startYear - 1);
    cacheStart = (cacheEnd = 0L);
  }
  
  private void decodeRules()
  {
    decodeStartRule();
    decodeEndRule();
  }
  
  private void decodeStartRule()
  {
    useDaylight = ((startDay != 0) && (endDay != 0));
    if (startDay != 0)
    {
      if ((startMonth < 0) || (startMonth > 11)) {
        throw new IllegalArgumentException("Illegal start month " + startMonth);
      }
      if ((startTime < 0) || (startTime > 86400000)) {
        throw new IllegalArgumentException("Illegal start time " + startTime);
      }
      if (startDayOfWeek == 0)
      {
        startMode = 1;
      }
      else
      {
        if (startDayOfWeek > 0)
        {
          startMode = 2;
        }
        else
        {
          startDayOfWeek = (-startDayOfWeek);
          if (startDay > 0)
          {
            startMode = 3;
          }
          else
          {
            startDay = (-startDay);
            startMode = 4;
          }
        }
        if (startDayOfWeek > 7) {
          throw new IllegalArgumentException("Illegal start day of week " + startDayOfWeek);
        }
      }
      if (startMode == 2)
      {
        if ((startDay < -5) || (startDay > 5)) {
          throw new IllegalArgumentException("Illegal start day of week in month " + startDay);
        }
      }
      else if ((startDay < 1) || (startDay > staticMonthLength[startMonth])) {
        throw new IllegalArgumentException("Illegal start day " + startDay);
      }
    }
  }
  
  private void decodeEndRule()
  {
    useDaylight = ((startDay != 0) && (endDay != 0));
    if (endDay != 0)
    {
      if ((endMonth < 0) || (endMonth > 11)) {
        throw new IllegalArgumentException("Illegal end month " + endMonth);
      }
      if ((endTime < 0) || (endTime > 86400000)) {
        throw new IllegalArgumentException("Illegal end time " + endTime);
      }
      if (endDayOfWeek == 0)
      {
        endMode = 1;
      }
      else
      {
        if (endDayOfWeek > 0)
        {
          endMode = 2;
        }
        else
        {
          endDayOfWeek = (-endDayOfWeek);
          if (endDay > 0)
          {
            endMode = 3;
          }
          else
          {
            endDay = (-endDay);
            endMode = 4;
          }
        }
        if (endDayOfWeek > 7) {
          throw new IllegalArgumentException("Illegal end day of week " + endDayOfWeek);
        }
      }
      if (endMode == 2)
      {
        if ((endDay < -5) || (endDay > 5)) {
          throw new IllegalArgumentException("Illegal end day of week in month " + endDay);
        }
      }
      else if ((endDay < 1) || (endDay > staticMonthLength[endMonth])) {
        throw new IllegalArgumentException("Illegal end day " + endDay);
      }
    }
  }
  
  private void makeRulesCompatible()
  {
    switch (startMode)
    {
    case 1: 
      startDay = (1 + startDay / 7);
      startDayOfWeek = 1;
      break;
    case 3: 
      if (startDay != 1) {
        startDay = (1 + startDay / 7);
      }
      break;
    case 4: 
      if (startDay >= 30) {
        startDay = -1;
      } else {
        startDay = (1 + startDay / 7);
      }
      break;
    }
    switch (endMode)
    {
    case 1: 
      endDay = (1 + endDay / 7);
      endDayOfWeek = 1;
      break;
    case 3: 
      if (endDay != 1) {
        endDay = (1 + endDay / 7);
      }
      break;
    case 4: 
      if (endDay >= 30) {
        endDay = -1;
      } else {
        endDay = (1 + endDay / 7);
      }
      break;
    }
    switch (startTimeMode)
    {
    case 2: 
      startTime += rawOffset;
    }
    while (startTime < 0)
    {
      startTime += 86400000;
      startDayOfWeek = (1 + (startDayOfWeek + 5) % 7);
    }
    while (startTime >= 86400000)
    {
      startTime -= 86400000;
      startDayOfWeek = (1 + startDayOfWeek % 7);
    }
    switch (endTimeMode)
    {
    case 2: 
      endTime += rawOffset + dstSavings;
      break;
    case 1: 
      endTime += dstSavings;
    }
    while (endTime < 0)
    {
      endTime += 86400000;
      endDayOfWeek = (1 + (endDayOfWeek + 5) % 7);
    }
    while (endTime >= 86400000)
    {
      endTime -= 86400000;
      endDayOfWeek = (1 + endDayOfWeek % 7);
    }
  }
  
  private byte[] packRules()
  {
    byte[] arrayOfByte = new byte[6];
    arrayOfByte[0] = ((byte)startDay);
    arrayOfByte[1] = ((byte)startDayOfWeek);
    arrayOfByte[2] = ((byte)endDay);
    arrayOfByte[3] = ((byte)endDayOfWeek);
    arrayOfByte[4] = ((byte)startTimeMode);
    arrayOfByte[5] = ((byte)endTimeMode);
    return arrayOfByte;
  }
  
  private void unpackRules(byte[] paramArrayOfByte)
  {
    startDay = paramArrayOfByte[0];
    startDayOfWeek = paramArrayOfByte[1];
    endDay = paramArrayOfByte[2];
    endDayOfWeek = paramArrayOfByte[3];
    if (paramArrayOfByte.length >= 6)
    {
      startTimeMode = paramArrayOfByte[4];
      endTimeMode = paramArrayOfByte[5];
    }
  }
  
  private int[] packTimes()
  {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = startTime;
    arrayOfInt[1] = endTime;
    return arrayOfInt;
  }
  
  private void unpackTimes(int[] paramArrayOfInt)
  {
    startTime = paramArrayOfInt[0];
    endTime = paramArrayOfInt[1];
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = packRules();
    int[] arrayOfInt = packTimes();
    makeRulesCompatible();
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(arrayOfByte.length);
    paramObjectOutputStream.write(arrayOfByte);
    paramObjectOutputStream.writeObject(arrayOfInt);
    unpackRules(arrayOfByte);
    unpackTimes(arrayOfInt);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (serialVersionOnStream < 1)
    {
      if (startDayOfWeek == 0) {
        startDayOfWeek = 1;
      }
      if (endDayOfWeek == 0) {
        endDayOfWeek = 1;
      }
      startMode = (endMode = 2);
      dstSavings = 3600000;
    }
    else
    {
      int i = paramObjectInputStream.readInt();
      if (i <= 6)
      {
        byte[] arrayOfByte = new byte[i];
        paramObjectInputStream.readFully(arrayOfByte);
        unpackRules(arrayOfByte);
      }
      else
      {
        throw new InvalidObjectException("Too many rules: " + i);
      }
    }
    if (serialVersionOnStream >= 2)
    {
      int[] arrayOfInt = (int[])paramObjectInputStream.readObject();
      unpackTimes(arrayOfInt);
    }
    serialVersionOnStream = 2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\SimpleTimeZone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */