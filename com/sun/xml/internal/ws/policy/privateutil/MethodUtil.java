package com.sun.xml.internal.ws.policy.privateutil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

class MethodUtil
{
  private static final Logger LOGGER = Logger.getLogger(MethodUtil.class.getName());
  private static final Method INVOKE_METHOD;
  
  MethodUtil() {}
  
  static Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws IllegalAccessException, InvocationTargetException
  {
    if (INVOKE_METHOD != null)
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Invoking method using sun.reflect.misc.MethodUtil");
      }
      try
      {
        return INVOKE_METHOD.invoke(null, new Object[] { paramMethod, paramObject, paramArrayOfObject });
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw unwrapException(localInvocationTargetException);
      }
    }
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "Invoking method directly, probably non-Oracle JVM");
    }
    return paramMethod.invoke(paramObject, paramArrayOfObject);
  }
  
  private static InvocationTargetException unwrapException(InvocationTargetException paramInvocationTargetException)
  {
    Throwable localThrowable = paramInvocationTargetException.getTargetException();
    if ((localThrowable != null) && ((localThrowable instanceof InvocationTargetException)))
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Unwrapping invocation target exception");
      }
      return (InvocationTargetException)localThrowable;
    }
    return paramInvocationTargetException;
  }
  
  static
  {
    Method localMethod;
    try
    {
      Class localClass = Class.forName("sun.reflect.misc.MethodUtil");
      localMethod = localClass.getMethod("invoke", new Class[] { Method.class, Object.class, Object[].class });
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Class sun.reflect.misc.MethodUtil found; it will be used to invoke methods.");
      }
    }
    catch (Throwable localThrowable)
    {
      localMethod = null;
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "Class sun.reflect.misc.MethodUtil not found, probably non-Oracle JVM");
      }
    }
    INVOKE_METHOD = localMethod;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\privateutil\MethodUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */