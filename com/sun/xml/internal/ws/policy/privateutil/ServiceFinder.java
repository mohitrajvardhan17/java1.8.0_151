package com.sun.xml.internal.ws.policy.privateutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

final class ServiceFinder<T>
  implements Iterable<T>
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ServiceFinder.class);
  private static final String prefix = "META-INF/services/";
  private final Class<T> serviceClass;
  private final ClassLoader classLoader;
  
  static <T> ServiceFinder<T> find(Class<T> paramClass, ClassLoader paramClassLoader)
  {
    if (null == paramClass) {
      throw ((NullPointerException)LOGGER.logSevereException(new NullPointerException(LocalizationMessages.WSP_0032_SERVICE_CAN_NOT_BE_NULL())));
    }
    return new ServiceFinder(paramClass, paramClassLoader);
  }
  
  public static <T> ServiceFinder<T> find(Class<T> paramClass)
  {
    return find(paramClass, Thread.currentThread().getContextClassLoader());
  }
  
  private ServiceFinder(Class<T> paramClass, ClassLoader paramClassLoader)
  {
    serviceClass = paramClass;
    classLoader = paramClassLoader;
  }
  
  public Iterator<T> iterator()
  {
    return new LazyIterator(serviceClass, classLoader, null);
  }
  
  public T[] toArray()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      localArrayList.add(localObject);
    }
    return localArrayList.toArray((Object[])Array.newInstance(serviceClass, localArrayList.size()));
  }
  
  private static void fail(Class paramClass, String paramString, Throwable paramThrowable)
    throws ServiceConfigurationError
  {
    ServiceConfigurationError localServiceConfigurationError = new ServiceConfigurationError(LocalizationMessages.WSP_0025_SPI_FAIL_SERVICE_MSG(paramClass.getName(), paramString));
    if (null != paramThrowable) {
      localServiceConfigurationError.initCause(paramThrowable);
    }
    throw ((ServiceConfigurationError)LOGGER.logSevereException(localServiceConfigurationError));
  }
  
  private static void fail(Class paramClass, URL paramURL, int paramInt, String paramString, Throwable paramThrowable)
    throws ServiceConfigurationError
  {
    fail(paramClass, LocalizationMessages.WSP_0024_SPI_FAIL_SERVICE_URL_LINE_MSG(paramURL, Integer.valueOf(paramInt), paramString), paramThrowable);
  }
  
  private static int parseLine(Class paramClass, URL paramURL, BufferedReader paramBufferedReader, int paramInt, List<String> paramList, Set<String> paramSet)
    throws IOException, ServiceConfigurationError
  {
    String str = paramBufferedReader.readLine();
    if (str == null) {
      return -1;
    }
    int i = str.indexOf('#');
    if (i >= 0) {
      str = str.substring(0, i);
    }
    str = str.trim();
    int j = str.length();
    if (j != 0)
    {
      if ((str.indexOf(' ') >= 0) || (str.indexOf('\t') >= 0)) {
        fail(paramClass, paramURL, paramInt, LocalizationMessages.WSP_0067_ILLEGAL_CFG_FILE_SYNTAX(), null);
      }
      int k = str.codePointAt(0);
      if (!Character.isJavaIdentifierStart(k)) {
        fail(paramClass, paramURL, paramInt, LocalizationMessages.WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(str), null);
      }
      int m = Character.charCount(k);
      while (m < j)
      {
        k = str.codePointAt(m);
        if ((!Character.isJavaIdentifierPart(k)) && (k != 46)) {
          fail(paramClass, paramURL, paramInt, LocalizationMessages.WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(str), null);
        }
        m += Character.charCount(k);
      }
      if (!paramSet.contains(str))
      {
        paramList.add(str);
        paramSet.add(str);
      }
    }
    return paramInt + 1;
  }
  
  private static Iterator<String> parse(Class paramClass, URL paramURL, Set<String> paramSet)
    throws ServiceConfigurationError
  {
    InputStream localInputStream = null;
    BufferedReader localBufferedReader = null;
    localArrayList = new ArrayList();
    try
    {
      localInputStream = paramURL.openStream();
      localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, "utf-8"));
      int i = 1;
      while ((i = parseLine(paramClass, paramURL, localBufferedReader, i, localArrayList, paramSet)) >= 0) {}
      return localArrayList.iterator();
    }
    catch (IOException localIOException2)
    {
      fail(paramClass, ": " + localIOException2, localIOException2);
    }
    finally
    {
      try
      {
        if (localBufferedReader != null) {
          localBufferedReader.close();
        }
        if (localInputStream != null) {
          localInputStream.close();
        }
      }
      catch (IOException localIOException4)
      {
        fail(paramClass, ": " + localIOException4, localIOException4);
      }
    }
  }
  
  private static class LazyIterator<T>
    implements Iterator<T>
  {
    Class<T> service;
    ClassLoader loader;
    Enumeration<URL> configs = null;
    Iterator<String> pending = null;
    Set<String> returned = new TreeSet();
    String nextName = null;
    
    private LazyIterator(Class<T> paramClass, ClassLoader paramClassLoader)
    {
      service = paramClass;
      loader = paramClassLoader;
    }
    
    public boolean hasNext()
      throws ServiceConfigurationError
    {
      if (nextName != null) {
        return true;
      }
      if (configs == null) {
        try
        {
          String str = "META-INF/services/" + service.getName();
          if (loader == null) {
            configs = ClassLoader.getSystemResources(str);
          } else {
            configs = loader.getResources(str);
          }
        }
        catch (IOException localIOException)
        {
          ServiceFinder.fail(service, ": " + localIOException, localIOException);
        }
      }
      while ((pending == null) || (!pending.hasNext()))
      {
        if (!configs.hasMoreElements()) {
          return false;
        }
        pending = ServiceFinder.parse(service, (URL)configs.nextElement(), returned);
      }
      nextName = ((String)pending.next());
      return true;
    }
    
    public T next()
      throws ServiceConfigurationError
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      String str = nextName;
      nextName = null;
      try
      {
        return (T)service.cast(Class.forName(str, true, loader).newInstance());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        ServiceFinder.fail(service, LocalizationMessages.WSP_0027_SERVICE_PROVIDER_NOT_FOUND(str), localClassNotFoundException);
      }
      catch (Exception localException)
      {
        ServiceFinder.fail(service, LocalizationMessages.WSP_0028_SERVICE_PROVIDER_COULD_NOT_BE_INSTANTIATED(str), localException);
      }
      return null;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\privateutil\ServiceFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */