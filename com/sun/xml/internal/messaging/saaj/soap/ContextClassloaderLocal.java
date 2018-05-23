package com.sun.xml.internal.messaging.saaj.soap;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

abstract class ContextClassloaderLocal<V>
{
  private static final String FAILED_TO_CREATE_NEW_INSTANCE = "FAILED_TO_CREATE_NEW_INSTANCE";
  private WeakHashMap<ClassLoader, V> CACHE = new WeakHashMap();
  
  ContextClassloaderLocal() {}
  
  public V get()
    throws Error
  {
    ClassLoader localClassLoader = getContextClassLoader();
    Object localObject = CACHE.get(localClassLoader);
    if (localObject == null)
    {
      localObject = createNewInstance();
      CACHE.put(localClassLoader, localObject);
    }
    return (V)localObject;
  }
  
  public void set(V paramV)
  {
    CACHE.put(getContextClassLoader(), paramV);
  }
  
  protected abstract V initialValue()
    throws Exception;
  
  private V createNewInstance()
  {
    try
    {
      return (V)initialValue();
    }
    catch (Exception localException)
    {
      throw new Error(format("FAILED_TO_CREATE_NEW_INSTANCE", new Object[] { getClass().getName() }), localException);
    }
  }
  
  private static String format(String paramString, Object... paramVarArgs)
  {
    String str = ResourceBundle.getBundle(ContextClassloaderLocal.class.getName()).getString(paramString);
    return MessageFormat.format(str, paramVarArgs);
  }
  
  private static ClassLoader getContextClassLoader()
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        try
        {
          localClassLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (SecurityException localSecurityException) {}
        return localClassLoader;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ContextClassloaderLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */