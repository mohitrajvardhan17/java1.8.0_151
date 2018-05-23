package java.lang.management;

import java.io.IOException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerPermission;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationEmitter;
import javax.management.ObjectName;
import javax.management.StandardEmitterMBean;
import javax.management.StandardMBean;
import sun.management.ExtendedPlatformComponent;
import sun.management.ManagementFactoryHelper;
import sun.misc.VM;

public class ManagementFactory
{
  public static final String CLASS_LOADING_MXBEAN_NAME = "java.lang:type=ClassLoading";
  public static final String COMPILATION_MXBEAN_NAME = "java.lang:type=Compilation";
  public static final String MEMORY_MXBEAN_NAME = "java.lang:type=Memory";
  public static final String OPERATING_SYSTEM_MXBEAN_NAME = "java.lang:type=OperatingSystem";
  public static final String RUNTIME_MXBEAN_NAME = "java.lang:type=Runtime";
  public static final String THREAD_MXBEAN_NAME = "java.lang:type=Threading";
  public static final String GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE = "java.lang:type=GarbageCollector";
  public static final String MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryManager";
  public static final String MEMORY_POOL_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryPool";
  private static MBeanServer platformMBeanServer;
  private static final String NOTIF_EMITTER = "javax.management.NotificationEmitter";
  
  private ManagementFactory() {}
  
  public static ClassLoadingMXBean getClassLoadingMXBean()
  {
    return ManagementFactoryHelper.getClassLoadingMXBean();
  }
  
  public static MemoryMXBean getMemoryMXBean()
  {
    return ManagementFactoryHelper.getMemoryMXBean();
  }
  
  public static ThreadMXBean getThreadMXBean()
  {
    return ManagementFactoryHelper.getThreadMXBean();
  }
  
  public static RuntimeMXBean getRuntimeMXBean()
  {
    return ManagementFactoryHelper.getRuntimeMXBean();
  }
  
  public static CompilationMXBean getCompilationMXBean()
  {
    return ManagementFactoryHelper.getCompilationMXBean();
  }
  
  public static OperatingSystemMXBean getOperatingSystemMXBean()
  {
    return ManagementFactoryHelper.getOperatingSystemMXBean();
  }
  
  public static List<MemoryPoolMXBean> getMemoryPoolMXBeans()
  {
    return ManagementFactoryHelper.getMemoryPoolMXBeans();
  }
  
  public static List<MemoryManagerMXBean> getMemoryManagerMXBeans()
  {
    return ManagementFactoryHelper.getMemoryManagerMXBeans();
  }
  
  public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans()
  {
    return ManagementFactoryHelper.getGarbageCollectorMXBeans();
  }
  
  public static synchronized MBeanServer getPlatformMBeanServer()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    Object localObject1;
    if (localSecurityManager != null)
    {
      localObject1 = new MBeanServerPermission("createMBeanServer");
      localSecurityManager.checkPermission((Permission)localObject1);
    }
    if (platformMBeanServer == null)
    {
      platformMBeanServer = MBeanServerFactory.createMBeanServer();
      for (Object localObject3 : PlatformComponent.values())
      {
        List localList = ((PlatformComponent)localObject3).getMXBeans(((PlatformComponent)localObject3).getMXBeanInterface());
        Iterator localIterator2 = localList.iterator();
        while (localIterator2.hasNext())
        {
          PlatformManagedObject localPlatformManagedObject = (PlatformManagedObject)localIterator2.next();
          if (!platformMBeanServer.isRegistered(localPlatformManagedObject.getObjectName())) {
            addMXBean(platformMBeanServer, localPlatformManagedObject);
          }
        }
      }
      localObject1 = ManagementFactoryHelper.getPlatformDynamicMBeans();
      Iterator localIterator1 = ((HashMap)localObject1).entrySet().iterator();
      Object localObject2;
      while (localIterator1.hasNext())
      {
        localObject2 = (Map.Entry)localIterator1.next();
        addDynamicMBean(platformMBeanServer, (DynamicMBean)((Map.Entry)localObject2).getValue(), (ObjectName)((Map.Entry)localObject2).getKey());
      }
      localIterator1 = ExtendedPlatformComponent.getMXBeans().iterator();
      while (localIterator1.hasNext())
      {
        localObject2 = (PlatformManagedObject)localIterator1.next();
        if (!platformMBeanServer.isRegistered(((PlatformManagedObject)localObject2).getObjectName())) {
          addMXBean(platformMBeanServer, (PlatformManagedObject)localObject2);
        }
      }
    }
    return platformMBeanServer;
  }
  
  public static <T> T newPlatformMXBeanProxy(MBeanServerConnection paramMBeanServerConnection, String paramString, Class<T> paramClass)
    throws IOException
  {
    Class<T> localClass = paramClass;
    ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return val$cls.getClassLoader();
      }
    });
    if (!VM.isSystemDomainLoader(localClassLoader)) {
      throw new IllegalArgumentException(paramString + " is not a platform MXBean");
    }
    try
    {
      ObjectName localObjectName = new ObjectName(paramString);
      String str = paramClass.getName();
      if (!paramMBeanServerConnection.isInstanceOf(localObjectName, str)) {
        throw new IllegalArgumentException(paramString + " is not an instance of " + paramClass);
      }
      boolean bool = paramMBeanServerConnection.isInstanceOf(localObjectName, "javax.management.NotificationEmitter");
      return (T)JMX.newMXBeanProxy(paramMBeanServerConnection, localObjectName, paramClass, bool);
    }
    catch (InstanceNotFoundException|MalformedObjectNameException localInstanceNotFoundException)
    {
      throw new IllegalArgumentException(localInstanceNotFoundException);
    }
  }
  
  public static <T extends PlatformManagedObject> T getPlatformMXBean(Class<T> paramClass)
  {
    PlatformComponent localPlatformComponent = PlatformComponent.getPlatformComponent(paramClass);
    if (localPlatformComponent == null)
    {
      PlatformManagedObject localPlatformManagedObject = ExtendedPlatformComponent.getMXBean(paramClass);
      if (localPlatformManagedObject != null) {
        return localPlatformManagedObject;
      }
      throw new IllegalArgumentException(paramClass.getName() + " is not a platform management interface");
    }
    if (!localPlatformComponent.isSingleton()) {
      throw new IllegalArgumentException(paramClass.getName() + " can have zero or more than one instances");
    }
    return localPlatformComponent.getSingletonMXBean(paramClass);
  }
  
  public static <T extends PlatformManagedObject> List<T> getPlatformMXBeans(Class<T> paramClass)
  {
    PlatformComponent localPlatformComponent = PlatformComponent.getPlatformComponent(paramClass);
    if (localPlatformComponent == null)
    {
      PlatformManagedObject localPlatformManagedObject = ExtendedPlatformComponent.getMXBean(paramClass);
      if (localPlatformManagedObject != null) {
        return Collections.singletonList(localPlatformManagedObject);
      }
      throw new IllegalArgumentException(paramClass.getName() + " is not a platform management interface");
    }
    return Collections.unmodifiableList(localPlatformComponent.getMXBeans(paramClass));
  }
  
  public static <T extends PlatformManagedObject> T getPlatformMXBean(MBeanServerConnection paramMBeanServerConnection, Class<T> paramClass)
    throws IOException
  {
    PlatformComponent localPlatformComponent = PlatformComponent.getPlatformComponent(paramClass);
    if (localPlatformComponent == null)
    {
      PlatformManagedObject localPlatformManagedObject = ExtendedPlatformComponent.getMXBean(paramClass);
      if (localPlatformManagedObject != null)
      {
        ObjectName localObjectName = localPlatformManagedObject.getObjectName();
        return (PlatformManagedObject)newPlatformMXBeanProxy(paramMBeanServerConnection, localObjectName.getCanonicalName(), paramClass);
      }
      throw new IllegalArgumentException(paramClass.getName() + " is not a platform management interface");
    }
    if (!localPlatformComponent.isSingleton()) {
      throw new IllegalArgumentException(paramClass.getName() + " can have zero or more than one instances");
    }
    return localPlatformComponent.getSingletonMXBean(paramMBeanServerConnection, paramClass);
  }
  
  public static <T extends PlatformManagedObject> List<T> getPlatformMXBeans(MBeanServerConnection paramMBeanServerConnection, Class<T> paramClass)
    throws IOException
  {
    PlatformComponent localPlatformComponent = PlatformComponent.getPlatformComponent(paramClass);
    if (localPlatformComponent == null)
    {
      PlatformManagedObject localPlatformManagedObject1 = ExtendedPlatformComponent.getMXBean(paramClass);
      if (localPlatformManagedObject1 != null)
      {
        ObjectName localObjectName = localPlatformManagedObject1.getObjectName();
        PlatformManagedObject localPlatformManagedObject2 = (PlatformManagedObject)newPlatformMXBeanProxy(paramMBeanServerConnection, localObjectName.getCanonicalName(), paramClass);
        return Collections.singletonList(localPlatformManagedObject2);
      }
      throw new IllegalArgumentException(paramClass.getName() + " is not a platform management interface");
    }
    return Collections.unmodifiableList(localPlatformComponent.getMXBeans(paramMBeanServerConnection, paramClass));
  }
  
  public static Set<Class<? extends PlatformManagedObject>> getPlatformManagementInterfaces()
  {
    HashSet localHashSet = new HashSet();
    for (PlatformComponent localPlatformComponent : PlatformComponent.values()) {
      localHashSet.add(localPlatformComponent.getMXBeanInterface());
    }
    return Collections.unmodifiableSet(localHashSet);
  }
  
  private static void addMXBean(final MBeanServer paramMBeanServer, PlatformManagedObject paramPlatformManagedObject)
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
        {
          Object localObject;
          if ((val$pmo instanceof DynamicMBean)) {
            localObject = (DynamicMBean)DynamicMBean.class.cast(val$pmo);
          } else if ((val$pmo instanceof NotificationEmitter)) {
            localObject = new StandardEmitterMBean(val$pmo, null, true, (NotificationEmitter)val$pmo);
          } else {
            localObject = new StandardMBean(val$pmo, null, true);
          }
          paramMBeanServer.registerMBean(localObject, val$pmo.getObjectName());
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new RuntimeException(localPrivilegedActionException.getException());
    }
  }
  
  private static void addDynamicMBean(MBeanServer paramMBeanServer, final DynamicMBean paramDynamicMBean, final ObjectName paramObjectName)
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
        {
          val$mbs.registerMBean(paramDynamicMBean, paramObjectName);
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new RuntimeException(localPrivilegedActionException.getException());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\ManagementFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */