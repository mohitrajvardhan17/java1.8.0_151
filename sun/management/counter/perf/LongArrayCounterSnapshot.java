package sun.management.counter.perf;

import sun.management.counter.AbstractCounter;
import sun.management.counter.LongArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class LongArrayCounterSnapshot
  extends AbstractCounter
  implements LongArrayCounter
{
  long[] value;
  private static final long serialVersionUID = 3585870271405924292L;
  
  LongArrayCounterSnapshot(String paramString, Units paramUnits, Variability paramVariability, int paramInt1, int paramInt2, long[] paramArrayOfLong)
  {
    super(paramString, paramUnits, paramVariability, paramInt1, paramInt2);
    value = paramArrayOfLong;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public long[] longArrayValue()
  {
    return value;
  }
  
  public long longAt(int paramInt)
  {
    return value[paramInt];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\LongArrayCounterSnapshot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */