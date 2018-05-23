package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import sun.util.calendar.BaseCalendar.Date;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.Gregorian.Date;
import sun.util.calendar.LocalGregorianCalendar;
import sun.util.calendar.LocalGregorianCalendar.Date;
import sun.util.calendar.ZoneInfo;
import sun.util.locale.provider.CalendarDataUtility;

class JapaneseImperialCalendar
  extends Calendar
{
  public static final int BEFORE_MEIJI = 0;
  public static final int MEIJI = 1;
  public static final int TAISHO = 2;
  public static final int SHOWA = 3;
  public static final int HEISEI = 4;
  private static final int EPOCH_OFFSET = 719163;
  private static final int EPOCH_YEAR = 1970;
  private static final int ONE_SECOND = 1000;
  private static final int ONE_MINUTE = 60000;
  private static final int ONE_HOUR = 3600000;
  private static final long ONE_DAY = 86400000L;
  private static final long ONE_WEEK = 604800000L;
  private static final LocalGregorianCalendar jcal;
  private static final Gregorian gcal;
  private static final Era BEFORE_MEIJI_ERA;
  private static final Era[] eras;
  private static final long[] sinceFixedDates;
  static final int[] MIN_VALUES;
  static final int[] LEAST_MAX_VALUES;
  static final int[] MAX_VALUES;
  private static final long serialVersionUID = -3364572813905467929L;
  private transient LocalGregorianCalendar.Date jdate;
  private transient int[] zoneOffsets;
  private transient int[] originalFields;
  private transient long cachedFixedDate = Long.MIN_VALUE;
  
  JapaneseImperialCalendar(TimeZone paramTimeZone, Locale paramLocale)
  {
    super(paramTimeZone, paramLocale);
    jdate = jcal.newCalendarDate(paramTimeZone);
    setTimeInMillis(System.currentTimeMillis());
  }
  
  JapaneseImperialCalendar(TimeZone paramTimeZone, Locale paramLocale, boolean paramBoolean)
  {
    super(paramTimeZone, paramLocale);
    jdate = jcal.newCalendarDate(paramTimeZone);
  }
  
  public String getCalendarType()
  {
    return "japanese";
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof JapaneseImperialCalendar)) && (super.equals(paramObject));
  }
  
  public int hashCode()
  {
    return super.hashCode() ^ jdate.hashCode();
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
    LocalGregorianCalendar.Date localDate;
    if (paramInt1 == 1)
    {
      localDate = (LocalGregorianCalendar.Date)jdate.clone();
      localDate.addYear(paramInt2);
      pinDayOfMonth(localDate);
      set(0, getEraIndex(localDate));
      set(1, localDate.getYear());
      set(2, localDate.getMonth() - 1);
      set(5, localDate.getDayOfMonth());
    }
    else if (paramInt1 == 2)
    {
      localDate = (LocalGregorianCalendar.Date)jdate.clone();
      localDate.addMonth(paramInt2);
      pinDayOfMonth(localDate);
      set(0, getEraIndex(localDate));
      set(1, localDate.getYear());
      set(2, localDate.getMonth() - 1);
      set(5, localDate.getDayOfMonth());
    }
    else if (paramInt1 == 0)
    {
      int i = internalGet(0) + paramInt2;
      if (i < 0) {
        i = 0;
      } else if (i > eras.length - 1) {
        i = eras.length - 1;
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
      long l3 = cachedFixedDate;
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
      int j = internalGet(15) + internalGet(16);
      setTimeInMillis((l3 - 719163L) * 86400000L + l2 - j);
      j -= internalGet(15) + internalGet(16);
      if (j != 0)
      {
        setTimeInMillis(time + j);
        long l4 = cachedFixedDate;
        if (l4 != l3) {
          setTimeInMillis(time - j);
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
    int i8;
    int i6;
    int i2;
    long l9;
    LocalGregorianCalendar.Date localDate7;
    int i15;
    int i7;
    LocalGregorianCalendar.Date localDate4;
    int m;
    switch (paramInt1)
    {
    case 0: 
    case 9: 
    case 12: 
    case 13: 
    case 14: 
      break;
    case 10: 
    case 11: 
      k = j + 1;
      int n = internalGet(paramInt1);
      int i5 = (n + paramInt2) % k;
      if (i5 < 0) {
        i5 += k;
      }
      time += 3600000 * (i5 - n);
      LocalGregorianCalendar.Date localDate3 = jcal.getCalendarDate(time, getZone());
      if (internalGet(5) != localDate3.getDayOfMonth())
      {
        localDate3.setEra(jdate.getEra());
        localDate3.setDate(internalGet(1), internalGet(2) + 1, internalGet(5));
        if (paramInt1 == 10)
        {
          assert (internalGet(9) == 1);
          localDate3.addHours(12);
        }
        time = jcal.getTime(localDate3);
      }
      int i10 = localDate3.getHours();
      internalSet(paramInt1, i10 % k);
      if (paramInt1 == 10)
      {
        internalSet(11, i10);
      }
      else
      {
        internalSet(9, i10 / 12);
        internalSet(10, i10 % 12);
      }
      int i12 = localDate3.getZoneOffset();
      int i14 = localDate3.getDaylightSaving();
      internalSet(15, i12 - i14);
      internalSet(16, i14);
      return;
    case 1: 
      i = getActualMinimum(paramInt1);
      j = getActualMaximum(paramInt1);
      break;
    case 2: 
      if (!isTransitionYear(jdate.getNormalizedYear()))
      {
        k = jdate.getYear();
        LocalGregorianCalendar.Date localDate1;
        LocalGregorianCalendar.Date localDate2;
        if (k == getMaximum(1))
        {
          localDate1 = jcal.getCalendarDate(time, getZone());
          localDate2 = jcal.getCalendarDate(Long.MAX_VALUE, getZone());
          j = localDate2.getMonth() - 1;
          i8 = getRolledValue(internalGet(paramInt1), paramInt2, i, j);
          if (i8 == j)
          {
            localDate1.addYear(65136);
            localDate1.setMonth(i8 + 1);
            if (localDate1.getDayOfMonth() > localDate2.getDayOfMonth())
            {
              localDate1.setDayOfMonth(localDate2.getDayOfMonth());
              jcal.normalize(localDate1);
            }
            if ((localDate1.getDayOfMonth() == localDate2.getDayOfMonth()) && (localDate1.getTimeOfDay() > localDate2.getTimeOfDay()))
            {
              localDate1.setMonth(i8 + 1);
              localDate1.setDayOfMonth(localDate2.getDayOfMonth() - 1);
              jcal.normalize(localDate1);
              i8 = localDate1.getMonth() - 1;
            }
            set(5, localDate1.getDayOfMonth());
          }
          set(2, i8);
        }
        else if (k == getMinimum(1))
        {
          localDate1 = jcal.getCalendarDate(time, getZone());
          localDate2 = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
          i = localDate2.getMonth() - 1;
          i8 = getRolledValue(internalGet(paramInt1), paramInt2, i, j);
          if (i8 == i)
          {
            localDate1.addYear(400);
            localDate1.setMonth(i8 + 1);
            if (localDate1.getDayOfMonth() < localDate2.getDayOfMonth())
            {
              localDate1.setDayOfMonth(localDate2.getDayOfMonth());
              jcal.normalize(localDate1);
            }
            if ((localDate1.getDayOfMonth() == localDate2.getDayOfMonth()) && (localDate1.getTimeOfDay() < localDate2.getTimeOfDay()))
            {
              localDate1.setMonth(i8 + 1);
              localDate1.setDayOfMonth(localDate2.getDayOfMonth() + 1);
              jcal.normalize(localDate1);
              i8 = localDate1.getMonth() - 1;
            }
            set(5, localDate1.getDayOfMonth());
          }
          set(2, i8);
        }
        else
        {
          int i1 = (internalGet(2) + paramInt2) % 12;
          if (i1 < 0) {
            i1 += 12;
          }
          set(2, i1);
          i6 = monthLength(i1);
          if (internalGet(5) > i6) {
            set(5, i6);
          }
        }
      }
      else
      {
        k = getEraIndex(jdate);
        CalendarDate localCalendarDate = null;
        if (jdate.getYear() == 1)
        {
          localCalendarDate = eras[k].getSinceDate();
          i = localCalendarDate.getMonth() - 1;
        }
        else if (k < eras.length - 1)
        {
          localCalendarDate = eras[(k + 1)].getSinceDate();
          if (localCalendarDate.getYear() == jdate.getNormalizedYear())
          {
            j = localCalendarDate.getMonth() - 1;
            if (localCalendarDate.getDayOfMonth() == 1) {
              j--;
            }
          }
        }
        if (i == j) {
          return;
        }
        i6 = getRolledValue(internalGet(paramInt1), paramInt2, i, j);
        set(2, i6);
        if (i6 == i)
        {
          if (((localCalendarDate.getMonth() != 1) || (localCalendarDate.getDayOfMonth() != 1)) && (jdate.getDayOfMonth() < localCalendarDate.getDayOfMonth())) {
            set(5, localCalendarDate.getDayOfMonth());
          }
        }
        else if ((i6 == j) && (localCalendarDate.getMonth() - 1 == i6))
        {
          i8 = localCalendarDate.getDayOfMonth();
          if (jdate.getDayOfMonth() >= i8) {
            set(5, i8 - 1);
          }
        }
      }
      return;
    case 3: 
      k = jdate.getNormalizedYear();
      j = getActualMaximum(3);
      set(7, internalGet(7));
      i2 = internalGet(3);
      i6 = i2 + paramInt2;
      if (!isTransitionYear(jdate.getNormalizedYear()))
      {
        i8 = jdate.getYear();
        if (i8 == getMaximum(1))
        {
          j = getActualMaximum(3);
        }
        else if (i8 == getMinimum(1))
        {
          i = getActualMinimum(3);
          j = getActualMaximum(3);
          if ((i6 > i) && (i6 < j))
          {
            set(3, i6);
            return;
          }
        }
        if ((i6 > i) && (i6 < j))
        {
          set(3, i6);
          return;
        }
        l9 = cachedFixedDate;
        long l11 = l9 - 7 * (i2 - i);
        if (i8 != getMinimum(1))
        {
          if (gcal.getYearFromFixedDate(l11) != k) {
            i++;
          }
        }
        else
        {
          localDate7 = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
          if (l11 < jcal.getFixedDate(localDate7)) {
            i++;
          }
        }
        l9 += 7 * (j - internalGet(3));
        if (gcal.getYearFromFixedDate(l9) != k) {
          j--;
        }
      }
      else
      {
        long l7 = cachedFixedDate;
        long l10 = l7 - 7 * (i2 - i);
        LocalGregorianCalendar.Date localDate6 = getCalendarDate(l10);
        if ((localDate6.getEra() != jdate.getEra()) || (localDate6.getYear() != jdate.getYear())) {
          i++;
        }
        l7 += 7 * (j - i2);
        jcal.getCalendarDateFromFixedDate(localDate6, l7);
        if ((localDate6.getEra() != jdate.getEra()) || (localDate6.getYear() != jdate.getYear())) {
          j--;
        }
        i6 = getRolledValue(i2, paramInt2, i, j) - 1;
        localDate6 = getCalendarDate(l10 + i6 * 7);
        set(2, localDate6.getMonth() - 1);
        set(5, localDate6.getDayOfMonth());
        return;
      }
      break;
    case 4: 
      boolean bool = isTransitionYear(jdate.getNormalizedYear());
      i2 = internalGet(7) - getFirstDayOfWeek();
      if (i2 < 0) {
        i2 += 7;
      }
      long l5 = cachedFixedDate;
      if (bool)
      {
        l9 = getFixedDateMonth1(jdate, l5);
        i15 = actualMonthLength();
      }
      else
      {
        l9 = l5 - internalGet(5) + 1L;
        i15 = jcal.getMonthLength(jdate);
      }
      long l12 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l9 + 6L, getFirstDayOfWeek());
      if ((int)(l12 - l9) >= getMinimalDaysInFirstWeek()) {
        l12 -= 7L;
      }
      j = getActualMaximum(paramInt1);
      int i17 = getRolledValue(internalGet(paramInt1), paramInt2, 1, j) - 1;
      long l13 = l12 + i17 * 7 + i2;
      if (l13 < l9) {
        l13 = l9;
      } else if (l13 >= l9 + i15) {
        l13 = l9 + i15 - 1L;
      }
      set(5, (int)(l13 - l9) + 1);
      return;
    case 5: 
      if (!isTransitionYear(jdate.getNormalizedYear()))
      {
        j = jcal.getMonthLength(jdate);
      }
      else
      {
        long l1 = getFixedDateMonth1(jdate, cachedFixedDate);
        i7 = getRolledValue((int)(cachedFixedDate - l1), paramInt2, 0, actualMonthLength() - 1);
        localDate4 = getCalendarDate(l1 + i7);
        assert ((getEraIndex(localDate4) == internalGetEra()) && (localDate4.getYear() == internalGet(1)) && (localDate4.getMonth() - 1 == internalGet(2)));
        set(5, localDate4.getDayOfMonth());
        return;
      }
      break;
    case 6: 
      j = getActualMaximum(paramInt1);
      if (isTransitionYear(jdate.getNormalizedYear()))
      {
        m = getRolledValue(internalGet(6), paramInt2, i, j);
        long l3 = cachedFixedDate - internalGet(6);
        localDate4 = getCalendarDate(l3 + m);
        assert ((getEraIndex(localDate4) == internalGetEra()) && (localDate4.getYear() == internalGet(1)));
        set(2, localDate4.getMonth() - 1);
        set(5, localDate4.getDayOfMonth());
        return;
      }
      break;
    case 7: 
      m = jdate.getNormalizedYear();
      if ((!isTransitionYear(m)) && (!isTransitionYear(m - 1)))
      {
        int i3 = internalGet(3);
        if ((i3 > 1) && (i3 < 52))
        {
          set(3, internalGet(3));
          j = 7;
          break;
        }
      }
      paramInt2 %= 7;
      if (paramInt2 == 0) {
        return;
      }
      long l4 = cachedFixedDate;
      long l8 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l4, getFirstDayOfWeek());
      l4 += paramInt2;
      if (l4 < l8) {
        l4 += 7L;
      } else if (l4 >= l8 + 7L) {
        l4 -= 7L;
      }
      LocalGregorianCalendar.Date localDate5 = getCalendarDate(l4);
      set(0, getEraIndex(localDate5));
      set(localDate5.getYear(), localDate5.getMonth() - 1, localDate5.getDayOfMonth());
      return;
    case 8: 
      i = 1;
      if (!isTransitionYear(jdate.getNormalizedYear()))
      {
        m = internalGet(5);
        int i4 = jcal.getMonthLength(jdate);
        i7 = i4 % 7;
        j = i4 / 7;
        int i9 = (m - 1) % 7;
        if (i9 < i7) {
          j++;
        }
        set(7, internalGet(7));
      }
      else
      {
        long l2 = cachedFixedDate;
        long l6 = getFixedDateMonth1(jdate, l2);
        int i11 = actualMonthLength();
        int i13 = i11 % 7;
        j = i11 / 7;
        i15 = (int)(l2 - l6) % 7;
        if (i15 < i13) {
          j++;
        }
        int i16 = getRolledValue(internalGet(paramInt1), paramInt2, i, j) - 1;
        l2 = l6 + i16 * 7 + i15;
        localDate7 = getCalendarDate(l2);
        set(5, localDate7.getDayOfMonth());
        return;
      }
      break;
    }
    set(paramInt1, getRolledValue(internalGet(paramInt1), paramInt2, i, j));
  }
  
  public String getDisplayName(int paramInt1, int paramInt2, Locale paramLocale)
  {
    if (!checkDisplayNameParams(paramInt1, paramInt2, 1, 4, paramLocale, 647)) {
      return null;
    }
    int i = get(paramInt1);
    if ((paramInt1 == 1) && ((getBaseStyle(paramInt2) != 2) || (i != 1) || (get(0) == 0))) {
      return null;
    }
    String str = CalendarDataUtility.retrieveFieldValueName(getCalendarType(), paramInt1, i, paramInt2, paramLocale);
    if ((str == null) && (paramInt1 == 0) && (i < eras.length))
    {
      Era localEra = eras[i];
      str = paramInt2 == 1 ? localEra.getAbbreviation() : localEra.getName();
    }
    return str;
  }
  
  public Map<String, Integer> getDisplayNames(int paramInt1, int paramInt2, Locale paramLocale)
  {
    if (!checkDisplayNameParams(paramInt1, paramInt2, 0, 4, paramLocale, 647)) {
      return null;
    }
    Map localMap = CalendarDataUtility.retrieveFieldValueNames(getCalendarType(), paramInt1, paramInt2, paramLocale);
    if ((localMap != null) && (paramInt1 == 0))
    {
      Iterator localIterator1 = localMap.size();
      Iterator localIterator2;
      Object localObject;
      if (paramInt2 == 0)
      {
        HashSet localHashSet = new HashSet();
        localIterator2 = localMap.keySet().iterator();
        while (localIterator2.hasNext())
        {
          localObject = (String)localIterator2.next();
          localHashSet.add(localMap.get(localObject));
        }
        localIterator1 = localHashSet.size();
      }
      if (localIterator1 < eras.length)
      {
        int i = getBaseStyle(paramInt2);
        for (localIterator2 = localIterator1; localIterator2 < eras.length; localIterator2++)
        {
          localObject = eras[localIterator2];
          if ((i == 0) || (i == 1) || (i == 4)) {
            localMap.put(((Era)localObject).getAbbreviation(), Integer.valueOf(localIterator2));
          }
          if ((i == 0) || (i == 2)) {
            localMap.put(((Era)localObject).getName(), Integer.valueOf(localIterator2));
          }
        }
      }
    }
    return localMap;
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
      LocalGregorianCalendar.Date localDate = jcal.getCalendarDate(Long.MAX_VALUE, getZone());
      return Math.max(LEAST_MAX_VALUES[1], localDate.getYear());
    }
    return MAX_VALUES[paramInt];
  }
  
  public int getGreatestMinimum(int paramInt)
  {
    return paramInt == 1 ? 1 : MIN_VALUES[paramInt];
  }
  
  public int getLeastMaximum(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return Math.min(LEAST_MAX_VALUES[1], getMaximum(1));
    }
    return LEAST_MAX_VALUES[paramInt];
  }
  
  public int getActualMinimum(int paramInt)
  {
    if (!isFieldSet(14, paramInt)) {
      return getMinimum(paramInt);
    }
    int i = 0;
    JapaneseImperialCalendar localJapaneseImperialCalendar = getNormalizedCalendar();
    LocalGregorianCalendar.Date localDate1 = jcal.getCalendarDate(localJapaneseImperialCalendar.getTimeInMillis(), getZone());
    int j = getEraIndex(localDate1);
    LocalGregorianCalendar.Date localDate4;
    switch (paramInt)
    {
    case 1: 
      if (j > 0)
      {
        i = 1;
        long l1 = eras[j].getSince(getZone());
        localDate4 = jcal.getCalendarDate(l1, getZone());
        localDate1.setYear(localDate4.getYear());
        jcal.normalize(localDate1);
        assert (localDate1.isLeapYear() == localDate4.isLeapYear());
        if (getYearOffsetInMillis(localDate1) < getYearOffsetInMillis(localDate4)) {
          i++;
        }
      }
      else
      {
        i = getMinimum(paramInt);
        LocalGregorianCalendar.Date localDate2 = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
        int k = localDate2.getYear();
        if (k > 400) {
          k -= 400;
        }
        localDate1.setYear(k);
        jcal.normalize(localDate1);
        if (getYearOffsetInMillis(localDate1) < getYearOffsetInMillis(localDate2)) {
          i++;
        }
      }
      break;
    case 2: 
      if ((j > 1) && (localDate1.getYear() == 1))
      {
        long l2 = eras[j].getSince(getZone());
        localDate4 = jcal.getCalendarDate(l2, getZone());
        i = localDate4.getMonth() - 1;
        if (localDate1.getDayOfMonth() < localDate4.getDayOfMonth()) {
          i++;
        }
      }
      break;
    case 3: 
      i = 1;
      LocalGregorianCalendar.Date localDate3 = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
      localDate3.addYear(400);
      jcal.normalize(localDate3);
      localDate1.setEra(localDate3.getEra());
      localDate1.setYear(localDate3.getYear());
      jcal.normalize(localDate1);
      long l3 = jcal.getFixedDate(localDate3);
      long l4 = jcal.getFixedDate(localDate1);
      int m = getWeekNumber(l3, l4);
      long l5 = l4 - 7 * (m - 1);
      if ((l5 < l3) || ((l5 == l3) && (localDate1.getTimeOfDay() < localDate3.getTimeOfDay()))) {
        i++;
      }
      break;
    }
    return i;
  }
  
  public int getActualMaximum(int paramInt)
  {
    if ((0x1FE81 & 1 << paramInt) != 0) {
      return getMaximum(paramInt);
    }
    JapaneseImperialCalendar localJapaneseImperialCalendar = getNormalizedCalendar();
    LocalGregorianCalendar.Date localDate1 = jdate;
    int i = localDate1.getNormalizedYear();
    int j = -1;
    long l1;
    long l5;
    Object localObject2;
    LocalGregorianCalendar.Date localDate3;
    long l4;
    long l7;
    Object localObject1;
    int i3;
    int i4;
    int i5;
    switch (paramInt)
    {
    case 2: 
      j = 11;
      if (isTransitionYear(localDate1.getNormalizedYear()))
      {
        int k = getEraIndex(localDate1);
        if (localDate1.getYear() != 1)
        {
          k++;
          assert (k < eras.length);
        }
        l1 = sinceFixedDates[k];
        l5 = cachedFixedDate;
        if (l5 < l1)
        {
          localObject2 = (LocalGregorianCalendar.Date)localDate1.clone();
          jcal.getCalendarDateFromFixedDate((CalendarDate)localObject2, l1 - 1L);
          j = ((LocalGregorianCalendar.Date)localObject2).getMonth() - 1;
        }
      }
      else
      {
        LocalGregorianCalendar.Date localDate2 = jcal.getCalendarDate(Long.MAX_VALUE, getZone());
        if ((localDate1.getEra() == localDate2.getEra()) && (localDate1.getYear() == localDate2.getYear())) {
          j = localDate2.getMonth() - 1;
        }
      }
      break;
    case 5: 
      j = jcal.getMonthLength(localDate1);
      break;
    case 6: 
      if (isTransitionYear(localDate1.getNormalizedYear()))
      {
        int m = getEraIndex(localDate1);
        if (localDate1.getYear() != 1)
        {
          m++;
          assert (m < eras.length);
        }
        l1 = sinceFixedDates[m];
        l5 = cachedFixedDate;
        localObject2 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
        ((CalendarDate)localObject2).setDate(localDate1.getNormalizedYear(), 1, 1);
        if (l5 < l1)
        {
          j = (int)(l1 - gcal.getFixedDate((CalendarDate)localObject2));
        }
        else
        {
          ((CalendarDate)localObject2).addYear(1);
          j = (int)(gcal.getFixedDate((CalendarDate)localObject2) - l1);
        }
      }
      else
      {
        localDate3 = jcal.getCalendarDate(Long.MAX_VALUE, getZone());
        if ((localDate1.getEra() == localDate3.getEra()) && (localDate1.getYear() == localDate3.getYear()))
        {
          l1 = jcal.getFixedDate(localDate3);
          l5 = getFixedDateJan1(localDate3, l1);
          j = (int)(l1 - l5) + 1;
        }
        else if (localDate1.getYear() == getMinimum(1))
        {
          LocalGregorianCalendar.Date localDate6 = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
          l4 = jcal.getFixedDate(localDate6);
          localDate6.addYear(1);
          localDate6.setMonth(1).setDayOfMonth(1);
          jcal.normalize(localDate6);
          l7 = jcal.getFixedDate(localDate6);
          j = (int)(l7 - l4);
        }
        else
        {
          j = jcal.getYearLength(localDate1);
        }
      }
      break;
    case 3: 
      if (!isTransitionYear(localDate1.getNormalizedYear()))
      {
        localDate3 = jcal.getCalendarDate(Long.MAX_VALUE, getZone());
        if ((localDate1.getEra() == localDate3.getEra()) && (localDate1.getYear() == localDate3.getYear()))
        {
          long l2 = jcal.getFixedDate(localDate3);
          l5 = getFixedDateJan1(localDate3, l2);
          j = getWeekNumber(l5, l2);
        }
        else if ((localDate1.getEra() == null) && (localDate1.getYear() == getMinimum(1)))
        {
          localObject1 = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
          ((CalendarDate)localObject1).addYear(400);
          jcal.normalize((CalendarDate)localObject1);
          localDate3.setEra(((CalendarDate)localObject1).getEra());
          localDate3.setDate(((CalendarDate)localObject1).getYear() + 1, 1, 1);
          jcal.normalize(localDate3);
          l4 = jcal.getFixedDate((CalendarDate)localObject1);
          l7 = jcal.getFixedDate(localDate3);
          long l8 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l7 + 6L, getFirstDayOfWeek());
          int i6 = (int)(l8 - l7);
          if (i6 >= getMinimalDaysInFirstWeek()) {
            l8 -= 7L;
          }
          j = getWeekNumber(l4, l8);
        }
        else
        {
          localObject1 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
          ((CalendarDate)localObject1).setDate(localDate1.getNormalizedYear(), 1, 1);
          i3 = gcal.getDayOfWeek((CalendarDate)localObject1);
          i3 -= getFirstDayOfWeek();
          if (i3 < 0) {
            i3 += 7;
          }
          j = 52;
          i4 = i3 + getMinimalDaysInFirstWeek() - 1;
          if ((i4 == 6) || ((localDate1.isLeapYear()) && ((i4 == 5) || (i4 == 12)))) {
            j++;
          }
        }
      }
      else
      {
        if (localJapaneseImperialCalendar == this) {
          localJapaneseImperialCalendar = (JapaneseImperialCalendar)localJapaneseImperialCalendar.clone();
        }
        int n = getActualMaximum(6);
        localJapaneseImperialCalendar.set(6, n);
        j = localJapaneseImperialCalendar.get(3);
        if ((j == 1) && (n > 7))
        {
          localJapaneseImperialCalendar.add(3, -1);
          j = localJapaneseImperialCalendar.get(3);
        }
      }
      break;
    case 4: 
      LocalGregorianCalendar.Date localDate4 = jcal.getCalendarDate(Long.MAX_VALUE, getZone());
      if ((localDate1.getEra() != localDate4.getEra()) || (localDate1.getYear() != localDate4.getYear()))
      {
        localObject1 = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
        ((CalendarDate)localObject1).setDate(localDate1.getNormalizedYear(), localDate1.getMonth(), 1);
        i3 = gcal.getDayOfWeek((CalendarDate)localObject1);
        i4 = gcal.getMonthLength((CalendarDate)localObject1);
        i3 -= getFirstDayOfWeek();
        if (i3 < 0) {
          i3 += 7;
        }
        i5 = 7 - i3;
        j = 3;
        if (i5 >= getMinimalDaysInFirstWeek()) {
          j++;
        }
        i4 -= i5 + 21;
        if (i4 > 0)
        {
          j++;
          if (i4 > 7) {
            j++;
          }
        }
      }
      else
      {
        long l3 = jcal.getFixedDate(localDate4);
        long l6 = l3 - localDate4.getDayOfMonth() + 1L;
        j = getWeekNumber(l6, l3);
      }
      break;
    case 8: 
      i3 = localDate1.getDayOfWeek();
      BaseCalendar.Date localDate = (BaseCalendar.Date)localDate1.clone();
      int i1 = jcal.getMonthLength(localDate);
      localDate.setDayOfMonth(1);
      jcal.normalize(localDate);
      int i2 = localDate.getDayOfWeek();
      i5 = i3 - i2;
      if (i5 < 0) {
        i5 += 7;
      }
      i1 -= i5;
      j = (i1 + 6) / 7;
      break;
    case 1: 
      LocalGregorianCalendar.Date localDate5 = jcal.getCalendarDate(localJapaneseImperialCalendar.getTimeInMillis(), getZone());
      i3 = getEraIndex(localDate1);
      LocalGregorianCalendar.Date localDate7;
      if (i3 == eras.length - 1)
      {
        localDate7 = jcal.getCalendarDate(Long.MAX_VALUE, getZone());
        j = localDate7.getYear();
        if (j > 400) {
          localDate5.setYear(j - 400);
        }
      }
      else
      {
        localDate7 = jcal.getCalendarDate(eras[(i3 + 1)].getSince(getZone()) - 1L, getZone());
        j = localDate7.getYear();
        localDate5.setYear(j);
      }
      jcal.normalize(localDate5);
      if (getYearOffsetInMillis(localDate5) > getYearOffsetInMillis(localDate7)) {
        j--;
      }
      break;
    case 7: 
    default: 
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    return j;
  }
  
  private long getYearOffsetInMillis(CalendarDate paramCalendarDate)
  {
    long l = (jcal.getDayOfYear(paramCalendarDate) - 1L) * 86400000L;
    return l + paramCalendarDate.getTimeOfDay() - paramCalendarDate.getZoneOffset();
  }
  
  public Object clone()
  {
    JapaneseImperialCalendar localJapaneseImperialCalendar = (JapaneseImperialCalendar)super.clone();
    jdate = ((LocalGregorianCalendar.Date)jdate.clone());
    originalFields = null;
    zoneOffsets = null;
    return localJapaneseImperialCalendar;
  }
  
  public TimeZone getTimeZone()
  {
    TimeZone localTimeZone = super.getTimeZone();
    jdate.setZone(localTimeZone);
    return localTimeZone;
  }
  
  public void setTimeZone(TimeZone paramTimeZone)
  {
    super.setTimeZone(paramTimeZone);
    jdate.setZone(paramTimeZone);
  }
  
  protected void computeFields()
  {
    int i = 0;
    if (isPartiallyNormalized())
    {
      i = getSetStateFields();
      int j = (i ^ 0xFFFFFFFF) & 0x1FFFF;
      if ((j != 0) || (cachedFixedDate == Long.MIN_VALUE))
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
    if ((l1 != cachedFixedDate) || (l1 < 0L))
    {
      jcal.getCalendarDateFromFixedDate(jdate, l1);
      cachedFixedDate = l1;
    }
    int k = getEraIndex(jdate);
    int m = jdate.getYear();
    internalSet(0, k);
    internalSet(1, m);
    int n = paramInt1 | 0x3;
    int i1 = jdate.getMonth() - 1;
    int i2 = jdate.getDayOfMonth();
    if ((paramInt1 & 0xA4) != 0)
    {
      internalSet(2, i1);
      internalSet(5, i2);
      internalSet(7, jdate.getDayOfWeek());
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
      i3 = jdate.getNormalizedYear();
      boolean bool = isTransitionYear(jdate.getNormalizedYear());
      long l2;
      int i5;
      if (bool)
      {
        l2 = getFixedDateJan1(jdate, l1);
        i5 = (int)(l1 - l2) + 1;
      }
      else if (i3 == MIN_VALUES[1])
      {
        LocalGregorianCalendar.Date localDate1 = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
        l2 = jcal.getFixedDate(localDate1);
        i5 = (int)(l1 - l2) + 1;
      }
      else
      {
        i5 = (int)jcal.getDayOfYear(jdate);
        l2 = l1 - i5 + 1L;
      }
      long l3 = bool ? getFixedDateMonth1(jdate, l1) : l1 - i2 + 1L;
      internalSet(6, i5);
      internalSet(8, (i2 - 1) / 7 + 1);
      int i6 = getWeekNumber(l2, l1);
      long l4;
      long l6;
      if (i6 == 0)
      {
        l4 = l2 - 1L;
        LocalGregorianCalendar.Date localDate3 = getCalendarDate(l4);
        if ((!bool) && (!isTransitionYear(localDate3.getNormalizedYear())))
        {
          l6 = l2 - 365L;
          if (localDate3.isLeapYear()) {
            l6 -= 1L;
          }
        }
        else
        {
          CalendarDate localCalendarDate2;
          if (bool)
          {
            if (jdate.getYear() == 1)
            {
              if (k > 4)
              {
                localCalendarDate2 = eras[(k - 1)].getSinceDate();
                if (i3 == localCalendarDate2.getYear()) {
                  localDate3.setMonth(localCalendarDate2.getMonth()).setDayOfMonth(localCalendarDate2.getDayOfMonth());
                }
              }
              else
              {
                localDate3.setMonth(1).setDayOfMonth(1);
              }
              jcal.normalize(localDate3);
              l6 = jcal.getFixedDate(localDate3);
            }
            else
            {
              l6 = l2 - 365L;
              if (localDate3.isLeapYear()) {
                l6 -= 1L;
              }
            }
          }
          else
          {
            localCalendarDate2 = eras[getEraIndex(jdate)].getSinceDate();
            localDate3.setMonth(localCalendarDate2.getMonth()).setDayOfMonth(localCalendarDate2.getDayOfMonth());
            jcal.normalize(localDate3);
            l6 = jcal.getFixedDate(localDate3);
          }
        }
        i6 = getWeekNumber(l6, l4);
      }
      else if (!bool)
      {
        if (i6 >= 52)
        {
          l4 = l2 + 365L;
          if (jdate.isLeapYear()) {
            l4 += 1L;
          }
          l6 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l4 + 6L, getFirstDayOfWeek());
          int i8 = (int)(l6 - l4);
          if ((i8 >= getMinimalDaysInFirstWeek()) && (l1 >= l6 - 7L)) {
            i6 = 1;
          }
        }
      }
      else
      {
        LocalGregorianCalendar.Date localDate2 = (LocalGregorianCalendar.Date)jdate.clone();
        long l5;
        if (jdate.getYear() == 1)
        {
          localDate2.addYear(1);
          localDate2.setMonth(1).setDayOfMonth(1);
          l5 = jcal.getFixedDate(localDate2);
        }
        else
        {
          int i7 = getEraIndex(localDate2) + 1;
          CalendarDate localCalendarDate1 = eras[i7].getSinceDate();
          localDate2.setEra(eras[i7]);
          localDate2.setDate(1, localCalendarDate1.getMonth(), localCalendarDate1.getDayOfMonth());
          jcal.normalize(localDate2);
          l5 = jcal.getFixedDate(localDate2);
        }
        long l7 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l5 + 6L, getFirstDayOfWeek());
        int i9 = (int)(l7 - l5);
        if ((i9 >= getMinimalDaysInFirstWeek()) && (l1 >= l7 - 7L)) {
          i6 = 1;
        }
      }
      internalSet(3, i6);
      internalSet(4, getWeekNumber(l3, l1));
      n |= 0x158;
    }
    return n;
  }
  
  private int getWeekNumber(long paramLong1, long paramLong2)
  {
    long l = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(paramLong1 + 6L, getFirstDayOfWeek());
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
    int j;
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
    int k;
    if (isSet(0))
    {
      k = internalGet(0);
      j = isSet(1) ? internalGet(1) : 1;
    }
    else if (isSet(1))
    {
      k = eras.length - 1;
      j = internalGet(1);
    }
    else
    {
      k = 3;
      j = 45;
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
    l2 += getFixedDate(k, j, i);
    long l3 = (l2 - 719163L) * 86400000L + l1;
    TimeZone localTimeZone = getZone();
    if (zoneOffsets == null) {
      zoneOffsets = new int[2];
    }
    int m = i & 0x18000;
    if (m != 98304) {
      if ((localTimeZone instanceof ZoneInfo)) {
        ((ZoneInfo)localTimeZone).getOffsetsByWall(l3, zoneOffsets);
      } else {
        localTimeZone.getOffsets(l3 - localTimeZone.getRawOffset(), zoneOffsets);
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
          int i2 = internalGet(i1);
          System.arraycopy(originalFields, 0, fields, 0, fields.length);
          throw new IllegalArgumentException(getFieldName(i1) + "=" + i2 + ", expected " + originalFields[i1]);
        }
      }
    }
    setFieldsNormalized(n);
  }
  
  private long getFixedDate(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    int j = 1;
    if (isFieldSet(paramInt3, 2))
    {
      i = internalGet(2);
      if (i > 11)
      {
        paramInt2 += i / 12;
        i %= 12;
      }
      else if (i < 0)
      {
        localObject = new int[1];
        paramInt2 += CalendarUtils.floorDivide(i, 12, (int[])localObject);
        i = localObject[0];
      }
    }
    else if ((paramInt2 == 1) && (paramInt1 != 0))
    {
      localObject = eras[paramInt1].getSinceDate();
      i = ((CalendarDate)localObject).getMonth() - 1;
      j = ((CalendarDate)localObject).getDayOfMonth();
    }
    if (paramInt2 == MIN_VALUES[1])
    {
      localObject = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
      int k = ((CalendarDate)localObject).getMonth() - 1;
      if (i < k) {
        i = k;
      }
      if (i == k) {
        j = ((CalendarDate)localObject).getDayOfMonth();
      }
    }
    Object localObject = jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    ((LocalGregorianCalendar.Date)localObject).setEra(paramInt1 > 0 ? eras[paramInt1] : null);
    ((LocalGregorianCalendar.Date)localObject).setDate(paramInt2, i + 1, j);
    jcal.normalize((CalendarDate)localObject);
    long l1 = jcal.getFixedDate((CalendarDate)localObject);
    int i1;
    if (isFieldSet(paramInt3, 2))
    {
      if (isFieldSet(paramInt3, 5))
      {
        if (isSet(5))
        {
          l1 += internalGet(5);
          l1 -= j;
        }
      }
      else if (isFieldSet(paramInt3, 4))
      {
        long l2 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, getFirstDayOfWeek());
        if (l2 - l1 >= getMinimalDaysInFirstWeek()) {
          l2 -= 7L;
        }
        if (isFieldSet(paramInt3, 7)) {
          l2 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l2 + 6L, internalGet(7));
        }
        l1 = l2 + 7 * (internalGet(4) - 1);
      }
      else
      {
        int m;
        if (isFieldSet(paramInt3, 7)) {
          m = internalGet(7);
        } else {
          m = getFirstDayOfWeek();
        }
        int n;
        if (isFieldSet(paramInt3, 8)) {
          n = internalGet(8);
        } else {
          n = 1;
        }
        if (n >= 0)
        {
          l1 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l1 + 7 * n - 1L, m);
        }
        else
        {
          i1 = monthLength(i, paramInt2) + 7 * (n + 1);
          l1 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l1 + i1 - 1L, m);
        }
      }
    }
    else if (isFieldSet(paramInt3, 6))
    {
      if (isTransitionYear(((LocalGregorianCalendar.Date)localObject).getNormalizedYear())) {
        l1 = getFixedDateJan1((LocalGregorianCalendar.Date)localObject, l1);
      }
      l1 += internalGet(6);
      l1 -= 1L;
    }
    else
    {
      long l3 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, getFirstDayOfWeek());
      if (l3 - l1 >= getMinimalDaysInFirstWeek()) {
        l3 -= 7L;
      }
      if (isFieldSet(paramInt3, 7))
      {
        i1 = internalGet(7);
        if (i1 != getFirstDayOfWeek()) {
          l3 = LocalGregorianCalendar.getDayOfWeekDateOnOrBefore(l3 + 6L, i1);
        }
      }
      l1 = l3 + 7L * (internalGet(3) - 1L);
    }
    return l1;
  }
  
  private long getFixedDateJan1(LocalGregorianCalendar.Date paramDate, long paramLong)
  {
    Era localEra = paramDate.getEra();
    if ((paramDate.getEra() != null) && (paramDate.getYear() == 1)) {
      for (int i = getEraIndex(paramDate); i > 0; i--)
      {
        CalendarDate localCalendarDate = eras[i].getSinceDate();
        long l = gcal.getFixedDate(localCalendarDate);
        if (l <= paramLong) {
          return l;
        }
      }
    }
    Gregorian.Date localDate = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    localDate.setDate(paramDate.getNormalizedYear(), 1, 1);
    return gcal.getFixedDate(localDate);
  }
  
  private long getFixedDateMonth1(LocalGregorianCalendar.Date paramDate, long paramLong)
  {
    int i = getTransitionEraIndex(paramDate);
    if (i != -1)
    {
      long l = sinceFixedDates[i];
      if (l <= paramLong) {
        return l;
      }
    }
    return paramLong - paramDate.getDayOfMonth() + 1L;
  }
  
  private static LocalGregorianCalendar.Date getCalendarDate(long paramLong)
  {
    LocalGregorianCalendar.Date localDate = jcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    jcal.getCalendarDateFromFixedDate(localDate, paramLong);
    return localDate;
  }
  
  private int monthLength(int paramInt1, int paramInt2)
  {
    return CalendarUtils.isGregorianLeapYear(paramInt2) ? GregorianCalendar.LEAP_MONTH_LENGTH[paramInt1] : GregorianCalendar.MONTH_LENGTH[paramInt1];
  }
  
  private int monthLength(int paramInt)
  {
    assert (jdate.isNormalized());
    return jdate.isLeapYear() ? GregorianCalendar.LEAP_MONTH_LENGTH[paramInt] : GregorianCalendar.MONTH_LENGTH[paramInt];
  }
  
  private int actualMonthLength()
  {
    int i = jcal.getMonthLength(jdate);
    int j = getTransitionEraIndex(jdate);
    if (j == -1)
    {
      long l = sinceFixedDates[j];
      CalendarDate localCalendarDate = eras[j].getSinceDate();
      if (l <= cachedFixedDate) {
        i -= localCalendarDate.getDayOfMonth() - 1;
      } else {
        i = localCalendarDate.getDayOfMonth() - 1;
      }
    }
    return i;
  }
  
  private static int getTransitionEraIndex(LocalGregorianCalendar.Date paramDate)
  {
    int i = getEraIndex(paramDate);
    CalendarDate localCalendarDate = eras[i].getSinceDate();
    if ((localCalendarDate.getYear() == paramDate.getNormalizedYear()) && (localCalendarDate.getMonth() == paramDate.getMonth())) {
      return i;
    }
    if (i < eras.length - 1)
    {
      localCalendarDate = eras[(++i)].getSinceDate();
      if ((localCalendarDate.getYear() == paramDate.getNormalizedYear()) && (localCalendarDate.getMonth() == paramDate.getMonth())) {
        return i;
      }
    }
    return -1;
  }
  
  private boolean isTransitionYear(int paramInt)
  {
    for (int i = eras.length - 1; i > 0; i--)
    {
      int j = eras[i].getSinceDate().getYear();
      if (paramInt == j) {
        return true;
      }
      if (paramInt > j) {
        break;
      }
    }
    return false;
  }
  
  private static int getEraIndex(LocalGregorianCalendar.Date paramDate)
  {
    Era localEra = paramDate.getEra();
    for (int i = eras.length - 1; i > 0; i--) {
      if (eras[i] == localEra) {
        return i;
      }
    }
    return 0;
  }
  
  private JapaneseImperialCalendar getNormalizedCalendar()
  {
    JapaneseImperialCalendar localJapaneseImperialCalendar;
    if (isFullyNormalized())
    {
      localJapaneseImperialCalendar = this;
    }
    else
    {
      localJapaneseImperialCalendar = (JapaneseImperialCalendar)clone();
      localJapaneseImperialCalendar.setLenient(true);
      localJapaneseImperialCalendar.complete();
    }
    return localJapaneseImperialCalendar;
  }
  
  private void pinDayOfMonth(LocalGregorianCalendar.Date paramDate)
  {
    int i = paramDate.getYear();
    int j = paramDate.getDayOfMonth();
    if (i != getMinimum(1))
    {
      paramDate.setDayOfMonth(1);
      jcal.normalize(paramDate);
      int k = jcal.getMonthLength(paramDate);
      if (j > k) {
        paramDate.setDayOfMonth(k);
      } else {
        paramDate.setDayOfMonth(j);
      }
      jcal.normalize(paramDate);
    }
    else
    {
      LocalGregorianCalendar.Date localDate1 = jcal.getCalendarDate(Long.MIN_VALUE, getZone());
      LocalGregorianCalendar.Date localDate2 = jcal.getCalendarDate(time, getZone());
      long l = localDate2.getTimeOfDay();
      localDate2.addYear(400);
      localDate2.setMonth(paramDate.getMonth());
      localDate2.setDayOfMonth(1);
      jcal.normalize(localDate2);
      int m = jcal.getMonthLength(localDate2);
      if (j > m) {
        localDate2.setDayOfMonth(m);
      } else if (j < localDate1.getDayOfMonth()) {
        localDate2.setDayOfMonth(localDate1.getDayOfMonth());
      } else {
        localDate2.setDayOfMonth(j);
      }
      if ((localDate2.getDayOfMonth() == localDate1.getDayOfMonth()) && (l < localDate1.getTimeOfDay())) {
        localDate2.setDayOfMonth(Math.min(j + 1, m));
      }
      paramDate.setDate(i, localDate2.getMonth(), localDate2.getDayOfMonth());
    }
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
    return isSet(0) ? internalGet(0) : eras.length - 1;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (jdate == null)
    {
      jdate = jcal.newCalendarDate(getZone());
      cachedFixedDate = Long.MIN_VALUE;
    }
  }
  
  static
  {
    jcal = (LocalGregorianCalendar)CalendarSystem.forName("japanese");
    gcal = CalendarSystem.getGregorianCalendar();
    BEFORE_MEIJI_ERA = new Era("BeforeMeiji", "BM", Long.MIN_VALUE, false);
    MIN_VALUES = new int[] { 0, -292275055, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, -46800000, 0 };
    LEAST_MAX_VALUES = new int[] { 0, 0, 0, 0, 4, 28, 0, 7, 4, 1, 11, 23, 59, 59, 999, 50400000, 1200000 };
    MAX_VALUES = new int[] { 0, 292278994, 11, 53, 6, 31, 366, 7, 6, 1, 11, 23, 59, 59, 999, 50400000, 7200000 };
    Era[] arrayOfEra1 = jcal.getEras();
    int i = arrayOfEra1.length + 1;
    eras = new Era[i];
    sinceFixedDates = new long[i];
    int j = 0;
    sinceFixedDates[j] = gcal.getFixedDate(BEFORE_MEIJI_ERA.getSinceDate());
    eras[(j++)] = BEFORE_MEIJI_ERA;
    for (Era localEra : arrayOfEra1)
    {
      CalendarDate localCalendarDate1 = localEra.getSinceDate();
      sinceFixedDates[j] = gcal.getFixedDate(localCalendarDate1);
      eras[(j++)] = localEra;
    }
    LEAST_MAX_VALUES[0] = (MAX_VALUES[0] = eras.length - 1);
    int k = Integer.MAX_VALUE;
    ??? = Integer.MAX_VALUE;
    Gregorian.Date localDate = gcal.newCalendarDate(TimeZone.NO_TIMEZONE);
    for (int i1 = 1; i1 < eras.length; i1++)
    {
      long l1 = sinceFixedDates[i1];
      CalendarDate localCalendarDate2 = eras[i1].getSinceDate();
      localDate.setDate(localCalendarDate2.getYear(), 1, 1);
      long l2 = gcal.getFixedDate(localDate);
      if (l1 != l2) {
        ??? = Math.min((int)(l1 - l2) + 1, ???);
      }
      localDate.setDate(localCalendarDate2.getYear(), 12, 31);
      l2 = gcal.getFixedDate(localDate);
      if (l1 != l2) {
        ??? = Math.min((int)(l2 - l1) + 1, ???);
      }
      LocalGregorianCalendar.Date localDate1 = getCalendarDate(l1 - 1L);
      int i2 = localDate1.getYear();
      if ((localDate1.getMonth() != 1) || (localDate1.getDayOfMonth() != 1)) {
        i2--;
      }
      k = Math.min(i2, k);
    }
    LEAST_MAX_VALUES[1] = k;
    LEAST_MAX_VALUES[6] = ???;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\JapaneseImperialCalendar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */