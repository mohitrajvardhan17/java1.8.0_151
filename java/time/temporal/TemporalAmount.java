package java.time.temporal;

import java.util.List;

public abstract interface TemporalAmount
{
  public abstract long get(TemporalUnit paramTemporalUnit);
  
  public abstract List<TemporalUnit> getUnits();
  
  public abstract Temporal addTo(Temporal paramTemporal);
  
  public abstract Temporal subtractFrom(Temporal paramTemporal);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\TemporalAmount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */