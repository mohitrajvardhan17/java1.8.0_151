package java.util.concurrent.atomic;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class AtomicReferenceArray<E>
  implements Serializable
{
  private static final long serialVersionUID = -6209656149925076980L;
  private static final Unsafe unsafe;
  private static final int base;
  private static final int shift;
  private static final long arrayFieldOffset;
  private final Object[] array;
  
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
  
  public AtomicReferenceArray(int paramInt)
  {
    array = new Object[paramInt];
  }
  
  public AtomicReferenceArray(E[] paramArrayOfE)
  {
    array = Arrays.copyOf(paramArrayOfE, paramArrayOfE.length, Object[].class);
  }
  
  public final int length()
  {
    return array.length;
  }
  
  public final E get(int paramInt)
  {
    return (E)getRaw(checkedByteOffset(paramInt));
  }
  
  private E getRaw(long paramLong)
  {
    return (E)unsafe.getObjectVolatile(array, paramLong);
  }
  
  public final void set(int paramInt, E paramE)
  {
    unsafe.putObjectVolatile(array, checkedByteOffset(paramInt), paramE);
  }
  
  public final void lazySet(int paramInt, E paramE)
  {
    unsafe.putOrderedObject(array, checkedByteOffset(paramInt), paramE);
  }
  
  public final E getAndSet(int paramInt, E paramE)
  {
    return (E)unsafe.getAndSetObject(array, checkedByteOffset(paramInt), paramE);
  }
  
  public final boolean compareAndSet(int paramInt, E paramE1, E paramE2)
  {
    return compareAndSetRaw(checkedByteOffset(paramInt), paramE1, paramE2);
  }
  
  private boolean compareAndSetRaw(long paramLong, E paramE1, E paramE2)
  {
    return unsafe.compareAndSwapObject(array, paramLong, paramE1, paramE2);
  }
  
  public final boolean weakCompareAndSet(int paramInt, E paramE1, E paramE2)
  {
    return compareAndSet(paramInt, paramE1, paramE2);
  }
  
  public final E getAndUpdate(int paramInt, UnaryOperator<E> paramUnaryOperator)
  {
    long l = checkedByteOffset(paramInt);
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = getRaw(l);
      localObject2 = paramUnaryOperator.apply(localObject1);
    } while (!compareAndSetRaw(l, localObject1, localObject2));
    return (E)localObject1;
  }
  
  public final E updateAndGet(int paramInt, UnaryOperator<E> paramUnaryOperator)
  {
    long l = checkedByteOffset(paramInt);
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = getRaw(l);
      localObject2 = paramUnaryOperator.apply(localObject1);
    } while (!compareAndSetRaw(l, localObject1, localObject2));
    return (E)localObject2;
  }
  
  public final E getAndAccumulate(int paramInt, E paramE, BinaryOperator<E> paramBinaryOperator)
  {
    long l = checkedByteOffset(paramInt);
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = getRaw(l);
      localObject2 = paramBinaryOperator.apply(localObject1, paramE);
    } while (!compareAndSetRaw(l, localObject1, localObject2));
    return (E)localObject1;
  }
  
  public final E accumulateAndGet(int paramInt, E paramE, BinaryOperator<E> paramBinaryOperator)
  {
    long l = checkedByteOffset(paramInt);
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = getRaw(l);
      localObject2 = paramBinaryOperator.apply(localObject1, paramE);
    } while (!compareAndSetRaw(l, localObject1, localObject2));
    return (E)localObject2;
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
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException, InvalidObjectException
  {
    Object localObject = paramObjectInputStream.readFields().get("array", null);
    if ((localObject == null) || (!localObject.getClass().isArray())) {
      throw new InvalidObjectException("Not array type");
    }
    if (localObject.getClass() != Object[].class) {
      localObject = Arrays.copyOf((Object[])localObject, Array.getLength(localObject), Object[].class);
    }
    unsafe.putObjectVolatile(this, arrayFieldOffset, localObject);
  }
  
  static
  {
    try
    {
      unsafe = Unsafe.getUnsafe();
      arrayFieldOffset = unsafe.objectFieldOffset(AtomicReferenceArray.class.getDeclaredField("array"));
      base = unsafe.arrayBaseOffset(Object[].class);
      int i = unsafe.arrayIndexScale(Object[].class);
      if ((i & i - 1) != 0) {
        throw new Error("data type scale not a power of two");
      }
      shift = 31 - Integer.numberOfLeadingZeros(i);
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\AtomicReferenceArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */