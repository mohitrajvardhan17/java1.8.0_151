package java.time.temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public final class WeekFields
  implements Serializable
{
  private static final ConcurrentMap<String, WeekFields> CACHE = new ConcurrentHashMap(4, 0.75F, 2);
  public static final WeekFields ISO = new WeekFields(DayOfWeek.MONDAY, 4);
  public static final WeekFields SUNDAY_START = of(DayOfWeek.SUNDAY, 1);
  public static final TemporalUnit WEEK_BASED_YEARS = IsoFields.WEEK_BASED_YEARS;
  private static final long serialVersionUID = -1177360819670808121L;
  private final DayOfWeek firstDayOfWeek;
  private final int minimalDays;
  private final transient TemporalField dayOfWeek = ComputedDayOfField.ofDayOfWeekField(this);
  private final transient TemporalField weekOfMonth = ComputedDayOfField.ofWeekOfMonthField(this);
  private final transient TemporalField weekOfYear = ComputedDayOfField.ofWeekOfYearField(this);
  private final transient TemporalField weekOfWeekBasedYear = ComputedDayOfField.ofWeekOfWeekBasedYearField(this);
  private final transient TemporalField weekBasedYear = ComputedDayOfField.ofWeekBasedYearField(this);
  
  public static WeekFields of(Locale paramLocale)
  {
    Objects.requireNonNull(paramLocale, "locale");
    paramLocale = new Locale(paramLocale.getLanguage(), paramLocale.getCountry());
    int i = CalendarDataUtility.retrieveFirstDayOfWeek(paramLocale);
    DayOfWeek localDayOfWeek = DayOfWeek.SUNDAY.plus(i - 1);
    int j = CalendarDataUtility.retrieveMinimalDaysInFirstWeek(paramLocale);
    return of(localDayOfWeek, j);
  }
  
  public static WeekFields of(DayOfWeek paramDayOfWeek, int paramInt)
  {
    String str = paramDayOfWeek.toString() + paramInt;
    WeekFields localWeekFields = (WeekFields)CACHE.get(str);
    if (localWeekFields == null)
    {
      localWeekFields = new WeekFields(paramDayOfWeek, paramInt);
      CACHE.putIfAbsent(str, localWeekFields);
      localWeekFields = (WeekFields)CACHE.get(str);
    }
    return localWeekFields;
  }
  
  private WeekFields(DayOfWeek paramDayOfWeek, int paramInt)
  {
    Objects.requireNonNull(paramDayOfWeek, "firstDayOfWeek");
    if ((paramInt < 1) || (paramInt > 7)) {
      throw new IllegalArgumentException("Minimal number of days is invalid");
    }
    firstDayOfWeek = paramDayOfWeek;
    minimalDays = paramInt;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException, InvalidObjectException
  {
    paramObjectInputStream.defaultReadObject();
    if (firstDayOfWeek == null) {
      throw new InvalidObjectException("firstDayOfWeek is null");
    }
    if ((minimalDays < 1) || (minimalDays > 7)) {
      throw new InvalidObjectException("Minimal number of days is invalid");
    }
  }
  
  private Object readResolve()
    throws InvalidObjectException
  {
    try
    {
      return of(firstDayOfWeek, minimalDays);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new InvalidObjectException("Invalid serialized WeekFields: " + localIllegalArgumentException.getMessage());
    }
  }
  
  public DayOfWeek getFirstDayOfWeek()
  {
    return firstDayOfWeek;
  }
  
  public int getMinimalDaysInFirstWeek()
  {
    return minimalDays;
  }
  
  public TemporalField dayOfWeek()
  {
    return dayOfWeek;
  }
  
  public TemporalField weekOfMonth()
  {
    return weekOfMonth;
  }
  
  public TemporalField weekOfYear()
  {
    return weekOfYear;
  }
  
  public TemporalField weekOfWeekBasedYear()
  {
    return weekOfWeekBasedYear;
  }
  
  public TemporalField weekBasedYear()
  {
    return weekBasedYear;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof WeekFields)) {
      return hashCode() == paramObject.hashCode();
    }
    return false;
  }
  
  public int hashCode()
  {
    return firstDayOfWeek.ordinal() * 7 + minimalDays;
  }
  
  public String toString()
  {
    return "WeekFields[" + firstDayOfWeek + ',' + minimalDays + ']';
  }
  
  static class ComputedDayOfField
    implements TemporalField
  {
    private final String name;
    private final WeekFields weekDef;
    private final TemporalUnit baseUnit;
    private final TemporalUnit rangeUnit;
    private final ValueRange range;
    private static final ValueRange DAY_OF_WEEK_RANGE = ValueRange.of(1L, 7L);
    private static final ValueRange WEEK_OF_MONTH_RANGE = ValueRange.of(0L, 1L, 4L, 6L);
    private static final ValueRange WEEK_OF_YEAR_RANGE = ValueRange.of(0L, 1L, 52L, 54L);
    private static final ValueRange WEEK_OF_WEEK_BASED_YEAR_RANGE = ValueRange.of(1L, 52L, 53L);
    
    static ComputedDayOfField ofDayOfWeekField(WeekFields paramWeekFields)
    {
      return new ComputedDayOfField("DayOfWeek", paramWeekFields, ChronoUnit.DAYS, ChronoUnit.WEEKS, DAY_OF_WEEK_RANGE);
    }
    
    static ComputedDayOfField ofWeekOfMonthField(WeekFields paramWeekFields)
    {
      return new ComputedDayOfField("WeekOfMonth", paramWeekFields, ChronoUnit.WEEKS, ChronoUnit.MONTHS, WEEK_OF_MONTH_RANGE);
    }
    
    static ComputedDayOfField ofWeekOfYearField(WeekFields paramWeekFields)
    {
      return new ComputedDayOfField("WeekOfYear", paramWeekFields, ChronoUnit.WEEKS, ChronoUnit.YEARS, WEEK_OF_YEAR_RANGE);
    }
    
    static ComputedDayOfField ofWeekOfWeekBasedYearField(WeekFields paramWeekFields)
    {
      return new ComputedDayOfField("WeekOfWeekBasedYear", paramWeekFields, ChronoUnit.WEEKS, IsoFields.WEEK_BASED_YEARS, WEEK_OF_WEEK_BASED_YEAR_RANGE);
    }
    
    static ComputedDayOfField ofWeekBasedYearField(WeekFields paramWeekFields)
    {
      return new ComputedDayOfField("WeekBasedYear", paramWeekFields, IsoFields.WEEK_BASED_YEARS, ChronoUnit.FOREVER, ChronoField.YEAR.range());
    }
    
    private ChronoLocalDate ofWeekBasedYear(Chronology paramChronology, int paramInt1, int paramInt2, int paramInt3)
    {
      ChronoLocalDate localChronoLocalDate = paramChronology.date(paramInt1, 1, 1);
      int i = localizedDayOfWeek(localChronoLocalDate);
      int j = startOfWeekOffset(1, i);
      int k = localChronoLocalDate.lengthOfYear();
      int m = computeWeek(j, k + weekDef.getMinimalDaysInFirstWeek());
      paramInt2 = Math.min(paramInt2, m - 1);
      int n = -j + (paramInt3 - 1) + (paramInt2 - 1) * 7;
      return localChronoLocalDate.plus(n, ChronoUnit.DAYS);
    }
    
    private ComputedDayOfField(String paramString, WeekFields paramWeekFields, TemporalUnit paramTemporalUnit1, TemporalUnit paramTemporalUnit2, ValueRange paramValueRange)
    {
      name = paramString;
      weekDef = paramWeekFields;
      baseUnit = paramTemporalUnit1;
      rangeUnit = paramTemporalUnit2;
      range = paramValueRange;
    }
    
    public long getFrom(TemporalAccessor paramTemporalAccessor)
    {
      if (rangeUnit == ChronoUnit.WEEKS) {
        return localizedDayOfWeek(paramTemporalAccessor);
      }
      if (rangeUnit == ChronoUnit.MONTHS) {
        return localizedWeekOfMonth(paramTemporalAccessor);
      }
      if (rangeUnit == ChronoUnit.YEARS) {
        return localizedWeekOfYear(paramTemporalAccessor);
      }
      if (rangeUnit == WeekFields.WEEK_BASED_YEARS) {
        return localizedWeekOfWeekBasedYear(paramTemporalAccessor);
      }
      if (rangeUnit == ChronoUnit.FOREVER) {
        return localizedWeekBasedYear(paramTemporalAccessor);
      }
      throw new IllegalStateException("unreachable, rangeUnit: " + rangeUnit + ", this: " + this);
    }
    
    private int localizedDayOfWeek(TemporalAccessor paramTemporalAccessor)
    {
      int i = weekDef.getFirstDayOfWeek().getValue();
      int j = paramTemporalAccessor.get(ChronoField.DAY_OF_WEEK);
      return Math.floorMod(j - i, 7) + 1;
    }
    
    private int localizedDayOfWeek(int paramInt)
    {
      int i = weekDef.getFirstDayOfWeek().getValue();
      return Math.floorMod(paramInt - i, 7) + 1;
    }
    
    private long localizedWeekOfMonth(TemporalAccessor paramTemporalAccessor)
    {
      int i = localizedDayOfWeek(paramTemporalAccessor);
      int j = paramTemporalAccessor.get(ChronoField.DAY_OF_MONTH);
      int k = startOfWeekOffset(j, i);
      return computeWeek(k, j);
    }
    
    private long localizedWeekOfYear(TemporalAccessor paramTemporalAccessor)
    {
      int i = localizedDayOfWeek(paramTemporalAccessor);
      int j = paramTemporalAccessor.get(ChronoField.DAY_OF_YEAR);
      int k = startOfWeekOffset(j, i);
      return computeWeek(k, j);
    }
    
    private int localizedWeekBasedYear(TemporalAccessor paramTemporalAccessor)
    {
      int i = localizedDayOfWeek(paramTemporalAccessor);
      int j = paramTemporalAccessor.get(ChronoField.YEAR);
      int k = paramTemporalAccessor.get(ChronoField.DAY_OF_YEAR);
      int m = startOfWeekOffset(k, i);
      int n = computeWeek(m, k);
      if (n == 0) {
        return j - 1;
      }
      ValueRange localValueRange = paramTemporalAccessor.range(ChronoField.DAY_OF_YEAR);
      int i1 = (int)localValueRange.getMaximum();
      int i2 = computeWeek(m, i1 + weekDef.getMinimalDaysInFirstWeek());
      if (n >= i2) {
        return j + 1;
      }
      return j;
    }
    
    private int localizedWeekOfWeekBasedYear(TemporalAccessor paramTemporalAccessor)
    {
      int i = localizedDayOfWeek(paramTemporalAccessor);
      int j = paramTemporalAccessor.get(ChronoField.DAY_OF_YEAR);
      int k = startOfWeekOffset(j, i);
      int m = computeWeek(k, j);
      Object localObject;
      if (m == 0)
      {
        localObject = Chronology.from(paramTemporalAccessor).date(paramTemporalAccessor);
        localObject = ((ChronoLocalDate)localObject).minus(j, ChronoUnit.DAYS);
        return localizedWeekOfWeekBasedYear((TemporalAccessor)localObject);
      }
      if (m > 50)
      {
        localObject = paramTemporalAccessor.range(ChronoField.DAY_OF_YEAR);
        int n = (int)((ValueRange)localObject).getMaximum();
        int i1 = computeWeek(k, n + weekDef.getMinimalDaysInFirstWeek());
        if (m >= i1) {
          m = m - i1 + 1;
        }
      }
      return m;
    }
    
    private int startOfWeekOffset(int paramInt1, int paramInt2)
    {
      int i = Math.floorMod(paramInt1 - paramInt2, 7);
      int j = -i;
      if (i + 1 > weekDef.getMinimalDaysInFirstWeek()) {
        j = 7 - i;
      }
      return j;
    }
    
    private int computeWeek(int paramInt1, int paramInt2)
    {
      return (7 + paramInt1 + (paramInt2 - 1)) / 7;
    }
    
    public <R extends Temporal> R adjustInto(R paramR, long paramLong)
    {
      int i = range.checkValidIntValue(paramLong, this);
      int j = paramR.get(this);
      if (i == j) {
        return paramR;
      }
      if (rangeUnit == ChronoUnit.FOREVER)
      {
        int k = paramR.get(weekDef.dayOfWeek);
        int m = paramR.get(weekDef.weekOfWeekBasedYear);
        return ofWeekBasedYear(Chronology.from(paramR), (int)paramLong, m, k);
      }
      return paramR.plus(i - j, baseUnit);
    }
    
    public ChronoLocalDate resolve(Map<TemporalField, Long> paramMap, TemporalAccessor paramTemporalAccessor, ResolverStyle paramResolverStyle)
    {
      long l1 = ((Long)paramMap.get(this)).longValue();
      int i = Math.toIntExact(l1);
      if (rangeUnit == ChronoUnit.WEEKS)
      {
        j = range.checkValidIntValue(l1, this);
        k = weekDef.getFirstDayOfWeek().getValue();
        long l2 = Math.floorMod(k - 1 + (j - 1), 7) + 1;
        paramMap.remove(this);
        paramMap.put(ChronoField.DAY_OF_WEEK, Long.valueOf(l2));
        return null;
      }
      if (!paramMap.containsKey(ChronoField.DAY_OF_WEEK)) {
        return null;
      }
      int j = ChronoField.DAY_OF_WEEK.checkValidIntValue(((Long)paramMap.get(ChronoField.DAY_OF_WEEK)).longValue());
      int k = localizedDayOfWeek(j);
      Chronology localChronology = Chronology.from(paramTemporalAccessor);
      if (paramMap.containsKey(ChronoField.YEAR))
      {
        int m = ChronoField.YEAR.checkValidIntValue(((Long)paramMap.get(ChronoField.YEAR)).longValue());
        if ((rangeUnit == ChronoUnit.MONTHS) && (paramMap.containsKey(ChronoField.MONTH_OF_YEAR)))
        {
          long l3 = ((Long)paramMap.get(ChronoField.MONTH_OF_YEAR)).longValue();
          return resolveWoM(paramMap, localChronology, m, l3, i, k, paramResolverStyle);
        }
        if (rangeUnit == ChronoUnit.YEARS) {
          return resolveWoY(paramMap, localChronology, m, i, k, paramResolverStyle);
        }
      }
      else if (((rangeUnit == WeekFields.WEEK_BASED_YEARS) || (rangeUnit == ChronoUnit.FOREVER)) && (paramMap.containsKey(weekDef.weekBasedYear)) && (paramMap.containsKey(weekDef.weekOfWeekBasedYear)))
      {
        return resolveWBY(paramMap, localChronology, k, paramResolverStyle);
      }
      return null;
    }
    
    private ChronoLocalDate resolveWoM(Map<TemporalField, Long> paramMap, Chronology paramChronology, int paramInt1, long paramLong1, long paramLong2, int paramInt2, ResolverStyle paramResolverStyle)
    {
      ChronoLocalDate localChronoLocalDate;
      int k;
      if (paramResolverStyle == ResolverStyle.LENIENT)
      {
        localChronoLocalDate = paramChronology.date(paramInt1, 1, 1).plus(Math.subtractExact(paramLong1, 1L), ChronoUnit.MONTHS);
        long l = Math.subtractExact(paramLong2, localizedWeekOfMonth(localChronoLocalDate));
        k = paramInt2 - localizedDayOfWeek(localChronoLocalDate);
        localChronoLocalDate = localChronoLocalDate.plus(Math.addExact(Math.multiplyExact(l, 7L), k), ChronoUnit.DAYS);
      }
      else
      {
        int i = ChronoField.MONTH_OF_YEAR.checkValidIntValue(paramLong1);
        localChronoLocalDate = paramChronology.date(paramInt1, i, 1);
        int j = range.checkValidIntValue(paramLong2, this);
        k = (int)(j - localizedWeekOfMonth(localChronoLocalDate));
        int m = paramInt2 - localizedDayOfWeek(localChronoLocalDate);
        localChronoLocalDate = localChronoLocalDate.plus(k * 7 + m, ChronoUnit.DAYS);
        if ((paramResolverStyle == ResolverStyle.STRICT) && (localChronoLocalDate.getLong(ChronoField.MONTH_OF_YEAR) != paramLong1)) {
          throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
        }
      }
      paramMap.remove(this);
      paramMap.remove(ChronoField.YEAR);
      paramMap.remove(ChronoField.MONTH_OF_YEAR);
      paramMap.remove(ChronoField.DAY_OF_WEEK);
      return localChronoLocalDate;
    }
    
    private ChronoLocalDate resolveWoY(Map<TemporalField, Long> paramMap, Chronology paramChronology, int paramInt1, long paramLong, int paramInt2, ResolverStyle paramResolverStyle)
    {
      ChronoLocalDate localChronoLocalDate = paramChronology.date(paramInt1, 1, 1);
      int k;
      if (paramResolverStyle == ResolverStyle.LENIENT)
      {
        long l = Math.subtractExact(paramLong, localizedWeekOfYear(localChronoLocalDate));
        k = paramInt2 - localizedDayOfWeek(localChronoLocalDate);
        localChronoLocalDate = localChronoLocalDate.plus(Math.addExact(Math.multiplyExact(l, 7L), k), ChronoUnit.DAYS);
      }
      else
      {
        int i = range.checkValidIntValue(paramLong, this);
        int j = (int)(i - localizedWeekOfYear(localChronoLocalDate));
        k = paramInt2 - localizedDayOfWeek(localChronoLocalDate);
        localChronoLocalDate = localChronoLocalDate.plus(j * 7 + k, ChronoUnit.DAYS);
        if ((paramResolverStyle == ResolverStyle.STRICT) && (localChronoLocalDate.getLong(ChronoField.YEAR) != paramInt1)) {
          throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
        }
      }
      paramMap.remove(this);
      paramMap.remove(ChronoField.YEAR);
      paramMap.remove(ChronoField.DAY_OF_WEEK);
      return localChronoLocalDate;
    }
    
    private ChronoLocalDate resolveWBY(Map<TemporalField, Long> paramMap, Chronology paramChronology, int paramInt, ResolverStyle paramResolverStyle)
    {
      int i = weekDef.weekBasedYear.range().checkValidIntValue(((Long)paramMap.get(weekDef.weekBasedYear)).longValue(), weekDef.weekBasedYear);
      ChronoLocalDate localChronoLocalDate;
      if (paramResolverStyle == ResolverStyle.LENIENT)
      {
        localChronoLocalDate = ofWeekBasedYear(paramChronology, i, 1, paramInt);
        long l1 = ((Long)paramMap.get(weekDef.weekOfWeekBasedYear)).longValue();
        long l2 = Math.subtractExact(l1, 1L);
        localChronoLocalDate = localChronoLocalDate.plus(l2, ChronoUnit.WEEKS);
      }
      else
      {
        int j = weekDef.weekOfWeekBasedYear.range().checkValidIntValue(((Long)paramMap.get(weekDef.weekOfWeekBasedYear)).longValue(), weekDef.weekOfWeekBasedYear);
        localChronoLocalDate = ofWeekBasedYear(paramChronology, i, j, paramInt);
        if ((paramResolverStyle == ResolverStyle.STRICT) && (localizedWeekBasedYear(localChronoLocalDate) != i)) {
          throw new DateTimeException("Strict mode rejected resolved date as it is in a different week-based-year");
        }
      }
      paramMap.remove(this);
      paramMap.remove(weekDef.weekBasedYear);
      paramMap.remove(weekDef.weekOfWeekBasedYear);
      paramMap.remove(ChronoField.DAY_OF_WEEK);
      return localChronoLocalDate;
    }
    
    public String getDisplayName(Locale paramLocale)
    {
      Objects.requireNonNull(paramLocale, "locale");
      if (rangeUnit == ChronoUnit.YEARS)
      {
        LocaleResources localLocaleResources = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(paramLocale);
        ResourceBundle localResourceBundle = localLocaleResources.getJavaTimeFormatData();
        return localResourceBundle.containsKey("field.week") ? localResourceBundle.getString("field.week") : name;
      }
      return name;
    }
    
    public TemporalUnit getBaseUnit()
    {
      return baseUnit;
    }
    
    public TemporalUnit getRangeUnit()
    {
      return rangeUnit;
    }
    
    public boolean isDateBased()
    {
      return true;
    }
    
    public boolean isTimeBased()
    {
      return false;
    }
    
    public ValueRange range()
    {
      return range;
    }
    
    public boolean isSupportedBy(TemporalAccessor paramTemporalAccessor)
    {
      if (paramTemporalAccessor.isSupported(ChronoField.DAY_OF_WEEK))
      {
        if (rangeUnit == ChronoUnit.WEEKS) {
          return true;
        }
        if (rangeUnit == ChronoUnit.MONTHS) {
          return paramTemporalAccessor.isSupported(ChronoField.DAY_OF_MONTH);
        }
        if (rangeUnit == ChronoUnit.YEARS) {
          return paramTemporalAccessor.isSupported(ChronoField.DAY_OF_YEAR);
        }
        if (rangeUnit == WeekFields.WEEK_BASED_YEARS) {
          return paramTemporalAccessor.isSupported(ChronoField.DAY_OF_YEAR);
        }
        if (rangeUnit == ChronoUnit.FOREVER) {
          return paramTemporalAccessor.isSupported(ChronoField.YEAR);
        }
      }
      return false;
    }
    
    public ValueRange rangeRefinedBy(TemporalAccessor paramTemporalAccessor)
    {
      if (rangeUnit == ChronoUnit.WEEKS) {
        return range;
      }
      if (rangeUnit == ChronoUnit.MONTHS) {
        return rangeByWeek(paramTemporalAccessor, ChronoField.DAY_OF_MONTH);
      }
      if (rangeUnit == ChronoUnit.YEARS) {
        return rangeByWeek(paramTemporalAccessor, ChronoField.DAY_OF_YEAR);
      }
      if (rangeUnit == WeekFields.WEEK_BASED_YEARS) {
        return rangeWeekOfWeekBasedYear(paramTemporalAccessor);
      }
      if (rangeUnit == ChronoUnit.FOREVER) {
        return ChronoField.YEAR.range();
      }
      throw new IllegalStateException("unreachable, rangeUnit: " + rangeUnit + ", this: " + this);
    }
    
    private ValueRange rangeByWeek(TemporalAccessor paramTemporalAccessor, TemporalField paramTemporalField)
    {
      int i = localizedDayOfWeek(paramTemporalAccessor);
      int j = startOfWeekOffset(paramTemporalAccessor.get(paramTemporalField), i);
      ValueRange localValueRange = paramTemporalAccessor.range(paramTemporalField);
      return ValueRange.of(computeWeek(j, (int)localValueRange.getMinimum()), computeWeek(j, (int)localValueRange.getMaximum()));
    }
    
    private ValueRange rangeWeekOfWeekBasedYear(TemporalAccessor paramTemporalAccessor)
    {
      if (!paramTemporalAccessor.isSupported(ChronoField.DAY_OF_YEAR)) {
        return WEEK_OF_YEAR_RANGE;
      }
      int i = localizedDayOfWeek(paramTemporalAccessor);
      int j = paramTemporalAccessor.get(ChronoField.DAY_OF_YEAR);
      int k = startOfWeekOffset(j, i);
      int m = computeWeek(k, j);
      if (m == 0)
      {
        localObject = Chronology.from(paramTemporalAccessor).date(paramTemporalAccessor);
        localObject = ((ChronoLocalDate)localObject).minus(j + 7, ChronoUnit.DAYS);
        return rangeWeekOfWeekBasedYear((TemporalAccessor)localObject);
      }
      Object localObject = paramTemporalAccessor.range(ChronoField.DAY_OF_YEAR);
      int n = (int)((ValueRange)localObject).getMaximum();
      int i1 = computeWeek(k, n + weekDef.getMinimalDaysInFirstWeek());
      if (m >= i1)
      {
        ChronoLocalDate localChronoLocalDate = Chronology.from(paramTemporalAccessor).date(paramTemporalAccessor);
        localChronoLocalDate = localChronoLocalDate.plus(n - j + 1 + 7, ChronoUnit.DAYS);
        return rangeWeekOfWeekBasedYear(localChronoLocalDate);
      }
      return ValueRange.of(1L, i1 - 1);
    }
    
    public String toString()
    {
      return name + "[" + weekDef.toString() + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\WeekFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */