package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicIntegerFieldUpdater<T>
{
  @CallerSensitive
  public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> paramClass, String paramString)
  {
    return new AtomicIntegerFieldUpdaterImpl(paramClass, paramString, Reflection.getCallerClass());
  }
  
  protected AtomicIntegerFieldUpdater() {}
  
  public abstract boolean compareAndSet(T paramT, int paramInt1, int paramInt2);
  
  public abstract boolean weakCompareAndSet(T paramT, int paramInt1, int paramInt2);
  
  public abstract void set(T paramT, int paramInt);
  
  public abstract void lazySet(T paramT, int paramInt);
  
  public abstract int get(T paramT);
  
  public int getAndSet(T paramT, int paramInt)
  {
    int i;
    do
    {
      i = get(paramT);
    } while (!compareAndSet(paramT, i, paramInt));
    return i;
  }
  
  public int getAndIncrement(T paramT)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = i + 1;
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public int getAndDecrement(T paramT)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = i - 1;
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public int getAndAdd(T paramT, int paramInt)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = i + paramInt;
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public int incrementAndGet(T paramT)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = i + 1;
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  public int decrementAndGet(T paramT)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = i - 1;
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  public int addAndGet(T paramT, int paramInt)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = i + paramInt;
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  public final int getAndUpdate(T paramT, IntUnaryOperator paramIntUnaryOperator)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = paramIntUnaryOperator.applyAsInt(i);
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public final int updateAndGet(T paramT, IntUnaryOperator paramIntUnaryOperator)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = paramIntUnaryOperator.applyAsInt(i);
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  public final int getAndAccumulate(T paramT, int paramInt, IntBinaryOperator paramIntBinaryOperator)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = paramIntBinaryOperator.applyAsInt(i, paramInt);
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public final int accumulateAndGet(T paramT, int paramInt, IntBinaryOperator paramIntBinaryOperator)
  {
    int i;
    int j;
    do
    {
      i = get(paramT);
      j = paramIntBinaryOperator.applyAsInt(i, paramInt);
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  private static final class AtomicIntegerFieldUpdaterImpl<T>
    extends AtomicIntegerFieldUpdater<T>
  {
    private static final Unsafe U = ;
    private final long offset;
    private final Class<?> cclass;
    private final Class<T> tclass;
    
    AtomicIntegerFieldUpdaterImpl(final Class<T> paramClass, final String paramString, Class<?> paramClass1)
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
      if (localField.getType() != Integer.TYPE) {
        throw new IllegalArgumentException("Must be integer type");
      }
      if (!Modifier.isVolatile(i)) {
        throw new IllegalArgumentException("Must be volatile type");
      }
      cclass = ((Modifier.isProtected(i)) && (paramClass.isAssignableFrom(paramClass1)) && (!isSamePackage(paramClass, paramClass1)) ? paramClass1 : paramClass);
      tclass = paramClass;
      offset = U.objectFieldOffset(localField);
    }
    
    private static boolean isAncestor(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
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
    
    public final boolean compareAndSet(T paramT, int paramInt1, int paramInt2)
    {
      accessCheck(paramT);
      return U.compareAndSwapInt(paramT, offset, paramInt1, paramInt2);
    }
    
    public final boolean weakCompareAndSet(T paramT, int paramInt1, int paramInt2)
    {
      accessCheck(paramT);
      return U.compareAndSwapInt(paramT, offset, paramInt1, paramInt2);
    }
    
    public final void set(T paramT, int paramInt)
    {
      accessCheck(paramT);
      U.putIntVolatile(paramT, offset, paramInt);
    }
    
    public final void lazySet(T paramT, int paramInt)
    {
      accessCheck(paramT);
      U.putOrderedInt(paramT, offset, paramInt);
    }
    
    public final int get(T paramT)
    {
      accessCheck(paramT);
      return U.getIntVolatile(paramT, offset);
    }
    
    public final int getAndSet(T paramT, int paramInt)
    {
      accessCheck(paramT);
      return U.getAndSetInt(paramT, offset, paramInt);
    }
    
    public final int getAndAdd(T paramT, int paramInt)
    {
      accessCheck(paramT);
      return U.getAndAddInt(paramT, offset, paramInt);
    }
    
    public final int getAndIncrement(T paramT)
    {
      return getAndAdd(paramT, 1);
    }
    
    public final int getAndDecrement(T paramT)
    {
      return getAndAdd(paramT, -1);
    }
    
    public final int incrementAndGet(T paramT)
    {
      return getAndAdd(paramT, 1) + 1;
    }
    
    public final int decrementAndGet(T paramT)
    {
      return getAndAdd(paramT, -1) - 1;
    }
    
    public final int addAndGet(T paramT, int paramInt)
    {
      return getAndAdd(paramT, paramInt) + paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\AtomicIntegerFieldUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */