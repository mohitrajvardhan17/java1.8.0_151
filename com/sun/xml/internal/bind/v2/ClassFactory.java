package com.sun.xml.internal.bind.v2;

import com.sun.xml.internal.bind.Util;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClassFactory
{
  private static final Class[] emptyClass = new Class[0];
  private static final Object[] emptyObject = new Object[0];
  private static final Logger logger = Util.getClassLogger();
  private static final ThreadLocal<Map<Class, WeakReference<Constructor>>> tls = new ThreadLocal()
  {
    public Map<Class, WeakReference<Constructor>> initialValue()
    {
      return new WeakHashMap();
    }
  };
  
  public ClassFactory() {}
  
  public static void cleanCache()
  {
    if (tls != null) {
      try
      {
        tls.remove();
      }
      catch (Exception localException)
      {
        logger.log(Level.WARNING, "Unable to clean Thread Local cache of classes used in Unmarshaller: {0}", localException.getLocalizedMessage());
      }
    }
  }
  
  public static <T> T create0(Class<T> paramClass)
    throws IllegalAccessException, InvocationTargetException, InstantiationException
  {
    Map localMap = (Map)tls.get();
    Constructor localConstructor = null;
    WeakReference localWeakReference = (WeakReference)localMap.get(paramClass);
    if (localWeakReference != null) {
      localConstructor = (Constructor)localWeakReference.get();
    }
    if (localConstructor == null)
    {
      try
      {
        localConstructor = paramClass.getDeclaredConstructor(emptyClass);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        logger.log(Level.INFO, "No default constructor found on " + paramClass, localNoSuchMethodException);
        NoSuchMethodError localNoSuchMethodError;
        if ((paramClass.getDeclaringClass() != null) && (!Modifier.isStatic(paramClass.getModifiers()))) {
          localNoSuchMethodError = new NoSuchMethodError(Messages.NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS.format(new Object[] { paramClass.getName() }));
        } else {
          localNoSuchMethodError = new NoSuchMethodError(localNoSuchMethodException.getMessage());
        }
        localNoSuchMethodError.initCause(localNoSuchMethodException);
        throw localNoSuchMethodError;
      }
      int i = paramClass.getModifiers();
      if ((!Modifier.isPublic(i)) || (!Modifier.isPublic(localConstructor.getModifiers()))) {
        try
        {
          localConstructor.setAccessible(true);
        }
        catch (SecurityException localSecurityException)
        {
          logger.log(Level.FINE, "Unable to make the constructor of " + paramClass + " accessible", localSecurityException);
          throw localSecurityException;
        }
      }
      localMap.put(paramClass, new WeakReference(localConstructor));
    }
    return (T)localConstructor.newInstance(emptyObject);
  }
  
  public static <T> T create(Class<T> paramClass)
  {
    try
    {
      return (T)create0(paramClass);
    }
    catch (InstantiationException localInstantiationException)
    {
      logger.log(Level.INFO, "failed to create a new instance of " + paramClass, localInstantiationException);
      throw new InstantiationError(localInstantiationException.toString());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      logger.log(Level.INFO, "failed to create a new instance of " + paramClass, localIllegalAccessException);
      throw new IllegalAccessError(localIllegalAccessException.toString());
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getTargetException();
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      throw new IllegalStateException(localThrowable);
    }
  }
  
  public static Object create(Method paramMethod)
  {
    Object localObject;
    try
    {
      return paramMethod.invoke(null, emptyObject);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getTargetException();
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      throw new IllegalStateException(localThrowable);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      logger.log(Level.INFO, "failed to create a new instance of " + paramMethod.getReturnType().getName(), localIllegalAccessException);
      throw new IllegalAccessError(localIllegalAccessException.toString());
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      logger.log(Level.INFO, "failed to create a new instance of " + paramMethod.getReturnType().getName(), localIllegalArgumentException);
      localObject = localIllegalArgumentException;
    }
    catch (NullPointerException localNullPointerException)
    {
      logger.log(Level.INFO, "failed to create a new instance of " + paramMethod.getReturnType().getName(), localNullPointerException);
      localObject = localNullPointerException;
    }
    catch (ExceptionInInitializerError localExceptionInInitializerError)
    {
      logger.log(Level.INFO, "failed to create a new instance of " + paramMethod.getReturnType().getName(), localExceptionInInitializerError);
      localObject = localExceptionInInitializerError;
    }
    NoSuchMethodError localNoSuchMethodError = new NoSuchMethodError(((Throwable)localObject).getMessage());
    localNoSuchMethodError.initCause((Throwable)localObject);
    throw localNoSuchMethodError;
  }
  
  public static <T> Class<? extends T> inferImplClass(Class<T> paramClass, Class[] paramArrayOfClass)
  {
    if (!paramClass.isInterface()) {
      return paramClass;
    }
    for (Class localClass : paramArrayOfClass) {
      if (paramClass.isAssignableFrom(localClass)) {
        return localClass.asSubclass(paramClass);
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\ClassFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */