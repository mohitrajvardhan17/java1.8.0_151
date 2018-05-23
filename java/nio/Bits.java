package java.nio;

import java.security.AccessController;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.JavaLangRefAccess;
import sun.misc.JavaNioAccess;
import sun.misc.JavaNioAccess.BufferPool;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.security.action.GetPropertyAction;

class Bits
{
  private static final Unsafe unsafe;
  private static final ByteOrder byteOrder;
  private static int pageSize;
  private static boolean unaligned;
  private static boolean unalignedKnown;
  private static volatile long maxMemory;
  private static final AtomicLong reservedMemory;
  private static final AtomicLong totalCapacity;
  private static final AtomicLong count;
  private static volatile boolean memoryLimitSet;
  private static final int MAX_SLEEPS = 9;
  static final int JNI_COPY_TO_ARRAY_THRESHOLD = 6;
  static final int JNI_COPY_FROM_ARRAY_THRESHOLD = 6;
  static final long UNSAFE_COPY_THRESHOLD = 1048576L;
  
  private Bits() {}
  
  static short swap(short paramShort)
  {
    return Short.reverseBytes(paramShort);
  }
  
  static char swap(char paramChar)
  {
    return Character.reverseBytes(paramChar);
  }
  
  static int swap(int paramInt)
  {
    return Integer.reverseBytes(paramInt);
  }
  
  static long swap(long paramLong)
  {
    return Long.reverseBytes(paramLong);
  }
  
  private static char makeChar(byte paramByte1, byte paramByte2)
  {
    return (char)(paramByte1 << 8 | paramByte2 & 0xFF);
  }
  
  static char getCharL(ByteBuffer paramByteBuffer, int paramInt)
  {
    return makeChar(paramByteBuffer._get(paramInt + 1), paramByteBuffer._get(paramInt));
  }
  
  static char getCharL(long paramLong)
  {
    return makeChar(_get(paramLong + 1L), _get(paramLong));
  }
  
  static char getCharB(ByteBuffer paramByteBuffer, int paramInt)
  {
    return makeChar(paramByteBuffer._get(paramInt), paramByteBuffer._get(paramInt + 1));
  }
  
  static char getCharB(long paramLong)
  {
    return makeChar(_get(paramLong), _get(paramLong + 1L));
  }
  
  static char getChar(ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean)
  {
    return paramBoolean ? getCharB(paramByteBuffer, paramInt) : getCharL(paramByteBuffer, paramInt);
  }
  
  static char getChar(long paramLong, boolean paramBoolean)
  {
    return paramBoolean ? getCharB(paramLong) : getCharL(paramLong);
  }
  
  private static byte char1(char paramChar)
  {
    return (byte)(paramChar >> '\b');
  }
  
  private static byte char0(char paramChar)
  {
    return (byte)paramChar;
  }
  
  static void putCharL(ByteBuffer paramByteBuffer, int paramInt, char paramChar)
  {
    paramByteBuffer._put(paramInt, char0(paramChar));
    paramByteBuffer._put(paramInt + 1, char1(paramChar));
  }
  
  static void putCharL(long paramLong, char paramChar)
  {
    _put(paramLong, char0(paramChar));
    _put(paramLong + 1L, char1(paramChar));
  }
  
  static void putCharB(ByteBuffer paramByteBuffer, int paramInt, char paramChar)
  {
    paramByteBuffer._put(paramInt, char1(paramChar));
    paramByteBuffer._put(paramInt + 1, char0(paramChar));
  }
  
  static void putCharB(long paramLong, char paramChar)
  {
    _put(paramLong, char1(paramChar));
    _put(paramLong + 1L, char0(paramChar));
  }
  
  static void putChar(ByteBuffer paramByteBuffer, int paramInt, char paramChar, boolean paramBoolean)
  {
    if (paramBoolean) {
      putCharB(paramByteBuffer, paramInt, paramChar);
    } else {
      putCharL(paramByteBuffer, paramInt, paramChar);
    }
  }
  
  static void putChar(long paramLong, char paramChar, boolean paramBoolean)
  {
    if (paramBoolean) {
      putCharB(paramLong, paramChar);
    } else {
      putCharL(paramLong, paramChar);
    }
  }
  
  private static short makeShort(byte paramByte1, byte paramByte2)
  {
    return (short)(paramByte1 << 8 | paramByte2 & 0xFF);
  }
  
  static short getShortL(ByteBuffer paramByteBuffer, int paramInt)
  {
    return makeShort(paramByteBuffer._get(paramInt + 1), paramByteBuffer._get(paramInt));
  }
  
  static short getShortL(long paramLong)
  {
    return makeShort(_get(paramLong + 1L), _get(paramLong));
  }
  
  static short getShortB(ByteBuffer paramByteBuffer, int paramInt)
  {
    return makeShort(paramByteBuffer._get(paramInt), paramByteBuffer._get(paramInt + 1));
  }
  
  static short getShortB(long paramLong)
  {
    return makeShort(_get(paramLong), _get(paramLong + 1L));
  }
  
  static short getShort(ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean)
  {
    return paramBoolean ? getShortB(paramByteBuffer, paramInt) : getShortL(paramByteBuffer, paramInt);
  }
  
  static short getShort(long paramLong, boolean paramBoolean)
  {
    return paramBoolean ? getShortB(paramLong) : getShortL(paramLong);
  }
  
  private static byte short1(short paramShort)
  {
    return (byte)(paramShort >> 8);
  }
  
  private static byte short0(short paramShort)
  {
    return (byte)paramShort;
  }
  
  static void putShortL(ByteBuffer paramByteBuffer, int paramInt, short paramShort)
  {
    paramByteBuffer._put(paramInt, short0(paramShort));
    paramByteBuffer._put(paramInt + 1, short1(paramShort));
  }
  
  static void putShortL(long paramLong, short paramShort)
  {
    _put(paramLong, short0(paramShort));
    _put(paramLong + 1L, short1(paramShort));
  }
  
  static void putShortB(ByteBuffer paramByteBuffer, int paramInt, short paramShort)
  {
    paramByteBuffer._put(paramInt, short1(paramShort));
    paramByteBuffer._put(paramInt + 1, short0(paramShort));
  }
  
  static void putShortB(long paramLong, short paramShort)
  {
    _put(paramLong, short1(paramShort));
    _put(paramLong + 1L, short0(paramShort));
  }
  
  static void putShort(ByteBuffer paramByteBuffer, int paramInt, short paramShort, boolean paramBoolean)
  {
    if (paramBoolean) {
      putShortB(paramByteBuffer, paramInt, paramShort);
    } else {
      putShortL(paramByteBuffer, paramInt, paramShort);
    }
  }
  
  static void putShort(long paramLong, short paramShort, boolean paramBoolean)
  {
    if (paramBoolean) {
      putShortB(paramLong, paramShort);
    } else {
      putShortL(paramLong, paramShort);
    }
  }
  
  private static int makeInt(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    return paramByte1 << 24 | (paramByte2 & 0xFF) << 16 | (paramByte3 & 0xFF) << 8 | paramByte4 & 0xFF;
  }
  
  static int getIntL(ByteBuffer paramByteBuffer, int paramInt)
  {
    return makeInt(paramByteBuffer._get(paramInt + 3), paramByteBuffer._get(paramInt + 2), paramByteBuffer._get(paramInt + 1), paramByteBuffer._get(paramInt));
  }
  
  static int getIntL(long paramLong)
  {
    return makeInt(_get(paramLong + 3L), _get(paramLong + 2L), _get(paramLong + 1L), _get(paramLong));
  }
  
  static int getIntB(ByteBuffer paramByteBuffer, int paramInt)
  {
    return makeInt(paramByteBuffer._get(paramInt), paramByteBuffer._get(paramInt + 1), paramByteBuffer._get(paramInt + 2), paramByteBuffer._get(paramInt + 3));
  }
  
  static int getIntB(long paramLong)
  {
    return makeInt(_get(paramLong), _get(paramLong + 1L), _get(paramLong + 2L), _get(paramLong + 3L));
  }
  
  static int getInt(ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean)
  {
    return paramBoolean ? getIntB(paramByteBuffer, paramInt) : getIntL(paramByteBuffer, paramInt);
  }
  
  static int getInt(long paramLong, boolean paramBoolean)
  {
    return paramBoolean ? getIntB(paramLong) : getIntL(paramLong);
  }
  
  private static byte int3(int paramInt)
  {
    return (byte)(paramInt >> 24);
  }
  
  private static byte int2(int paramInt)
  {
    return (byte)(paramInt >> 16);
  }
  
  private static byte int1(int paramInt)
  {
    return (byte)(paramInt >> 8);
  }
  
  private static byte int0(int paramInt)
  {
    return (byte)paramInt;
  }
  
  static void putIntL(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    paramByteBuffer._put(paramInt1 + 3, int3(paramInt2));
    paramByteBuffer._put(paramInt1 + 2, int2(paramInt2));
    paramByteBuffer._put(paramInt1 + 1, int1(paramInt2));
    paramByteBuffer._put(paramInt1, int0(paramInt2));
  }
  
  static void putIntL(long paramLong, int paramInt)
  {
    _put(paramLong + 3L, int3(paramInt));
    _put(paramLong + 2L, int2(paramInt));
    _put(paramLong + 1L, int1(paramInt));
    _put(paramLong, int0(paramInt));
  }
  
  static void putIntB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
  {
    paramByteBuffer._put(paramInt1, int3(paramInt2));
    paramByteBuffer._put(paramInt1 + 1, int2(paramInt2));
    paramByteBuffer._put(paramInt1 + 2, int1(paramInt2));
    paramByteBuffer._put(paramInt1 + 3, int0(paramInt2));
  }
  
  static void putIntB(long paramLong, int paramInt)
  {
    _put(paramLong, int3(paramInt));
    _put(paramLong + 1L, int2(paramInt));
    _put(paramLong + 2L, int1(paramInt));
    _put(paramLong + 3L, int0(paramInt));
  }
  
  static void putInt(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean) {
      putIntB(paramByteBuffer, paramInt1, paramInt2);
    } else {
      putIntL(paramByteBuffer, paramInt1, paramInt2);
    }
  }
  
  static void putInt(long paramLong, int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      putIntB(paramLong, paramInt);
    } else {
      putIntL(paramLong, paramInt);
    }
  }
  
  private static long makeLong(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte paramByte5, byte paramByte6, byte paramByte7, byte paramByte8)
  {
    return paramByte1 << 56 | (paramByte2 & 0xFF) << 48 | (paramByte3 & 0xFF) << 40 | (paramByte4 & 0xFF) << 32 | (paramByte5 & 0xFF) << 24 | (paramByte6 & 0xFF) << 16 | (paramByte7 & 0xFF) << 8 | paramByte8 & 0xFF;
  }
  
  static long getLongL(ByteBuffer paramByteBuffer, int paramInt)
  {
    return makeLong(paramByteBuffer._get(paramInt + 7), paramByteBuffer._get(paramInt + 6), paramByteBuffer._get(paramInt + 5), paramByteBuffer._get(paramInt + 4), paramByteBuffer._get(paramInt + 3), paramByteBuffer._get(paramInt + 2), paramByteBuffer._get(paramInt + 1), paramByteBuffer._get(paramInt));
  }
  
  static long getLongL(long paramLong)
  {
    return makeLong(_get(paramLong + 7L), _get(paramLong + 6L), _get(paramLong + 5L), _get(paramLong + 4L), _get(paramLong + 3L), _get(paramLong + 2L), _get(paramLong + 1L), _get(paramLong));
  }
  
  static long getLongB(ByteBuffer paramByteBuffer, int paramInt)
  {
    return makeLong(paramByteBuffer._get(paramInt), paramByteBuffer._get(paramInt + 1), paramByteBuffer._get(paramInt + 2), paramByteBuffer._get(paramInt + 3), paramByteBuffer._get(paramInt + 4), paramByteBuffer._get(paramInt + 5), paramByteBuffer._get(paramInt + 6), paramByteBuffer._get(paramInt + 7));
  }
  
  static long getLongB(long paramLong)
  {
    return makeLong(_get(paramLong), _get(paramLong + 1L), _get(paramLong + 2L), _get(paramLong + 3L), _get(paramLong + 4L), _get(paramLong + 5L), _get(paramLong + 6L), _get(paramLong + 7L));
  }
  
  static long getLong(ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean)
  {
    return paramBoolean ? getLongB(paramByteBuffer, paramInt) : getLongL(paramByteBuffer, paramInt);
  }
  
  static long getLong(long paramLong, boolean paramBoolean)
  {
    return paramBoolean ? getLongB(paramLong) : getLongL(paramLong);
  }
  
  private static byte long7(long paramLong)
  {
    return (byte)(int)(paramLong >> 56);
  }
  
  private static byte long6(long paramLong)
  {
    return (byte)(int)(paramLong >> 48);
  }
  
  private static byte long5(long paramLong)
  {
    return (byte)(int)(paramLong >> 40);
  }
  
  private static byte long4(long paramLong)
  {
    return (byte)(int)(paramLong >> 32);
  }
  
  private static byte long3(long paramLong)
  {
    return (byte)(int)(paramLong >> 24);
  }
  
  private static byte long2(long paramLong)
  {
    return (byte)(int)(paramLong >> 16);
  }
  
  private static byte long1(long paramLong)
  {
    return (byte)(int)(paramLong >> 8);
  }
  
  private static byte long0(long paramLong)
  {
    return (byte)(int)paramLong;
  }
  
  static void putLongL(ByteBuffer paramByteBuffer, int paramInt, long paramLong)
  {
    paramByteBuffer._put(paramInt + 7, long7(paramLong));
    paramByteBuffer._put(paramInt + 6, long6(paramLong));
    paramByteBuffer._put(paramInt + 5, long5(paramLong));
    paramByteBuffer._put(paramInt + 4, long4(paramLong));
    paramByteBuffer._put(paramInt + 3, long3(paramLong));
    paramByteBuffer._put(paramInt + 2, long2(paramLong));
    paramByteBuffer._put(paramInt + 1, long1(paramLong));
    paramByteBuffer._put(paramInt, long0(paramLong));
  }
  
  static void putLongL(long paramLong1, long paramLong2)
  {
    _put(paramLong1 + 7L, long7(paramLong2));
    _put(paramLong1 + 6L, long6(paramLong2));
    _put(paramLong1 + 5L, long5(paramLong2));
    _put(paramLong1 + 4L, long4(paramLong2));
    _put(paramLong1 + 3L, long3(paramLong2));
    _put(paramLong1 + 2L, long2(paramLong2));
    _put(paramLong1 + 1L, long1(paramLong2));
    _put(paramLong1, long0(paramLong2));
  }
  
  static void putLongB(ByteBuffer paramByteBuffer, int paramInt, long paramLong)
  {
    paramByteBuffer._put(paramInt, long7(paramLong));
    paramByteBuffer._put(paramInt + 1, long6(paramLong));
    paramByteBuffer._put(paramInt + 2, long5(paramLong));
    paramByteBuffer._put(paramInt + 3, long4(paramLong));
    paramByteBuffer._put(paramInt + 4, long3(paramLong));
    paramByteBuffer._put(paramInt + 5, long2(paramLong));
    paramByteBuffer._put(paramInt + 6, long1(paramLong));
    paramByteBuffer._put(paramInt + 7, long0(paramLong));
  }
  
  static void putLongB(long paramLong1, long paramLong2)
  {
    _put(paramLong1, long7(paramLong2));
    _put(paramLong1 + 1L, long6(paramLong2));
    _put(paramLong1 + 2L, long5(paramLong2));
    _put(paramLong1 + 3L, long4(paramLong2));
    _put(paramLong1 + 4L, long3(paramLong2));
    _put(paramLong1 + 5L, long2(paramLong2));
    _put(paramLong1 + 6L, long1(paramLong2));
    _put(paramLong1 + 7L, long0(paramLong2));
  }
  
  static void putLong(ByteBuffer paramByteBuffer, int paramInt, long paramLong, boolean paramBoolean)
  {
    if (paramBoolean) {
      putLongB(paramByteBuffer, paramInt, paramLong);
    } else {
      putLongL(paramByteBuffer, paramInt, paramLong);
    }
  }
  
  static void putLong(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    if (paramBoolean) {
      putLongB(paramLong1, paramLong2);
    } else {
      putLongL(paramLong1, paramLong2);
    }
  }
  
  static float getFloatL(ByteBuffer paramByteBuffer, int paramInt)
  {
    return Float.intBitsToFloat(getIntL(paramByteBuffer, paramInt));
  }
  
  static float getFloatL(long paramLong)
  {
    return Float.intBitsToFloat(getIntL(paramLong));
  }
  
  static float getFloatB(ByteBuffer paramByteBuffer, int paramInt)
  {
    return Float.intBitsToFloat(getIntB(paramByteBuffer, paramInt));
  }
  
  static float getFloatB(long paramLong)
  {
    return Float.intBitsToFloat(getIntB(paramLong));
  }
  
  static float getFloat(ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean)
  {
    return paramBoolean ? getFloatB(paramByteBuffer, paramInt) : getFloatL(paramByteBuffer, paramInt);
  }
  
  static float getFloat(long paramLong, boolean paramBoolean)
  {
    return paramBoolean ? getFloatB(paramLong) : getFloatL(paramLong);
  }
  
  static void putFloatL(ByteBuffer paramByteBuffer, int paramInt, float paramFloat)
  {
    putIntL(paramByteBuffer, paramInt, Float.floatToRawIntBits(paramFloat));
  }
  
  static void putFloatL(long paramLong, float paramFloat)
  {
    putIntL(paramLong, Float.floatToRawIntBits(paramFloat));
  }
  
  static void putFloatB(ByteBuffer paramByteBuffer, int paramInt, float paramFloat)
  {
    putIntB(paramByteBuffer, paramInt, Float.floatToRawIntBits(paramFloat));
  }
  
  static void putFloatB(long paramLong, float paramFloat)
  {
    putIntB(paramLong, Float.floatToRawIntBits(paramFloat));
  }
  
  static void putFloat(ByteBuffer paramByteBuffer, int paramInt, float paramFloat, boolean paramBoolean)
  {
    if (paramBoolean) {
      putFloatB(paramByteBuffer, paramInt, paramFloat);
    } else {
      putFloatL(paramByteBuffer, paramInt, paramFloat);
    }
  }
  
  static void putFloat(long paramLong, float paramFloat, boolean paramBoolean)
  {
    if (paramBoolean) {
      putFloatB(paramLong, paramFloat);
    } else {
      putFloatL(paramLong, paramFloat);
    }
  }
  
  static double getDoubleL(ByteBuffer paramByteBuffer, int paramInt)
  {
    return Double.longBitsToDouble(getLongL(paramByteBuffer, paramInt));
  }
  
  static double getDoubleL(long paramLong)
  {
    return Double.longBitsToDouble(getLongL(paramLong));
  }
  
  static double getDoubleB(ByteBuffer paramByteBuffer, int paramInt)
  {
    return Double.longBitsToDouble(getLongB(paramByteBuffer, paramInt));
  }
  
  static double getDoubleB(long paramLong)
  {
    return Double.longBitsToDouble(getLongB(paramLong));
  }
  
  static double getDouble(ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean)
  {
    return paramBoolean ? getDoubleB(paramByteBuffer, paramInt) : getDoubleL(paramByteBuffer, paramInt);
  }
  
  static double getDouble(long paramLong, boolean paramBoolean)
  {
    return paramBoolean ? getDoubleB(paramLong) : getDoubleL(paramLong);
  }
  
  static void putDoubleL(ByteBuffer paramByteBuffer, int paramInt, double paramDouble)
  {
    putLongL(paramByteBuffer, paramInt, Double.doubleToRawLongBits(paramDouble));
  }
  
  static void putDoubleL(long paramLong, double paramDouble)
  {
    putLongL(paramLong, Double.doubleToRawLongBits(paramDouble));
  }
  
  static void putDoubleB(ByteBuffer paramByteBuffer, int paramInt, double paramDouble)
  {
    putLongB(paramByteBuffer, paramInt, Double.doubleToRawLongBits(paramDouble));
  }
  
  static void putDoubleB(long paramLong, double paramDouble)
  {
    putLongB(paramLong, Double.doubleToRawLongBits(paramDouble));
  }
  
  static void putDouble(ByteBuffer paramByteBuffer, int paramInt, double paramDouble, boolean paramBoolean)
  {
    if (paramBoolean) {
      putDoubleB(paramByteBuffer, paramInt, paramDouble);
    } else {
      putDoubleL(paramByteBuffer, paramInt, paramDouble);
    }
  }
  
  static void putDouble(long paramLong, double paramDouble, boolean paramBoolean)
  {
    if (paramBoolean) {
      putDoubleB(paramLong, paramDouble);
    } else {
      putDoubleL(paramLong, paramDouble);
    }
  }
  
  private static byte _get(long paramLong)
  {
    return unsafe.getByte(paramLong);
  }
  
  private static void _put(long paramLong, byte paramByte)
  {
    unsafe.putByte(paramLong, paramByte);
  }
  
  static Unsafe unsafe()
  {
    return unsafe;
  }
  
  static ByteOrder byteOrder()
  {
    if (byteOrder == null) {
      throw new Error("Unknown byte order");
    }
    return byteOrder;
  }
  
  static int pageSize()
  {
    if (pageSize == -1) {
      pageSize = unsafe().pageSize();
    }
    return pageSize;
  }
  
  static int pageCount(long paramLong)
  {
    return (int)(paramLong + pageSize() - 1L) / pageSize();
  }
  
  static boolean unaligned()
  {
    if (unalignedKnown) {
      return unaligned;
    }
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.arch"));
    unaligned = (str.equals("i386")) || (str.equals("x86")) || (str.equals("amd64")) || (str.equals("x86_64")) || (str.equals("ppc64")) || (str.equals("ppc64le"));
    unalignedKnown = true;
    return unaligned;
  }
  
  static void reserveMemory(long paramLong, int paramInt)
  {
    if ((!memoryLimitSet) && (VM.isBooted()))
    {
      maxMemory = VM.maxDirectMemory();
      memoryLimitSet = true;
    }
    if (tryReserveMemory(paramLong, paramInt)) {
      return;
    }
    JavaLangRefAccess localJavaLangRefAccess = SharedSecrets.getJavaLangRefAccess();
    while (localJavaLangRefAccess.tryHandlePendingReference()) {
      if (tryReserveMemory(paramLong, paramInt)) {
        return;
      }
    }
    System.gc();
    int i = 0;
    try
    {
      long l = 1L;
      int j = 0;
      for (;;)
      {
        if (tryReserveMemory(paramLong, paramInt)) {
          return;
        }
        if (j >= 9) {
          break;
        }
        if (!localJavaLangRefAccess.tryHandlePendingReference()) {
          try
          {
            Thread.sleep(l);
            l <<= 1;
            j++;
          }
          catch (InterruptedException localInterruptedException)
          {
            i = 1;
          }
        }
      }
      throw new OutOfMemoryError("Direct buffer memory");
    }
    finally
    {
      if (i != 0) {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  private static boolean tryReserveMemory(long paramLong, int paramInt)
  {
    long l;
    while (paramInt <= maxMemory - (l = totalCapacity.get())) {
      if (totalCapacity.compareAndSet(l, l + paramInt))
      {
        reservedMemory.addAndGet(paramLong);
        count.incrementAndGet();
        return true;
      }
    }
    return false;
  }
  
  static void unreserveMemory(long paramLong, int paramInt)
  {
    long l1 = count.decrementAndGet();
    long l2 = reservedMemory.addAndGet(-paramLong);
    long l3 = totalCapacity.addAndGet(-paramInt);
    assert ((l1 >= 0L) && (l2 >= 0L) && (l3 >= 0L));
  }
  
  static void copyFromArray(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    long l1 = paramLong1 + paramLong2;
    while (paramLong4 > 0L)
    {
      long l2 = paramLong4 > 1048576L ? 1048576L : paramLong4;
      unsafe.copyMemory(paramObject, l1, null, paramLong3, l2);
      paramLong4 -= l2;
      l1 += l2;
      paramLong3 += l2;
    }
  }
  
  static void copyToArray(long paramLong1, Object paramObject, long paramLong2, long paramLong3, long paramLong4)
  {
    long l2;
    for (long l1 = paramLong2 + paramLong3; paramLong4 > 0L; l1 += l2)
    {
      l2 = paramLong4 > 1048576L ? 1048576L : paramLong4;
      unsafe.copyMemory(null, paramLong1, paramObject, l1, l2);
      paramLong4 -= l2;
      paramLong1 += l2;
    }
  }
  
  static void copyFromCharArray(Object paramObject, long paramLong1, long paramLong2, long paramLong3)
  {
    copyFromShortArray(paramObject, paramLong1, paramLong2, paramLong3);
  }
  
  static void copyToCharArray(long paramLong1, Object paramObject, long paramLong2, long paramLong3)
  {
    copyToShortArray(paramLong1, paramObject, paramLong2, paramLong3);
  }
  
  static native void copyFromShortArray(Object paramObject, long paramLong1, long paramLong2, long paramLong3);
  
  static native void copyToShortArray(long paramLong1, Object paramObject, long paramLong2, long paramLong3);
  
  static native void copyFromIntArray(Object paramObject, long paramLong1, long paramLong2, long paramLong3);
  
  static native void copyToIntArray(long paramLong1, Object paramObject, long paramLong2, long paramLong3);
  
  static native void copyFromLongArray(Object paramObject, long paramLong1, long paramLong2, long paramLong3);
  
  static native void copyToLongArray(long paramLong1, Object paramObject, long paramLong2, long paramLong3);
  
  /* Error */
  static
  {
    // Byte code:
    //   0: ldc 10
    //   2: invokevirtual 452	java/lang/Class:desiredAssertionStatus	()Z
    //   5: ifne +7 -> 12
    //   8: iconst_1
    //   9: goto +4 -> 13
    //   12: iconst_0
    //   13: putstatic 439	java/nio/Bits:$assertionsDisabled	Z
    //   16: invokestatic 561	sun/misc/Unsafe:getUnsafe	()Lsun/misc/Unsafe;
    //   19: putstatic 447	java/nio/Bits:unsafe	Lsun/misc/Unsafe;
    //   22: getstatic 447	java/nio/Bits:unsafe	Lsun/misc/Unsafe;
    //   25: ldc2_w 240
    //   28: invokevirtual 557	sun/misc/Unsafe:allocateMemory	(J)J
    //   31: lstore_0
    //   32: getstatic 447	java/nio/Bits:unsafe	Lsun/misc/Unsafe;
    //   35: lload_0
    //   36: ldc2_w 246
    //   39: invokevirtual 560	sun/misc/Unsafe:putLong	(JJ)V
    //   42: getstatic 447	java/nio/Bits:unsafe	Lsun/misc/Unsafe;
    //   45: lload_0
    //   46: invokevirtual 556	sun/misc/Unsafe:getByte	(J)B
    //   49: istore_2
    //   50: iload_2
    //   51: lookupswitch	default:+43->94, 1:+25->76, 8:+34->85
    //   76: getstatic 448	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   79: putstatic 443	java/nio/Bits:byteOrder	Ljava/nio/ByteOrder;
    //   82: goto +30 -> 112
    //   85: getstatic 449	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
    //   88: putstatic 443	java/nio/Bits:byteOrder	Ljava/nio/ByteOrder;
    //   91: goto +21 -> 112
    //   94: getstatic 439	java/nio/Bits:$assertionsDisabled	Z
    //   97: ifne +11 -> 108
    //   100: new 248	java/lang/AssertionError
    //   103: dup
    //   104: invokespecial 450	java/lang/AssertionError:<init>	()V
    //   107: athrow
    //   108: aconst_null
    //   109: putstatic 443	java/nio/Bits:byteOrder	Ljava/nio/ByteOrder;
    //   112: getstatic 447	java/nio/Bits:unsafe	Lsun/misc/Unsafe;
    //   115: lload_0
    //   116: invokevirtual 558	sun/misc/Unsafe:freeMemory	(J)V
    //   119: goto +13 -> 132
    //   122: astore_3
    //   123: getstatic 447	java/nio/Bits:unsafe	Lsun/misc/Unsafe;
    //   126: lload_0
    //   127: invokevirtual 558	sun/misc/Unsafe:freeMemory	(J)V
    //   130: aload_3
    //   131: athrow
    //   132: iconst_m1
    //   133: putstatic 437	java/nio/Bits:pageSize	I
    //   136: iconst_0
    //   137: putstatic 442	java/nio/Bits:unalignedKnown	Z
    //   140: invokestatic 563	sun/misc/VM:maxDirectMemory	()J
    //   143: putstatic 438	java/nio/Bits:maxMemory	J
    //   146: new 268	java/util/concurrent/atomic/AtomicLong
    //   149: dup
    //   150: invokespecial 550	java/util/concurrent/atomic/AtomicLong:<init>	()V
    //   153: putstatic 445	java/nio/Bits:reservedMemory	Ljava/util/concurrent/atomic/AtomicLong;
    //   156: new 268	java/util/concurrent/atomic/AtomicLong
    //   159: dup
    //   160: invokespecial 550	java/util/concurrent/atomic/AtomicLong:<init>	()V
    //   163: putstatic 446	java/nio/Bits:totalCapacity	Ljava/util/concurrent/atomic/AtomicLong;
    //   166: new 268	java/util/concurrent/atomic/AtomicLong
    //   169: dup
    //   170: invokespecial 550	java/util/concurrent/atomic/AtomicLong:<init>	()V
    //   173: putstatic 444	java/nio/Bits:count	Ljava/util/concurrent/atomic/AtomicLong;
    //   176: iconst_0
    //   177: putstatic 440	java/nio/Bits:memoryLimitSet	Z
    //   180: new 264	java/nio/Bits$1
    //   183: dup
    //   184: invokespecial 543	java/nio/Bits$1:<init>	()V
    //   187: invokestatic 554	sun/misc/SharedSecrets:setJavaNioAccess	(Lsun/misc/JavaNioAccess;)V
    //   190: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   31	96	0	l	long
    //   49	2	2	i	int
    //   122	9	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   32	112	122	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\Bits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */