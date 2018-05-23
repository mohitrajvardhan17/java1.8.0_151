package java.nio;

public final class ByteOrder
{
  private String name;
  public static final ByteOrder BIG_ENDIAN = new ByteOrder("BIG_ENDIAN");
  public static final ByteOrder LITTLE_ENDIAN = new ByteOrder("LITTLE_ENDIAN");
  
  private ByteOrder(String paramString)
  {
    name = paramString;
  }
  
  public static ByteOrder nativeOrder()
  {
    return Bits.byteOrder();
  }
  
  public String toString()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */