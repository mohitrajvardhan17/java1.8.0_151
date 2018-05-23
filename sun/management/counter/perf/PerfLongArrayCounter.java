package sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.LongBuffer;
import sun.management.counter.AbstractCounter;
import sun.management.counter.LongArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfLongArrayCounter
  extends AbstractCounter
  implements LongArrayCounter
{
  LongBuffer lb;
  private static final long serialVersionUID = -2733617913045487126L;
  
  PerfLongArrayCounter(String paramString, Units paramUnits, Variability paramVariability, int paramInt1, int paramInt2, LongBuffer paramLongBuffer)
  {
    super(paramString, paramUnits, paramVariability, paramInt1, paramInt2);
    lb = paramLongBuffer;
  }
  
  public Object getValue()
  {
    return longArrayValue();
  }
  
  public long[] longArrayValue()
  {
    lb.position(0);
    long[] arrayOfLong = new long[lb.limit()];
    lb.get(arrayOfLong);
    return arrayOfLong;
  }
  
  public long longAt(int paramInt)
  {
    lb.position(paramInt);
    return lb.get();
  }
  
  protected Object writeReplace()
    throws ObjectStreamException
  {
    return new LongArrayCounterSnapshot(getName(), getUnits(), getVariability(), getFlags(), getVectorLength(), longArrayValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\PerfLongArrayCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */