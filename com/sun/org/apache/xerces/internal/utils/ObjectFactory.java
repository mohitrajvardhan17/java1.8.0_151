package com.sun.org.apache.xerces.internal.utils;

import java.io.PrintStream;

public final class ObjectFactory
{
  private static final String JAXP_INTERNAL = "com.sun.org.apache";
  private static final String STAX_INTERNAL = "com.sun.xml.internal";
  private static final boolean DEBUG = ;
  
  public ObjectFactory() {}
  
  private static boolean isDebugEnabled()
  {
    try
    {
      String str = SecuritySupport.getSystemProperty("xerces.debug");
      return (str != null) && (!"false".equals(str));
    }
    catch (SecurityException localSecurityException) {}
    return false;
  }
  
  private static void debugPrintln(String paramString)
  {
    if (DEBUG) {
      System.err.println("XERCES: " + paramString);
    }
  }
  
  public static ClassLoader findClassLoader()
    throws ConfigurationError
  {
    if (System.getSecurityManager() != null) {
      return null;
    }
    ClassLoader localClassLoader1 = SecuritySupport.getContextClassLoader();
    ClassLoader localClassLoader2 = SecuritySupport.getSystemClassLoader();
    for (ClassLoader localClassLoader3 = localClassLoader2;; localClassLoader3 = SecuritySupport.getParentClassLoader(localClassLoader3))
    {
      if (localClassLoader1 == localClassLoader3)
      {
        ClassLoader localClassLoader4 = ObjectFactory.class.getClassLoader();
        for (localClassLoader3 = localClassLoader2;; localClassLoader3 = SecuritySupport.getParentClassLoader(localClassLoader3))
        {
          if (localClassLoader4 == localClassLoader3) {
            return localClassLoader2;
          }
          if (localClassLoader3 == null) {
            break;
          }
        }
        return localClassLoader4;
      }
      if (localClassLoader3 == null) {
        break;
      }
    }
    return localClassLoader1;
  }
  
  public static Object newInstance(String paramString, boolean paramBoolean)
    throws ConfigurationError
  {
    if (System.getSecurityManager() != null) {
      return newInstance(paramString, null, paramBoolean);
    }
    return newInstance(paramString, findClassLoader(), paramBoolean);
  }
  
  public static Object newInstance(String paramString, ClassLoader paramClassLoader, boolean paramBoolean)
    throws ConfigurationError
  {
    try
    {
      Class localClass = findProviderClass(paramString, paramClassLoader, paramBoolean);
      Object localObject = localClass.newInstance();
      if (DEBUG) {
        debugPrintln("created new instance of " + localClass + " using ClassLoader: " + paramClassLoader);
      }
      return localObject;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new ConfigurationError("Provider " + paramString + " not found", localClassNotFoundException);
    }
    catch (Exception localException)
    {
      throw new ConfigurationError("Provider " + paramString + " could not be instantiated: " + localException, localException);
    }
  }
  
  public static Class findProviderClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException, ConfigurationError
  {
    return findProviderClass(paramString, findClassLoader(), paramBoolean);
  }
  
  public static Class findProviderClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean)
    throws ClassNotFoundException, ConfigurationError
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      if ((paramString.startsWith("com.sun.org.apache")) || (paramString.startsWith("com.sun.xml.internal")))
      {
        paramClassLoader = null;
      }
      else
      {
        int i = paramString.lastIndexOf(".");
        String str = paramString;
        if (i != -1) {
          str = paramString.substring(0, i);
        }
        localSecurityManager.checkPackageAccess(str);
      }
    }
    Class localClass;
    if (paramClassLoader == null) {
      localClass = Class.forName(paramString, false, ObjectFactory.class.getClassLoader());
    } else {
      try
      {
        localClass = paramClassLoader.loadClass(paramString);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        if (paramBoolean)
        {
          ClassLoader localClassLoader = ObjectFactory.class.getClassLoader();
          if (localClassLoader == null)
          {
            localClass = Class.forName(paramString);
          }
          else if (paramClassLoader != localClassLoader)
          {
            paramClassLoader = localClassLoader;
            localClass = paramClassLoader.loadClass(paramString);
          }
          else
          {
            throw localClassNotFoundException;
          }
        }
        else
        {
          throw localClassNotFoundException;
        }
      }
    }
    return localClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\utils\ObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */