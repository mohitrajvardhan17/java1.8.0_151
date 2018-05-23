package com.sun.beans.finder;

import sun.reflect.misc.ReflectUtil;

public final class ClassFinder
{
  public static Class<?> findClass(String paramString)
    throws ClassNotFoundException
  {
    ReflectUtil.checkPackageAccess(paramString);
    try
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = ClassLoader.getSystemClassLoader();
      }
      if (localClassLoader != null) {
        return Class.forName(paramString, false, localClassLoader);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException) {}catch (SecurityException localSecurityException) {}
    return Class.forName(paramString);
  }
  
  public static Class<?> findClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    ReflectUtil.checkPackageAccess(paramString);
    if (paramClassLoader != null) {
      try
      {
        return Class.forName(paramString, false, paramClassLoader);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}catch (SecurityException localSecurityException) {}
    }
    return findClass(paramString);
  }
  
  public static Class<?> resolveClass(String paramString)
    throws ClassNotFoundException
  {
    return resolveClass(paramString, null);
  }
  
  public static Class<?> resolveClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    Class localClass = PrimitiveTypeMap.getType(paramString);
    return localClass == null ? findClass(paramString, paramClassLoader) : localClass;
  }
  
  private ClassFinder() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\ClassFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */