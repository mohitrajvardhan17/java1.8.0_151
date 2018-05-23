package java.time.temporal;

import java.time.DateTimeException;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.util.Map;

public final class JulianFields
{
  private static final long JULIAN_DAY_OFFSET = 2440588L;
  public static final TemporalField JULIAN_DAY = Field.JULIAN_DAY;
  public static final TemporalField MODIFIED_JULIAN_DAY = Field.MODIFIED_JULIAN_DAY;
  public static final TemporalField RATA_DIE = Field.RATA_DIE;
  
  private JulianFields()
  {
    throw new AssertionError("Not instantiable");
  }
  
  private static enum Field
    implements TemporalField
  {
    JULIAN_DAY("JulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 2440588L),  MODIFIED_JULIAN_DAY("ModifiedJulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 40587L),  RATA_DIE("RataDie", ChronoUnit.DAYS, ChronoUnit.FOREVER, 719163L);
    
    private static final long serialVersionUID = -7501623920830201812L;
    private final transient String name;
    private final transient TemporalUnit baseUnit;
    private final transient TemporalUnit rangeUnit;
    private final transient ValueRange range;
    private final transient long offset;
    
    private Field(String paramString, TemporalUnit paramTemporalUnit1, TemporalUnit paramTemporalUnit2, long paramLong)
    {
      name = paramString;
      baseUnit = paramTemporalUnit1;
      rangeUnit = paramTemporalUnit2;
      range = ValueRange.of(-365243219162L + paramLong, 365241780471L + paramLong);
      offset = paramLong;
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
      return paramTemporalAccessor.isSupported(ChronoField.EPOCH_DAY);
    }
    
    public ValueRange rangeRefinedBy(TemporalAccessor paramTemporalAccessor)
    {
      if (!isSupportedBy(paramTemporalAccessor)) {
        throw new DateTimeException("Unsupported field: " + this);
      }
      return range();
    }
    
    public long getFrom(TemporalAccessor paramTemporalAccessor)
    {
      return paramTemporalAccessor.getLong(ChronoField.EPOCH_DAY) + offset;
    }
    
    public <R extends Temporal> R adjustInto(R paramR, long paramLong)
    {
      if (!range().isValidValue(paramLong)) {
        throw new DateTimeException("Invalid value: " + name + " " + paramLong);
      }
      return paramR.with(ChronoField.EPOCH_DAY, Math.subtractExact(paramLong, offset));
    }
    
    public ChronoLocalDate resolve(Map<TemporalField, Long> paramMap, TemporalAccessor paramTemporalAccessor, ResolverStyle paramResolverStyle)
    {
      long l = ((Long)paramMap.remove(this)).longValue();
      Chronology localChronology = Chronology.from(paramTemporalAccessor);
      if (paramResolverStyle == ResolverStyle.LENIENT) {
        return localChronology.dateEpochDay(Math.subtractExact(l, offset));
      }
      range().checkValidValue(l, this);
      return localChronology.dateEpochDay(l - offset);
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\JulianFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */