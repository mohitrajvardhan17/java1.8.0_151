package javax.management.remote;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.MBeanServer;

public class JMXConnectorServerFactory
{
  public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
  public static final String DEFAULT_CLASS_LOADER_NAME = "jmx.remote.default.class.loader.name";
  public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
  public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
  private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorServerFactory");
  
  private JMXConnectorServerFactory() {}
  
  private static JMXConnectorServer getConnectorServerAsService(ClassLoader paramClassLoader, JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer)
    throws IOException
  {
    Iterator localIterator = JMXConnectorFactory.getProviderIterator(JMXConnectorServerProvider.class, paramClassLoader);
    IOException localIOException = null;
    while (localIterator.hasNext()) {
      try
      {
        return ((JMXConnectorServerProvider)localIterator.next()).newJMXConnectorServer(paramJMXServiceURL, paramMap, paramMBeanServer);
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
  
  public static JMXConnectorServer newJMXConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer)
    throws IOException
  {
    if (paramMap == null)
    {
      localObject1 = new HashMap();
    }
    else
    {
      EnvHelp.checkAttributes(paramMap);
      localObject1 = new HashMap(paramMap);
    }
    Class localClass = JMXConnectorServerProvider.class;
    ClassLoader localClassLoader = JMXConnectorFactory.resolveClassLoader((Map)localObject1);
    String str = paramJMXServiceURL.getProtocol();
    JMXConnectorServerProvider localJMXConnectorServerProvider = (JMXConnectorServerProvider)JMXConnectorFactory.getProvider(paramJMXServiceURL, (Map)localObject1, "ServerProvider", localClass, localClassLoader);
    Object localObject2 = null;
    if (localJMXConnectorServerProvider == null)
    {
      if (localClassLoader != null) {
        try
        {
          JMXConnectorServer localJMXConnectorServer = getConnectorServerAsService(localClassLoader, paramJMXServiceURL, (Map)localObject1, paramMBeanServer);
          if (localJMXConnectorServer != null) {
            return localJMXConnectorServer;
          }
        }
        catch (JMXProviderException localJMXProviderException)
        {
          throw localJMXProviderException;
        }
        catch (IOException localIOException)
        {
          localObject2 = localIOException;
        }
      }
      localJMXConnectorServerProvider = (JMXConnectorServerProvider)JMXConnectorFactory.getProvider(str, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ServerProvider", localClass);
    }
    if (localJMXConnectorServerProvider == null)
    {
      MalformedURLException localMalformedURLException = new MalformedURLException("Unsupported protocol: " + str);
      if (localObject2 == null) {
        throw localMalformedURLException;
      }
      throw ((MalformedURLException)EnvHelp.initCause(localMalformedURLException, (Throwable)localObject2));
    }
    Object localObject1 = Collections.unmodifiableMap((Map)localObject1);
    return localJMXConnectorServerProvider.newJMXConnectorServer(paramJMXServiceURL, (Map)localObject1, paramMBeanServer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXConnectorServerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */