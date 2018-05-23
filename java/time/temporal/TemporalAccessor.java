package java.time.temporal;

import java.time.DateTimeException;
import java.util.Objects;

public abstract interface TemporalAccessor
{
  public abstract boolean isSupported(TemporalField paramTemporalField);
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      if (isSupported(paramTemporalField)) {
        return paramTemporalField.range();
      }
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    Objects.requireNonNull(paramTemporalField, "field");
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public int get(TemporalField paramTemporalField)
  {
    ValueRange localValueRange = range(paramTemporalField);
    if (!localValueRange.isIntValue()) {
      throw new UnsupportedTemporalTypeException("Invalid field " + paramTemporalField + " for get() method, use getLong() instead");
    }
    long l = getLong(paramTemporalField);
    if (!localValueRange.isValidValue(l)) {
      throw new DateTimeException("Invalid value for " + paramTemporalField + " (valid values " + localValueRange + "): " + l);
    }
    return (int)l;
  }
  
  public abstract long getLong(TemporalField paramTemporalField);
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery)
  {
    if ((paramTemporalQuery == TemporalQueries.zoneId()) || (paramTemporalQuery == TemporalQueries.chronology()) || (paramTemporalQuery == TemporalQueries.precision())) {
      return null;
    }
    return (R)paramTemporalQuery.queryFrom(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\TemporalAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */