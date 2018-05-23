package javax.xml.ws.spi;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.ws.WebServiceException;

class FactoryFinder
{
  private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader";
  
  FactoryFinder() {}
  
  private static Object newInstance(String paramString, ClassLoader paramClassLoader)
  {
    try
    {
      Class localClass = safeLoadClass(paramString, paramClassLoader);
      return localClass.newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new WebServiceException("Provider " + paramString + " not found", localClassNotFoundException);
    }
    catch (Exception localException)
    {
      throw new WebServiceException("Provider " + paramString + " could not be instantiated: " + localException, localException);
    }
  }
  
  static Object find(String paramString1, String paramString2)
  {
    if (isOsgi()) {
      return lookupUsingOSGiServiceLoader(paramString1);
    }
    ClassLoader localClassLoader;
    try
    {
      localClassLoader = Thread.currentThread().getContextClassLoader();
    }
    catch (Exception localException1)
    {
      throw new WebServiceException(localException1.toString(), localException1);
    }
    String str1 = "META-INF/services/" + paramString1;
    BufferedReader localBufferedReader = null;
    String str2;
    Object localObject1;
    try
    {
      InputStream localInputStream;
      if (localClassLoader == null) {
        localInputStream = ClassLoader.getSystemResourceAsStream(str1);
      } else {
        localInputStream = localClassLoader.getResourceAsStream(str1);
      }
      if (localInputStream != null)
      {
        localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, "UTF-8"));
        str2 = localBufferedReader.readLine();
        if ((str2 != null) && (!"".equals(str2)))
        {
          localObject1 = newInstance(str2, localClassLoader);
          return localObject1;
        }
      }
    }
    catch (Exception localException2) {}finally
    {
      close(localBufferedReader);
    }
    FileInputStream localFileInputStream = null;
    try
    {
      str2 = System.getProperty("java.home");
      localObject1 = str2 + File.separator + "lib" + File.separator + "jaxws.properties";
      File localFile = new File((String)localObject1);
      if (localFile.exists())
      {
        Properties localProperties = new Properties();
        localFileInputStream = new FileInputStream(localFile);
        localProperties.load(localFileInputStream);
        String str4 = localProperties.getProperty(paramString1);
        Object localObject3 = newInstance(str4, localClassLoader);
        return localObject3;
      }
    }
    catch (Exception localException3) {}finally
    {
      close(localFileInputStream);
    }
    try
    {
      String str3 = System.getProperty(paramString1);
      if (str3 != null) {
        return newInstance(str3, localClassLoader);
      }
    }
    catch (SecurityException localSecurityException) {}
    if (paramString2 == null) {
      throw new WebServiceException("Provider for " + paramString1 + " cannot be found", null);
    }
    return newInstance(paramString2, localClassLoader);
  }
  
  private static void close(Closeable paramCloseable)
  {
    if (paramCloseable != null) {
      try
      {
        paramCloseable.close();
      }
      catch (IOException localIOException) {}
    }
  }
  
  private static Class safeLoadClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        int i = paramString.lastIndexOf('.');
        if (i != -1) {
          localSecurityManager.checkPackageAccess(paramString.substring(0, i));
        }
      }
      if (paramClassLoader == null) {
        return Class.forName(paramString);
      }
      return paramClassLoader.loadClass(paramString);
    }
    catch (SecurityException localSecurityException)
    {
      if ("com.sun.xml.internal.ws.spi.ProviderImpl".equals(paramString)) {
        return Class.forName(paramString);
      }
      throw localSecurityException;
    }
  }
  
  private static boolean isOsgi()
  {
    try
    {
      Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
      return true;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return false;
  }
  
  private static Object lookupUsingOSGiServiceLoader(String paramString)
  {
    try
    {
      Class localClass1 = Class.forName(paramString);
      Class[] arrayOfClass = { localClass1 };
      Class localClass2 = Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
      Method localMethod = localClass2.getMethod("lookupProviderInstances", new Class[] { Class.class });
      Iterator localIterator = ((Iterable)localMethod.invoke(null, (Object[])arrayOfClass)).iterator();
      return localIterator.hasNext() ? localIterator.next() : null;
    }
    catch (Exception localException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\spi\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */