package sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.LongBuffer;
import sun.management.counter.AbstractCounter;
import sun.management.counter.LongCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfLongCounter
  extends AbstractCounter
  implements LongCounter
{
  LongBuffer lb;
  private static final long serialVersionUID = 857711729279242948L;
  
  PerfLongCounter(String paramString, Units paramUnits, Variability paramVariability, int paramInt, LongBuffer paramLongBuffer)
  {
    super(paramString, paramUnits, paramVariability, paramInt);
    lb = paramLongBuffer;
  }
  
  public Object getValue()
  {
    return new Long(lb.get(0));
  }
  
  public long longValue()
  {
    return lb.get(0);
  }
  
  protected Object writeReplace()
    throws ObjectStreamException
  {
    return new LongCounterSnapshot(getName(), getUnits(), getVariability(), getFlags(), longValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\PerfLongCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */