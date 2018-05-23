package sun.management.counter.perf;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class PerfDataEntry
{
  private String name;
  private int entryStart;
  private int entryLength;
  private int vectorLength;
  private PerfDataType dataType;
  private int flags;
  private Units unit;
  private Variability variability;
  private int dataOffset;
  private int dataSize;
  private ByteBuffer data;
  
  PerfDataEntry(ByteBuffer paramByteBuffer)
  {
    entryStart = paramByteBuffer.position();
    entryLength = paramByteBuffer.getInt();
    if ((entryLength <= 0) || (entryLength > paramByteBuffer.limit())) {
      throw new InstrumentationException("Invalid entry length:  entryLength = " + entryLength);
    }
    if (entryStart + entryLength > paramByteBuffer.limit()) {
      throw new InstrumentationException("Entry extends beyond end of buffer:  entryStart = " + entryStart + " entryLength = " + entryLength + " buffer limit = " + paramByteBuffer.limit());
    }
    paramByteBuffer.position(entryStart + 4);
    int i = paramByteBuffer.getInt();
    if (entryStart + i > paramByteBuffer.limit()) {
      throw new InstrumentationException("Invalid name offset:  entryStart = " + entryStart + " nameOffset = " + i + " buffer limit = " + paramByteBuffer.limit());
    }
    paramByteBuffer.position(entryStart + 8);
    vectorLength = paramByteBuffer.getInt();
    paramByteBuffer.position(entryStart + 12);
    dataType = PerfDataType.toPerfDataType(paramByteBuffer.get());
    paramByteBuffer.position(entryStart + 13);
    flags = paramByteBuffer.get();
    paramByteBuffer.position(entryStart + 14);
    unit = Units.toUnits(paramByteBuffer.get());
    paramByteBuffer.position(entryStart + 15);
    variability = Variability.toVariability(paramByteBuffer.get());
    paramByteBuffer.position(entryStart + 16);
    dataOffset = paramByteBuffer.getInt();
    paramByteBuffer.position(entryStart + i);
    int k;
    for (int j = 0; (k = paramByteBuffer.get()) != 0; j++) {}
    byte[] arrayOfByte = new byte[j];
    paramByteBuffer.position(entryStart + i);
    for (int m = 0; m < j; m++) {
      arrayOfByte[m] = paramByteBuffer.get();
    }
    try
    {
      name = new String(arrayOfByte, "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError(localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
    if (variability == Variability.INVALID) {
      throw new InstrumentationException("Invalid variability attribute: name = " + name);
    }
    if (unit == Units.INVALID) {
      throw new InstrumentationException("Invalid units attribute:  name = " + name);
    }
    if (vectorLength > 0) {
      dataSize = (vectorLength * dataType.size());
    } else {
      dataSize = dataType.size();
    }
    if (entryStart + dataOffset + dataSize > paramByteBuffer.limit()) {
      throw new InstrumentationException("Data extends beyond end of buffer:  entryStart = " + entryStart + " dataOffset = " + dataOffset + " dataSize = " + dataSize + " buffer limit = " + paramByteBuffer.limit());
    }
    paramByteBuffer.position(entryStart + dataOffset);
    data = paramByteBuffer.slice();
    data.order(paramByteBuffer.order());
    data.limit(dataSize);
  }
  
  public int size()
  {
    return entryLength;
  }
  
  public String name()
  {
    return name;
  }
  
  public PerfDataType type()
  {
    return dataType;
  }
  
  public Units units()
  {
    return unit;
  }
  
  public int flags()
  {
    return flags;
  }
  
  public int vectorLength()
  {
    return vectorLength;
  }
  
  public Variability variability()
  {
    return variability;
  }
  
  public ByteBuffer byteData()
  {
    data.position(0);
    assert (data.remaining() == vectorLength());
    return data.duplicate();
  }
  
  public LongBuffer longData()
  {
    LongBuffer localLongBuffer = data.asLongBuffer();
    return localLongBuffer;
  }
  
  private class EntryFieldOffset
  {
    private static final int SIZEOF_BYTE = 1;
    private static final int SIZEOF_INT = 4;
    private static final int SIZEOF_LONG = 8;
    private static final int ENTRY_LENGTH_SIZE = 4;
    private static final int NAME_OFFSET_SIZE = 4;
    private static final int VECTOR_LENGTH_SIZE = 4;
    private static final int DATA_TYPE_SIZE = 1;
    private static final int FLAGS_SIZE = 1;
    private static final int DATA_UNIT_SIZE = 1;
    private static final int DATA_VAR_SIZE = 1;
    private static final int DATA_OFFSET_SIZE = 4;
    static final int ENTRY_LENGTH = 0;
    static final int NAME_OFFSET = 4;
    static final int VECTOR_LENGTH = 8;
    static final int DATA_TYPE = 12;
    static final int FLAGS = 13;
    static final int DATA_UNIT = 14;
    static final int DATA_VAR = 15;
    static final int DATA_OFFSET = 16;
    
    private EntryFieldOffset() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\PerfDataEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */