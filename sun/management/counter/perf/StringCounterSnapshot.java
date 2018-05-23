package sun.management.counter.perf;

import sun.management.counter.AbstractCounter;
import sun.management.counter.StringCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class StringCounterSnapshot
  extends AbstractCounter
  implements StringCounter
{
  String value;
  private static final long serialVersionUID = 1132921539085572034L;
  
  StringCounterSnapshot(String paramString1, Units paramUnits, Variability paramVariability, int paramInt, String paramString2)
  {
    super(paramString1, paramUnits, paramVariability, paramInt);
    value = paramString2;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public String stringValue()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\StringCounterSnapshot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */