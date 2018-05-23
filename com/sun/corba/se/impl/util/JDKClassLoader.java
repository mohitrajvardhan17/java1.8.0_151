package com.sun.corba.se.impl.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import sun.corba.Bridge;

class JDKClassLoader
{
  private static final JDKClassLoaderCache classCache = new JDKClassLoaderCache(null);
  private static final Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Object run()
    {
      return Bridge.get();
    }
  });
  
  JDKClassLoader() {}
  
  static Class loadClass(Class paramClass, String paramString)
    throws ClassNotFoundException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    if (paramString.length() == 0) {
      throw new ClassNotFoundException();
    }
    ClassLoader localClassLoader;
    if (paramClass != null) {
      localClassLoader = paramClass.getClassLoader();
    } else {
      localClassLoader = bridge.getLatestUserDefinedLoader();
    }
    Object localObject = classCache.createKey(paramString, localClassLoader);
    if (classCache.knownToFail(localObject)) {
      throw new ClassNotFoundException(paramString);
    }
    try
    {
      return Class.forName(paramString, false, localClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      classCache.recordFailure(localObject);
      throw localClassNotFoundException;
    }
  }
  
  private static class JDKClassLoaderCache
  {
    private final Map cache = Collections.synchronizedMap(new WeakHashMap());
    private static final Object KNOWN_TO_FAIL = new Object();
    
    private JDKClassLoaderCache() {}
    
    public final void recordFailure(Object paramObject)
    {
      cache.put(paramObject, KNOWN_TO_FAIL);
    }
    
    public final Object createKey(String paramString, ClassLoader paramClassLoader)
    {
      return new CacheKey(paramString, paramClassLoader);
    }
    
    public final boolean knownToFail(Object paramObject)
    {
      return cache.get(paramObject) == KNOWN_TO_FAIL;
    }
    
    private static class CacheKey
    {
      String className;
      ClassLoader loader;
      
      public CacheKey(String paramString, ClassLoader paramClassLoader)
      {
        className = paramString;
        loader = paramClassLoader;
      }
      
      public int hashCode()
      {
        if (loader == null) {
          return className.hashCode();
        }
        return className.hashCode() ^ loader.hashCode();
      }
      
      public boolean equals(Object paramObject)
      {
        try
        {
          if (paramObject == null) {
            return false;
          }
          CacheKey localCacheKey = (CacheKey)paramObject;
          return (className.equals(className)) && (loader == loader);
        }
        catch (ClassCastException localClassCastException) {}
        return false;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\JDKClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */