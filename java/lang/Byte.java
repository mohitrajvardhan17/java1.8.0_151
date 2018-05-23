package java.lang;

public final class Byte
  extends Number
  implements Comparable<Byte>
{
  public static final byte MIN_VALUE = -128;
  public static final byte MAX_VALUE = 127;
  public static final Class<Byte> TYPE = Class.getPrimitiveClass("byte");
  private final byte value;
  public static final int SIZE = 8;
  public static final int BYTES = 1;
  private static final long serialVersionUID = -7183698231559129828L;
  
  public static String toString(byte paramByte)
  {
    return Integer.toString(paramByte, 10);
  }
  
  public static Byte valueOf(byte paramByte)
  {
    return ByteCache.cache[(paramByte + 128)];
  }
  
  public static byte parseByte(String paramString, int paramInt)
    throws NumberFormatException
  {
    int i = Integer.parseInt(paramString, paramInt);
    if ((i < -128) || (i > 127)) {
      throw new NumberFormatException("Value out of range. Value:\"" + paramString + "\" Radix:" + paramInt);
    }
    return (byte)i;
  }
  
  public static byte parseByte(String paramString)
    throws NumberFormatException
  {
    return parseByte(paramString, 10);
  }
  
  public static Byte valueOf(String paramString, int paramInt)
    throws NumberFormatException
  {
    return valueOf(parseByte(paramString, paramInt));
  }
  
  public static Byte valueOf(String paramString)
    throws NumberFormatException
  {
    return valueOf(paramString, 10);
  }
  
  public static Byte decode(String paramString)
    throws NumberFormatException
  {
    int i = Integer.decode(paramString).intValue();
    if ((i < -128) || (i > 127)) {
      throw new NumberFormatException("Value " + i + " out of range from input " + paramString);
    }
    return valueOf((byte)i);
  }
  
  public Byte(byte paramByte)
  {
    value = paramByte;
  }
  
  public Byte(String paramString)
    throws NumberFormatException
  {
    value = parseByte(paramString, 10);
  }
  
  public byte byteValue()
  {
    return value;
  }
  
  public short shortValue()
  {
    return (short)value;
  }
  
  public int intValue()
  {
    return value;
  }
  
  public long longValue()
  {
    return value;
  }
  
  public float floatValue()
  {
    return value;
  }
  
  public double doubleValue()
  {
    return value;
  }
  
  public String toString()
  {
    return Integer.toString(value);
  }
  
  public int hashCode()
  {
    return hashCode(value);
  }
  
  public static int hashCode(byte paramByte)
  {
    return paramByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Byte)) {
      return value == ((Byte)paramObject).byteValue();
    }
    return false;
  }
  
  public int compareTo(Byte paramByte)
  {
    return compare(value, value);
  }
  
  public static int compare(byte paramByte1, byte paramByte2)
  {
    return paramByte1 - paramByte2;
  }
  
  public static int toUnsignedInt(byte paramByte)
  {
    return paramByte & 0xFF;
  }
  
  public static long toUnsignedLong(byte paramByte)
  {
    return paramByte & 0xFF;
  }
  
  private static class ByteCache
  {
    static final Byte[] cache = new Byte['Ā'];
    
    private ByteCache() {}
    
    static
    {
      for (int i = 0; i < cache.length; i++) {
        cache[i] = new Byte((byte)(i - 128));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Byte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */