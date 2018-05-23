package sun.nio.ch;

import java.nio.ByteOrder;
import sun.misc.Unsafe;

class NativeObject
{
  protected static final Unsafe unsafe = Unsafe.getUnsafe();
  protected long allocationAddress;
  private final long address;
  private static ByteOrder byteOrder = null;
  private static int pageSize = -1;
  
  NativeObject(long paramLong)
  {
    allocationAddress = paramLong;
    address = paramLong;
  }
  
  NativeObject(long paramLong1, long paramLong2)
  {
    allocationAddress = paramLong1;
    address = (paramLong1 + paramLong2);
  }
  
  protected NativeObject(int paramInt, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      allocationAddress = unsafe.allocateMemory(paramInt);
      address = allocationAddress;
    }
    else
    {
      int i = pageSize();
      long l = unsafe.allocateMemory(paramInt + i);
      allocationAddress = l;
      address = (l + i - (l & i - 1));
    }
  }
  
  long address()
  {
    return address;
  }
  
  long allocationAddress()
  {
    return allocationAddress;
  }
  
  NativeObject subObject(int paramInt)
  {
    return new NativeObject(paramInt + address);
  }
  
  NativeObject getObject(int paramInt)
  {
    long l = 0L;
    switch (addressSize())
    {
    case 8: 
      l = unsafe.getLong(paramInt + address);
      break;
    case 4: 
      l = unsafe.getInt(paramInt + address) & 0xFFFFFFFF;
      break;
    default: 
      throw new InternalError("Address size not supported");
    }
    return new NativeObject(l);
  }
  
  void putObject(int paramInt, NativeObject paramNativeObject)
  {
    switch ()
    {
    case 8: 
      putLong(paramInt, address);
      break;
    case 4: 
      putInt(paramInt, (int)(address & 0xFFFFFFFFFFFFFFFF));
      break;
    default: 
      throw new InternalError("Address size not supported");
    }
  }
  
  final byte getByte(int paramInt)
  {
    return unsafe.getByte(paramInt + address);
  }
  
  final void putByte(int paramInt, byte paramByte)
  {
    unsafe.putByte(paramInt + address, paramByte);
  }
  
  final short getShort(int paramInt)
  {
    return unsafe.getShort(paramInt + address);
  }
  
  final void putShort(int paramInt, short paramShort)
  {
    unsafe.putShort(paramInt + address, paramShort);
  }
  
  final char getChar(int paramInt)
  {
    return unsafe.getChar(paramInt + address);
  }
  
  final void putChar(int paramInt, char paramChar)
  {
    unsafe.putChar(paramInt + address, paramChar);
  }
  
  final int getInt(int paramInt)
  {
    return unsafe.getInt(paramInt + address);
  }
  
  final void putInt(int paramInt1, int paramInt2)
  {
    unsafe.putInt(paramInt1 + address, paramInt2);
  }
  
  final long getLong(int paramInt)
  {
    return unsafe.getLong(paramInt + address);
  }
  
  final void putLong(int paramInt, long paramLong)
  {
    unsafe.putLong(paramInt + address, paramLong);
  }
  
  final float getFloat(int paramInt)
  {
    return unsafe.getFloat(paramInt + address);
  }
  
  final void putFloat(int paramInt, float paramFloat)
  {
    unsafe.putFloat(paramInt + address, paramFloat);
  }
  
  final double getDouble(int paramInt)
  {
    return unsafe.getDouble(paramInt + address);
  }
  
  final void putDouble(int paramInt, double paramDouble)
  {
    unsafe.putDouble(paramInt + address, paramDouble);
  }
  
  static int addressSize()
  {
    return unsafe.addressSize();
  }
  
  /* Error */
  static ByteOrder byteOrder()
  {
    // Byte code:
    //   0: getstatic 142	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
    //   3: ifnull +7 -> 10
    //   6: getstatic 142	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
    //   9: areturn
    //   10: getstatic 143	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
    //   13: ldc2_w 86
    //   16: invokevirtual 155	sun/misc/Unsafe:allocateMemory	(J)J
    //   19: lstore_0
    //   20: getstatic 143	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
    //   23: lload_0
    //   24: ldc2_w 88
    //   27: invokevirtual 164	sun/misc/Unsafe:putLong	(JJ)V
    //   30: getstatic 143	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
    //   33: lload_0
    //   34: invokevirtual 150	sun/misc/Unsafe:getByte	(J)B
    //   37: istore_2
    //   38: iload_2
    //   39: lookupswitch	default:+43->82, 1:+25->64, 8:+34->73
    //   64: getstatic 136	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   67: putstatic 142	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
    //   70: goto +26 -> 96
    //   73: getstatic 137	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
    //   76: putstatic 142	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
    //   79: goto +17 -> 96
    //   82: getstatic 141	sun/nio/ch/NativeObject:$assertionsDisabled	Z
    //   85: ifne +11 -> 96
    //   88: new 90	java/lang/AssertionError
    //   91: dup
    //   92: invokespecial 144	java/lang/AssertionError:<init>	()V
    //   95: athrow
    //   96: getstatic 143	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
    //   99: lload_0
    //   100: invokevirtual 158	sun/misc/Unsafe:freeMemory	(J)V
    //   103: goto +13 -> 116
    //   106: astore_3
    //   107: getstatic 143	sun/nio/ch/NativeObject:unsafe	Lsun/misc/Unsafe;
    //   110: lload_0
    //   111: invokevirtual 158	sun/misc/Unsafe:freeMemory	(J)V
    //   114: aload_3
    //   115: athrow
    //   116: getstatic 142	sun/nio/ch/NativeObject:byteOrder	Ljava/nio/ByteOrder;
    //   119: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   19	92	0	l	long
    //   37	2	2	i	int
    //   106	9	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   20	96	106	finally
  }
  
  static int pageSize()
  {
    if (pageSize == -1) {
      pageSize = unsafe.pageSize();
    }
    return pageSize;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\NativeObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */