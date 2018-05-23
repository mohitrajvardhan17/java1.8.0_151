package java.time;

import java.io.Serializable;
import java.util.Objects;

public abstract class Clock
{
  public static Clock systemUTC()
  {
    return new SystemClock(ZoneOffset.UTC);
  }
  
  public static Clock systemDefaultZone()
  {
    return new SystemClock(ZoneId.systemDefault());
  }
  
  public static Clock system(ZoneId paramZoneId)
  {
    Objects.requireNonNull(paramZoneId, "zone");
    return new SystemClock(paramZoneId);
  }
  
  public static Clock tickSeconds(ZoneId paramZoneId)
  {
    return new TickClock(system(paramZoneId), 1000000000L);
  }
  
  public static Clock tickMinutes(ZoneId paramZoneId)
  {
    return new TickClock(system(paramZoneId), 60000000000L);
  }
  
  public static Clock tick(Clock paramClock, Duration paramDuration)
  {
    Objects.requireNonNull(paramClock, "baseClock");
    Objects.requireNonNull(paramDuration, "tickDuration");
    if (paramDuration.isNegative()) {
      throw new IllegalArgumentException("Tick duration must not be negative");
    }
    long l = paramDuration.toNanos();
    if ((l % 1000000L != 0L) && (1000000000L % l != 0L)) {
      throw new IllegalArgumentException("Invalid tick duration");
    }
    if (l <= 1L) {
      return paramClock;
    }
    return new TickClock(paramClock, l);
  }
  
  public static Clock fixed(Instant paramInstant, ZoneId paramZoneId)
  {
    Objects.requireNonNull(paramInstant, "fixedInstant");
    Objects.requireNonNull(paramZoneId, "zone");
    return new FixedClock(paramInstant, paramZoneId);
  }
  
  public static Clock offset(Clock paramClock, Duration paramDuration)
  {
    Objects.requireNonNull(paramClock, "baseClock");
    Objects.requireNonNull(paramDuration, "offsetDuration");
    if (paramDuration.equals(Duration.ZERO)) {
      return paramClock;
    }
    return new OffsetClock(paramClock, paramDuration);
  }
  
  protected Clock() {}
  
  public abstract ZoneId getZone();
  
  public abstract Clock withZone(ZoneId paramZoneId);
  
  public long millis()
  {
    return instant().toEpochMilli();
  }
  
  public abstract Instant instant();
  
  public boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  static final class FixedClock
    extends Clock
    implements Serializable
  {
    private static final long serialVersionUID = 7430389292664866958L;
    private final Instant instant;
    private final ZoneId zone;
    
    FixedClock(Instant paramInstant, ZoneId paramZoneId)
    {
      instant = paramInstant;
      zone = paramZoneId;
    }
    
    public ZoneId getZone()
    {
      return zone;
    }
    
    public Clock withZone(ZoneId paramZoneId)
    {
      if (paramZoneId.equals(zone)) {
        return this;
      }
      return new FixedClock(instant, paramZoneId);
    }
    
    public long millis()
    {
      return instant.toEpochMilli();
    }
    
    public Instant instant()
    {
      return instant;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof FixedClock))
      {
        FixedClock localFixedClock = (FixedClock)paramObject;
        return (instant.equals(instant)) && (zone.equals(zone));
      }
      return false;
    }
    
    public int hashCode()
    {
      return instant.hashCode() ^ zone.hashCode();
    }
    
    public String toString()
    {
      return "FixedClock[" + instant + "," + zone + "]";
    }
  }
  
  static final class OffsetClock
    extends Clock
    implements Serializable
  {
    private static final long serialVersionUID = 2007484719125426256L;
    private final Clock baseClock;
    private final Duration offset;
    
    OffsetClock(Clock paramClock, Duration paramDuration)
    {
      baseClock = paramClock;
      offset = paramDuration;
    }
    
    public ZoneId getZone()
    {
      return baseClock.getZone();
    }
    
    public Clock withZone(ZoneId paramZoneId)
    {
      if (paramZoneId.equals(baseClock.getZone())) {
        return this;
      }
      return new OffsetClock(baseClock.withZone(paramZoneId), offset);
    }
    
    public long millis()
    {
      return Math.addExact(baseClock.millis(), offset.toMillis());
    }
    
    public Instant instant()
    {
      return baseClock.instant().plus(offset);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof OffsetClock))
      {
        OffsetClock localOffsetClock = (OffsetClock)paramObject;
        return (baseClock.equals(baseClock)) && (offset.equals(offset));
      }
      return false;
    }
    
    public int hashCode()
    {
      return baseClock.hashCode() ^ offset.hashCode();
    }
    
    public String toString()
    {
      return "OffsetClock[" + baseClock + "," + offset + "]";
    }
  }
  
  static final class SystemClock
    extends Clock
    implements Serializable
  {
    private static final long serialVersionUID = 6740630888130243051L;
    private final ZoneId zone;
    
    SystemClock(ZoneId paramZoneId)
    {
      zone = paramZoneId;
    }
    
    public ZoneId getZone()
    {
      return zone;
    }
    
    public Clock withZone(ZoneId paramZoneId)
    {
      if (paramZoneId.equals(zone)) {
        return this;
      }
      return new SystemClock(paramZoneId);
    }
    
    public long millis()
    {
      return System.currentTimeMillis();
    }
    
    public Instant instant()
    {
      return Instant.ofEpochMilli(millis());
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof SystemClock)) {
        return zone.equals(zone);
      }
      return false;
    }
    
    public int hashCode()
    {
      return zone.hashCode() + 1;
    }
    
    public String toString()
    {
      return "SystemClock[" + zone + "]";
    }
  }
  
  static final class TickClock
    extends Clock
    implements Serializable
  {
    private static final long serialVersionUID = 6504659149906368850L;
    private final Clock baseClock;
    private final long tickNanos;
    
    TickClock(Clock paramClock, long paramLong)
    {
      baseClock = paramClock;
      tickNanos = paramLong;
    }
    
    public ZoneId getZone()
    {
      return baseClock.getZone();
    }
    
    public Clock withZone(ZoneId paramZoneId)
    {
      if (paramZoneId.equals(baseClock.getZone())) {
        return this;
      }
      return new TickClock(baseClock.withZone(paramZoneId), tickNanos);
    }
    
    public long millis()
    {
      long l = baseClock.millis();
      return l - Math.floorMod(l, tickNanos / 1000000L);
    }
    
    public Instant instant()
    {
      if (tickNanos % 1000000L == 0L)
      {
        long l1 = baseClock.millis();
        return Instant.ofEpochMilli(l1 - Math.floorMod(l1, tickNanos / 1000000L));
      }
      Instant localInstant = baseClock.instant();
      long l2 = localInstant.getNano();
      long l3 = Math.floorMod(l2, tickNanos);
      return localInstant.minusNanos(l3);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof TickClock))
      {
        TickClock localTickClock = (TickClock)paramObject;
        return (baseClock.equals(baseClock)) && (tickNanos == tickNanos);
      }
      return false;
    }
    
    public int hashCode()
    {
      return baseClock.hashCode() ^ (int)(tickNanos ^ tickNanos >>> 32);
    }
    
    public String toString()
    {
      return "TickClock[" + baseClock + "," + Duration.ofNanos(tickNanos) + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\Clock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */