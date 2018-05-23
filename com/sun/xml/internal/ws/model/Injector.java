package com.sun.xml.internal.ws.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

final class Injector
{
  private static final Logger LOGGER = Logger.getLogger(Injector.class.getName());
  private static final Method defineClass;
  private static final Method resolveClass;
  private static final Method getPackage;
  private static final Method definePackage;
  
  Injector() {}
  
  static synchronized Class inject(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte)
  {
    try
    {
      return paramClassLoader.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      try
      {
        int i = paramString.lastIndexOf('.');
        if (i != -1)
        {
          localObject = paramString.substring(0, i);
          Package localPackage = (Package)getPackage.invoke(paramClassLoader, new Object[] { localObject });
          if (localPackage == null) {
            definePackage.invoke(paramClassLoader, new Object[] { localObject, null, null, null, null, null, null, null });
          }
        }
        Object localObject = (Class)defineClass.invoke(paramClassLoader, new Object[] { paramString.replace('/', '.'), paramArrayOfByte, Integer.valueOf(0), Integer.valueOf(paramArrayOfByte.length) });
        resolveClass.invoke(paramClassLoader, new Object[] { localObject });
        return (Class)localObject;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        LOGGER.log(Level.FINE, "Unable to inject " + paramString, localIllegalAccessException);
        throw new WebServiceException(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        LOGGER.log(Level.FINE, "Unable to inject " + paramString, localInvocationTargetException);
        throw new WebServiceException(localInvocationTargetException);
      }
    }
  }
  
  static
  {
    try
    {
      defineClass = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, Integer.TYPE, Integer.TYPE });
      resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", new Class[] { Class.class });
      getPackage = ClassLoader.class.getDeclaredMethod("getPackage", new Class[] { String.class });
      definePackage = ClassLoader.class.getDeclaredMethod("definePackage", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        Injector.defineClass.setAccessible(true);
        Injector.resolveClass.setAccessible(true);
        Injector.getPackage.setAccessible(true);
        Injector.definePackage.setAccessible(true);
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\Injector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */