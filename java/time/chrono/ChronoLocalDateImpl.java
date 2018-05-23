package java.time.chrono;

import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

abstract class ChronoLocalDateImpl<D extends ChronoLocalDate>
  implements ChronoLocalDate, Temporal, TemporalAdjuster, Serializable
{
  private static final long serialVersionUID = 6282433883239719096L;
  
  static <D extends ChronoLocalDate> D ensureValid(Chronology paramChronology, Temporal paramTemporal)
  {
    ChronoLocalDate localChronoLocalDate = (ChronoLocalDate)paramTemporal;
    if (!paramChronology.equals(localChronoLocalDate.getChronology())) {
      throw new ClassCastException("Chronology mismatch, expected: " + paramChronology.getId() + ", actual: " + localChronoLocalDate.getChronology().getId());
    }
    return localChronoLocalDate;
  }
  
  ChronoLocalDateImpl() {}
  
  public D with(TemporalAdjuster paramTemporalAdjuster)
  {
    return super.with(paramTemporalAdjuster);
  }
  
  public D with(TemporalField paramTemporalField, long paramLong)
  {
    return super.with(paramTemporalField, paramLong);
  }
  
  public D plus(TemporalAmount paramTemporalAmount)
  {
    return super.plus(paramTemporalAmount);
  }
  
  public D plus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    if ((paramTemporalUnit instanceof ChronoUnit))
    {
      ChronoUnit localChronoUnit = (ChronoUnit)paramTemporalUnit;
      switch (localChronoUnit)
      {
      case DAYS: 
        return plusDays(paramLong);
      case WEEKS: 
        return plusDays(Math.multiplyExact(paramLong, 7L));
      case MONTHS: 
        return plusMonths(paramLong);
      case YEARS: 
        return plusYears(paramLong);
      case DECADES: 
        return plusYears(Math.multiplyExact(paramLong, 10L));
      case CENTURIES: 
        return plusYears(Math.multiplyExact(paramLong, 100L));
      case MILLENNIA: 
        return plusYears(Math.multiplyExact(paramLong, 1000L));
      case ERAS: 
        return with(ChronoField.ERA, Math.addExact(getLong(ChronoField.ERA), paramLong));
      }
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    }
    return super.plus(paramLong, paramTemporalUnit);
  }
  
  public D minus(TemporalAmount paramTemporalAmount)
  {
    return super.minus(paramTemporalAmount);
  }
  
  public D minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return super.minus(paramLong, paramTemporalUnit);
  }
  
  abstract D plusYears(long paramLong);
  
  abstract D plusMonths(long paramLong);
  
  D plusWeeks(long paramLong)
  {
    return plusDays(Math.multiplyExact(paramLong, 7L));
  }
  
  abstract D plusDays(long paramLong);
  
  D minusYears(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? ((ChronoLocalDateImpl)plusYears(Long.MAX_VALUE)).plusYears(1L) : plusYears(-paramLong);
  }
  
  D minusMonths(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? ((ChronoLocalDateImpl)plusMonths(Long.MAX_VALUE)).plusMonths(1L) : plusMonths(-paramLong);
  }
  
  D minusWeeks(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? ((ChronoLocalDateImpl)plusWeeks(Long.MAX_VALUE)).plusWeeks(1L) : plusWeeks(-paramLong);
  }
  
  D minusDays(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? ((ChronoLocalDateImpl)plusDays(Long.MAX_VALUE)).plusDays(1L) : plusDays(-paramLong);
  }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit)
  {
    Objects.requireNonNull(paramTemporal, "endExclusive");
    ChronoLocalDate localChronoLocalDate = getChronology().date(paramTemporal);
    if ((paramTemporalUnit instanceof ChronoUnit))
    {
      switch ((ChronoUnit)paramTemporalUnit)
      {
      case DAYS: 
        return daysUntil(localChronoLocalDate);
      case WEEKS: 
        return daysUntil(localChronoLocalDate) / 7L;
      case MONTHS: 
        return monthsUntil(localChronoLocalDate);
      case YEARS: 
        return monthsUntil(localChronoLocalDate) / 12L;
      case DECADES: 
        return monthsUntil(localChronoLocalDate) / 120L;
      case CENTURIES: 
        return monthsUntil(localChronoLocalDate) / 1200L;
      case MILLENNIA: 
        return monthsUntil(localChronoLocalDate) / 12000L;
      case ERAS: 
        return localChronoLocalDate.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
      }
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
    }
    Objects.requireNonNull(paramTemporalUnit, "unit");
    return paramTemporalUnit.between(this, localChronoLocalDate);
  }
  
  private long daysUntil(ChronoLocalDate paramChronoLocalDate)
  {
    return paramChronoLocalDate.toEpochDay() - toEpochDay();
  }
  
  private long monthsUntil(ChronoLocalDate paramChronoLocalDate)
  {
    ValueRange localValueRange = getChronology().range(ChronoField.MONTH_OF_YEAR);
    if (localValueRange.getMaximum() != 12L) {
      throw new IllegalStateException("ChronoLocalDateImpl only supports Chronologies with 12 months per year");
    }
    long l1 = getLong(ChronoField.PROLEPTIC_MONTH) * 32L + get(ChronoField.DAY_OF_MONTH);
    long l2 = paramChronoLocalDate.getLong(ChronoField.PROLEPTIC_MONTH) * 32L + paramChronoLocalDate.get(ChronoField.DAY_OF_MONTH);
    return (l2 - l1) / 32L;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ChronoLocalDate)) {
      return compareTo((ChronoLocalDate)paramObject) == 0;
    }
    return false;
  }
  
  public int hashCode()
  {
    long l = toEpochDay();
    return getChronology().hashCode() ^ (int)(l ^ l >>> 32);
  }
  
  public String toString()
  {
    long l1 = getLong(ChronoField.YEAR_OF_ERA);
    long l2 = getLong(ChronoField.MONTH_OF_YEAR);
    long l3 = getLong(ChronoField.DAY_OF_MONTH);
    StringBuilder localStringBuilder = new StringBuilder(30);
    localStringBuilder.append(getChronology().toString()).append(" ").append(getEra()).append(" ").append(l1).append(l2 < 10L ? "-0" : "-").append(l2).append(l3 < 10L ? "-0" : "-").append(l3);
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\ChronoLocalDateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */