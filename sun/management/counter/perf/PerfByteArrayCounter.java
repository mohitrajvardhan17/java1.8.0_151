package sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.ByteBuffer;
import sun.management.counter.AbstractCounter;
import sun.management.counter.ByteArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfByteArrayCounter
  extends AbstractCounter
  implements ByteArrayCounter
{
  ByteBuffer bb;
  private static final long serialVersionUID = 2545474036937279921L;
  
  PerfByteArrayCounter(String paramString, Units paramUnits, Variability paramVariability, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer)
  {
    super(paramString, paramUnits, paramVariability, paramInt1, paramInt2);
    bb = paramByteBuffer;
  }
  
  public Object getValue()
  {
    return byteArrayValue();
  }
  
  public byte[] byteArrayValue()
  {
    bb.position(0);
    byte[] arrayOfByte = new byte[bb.limit()];
    bb.get(arrayOfByte);
    return arrayOfByte;
  }
  
  public byte byteAt(int paramInt)
  {
    bb.position(paramInt);
    return bb.get();
  }
  
  public String toString()
  {
    String str = getName() + ": " + new String(byteArrayValue()) + " " + getUnits();
    if (isInternal()) {
      return str + " [INTERNAL]";
    }
    return str;
  }
  
  protected Object writeReplace()
    throws ObjectStreamException
  {
    return new ByteArrayCounterSnapshot(getName(), getUnits(), getVariability(), getFlags(), getVectorLength(), byteArrayValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\PerfByteArrayCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */