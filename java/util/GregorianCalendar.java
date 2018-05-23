package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.BaseCalendar.Date;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.JulianCalendar;
import sun.util.calendar.ZoneInfo;

public class GregorianCalendar
  extends Calendar
{
  public static final int BC = 0;
  static final int BCE = 0;
  public static final int AD = 1;
  static final int CE = 1;
  private static final int EPOCH_OFFSET = 719163;
  private static final int EPOCH_YEAR = 1970;
  static final int[] MONTH_LENGTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  static final int[] LEAP_MONTH_LENGTH = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  private static final int ONE_SECOND = 1000;
  private static final int ONE_MINUTE = 60000;
  private static final int ONE_HOUR = 3600000;
  private static final long ONE_DAY = 86400000L;
  private static final long ONE_WEEK = 604800000L;
  static final int[] MIN_VALUES = { 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, -46800000, 0 };
  static final int[] LEAST_MAX_VALUES = { 1, 292269054, 11, 52, 4, 28, 365, 7, 4, 1, 11, 23, 59, 59, 999, 50400000, 1200000 };
  static final int[] MAX_VALUES = { 1, 292278994, 11, 53, 6, 31, 366, 7, 6, 1, 11, 23, 59, 59, 999, 50400000, 7200000 };
  static final long serialVersionUID = -8125100834729963327L;
  private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
  private static JulianCalendar jcal;
  private static Era[] jeras;
  static final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;
  private long gregorianCutover = -12219292800000L;
  private transient long gregorianCutoverDate = 577736L;
  private transient int gregorianCutoverYear = 1582;
  private transient int gregorianCutoverYearJulian = 1582;
  private transient BaseCalendar.Date gdate;
  private transient BaseCalendar.Date cdate;
  private transient BaseCalendar calsys;
  private transient int[] zoneOffsets;
  private transient int[] originalFields;
  private transient long cachedFixedDate = Long.MIN_VALUE;
  
  public GregorianCalendar()
  {
    this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
    setZoneShared(true);
  }
  
  public GregorianCalendar(TimeZone paramTimeZone)
  {
    this(paramTimeZone, Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public GregorianCalendar(Locale paramLocale)
  {
    this(TimeZone.getDefaultRef(), paramLocale);
    setZoneShared(true);
  }
  
  public GregorianCalendar(TimeZone paramTimeZone, Locale paramLocale)
  {
    super(paramTimeZone, paramLocale);
    gdate = gcal.newCalendarDate(paramTimeZone);
    setTimeInMillis(System.currentTimeMillis());
  }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramInt1, paramInt2, paramInt3, 0, 0, 0, 0);
  }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0);
  }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0);
  }
  
  GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    gdate = gcal.newCalendarDate(getZone());
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
    if ((paramInt4 >= 12) && (paramInt4 <= 23))
    {
      internalSet(9, 1);
      internalSet(10, paramInt4 - 12);
    }
    else
    {
      internalSet(10, paramInt4);
    }
    setFieldsComputed(1536);
    set(11, paramInt4);
    set(12, paramInt5);
    set(13, paramInt6);
    internalSet(14, paramInt7);
  }
  
  GregorianCalendar(TimeZone paramTimeZone, Locale paramLocale, boolean paramBoolean)
  {
    super(paramTimeZone, paramLocale);
    gdate = gcal.newCalendarDate(getZone());
  }
  
  public void setGregorianChange(Date paramDate)
  {
    long l = paramDate.getTime();
    if (l == gregorianCutover) {
      return;
    }
    complete();
    setGregorianChange(l);
  }
  
  private void setGregorianChange(long paramLong)
  {
    gregorianCutover = paramLong;
    gregorianCutoverDate = (CalendarUtils.floorDivide(paramLong, 86400000L) + 719163L);
    if (paramLong == Long.MAX_VALUE) {
      gregorianCutoverDate += 1L;
    }
    BaseCalendar.Date localDate = getGregorianCutoverDate();
    gregorianCutoverYear = localDate.getYear();
    BaseCalendar localBaseCalendar = getJulianCalendarSystem();
    localDate = (BaseCalendar.Date)localBaseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
    localBaseCalendar.getCalendarDateFromFixedDate(localDate, gregorianCutoverDate - 1L);
    gregorianCutoverYearJulian = localDate.getNormalizedYear();
    if (time < gregorianCutover) {
      setUnnormalized();
    }
  }
  
  public final Date getGregorianChange()
  {
    return new Date(gregorianCutover);
  }
  
  public boolean isLeapYear(int paramInt)
  {
    if ((paramInt & 0x3) != 0) {
      return false;
    }
    if (paramInt > gregorianCutoverYear) {
      return (paramInt % 100 != 0) || (paramInt % 400 == 0);
    }
    if (paramInt < gregorianCutoverYearJulian) {
      return true;
    }
    int i;
    if (gregorianCutoverYear == gregorianCutoverYearJulian)
    {
      BaseCalendar.Date localDate = getCalendarDate(gregorianCutoverDate);
      i = localDate.getMonth() < 3 ? 1 : 0;
    }
    else
    {
      i = paramInt == gregorianCutoverYear ? 1 : 0;
    }
    return (paramInt % 100 != 0) || (paramInt % 400 == 0);
  }
  
  public String getCalendarType()
  {
    return "gregory";
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof GregorianCalendar)) && (super.equals(paramObject)) && (gregorianCutover == gregorianCutover);
  }
  
  public int hashCode()
  {
    return super.hashCode() ^ (int)gregorianCutoverDate;
  }
  
  public void add(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return;
    }
    if ((paramInt1 < 0) || (paramInt1 >= 15)) {
      throw new IllegalArgumentException();
    }
    complete();
    int i;
    if (paramInt1 == 1)
    {
      i = internalGet(1);
      if (internalGetEra() == 1)
      {
        i += paramInt2;
        if (i > 0)
        {
          set(1, i);
        }
        else
        {
          set(1, 1 - i);
          set(0, 0);
        }
      }
      else
      {
        i -= paramInt2;
        if (i > 0)
        {
          set(1, i);
        }
        else
        {
          set(1, 1 - i);
          set(0, 1);
        }
      }
      pinDayOfMonth();
    }
    else if (paramInt1 == 2)
    {
      i = internalGet(2) + paramInt2;
      int j = internalGet(1);
      int k;
      if (i >= 0) {
        k = i / 12;
      } else {
        k = (i + 1) / 12 - 1;
      }
      if (k != 0) {
        if (internalGetEra() == 1)
        {
          j += k;
          if (j > 0)
          {
            set(1, j);
          }
          else
          {
            set(1, 1 - j);
            set(0, 0);
          }
        }
        else
        {
          j -= k;
          if (j > 0)
          {
            set(1, j);
          }
          else
          {
            set(1, 1 - j);
            set(0, 1);
          }
        }
      }
      if (i >= 0)
      {
        set(2, i % 12);
      }
      else
      {
        i %= 12;
        if (i < 0) {
          i += 12;
        }
        set(2, 0 + i);
      }
      pinDayOfMonth();
    }
    else if (paramInt1 == 0)
    {
      i = internalGet(0) + paramInt2;
      if (i < 0) {
        i = 0;
      }
      if (i > 1) {
        i = 1;
      }
      set(0, i);
    }
    else
    {
      long l1 = paramInt2;
      long l2 = 0L;
      switch (paramInt1)
      {
      case 10: 
      case 11: 
        l1 *= 3600000L;
        break;
      case 12: 
        l1 *= 60000L;
        break;
      case 13: 
        l1 *= 1000L;
        break;
      case 14: 
        break;
      case 3: 
      case 4: 
      case 8: 
        l1 *= 7L;
        break;
      case 5: 
      case 6: 
      case 7: 
        break;
      case 9: 
        l1 = paramInt2 / 2;
        l2 = 12 * (paramInt2 % 2);
      }
      if (paramInt1 >= 10)
      {
        setTimeInMillis(time + l1);
        return;
      }
      long l3 = getCurrentFixedDate();
      l2 += internalGet(11);
      l2 *= 60L;
      l2 += internalGet(12);
      l2 *= 60L;
      l2 += internalGet(13);
      l2 *= 1000L;
      l2 += internalGet(14);
      if (l2 >= 86400000L)
      {
        l3 += 1L;
        l2 -= 86400000L;
      }
      else if (l2 < 0L)
      {
        l3 -= 1L;
        l2 += 86400000L;
      }
      l3 += l1;
      int m = internalGet(15) + internalGet(16);
      setTimeInMillis((l3 - 719163L) * 86400000L + l2 - m);
      m -= internalGet(15) + internalGet(16);
      if (m != 0)
      {
        setTimeInMillis(time + m);
        long l4 = getCurrentFixedDate();
        if (l4 != l3) {
          setTimeInMillis(time - m);
        }
      }
    }
  }
  
  public void roll(int paramInt, boolean paramBoolean)
  {
    roll(paramInt, paramBoolean ? 1 : -1);
  }
  
  public void roll(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return;
    }
    if ((paramInt1 < 0) || (paramInt1 >= 15)) {
      throw new IllegalArgumentException();
    }
    complete();
    int i = getMinimum(paramInt1);
    int j = getMaximum(paramInt1);
    int k;
    int i1;
    int i2;
    long l7;
    Object localObject1;
    Object localObject2;
    long l4;
    int i12;
    long l1;
    int i7;
    switch (paramInt1)
    {
    case 0: 
    case 1: 
    case 9: 
    case 12: 
    case 13: 
    case 14: 
      break;
    case 10: 
    case 11: 
      k = j + 1;
      i1 = internalGet(paramInt1);
      i2 = (i1 + paramInt2) % k;
      if (i2 < 0) {
        i2 += k;
      }
      time += 3600000 * (i2 - i1);
      CalendarDate localCalendarDate = calsys.getCalendarDate(time, getZone());
      if (internalGet(5) != localCalendarDate.getDayOfMonth())
      {
        localCalendarDate.setDate(internalGet(1), internalGet(2) + 1, internalGet(5));
        if (paramInt1 == 10)
        {
          assert (internalGet(9) == 1);
          localCalendarDate.addHours(12);
        }
        time = calsys.getTime(localCalendarDate);
      }
      int i6 = localCalendarDate.getHours();
      internalSet(paramInt1, i6 % k);
      if (paramInt1 == 10)
      {
        internalSet(11, i6);
      }
      else
      {
        internalSet(9, i6 / 12);
        internalSet(10, i6 % 12);
      }
      int i9 = localCalendarDate.getZoneOffset();
      int i11 = localCalendarDate.getDaylightSaving();
      internalSet(15, i9 - i11);
      internalSet(16, i11);
      return;
    case 2: 
      if (!isCutoverYear(cdate.getNormalizedYear()))
      {
        k = (internalGet(2) + paramInt2) % 12;
        if (k < 0) {
          k += 12;
        }
        set(2, k);
        i1 = monthLength(k);
        if (internalGet(5) > i1) {
          set(5, i1);
        }
      }
      else
      {
        k = getActualMaximum(2) + 1;
        i1 = (internalGet(2) + paramInt2) % k;
        if (i1 < 0) {
          i1 += k;
        }
        set(2, i1);
        i2 = getActualMaximum(5);
        if (internalGet(5) > i2) {
          set(5, i2);
        }
      }
      return;
    case 3: 
      k = cdate.getNormalizedYear();
      j = getActualMaximum(3);
      set(7, internalGet(7));
      i1 = internalGet(3);
      i2 = i1 + paramInt2;
      if (!isCutoverYear(k))
      {
        int i4 = getWeekYear();
        if (i4 == k)
        {
          if ((i2 > i) && (i2 < j))
          {
            set(3, i2);
            return;
          }
          l7 = getCurrentFixedDate();
          l8 = l7 - 7 * (i1 - i);
          if (calsys.getYearFromFixedDate(l8) != k) {
            i++;
          }
          l7 += 7 * (j - internalGet(3));
          if (calsys.getYearFromFixedDate(l7) != k) {
            j--;
          }
        }
        else if (i4 > k)
        {
          if (paramInt2 < 0) {
            paramInt2++;
          }
          i1 = j;
        }
        else
        {
          if (paramInt2 > 0) {
            paramInt2 -= i1 - j;
          }
          i1 = i;
        }
        set(paramInt1, getRolledValue(i1, paramInt2, i, j));
        return;
      }
      long l6 = getCurrentFixedDate();
      if (gregorianCutoverYear == gregorianCutoverYearJulian) {
        localObject1 = getCutoverCalendarSystem();
      } else if (k == gregorianCutoverYear) {
        localObject1 = gcal;
      } else {
        localObject1 = getJulianCalendarSystem();
      }
      long l8 = l6 - 7 * (i1 - i);
      if (((BaseCalendar)localObject1).getYearFromFixedDate(l8) != k) {
        i++;
      }
      l6 += 7 * (j - i1);
      localObject1 = l6 >= gregorianCutoverDate ? gcal : getJulianCalendarSystem();
      if (((BaseCalendar)localObject1).getYearFromFixedDate(l6) != k) {
        j--;
      }
      i2 = getRolledValue(i1, paramInt2, i, j) - 1;
      localObject2 = getCalendarDate(l8 + i2 * 7);
      set(2, ((BaseCalendar.Date)localObject2).getMonth() - 1);
      set(5, ((BaseCalendar.Date)localObject2).getDayOfMonth());
      return;
    case 4: 
      boolean bool = isCutoverYear(cdate.getNormalizedYear());
      i1 = internalGet(7) - getFirstDayOfWeek();
      if (i1 < 0) {
        i1 += 7;
      }
      l4 = getCurrentFixedDate();
      if (bool)
      {
        l7 = getFixedDateMonth1(cdate, l4);
        i12 = actualMonthLength();
      }
      else
      {
        l7 = l4 - internalGet(5) + 1L;
        i12 = calsys.getMonthLength(cdate);
      }
      long l9 = BaseCalendar.getDayOfWeekDateOnOrBefore(l7 + 6L, getFirstDayOfWeek());
      if ((int)(l9 - l7) >= getMinimalDaysInFirstWeek()) {
        l9 -= 7L;
      }
      j = getActualMaximum(paramInt1);
      int i14 = getRolledValue(internalGet(paramInt1), paramInt2, 1, j) - 1;
      long l10 = l9 + i14 * 7 + i1;
      if (l10 < l7) {
        l10 = l7;
      } else if (l10 >= l7 + i12) {
        l10 = l7 + i12 - 1L;
      }
      int i15;
      if (bool)
      {
        BaseCalendar.Date localDate3 = getCalendarDate(l10);
        i15 = localDate3.getDayOfMonth();
      }
      else
      {
        i15 = (int)(l10 - l7) + 1;
      }
      set(5, i15);
      return;
    case 5: 
      if (!isCutoverYear(cdate.getNormalizedYear()))
      {
        j = calsys.getMonthLength(cdate);
      }
      else
      {
        l1 = getCurrentFixedDate();
        l4 = getFixedDateMonth1(cdate, l1);
        i7 = getRolledValue((int)(l1 - l4), paramInt2, 0, actualMonthLength() - 1);
        localObject1 = getCalendarDate(l4 + i7);
        assert (((BaseCalendar.Date)localObject1).getMonth() - 1 == internalGet(2));
        set(5, ((BaseCalendar.Date)localObject1).getDayOfMonth());
        return;
      }
      break;
    case 6: 
      j = getActualMaximum(paramInt1);
      if (isCutoverYear(cdate.getNormalizedYear()))
      {
        l1 = getCurrentFixedDate();
        l4 = l1 - internalGet(6) + 1L;
        i7 = getRolledValue((int)(l1 - l4) + 1, paramInt2, i, j);
        localObject1 = getCalendarDate(l4 + i7 - 1L);
        set(2, ((BaseCalendar.Date)localObject1).getMonth() - 1);
        set(5, ((BaseCalendar.Date)localObject1).getDayOfMonth());
        return;
      }
      break;
    case 7: 
      if (!isCutoverYear(cdate.getNormalizedYear()))
      {
        int m = internalGet(3);
        if ((m > 1) && (m < 52))
        {
          set(3, m);
          j = 7;
          break;
        }
      }
      paramInt2 %= 7;
      if (paramInt2 == 0) {
        return;
      }
      long l2 = getCurrentFixedDate();
      l4 = BaseCalendar.getDayOfWeekDateOnOrBefore(l2, getFirstDayOfWeek());
      l2 += paramInt2;
      if (l2 < l4) {
        l2 += 7L;
      } else if (l2 >= l4 + 7L) {
        l2 -= 7L;
      }
      BaseCalendar.Date localDate1 = getCalendarDate(l2);
      set(0, localDate1.getNormalizedYear() <= 0 ? 0 : 1);
      set(localDate1.getYear(), localDate1.getMonth() - 1, localDate1.getDayOfMonth());
      return;
    case 8: 
      i = 1;
      if (!isCutoverYear(cdate.getNormalizedYear()))
      {
        int n = internalGet(5);
        i1 = calsys.getMonthLength(cdate);
        int i3 = i1 % 7;
        j = i1 / 7;
        int i5 = (n - 1) % 7;
        if (i5 < i3) {
          j++;
        }
        set(7, internalGet(7));
      }
      else
      {
        long l3 = getCurrentFixedDate();
        long l5 = getFixedDateMonth1(cdate, l3);
        int i8 = actualMonthLength();
        int i10 = i8 % 7;
        j = i8 / 7;
        i12 = (int)(l3 - l5) % 7;
        if (i12 < i10) {
          j++;
        }
        int i13 = getRolledValue(internalGet(paramInt1), paramInt2, i, j) - 1;
        l3 = l5 + i13 * 7 + i12;
        localObject2 = l3 >= gregorianCutoverDate ? gcal : getJulianCalendarSystem();
        BaseCalendar.Date localDate2 = (BaseCalendar.Date)((BaseCalendar)localObject2).newCalendarDate(TimeZone.NO_TIMEZONE);
        ((BaseCalendar)localObject2).getCalendarDateFromFixedDate(localDate2, l3);
        set(5, localDate2.getDayOfMonth());
        return;
      }
      break;
    }
    set(paramInt1, getRolledValue(internalGet(paramInt1), paramInt2, i, j));
  }
  
  public int getMinimum(int paramInt)
  {
    return MIN_VALUES[paramInt];
  }
  
  public int getMaximum(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 8: 
      if (gregorianCutoverYear <= 200)
      {
        GregorianCalendar localGregorianCalendar = (GregorianCalendar)clone();
        localGregorianCalendar.setLenient(true);
        localGregorianCalendar.setTimeInMillis(gregorianCutover);
        int i = localGregorianCalendar.getActualMaximum(paramInt);
        localGregorianCalendar.setTimeInMillis(gregorianCutover - 1L);
        int j = localGregorianCalendar.getActualMaximum(paramInt);
        return Math.max(MAX_VALUES[paramInt], Math.max(i, j));
      }
      break;
    }
    return MAX_VALUES[paramInt];
  }
  
  public int getGreatestMinimum(int paramInt)
  {
    if (paramInt == 5)
    {
      BaseCalendar.Date localDate = getGregorianCutoverDate();
      long l = getFixedDateMonth1(localDate, gregorianCutoverDate);
      localDate = getCalendarDate(l);
      return Math.max(MIN_VALUES[paramInt], localDate.getDayOfMonth());
    }
    return MIN_VALUES[paramInt];
  }
  
  public int getLeastMaximum(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 8: 
      GregorianCalendar localGregorianCalendar = (GregorianCalendar)clone();
      localGregorianCalendar.setLenient(true);
      localGregorianCalendar.setTimeInMillis(gregorianCutover);
      int i = localGregorianCalendar.getActualMaximum(paramInt);
      localGregorianCalendar.setTimeInMillis(gregorianCutover - 1L);
      int j = localGregorianCalendar.getActualMaximum(paramInt);
      return Math.min(LEAST_MAX_VALUES[paramInt], Math.min(i, j));
    }
    return LEAST_MAX_VALUES[paramInt];
  }
  
  public int getActualMinimum(int paramInt)
  {
    if (paramInt == 5)
    {
      GregorianCalendar localGregorianCalendar = getNormalizedCalendar();
      int i = cdate.getNormalizedYear();
      if ((i == gregorianCutoverYear) || (i == gregorianCutoverYearJulian))
      {
        long l = getFixedDateMonth1(cdate, calsys.getFixedDate(cdate));
        BaseCalendar.Date localDate = getCalendarDate(l);
        return localDate.getDayOfMonth();
      }
    }
    return getMinimum(paramInt);
  }
  
  public int getActualMaximum(int paramInt)
  {
    if ((0x1FE81 & 1 << paramInt) != 0) {
      return getMaximum(paramInt);
    }
    GregorianCalendar localGregorianCalendar = getNormalizedCalendar();
    BaseCalendar.Date localDate1 = cdate;
    BaseCalendar localBaseCalendar1 = calsys;
    int i = localDate1.getNormalizedYear();
    int j = -1;
    long l1;
    int n;
    int i2;
    int m;
    switch (paramInt)
    {
    case 2: 
      if (!localGregorianCalendar.isCutoverYear(i))
      {
        j = 11;
      }
      else
      {
        do
        {
          l1 = gcal.getFixedDate(++i, 1, 1, null);
        } while (l1 < gregorianCutoverDate);
        BaseCalendar.Date localDate2 = (BaseCalendar.Date)localDate1.clone();
        localBaseCalendar1.getCalendarDateFromFixedDate(localDate2, l1 - 1L);
        j = localDate2.getMonth() - 1;
      }
      break;
    case 5: 
      j = localBaseCalendar1.getMonthLength(localDate1);
      if ((localGregorianCalendar.isCutoverYear(i)) && (localDate1.getDayOfMonth() != j))
      {
        l1 = localGregorianCalendar.getCurrentFixedDate();
        if (l1 < gregorianCutoverDate)
        {
          int i1 = localGregorianCalendar.actualMonthLength();
          long l5 = localGregorianCalendar.getFixedDateMonth1(cdate, l1) + i1 - 1L;
          BaseCalendar.Date localDate4 = localGregorianCalendar.getCalendarDate(l5);
          j = localDate4.getDayOfMonth();
        }
      }
      break;
    case 6: 
      if (!localGregorianCalendar.isCutoverYear(i))
      {
        j = localBaseCalendar1.getYearLength(localDate1);
      }
      else
      {
        if (gregorianCutoverYear == gregorianCutoverYearJulian)
        {
          BaseCalendar localBaseCalendar2 = localGregorianCalendar.getCutoverCalendarSystem();
          l1 = localBaseCalendar2.getFixedDate(i, 1, 1, null);
        }
        else if (i == gregorianCutoverYearJulian)
        {
          l1 = localBaseCalendar1.getFixedDate(i, 1, 1, null);
        }
        else
        {
          l1 = gregorianCutoverDate;
        }
        long l3 = gcal.getFixedDate(++i, 1, 1, null);
        if (l3 < gregorianCutoverDate) {
          l3 = gregorianCutoverDate;
        }
        assert (l1 <= localBaseCalendar1.getFixedDate(localDate1.getNormalizedYear(), localDate1.getMonth(), localDate1.getDayOfMonth(), localDate1));
        assert (l3 >= localBaseCalendar1.getFixedDate(localDate1.getNormalizedYear(), localDate1.getMonth(), localDate1.getDayOfMonth(), localDate1));
        j = (int)(l3 - l1);
      }
      break;
    case 3: 
      if (!localGregorianCalendar.isCutoverYear(i))
      {
        CalendarDate localCalendarDate1 = localBaseCalendar1.newCalendarDate(TimeZone.NO_TIMEZONE);
        localCalendarDate1.setDate(localDate1.getYear(), 1, 1);
        n = localBaseCalendar1.getDayOfWeek(localCalendarDate1);
        n -= getFirstDayOfWeek();
        if (n < 0) {
          n += 7;
        }
        j = 52;
        i2 = n + getMinimalDaysInFirstWeek() - 1;
        if ((i2 == 6) || ((localDate1.isLeapYear()) && ((i2 == 5) || (i2 == 12)))) {
          j++;
        }
      }
      else
      {
        if (localGregorianCalendar == this) {
          localGregorianCalendar = (GregorianCalendar)localGregorianCalendar.clone();
        }
        int k = getActualMaximum(6);
        localGregorianCalendar.set(6, k);
        j = localGregorianCalendar.get(3);
        if (internalGet(1) != localGregorianCalendar.getWeekYear())
        {
          localGregorianCalendar.set(6, k - 7);
          j = localGregorianCalendar.get(3);
        }
      }
      break;
    case 4: 
      if (!localGregorianCalendar.isCutoverYear(i))
      {
        CalendarDate localCalendarDate2 = localBaseCalendar1.newCalendarDate(null);
        localCalendarDate2.setDate(localDate1.getYear(), localDate1.getMonth(), 1);
        n = localBaseCalendar1.getDayOfWeek(localCalendarDate2);
        i2 = localBaseCalendar1.getMonthLength(localCalendarDate2);
        n -= getFirstDayOfWeek();
        if (n < 0) {
          n += 7;
        }
        int i3 = 7 - n;
        j = 3;
        if (i3 >= getMinimalDaysInFirstWeek()) {
          j++;
        }
        i2 -= i3 + 21;
        if (i2 > 0)
        {
          j++;
          if (i2 > 7) {
            j++;
          }
        }
      }
      else
      {
        if (localGregorianCalendar == this) {
          localGregorianCalendar = (GregorianCalendar)localGregorianCalendar.clone();
        }
        m = localGregorianCalendar.internalGet(1);
        n = localGregorianCalendar.internalGet(2);
        do
        {
          j = localGregorianCalendar.get(4);
          localGregorianCalendar.add(4, 1);
        } while ((localGregorianCalendar.get(1) == m) && (localGregorianCalendar.get(2) == n));
      }
      break;
    case 8: 
      i2 = localDate1.getDayOfWeek();
      if (!localGregorianCalendar.isCutoverYear(i))
      {
        BaseCalendar.Date localDate3 = (BaseCalendar.Date)localDate1.clone();
        m = localBaseCalendar1.getMonthLength(localDate3);
        localDate3.setDayOfMonth(1);
        localBaseCalendar1.normalize(localDate3);
        n = localDate3.getDayOfWeek();
      }
      else
      {
        if (localGregorianCalendar == this) {
          localGregorianCalendar = (GregorianCalendar)clone();
        }
        m = localGregorianCalendar.actualMonthLength();
        localGregorianCalendar.set(5, localGregorianCalendar.getActualMinimum(5));
        n = localGregorianCalendar.get(7);
      }
      int i4 = i2 - n;
      if (i4 < 0) {
        i4 += 7;
      }
      m -= i4;
      j = (m + 6) / 7;
      break;
    case 1: 
      if (localGregorianCalendar == this) {
        localGregorianCalendar = (GregorianCalendar)clone();
      }
      long l2 = localGregorianCalendar.getYearOffsetInMillis();
      if (localGregorianCalendar.internalGetEra() == 1)
      {
        localGregorianCalendar.setTimeInMillis(Long.MAX_VALUE);
        j = localGregorianCalendar.get(1);
        long l4 = localGregorianCalendar.getYearOffsetInMillis();
        if (l2 > l4) {
          j--;
        }
      }
      else
      {
        BaseCalendar localBaseCalendar3 = localGregorianCalendar.getTimeInMillis() >= gregorianCutover ? gcal : getJulianCalendarSystem();
        CalendarDate localCalendarDate3 = localBaseCalendar3.getCalendarDate(Long.MIN_VALUE, getZone());
        long l6 = (localBaseCalendar1.getDayOfYear(localCalendarDate3) - 1L) * 24L + localCalendarDate3.getHours();
        l6 *= 60L;
        l6 += localCalendarDate3.getMinutes();
        l6 *= 60L;
        l6 += localCalendarDate3.getSeconds();
        l6 *= 1000L;
        l6 += localCalendarDate3.getMillis();
        j = localCalendarDate3.getYear();
        if (j <= 0)
        {
          assert (localBaseCalendar3 == gcal);
          j = 1 - j;
        }
        if (l2 < l6) {
          j--;
        }
      }
      break;
    case 7: 
    default: 
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    return j;
  }
  
  private long getYearOffsetInMillis()
  {
    long l = (internalGet(6) - 1) * 24;
    l += internalGet(11);
    l *= 60L;
    l += internalGet(12);
    l *= 60L;
    l += internalGet(13);
    l *= 1000L;
    return l + internalGet(14) - (internalGet(15) + internalGet(16));
  }
  
  public Object clone()
  {
    GregorianCalendar localGregorianCalendar = (GregorianCalendar)super.clone();
    gdate = ((BaseCalendar.Date)gdate.clone());
    if (cdate != null) {
      if (cdate != gdate) {
        cdate = ((BaseCalendar.Date)cdate.clone());
      } else {
        cdate = gdate;
      }
    }
    originalFields = null;
    zoneOffsets = null;
    return localGregorianCalendar;
  }
  
  public TimeZone getTimeZone()
  {
    TimeZone localTimeZone = super.getTimeZone();
    gdate.setZone(localTimeZone);
    if ((cdate != null) && (cdate != gdate)) {
      cdate.setZone(localTimeZone);
    }
    return localTimeZone;
  }
  
  public void setTimeZone(TimeZone paramTimeZone)
  {
    super.setTimeZone(paramTimeZone);
    gdate.setZone(paramTimeZone);
    if ((cdate != null) && (cdate != gdate)) {
      cdate.setZone(paramTimeZone);
    }
  }
  
  public final boolean isWeekDateSupported()
  {
    return true;
  }
  
  public int getWeekYear()
  {
    int i = get(1);
    if (internalGetEra() == 0) {
      i = 1 - i;
    }
    if (i > gregorianCutoverYear + 1)
    {
      j = internalGet(3);
      if (internalGet(2) == 0)
      {
        if (j >= 52) {
          i--;
        }
      }
      else if (j == 1) {
        i++;
      }
      return i;
    }
    int j = internalGet(6);
    int k = getActualMaximum(6);
    int m = getMinimalDaysInFirstWeek();
    if ((j > m) && (j < k - 6)) {
      return i;
    }
    GregorianCalendar localGregorianCalendar = (GregorianCalendar)clone();
    localGregorianCalendar.setLenient(true);
    localGregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    localGregorianCalendar.set(6, 1);
    localGregorianCalendar.complete();
    int n = getFirstDayOfWeek() - localGregorianCalendar.get(7);
    if (n != 0)
    {
      if (n < 0) {
        n += 7;
      }
      localGregorianCalendar.add(6, n);
    }
    int i1 = localGregorianCalendar.get(6);
    if (j < i1)
    {
      if (i1 <= m) {
        i--;
      }
    }
    else
    {
      localGregorianCalendar.set(1, i + 1);
      localGregorianCalendar.set(6, 1);
      localGregorianCalendar.complete();
      int i2 = getFirstDayOfWeek() - localGregorianCalendar.get(7);
      if (i2 != 0)
      {
        if (i2 < 0) {
          i2 += 7;
        }
        localGregorianCalendar.add(6, i2);
      }
      i1 = localGregorianCalendar.get(6) - 1;
      if (i1 == 0) {
        i1 = 7;
      }
      if (i1 >= m)
      {
        int i3 = k - j + 1;
        if (i3 <= 7 - i1) {
          i++;
        }
      }
    }
    return i;
  }
  
  public void setWeekDate(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt3 < 1) || (paramInt3 > 7)) {
      throw new IllegalArgumentException("invalid dayOfWeek: " + paramInt3);
    }
    GregorianCalendar localGregorianCalendar = (GregorianCalendar)clone();
    localGregorianCalendar.setLenient(true);
    int i = localGregorianCalendar.get(0);
    localGregorianCalendar.clear();
    localGregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    localGregorianCalendar.set(0, i);
    localGregorianCalendar.set(1, paramInt1);
    localGregorianCalendar.set(3, 1);
    localGregorianCalendar.set(7, getFirstDayOfWeek());
    int j = paramInt3 - getFirstDayOfWeek();
    if (j < 0) {
      j += 7;
    }
    j += 7 * (paramInt2 - 1);
    if (j != 0) {
      localGregorianCalendar.add(6, j);
    } else {
      localGregorianCalendar.complete();
    }
    if ((!isLenient()) && ((localGregorianCalendar.getWeekYear() != paramInt1) || (localGregorianCalendar.internalGet(3) != paramInt2) || (localGregorianCalendar.internalGet(7) != paramInt3))) {
      throw new IllegalArgumentException();
    }
    set(0, localGregorianCalendar.internalGet(0));
    set(1, localGregorianCalendar.internalGet(1));
    set(2, localGregorianCalendar.internalGet(2));
    set(5, localGregorianCalendar.internalGet(5));
    internalSet(3, paramInt2);
    complete();
  }
  
  public int getWeeksInWeekYear()
  {
    GregorianCalendar localGregorianCalendar = getNormalizedCalendar();
    int i = localGregorianCalendar.getWeekYear();
    if (i == localGregorianCalendar.internalGet(1)) {
      return localGregorianCalendar.getActualMaximum(3);
    }
    if (localGregorianCalendar == this) {
      localGregorianCalendar = (GregorianCalendar)localGregorianCalendar.clone();
    }
    localGregorianCalendar.setWeekDate(i, 2, internalGet(7));
    return localGregorianCalendar.getActualMaximum(3);
  }
  
  protected void computeFields()
  {
    int i;
    if (isPartiallyNormalized())
    {
      i = getSetStateFields();
      int j = (i ^ 0xFFFFFFFF) & 0x1FFFF;
      if ((j != 0) || (calsys == null))
      {
        i |= computeFields(j, i & 0x18000);
        assert (i == 131071);
      }
    }
    else
    {
      i = 131071;
      computeFields(i, 0);
    }
    setFieldsComputed(i);
  }
  
  private int computeFields(int paramInt1, int paramInt2)
  {
    int i = 0;
    TimeZone localTimeZone = getZone();
    if (zoneOffsets == null) {
      zoneOffsets = new int[2];
    }
    if (paramInt2 != 98304) {
      if ((localTimeZone instanceof ZoneInfo))
      {
        i = ((ZoneInfo)localTimeZone).getOffsets(time, zoneOffsets);
      }
      else
      {
        i = localTimeZone.getOffset(time);
        zoneOffsets[0] = localTimeZone.getRawOffset();
        zoneOffsets[1] = (i - zoneOffsets[0]);
      }
    }
    if (paramInt2 != 0)
    {
      if (isFieldSet(paramInt2, 15)) {
        zoneOffsets[0] = internalGet(15);
      }
      if (isFieldSet(paramInt2, 16)) {
        zoneOffsets[1] = internalGet(16);
      }
      i = zoneOffsets[0] + zoneOffsets[1];
    }
    long l1 = i / 86400000L;
    int j = i % 86400000;
    l1 += time / 86400000L;
    j += (int)(time % 86400000L);
    if (j >= 86400000L)
    {
      j = (int)(j - 86400000L);
      l1 += 1L;
    }
    else
    {
      while (j < 0)
      {
        j = (int)(j + 86400000L);
        l1 -= 1L;
      }
    }
    l1 += 719163L;
    int k = 1;
    int m;
    if (l1 >= gregorianCutoverDate)
    {
      assert ((cachedFixedDate == Long.MIN_VALUE) || (gdate.isNormalized())) : "cache control: not normalized";
      assert ((cachedFixedDate == Long.MIN_VALUE) || (gcal.getFixedDate(gdate.getNormalizedYear(), gdate.getMonth(), gdate.getDayOfMonth(), gdate) == cachedFixedDate)) : ("cache control: inconsictency, cachedFixedDate=" + cachedFixedDate + ", computed=" + gcal.getFixedDate(gdate.getNormalizedYear(), gdate.getMonth(), gdate.getDayOfMonth(), gdate) + ", date=" + gdate);
      if (l1 != cachedFixedDate)
      {
        gcal.getCalendarDateFromFixedDate(gdate, l1);
        cachedFixedDate = l1;
      }
      m = gdate.getYear();
      if (m <= 0)
      {
        m = 1 - m;
        k = 0;
      }
      calsys = gcal;
      cdate = gdate;
      if ((!$assertionsDisabled) && (cdate.getDayOfWeek() <= 0)) {
        throw new AssertionError("dow=" + cdate.getDayOfWeek() + ", date=" + cdate);
      }
    }
    else
    {
      calsys = getJulianCalendarSystem();
      cdate = jcal.newCalendarDate(getZone());
      jcal.getCalendarDateFromFixedDate(cdate, l1);
      Era localEra = cdate.getEra();
      if (localEra == jeras[0]) {
        k = 0;
      }
      m = cdate.getYear();
    }
    internalSet(0, k);
    internalSet(1, m);
    int n = paramInt1 | 0x3;
    int i1 = cdate.getMonth() - 1;
    int i2 = cdate.getDayOfMonth();
    if ((paramInt1 & 0xA4) != 0)
    {
      internalSet(2, i1);
      internalSet(5, i2);
      internalSet(7, cdate.getDayOfWeek());
      n |= 0xA4;
    }
    int i3;
    if ((paramInt1 & 0x7E00) != 0)
    {
      if (j != 0)
      {
        i3 = j / 3600000;
        internalSet(11, i3);
        internalSet(9, i3 / 12);
        internalSet(10, i3 % 12);
        int i4 = j % 3600000;
        internalSet(12, i4 / 60000);
        i4 %= 60000;
        internalSet(13, i4 / 1000);
        internalSet(14, i4 % 1000);
      }
      else
      {
        internalSet(11, 0);
        internalSet(9, 0);
        internalSet(10, 0);
        internalSet(12, 0);
        internalSet(13, 0);
        internalSet(14, 0);
      }
      n |= 0x7E00;
    }
    if ((paramInt1 & 0x18000) != 0)
    {
      internalSet(15, zoneOffsets[0]);
      internalSet(16, zoneOffsets[1]);
      n |= 0x18000;
    }
    if ((paramInt1 & 0x158) != 0)
    {
      i3 = cdate.getNormalizedYear();
      long l2 = calsys.getFixedDate(i3, 1, 1, cdate);
      int i5 = (int)(l1 - l2) + 1;
      long l3 = l1 - i2 + 1L;
      int i6 = 0;
      int i7 = calsys == gcal ? gregorianCutoverYear : gregorianCutoverYearJulian;
      int i8 = i2 - 1;
      if (i3 == i7)
      {
        if (gregorianCutoverYearJulian <= gregorianCutoverYear)
        {
          l2 = getFixedDateJan1(cdate, l1);
          if (l1 >= gregorianCutoverDate) {
            l3 = getFixedDateMonth1(cdate, l1);
          }
        }
        i9 = (int)(l1 - l2) + 1;
        i6 = i5 - i9;
        i5 = i9;
        i8 = (int)(l1 - l3);
      }
      internalSet(6, i5);
      internalSet(8, i8 / 7 + 1);
      int i9 = getWeekNumber(l2, l1);
      long l4;
      long l5;
      if (i9 == 0)
      {
        l4 = l2 - 1L;
        l5 = l2 - 365L;
        if (i3 > i7 + 1)
        {
          if (CalendarUtils.isGregorianLeapYear(i3 - 1)) {
            l5 -= 1L;
          }
        }
        else if (i3 <= gregorianCutoverYearJulian)
        {
          if (CalendarUtils.isJulianLeapYear(i3 - 1)) {
            l5 -= 1L;
          }
        }
        else
        {
          Object localObject2 = calsys;
          int i12 = getCalendarDate(l4).getNormalizedYear();
          if (i12 == gregorianCutoverYear)
          {
            localObject2 = getCutoverCalendarSystem();
            if (localObject2 == jcal)
            {
              l5 = ((BaseCalendar)localObject2).getFixedDate(i12, 1, 1, null);
            }
            else
            {
              l5 = gregorianCutoverDate;
              localObject2 = gcal;
            }
          }
          else if (i12 <= gregorianCutoverYearJulian)
          {
            localObject2 = getJulianCalendarSystem();
            l5 = ((BaseCalendar)localObject2).getFixedDate(i12, 1, 1, null);
          }
        }
        i9 = getWeekNumber(l5, l4);
      }
      else if ((i3 > gregorianCutoverYear) || (i3 < gregorianCutoverYearJulian - 1))
      {
        if (i9 >= 52)
        {
          l4 = l2 + 365L;
          if (cdate.isLeapYear()) {
            l4 += 1L;
          }
          l5 = BaseCalendar.getDayOfWeekDateOnOrBefore(l4 + 6L, getFirstDayOfWeek());
          int i11 = (int)(l5 - l4);
          if ((i11 >= getMinimalDaysInFirstWeek()) && (l1 >= l5 - 7L)) {
            i9 = 1;
          }
        }
      }
      else
      {
        Object localObject1 = calsys;
        int i10 = i3 + 1;
        if ((i10 == gregorianCutoverYearJulian + 1) && (i10 < gregorianCutoverYear)) {
          i10 = gregorianCutoverYear;
        }
        if (i10 == gregorianCutoverYear) {
          localObject1 = getCutoverCalendarSystem();
        }
        if ((i10 > gregorianCutoverYear) || (gregorianCutoverYearJulian == gregorianCutoverYear) || (i10 == gregorianCutoverYearJulian))
        {
          l5 = ((BaseCalendar)localObject1).getFixedDate(i10, 1, 1, null);
        }
        else
        {
          l5 = gregorianCutoverDate;
          localObject1 = gcal;
        }
        long l6 = BaseCalendar.getDayOfWeekDateOnOrBefore(l5 + 6L, getFirstDayOfWeek());
        int i13 = (int)(l6 - l5);
        if ((i13 >= getMinimalDaysInFirstWeek()) && (l1 >= l6 - 7L)) {
          i9 = 1;
        }
      }
      internalSet(3, i9);
      internalSet(4, getWeekNumber(l3, l1));
      n |= 0x158;
    }
    return n;
  }
  
  private int getWeekNumber(long paramLong1, long paramLong2)
  {
    long l = Gregorian.getDayOfWeekDateOnOrBefore(paramLong1 + 6L, getFirstDayOfWeek());
    int i = (int)(l - paramLong1);
    assert (i <= 7);
    if (i >= getMinimalDaysInFirstWeek()) {
      l -= 7L;
    }
    int j = (int)(paramLong2 - l);
    if (j >= 0) {
      return j / 7 + 1;
    }
    return CalendarUtils.floorDivide(j, 7) + 1;
  }
  
  protected void computeTime()
  {
    if (!isLenient())
    {
      if (originalFields == null) {
        originalFields = new int[17];
      }
      for (i = 0; i < 17; i++)
      {
        j = internalGet(i);
        if ((isExternallySet(i)) && ((j < getMinimum(i)) || (j > getMaximum(i)))) {
          throw new IllegalArgumentException(getFieldName(i));
        }
        originalFields[i] = j;
      }
    }
    int i = selectFields();
    int j = isSet(1) ? internalGet(1) : 1970;
    int k = internalGetEra();
    if (k == 0) {
      j = 1 - j;
    } else if (k != 1) {
      throw new IllegalArgumentException("Invalid era");
    }
    if ((j <= 0) && (!isSet(0)))
    {
      i |= 0x1;
      setFieldsComputed(1);
    }
    long l1 = 0L;
    if (isFieldSet(i, 11))
    {
      l1 += internalGet(11);
    }
    else
    {
      l1 += internalGet(10);
      if (isFieldSet(i, 9)) {
        l1 += 12 * internalGet(9);
      }
    }
    l1 *= 60L;
    l1 += internalGet(12);
    l1 *= 60L;
    l1 += internalGet(13);
    l1 *= 1000L;
    l1 += internalGet(14);
    long l2 = l1 / 86400000L;
    l1 %= 86400000L;
    while (l1 < 0L)
    {
      l1 += 86400000L;
      l2 -= 1L;
    }
    long l4;
    if ((j > gregorianCutoverYear) && (j > gregorianCutoverYearJulian))
    {
      l3 = l2 + getFixedDate(gcal, j, i);
      if (l3 >= gregorianCutoverDate)
      {
        l2 = l3;
        break label619;
      }
      l4 = l2 + getFixedDate(getJulianCalendarSystem(), j, i);
    }
    else if ((j < gregorianCutoverYear) && (j < gregorianCutoverYearJulian))
    {
      l4 = l2 + getFixedDate(getJulianCalendarSystem(), j, i);
      if (l4 < gregorianCutoverDate)
      {
        l2 = l4;
        break label619;
      }
      l3 = l4;
    }
    else
    {
      l4 = l2 + getFixedDate(getJulianCalendarSystem(), j, i);
      l3 = l2 + getFixedDate(gcal, j, i);
    }
    if ((isFieldSet(i, 6)) || (isFieldSet(i, 3)))
    {
      if (gregorianCutoverYear == gregorianCutoverYearJulian)
      {
        l2 = l4;
        break label619;
      }
      if (j == gregorianCutoverYear)
      {
        l2 = l3;
        break label619;
      }
    }
    if (l3 >= gregorianCutoverDate)
    {
      if (l4 >= gregorianCutoverDate) {
        l2 = l3;
      } else if ((calsys == gcal) || (calsys == null)) {
        l2 = l3;
      } else {
        l2 = l4;
      }
    }
    else if (l4 < gregorianCutoverDate)
    {
      l2 = l4;
    }
    else
    {
      if (!isLenient()) {
        throw new IllegalArgumentException("the specified date doesn't exist");
      }
      l2 = l4;
    }
    label619:
    long l3 = (l2 - 719163L) * 86400000L + l1;
    TimeZone localTimeZone = getZone();
    if (zoneOffsets == null) {
      zoneOffsets = new int[2];
    }
    int m = i & 0x18000;
    if (m != 98304) {
      if ((localTimeZone instanceof ZoneInfo))
      {
        ((ZoneInfo)localTimeZone).getOffsetsByWall(l3, zoneOffsets);
      }
      else
      {
        n = isFieldSet(i, 15) ? internalGet(15) : localTimeZone.getRawOffset();
        localTimeZone.getOffsets(l3 - n, zoneOffsets);
      }
    }
    if (m != 0)
    {
      if (isFieldSet(m, 15)) {
        zoneOffsets[0] = internalGet(15);
      }
      if (isFieldSet(m, 16)) {
        zoneOffsets[1] = internalGet(16);
      }
    }
    l3 -= zoneOffsets[0] + zoneOffsets[1];
    time = l3;
    int n = computeFields(i | getSetStateFields(), m);
    if (!isLenient()) {
      for (int i1 = 0; i1 < 17; i1++) {
        if ((isExternallySet(i1)) && (originalFields[i1] != internalGet(i1)))
        {
          String str = originalFields[i1] + " -> " + internalGet(i1);
          System.arraycopy(originalFields, 0, fields, 0, fields.length);
          throw new IllegalArgumentException(getFieldName(i1) + ": " + str);
        }
      }
    }
    setFieldsNormalized(n);
  }
  
  private long getFixedDate(BaseCalendar paramBaseCalendar, int paramInt1, int paramInt2)
  {
    int i = 0;
    if (isFieldSet(paramInt2, 2))
    {
      i = internalGet(2);
      if (i > 11)
      {
        paramInt1 += i / 12;
        i %= 12;
      }
      else if (i < 0)
      {
        int[] arrayOfInt = new int[1];
        paramInt1 += CalendarUtils.floorDivide(i, 12, arrayOfInt);
        i = arrayOfInt[0];
      }
    }
    long l1 = paramBaseCalendar.getFixedDate(paramInt1, i + 1, 1, paramBaseCalendar == gcal ? gdate : null);
    int m;
    if (isFieldSet(paramInt2, 2))
    {
      if (isFieldSet(paramInt2, 5))
      {
        if (isSet(5))
        {
          l1 += internalGet(5);
          l1 -= 1L;
        }
      }
      else if (isFieldSet(paramInt2, 4))
      {
        long l2 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, getFirstDayOfWeek());
        if (l2 - l1 >= getMinimalDaysInFirstWeek()) {
          l2 -= 7L;
        }
        if (isFieldSet(paramInt2, 7)) {
          l2 = BaseCalendar.getDayOfWeekDateOnOrBefore(l2 + 6L, internalGet(7));
        }
        l1 = l2 + 7 * (internalGet(4) - 1);
      }
      else
      {
        int j;
        if (isFieldSet(paramInt2, 7)) {
          j = internalGet(7);
        } else {
          j = getFirstDayOfWeek();
        }
        int k;
        if (isFieldSet(paramInt2, 8)) {
          k = internalGet(8);
        } else {
          k = 1;
        }
        if (k >= 0)
        {
          l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1 + 7 * k - 1L, j);
        }
        else
        {
          m = monthLength(i, paramInt1) + 7 * (k + 1);
          l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1 + m - 1L, j);
        }
      }
    }
    else
    {
      if ((paramInt1 == gregorianCutoverYear) && (paramBaseCalendar == gcal) && (l1 < gregorianCutoverDate) && (gregorianCutoverYear != gregorianCutoverYearJulian)) {
        l1 = gregorianCutoverDate;
      }
      if (isFieldSet(paramInt2, 6))
      {
        l1 += internalGet(6);
        l1 -= 1L;
      }
      else
      {
        long l3 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, getFirstDayOfWeek());
        if (l3 - l1 >= getMinimalDaysInFirstWeek()) {
          l3 -= 7L;
        }
        if (isFieldSet(paramInt2, 7))
        {
          m = internalGet(7);
          if (m != getFirstDayOfWeek()) {
            l3 = BaseCalendar.getDayOfWeekDateOnOrBefore(l3 + 6L, m);
          }
        }
        l1 = l3 + 7L * (internalGet(3) - 1L);
      }
    }
    return l1;
  }
  
  private GregorianCalendar getNormalizedCalendar()
  {
    GregorianCalendar localGregorianCalendar;
    if (isFullyNormalized())
    {
      localGregorianCalendar = this;
    }
    else
    {
      localGregorianCalendar = (GregorianCalendar)clone();
      localGregorianCalendar.setLenient(true);
      localGregorianCalendar.complete();
    }
    return localGregorianCalendar;
  }
  
  private static synchronized BaseCalendar getJulianCalendarSystem()
  {
    if (jcal == null)
    {
      jcal = (JulianCalendar)CalendarSystem.forName("julian");
      jeras = jcal.getEras();
    }
    return jcal;
  }
  
  private BaseCalendar getCutoverCalendarSystem()
  {
    if (gregorianCutoverYearJulian < gregorianCutoverYear) {
      return gcal;
    }
    return getJulianCalendarSystem();
  }
  
  private boolean isCutoverYear(int paramInt)
  {
    int i = calsys == gcal ? gregorianCutoverYear : gregorianCutoverYearJulian;
    return paramInt == i;
  }
  
  private long getFixedDateJan1(BaseCalendar.Date paramDate, long paramLong)
  {
    assert ((paramDate.getNormalizedYear() == gregorianCutoverYear) || (paramDate.getNormalizedYear() == gregorianCutoverYearJulian));
    if ((gregorianCutoverYear != gregorianCutoverYearJulian) && (paramLong >= gregorianCutoverDate)) {
      return gregorianCutoverDate;
    }
    BaseCalendar localBaseCalendar = getJulianCalendarSystem();
    return localBaseCalendar.getFixedDate(paramDate.getNormalizedYear(), 1, 1, null);
  }
  
  private long getFixedDateMonth1(BaseCalendar.Date paramDate, long paramLong)
  {
    assert ((paramDate.getNormalizedYear() == gregorianCutoverYear) || (paramDate.getNormalizedYear() == gregorianCutoverYearJulian));
    BaseCalendar.Date localDate1 = getGregorianCutoverDate();
    if ((localDate1.getMonth() == 1) && (localDate1.getDayOfMonth() == 1)) {
      return paramLong - paramDate.getDayOfMonth() + 1L;
    }
    long l;
    if (paramDate.getMonth() == localDate1.getMonth())
    {
      BaseCalendar.Date localDate2 = getLastJulianDate();
      if ((gregorianCutoverYear == gregorianCutoverYearJulian) && (localDate1.getMonth() == localDate2.getMonth())) {
        l = jcal.getFixedDate(paramDate.getNormalizedYear(), paramDate.getMonth(), 1, null);
      } else {
        l = gregorianCutoverDate;
      }
    }
    else
    {
      l = paramLong - paramDate.getDayOfMonth() + 1L;
    }
    return l;
  }
  
  private BaseCalendar.Date getCalendarDate(long paramLong)
  {
    BaseCalendar localBaseCalendar = paramLong >= gregorianCutoverDate ? gcal : getJulianCalendarSystem();
    BaseCalendar.Date localDate = (BaseCalendar.Date)localBaseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
    localBaseCalendar.getCalendarDateFromFixedDate(localDate, paramLong);
    return localDate;
  }
  
  private BaseCalendar.Date getGregorianCutoverDate()
  {
    return getCalendarDate(gregorianCutoverDate);
  }
  
  private BaseCalendar.Date getLastJulianDate()
  {
    return getCalendarDate(gregorianCutoverDate - 1L);
  }
  
  private int monthLength(int paramInt1, int paramInt2)
  {
    return isLeapYear(paramInt2) ? LEAP_MONTH_LENGTH[paramInt1] : MONTH_LENGTH[paramInt1];
  }
  
  private int monthLength(int paramInt)
  {
    int i = internalGet(1);
    if (internalGetEra() == 0) {
      i = 1 - i;
    }
    return monthLength(paramInt, i);
  }
  
  private int actualMonthLength()
  {
    int i = cdate.getNormalizedYear();
    if ((i != gregorianCutoverYear) && (i != gregorianCutoverYearJulian)) {
      return calsys.getMonthLength(cdate);
    }
    Object localObject = (BaseCalendar.Date)cdate.clone();
    long l1 = calsys.getFixedDate((CalendarDate)localObject);
    long l2 = getFixedDateMonth1((BaseCalendar.Date)localObject, l1);
    long l3 = l2 + calsys.getMonthLength((CalendarDate)localObject);
    if (l3 < gregorianCutoverDate) {
      return (int)(l3 - l2);
    }
    if (cdate != gdate) {
      localObject = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    }
    gcal.getCalendarDateFromFixedDate((CalendarDate)localObject, l3);
    l3 = getFixedDateMonth1((BaseCalendar.Date)localObject, l3);
    return (int)(l3 - l2);
  }
  
  private int yearLength(int paramInt)
  {
    return isLeapYear(paramInt) ? 366 : 365;
  }
  
  private int yearLength()
  {
    int i = internalGet(1);
    if (internalGetEra() == 0) {
      i = 1 - i;
    }
    return yearLength(i);
  }
  
  private void pinDayOfMonth()
  {
    int i = internalGet(1);
    int j;
    if ((i > gregorianCutoverYear) || (i < gregorianCutoverYearJulian))
    {
      j = monthLength(internalGet(2));
    }
    else
    {
      GregorianCalendar localGregorianCalendar = getNormalizedCalendar();
      j = localGregorianCalendar.getActualMaximum(5);
    }
    int k = internalGet(5);
    if (k > j) {
      set(5, j);
    }
  }
  
  private long getCurrentFixedDate()
  {
    return calsys == gcal ? cachedFixedDate : calsys.getFixedDate(cdate);
  }
  
  private static int getRolledValue(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    assert ((paramInt1 >= paramInt3) && (paramInt1 <= paramInt4));
    int i = paramInt4 - paramInt3 + 1;
    paramInt2 %= i;
    int j = paramInt1 + paramInt2;
    if (j > paramInt4) {
      j -= i;
    } else if (j < paramInt3) {
      j += i;
    }
    assert ((j >= paramInt3) && (j <= paramInt4));
    return j;
  }
  
  private int internalGetEra()
  {
    return isSet(0) ? internalGet(0) : 1;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (gdate == null)
    {
      gdate = gcal.newCalendarDate(getZone());
      cachedFixedDate = Long.MIN_VALUE;
    }
    setGregorianChange(gregorianCutover);
  }
  
  public ZonedDateTime toZonedDateTime()
  {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getTimeInMillis()), getTimeZone().toZoneId());
  }
  
  public static GregorianCalendar from(ZonedDateTime paramZonedDateTime)
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone(paramZonedDateTime.getZone()));
    localGregorianCalendar.setGregorianChange(new Date(Long.MIN_VALUE));
    localGregorianCalendar.setFirstDayOfWeek(2);
    localGregorianCalendar.setMinimalDaysInFirstWeek(4);
    try
    {
      localGregorianCalendar.setTimeInMillis(Math.addExact(Math.multiplyExact(paramZonedDateTime.toEpochSecond(), 1000L), paramZonedDateTime.get(ChronoField.MILLI_OF_SECOND)));
    }
    catch (ArithmeticException localArithmeticException)
    {
      throw new IllegalArgumentException(localArithmeticException);
    }
    return localGregorianCalendar;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\GregorianCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */