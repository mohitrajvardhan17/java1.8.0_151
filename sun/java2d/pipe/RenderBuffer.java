package sun.java2d.pipe;

import sun.misc.Unsafe;

public class RenderBuffer
{
  protected static final long SIZEOF_BYTE = 1L;
  protected static final long SIZEOF_SHORT = 2L;
  protected static final long SIZEOF_INT = 4L;
  protected static final long SIZEOF_FLOAT = 4L;
  protected static final long SIZEOF_LONG = 8L;
  protected static final long SIZEOF_DOUBLE = 8L;
  private static final int COPY_FROM_ARRAY_THRESHOLD = 6;
  protected final Unsafe unsafe = Unsafe.getUnsafe();
  protected final long baseAddress;
  protected final long endAddress;
  protected long curAddress;
  protected final int capacity;
  
  protected RenderBuffer(int paramInt)
  {
    curAddress = (baseAddress = unsafe.allocateMemory(paramInt));
    endAddress = (baseAddress + paramInt);
    capacity = paramInt;
  }
  
  public static RenderBuffer allocate(int paramInt)
  {
    return new RenderBuffer(paramInt);
  }
  
  public final long getAddress()
  {
    return baseAddress;
  }
  
  public final int capacity()
  {
    return capacity;
  }
  
  public final int remaining()
  {
    return (int)(endAddress - curAddress);
  }
  
  public final int position()
  {
    return (int)(curAddress - baseAddress);
  }
  
  public final void position(long paramLong)
  {
    curAddress = (baseAddress + paramLong);
  }
  
  public final void clear()
  {
    curAddress = baseAddress;
  }
  
  public final RenderBuffer skip(long paramLong)
  {
    curAddress += paramLong;
    return this;
  }
  
  public final RenderBuffer putByte(byte paramByte)
  {
    unsafe.putByte(curAddress, paramByte);
    curAddress += 1L;
    return this;
  }
  
  public RenderBuffer put(byte[] paramArrayOfByte)
  {
    return put(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public RenderBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 6)
    {
      long l1 = paramInt1 * 1L + Unsafe.ARRAY_BYTE_BASE_OFFSET;
      long l2 = paramInt2 * 1L;
      unsafe.copyMemory(paramArrayOfByte, l1, null, curAddress, l2);
      position(position() + l2);
    }
    else
    {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++) {
        putByte(paramArrayOfByte[j]);
      }
    }
    return this;
  }
  
  public final RenderBuffer putShort(short paramShort)
  {
    unsafe.putShort(curAddress, paramShort);
    curAddress += 2L;
    return this;
  }
  
  public RenderBuffer put(short[] paramArrayOfShort)
  {
    return put(paramArrayOfShort, 0, paramArrayOfShort.length);
  }
  
  public RenderBuffer put(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 6)
    {
      long l1 = paramInt1 * 2L + Unsafe.ARRAY_SHORT_BASE_OFFSET;
      long l2 = paramInt2 * 2L;
      unsafe.copyMemory(paramArrayOfShort, l1, null, curAddress, l2);
      position(position() + l2);
    }
    else
    {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++) {
        putShort(paramArrayOfShort[j]);
      }
    }
    return this;
  }
  
  public final RenderBuffer putInt(int paramInt1, int paramInt2)
  {
    unsafe.putInt(baseAddress + paramInt1, paramInt2);
    return this;
  }
  
  public final RenderBuffer putInt(int paramInt)
  {
    unsafe.putInt(curAddress, paramInt);
    curAddress += 4L;
    return this;
  }
  
  public RenderBuffer put(int[] paramArrayOfInt)
  {
    return put(paramArrayOfInt, 0, paramArrayOfInt.length);
  }
  
  public RenderBuffer put(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 6)
    {
      long l1 = paramInt1 * 4L + Unsafe.ARRAY_INT_BASE_OFFSET;
      long l2 = paramInt2 * 4L;
      unsafe.copyMemory(paramArrayOfInt, l1, null, curAddress, l2);
      position(position() + l2);
    }
    else
    {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++) {
        putInt(paramArrayOfInt[j]);
      }
    }
    return this;
  }
  
  public final RenderBuffer putFloat(float paramFloat)
  {
    unsafe.putFloat(curAddress, paramFloat);
    curAddress += 4L;
    return this;
  }
  
  public RenderBuffer put(float[] paramArrayOfFloat)
  {
    return put(paramArrayOfFloat, 0, paramArrayOfFloat.length);
  }
  
  public RenderBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 6)
    {
      long l1 = paramInt1 * 4L + Unsafe.ARRAY_FLOAT_BASE_OFFSET;
      long l2 = paramInt2 * 4L;
      unsafe.copyMemory(paramArrayOfFloat, l1, null, curAddress, l2);
      position(position() + l2);
    }
    else
    {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++) {
        putFloat(paramArrayOfFloat[j]);
      }
    }
    return this;
  }
  
  public final RenderBuffer putLong(long paramLong)
  {
    unsafe.putLong(curAddress, paramLong);
    curAddress += 8L;
    return this;
  }
  
  public RenderBuffer put(long[] paramArrayOfLong)
  {
    return put(paramArrayOfLong, 0, paramArrayOfLong.length);
  }
  
  public RenderBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 6)
    {
      long l1 = paramInt1 * 8L + Unsafe.ARRAY_LONG_BASE_OFFSET;
      long l2 = paramInt2 * 8L;
      unsafe.copyMemory(paramArrayOfLong, l1, null, curAddress, l2);
      position(position() + l2);
    }
    else
    {
      int i = paramInt1 + paramInt2;
      for (int j = paramInt1; j < i; j++) {
        putLong(paramArrayOfLong[j]);
      }
    }
    return this;
  }
  
  public final RenderBuffer putDouble(double paramDouble)
  {
    unsafe.putDouble(curAddress, paramDouble);
    curAddress += 8L;
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\RenderBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */