package sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import sun.management.counter.StringCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfStringCounter
  extends PerfByteArrayCounter
  implements StringCounter
{
  private static Charset defaultCharset = ;
  private static final long serialVersionUID = 6802913433363692452L;
  
  PerfStringCounter(String paramString, Variability paramVariability, int paramInt, ByteBuffer paramByteBuffer)
  {
    this(paramString, paramVariability, paramInt, paramByteBuffer.limit(), paramByteBuffer);
  }
  
  PerfStringCounter(String paramString, Variability paramVariability, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer)
  {
    super(paramString, Units.STRING, paramVariability, paramInt1, paramInt2, paramByteBuffer);
  }
  
  public boolean isVector()
  {
    return false;
  }
  
  public int getVectorLength()
  {
    return 0;
  }
  
  public Object getValue()
  {
    return stringValue();
  }
  
  public String stringValue()
  {
    String str = "";
    byte[] arrayOfByte = byteArrayValue();
    if ((arrayOfByte == null) || (arrayOfByte.length <= 1)) {
      return str;
    }
    for (int i = 0; (i < arrayOfByte.length) && (arrayOfByte[i] != 0); i++) {}
    return new String(arrayOfByte, 0, i, defaultCharset);
  }
  
  protected Object writeReplace()
    throws ObjectStreamException
  {
    return new StringCounterSnapshot(getName(), getUnits(), getVariability(), getFlags(), stringValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\PerfStringCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */