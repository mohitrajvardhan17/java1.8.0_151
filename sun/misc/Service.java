package sun.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public final class Service<S>
{
  private static final String prefix = "META-INF/services/";
  
  private Service() {}
  
  private static void fail(Class<?> paramClass, String paramString, Throwable paramThrowable)
    throws ServiceConfigurationError
  {
    ServiceConfigurationError localServiceConfigurationError = new ServiceConfigurationError(paramClass.getName() + ": " + paramString);
    localServiceConfigurationError.initCause(paramThrowable);
    throw localServiceConfigurationError;
  }
  
  private static void fail(Class<?> paramClass, String paramString)
    throws ServiceConfigurationError
  {
    throw new ServiceConfigurationError(paramClass.getName() + ": " + paramString);
  }
  
  private static void fail(Class<?> paramClass, URL paramURL, int paramInt, String paramString)
    throws ServiceConfigurationError
  {
    fail(paramClass, paramURL + ":" + paramInt + ": " + paramString);
  }
  
  private static int parseLine(Class<?> paramClass, URL paramURL, BufferedReader paramBufferedReader, int paramInt, List<String> paramList, Set<String> paramSet)
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
        fail(paramClass, paramURL, paramInt, "Illegal configuration-file syntax");
      }
      int k = str.codePointAt(0);
      if (!Character.isJavaIdentifierStart(k)) {
        fail(paramClass, paramURL, paramInt, "Illegal provider-class name: " + str);
      }
      int m = Character.charCount(k);
      while (m < j)
      {
        k = str.codePointAt(m);
        if ((!Character.isJavaIdentifierPart(k)) && (k != 46)) {
          fail(paramClass, paramURL, paramInt, "Illegal provider-class name: " + str);
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
  
  private static Iterator<String> parse(Class<?> paramClass, URL paramURL, Set<String> paramSet)
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
      fail(paramClass, ": " + localIOException2);
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
        fail(paramClass, ": " + localIOException4);
      }
    }
  }
  
  public static <S> Iterator<S> providers(Class<S> paramClass, ClassLoader paramClassLoader)
    throws ServiceConfigurationError
  {
    return new LazyIterator(paramClass, paramClassLoader, null);
  }
  
  public static <S> Iterator<S> providers(Class<S> paramClass)
    throws ServiceConfigurationError
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    return providers(paramClass, localClassLoader);
  }
  
  public static <S> Iterator<S> installedProviders(Class<S> paramClass)
    throws ServiceConfigurationError
  {
    ClassLoader localClassLoader1 = ClassLoader.getSystemClassLoader();
    ClassLoader localClassLoader2 = null;
    while (localClassLoader1 != null)
    {
      localClassLoader2 = localClassLoader1;
      localClassLoader1 = localClassLoader1.getParent();
    }
    return providers(paramClass, localClassLoader2);
  }
  
  private static class LazyIterator<S>
    implements Iterator<S>
  {
    Class<S> service;
    ClassLoader loader;
    Enumeration<URL> configs = null;
    Iterator<String> pending = null;
    Set<String> returned = new TreeSet();
    String nextName = null;
    
    private LazyIterator(Class<S> paramClass, ClassLoader paramClassLoader)
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
          Service.fail(service, ": " + localIOException);
        }
      }
      while ((pending == null) || (!pending.hasNext()))
      {
        if (!configs.hasMoreElements()) {
          return false;
        }
        pending = Service.parse(service, (URL)configs.nextElement(), returned);
      }
      nextName = ((String)pending.next());
      return true;
    }
    
    public S next()
      throws ServiceConfigurationError
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      String str = nextName;
      nextName = null;
      Class localClass = null;
      try
      {
        localClass = Class.forName(str, false, loader);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Service.fail(service, "Provider " + str + " not found");
      }
      if (!service.isAssignableFrom(localClass)) {
        Service.fail(service, "Provider " + str + " not a subtype");
      }
      try
      {
        return (S)service.cast(localClass.newInstance());
      }
      catch (Throwable localThrowable)
      {
        Service.fail(service, "Provider " + str + " could not be instantiated", localThrowable);
      }
      return null;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Service.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */