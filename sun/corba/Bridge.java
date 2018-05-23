package sun.corba;

import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public final class Bridge
{
  private static final Class[] NO_ARGS = new Class[0];
  private static final Permission getBridgePermission = new BridgePermission("getBridge");
  private static Bridge bridge = null;
  private final Method latestUserDefinedLoaderMethod = getLatestUserDefinedLoaderMethod();
  private final Unsafe unsafe = getUnsafe();
  private final ReflectionFactory reflectionFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
  public static final long INVALID_FIELD_OFFSET = -1L;
  
  private Method getLatestUserDefinedLoaderMethod()
  {
    (Method)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        Method localMethod = null;
        try
        {
          Class localClass = ObjectInputStream.class;
          localMethod = localClass.getDeclaredMethod("latestUserDefinedLoader", Bridge.NO_ARGS);
          localMethod.setAccessible(true);
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          Error localError = new Error("java.io.ObjectInputStream latestUserDefinedLoader " + localNoSuchMethodException);
          localError.initCause(localNoSuchMethodException);
          throw localError;
        }
        return localMethod;
      }
    });
  }
  
  private Unsafe getUnsafe()
  {
    Field localField = (Field)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        Field localField = null;
        try
        {
          Class localClass = Unsafe.class;
          localField = localClass.getDeclaredField("theUnsafe");
          localField.setAccessible(true);
          return localField;
        }
        catch (NoSuchFieldException localNoSuchFieldException)
        {
          Error localError = new Error("Could not access Unsafe");
          localError.initCause(localNoSuchFieldException);
          throw localError;
        }
      }
    });
    Unsafe localUnsafe = null;
    try
    {
      localUnsafe = (Unsafe)localField.get(null);
    }
    catch (Throwable localThrowable)
    {
      Error localError = new Error("Could not access Unsafe");
      localError.initCause(localThrowable);
      throw localError;
    }
    return localUnsafe;
  }
  
  private Bridge() {}
  
  public static final synchronized Bridge get()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(getBridgePermission);
    }
    if (bridge == null) {
      bridge = new Bridge();
    }
    return bridge;
  }
  
  public final ClassLoader getLatestUserDefinedLoader()
  {
    try
    {
      return (ClassLoader)latestUserDefinedLoaderMethod.invoke(null, (Object[])NO_ARGS);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      localError = new Error("sun.corba.Bridge.latestUserDefinedLoader: " + localInvocationTargetException);
      localError.initCause(localInvocationTargetException);
      throw localError;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Error localError = new Error("sun.corba.Bridge.latestUserDefinedLoader: " + localIllegalAccessException);
      localError.initCause(localIllegalAccessException);
      throw localError;
    }
  }
  
  public final int getInt(Object paramObject, long paramLong)
  {
    return unsafe.getInt(paramObject, paramLong);
  }
  
  public final void putInt(Object paramObject, long paramLong, int paramInt)
  {
    unsafe.putInt(paramObject, paramLong, paramInt);
  }
  
  public final Object getObject(Object paramObject, long paramLong)
  {
    return unsafe.getObject(paramObject, paramLong);
  }
  
  public final void putObject(Object paramObject1, long paramLong, Object paramObject2)
  {
    unsafe.putObject(paramObject1, paramLong, paramObject2);
  }
  
  public final boolean getBoolean(Object paramObject, long paramLong)
  {
    return unsafe.getBoolean(paramObject, paramLong);
  }
  
  public final void putBoolean(Object paramObject, long paramLong, boolean paramBoolean)
  {
    unsafe.putBoolean(paramObject, paramLong, paramBoolean);
  }
  
  public final byte getByte(Object paramObject, long paramLong)
  {
    return unsafe.getByte(paramObject, paramLong);
  }
  
  public final void putByte(Object paramObject, long paramLong, byte paramByte)
  {
    unsafe.putByte(paramObject, paramLong, paramByte);
  }
  
  public final short getShort(Object paramObject, long paramLong)
  {
    return unsafe.getShort(paramObject, paramLong);
  }
  
  public final void putShort(Object paramObject, long paramLong, short paramShort)
  {
    unsafe.putShort(paramObject, paramLong, paramShort);
  }
  
  public final char getChar(Object paramObject, long paramLong)
  {
    return unsafe.getChar(paramObject, paramLong);
  }
  
  public final void putChar(Object paramObject, long paramLong, char paramChar)
  {
    unsafe.putChar(paramObject, paramLong, paramChar);
  }
  
  public final long getLong(Object paramObject, long paramLong)
  {
    return unsafe.getLong(paramObject, paramLong);
  }
  
  public final void putLong(Object paramObject, long paramLong1, long paramLong2)
  {
    unsafe.putLong(paramObject, paramLong1, paramLong2);
  }
  
  public final float getFloat(Object paramObject, long paramLong)
  {
    return unsafe.getFloat(paramObject, paramLong);
  }
  
  public final void putFloat(Object paramObject, long paramLong, float paramFloat)
  {
    unsafe.putFloat(paramObject, paramLong, paramFloat);
  }
  
  public final double getDouble(Object paramObject, long paramLong)
  {
    return unsafe.getDouble(paramObject, paramLong);
  }
  
  public final void putDouble(Object paramObject, long paramLong, double paramDouble)
  {
    unsafe.putDouble(paramObject, paramLong, paramDouble);
  }
  
  public final long objectFieldOffset(Field paramField)
  {
    return unsafe.objectFieldOffset(paramField);
  }
  
  public final void throwException(Throwable paramThrowable)
  {
    unsafe.throwException(paramThrowable);
  }
  
  public final Constructor newConstructorForSerialization(Class paramClass, Constructor paramConstructor)
  {
    return reflectionFactory.newConstructorForSerialization(paramClass, paramConstructor);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\corba\Bridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */