package java.time.temporal;

import java.time.Duration;

public enum ChronoUnit
  implements TemporalUnit
{
  NANOS("Nanos", Duration.ofNanos(1L)),  MICROS("Micros", Duration.ofNanos(1000L)),  MILLIS("Millis", Duration.ofNanos(1000000L)),  SECONDS("Seconds", Duration.ofSeconds(1L)),  MINUTES("Minutes", Duration.ofSeconds(60L)),  HOURS("Hours", Duration.ofSeconds(3600L)),  HALF_DAYS("HalfDays", Duration.ofSeconds(43200L)),  DAYS("Days", Duration.ofSeconds(86400L)),  WEEKS("Weeks", Duration.ofSeconds(604800L)),  MONTHS("Months", Duration.ofSeconds(2629746L)),  YEARS("Years", Duration.ofSeconds(31556952L)),  DECADES("Decades", Duration.ofSeconds(315569520L)),  CENTURIES("Centuries", Duration.ofSeconds(3155695200L)),  MILLENNIA("Millennia", Duration.ofSeconds(31556952000L)),  ERAS("Eras", Duration.ofSeconds(31556952000000000L)),  FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999L));
  
  private final String name;
  private final Duration duration;
  
  private ChronoUnit(String paramString, Duration paramDuration)
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
    return compareTo(DAYS) >= 0;
  }
  
  public boolean isDateBased()
  {
    return (compareTo(DAYS) >= 0) && (this != FOREVER);
  }
  
  public boolean isTimeBased()
  {
    return compareTo(DAYS) < 0;
  }
  
  public boolean isSupportedBy(Temporal paramTemporal)
  {
    return paramTemporal.isSupported(this);
  }
  
  public <R extends Temporal> R addTo(R paramR, long paramLong)
  {
    return paramR.plus(paramLong, this);
  }
  
  public long between(Temporal paramTemporal1, Temporal paramTemporal2)
  {
    return paramTemporal1.until(paramTemporal2, this);
  }
  
  public String toString()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\ChronoUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */