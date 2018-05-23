package javax.xml.parsers;

import java.io.File;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class FactoryFinder
{
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";
  private static boolean debug;
  private static final Properties cacheProps;
  static volatile boolean firstTime;
  private static final SecuritySupport ss;
  
  FactoryFinder() {}
  
  private static void dPrint(String paramString)
  {
    if (debug) {
      System.err.println("JAXP: " + paramString);
    }
  }
  
  private static Class<?> getProviderClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2)
    throws ClassNotFoundException
  {
    try
    {
      if (paramClassLoader == null)
      {
        if (paramBoolean2) {
          return Class.forName(paramString, false, FactoryFinder.class.getClassLoader());
        }
        paramClassLoader = ss.getContextClassLoader();
        if (paramClassLoader == null) {
          throw new ClassNotFoundException();
        }
        return Class.forName(paramString, false, paramClassLoader);
      }
      return Class.forName(paramString, false, paramClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (paramBoolean1) {
        return Class.forName(paramString, false, FactoryFinder.class.getClassLoader());
      }
      throw localClassNotFoundException;
    }
  }
  
  static <T> T newInstance(Class<T> paramClass, String paramString, ClassLoader paramClassLoader, boolean paramBoolean)
    throws FactoryConfigurationError
  {
    return (T)newInstance(paramClass, paramString, paramClassLoader, paramBoolean, false);
  }
  
  static <T> T newInstance(Class<T> paramClass, String paramString, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2)
    throws FactoryConfigurationError
  {
    assert (paramClass != null);
    if ((System.getSecurityManager() != null) && (paramString != null) && (paramString.startsWith("com.sun.org.apache.xerces.internal")))
    {
      paramClassLoader = null;
      paramBoolean2 = true;
    }
    try
    {
      Class localClass = getProviderClass(paramString, paramClassLoader, paramBoolean1, paramBoolean2);
      if (!paramClass.isAssignableFrom(localClass)) {
        throw new ClassCastException(paramString + " cannot be cast to " + paramClass.getName());
      }
      Object localObject = localClass.newInstance();
      if (debug) {
        dPrint("created new instance of " + localClass + " using ClassLoader: " + paramClassLoader);
      }
      return (T)paramClass.cast(localObject);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new FactoryConfigurationError(localClassNotFoundException, "Provider " + paramString + " not found");
    }
    catch (Exception localException)
    {
      throw new FactoryConfigurationError(localException, "Provider " + paramString + " could not be instantiated: " + localException);
    }
  }
  
  static <T> T find(Class<T> paramClass, String paramString)
    throws FactoryConfigurationError
  {
    String str1 = paramClass.getName();
    dPrint("find factoryId =" + str1);
    try
    {
      String str2 = ss.getSystemProperty(str1);
      if (str2 != null)
      {
        dPrint("found system property, value=" + str2);
        return (T)newInstance(paramClass, str2, null, true);
      }
    }
    catch (SecurityException localSecurityException)
    {
      if (debug) {
        localSecurityException.printStackTrace();
      }
    }
    try
    {
      if (firstTime) {
        synchronized (cacheProps)
        {
          if (firstTime)
          {
            String str3 = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            File localFile = new File(str3);
            firstTime = false;
            if (ss.doesFileExist(localFile))
            {
              dPrint("Read properties file " + localFile);
              cacheProps.load(ss.getFileInputStream(localFile));
            }
          }
        }
      }
      ??? = cacheProps.getProperty(str1);
      if (??? != null)
      {
        dPrint("found in $java.home/jaxp.properties, value=" + (String)???);
        return (T)newInstance(paramClass, (String)???, null, true);
      }
    }
    catch (Exception localException)
    {
      if (debug) {
        localException.printStackTrace();
      }
    }
    Object localObject1 = findServiceProvider(paramClass);
    if (localObject1 != null) {
      return (T)localObject1;
    }
    if (paramString == null) {
      throw new FactoryConfigurationError("Provider for " + str1 + " cannot be found");
    }
    dPrint("loaded from fallback value: " + paramString);
    return (T)newInstance(paramClass, paramString, null, true);
  }
  
  private static <T> T findServiceProvider(Class<T> paramClass)
  {
    try
    {
      (T)AccessController.doPrivileged(new PrivilegedAction()
      {
        public T run()
        {
          ServiceLoader localServiceLoader = ServiceLoader.load(val$type);
          Iterator localIterator = localServiceLoader.iterator();
          if (localIterator.hasNext()) {
            return (T)localIterator.next();
          }
          return null;
        }
      });
    }
    catch (ServiceConfigurationError localServiceConfigurationError)
    {
      RuntimeException localRuntimeException = new RuntimeException("Provider for " + paramClass + " cannot be created", localServiceConfigurationError);
      FactoryConfigurationError localFactoryConfigurationError = new FactoryConfigurationError(localRuntimeException, localRuntimeException.getMessage());
      throw localFactoryConfigurationError;
    }
  }
  
  static
  {
    debug = false;
    cacheProps = new Properties();
    firstTime = true;
    ss = new SecuritySupport();
    try
    {
      String str = ss.getSystemProperty("jaxp.debug");
      debug = (str != null) && (!"false".equals(str));
    }
    catch (SecurityException localSecurityException)
    {
      debug = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\parsers\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */