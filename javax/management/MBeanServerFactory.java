package javax.management;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.loading.ClassLoaderRepository;
import sun.reflect.misc.ReflectUtil;

public class MBeanServerFactory
{
  private static MBeanServerBuilder builder = null;
  private static final ArrayList<MBeanServer> mBeanServerList = new ArrayList();
  
  private MBeanServerFactory() {}
  
  public static void releaseMBeanServer(MBeanServer paramMBeanServer)
  {
    checkPermission("releaseMBeanServer");
    removeMBeanServer(paramMBeanServer);
  }
  
  public static MBeanServer createMBeanServer()
  {
    return createMBeanServer(null);
  }
  
  public static MBeanServer createMBeanServer(String paramString)
  {
    checkPermission("createMBeanServer");
    MBeanServer localMBeanServer = newMBeanServer(paramString);
    addMBeanServer(localMBeanServer);
    return localMBeanServer;
  }
  
  public static MBeanServer newMBeanServer()
  {
    return newMBeanServer(null);
  }
  
  public static MBeanServer newMBeanServer(String paramString)
  {
    checkPermission("newMBeanServer");
    MBeanServerBuilder localMBeanServerBuilder = getNewMBeanServerBuilder();
    synchronized (localMBeanServerBuilder)
    {
      MBeanServerDelegate localMBeanServerDelegate = localMBeanServerBuilder.newMBeanServerDelegate();
      if (localMBeanServerDelegate == null) {
        throw new JMRuntimeException("MBeanServerBuilder.newMBeanServerDelegate() returned null");
      }
      MBeanServer localMBeanServer = localMBeanServerBuilder.newMBeanServer(paramString, null, localMBeanServerDelegate);
      if (localMBeanServer == null) {
        throw new JMRuntimeException("MBeanServerBuilder.newMBeanServer() returned null");
      }
      return localMBeanServer;
    }
  }
  
  public static synchronized ArrayList<MBeanServer> findMBeanServer(String paramString)
  {
    checkPermission("findMBeanServer");
    if (paramString == null) {
      return new ArrayList(mBeanServerList);
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = mBeanServerList.iterator();
    while (localIterator.hasNext())
    {
      MBeanServer localMBeanServer = (MBeanServer)localIterator.next();
      String str = mBeanServerId(localMBeanServer);
      if (paramString.equals(str)) {
        localArrayList.add(localMBeanServer);
      }
    }
    return localArrayList;
  }
  
  public static ClassLoaderRepository getClassLoaderRepository(MBeanServer paramMBeanServer)
  {
    return paramMBeanServer.getClassLoaderRepository();
  }
  
  private static String mBeanServerId(MBeanServer paramMBeanServer)
  {
    try
    {
      return (String)paramMBeanServer.getAttribute(MBeanServerDelegate.DELEGATE_NAME, "MBeanServerId");
    }
    catch (JMException localJMException)
    {
      JmxProperties.MISC_LOGGER.finest("Ignoring exception while getting MBeanServerId: " + localJMException);
    }
    return null;
  }
  
  private static void checkPermission(String paramString)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      MBeanServerPermission localMBeanServerPermission = new MBeanServerPermission(paramString);
      localSecurityManager.checkPermission(localMBeanServerPermission);
    }
  }
  
  private static synchronized void addMBeanServer(MBeanServer paramMBeanServer)
  {
    mBeanServerList.add(paramMBeanServer);
  }
  
  private static synchronized void removeMBeanServer(MBeanServer paramMBeanServer)
  {
    boolean bool = mBeanServerList.remove(paramMBeanServer);
    if (!bool)
    {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "removeMBeanServer(MBeanServer)", "MBeanServer was not in list!");
      throw new IllegalArgumentException("MBeanServer was not in list!");
    }
  }
  
  private static Class<?> loadBuilderClass(String paramString)
    throws ClassNotFoundException
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    if (localClassLoader != null) {
      return localClassLoader.loadClass(paramString);
    }
    return ReflectUtil.forName(paramString);
  }
  
  private static MBeanServerBuilder newBuilder(Class<?> paramClass)
  {
    try
    {
      Object localObject = paramClass.newInstance();
      return (MBeanServerBuilder)localObject;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      String str = "Failed to instantiate a MBeanServerBuilder from " + paramClass + ": " + localException;
      throw new JMRuntimeException(str, localException);
    }
  }
  
  private static synchronized void checkMBeanServerBuilder()
  {
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("javax.management.builder.initial");
      localObject1 = (String)AccessController.doPrivileged(localGetPropertyAction);
      try
      {
        Class localClass;
        if ((localObject1 == null) || (((String)localObject1).length() == 0)) {
          localClass = MBeanServerBuilder.class;
        } else {
          localClass = loadBuilderClass((String)localObject1);
        }
        if (builder != null)
        {
          localObject2 = builder.getClass();
          if (localClass == localObject2) {
            return;
          }
        }
        builder = newBuilder(localClass);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Object localObject2 = "Failed to load MBeanServerBuilder class " + (String)localObject1 + ": " + localClassNotFoundException;
        throw new JMRuntimeException((String)localObject2, localClassNotFoundException);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      Object localObject1;
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
      {
        localObject1 = new StringBuilder().append("Failed to instantiate MBeanServerBuilder: ").append(localRuntimeException).append("\n\t\tCheck the value of the ").append("javax.management.builder.initial").append(" property.");
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "checkMBeanServerBuilder", ((StringBuilder)localObject1).toString());
      }
      throw localRuntimeException;
    }
  }
  
  private static synchronized MBeanServerBuilder getNewMBeanServerBuilder()
  {
    checkMBeanServerBuilder();
    return builder;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanServerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */