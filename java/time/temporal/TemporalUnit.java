package java.time.temporal;

import java.time.Duration;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;

public abstract interface TemporalUnit
{
  public abstract Duration getDuration();
  
  public abstract boolean isDurationEstimated();
  
  public abstract boolean isDateBased();
  
  public abstract boolean isTimeBased();
  
  public boolean isSupportedBy(Temporal paramTemporal)
  {
    if ((paramTemporal instanceof LocalTime)) {
      return isTimeBased();
    }
    if ((paramTemporal instanceof ChronoLocalDate)) {
      return isDateBased();
    }
    if (((paramTemporal instanceof ChronoLocalDateTime)) || ((paramTemporal instanceof ChronoZonedDateTime))) {
      return true;
    }
    try
    {
      paramTemporal.plus(1L, this);
      return true;
    }
    catch (UnsupportedTemporalTypeException localUnsupportedTemporalTypeException)
    {
      return false;
    }
    catch (RuntimeException localRuntimeException1)
    {
      try
      {
        paramTemporal.plus(-1L, this);
        return true;
      }
      catch (RuntimeException localRuntimeException2) {}
    }
    return false;
  }
  
  public abstract <R extends Temporal> R addTo(R paramR, long paramLong);
  
  public abstract long between(Temporal paramTemporal1, Temporal paramTemporal2);
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\TemporalUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */