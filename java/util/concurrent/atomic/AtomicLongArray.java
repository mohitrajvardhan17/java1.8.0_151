package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.misc.Unsafe;

public class AtomicLongArray
  implements Serializable
{
  private static final long serialVersionUID = -2308431214976778248L;
  private static final Unsafe unsafe = ;
  private static final int base = unsafe.arrayBaseOffset(long[].class);
  private static final int shift;
  private final long[] array;
  
  private long checkedByteOffset(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= array.length)) {
      throw new IndexOutOfBoundsException("index " + paramInt);
    }
    return byteOffset(paramInt);
  }
  
  private static long byteOffset(int paramInt)
  {
    return (paramInt << shift) + base;
  }
  
  public AtomicLongArray(int paramInt)
  {
    array = new long[paramInt];
  }
  
  public AtomicLongArray(long[] paramArrayOfLong)
  {
    array = ((long[])paramArrayOfLong.clone());
  }
  
  public final int length()
  {
    return array.length;
  }
  
  public final long get(int paramInt)
  {
    return getRaw(checkedByteOffset(paramInt));
  }
  
  private long getRaw(long paramLong)
  {
    return unsafe.getLongVolatile(array, paramLong);
  }
  
  public final void set(int paramInt, long paramLong)
  {
    unsafe.putLongVolatile(array, checkedByteOffset(paramInt), paramLong);
  }
  
  public final void lazySet(int paramInt, long paramLong)
  {
    unsafe.putOrderedLong(array, checkedByteOffset(paramInt), paramLong);
  }
  
  public final long getAndSet(int paramInt, long paramLong)
  {
    return unsafe.getAndSetLong(array, checkedByteOffset(paramInt), paramLong);
  }
  
  public final boolean compareAndSet(int paramInt, long paramLong1, long paramLong2)
  {
    return compareAndSetRaw(checkedByteOffset(paramInt), paramLong1, paramLong2);
  }
  
  private boolean compareAndSetRaw(long paramLong1, long paramLong2, long paramLong3)
  {
    return unsafe.compareAndSwapLong(array, paramLong1, paramLong2, paramLong3);
  }
  
  public final boolean weakCompareAndSet(int paramInt, long paramLong1, long paramLong2)
  {
    return compareAndSet(paramInt, paramLong1, paramLong2);
  }
  
  public final long getAndIncrement(int paramInt)
  {
    return getAndAdd(paramInt, 1L);
  }
  
  public final long getAndDecrement(int paramInt)
  {
    return getAndAdd(paramInt, -1L);
  }
  
  public final long getAndAdd(int paramInt, long paramLong)
  {
    return unsafe.getAndAddLong(array, checkedByteOffset(paramInt), paramLong);
  }
  
  public final long incrementAndGet(int paramInt)
  {
    return getAndAdd(paramInt, 1L) + 1L;
  }
  
  public final long decrementAndGet(int paramInt)
  {
    return getAndAdd(paramInt, -1L) - 1L;
  }
  
  public long addAndGet(int paramInt, long paramLong)
  {
    return getAndAdd(paramInt, paramLong) + paramLong;
  }
  
  public final long getAndUpdate(int paramInt, LongUnaryOperator paramLongUnaryOperator)
  {
    long l1 = checkedByteOffset(paramInt);
    long l2;
    long l3;
    do
    {
      l2 = getRaw(l1);
      l3 = paramLongUnaryOperator.applyAsLong(l2);
    } while (!compareAndSetRaw(l1, l2, l3));
    return l2;
  }
  
  public final long updateAndGet(int paramInt, LongUnaryOperator paramLongUnaryOperator)
  {
    long l1 = checkedByteOffset(paramInt);
    long l2;
    long l3;
    do
    {
      l2 = getRaw(l1);
      l3 = paramLongUnaryOperator.applyAsLong(l2);
    } while (!compareAndSetRaw(l1, l2, l3));
    return l3;
  }
  
  public final long getAndAccumulate(int paramInt, long paramLong, LongBinaryOperator paramLongBinaryOperator)
  {
    long l1 = checkedByteOffset(paramInt);
    long l2;
    long l3;
    do
    {
      l2 = getRaw(l1);
      l3 = paramLongBinaryOperator.applyAsLong(l2, paramLong);
    } while (!compareAndSetRaw(l1, l2, l3));
    return l2;
  }
  
  public final long accumulateAndGet(int paramInt, long paramLong, LongBinaryOperator paramLongBinaryOperator)
  {
    long l1 = checkedByteOffset(paramInt);
    long l2;
    long l3;
    do
    {
      l2 = getRaw(l1);
      l3 = paramLongBinaryOperator.applyAsLong(l2, paramLong);
    } while (!compareAndSetRaw(l1, l2, l3));
    return l3;
  }
  
  public String toString()
  {
    int i = array.length - 1;
    if (i == -1) {
      return "[]";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    for (int j = 0;; j++)
    {
      localStringBuilder.append(getRaw(byteOffset(j)));
      if (j == i) {
        return ']';
      }
      localStringBuilder.append(',').append(' ');
    }
  }
  
  static
  {
    int i = unsafe.arrayIndexScale(long[].class);
    if ((i & i - 1) != 0) {
      throw new Error("data type scale not a power of two");
    }
    shift = 31 - Integer.numberOfLeadingZeros(i);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\AtomicLongArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */