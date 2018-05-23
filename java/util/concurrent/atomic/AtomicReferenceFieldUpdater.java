package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicReferenceFieldUpdater<T, V>
{
  @CallerSensitive
  public static <U, W> AtomicReferenceFieldUpdater<U, W> newUpdater(Class<U> paramClass, Class<W> paramClass1, String paramString)
  {
    return new AtomicReferenceFieldUpdaterImpl(paramClass, paramClass1, paramString, Reflection.getCallerClass());
  }
  
  protected AtomicReferenceFieldUpdater() {}
  
  public abstract boolean compareAndSet(T paramT, V paramV1, V paramV2);
  
  public abstract boolean weakCompareAndSet(T paramT, V paramV1, V paramV2);
  
  public abstract void set(T paramT, V paramV);
  
  public abstract void lazySet(T paramT, V paramV);
  
  public abstract V get(T paramT);
  
  public V getAndSet(T paramT, V paramV)
  {
    Object localObject;
    do
    {
      localObject = get(paramT);
    } while (!compareAndSet(paramT, localObject, paramV));
    return (V)localObject;
  }
  
  public final V getAndUpdate(T paramT, UnaryOperator<V> paramUnaryOperator)
  {
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = get(paramT);
      localObject2 = paramUnaryOperator.apply(localObject1);
    } while (!compareAndSet(paramT, localObject1, localObject2));
    return (V)localObject1;
  }
  
  public final V updateAndGet(T paramT, UnaryOperator<V> paramUnaryOperator)
  {
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = get(paramT);
      localObject2 = paramUnaryOperator.apply(localObject1);
    } while (!compareAndSet(paramT, localObject1, localObject2));
    return (V)localObject2;
  }
  
  public final V getAndAccumulate(T paramT, V paramV, BinaryOperator<V> paramBinaryOperator)
  {
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = get(paramT);
      localObject2 = paramBinaryOperator.apply(localObject1, paramV);
    } while (!compareAndSet(paramT, localObject1, localObject2));
    return (V)localObject1;
  }
  
  public final V accumulateAndGet(T paramT, V paramV, BinaryOperator<V> paramBinaryOperator)
  {
    Object localObject1;
    Object localObject2;
    do
    {
      localObject1 = get(paramT);
      localObject2 = paramBinaryOperator.apply(localObject1, paramV);
    } while (!compareAndSet(paramT, localObject1, localObject2));
    return (V)localObject2;
  }
  
  private static final class AtomicReferenceFieldUpdaterImpl<T, V>
    extends AtomicReferenceFieldUpdater<T, V>
  {
    private static final Unsafe U = ;
    private final long offset;
    private final Class<?> cclass;
    private final Class<T> tclass;
    private final Class<V> vclass;
    
    AtomicReferenceFieldUpdaterImpl(final Class<T> paramClass, Class<V> paramClass1, final String paramString, Class<?> paramClass2)
    {
      Field localField;
      int i;
      Class localClass;
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
        ReflectUtil.ensureMemberAccess(paramClass2, paramClass, null, i);
        ClassLoader localClassLoader1 = paramClass.getClassLoader();
        ClassLoader localClassLoader2 = paramClass2.getClassLoader();
        if ((localClassLoader2 != null) && (localClassLoader2 != localClassLoader1) && ((localClassLoader1 == null) || (!isAncestor(localClassLoader1, localClassLoader2)))) {
          ReflectUtil.checkPackageAccess(paramClass);
        }
        localClass = localField.getType();
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw new RuntimeException(localPrivilegedActionException.getException());
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
      if (paramClass1 != localClass) {
        throw new ClassCastException();
      }
      if (paramClass1.isPrimitive()) {
        throw new IllegalArgumentException("Must be reference type");
      }
      if (!Modifier.isVolatile(i)) {
        throw new IllegalArgumentException("Must be volatile type");
      }
      cclass = ((Modifier.isProtected(i)) && (paramClass.isAssignableFrom(paramClass2)) && (!isSamePackage(paramClass, paramClass2)) ? paramClass2 : paramClass);
      tclass = paramClass;
      vclass = paramClass1;
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
    
    private final void valueCheck(V paramV)
    {
      if ((paramV != null) && (!vclass.isInstance(paramV))) {
        throwCCE();
      }
    }
    
    static void throwCCE()
    {
      throw new ClassCastException();
    }
    
    public final boolean compareAndSet(T paramT, V paramV1, V paramV2)
    {
      accessCheck(paramT);
      valueCheck(paramV2);
      return U.compareAndSwapObject(paramT, offset, paramV1, paramV2);
    }
    
    public final boolean weakCompareAndSet(T paramT, V paramV1, V paramV2)
    {
      accessCheck(paramT);
      valueCheck(paramV2);
      return U.compareAndSwapObject(paramT, offset, paramV1, paramV2);
    }
    
    public final void set(T paramT, V paramV)
    {
      accessCheck(paramT);
      valueCheck(paramV);
      U.putObjectVolatile(paramT, offset, paramV);
    }
    
    public final void lazySet(T paramT, V paramV)
    {
      accessCheck(paramT);
      valueCheck(paramV);
      U.putOrderedObject(paramT, offset, paramV);
    }
    
    public final V get(T paramT)
    {
      accessCheck(paramT);
      return (V)U.getObjectVolatile(paramT, offset);
    }
    
    public final V getAndSet(T paramT, V paramV)
    {
      accessCheck(paramT);
      valueCheck(paramV);
      return (V)U.getAndSetObject(paramT, offset, paramV);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\atomic\AtomicReferenceFieldUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */