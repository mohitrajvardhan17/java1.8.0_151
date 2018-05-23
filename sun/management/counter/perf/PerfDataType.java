package sun.management.counter.perf;

import java.io.UnsupportedEncodingException;

class PerfDataType
{
  private final String name;
  private final byte value;
  private final int size;
  public static final PerfDataType BOOLEAN = new PerfDataType("boolean", "Z", 1);
  public static final PerfDataType CHAR = new PerfDataType("char", "C", 1);
  public static final PerfDataType FLOAT = new PerfDataType("float", "F", 8);
  public static final PerfDataType DOUBLE = new PerfDataType("double", "D", 8);
  public static final PerfDataType BYTE = new PerfDataType("byte", "B", 1);
  public static final PerfDataType SHORT = new PerfDataType("short", "S", 2);
  public static final PerfDataType INT = new PerfDataType("int", "I", 4);
  public static final PerfDataType LONG = new PerfDataType("long", "J", 8);
  public static final PerfDataType ILLEGAL = new PerfDataType("illegal", "X", 0);
  private static PerfDataType[] basicTypes = { LONG, BYTE, BOOLEAN, CHAR, FLOAT, DOUBLE, SHORT, INT };
  
  public String toString()
  {
    return name;
  }
  
  public byte byteValue()
  {
    return value;
  }
  
  public int size()
  {
    return size;
  }
  
  public static PerfDataType toPerfDataType(byte paramByte)
  {
    for (int i = 0; i < basicTypes.length; i++) {
      if (basicTypes[i].byteValue() == paramByte) {
        return basicTypes[i];
      }
    }
    return ILLEGAL;
  }
  
  private PerfDataType(String paramString1, String paramString2, int paramInt)
  {
    name = paramString1;
    size = paramInt;
    try
    {
      byte[] arrayOfByte = paramString2.getBytes("UTF-8");
      value = arrayOfByte[0];
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError("Unknown encoding", localUnsupportedEncodingException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\PerfDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */