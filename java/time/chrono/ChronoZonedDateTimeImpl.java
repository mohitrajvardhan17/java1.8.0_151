package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.List;
import java.util.Objects;

final class ChronoZonedDateTimeImpl<D extends ChronoLocalDate>
  implements ChronoZonedDateTime<D>, Serializable
{
  private static final long serialVersionUID = -5261813987200935591L;
  private final transient ChronoLocalDateTimeImpl<D> dateTime;
  private final transient ZoneOffset offset;
  private final transient ZoneId zone;
  
  static <R extends ChronoLocalDate> ChronoZonedDateTime<R> ofBest(ChronoLocalDateTimeImpl<R> paramChronoLocalDateTimeImpl, ZoneId paramZoneId, ZoneOffset paramZoneOffset)
  {
    Objects.requireNonNull(paramChronoLocalDateTimeImpl, "localDateTime");
    Objects.requireNonNull(paramZoneId, "zone");
    if ((paramZoneId instanceof ZoneOffset)) {
      return new ChronoZonedDateTimeImpl(paramChronoLocalDateTimeImpl, (ZoneOffset)paramZoneId, paramZoneId);
    }
    ZoneRules localZoneRules = paramZoneId.getRules();
    LocalDateTime localLocalDateTime = LocalDateTime.from(paramChronoLocalDateTimeImpl);
    List localList = localZoneRules.getValidOffsets(localLocalDateTime);
    ZoneOffset localZoneOffset;
    if (localList.size() == 1)
    {
      localZoneOffset = (ZoneOffset)localList.get(0);
    }
    else if (localList.size() == 0)
    {
      ZoneOffsetTransition localZoneOffsetTransition = localZoneRules.getTransition(localLocalDateTime);
      paramChronoLocalDateTimeImpl = paramChronoLocalDateTimeImpl.plusSeconds(localZoneOffsetTransition.getDuration().getSeconds());
      localZoneOffset = localZoneOffsetTransition.getOffsetAfter();
    }
    else if ((paramZoneOffset != null) && (localList.contains(paramZoneOffset)))
    {
      localZoneOffset = paramZoneOffset;
    }
    else
    {
      localZoneOffset = (ZoneOffset)localList.get(0);
    }
    Objects.requireNonNull(localZoneOffset, "offset");
    return new ChronoZonedDateTimeImpl(paramChronoLocalDateTimeImpl, localZoneOffset, paramZoneId);
  }
  
  static ChronoZonedDateTimeImpl<?> ofInstant(Chronology paramChronology, Instant paramInstant, ZoneId paramZoneId)
  {
    ZoneRules localZoneRules = paramZoneId.getRules();
    ZoneOffset localZoneOffset = localZoneRules.getOffset(paramInstant);
    Objects.requireNonNull(localZoneOffset, "offset");
    LocalDateTime localLocalDateTime = LocalDateTime.ofEpochSecond(paramInstant.getEpochSecond(), paramInstant.getNano(), localZoneOffset);
    ChronoLocalDateTimeImpl localChronoLocalDateTimeImpl = (ChronoLocalDateTimeImpl)paramChronology.localDateTime(localLocalDateTime);
    return new ChronoZonedDateTimeImpl(localChronoLocalDateTimeImpl, localZoneOffset, paramZoneId);
  }
  
  private ChronoZonedDateTimeImpl<D> create(Instant paramInstant, ZoneId paramZoneId)
  {
    return ofInstant(getChronology(), paramInstant, paramZoneId);
  }
  
  static <R extends ChronoLocalDate> ChronoZonedDateTimeImpl<R> ensureValid(Chronology paramChronology, Temporal paramTemporal)
  {
    ChronoZonedDateTimeImpl localChronoZonedDateTimeImpl = (ChronoZonedDateTimeImpl)paramTemporal;
    if (!paramChronology.equals(localChronoZonedDateTimeImpl.getChronology())) {
      throw new ClassCastException("Chronology mismatch, required: " + paramChronology.getId() + ", actual: " + localChronoZonedDateTimeImpl.getChronology().getId());
    }
    return localChronoZonedDateTimeImpl;
  }
  
  private ChronoZonedDateTimeImpl(ChronoLocalDateTimeImpl<D> paramChronoLocalDateTimeImpl, ZoneOffset paramZoneOffset, ZoneId paramZoneId)
  {
    dateTime = ((ChronoLocalDateTimeImpl)Objects.requireNonNull(paramChronoLocalDateTimeImpl, "dateTime"));
    offset = ((ZoneOffset)Objects.requireNonNull(paramZoneOffset, "offset"));
    zone = ((ZoneId)Objects.requireNonNull(paramZoneId, "zone"));
  }
  
  public ZoneOffset getOffset()
  {
    return offset;
  }
  
  public ChronoZonedDateTime<D> withEarlierOffsetAtOverlap()
  {
    ZoneOffsetTransition localZoneOffsetTransition = getZone().getRules().getTransition(LocalDateTime.from(this));
    if ((localZoneOffsetTransition != null) && (localZoneOffsetTransition.isOverlap()))
    {
      ZoneOffset localZoneOffset = localZoneOffsetTransition.getOffsetBefore();
      if (!localZoneOffset.equals(offset)) {
        return new ChronoZonedDateTimeImpl(dateTime, localZoneOffset, zone);
      }
    }
    return this;
  }
  
  public ChronoZonedDateTime<D> withLaterOffsetAtOverlap()
  {
    ZoneOffsetTransition localZoneOffsetTransition = getZone().getRules().getTransition(LocalDateTime.from(this));
    if (localZoneOffsetTransition != null)
    {
      ZoneOffset localZoneOffset = localZoneOffsetTransition.getOffsetAfter();
      if (!localZoneOffset.equals(getOffset())) {
        return new ChronoZonedDateTimeImpl(dateTime, localZoneOffset, zone);
      }
    }
    return this;
  }
  
  public ChronoLocalDateTime<D> toLocalDateTime()
  {
    return dateTime;
  }
  
  public ZoneId getZone()
  {
    return zone;
  }
  
  public ChronoZonedDateTime<D> withZoneSameLocal(ZoneId paramZoneId)
  {
    return ofBest(dateTime, paramZoneId, offset);
  }
  
  public ChronoZonedDateTime<D> withZoneSameInstant(ZoneId paramZoneId)
  {
    Objects.requireNonNull(paramZoneId, "zone");
    return zone.equals(paramZoneId) ? this : create(dateTime.toInstant(offset), paramZoneId);
  }
  
  public boolean isSupported(TemporalField paramTemporalField)
  {
    return ((paramTemporalField instanceof ChronoField)) || ((paramTemporalField != null) && (paramTemporalField.isSupportedBy(this)));
  }
  
  public ChronoZonedDateTime<D> with(TemporalField paramTemporalField, long paramLong)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      switch (localChronoField)
      {
      case INSTANT_SECONDS: 
        return plus(paramLong - toEpochSecond(), ChronoUnit.SECONDS);
      case OFFSET_SECONDS: 
        ZoneOffset localZoneOffset = ZoneOffset.ofTotalSeconds(localChronoField.checkValidIntValue(paramLong));
        return create(dateTime.toInstant(localZoneOffset), zone);
      }
      return ofBest(dateTime.with(paramTemporalField, paramLong), zone, offset);
    }
    return ensureValid(getChronology(), paramTemporalField.adjustInto(this, paramLong));
  }
  
  public ChronoZonedDateTime<D> plus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    if ((paramTemporalUnit instanceof ChronoUnit)) {
      return with(dateTime.plus(paramLong, paramTemporalUnit));
    }
    return ensureValid(getChronology(), paramTemporalUnit.addTo(this, paramLong));
  }
  
  public long until(Temporal paramTemporal, TemporalUnit paramTemporalUnit)
  {
    Objects.requireNonNull(paramTemporal, "endExclusive");
    ChronoZonedDateTime localChronoZonedDateTime = getChronology().zonedDateTime(paramTemporal);
    if ((paramTemporalUnit instanceof ChronoUnit))
    {
      localChronoZonedDateTime = localChronoZonedDateTime.withZoneSameInstant(offset);
      return dateTime.until(localChronoZonedDateTime.toLocalDateTime(), paramTemporalUnit);
    }
    Objects.requireNonNull(paramTemporalUnit, "unit");
    return paramTemporalUnit.between(this, localChronoZonedDateTime);
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)3, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeObject(dateTime);
    paramObjectOutput.writeObject(offset);
    paramObjectOutput.writeObject(zone);
  }
  
  static ChronoZonedDateTime<?> readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    ChronoLocalDateTime localChronoLocalDateTime = (ChronoLocalDateTime)paramObjectInput.readObject();
    ZoneOffset localZoneOffset = (ZoneOffset)paramObjectInput.readObject();
    ZoneId localZoneId = (ZoneId)paramObjectInput.readObject();
    return localChronoLocalDateTime.atZone(localZoneOffset).withZoneSameLocal(localZoneId);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ChronoZonedDateTime)) {
      return compareTo((ChronoZonedDateTime)paramObject) == 0;
    }
    return false;
  }
  
  public int hashCode()
  {
    return toLocalDateTime().hashCode() ^ getOffset().hashCode() ^ Integer.rotateLeft(getZone().hashCode(), 3);
  }
  
  public String toString()
  {
    String str = toLocalDateTime().toString() + getOffset().toString();
    if (getOffset() != getZone()) {
      str = str + '[' + getZone().toString() + ']';
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\ChronoZonedDateTimeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */