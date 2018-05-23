package java.util.concurrent.locks;

import java.util.concurrent.ThreadLocalRandom;
import sun.misc.Unsafe;

public class LockSupport
{
  private static final Unsafe UNSAFE;
  private static final long parkBlockerOffset;
  private static final long SEED;
  private static final long PROBE;
  private static final long SECONDARY;
  
  private LockSupport() {}
  
  private static void setBlocker(Thread paramThread, Object paramObject)
  {
    UNSAFE.putObject(paramThread, parkBlockerOffset, paramObject);
  }
  
  public static void unpark(Thread paramThread)
  {
    if (paramThread != null) {
      UNSAFE.unpark(paramThread);
    }
  }
  
  public static void park(Object paramObject)
  {
    Thread localThread = Thread.currentThread();
    setBlocker(localThread, paramObject);
    UNSAFE.park(false, 0L);
    setBlocker(localThread, null);
  }
  
  public static void parkNanos(Object paramObject, long paramLong)
  {
    if (paramLong > 0L)
    {
      Thread localThread = Thread.currentThread();
      setBlocker(localThread, paramObject);
      UNSAFE.park(false, paramLong);
      setBlocker(localThread, null);
    }
  }
  
  public static void parkUntil(Object paramObject, long paramLong)
  {
    Thread localThread = Thread.currentThread();
    setBlocker(localThread, paramObject);
    UNSAFE.park(true, paramLong);
    setBlocker(localThread, null);
  }
  
  public static Object getBlocker(Thread paramThread)
  {
    if (paramThread == null) {
      throw new NullPointerException();
    }
    return UNSAFE.getObjectVolatile(paramThread, parkBlockerOffset);
  }
  
  public static void park()
  {
    UNSAFE.park(false, 0L);
  }
  
  public static void parkNanos(long paramLong)
  {
    if (paramLong > 0L) {
      UNSAFE.park(false, paramLong);
    }
  }
  
  public static void parkUntil(long paramLong)
  {
    UNSAFE.park(true, paramLong);
  }
  
  static final int nextSecondarySeed()
  {
    Thread localThread = Thread.currentThread();
    int i;
    if ((i = UNSAFE.getInt(localThread, SECONDARY)) != 0)
    {
      i ^= i << 13;
      i ^= i >>> 17;
      i ^= i << 5;
    }
    else if ((i = ThreadLocalRandom.current().nextInt()) == 0)
    {
      i = 1;
    }
    UNSAFE.putInt(localThread, SECONDARY, i);
    return i;
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = Thread.class;
      parkBlockerOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("parkBlocker"));
      SEED = UNSAFE.objectFieldOffset(localClass.getDeclaredField("threadLocalRandomSeed"));
      PROBE = UNSAFE.objectFieldOffset(localClass.getDeclaredField("threadLocalRandomProbe"));
      SECONDARY = UNSAFE.objectFieldOffset(localClass.getDeclaredField("threadLocalRandomSecondarySeed"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\locks\LockSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */