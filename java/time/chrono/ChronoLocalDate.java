package java.time.chrono;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Comparator;
import java.util.Objects;

public abstract interface ChronoLocalDate
  extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDate>
{
  public static Comparator<ChronoLocalDate> timeLineOrder()
  {
    return AbstractChronology.DATE_ORDER;
  }
  
  public static ChronoLocalDate from(TemporalAccessor paramTemporalAccessor)
  {
    if ((paramTemporalAccessor instanceof ChronoLocalDate)) {
      return (ChronoLocalDate)paramTemporalAccessor;
    }
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology localChronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if (localChronology == null) {
      throw new DateTimeException("Unable to obtain ChronoLocalDate from TemporalAccessor: " + paramTemporalAccessor.getClass());
    }
    return localChronology.date(paramTemporalAccessor);
  }
  
  public abstract Chronology getChronology();
  
  public Era getEra()
  {
    return getChronology().eraOf(get(ChronoField.ERA));
  }
  
  public boolean isLeapYear()
  {
    return getChronology().isLeapYear(getLong(ChronoField.YEAR));
  }
  
  public abstract int lengthOfMonth();
  
  public int lengthOfYear()
  {
    return isLeapYear() ? 366 : 365;
  }
  
  public boolean isSupported(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField)) {
      return paramTemporalField.isDateBased();
    }
    return (paramTemporalField != null) && (paramTemporalField.isSupportedBy(this));
  }
  
  public boolean isSupported(TemporalUnit paramTemporalUnit)
  {
    if ((paramTemporalUnit instanceof ChronoUnit)) {
      return paramTemporalUnit.isDateBased();
    }
    return (paramTemporalUnit != null) && (paramTemporalUnit.isSupportedBy(this));
  }
  
  public ChronoLocalDate with(TemporalAdjuster paramTemporalAdjuster)
  {
    return ChronoLocalDateImpl.ensureValid(getChronology(), super.with(paramTemporalAdjuster));
  }
  
  public ChronoLocalDate with(TemporalField paramTemporalField, long paramLong)
  {
    if ((paramTemporalField instanceof ChronoField)) {
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return ChronoLocalDateImpl.ensureValid(getChronology(), paramTemporalField.adjustInto(this, paramLong));
  }
  
  public ChronoLocalDate plus(TemporalAmount paramTemporalAmount)
  {
    return ChronoLocalDateImpl.ensureValid(getChronology(), super.plus(paramTemporalAmount));
  }
  
  public ChronoLocalDate plus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    if ((paramTemporalUnit instanceof ChronoUnit)) {
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    }
    return ChronoLocalDateImpl.ensureValid(getChronology(), paramTemporalUnit.addTo(this, paramLong));
  }
  
  public ChronoLocalDate minus(TemporalAmount paramTemporalAmount)
  {
    return ChronoLocalDateImpl.ensureValid(getChronology(), super.minus(paramTemporalAmount));
  }
  
  public ChronoLocalDate minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return ChronoLocalDateImpl.ensureValid(getChronology(), super.minus(paramLong, paramTemporalUnit));
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery)
  {
    if ((paramTemporalQuery == TemporalQueries.zoneId()) || (paramTemporalQuery == TemporalQueries.zone()) || (paramTemporalQuery == TemporalQueries.offset())) {
      return null;
    }
    if (paramTemporalQuery == TemporalQueries.localTime()) {
      return null;
    }
    if (paramTemporalQuery == TemporalQueries.chronology()) {
      return getChronology();
    }
    if (paramTemporalQuery == TemporalQueries.precision()) {
      return ChronoUnit.DAYS;
    }
    return (R)paramTemporalQuery.queryFrom(this);
  }
  
  public Temporal adjustInto(Temporal paramTemporal)
  {
    return paramTemporal.with(ChronoField.EPOCH_DAY, toEpochDay());
  }
  
  public abstract long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit);
  
  public abstract ChronoPeriod until(ChronoLocalDate paramChronoLocalDate);
  
  public String format(DateTimeFormatter paramDateTimeFormatter)
  {
    Objects.requireNonNull(paramDateTimeFormatter, "formatter");
    return paramDateTimeFormatter.format(this);
  }
  
  public ChronoLocalDateTime<?> atTime(LocalTime paramLocalTime)
  {
    return ChronoLocalDateTimeImpl.of(this, paramLocalTime);
  }
  
  public long toEpochDay()
  {
    return getLong(ChronoField.EPOCH_DAY);
  }
  
  public int compareTo(ChronoLocalDate paramChronoLocalDate)
  {
    int i = Long.compare(toEpochDay(), paramChronoLocalDate.toEpochDay());
    if (i == 0) {
      i = getChronology().compareTo(paramChronoLocalDate.getChronology());
    }
    return i;
  }
  
  public boolean isAfter(ChronoLocalDate paramChronoLocalDate)
  {
    return toEpochDay() > paramChronoLocalDate.toEpochDay();
  }
  
  public boolean isBefore(ChronoLocalDate paramChronoLocalDate)
  {
    return toEpochDay() < paramChronoLocalDate.toEpochDay();
  }
  
  public boolean isEqual(ChronoLocalDate paramChronoLocalDate)
  {
    return toEpochDay() == paramChronoLocalDate.toEpochDay();
  }
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\ChronoLocalDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */