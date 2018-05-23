package javax.xml.stream;

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
  private static final String DEFAULT_PACKAGE = "com.sun.xml.internal.";
  private static boolean debug;
  private static final Properties cacheProps;
  private static volatile boolean firstTime;
  private static final SecuritySupport ss;
  
  FactoryFinder() {}
  
  private static void dPrint(String paramString)
  {
    if (debug) {
      System.err.println("JAXP: " + paramString);
    }
  }
  
  private static Class getProviderClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2)
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
    if ((System.getSecurityManager() != null) && (paramString != null) && (paramString.startsWith("com.sun.xml.internal.")))
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
      throw new FactoryConfigurationError("Provider " + paramString + " not found", localClassNotFoundException);
    }
    catch (Exception localException)
    {
      throw new FactoryConfigurationError("Provider " + paramString + " could not be instantiated: " + localException, localException);
    }
  }
  
  static <T> T find(Class<T> paramClass, String paramString)
    throws FactoryConfigurationError
  {
    return (T)find(paramClass, paramClass.getName(), null, paramString);
  }
  
  static <T> T find(Class<T> paramClass, String paramString1, ClassLoader paramClassLoader, String paramString2)
    throws FactoryConfigurationError
  {
    dPrint("find factoryId =" + paramString1);
    try
    {
      String str1;
      if (paramClass.getName().equals(paramString1)) {
        str1 = ss.getSystemProperty(paramString1);
      } else {
        str1 = System.getProperty(paramString1);
      }
      if (str1 != null)
      {
        dPrint("found system property, value=" + str1);
        return (T)newInstance(paramClass, str1, paramClassLoader, true);
      }
    }
    catch (SecurityException localSecurityException)
    {
      throw new FactoryConfigurationError("Failed to read factoryId '" + paramString1 + "'", localSecurityException);
    }
    String str2 = null;
    try
    {
      if (firstTime) {
        synchronized (cacheProps)
        {
          if (firstTime)
          {
            str2 = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "stax.properties";
            File localFile = new File(str2);
            firstTime = false;
            if (ss.doesFileExist(localFile))
            {
              dPrint("Read properties file " + localFile);
              cacheProps.load(ss.getFileInputStream(localFile));
            }
            else
            {
              str2 = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
              localFile = new File(str2);
              if (ss.doesFileExist(localFile))
              {
                dPrint("Read properties file " + localFile);
                cacheProps.load(ss.getFileInputStream(localFile));
              }
            }
          }
        }
      }
      ??? = cacheProps.getProperty(paramString1);
      if (??? != null)
      {
        dPrint("found in " + str2 + " value=" + (String)???);
        return (T)newInstance(paramClass, (String)???, paramClassLoader, true);
      }
    }
    catch (Exception localException)
    {
      if (debug) {
        localException.printStackTrace();
      }
    }
    if (paramClass.getName().equals(paramString1))
    {
      Object localObject1 = findServiceProvider(paramClass, paramClassLoader);
      if (localObject1 != null) {
        return (T)localObject1;
      }
    }
    else
    {
      assert (paramString2 == null);
    }
    if (paramString2 == null) {
      throw new FactoryConfigurationError("Provider for " + paramString1 + " cannot be found", null);
    }
    dPrint("loaded from fallback value: " + paramString2);
    return (T)newInstance(paramClass, paramString2, paramClassLoader, true);
  }
  
  private static <T> T findServiceProvider(final Class<T> paramClass, ClassLoader paramClassLoader)
  {
    try
    {
      (T)AccessController.doPrivileged(new PrivilegedAction()
      {
        public T run()
        {
          ServiceLoader localServiceLoader;
          if (val$cl == null) {
            localServiceLoader = ServiceLoader.load(paramClass);
          } else {
            localServiceLoader = ServiceLoader.load(paramClass, val$cl);
          }
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */