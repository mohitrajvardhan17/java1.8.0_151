package sun.management.counter.perf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class Prologue
{
  private static final byte PERFDATA_BIG_ENDIAN = 0;
  private static final byte PERFDATA_LITTLE_ENDIAN = 1;
  private static final int PERFDATA_MAGIC = -889274176;
  private ByteBuffer header;
  private int magic;
  
  Prologue(ByteBuffer paramByteBuffer)
  {
    header = paramByteBuffer.duplicate();
    header.order(ByteOrder.BIG_ENDIAN);
    header.position(0);
    magic = header.getInt();
    if (magic != -889274176) {
      throw new InstrumentationException("Bad Magic: " + Integer.toHexString(getMagic()));
    }
    header.order(getByteOrder());
    int i = getMajorVersion();
    int j = getMinorVersion();
    if (i < 2) {
      throw new InstrumentationException("Unsupported version: " + i + "." + j);
    }
    header.limit(32);
  }
  
  public int getMagic()
  {
    return magic;
  }
  
  public int getMajorVersion()
  {
    header.position(5);
    return header.get();
  }
  
  public int getMinorVersion()
  {
    header.position(6);
    return header.get();
  }
  
  public ByteOrder getByteOrder()
  {
    header.position(4);
    int i = header.get();
    if (i == 0) {
      return ByteOrder.BIG_ENDIAN;
    }
    return ByteOrder.LITTLE_ENDIAN;
  }
  
  public int getEntryOffset()
  {
    header.position(24);
    return header.getInt();
  }
  
  public int getUsed()
  {
    header.position(8);
    return header.getInt();
  }
  
  public int getOverflow()
  {
    header.position(12);
    return header.getInt();
  }
  
  public long getModificationTimeStamp()
  {
    header.position(16);
    return header.getLong();
  }
  
  public int getNumEntries()
  {
    header.position(28);
    return header.getInt();
  }
  
  public boolean isAccessible()
  {
    header.position(7);
    int i = header.get();
    return i != 0;
  }
  
  private class PrologueFieldOffset
  {
    private static final int SIZEOF_BYTE = 1;
    private static final int SIZEOF_INT = 4;
    private static final int SIZEOF_LONG = 8;
    private static final int MAGIC_SIZE = 4;
    private static final int BYTE_ORDER_SIZE = 1;
    private static final int MAJOR_SIZE = 1;
    private static final int MINOR_SIZE = 1;
    private static final int ACCESSIBLE_SIZE = 1;
    private static final int USED_SIZE = 4;
    private static final int OVERFLOW_SIZE = 4;
    private static final int MOD_TIMESTAMP_SIZE = 8;
    private static final int ENTRY_OFFSET_SIZE = 4;
    private static final int NUM_ENTRIES_SIZE = 4;
    static final int MAGIC = 0;
    static final int BYTE_ORDER = 4;
    static final int MAJOR_VERSION = 5;
    static final int MINOR_VERSION = 6;
    static final int ACCESSIBLE = 7;
    static final int USED = 8;
    static final int OVERFLOW = 12;
    static final int MOD_TIMESTAMP = 16;
    static final int ENTRY_OFFSET = 24;
    static final int NUM_ENTRIES = 28;
    static final int PROLOGUE_2_0_SIZE = 32;
    
    private PrologueFieldOffset() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\Prologue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */