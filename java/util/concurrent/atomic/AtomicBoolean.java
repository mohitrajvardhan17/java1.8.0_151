package java.util.concurrent.atomic;

import java.io.Serializable;
import sun.misc.Unsafe;

public class AtomicBoolean
  implements Serializable
{
  private static final long serialVersionUID = 4654671469794556979L;
  private static final Unsafe unsafe = ;
  private static final long valueOffset;
  private volatile int value;
  
  public AtomicBoolean(boolean paramBoolean)
  {
    value = (paramBoolean ? 1 : 0);
  }
  
  public AtomicBoolean() {}
  
  public final boolean get()
  {
    return value != 0;
  }
  
  public final boolean compareAndSet(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramBoolean1 ? 1 : 0;
    int j = paramBoolean2 ? 1 : 0;
    return unsafe.compareAndSwapInt(this, valueOffset, i, j);
  }
  
  public boolean weakCompareAndSet(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramBoolean1 ? 1 : 0;
    int j = paramBoolean2 ? 1 : 0;
    return unsafe.compareAndSwapInt(this, valueOffset, i, j);
  }
  
  public final void set(boolean paramBoolean)
  {
    value = (paramBoolean ? 1 : 0);
  }
  
  public final void lazySet(boolean paramBoolean)
  {
    int i = paramBoolean ? 1 : 0;
    unsafe.putOrderedInt(this, valueOffset, i);
  }
  
  public final boolean getAndSet(boolean paramBoolean)
  {
    boolean bool;
    do
    {
      bool = get();
    } while (!compareAndSet(bool, paramBoolean));
    return bool;
  }
  
  public String toString()
  {
    return Boolean.toString(get());
  }
  
  static
  {
    try
    {
      valueOffset = unsafe.objectFieldOffset(AtomicBoolean.class.getDeclaredField("value"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\AtomicBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */