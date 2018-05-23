package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Objects;

final class ChronoLocalDateTimeImpl<D extends ChronoLocalDate>
  implements ChronoLocalDateTime<D>, Temporal, TemporalAdjuster, Serializable
{
  private static final long serialVersionUID = 4556003607393004514L;
  static final int HOURS_PER_DAY = 24;
  static final int MINUTES_PER_HOUR = 60;
  static final int MINUTES_PER_DAY = 1440;
  static final int SECONDS_PER_MINUTE = 60;
  static final int SECONDS_PER_HOUR = 3600;
  static final int SECONDS_PER_DAY = 86400;
  static final long MILLIS_PER_DAY = 86400000L;
  static final long MICROS_PER_DAY = 86400000000L;
  static final long NANOS_PER_SECOND = 1000000000L;
  static final long NANOS_PER_MINUTE = 60000000000L;
  static final long NANOS_PER_HOUR = 3600000000000L;
  static final long NANOS_PER_DAY = 86400000000000L;
  private final transient D date;
  private final transient LocalTime time;
  
  static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> of(R paramR, LocalTime paramLocalTime)
  {
    return new ChronoLocalDateTimeImpl(paramR, paramLocalTime);
  }
  
  static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> ensureValid(Chronology paramChronology, Temporal paramTemporal)
  {
    ChronoLocalDateTimeImpl localChronoLocalDateTimeImpl = (ChronoLocalDateTimeImpl)paramTemporal;
    if (!paramChronology.equals(localChronoLocalDateTimeImpl.getChronology())) {
      throw new ClassCastException("Chronology mismatch, required: " + paramChronology.getId() + ", actual: " + localChronoLocalDateTimeImpl.getChronology().getId());
    }
    return localChronoLocalDateTimeImpl;
  }
  
  private ChronoLocalDateTimeImpl(D paramD, LocalTime paramLocalTime)
  {
    Objects.requireNonNull(paramD, "date");
    Objects.requireNonNull(paramLocalTime, "time");
    date = paramD;
    time = paramLocalTime;
  }
  
  private ChronoLocalDateTimeImpl<D> with(Temporal paramTemporal, LocalTime paramLocalTime)
  {
    if ((date == paramTemporal) && (time == paramLocalTime)) {
      return this;
    }
    ChronoLocalDate localChronoLocalDate = ChronoLocalDateImpl.ensureValid(date.getChronology(), paramTemporal);
    return new ChronoLocalDateTimeImpl(localChronoLocalDate, paramLocalTime);
  }
  
  public D toLocalDate()
  {
    return date;
  }
  
  public LocalTime toLocalTime()
  {
    return time;
  }
  
  public boolean isSupported(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      return (localChronoField.isDateBased()) || (localChronoField.isTimeBased());
    }
    return (paramTemporalField != null) && (paramTemporalField.isSupportedBy(this));
  }
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      return localChronoField.isTimeBased() ? time.range(paramTemporalField) : date.range(paramTemporalField);
    }
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public int get(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      return localChronoField.isTimeBased() ? time.get(paramTemporalField) : date.get(paramTemporalField);
    }
    return range(paramTemporalField).checkValidIntValue(getLong(paramTemporalField), paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      return localChronoField.isTimeBased() ? time.getLong(paramTemporalField) : date.getLong(paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  public ChronoLocalDateTimeImpl<D> with(TemporalAdjuster paramTemporalAdjuster)
  {
    if ((paramTemporalAdjuster instanceof ChronoLocalDate)) {
      return with((ChronoLocalDate)paramTemporalAdjuster, time);
    }
    if ((paramTemporalAdjuster instanceof LocalTime)) {
      return with(date, (LocalTime)paramTemporalAdjuster);
    }
    if ((paramTemporalAdjuster instanceof ChronoLocalDateTimeImpl)) {
      return ensureValid(date.getChronology(), (ChronoLocalDateTimeImpl)paramTemporalAdjuster);
    }
    return ensureValid(date.getChronology(), (ChronoLocalDateTimeImpl)paramTemporalAdjuster.adjustInto(this));
  }
  
  public ChronoLocalDateTimeImpl<D> with(TemporalField paramTemporalField, long paramLong)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      if (localChronoField.isTimeBased()) {
        return with(date, time.with(paramTemporalField, paramLong));
      }
      return with(date.with(paramTemporalField, paramLong), time);
    }
    return ensureValid(date.getChronology(), paramTemporalField.adjustInto(this, paramLong));
  }
  
  public ChronoLocalDateTimeImpl<D> plus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    if ((paramTemporalUnit instanceof ChronoUnit))
    {
      ChronoUnit localChronoUnit = (ChronoUnit)paramTemporalUnit;
      switch (localChronoUnit)
      {
      case NANOS: 
        return plusNanos(paramLong);
      case MICROS: 
        return plusDays(paramLong / 86400000000L).plusNanos(paramLong % 86400000000L * 1000L);
      case MILLIS: 
        return plusDays(paramLong / 86400000L).plusNanos(paramLong % 86400000L * 1000000L);
      case SECONDS: 
        return plusSeconds(paramLong);
      case MINUTES: 
        return plusMinutes(paramLong);
      case HOURS: 
        return plusHours(paramLong);
      case HALF_DAYS: 
        return plusDays(paramLong / 256L).plusHours(paramLong % 256L * 12L);
      }
      return with(date.plus(paramLong, paramTemporalUnit), time);
    }
    return ensureValid(date.getChronology(), paramTemporalUnit.addTo(this, paramLong));
  }
  
  private ChronoLocalDateTimeImpl<D> plusDays(long paramLong)
  {
    return with(date.plus(paramLong, ChronoUnit.DAYS), time);
  }
  
  private ChronoLocalDateTimeImpl<D> plusHours(long paramLong)
  {
    return plusWithOverflow(date, paramLong, 0L, 0L, 0L);
  }
  
  private ChronoLocalDateTimeImpl<D> plusMinutes(long paramLong)
  {
    return plusWithOverflow(date, 0L, paramLong, 0L, 0L);
  }
  
  ChronoLocalDateTimeImpl<D> plusSeconds(long paramLong)
  {
    return plusWithOverflow(date, 0L, 0L, paramLong, 0L);
  }
  
  private ChronoLocalDateTimeImpl<D> plusNanos(long paramLong)
  {
    return plusWithOverflow(date, 0L, 0L, 0L, paramLong);
  }
  
  private ChronoLocalDateTimeImpl<D> plusWithOverflow(D paramD, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    if ((paramLong1 | paramLong2 | paramLong3 | paramLong4) == 0L) {
      return with(paramD, time);
    }
    long l1 = paramLong4 / 86400000000000L + paramLong3 / 86400L + paramLong2 / 1440L + paramLong1 / 24L;
    long l2 = paramLong4 % 86400000000000L + paramLong3 % 86400L * 1000000000L + paramLong2 % 1440L * 60000000000L + paramLong1 % 24L * 3600000000000L;
    long l3 = time.toNanoOfDay();
    l2 += l3;
    l1 += Math.floorDiv(l2, 86400000000000L);
    long l4 = Math.floorMod(l2, 86400000000000L);
    LocalTime localLocalTime = l4 == l3 ? time : LocalTime.ofNanoOfDay(l4);
    return with(paramD.plus(l1, ChronoUnit.DAYS), localLocalTime);
  }
  
  public ChronoZonedDateTime<D> atZone(ZoneId paramZoneId)
  {
    return ChronoZonedDateTimeImpl.ofBest(this, paramZoneId, null);
  }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit)
  {
    Objects.requireNonNull(paramTemporal, "endExclusive");
    ChronoLocalDateTime localChronoLocalDateTime = getChronology().localDateTime(paramTemporal);
    if ((paramTemporalUnit instanceof ChronoUnit))
    {
      if (paramTemporalUnit.isTimeBased())
      {
        long l = localChronoLocalDateTime.getLong(ChronoField.EPOCH_DAY) - date.getLong(ChronoField.EPOCH_DAY);
        switch ((ChronoUnit)paramTemporalUnit)
        {
        case NANOS: 
          l = Math.multiplyExact(l, 86400000000000L);
          break;
        case MICROS: 
          l = Math.multiplyExact(l, 86400000000L);
          break;
        case MILLIS: 
          l = Math.multiplyExact(l, 86400000L);
          break;
        case SECONDS: 
          l = Math.multiplyExact(l, 86400L);
          break;
        case MINUTES: 
          l = Math.multiplyExact(l, 1440L);
          break;
        case HOURS: 
          l = Math.multiplyExact(l, 24L);
          break;
        case HALF_DAYS: 
          l = Math.multiplyExact(l, 2L);
        }
        return Math.addExact(l, time.until(localChronoLocalDateTime.toLocalTime(), paramTemporalUnit));
      }
      ChronoLocalDate localChronoLocalDate = localChronoLocalDateTime.toLocalDate();
      if (localChronoLocalDateTime.toLocalTime().isBefore(time)) {
        localChronoLocalDate = localChronoLocalDate.minus(1L, ChronoUnit.DAYS);
      }
      return date.until(localChronoLocalDate, paramTemporalUnit);
    }
    Objects.requireNonNull(paramTemporalUnit, "unit");
    return paramTemporalUnit.between(this, localChronoLocalDateTime);
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)2, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeObject(date);
    paramObjectOutput.writeObject(time);
  }
  
  static ChronoLocalDateTime<?> readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    ChronoLocalDate localChronoLocalDate = (ChronoLocalDate)paramObjectInput.readObject();
    LocalTime localLocalTime = (LocalTime)paramObjectInput.readObject();
    return localChronoLocalDate.atTime(localLocalTime);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ChronoLocalDateTime)) {
      return compareTo((ChronoLocalDateTime)paramObject) == 0;
    }
    return false;
  }
  
  public int hashCode()
  {
    return toLocalDate().hashCode() ^ toLocalTime().hashCode();
  }
  
  public String toString()
  {
    return toLocalDate().toString() + 'T' + toLocalTime().toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\ChronoLocalDateTimeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */