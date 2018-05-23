package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicLongFieldUpdater<T>
{
  @CallerSensitive
  public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> paramClass, String paramString)
  {
    Class localClass = Reflection.getCallerClass();
    if (AtomicLong.VM_SUPPORTS_LONG_CAS) {
      return new CASUpdater(paramClass, paramString, localClass);
    }
    return new LockedUpdater(paramClass, paramString, localClass);
  }
  
  protected AtomicLongFieldUpdater() {}
  
  public abstract boolean compareAndSet(T paramT, long paramLong1, long paramLong2);
  
  public abstract boolean weakCompareAndSet(T paramT, long paramLong1, long paramLong2);
  
  public abstract void set(T paramT, long paramLong);
  
  public abstract void lazySet(T paramT, long paramLong);
  
  public abstract long get(T paramT);
  
  public long getAndSet(T paramT, long paramLong)
  {
    long l;
    do
    {
      l = get(paramT);
    } while (!compareAndSet(paramT, l, paramLong));
    return l;
  }
  
  public long getAndIncrement(T paramT)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = l1 + 1L;
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public long getAndDecrement(T paramT)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = l1 - 1L;
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public long getAndAdd(T paramT, long paramLong)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = l1 + paramLong;
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public long incrementAndGet(T paramT)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = l1 + 1L;
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  public long decrementAndGet(T paramT)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = l1 - 1L;
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  public long addAndGet(T paramT, long paramLong)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = l1 + paramLong;
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  public final long getAndUpdate(T paramT, LongUnaryOperator paramLongUnaryOperator)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = paramLongUnaryOperator.applyAsLong(l1);
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public final long updateAndGet(T paramT, LongUnaryOperator paramLongUnaryOperator)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = paramLongUnaryOperator.applyAsLong(l1);
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  public final long getAndAccumulate(T paramT, long paramLong, LongBinaryOperator paramLongBinaryOperator)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public final long accumulateAndGet(T paramT, long paramLong, LongBinaryOperator paramLongBinaryOperator)
  {
    long l1;
    long l2;
    do
    {
      l1 = get(paramT);
      l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  static boolean isAncestor(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    ClassLoader localClassLoader = paramClassLoader1;
    do
    {
      localClassLoader = localClassLoader.getParent();
      if (paramClassLoader2 == localClassLoader) {
        return true;
      }
    } while (localClassLoader != null);
    return false;
  }
  
  private static boolean isSamePackage(Class<?> paramClass1, Class<?> paramClass2)
  {
    return (paramClass1.getClassLoader() == paramClass2.getClassLoader()) && (Objects.equals(getPackageName(paramClass1), getPackageName(paramClass2)));
  }
  
  private static String getPackageName(Class<?> paramClass)
  {
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    return i != -1 ? str.substring(0, i) : "";
  }
  
  private static final class CASUpdater<T>
    extends AtomicLongFieldUpdater<T>
  {
    private static final Unsafe U = ;
    private final long offset;
    private final Class<?> cclass;
    private final Class<T> tclass;
    
    CASUpdater(final Class<T> paramClass, final String paramString, Class<?> paramClass1)
    {
      Field localField;
      int i;
      try
      {
        localField = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Field run()
            throws NoSuchFieldException
          {
            return paramClass.getDeclaredField(paramString);
          }
        });
        i = localField.getModifiers();
        ReflectUtil.ensureMemberAccess(paramClass1, paramClass, null, i);
        ClassLoader localClassLoader1 = paramClass.getClassLoader();
        ClassLoader localClassLoader2 = paramClass1.getClassLoader();
        if ((localClassLoader2 != null) && (localClassLoader2 != localClassLoader1) && ((localClassLoader1 == null) || (!isAncestor(localClassLoader1, localClassLoader2)))) {
          ReflectUtil.checkPackageAccess(paramClass);
        }
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw new RuntimeException(localPrivilegedActionException.getException());
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
      if (localField.getType() != Long.TYPE) {
        throw new IllegalArgumentException("Must be long type");
      }
      if (!Modifier.isVolatile(i)) {
        throw new IllegalArgumentException("Must be volatile type");
      }
      cclass = ((Modifier.isProtected(i)) && (paramClass.isAssignableFrom(paramClass1)) && (!AtomicLongFieldUpdater.isSamePackage(paramClass, paramClass1)) ? paramClass1 : paramClass);
      tclass = paramClass;
      offset = U.objectFieldOffset(localField);
    }
    
    private final void accessCheck(T paramT)
    {
      if (!cclass.isInstance(paramT)) {
        throwAccessCheckException(paramT);
      }
    }
    
    private final void throwAccessCheckException(T paramT)
    {
      if (cclass == tclass) {
        throw new ClassCastException();
      }
      throw new RuntimeException(new IllegalAccessException("Class " + cclass.getName() + " can not access a protected member of class " + tclass.getName() + " using an instance of " + paramT.getClass().getName()));
    }
    
    public final boolean compareAndSet(T paramT, long paramLong1, long paramLong2)
    {
      accessCheck(paramT);
      return U.compareAndSwapLong(paramT, offset, paramLong1, paramLong2);
    }
    
    public final boolean weakCompareAndSet(T paramT, long paramLong1, long paramLong2)
    {
      accessCheck(paramT);
      return U.compareAndSwapLong(paramT, offset, paramLong1, paramLong2);
    }
    
    public final void set(T paramT, long paramLong)
    {
      accessCheck(paramT);
      U.putLongVolatile(paramT, offset, paramLong);
    }
    
    public final void lazySet(T paramT, long paramLong)
    {
      accessCheck(paramT);
      U.putOrderedLong(paramT, offset, paramLong);
    }
    
    public final long get(T paramT)
    {
      accessCheck(paramT);
      return U.getLongVolatile(paramT, offset);
    }
    
    public final long getAndSet(T paramT, long paramLong)
    {
      accessCheck(paramT);
      return U.getAndSetLong(paramT, offset, paramLong);
    }
    
    public final long getAndAdd(T paramT, long paramLong)
    {
      accessCheck(paramT);
      return U.getAndAddLong(paramT, offset, paramLong);
    }
    
    public final long getAndIncrement(T paramT)
    {
      return getAndAdd(paramT, 1L);
    }
    
    public final long getAndDecrement(T paramT)
    {
      return getAndAdd(paramT, -1L);
    }
    
    public final long incrementAndGet(T paramT)
    {
      return getAndAdd(paramT, 1L) + 1L;
    }
    
    public final long decrementAndGet(T paramT)
    {
      return getAndAdd(paramT, -1L) - 1L;
    }
    
    public final long addAndGet(T paramT, long paramLong)
    {
      return getAndAdd(paramT, paramLong) + paramLong;
    }
  }
  
  private static final class LockedUpdater<T>
    extends AtomicLongFieldUpdater<T>
  {
    private static final Unsafe U = ;
    private final long offset;
    private final Class<?> cclass;
    private final Class<T> tclass;
    
    LockedUpdater(final Class<T> paramClass, final String paramString, Class<?> paramClass1)
    {
      Field localField = null;
      int i = 0;
      try
      {
        localField = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Field run()
            throws NoSuchFieldException
          {
            return paramClass.getDeclaredField(paramString);
          }
        });
        i = localField.getModifiers();
        ReflectUtil.ensureMemberAccess(paramClass1, paramClass, null, i);
        ClassLoader localClassLoader1 = paramClass.getClassLoader();
        ClassLoader localClassLoader2 = paramClass1.getClassLoader();
        if ((localClassLoader2 != null) && (localClassLoader2 != localClassLoader1) && ((localClassLoader1 == null) || (!isAncestor(localClassLoader1, localClassLoader2)))) {
          ReflectUtil.checkPackageAccess(paramClass);
        }
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw new RuntimeException(localPrivilegedActionException.getException());
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
      if (localField.getType() != Long.TYPE) {
        throw new IllegalArgumentException("Must be long type");
      }
      if (!Modifier.isVolatile(i)) {
        throw new IllegalArgumentException("Must be volatile type");
      }
      cclass = ((Modifier.isProtected(i)) && (paramClass.isAssignableFrom(paramClass1)) && (!AtomicLongFieldUpdater.isSamePackage(paramClass, paramClass1)) ? paramClass1 : paramClass);
      tclass = paramClass;
      offset = U.objectFieldOffset(localField);
    }
    
    private final void accessCheck(T paramT)
    {
      if (!cclass.isInstance(paramT)) {
        throw accessCheckException(paramT);
      }
    }
    
    private final RuntimeException accessCheckException(T paramT)
    {
      if (cclass == tclass) {
        return new ClassCastException();
      }
      return new RuntimeException(new IllegalAccessException("Class " + cclass.getName() + " can not access a protected member of class " + tclass.getName() + " using an instance of " + paramT.getClass().getName()));
    }
    
    public final boolean compareAndSet(T paramT, long paramLong1, long paramLong2)
    {
      accessCheck(paramT);
      synchronized (this)
      {
        long l = U.getLong(paramT, offset);
        if (l != paramLong1) {
          return false;
        }
        U.putLong(paramT, offset, paramLong2);
        return true;
      }
    }
    
    public final boolean weakCompareAndSet(T paramT, long paramLong1, long paramLong2)
    {
      return compareAndSet(paramT, paramLong1, paramLong2);
    }
    
    public final void set(T paramT, long paramLong)
    {
      accessCheck(paramT);
      synchronized (this)
      {
        U.putLong(paramT, offset, paramLong);
      }
    }
    
    public final void lazySet(T paramT, long paramLong)
    {
      set(paramT, paramLong);
    }
    
    /* Error */
    public final long get(T paramT)
    {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: invokespecial 191	java/util/concurrent/atomic/AtomicLongFieldUpdater$LockedUpdater:accessCheck	(Ljava/lang/Object;)V
      //   5: aload_0
      //   6: dup
      //   7: astore_2
      //   8: monitorenter
      //   9: getstatic 170	java/util/concurrent/atomic/AtomicLongFieldUpdater$LockedUpdater:U	Lsun/misc/Unsafe;
      //   12: aload_1
      //   13: aload_0
      //   14: getfield 167	java/util/concurrent/atomic/AtomicLongFieldUpdater$LockedUpdater:offset	J
      //   17: invokevirtual 197	sun/misc/Unsafe:getLong	(Ljava/lang/Object;J)J
      //   20: aload_2
      //   21: monitorexit
      //   22: lreturn
      //   23: astore_3
      //   24: aload_2
      //   25: monitorexit
      //   26: aload_3
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	LockedUpdater
      //   0	28	1	paramT	T
      //   7	18	2	Ljava/lang/Object;	Object
      //   23	4	3	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   9	22	23	finally
      //   23	26	23	finally
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\AtomicLongFieldUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */