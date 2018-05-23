package java.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ServiceLoader<S>
  implements Iterable<S>
{
  private static final String PREFIX = "META-INF/services/";
  private final Class<S> service;
  private final ClassLoader loader;
  private final AccessControlContext acc;
  private LinkedHashMap<String, S> providers = new LinkedHashMap();
  private ServiceLoader<S>.LazyIterator lookupIterator;
  
  public void reload()
  {
    providers.clear();
    lookupIterator = new LazyIterator(service, loader, null);
  }
  
  private ServiceLoader(Class<S> paramClass, ClassLoader paramClassLoader)
  {
    service = ((Class)Objects.requireNonNull(paramClass, "Service interface cannot be null"));
    loader = (paramClassLoader == null ? ClassLoader.getSystemClassLoader() : paramClassLoader);
    acc = (System.getSecurityManager() != null ? AccessController.getContext() : null);
    reload();
  }
  
  private static void fail(Class<?> paramClass, String paramString, Throwable paramThrowable)
    throws ServiceConfigurationError
  {
    throw new ServiceConfigurationError(paramClass.getName() + ": " + paramString, paramThrowable);
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
  
  private int parseLine(Class<?> paramClass, URL paramURL, BufferedReader paramBufferedReader, int paramInt, List<String> paramList)
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
      if ((!providers.containsKey(str)) && (!paramList.contains(str))) {
        paramList.add(str);
      }
    }
    return paramInt + 1;
  }
  
  private Iterator<String> parse(Class<?> paramClass, URL paramURL)
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
      while ((i = parseLine(paramClass, paramURL, localBufferedReader, i, localArrayList)) >= 0) {}
      return localArrayList.iterator();
    }
    catch (IOException localIOException2)
    {
      fail(paramClass, "Error reading configuration file", localIOException2);
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
        fail(paramClass, "Error closing configuration file", localIOException4);
      }
    }
  }
  
  public Iterator<S> iterator()
  {
    new Iterator()
    {
      Iterator<Map.Entry<String, S>> knownProviders = providers.entrySet().iterator();
      
      public boolean hasNext()
      {
        if (knownProviders.hasNext()) {
          return true;
        }
        return lookupIterator.hasNext();
      }
      
      public S next()
      {
        if (knownProviders.hasNext()) {
          return (S)((Map.Entry)knownProviders.next()).getValue();
        }
        return (S)lookupIterator.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public static <S> ServiceLoader<S> load(Class<S> paramClass, ClassLoader paramClassLoader)
  {
    return new ServiceLoader(paramClass, paramClassLoader);
  }
  
  public static <S> ServiceLoader<S> load(Class<S> paramClass)
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    return load(paramClass, localClassLoader);
  }
  
  public static <S> ServiceLoader<S> loadInstalled(Class<S> paramClass)
  {
    ClassLoader localClassLoader1 = ClassLoader.getSystemClassLoader();
    ClassLoader localClassLoader2 = null;
    while (localClassLoader1 != null)
    {
      localClassLoader2 = localClassLoader1;
      localClassLoader1 = localClassLoader1.getParent();
    }
    return load(paramClass, localClassLoader2);
  }
  
  public String toString()
  {
    return "java.util.ServiceLoader[" + service.getName() + "]";
  }
  
  private class LazyIterator
    implements Iterator<S>
  {
    Class<S> service;
    ClassLoader loader;
    Enumeration<URL> configs = null;
    Iterator<String> pending = null;
    String nextName = null;
    
    private LazyIterator(ClassLoader paramClassLoader)
    {
      service = paramClassLoader;
      ClassLoader localClassLoader;
      loader = localClassLoader;
    }
    
    private boolean hasNextService()
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
          ServiceLoader.fail(service, "Error locating configuration files", localIOException);
        }
      }
      while ((pending == null) || (!pending.hasNext()))
      {
        if (!configs.hasMoreElements()) {
          return false;
        }
        pending = ServiceLoader.this.parse(service, (URL)configs.nextElement());
      }
      nextName = ((String)pending.next());
      return true;
    }
    
    private S nextService()
    {
      if (!hasNextService()) {
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
        ServiceLoader.fail(service, "Provider " + str + " not found");
      }
      if (!service.isAssignableFrom(localClass)) {
        ServiceLoader.fail(service, "Provider " + str + " not a subtype");
      }
      try
      {
        Object localObject = service.cast(localClass.newInstance());
        providers.put(str, localObject);
        return (S)localObject;
      }
      catch (Throwable localThrowable)
      {
        ServiceLoader.fail(service, "Provider " + str + " could not be instantiated", localThrowable);
        throw new Error();
      }
    }
    
    public boolean hasNext()
    {
      if (acc == null) {
        return hasNextService();
      }
      PrivilegedAction local1 = new PrivilegedAction()
      {
        public Boolean run()
        {
          return Boolean.valueOf(ServiceLoader.LazyIterator.this.hasNextService());
        }
      };
      return ((Boolean)AccessController.doPrivileged(local1, acc)).booleanValue();
    }
    
    public S next()
    {
      if (acc == null) {
        return (S)nextService();
      }
      PrivilegedAction local2 = new PrivilegedAction()
      {
        public S run()
        {
          return (S)ServiceLoader.LazyIterator.this.nextService();
        }
      };
      return (S)AccessController.doPrivileged(local2, acc);
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\ServiceLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */