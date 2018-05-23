package java.time.chrono;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract interface ChronoPeriod
  extends TemporalAmount
{
  public static ChronoPeriod between(ChronoLocalDate paramChronoLocalDate1, ChronoLocalDate paramChronoLocalDate2)
  {
    Objects.requireNonNull(paramChronoLocalDate1, "startDateInclusive");
    Objects.requireNonNull(paramChronoLocalDate2, "endDateExclusive");
    return paramChronoLocalDate1.until(paramChronoLocalDate2);
  }
  
  public abstract long get(TemporalUnit paramTemporalUnit);
  
  public abstract List<TemporalUnit> getUnits();
  
  public abstract Chronology getChronology();
  
  public boolean isZero()
  {
    Iterator localIterator = getUnits().iterator();
    while (localIterator.hasNext())
    {
      TemporalUnit localTemporalUnit = (TemporalUnit)localIterator.next();
      if (get(localTemporalUnit) != 0L) {
        return false;
      }
    }
    return true;
  }
  
  public boolean isNegative()
  {
    Iterator localIterator = getUnits().iterator();
    while (localIterator.hasNext())
    {
      TemporalUnit localTemporalUnit = (TemporalUnit)localIterator.next();
      if (get(localTemporalUnit) < 0L) {
        return true;
      }
    }
    return false;
  }
  
  public abstract ChronoPeriod plus(TemporalAmount paramTemporalAmount);
  
  public abstract ChronoPeriod minus(TemporalAmount paramTemporalAmount);
  
  public abstract ChronoPeriod multipliedBy(int paramInt);
  
  public ChronoPeriod negated()
  {
    return multipliedBy(-1);
  }
  
  public abstract ChronoPeriod normalized();
  
  public abstract Temporal addTo(Temporal paramTemporal);
  
  public abstract Temporal subtractFrom(Temporal paramTemporal);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\ChronoPeriod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */