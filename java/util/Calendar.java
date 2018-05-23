package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.BuddhistCalendar;
import sun.util.calendar.ZoneInfo;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.spi.CalendarProvider;

public abstract class Calendar
  implements Serializable, Cloneable, Comparable<Calendar>
{
  public static final int ERA = 0;
  public static final int YEAR = 1;
  public static final int MONTH = 2;
  public static final int WEEK_OF_YEAR = 3;
  public static final int WEEK_OF_MONTH = 4;
  public static final int DATE = 5;
  public static final int DAY_OF_MONTH = 5;
  public static final int DAY_OF_YEAR = 6;
  public static final int DAY_OF_WEEK = 7;
  public static final int DAY_OF_WEEK_IN_MONTH = 8;
  public static final int AM_PM = 9;
  public static final int HOUR = 10;
  public static final int HOUR_OF_DAY = 11;
  public static final int MINUTE = 12;
  public static final int SECOND = 13;
  public static final int MILLISECOND = 14;
  public static final int ZONE_OFFSET = 15;
  public static final int DST_OFFSET = 16;
  public static final int FIELD_COUNT = 17;
  public static final int SUNDAY = 1;
  public static final int MONDAY = 2;
  public static final int TUESDAY = 3;
  public static final int WEDNESDAY = 4;
  public static final int THURSDAY = 5;
  public static final int FRIDAY = 6;
  public static final int SATURDAY = 7;
  public static final int JANUARY = 0;
  public static final int FEBRUARY = 1;
  public static final int MARCH = 2;
  public static final int APRIL = 3;
  public static final int MAY = 4;
  public static final int JUNE = 5;
  public static final int JULY = 6;
  public static final int AUGUST = 7;
  public static final int SEPTEMBER = 8;
  public static final int OCTOBER = 9;
  public static final int NOVEMBER = 10;
  public static final int DECEMBER = 11;
  public static final int UNDECIMBER = 12;
  public static final int AM = 0;
  public static final int PM = 1;
  public static final int ALL_STYLES = 0;
  static final int STANDALONE_MASK = 32768;
  public static final int SHORT = 1;
  public static final int LONG = 2;
  public static final int NARROW_FORMAT = 4;
  public static final int NARROW_STANDALONE = 32772;
  public static final int SHORT_FORMAT = 1;
  public static final int LONG_FORMAT = 2;
  public static final int SHORT_STANDALONE = 32769;
  public static final int LONG_STANDALONE = 32770;
  protected int[] fields = new int[17];
  protected boolean[] isSet = new boolean[17];
  private transient int[] stamp = new int[17];
  protected long time;
  protected boolean isTimeSet;
  protected boolean areFieldsSet;
  transient boolean areAllFieldsSet;
  private boolean lenient = true;
  private TimeZone zone;
  private transient boolean sharedZone = false;
  private int firstDayOfWeek;
  private int minimalDaysInFirstWeek;
  private static final ConcurrentMap<Locale, int[]> cachedLocaleData = new ConcurrentHashMap(3);
  private static final int UNSET = 0;
  private static final int COMPUTED = 1;
  private static final int MINIMUM_USER_STAMP = 2;
  static final int ALL_FIELDS = 131071;
  private int nextStamp = 2;
  static final int currentSerialVersion = 1;
  private int serialVersionOnStream = 1;
  static final long serialVersionUID = -1807547505821590642L;
  static final int ERA_MASK = 1;
  static final int YEAR_MASK = 2;
  static final int MONTH_MASK = 4;
  static final int WEEK_OF_YEAR_MASK = 8;
  static final int WEEK_OF_MONTH_MASK = 16;
  static final int DAY_OF_MONTH_MASK = 32;
  static final int DATE_MASK = 32;
  static final int DAY_OF_YEAR_MASK = 64;
  static final int DAY_OF_WEEK_MASK = 128;
  static final int DAY_OF_WEEK_IN_MONTH_MASK = 256;
  static final int AM_PM_MASK = 512;
  static final int HOUR_MASK = 1024;
  static final int HOUR_OF_DAY_MASK = 2048;
  static final int MINUTE_MASK = 4096;
  static final int SECOND_MASK = 8192;
  static final int MILLISECOND_MASK = 16384;
  static final int ZONE_OFFSET_MASK = 32768;
  static final int DST_OFFSET_MASK = 65536;
  private static final String[] FIELD_NAME = { "ERA", "YEAR", "MONTH", "WEEK_OF_YEAR", "WEEK_OF_MONTH", "DAY_OF_MONTH", "DAY_OF_YEAR", "DAY_OF_WEEK", "DAY_OF_WEEK_IN_MONTH", "AM_PM", "HOUR", "HOUR_OF_DAY", "MINUTE", "SECOND", "MILLISECOND", "ZONE_OFFSET", "DST_OFFSET" };
  
  protected Calendar()
  {
    this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
    sharedZone = true;
  }
  
  protected Calendar(TimeZone paramTimeZone, Locale paramLocale)
  {
    zone = paramTimeZone;
    setWeekCountData(paramLocale);
  }
  
  public static Calendar getInstance()
  {
    return createCalendar(TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public static Calendar getInstance(TimeZone paramTimeZone)
  {
    return createCalendar(paramTimeZone, Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public static Calendar getInstance(Locale paramLocale)
  {
    return createCalendar(TimeZone.getDefault(), paramLocale);
  }
  
  public static Calendar getInstance(TimeZone paramTimeZone, Locale paramLocale)
  {
    return createCalendar(paramTimeZone, paramLocale);
  }
  
  private static Calendar createCalendar(TimeZone paramTimeZone, Locale paramLocale)
  {
    CalendarProvider localCalendarProvider = LocaleProviderAdapter.getAdapter(CalendarProvider.class, paramLocale).getCalendarProvider();
    if (localCalendarProvider != null) {
      try
      {
        return localCalendarProvider.getInstance(paramTimeZone, paramLocale);
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
    Object localObject = null;
    if (paramLocale.hasExtensions())
    {
      String str1 = paramLocale.getUnicodeLocaleType("ca");
      if (str1 != null) {
        switch (str1)
        {
        case "buddhist": 
          localObject = new BuddhistCalendar(paramTimeZone, paramLocale);
          break;
        case "japanese": 
          localObject = new JapaneseImperialCalendar(paramTimeZone, paramLocale);
          break;
        case "gregory": 
          localObject = new GregorianCalendar(paramTimeZone, paramLocale);
        }
      }
    }
    if (localObject == null) {
      if ((paramLocale.getLanguage() == "th") && (paramLocale.getCountry() == "TH")) {
        localObject = new BuddhistCalendar(paramTimeZone, paramLocale);
      } else if ((paramLocale.getVariant() == "JP") && (paramLocale.getLanguage() == "ja") && (paramLocale.getCountry() == "JP")) {
        localObject = new JapaneseImperialCalendar(paramTimeZone, paramLocale);
      } else {
        localObject = new GregorianCalendar(paramTimeZone, paramLocale);
      }
    }
    return (Calendar)localObject;
  }
  
  public static synchronized Locale[] getAvailableLocales()
  {
    return DateFormat.getAvailableLocales();
  }
  
  protected abstract void computeTime();
  
  protected abstract void computeFields();
  
  public final Date getTime()
  {
    return new Date(getTimeInMillis());
  }
  
  public final void setTime(Date paramDate)
  {
    setTimeInMillis(paramDate.getTime());
  }
  
  public long getTimeInMillis()
  {
    if (!isTimeSet) {
      updateTime();
    }
    return time;
  }
  
  public void setTimeInMillis(long paramLong)
  {
    if ((time == paramLong) && (isTimeSet) && (areFieldsSet) && (areAllFieldsSet) && ((zone instanceof ZoneInfo)) && (!((ZoneInfo)zone).isDirty())) {
      return;
    }
    time = paramLong;
    isTimeSet = true;
    areFieldsSet = false;
    computeFields();
    areAllFieldsSet = (areFieldsSet = 1);
  }
  
  public int get(int paramInt)
  {
    complete();
    return internalGet(paramInt);
  }
  
  protected final int internalGet(int paramInt)
  {
    return fields[paramInt];
  }
  
  final void internalSet(int paramInt1, int paramInt2)
  {
    fields[paramInt1] = paramInt2;
  }
  
  public void set(int paramInt1, int paramInt2)
  {
    if ((areFieldsSet) && (!areAllFieldsSet)) {
      computeFields();
    }
    internalSet(paramInt1, paramInt2);
    isTimeSet = false;
    areFieldsSet = false;
    isSet[paramInt1] = true;
    stamp[paramInt1] = (nextStamp++);
    if (nextStamp == Integer.MAX_VALUE) {
      adjustStamp();
    }
  }
  
  public final void set(int paramInt1, int paramInt2, int paramInt3)
  {
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
  }
  
  public final void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
    set(11, paramInt4);
    set(12, paramInt5);
  }
  
  public final void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
    set(11, paramInt4);
    set(12, paramInt5);
    set(13, paramInt6);
  }
  
  public final void clear()
  {
    int i = 0;
    while (i < fields.length)
    {
      stamp[i] = (fields[i] = 0);
      isSet[(i++)] = false;
    }
    areAllFieldsSet = (areFieldsSet = 0);
    isTimeSet = false;
  }
  
  public final void clear(int paramInt)
  {
    fields[paramInt] = 0;
    stamp[paramInt] = 0;
    isSet[paramInt] = false;
    areAllFieldsSet = (areFieldsSet = 0);
    isTimeSet = false;
  }
  
  public final boolean isSet(int paramInt)
  {
    return stamp[paramInt] != 0;
  }
  
  public String getDisplayName(int paramInt1, int paramInt2, Locale paramLocale)
  {
    if (!checkDisplayNameParams(paramInt1, paramInt2, 1, 4, paramLocale, 645)) {
      return null;
    }
    String str = getCalendarType();
    int i = get(paramInt1);
    if ((isStandaloneStyle(paramInt2)) || (isNarrowFormatStyle(paramInt2)))
    {
      localObject = CalendarDataUtility.retrieveFieldValueName(str, paramInt1, i, paramInt2, paramLocale);
      if (localObject == null) {
        if (isNarrowFormatStyle(paramInt2)) {
          localObject = CalendarDataUtility.retrieveFieldValueName(str, paramInt1, i, toStandaloneStyle(paramInt2), paramLocale);
        } else if (isStandaloneStyle(paramInt2)) {
          localObject = CalendarDataUtility.retrieveFieldValueName(str, paramInt1, i, getBaseStyle(paramInt2), paramLocale);
        }
      }
      return (String)localObject;
    }
    Object localObject = DateFormatSymbols.getInstance(paramLocale);
    String[] arrayOfString = getFieldStrings(paramInt1, paramInt2, (DateFormatSymbols)localObject);
    if ((arrayOfString != null) && (i < arrayOfString.length)) {
      return arrayOfString[i];
    }
    return null;
  }
  
  public Map<String, Integer> getDisplayNames(int paramInt1, int paramInt2, Locale paramLocale)
  {
    if (!checkDisplayNameParams(paramInt1, paramInt2, 0, 4, paramLocale, 645)) {
      return null;
    }
    String str = getCalendarType();
    if ((paramInt2 == 0) || (isStandaloneStyle(paramInt2)) || (isNarrowFormatStyle(paramInt2)))
    {
      Map localMap = CalendarDataUtility.retrieveFieldValueNames(str, paramInt1, paramInt2, paramLocale);
      if (localMap == null) {
        if (isNarrowFormatStyle(paramInt2)) {
          localMap = CalendarDataUtility.retrieveFieldValueNames(str, paramInt1, toStandaloneStyle(paramInt2), paramLocale);
        } else if (paramInt2 != 0) {
          localMap = CalendarDataUtility.retrieveFieldValueNames(str, paramInt1, getBaseStyle(paramInt2), paramLocale);
        }
      }
      return localMap;
    }
    return getDisplayNamesImpl(paramInt1, paramInt2, paramLocale);
  }
  
  private Map<String, Integer> getDisplayNamesImpl(int paramInt1, int paramInt2, Locale paramLocale)
  {
    DateFormatSymbols localDateFormatSymbols = DateFormatSymbols.getInstance(paramLocale);
    String[] arrayOfString = getFieldStrings(paramInt1, paramInt2, localDateFormatSymbols);
    if (arrayOfString != null)
    {
      HashMap localHashMap = new HashMap();
      for (int i = 0; i < arrayOfString.length; i++) {
        if (arrayOfString[i].length() != 0) {
          localHashMap.put(arrayOfString[i], Integer.valueOf(i));
        }
      }
      return localHashMap;
    }
    return null;
  }
  
  boolean checkDisplayNameParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Locale paramLocale, int paramInt5)
  {
    int i = getBaseStyle(paramInt2);
    if ((paramInt1 < 0) || (paramInt1 >= fields.length) || (i < paramInt3) || (i > paramInt4)) {
      throw new IllegalArgumentException();
    }
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    return isFieldSet(paramInt5, paramInt1);
  }
  
  private String[] getFieldStrings(int paramInt1, int paramInt2, DateFormatSymbols paramDateFormatSymbols)
  {
    int i = getBaseStyle(paramInt2);
    if (i == 4) {
      return null;
    }
    String[] arrayOfString = null;
    switch (paramInt1)
    {
    case 0: 
      arrayOfString = paramDateFormatSymbols.getEras();
      break;
    case 2: 
      arrayOfString = i == 2 ? paramDateFormatSymbols.getMonths() : paramDateFormatSymbols.getShortMonths();
      break;
    case 7: 
      arrayOfString = i == 2 ? paramDateFormatSymbols.getWeekdays() : paramDateFormatSymbols.getShortWeekdays();
      break;
    case 9: 
      arrayOfString = paramDateFormatSymbols.getAmPmStrings();
    }
    return arrayOfString;
  }
  
  protected void complete()
  {
    if (!isTimeSet) {
      updateTime();
    }
    if ((!areFieldsSet) || (!areAllFieldsSet))
    {
      computeFields();
      areAllFieldsSet = (areFieldsSet = 1);
    }
  }
  
  final boolean isExternallySet(int paramInt)
  {
    return stamp[paramInt] >= 2;
  }
  
  final int getSetStateFields()
  {
    int i = 0;
    for (int j = 0; j < fields.length; j++) {
      if (stamp[j] != 0) {
        i |= 1 << j;
      }
    }
    return i;
  }
  
  final void setFieldsComputed(int paramInt)
  {
    int i;
    if (paramInt == 131071)
    {
      for (i = 0; i < fields.length; i++)
      {
        stamp[i] = 1;
        isSet[i] = true;
      }
      areFieldsSet = (areAllFieldsSet = 1);
    }
    else
    {
      for (i = 0; i < fields.length; i++)
      {
        if ((paramInt & 0x1) == 1)
        {
          stamp[i] = 1;
          isSet[i] = true;
        }
        else if ((areAllFieldsSet) && (isSet[i] == 0))
        {
          areAllFieldsSet = false;
        }
        paramInt >>>= 1;
      }
    }
  }
  
  final void setFieldsNormalized(int paramInt)
  {
    if (paramInt != 131071) {
      for (int i = 0; i < fields.length; i++)
      {
        if ((paramInt & 0x1) == 0)
        {
          stamp[i] = (fields[i] = 0);
          isSet[i] = false;
        }
        paramInt >>= 1;
      }
    }
    areFieldsSet = true;
    areAllFieldsSet = false;
  }
  
  final boolean isPartiallyNormalized()
  {
    return (areFieldsSet) && (!areAllFieldsSet);
  }
  
  final boolean isFullyNormalized()
  {
    return (areFieldsSet) && (areAllFieldsSet);
  }
  
  final void setUnnormalized()
  {
    areFieldsSet = (areAllFieldsSet = 0);
  }
  
  static boolean isFieldSet(int paramInt1, int paramInt2)
  {
    return (paramInt1 & 1 << paramInt2) != 0;
  }
  
  final int selectFields()
  {
    int i = 2;
    if (stamp[0] != 0) {
      i |= 0x1;
    }
    int j = stamp[7];
    int k = stamp[2];
    int m = stamp[5];
    int n = aggregateStamp(stamp[4], j);
    int i1 = aggregateStamp(stamp[8], j);
    int i2 = stamp[6];
    int i3 = aggregateStamp(stamp[3], j);
    int i4 = m;
    if (n > i4) {
      i4 = n;
    }
    if (i1 > i4) {
      i4 = i1;
    }
    if (i2 > i4) {
      i4 = i2;
    }
    if (i3 > i4) {
      i4 = i3;
    }
    if (i4 == 0)
    {
      n = stamp[4];
      i1 = Math.max(stamp[8], j);
      i3 = stamp[3];
      i4 = Math.max(Math.max(n, i1), i3);
      if (i4 == 0) {
        i4 = m = k;
      }
    }
    if ((i4 == m) || ((i4 == n) && (stamp[4] >= stamp[3])) || ((i4 == i1) && (stamp[8] >= stamp[3])))
    {
      i |= 0x4;
      if (i4 == m)
      {
        i |= 0x20;
      }
      else
      {
        assert ((i4 == n) || (i4 == i1));
        if (j != 0) {
          i |= 0x80;
        }
        if (n == i1)
        {
          if (stamp[4] >= stamp[8]) {
            i |= 0x10;
          } else {
            i |= 0x100;
          }
        }
        else if (i4 == n)
        {
          i |= 0x10;
        }
        else
        {
          assert (i4 == i1);
          if (stamp[8] != 0) {
            i |= 0x100;
          }
        }
      }
    }
    else
    {
      assert ((i4 == i2) || (i4 == i3) || (i4 == 0));
      if (i4 == i2)
      {
        i |= 0x40;
      }
      else
      {
        assert (i4 == i3);
        if (j != 0) {
          i |= 0x80;
        }
        i |= 0x8;
      }
    }
    int i5 = stamp[11];
    int i6 = aggregateStamp(stamp[10], stamp[9]);
    i4 = i6 > i5 ? i6 : i5;
    if (i4 == 0) {
      i4 = Math.max(stamp[10], stamp[9]);
    }
    if (i4 != 0) {
      if (i4 == i5)
      {
        i |= 0x800;
      }
      else
      {
        i |= 0x400;
        if (stamp[9] != 0) {
          i |= 0x200;
        }
      }
    }
    if (stamp[12] != 0) {
      i |= 0x1000;
    }
    if (stamp[13] != 0) {
      i |= 0x2000;
    }
    if (stamp[14] != 0) {
      i |= 0x4000;
    }
    if (stamp[15] >= 2) {
      i |= 0x8000;
    }
    if (stamp[16] >= 2) {
      i |= 0x10000;
    }
    return i;
  }
  
  int getBaseStyle(int paramInt)
  {
    return paramInt & 0xFFFF7FFF;
  }
  
  private int toStandaloneStyle(int paramInt)
  {
    return paramInt | 0x8000;
  }
  
  private boolean isStandaloneStyle(int paramInt)
  {
    return (paramInt & 0x8000) != 0;
  }
  
  private boolean isNarrowStyle(int paramInt)
  {
    return (paramInt == 4) || (paramInt == 32772);
  }
  
  private boolean isNarrowFormatStyle(int paramInt)
  {
    return paramInt == 4;
  }
  
  private static int aggregateStamp(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      return 0;
    }
    return paramInt1 > paramInt2 ? paramInt1 : paramInt2;
  }
  
  public static Set<String> getAvailableCalendarTypes()
  {
    return AvailableCalendarTypes.SET;
  }
  
  public String getCalendarType()
  {
    return getClass().getName();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    try
    {
      Calendar localCalendar = (Calendar)paramObject;
      return (compareTo(getMillisOf(localCalendar)) == 0) && (lenient == lenient) && (firstDayOfWeek == firstDayOfWeek) && (minimalDaysInFirstWeek == minimalDaysInFirstWeek) && (zone.equals(zone));
    }
    catch (Exception localException) {}
    return false;
  }
  
  public int hashCode()
  {
    int i = (lenient ? 1 : 0) | firstDayOfWeek << 1 | minimalDaysInFirstWeek << 4 | zone.hashCode() << 7;
    long l = getMillisOf(this);
    return (int)l ^ (int)(l >> 32) ^ i;
  }
  
  public boolean before(Object paramObject)
  {
    return ((paramObject instanceof Calendar)) && (compareTo((Calendar)paramObject) < 0);
  }
  
  public boolean after(Object paramObject)
  {
    return ((paramObject instanceof Calendar)) && (compareTo((Calendar)paramObject) > 0);
  }
  
  public int compareTo(Calendar paramCalendar)
  {
    return compareTo(getMillisOf(paramCalendar));
  }
  
  public abstract void add(int paramInt1, int paramInt2);
  
  public abstract void roll(int paramInt, boolean paramBoolean);
  
  public void roll(int paramInt1, int paramInt2)
  {
    while (paramInt2 > 0)
    {
      roll(paramInt1, true);
      paramInt2--;
    }
    while (paramInt2 < 0)
    {
      roll(paramInt1, false);
      paramInt2++;
    }
  }
  
  public void setTimeZone(TimeZone paramTimeZone)
  {
    zone = paramTimeZone;
    sharedZone = false;
    areAllFieldsSet = (areFieldsSet = 0);
  }
  
  public TimeZone getTimeZone()
  {
    if (sharedZone)
    {
      zone = ((TimeZone)zone.clone());
      sharedZone = false;
    }
    return zone;
  }
  
  TimeZone getZone()
  {
    return zone;
  }
  
  void setZoneShared(boolean paramBoolean)
  {
    sharedZone = paramBoolean;
  }
  
  public void setLenient(boolean paramBoolean)
  {
    lenient = paramBoolean;
  }
  
  public boolean isLenient()
  {
    return lenient;
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    if (firstDayOfWeek == paramInt) {
      return;
    }
    firstDayOfWeek = paramInt;
    invalidateWeekFields();
  }
  
  public int getFirstDayOfWeek()
  {
    return firstDayOfWeek;
  }
  
  public void setMinimalDaysInFirstWeek(int paramInt)
  {
    if (minimalDaysInFirstWeek == paramInt) {
      return;
    }
    minimalDaysInFirstWeek = paramInt;
    invalidateWeekFields();
  }
  
  public int getMinimalDaysInFirstWeek()
  {
    return minimalDaysInFirstWeek;
  }
  
  public boolean isWeekDateSupported()
  {
    return false;
  }
  
  public int getWeekYear()
  {
    throw new UnsupportedOperationException();
  }
  
  public void setWeekDate(int paramInt1, int paramInt2, int paramInt3)
  {
    throw new UnsupportedOperationException();
  }
  
  public int getWeeksInWeekYear()
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract int getMinimum(int paramInt);
  
  public abstract int getMaximum(int paramInt);
  
  public abstract int getGreatestMinimum(int paramInt);
  
  public abstract int getLeastMaximum(int paramInt);
  
  public int getActualMinimum(int paramInt)
  {
    int i = getGreatestMinimum(paramInt);
    int j = getMinimum(paramInt);
    if (i == j) {
      return i;
    }
    Calendar localCalendar = (Calendar)clone();
    localCalendar.setLenient(true);
    int k = i;
    do
    {
      localCalendar.set(paramInt, i);
      if (localCalendar.get(paramInt) != i) {
        break;
      }
      k = i;
      i--;
    } while (i >= j);
    return k;
  }
  
  public int getActualMaximum(int paramInt)
  {
    int i = getLeastMaximum(paramInt);
    int j = getMaximum(paramInt);
    if (i == j) {
      return i;
    }
    Calendar localCalendar = (Calendar)clone();
    localCalendar.setLenient(true);
    if ((paramInt == 3) || (paramInt == 4)) {
      localCalendar.set(7, firstDayOfWeek);
    }
    int k = i;
    do
    {
      localCalendar.set(paramInt, i);
      if (localCalendar.get(paramInt) != i) {
        break;
      }
      k = i;
      i++;
    } while (i <= j);
    return k;
  }
  
  public Object clone()
  {
    try
    {
      Calendar localCalendar = (Calendar)super.clone();
      fields = new int[17];
      isSet = new boolean[17];
      stamp = new int[17];
      for (int i = 0; i < 17; i++)
      {
        fields[i] = fields[i];
        stamp[i] = stamp[i];
        isSet[i] = isSet[i];
      }
      zone = ((TimeZone)zone.clone());
      return localCalendar;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  static String getFieldName(int paramInt)
  {
    return FIELD_NAME[paramInt];
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(800);
    localStringBuilder.append(getClass().getName()).append('[');
    appendValue(localStringBuilder, "time", isTimeSet, time);
    localStringBuilder.append(",areFieldsSet=").append(areFieldsSet);
    localStringBuilder.append(",areAllFieldsSet=").append(areAllFieldsSet);
    localStringBuilder.append(",lenient=").append(lenient);
    localStringBuilder.append(",zone=").append(zone);
    appendValue(localStringBuilder, ",firstDayOfWeek", true, firstDayOfWeek);
    appendValue(localStringBuilder, ",minimalDaysInFirstWeek", true, minimalDaysInFirstWeek);
    for (int i = 0; i < 17; i++)
    {
      localStringBuilder.append(',');
      appendValue(localStringBuilder, FIELD_NAME[i], isSet(i), fields[i]);
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  private static void appendValue(StringBuilder paramStringBuilder, String paramString, boolean paramBoolean, long paramLong)
  {
    paramStringBuilder.append(paramString).append('=');
    if (paramBoolean) {
      paramStringBuilder.append(paramLong);
    } else {
      paramStringBuilder.append('?');
    }
  }
  
  private void setWeekCountData(Locale paramLocale)
  {
    int[] arrayOfInt = (int[])cachedLocaleData.get(paramLocale);
    if (arrayOfInt == null)
    {
      arrayOfInt = new int[2];
      arrayOfInt[0] = CalendarDataUtility.retrieveFirstDayOfWeek(paramLocale);
      arrayOfInt[1] = CalendarDataUtility.retrieveMinimalDaysInFirstWeek(paramLocale);
      cachedLocaleData.putIfAbsent(paramLocale, arrayOfInt);
    }
    firstDayOfWeek = arrayOfInt[0];
    minimalDaysInFirstWeek = arrayOfInt[1];
  }
  
  private void updateTime()
  {
    computeTime();
    isTimeSet = true;
  }
  
  private int compareTo(long paramLong)
  {
    long l = getMillisOf(this);
    return l == paramLong ? 0 : l > paramLong ? 1 : -1;
  }
  
  private static long getMillisOf(Calendar paramCalendar)
  {
    if (isTimeSet) {
      return time;
    }
    Calendar localCalendar = (Calendar)paramCalendar.clone();
    localCalendar.setLenient(true);
    return localCalendar.getTimeInMillis();
  }
  
  private void adjustStamp()
  {
    int i = 2;
    int j = 2;
    for (;;)
    {
      int k = Integer.MAX_VALUE;
      for (int m = 0; m < stamp.length; m++)
      {
        int n = stamp[m];
        if ((n >= j) && (k > n)) {
          k = n;
        }
        if (i < n) {
          i = n;
        }
      }
      if ((i != k) && (k == Integer.MAX_VALUE)) {
        break;
      }
      for (m = 0; m < stamp.length; m++) {
        if (stamp[m] == k) {
          stamp[m] = j;
        }
      }
      j++;
      if (k == i) {
        break;
      }
    }
    nextStamp = j;
  }
  
  private void invalidateWeekFields()
  {
    if ((stamp[4] != 1) && (stamp[3] != 1)) {
      return;
    }
    Calendar localCalendar = (Calendar)clone();
    localCalendar.setLenient(true);
    localCalendar.clear(4);
    localCalendar.clear(3);
    int i;
    if (stamp[4] == 1)
    {
      i = localCalendar.get(4);
      if (fields[4] != i) {
        fields[4] = i;
      }
    }
    if (stamp[3] == 1)
    {
      i = localCalendar.get(3);
      if (fields[3] != i) {
        fields[3] = i;
      }
    }
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (!isTimeSet) {
      try
      {
        updateTime();
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
    TimeZone localTimeZone = null;
    if ((zone instanceof ZoneInfo))
    {
      SimpleTimeZone localSimpleTimeZone = ((ZoneInfo)zone).getLastRuleInstance();
      if (localSimpleTimeZone == null) {
        localSimpleTimeZone = new SimpleTimeZone(zone.getRawOffset(), zone.getID());
      }
      localTimeZone = zone;
      zone = localSimpleTimeZone;
    }
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(localTimeZone);
    if (localTimeZone != null) {
      zone = localTimeZone;
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    final ObjectInputStream localObjectInputStream = paramObjectInputStream;
    localObjectInputStream.defaultReadObject();
    stamp = new int[17];
    if (serialVersionOnStream >= 2)
    {
      isTimeSet = true;
      if (fields == null) {
        fields = new int[17];
      }
      if (isSet == null) {
        isSet = new boolean[17];
      }
    }
    else if (serialVersionOnStream >= 0)
    {
      for (int i = 0; i < 17; i++) {
        stamp[i] = (isSet[i] != 0 ? 1 : 0);
      }
    }
    serialVersionOnStream = 1;
    ZoneInfo localZoneInfo = null;
    Object localObject;
    try
    {
      localZoneInfo = (ZoneInfo)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public ZoneInfo run()
          throws Exception
        {
          return (ZoneInfo)localObjectInputStream.readObject();
        }
      }, CalendarAccessControlContext.INSTANCE);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      localObject = localPrivilegedActionException.getException();
      if (!(localObject instanceof OptionalDataException))
      {
        if ((localObject instanceof RuntimeException)) {
          throw ((RuntimeException)localObject);
        }
        if ((localObject instanceof IOException)) {
          throw ((IOException)localObject);
        }
        if ((localObject instanceof ClassNotFoundException)) {
          throw ((ClassNotFoundException)localObject);
        }
        throw new RuntimeException((Throwable)localObject);
      }
    }
    if (localZoneInfo != null) {
      zone = localZoneInfo;
    }
    if ((zone instanceof SimpleTimeZone))
    {
      String str = zone.getID();
      localObject = TimeZone.getTimeZone(str);
      if ((localObject != null) && (((TimeZone)localObject).hasSameRules(zone)) && (((TimeZone)localObject).getID().equals(str))) {
        zone = ((TimeZone)localObject);
      }
    }
  }
  
  public final Instant toInstant()
  {
    return Instant.ofEpochMilli(getTimeInMillis());
  }
  
  private static class AvailableCalendarTypes
  {
    private static final Set<String> SET;
    
    private AvailableCalendarTypes() {}
    
    static
    {
      HashSet localHashSet = new HashSet(3);
      localHashSet.add("gregory");
      localHashSet.add("buddhist");
      localHashSet.add("japanese");
      SET = Collections.unmodifiableSet(localHashSet);
    }
  }
  
  public static class Builder
  {
    private static final int NFIELDS = 18;
    private static final int WEEK_YEAR = 17;
    private long instant;
    private int[] fields;
    private int nextStamp;
    private int maxFieldIndex;
    private String type;
    private TimeZone zone;
    private boolean lenient = true;
    private Locale locale;
    private int firstDayOfWeek;
    private int minimalDaysInFirstWeek;
    
    public Builder() {}
    
    public Builder setInstant(long paramLong)
    {
      if (fields != null) {
        throw new IllegalStateException();
      }
      instant = paramLong;
      nextStamp = 1;
      return this;
    }
    
    public Builder setInstant(Date paramDate)
    {
      return setInstant(paramDate.getTime());
    }
    
    public Builder set(int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt1 >= 17)) {
        throw new IllegalArgumentException("field is invalid");
      }
      if (isInstantSet()) {
        throw new IllegalStateException("instant has been set");
      }
      allocateFields();
      internalSet(paramInt1, paramInt2);
      return this;
    }
    
    public Builder setFields(int... paramVarArgs)
    {
      int i = paramVarArgs.length;
      if (i % 2 != 0) {
        throw new IllegalArgumentException();
      }
      if (isInstantSet()) {
        throw new IllegalStateException("instant has been set");
      }
      if (nextStamp + i / 2 < 0) {
        throw new IllegalStateException("stamp counter overflow");
      }
      allocateFields();
      int j = 0;
      while (j < i)
      {
        int k = paramVarArgs[(j++)];
        if ((k < 0) || (k >= 17)) {
          throw new IllegalArgumentException("field is invalid");
        }
        internalSet(k, paramVarArgs[(j++)]);
      }
      return this;
    }
    
    public Builder setDate(int paramInt1, int paramInt2, int paramInt3)
    {
      return setFields(new int[] { 1, paramInt1, 2, paramInt2, 5, paramInt3 });
    }
    
    public Builder setTimeOfDay(int paramInt1, int paramInt2, int paramInt3)
    {
      return setTimeOfDay(paramInt1, paramInt2, paramInt3, 0);
    }
    
    public Builder setTimeOfDay(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return setFields(new int[] { 11, paramInt1, 12, paramInt2, 13, paramInt3, 14, paramInt4 });
    }
    
    public Builder setWeekDate(int paramInt1, int paramInt2, int paramInt3)
    {
      allocateFields();
      internalSet(17, paramInt1);
      internalSet(3, paramInt2);
      internalSet(7, paramInt3);
      return this;
    }
    
    public Builder setTimeZone(TimeZone paramTimeZone)
    {
      if (paramTimeZone == null) {
        throw new NullPointerException();
      }
      zone = paramTimeZone;
      return this;
    }
    
    public Builder setLenient(boolean paramBoolean)
    {
      lenient = paramBoolean;
      return this;
    }
    
    public Builder setCalendarType(String paramString)
    {
      if (paramString.equals("gregorian")) {
        paramString = "gregory";
      }
      if ((!Calendar.getAvailableCalendarTypes().contains(paramString)) && (!paramString.equals("iso8601"))) {
        throw new IllegalArgumentException("unknown calendar type: " + paramString);
      }
      if (type == null) {
        type = paramString;
      } else if (!type.equals(paramString)) {
        throw new IllegalStateException("calendar type override");
      }
      return this;
    }
    
    public Builder setLocale(Locale paramLocale)
    {
      if (paramLocale == null) {
        throw new NullPointerException();
      }
      locale = paramLocale;
      return this;
    }
    
    public Builder setWeekDefinition(int paramInt1, int paramInt2)
    {
      if ((!isValidWeekParameter(paramInt1)) || (!isValidWeekParameter(paramInt2))) {
        throw new IllegalArgumentException();
      }
      firstDayOfWeek = paramInt1;
      minimalDaysInFirstWeek = paramInt2;
      return this;
    }
    
    public Calendar build()
    {
      if (locale == null) {
        locale = Locale.getDefault();
      }
      if (zone == null) {
        zone = TimeZone.getDefault();
      }
      if (type == null) {
        type = locale.getUnicodeLocaleType("ca");
      }
      if (type == null) {
        if ((locale.getCountry() == "TH") && (locale.getLanguage() == "th")) {
          type = "buddhist";
        } else {
          type = "gregory";
        }
      }
      Object localObject;
      switch (type)
      {
      case "gregory": 
        localObject = new GregorianCalendar(zone, locale, true);
        break;
      case "iso8601": 
        GregorianCalendar localGregorianCalendar = new GregorianCalendar(zone, locale, true);
        localGregorianCalendar.setGregorianChange(new Date(Long.MIN_VALUE));
        setWeekDefinition(2, 4);
        localObject = localGregorianCalendar;
        break;
      case "buddhist": 
        localObject = new BuddhistCalendar(zone, locale);
        ((Calendar)localObject).clear();
        break;
      case "japanese": 
        localObject = new JapaneseImperialCalendar(zone, locale, true);
        break;
      default: 
        throw new IllegalArgumentException("unknown calendar type: " + type);
      }
      ((Calendar)localObject).setLenient(lenient);
      if (firstDayOfWeek != 0)
      {
        ((Calendar)localObject).setFirstDayOfWeek(firstDayOfWeek);
        ((Calendar)localObject).setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
      }
      if (isInstantSet())
      {
        ((Calendar)localObject).setTimeInMillis(instant);
        ((Calendar)localObject).complete();
        return (Calendar)localObject;
      }
      if (fields != null)
      {
        int i = (isSet(17)) && (fields[17] > fields[1]) ? 1 : 0;
        if ((i != 0) && (!((Calendar)localObject).isWeekDateSupported())) {
          throw new IllegalArgumentException("week date is unsupported by " + type);
        }
        int k;
        for (??? = 2; ??? < nextStamp; ???++) {
          for (k = 0; k <= maxFieldIndex; k++) {
            if (fields[k] == ???)
            {
              ((Calendar)localObject).set(k, fields[(18 + k)]);
              break;
            }
          }
        }
        if (i != 0)
        {
          ??? = isSet(3) ? fields[21] : 1;
          k = isSet(7) ? fields[25] : ((Calendar)localObject).getFirstDayOfWeek();
          ((Calendar)localObject).setWeekDate(fields[35], ???, k);
        }
        ((Calendar)localObject).complete();
      }
      return (Calendar)localObject;
    }
    
    private void allocateFields()
    {
      if (fields == null)
      {
        fields = new int[36];
        nextStamp = 2;
        maxFieldIndex = -1;
      }
    }
    
    private void internalSet(int paramInt1, int paramInt2)
    {
      fields[paramInt1] = (nextStamp++);
      if (nextStamp < 0) {
        throw new IllegalStateException("stamp counter overflow");
      }
      fields[(18 + paramInt1)] = paramInt2;
      if ((paramInt1 > maxFieldIndex) && (paramInt1 < 17)) {
        maxFieldIndex = paramInt1;
      }
    }
    
    private boolean isInstantSet()
    {
      return nextStamp == 1;
    }
    
    private boolean isSet(int paramInt)
    {
      return (fields != null) && (fields[paramInt] > 0);
    }
    
    private boolean isValidWeekParameter(int paramInt)
    {
      return (paramInt > 0) && (paramInt <= 7);
    }
  }
  
  private static class CalendarAccessControlContext
  {
    private static final AccessControlContext INSTANCE;
    
    private CalendarAccessControlContext() {}
    
    static
    {
      RuntimePermission localRuntimePermission = new RuntimePermission("accessClassInPackage.sun.util.calendar");
      PermissionCollection localPermissionCollection = localRuntimePermission.newPermissionCollection();
      localPermissionCollection.add(localRuntimePermission);
      INSTANCE = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, localPermissionCollection) });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Calendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */