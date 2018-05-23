package java.util.concurrent.atomic;

import java.io.Serializable;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class AtomicReference<V>
  implements Serializable
{
  private static final long serialVersionUID = -1848883965231344442L;
  private static final Unsafe unsafe = ;
  private static final long valueOffset;
  private volatile V value;
  
  public AtomicReference(V paramV)
  {
    value = paramV;
  }
  
  public AtomicReference() {}
  
  public final V get()
  {
    return (V)value;
  }
  
  public final void set(V paramV)
  {
    value = paramV;
  }
  
  public final void lazySet(V paramV)
  {
    unsafe.putOrderedObject(this, valueOffset, paramV);
  }
  
  public final boolean compareAndSet(V paramV1, V paramV2)
  {
    return unsafe.compareAndSwapObject(this, valueOffset, paramV1, paramV2);
  }
  
  public final boolean weakCompareAndSet(V paramV1, V paramV2)
  {
    return unsafe.compareAndSwapObject(this, valueOffset, paramV1, paramV2);
  }
  
  public final V getAndSet(V paramV)
  {
    return (V)unsafe.getAndSetObject(this, valueOffset, paramV);
  }
  
  public final V getAndUpdate(UnaryOperator<V> paramUnaryOperator)
  {
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = get();
      localObject2 = paramUnaryOperator.apply(localObject1);
    } while (!compareAndSet(localObject1, localObject2));
    return (V)localObject1;
  }
  
  public final V updateAndGet(UnaryOperator<V> paramUnaryOperator)
  {
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = get();
      localObject2 = paramUnaryOperator.apply(localObject1);
    } while (!compareAndSet(localObject1, localObject2));
    return (V)localObject2;
  }
  
  public final V getAndAccumulate(V paramV, BinaryOperator<V> paramBinaryOperator)
  {
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = get();
      localObject2 = paramBinaryOperator.apply(localObject1, paramV);
    } while (!compareAndSet(localObject1, localObject2));
    return (V)localObject1;
  }
  
  public final V accumulateAndGet(V paramV, BinaryOperator<V> paramBinaryOperator)
  {
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = get();
      localObject2 = paramBinaryOperator.apply(localObject1, paramV);
    } while (!compareAndSet(localObject1, localObject2));
    return (V)localObject2;
  }
  
  public String toString()
  {
    return String.valueOf(get());
  }
  
  static
  {
    try
    {
      valueOffset = unsafe.objectFieldOffset(AtomicReference.class.getDeclaredField("value"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\AtomicReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */