package javax.management.remote;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import sun.reflect.misc.ReflectUtil;

public class JMXConnectorFactory
{
  public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
  public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
  public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
  private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorFactory");
  
  private JMXConnectorFactory() {}
  
  public static JMXConnector connect(JMXServiceURL paramJMXServiceURL)
    throws IOException
  {
    return connect(paramJMXServiceURL, null);
  }
  
  public static JMXConnector connect(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
    throws IOException
  {
    if (paramJMXServiceURL == null) {
      throw new NullPointerException("Null JMXServiceURL");
    }
    JMXConnector localJMXConnector = newJMXConnector(paramJMXServiceURL, paramMap);
    localJMXConnector.connect(paramMap);
    return localJMXConnector;
  }
  
  private static <K, V> Map<K, V> newHashMap()
  {
    return new HashMap();
  }
  
  private static <K> Map<K, Object> newHashMap(Map<K, ?> paramMap)
  {
    return new HashMap(paramMap);
  }
  
  public static JMXConnector newJMXConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
    throws IOException
  {
    Map localMap;
    if (paramMap == null)
    {
      localMap = newHashMap();
    }
    else
    {
      EnvHelp.checkAttributes(paramMap);
      localMap = newHashMap(paramMap);
    }
    ClassLoader localClassLoader = resolveClassLoader(localMap);
    Class localClass = JMXConnectorProvider.class;
    String str = paramJMXServiceURL.getProtocol();
    JMXServiceURL localJMXServiceURL = paramJMXServiceURL;
    JMXConnectorProvider localJMXConnectorProvider = (JMXConnectorProvider)getProvider(localJMXServiceURL, localMap, "ClientProvider", localClass, localClassLoader);
    Object localObject1 = null;
    if (localJMXConnectorProvider == null)
    {
      if (localClassLoader != null) {
        try
        {
          JMXConnector localJMXConnector = getConnectorAsService(localClassLoader, localJMXServiceURL, localMap);
          if (localJMXConnector != null) {
            return localJMXConnector;
          }
        }
        catch (JMXProviderException localJMXProviderException)
        {
          throw localJMXProviderException;
        }
        catch (IOException localIOException)
        {
          localObject1 = localIOException;
        }
      }
      localJMXConnectorProvider = (JMXConnectorProvider)getProvider(str, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ClientProvider", localClass);
    }
    if (localJMXConnectorProvider == null)
    {
      localObject2 = new MalformedURLException("Unsupported protocol: " + str);
      if (localObject1 == null) {
        throw ((Throwable)localObject2);
      }
      throw ((MalformedURLException)EnvHelp.initCause((Throwable)localObject2, (Throwable)localObject1));
    }
    Object localObject2 = Collections.unmodifiableMap(localMap);
    return localJMXConnectorProvider.newJMXConnector(paramJMXServiceURL, (Map)localObject2);
  }
  
  private static String resolvePkgs(Map<String, ?> paramMap)
    throws JMXProviderException
  {
    Object localObject = null;
    if (paramMap != null) {
      localObject = paramMap.get("jmx.remote.protocol.provider.pkgs");
    }
    if (localObject == null) {
      localObject = AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getProperty("jmx.remote.protocol.provider.pkgs");
        }
      });
    }
    if (localObject == null) {
      return null;
    }
    if (!(localObject instanceof String))
    {
      str1 = "Value of jmx.remote.protocol.provider.pkgs parameter is not a String: " + localObject.getClass().getName();
      throw new JMXProviderException(str1);
    }
    String str1 = (String)localObject;
    if (str1.trim().equals("")) {
      return null;
    }
    if ((str1.startsWith("|")) || (str1.endsWith("|")) || (str1.indexOf("||") >= 0))
    {
      String str2 = "Value of jmx.remote.protocol.provider.pkgs contains an empty element: " + str1;
      throw new JMXProviderException(str2);
    }
    return str1;
  }
  
  static <T> T getProvider(JMXServiceURL paramJMXServiceURL, Map<String, Object> paramMap, String paramString, Class<T> paramClass, ClassLoader paramClassLoader)
    throws IOException
  {
    String str1 = paramJMXServiceURL.getProtocol();
    String str2 = resolvePkgs(paramMap);
    Object localObject = null;
    if (str2 != null)
    {
      localObject = getProvider(str1, str2, paramClassLoader, paramString, paramClass);
      if (localObject != null)
      {
        int i = paramClassLoader != localObject.getClass().getClassLoader() ? 1 : 0;
        paramMap.put("jmx.remote.protocol.provider.class.loader", i != 0 ? wrap(paramClassLoader) : paramClassLoader);
      }
    }
    return (T)localObject;
  }
  
  static <T> Iterator<T> getProviderIterator(Class<T> paramClass, ClassLoader paramClassLoader)
  {
    ServiceLoader localServiceLoader = ServiceLoader.load(paramClass, paramClassLoader);
    return localServiceLoader.iterator();
  }
  
  private static ClassLoader wrap(ClassLoader paramClassLoader)
  {
    paramClassLoader != null ? (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        new ClassLoader(val$parent)
        {
          protected Class<?> loadClass(String paramAnonymous2String, boolean paramAnonymous2Boolean)
            throws ClassNotFoundException
          {
            ReflectUtil.checkPackageAccess(paramAnonymous2String);
            return super.loadClass(paramAnonymous2String, paramAnonymous2Boolean);
          }
        };
      }
    }) : null;
  }
  
  private static JMXConnector getConnectorAsService(ClassLoader paramClassLoader, JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
    throws IOException
  {
    Iterator localIterator = getProviderIterator(JMXConnectorProvider.class, paramClassLoader);
    IOException localIOException = null;
    while (localIterator.hasNext())
    {
      JMXConnectorProvider localJMXConnectorProvider = (JMXConnectorProvider)localIterator.next();
      try
      {
        JMXConnector localJMXConnector = localJMXConnectorProvider.newJMXConnector(paramJMXServiceURL, paramMap);
        return localJMXConnector;
      }
      catch (JMXProviderException localJMXProviderException)
      {
        throw localJMXProviderException;
      }
      catch (Exception localException)
      {
        if (logger.traceOn()) {
          logger.trace("getConnectorAsService", "URL[" + paramJMXServiceURL + "] Service provider exception: " + localException);
        }
        if ((!(localException instanceof MalformedURLException)) && (localIOException == null)) {
          if ((localException instanceof IOException)) {
            localIOException = (IOException)localException;
          } else {
            localIOException = (IOException)EnvHelp.initCause(new IOException(localException.getMessage()), localException);
          }
        }
      }
    }
    if (localIOException == null) {
      return null;
    }
    throw localIOException;
  }
  
  static <T> T getProvider(String paramString1, String paramString2, ClassLoader paramClassLoader, String paramString3, Class<T> paramClass)
    throws IOException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, "|");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str1 = localStringTokenizer.nextToken();
      String str2 = str1 + "." + protocol2package(paramString1) + "." + paramString3;
      Class localClass;
      try
      {
        localClass = Class.forName(str2, true, paramClassLoader);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      continue;
      if (!paramClass.isAssignableFrom(localClass))
      {
        localObject = "Provider class does not implement " + paramClass.getName() + ": " + localClass.getName();
        throw new JMXProviderException((String)localObject);
      }
      Object localObject = (Class)Util.cast(localClass);
      try
      {
        return (T)((Class)localObject).newInstance();
      }
      catch (Exception localException)
      {
        String str3 = "Exception when instantiating provider [" + str2 + "]";
        throw new JMXProviderException(str3, localException);
      }
    }
    return null;
  }
  
  static ClassLoader resolveClassLoader(Map<String, ?> paramMap)
  {
    ClassLoader localClassLoader = null;
    if (paramMap != null) {
      try
      {
        localClassLoader = (ClassLoader)paramMap.get("jmx.remote.protocol.provider.class.loader");
      }
      catch (ClassCastException localClassCastException)
      {
        throw new IllegalArgumentException("The ClassLoader supplied in the environment map using the jmx.remote.protocol.provider.class.loader attribute is not an instance of java.lang.ClassLoader");
      }
    }
    if (localClassLoader == null) {
      localClassLoader = Thread.currentThread().getContextClassLoader();
    }
    return localClassLoader;
  }
  
  private static String protocol2package(String paramString)
  {
    return paramString.replace('+', '.').replace('-', '_');
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXConnectorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */