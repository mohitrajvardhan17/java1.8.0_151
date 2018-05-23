package java.time.temporal;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public final class IsoFields
{
  public static final TemporalField DAY_OF_QUARTER = Field.DAY_OF_QUARTER;
  public static final TemporalField QUARTER_OF_YEAR = Field.QUARTER_OF_YEAR;
  public static final TemporalField WEEK_OF_WEEK_BASED_YEAR = Field.WEEK_OF_WEEK_BASED_YEAR;
  public static final TemporalField WEEK_BASED_YEAR = Field.WEEK_BASED_YEAR;
  public static final TemporalUnit WEEK_BASED_YEARS = Unit.WEEK_BASED_YEARS;
  public static final TemporalUnit QUARTER_YEARS = Unit.QUARTER_YEARS;
  
  private IsoFields()
  {
    throw new AssertionError("Not instantiable");
  }
  
  private static abstract enum Field
    implements TemporalField
  {
    DAY_OF_QUARTER,  QUARTER_OF_YEAR,  WEEK_OF_WEEK_BASED_YEAR,  WEEK_BASED_YEAR;
    
    private static final int[] QUARTER_DAYS = { 0, 90, 181, 273, 0, 91, 182, 274 };
    
    private Field() {}
    
    public boolean isDateBased()
    {
      return true;
    }
    
    public boolean isTimeBased()
    {
      return false;
    }
    
    public ValueRange rangeRefinedBy(TemporalAccessor paramTemporalAccessor)
    {
      return range();
    }
    
    private static boolean isIso(TemporalAccessor paramTemporalAccessor)
    {
      return Chronology.from(paramTemporalAccessor).equals(IsoChronology.INSTANCE);
    }
    
    private static void ensureIso(TemporalAccessor paramTemporalAccessor)
    {
      if (!isIso(paramTemporalAccessor)) {
        throw new DateTimeException("Resolve requires IsoChronology");
      }
    }
    
    private static ValueRange getWeekRange(LocalDate paramLocalDate)
    {
      int i = getWeekBasedYear(paramLocalDate);
      return ValueRange.of(1L, getWeekRange(i));
    }
    
    private static int getWeekRange(int paramInt)
    {
      LocalDate localLocalDate = LocalDate.of(paramInt, 1, 1);
      if ((localLocalDate.getDayOfWeek() == DayOfWeek.THURSDAY) || ((localLocalDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) && (localLocalDate.isLeapYear()))) {
        return 53;
      }
      return 52;
    }
    
    private static int getWeek(LocalDate paramLocalDate)
    {
      int i = paramLocalDate.getDayOfWeek().ordinal();
      int j = paramLocalDate.getDayOfYear() - 1;
      int k = j + (3 - i);
      int m = k / 7;
      int n = k - m * 7;
      int i1 = n - 3;
      if (i1 < -3) {
        i1 += 7;
      }
      if (j < i1) {
        return (int)getWeekRange(paramLocalDate.withDayOfYear(180).minusYears(1L)).getMaximum();
      }
      int i2 = (j - i1) / 7 + 1;
      if (i2 == 53) {
        if (((i1 == -3) || ((i1 == -2) && (paramLocalDate.isLeapYear())) ? 1 : 0) == 0) {
          i2 = 1;
        }
      }
      return i2;
    }
    
    private static int getWeekBasedYear(LocalDate paramLocalDate)
    {
      int i = paramLocalDate.getYear();
      int j = paramLocalDate.getDayOfYear();
      int k;
      if (j <= 3)
      {
        k = paramLocalDate.getDayOfWeek().ordinal();
        if (j - k < -2) {
          i--;
        }
      }
      else if (j >= 363)
      {
        k = paramLocalDate.getDayOfWeek().ordinal();
        j = j - 363 - (paramLocalDate.isLeapYear() ? 1 : 0);
        if (j - k >= 0) {
          i++;
        }
      }
      return i;
    }
  }
  
  private static enum Unit
    implements TemporalUnit
  {
    WEEK_BASED_YEARS("WeekBasedYears", Duration.ofSeconds(31556952L)),  QUARTER_YEARS("QuarterYears", Duration.ofSeconds(7889238L));
    
    private final String name;
    private final Duration duration;
    
    private Unit(String paramString, Duration paramDuration)
    {
      name = paramString;
      duration = paramDuration;
    }
    
    public Duration getDuration()
    {
      return duration;
    }
    
    public boolean isDurationEstimated()
    {
      return true;
    }
    
    public boolean isDateBased()
    {
      return true;
    }
    
    public boolean isTimeBased()
    {
      return false;
    }
    
    public boolean isSupportedBy(Temporal paramTemporal)
    {
      return paramTemporal.isSupported(ChronoField.EPOCH_DAY);
    }
    
    public <R extends Temporal> R addTo(R paramR, long paramLong)
    {
      switch (IsoFields.1.$SwitchMap$java$time$temporal$IsoFields$Unit[ordinal()])
      {
      case 1: 
        return paramR.with(IsoFields.WEEK_BASED_YEAR, Math.addExact(paramR.get(IsoFields.WEEK_BASED_YEAR), paramLong));
      case 2: 
        return paramR.plus(paramLong / 256L, ChronoUnit.YEARS).plus(paramLong % 256L * 3L, ChronoUnit.MONTHS);
      }
      throw new IllegalStateException("Unreachable");
    }
    
    public long between(Temporal paramTemporal1, Temporal paramTemporal2)
    {
      if (paramTemporal1.getClass() != paramTemporal2.getClass()) {
        return paramTemporal1.until(paramTemporal2, this);
      }
      switch (IsoFields.1.$SwitchMap$java$time$temporal$IsoFields$Unit[ordinal()])
      {
      case 1: 
        return Math.subtractExact(paramTemporal2.getLong(IsoFields.WEEK_BASED_YEAR), paramTemporal1.getLong(IsoFields.WEEK_BASED_YEAR));
      case 2: 
        return paramTemporal1.until(paramTemporal2, ChronoUnit.MONTHS) / 3L;
      }
      throw new IllegalStateException("Unreachable");
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\IsoFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */