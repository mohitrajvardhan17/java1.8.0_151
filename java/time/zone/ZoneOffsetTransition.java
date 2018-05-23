package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ZoneOffsetTransition
  implements Comparable<ZoneOffsetTransition>, Serializable
{
  private static final long serialVersionUID = -6946044323557704546L;
  private final LocalDateTime transition;
  private final ZoneOffset offsetBefore;
  private final ZoneOffset offsetAfter;
  
  public static ZoneOffsetTransition of(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2)
  {
    Objects.requireNonNull(paramLocalDateTime, "transition");
    Objects.requireNonNull(paramZoneOffset1, "offsetBefore");
    Objects.requireNonNull(paramZoneOffset2, "offsetAfter");
    if (paramZoneOffset1.equals(paramZoneOffset2)) {
      throw new IllegalArgumentException("Offsets must not be equal");
    }
    if (paramLocalDateTime.getNano() != 0) {
      throw new IllegalArgumentException("Nano-of-second must be zero");
    }
    return new ZoneOffsetTransition(paramLocalDateTime, paramZoneOffset1, paramZoneOffset2);
  }
  
  ZoneOffsetTransition(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2)
  {
    transition = paramLocalDateTime;
    offsetBefore = paramZoneOffset1;
    offsetAfter = paramZoneOffset2;
  }
  
  ZoneOffsetTransition(long paramLong, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2)
  {
    transition = LocalDateTime.ofEpochSecond(paramLong, 0, paramZoneOffset1);
    offsetBefore = paramZoneOffset1;
    offsetAfter = paramZoneOffset2;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)2, this);
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    Ser.writeEpochSec(toEpochSecond(), paramDataOutput);
    Ser.writeOffset(offsetBefore, paramDataOutput);
    Ser.writeOffset(offsetAfter, paramDataOutput);
  }
  
  static ZoneOffsetTransition readExternal(DataInput paramDataInput)
    throws IOException
  {
    long l = Ser.readEpochSec(paramDataInput);
    ZoneOffset localZoneOffset1 = Ser.readOffset(paramDataInput);
    ZoneOffset localZoneOffset2 = Ser.readOffset(paramDataInput);
    if (localZoneOffset1.equals(localZoneOffset2)) {
      throw new IllegalArgumentException("Offsets must not be equal");
    }
    return new ZoneOffsetTransition(l, localZoneOffset1, localZoneOffset2);
  }
  
  public Instant getInstant()
  {
    return transition.toInstant(offsetBefore);
  }
  
  public long toEpochSecond()
  {
    return transition.toEpochSecond(offsetBefore);
  }
  
  public LocalDateTime getDateTimeBefore()
  {
    return transition;
  }
  
  public LocalDateTime getDateTimeAfter()
  {
    return transition.plusSeconds(getDurationSeconds());
  }
  
  public ZoneOffset getOffsetBefore()
  {
    return offsetBefore;
  }
  
  public ZoneOffset getOffsetAfter()
  {
    return offsetAfter;
  }
  
  public Duration getDuration()
  {
    return Duration.ofSeconds(getDurationSeconds());
  }
  
  private int getDurationSeconds()
  {
    return getOffsetAfter().getTotalSeconds() - getOffsetBefore().getTotalSeconds();
  }
  
  public boolean isGap()
  {
    return getOffsetAfter().getTotalSeconds() > getOffsetBefore().getTotalSeconds();
  }
  
  public boolean isOverlap()
  {
    return getOffsetAfter().getTotalSeconds() < getOffsetBefore().getTotalSeconds();
  }
  
  public boolean isValidOffset(ZoneOffset paramZoneOffset)
  {
    return !isGap();
  }
  
  List<ZoneOffset> getValidOffsets()
  {
    if (isGap()) {
      return Collections.emptyList();
    }
    return Arrays.asList(new ZoneOffset[] { getOffsetBefore(), getOffsetAfter() });
  }
  
  public int compareTo(ZoneOffsetTransition paramZoneOffsetTransition)
  {
    return getInstant().compareTo(paramZoneOffsetTransition.getInstant());
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof ZoneOffsetTransition))
    {
      ZoneOffsetTransition localZoneOffsetTransition = (ZoneOffsetTransition)paramObject;
      return (transition.equals(transition)) && (offsetBefore.equals(offsetBefore)) && (offsetAfter.equals(offsetAfter));
    }
    return false;
  }
  
  public int hashCode()
  {
    return transition.hashCode() ^ offsetBefore.hashCode() ^ Integer.rotateLeft(offsetAfter.hashCode(), 16);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Transition[").append(isGap() ? "Gap" : "Overlap").append(" at ").append(transition).append(offsetBefore).append(" to ").append(offsetAfter).append(']');
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\zone\ZoneOffsetTransition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */