package sun.management;

import com.sun.management.DiagnosticCommandMBean;
import com.sun.management.HotSpotDiagnosticMXBean;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LoggingMXBean;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import sun.misc.JavaNioAccess;
import sun.misc.JavaNioAccess.BufferPool;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.nio.ch.FileChannelImpl;
import sun.util.logging.LoggingSupport;

public class ManagementFactoryHelper
{
  private static VMManagement jvm = new VMManagementImpl();
  private static ClassLoadingImpl classMBean = null;
  private static MemoryImpl memoryMBean = null;
  private static ThreadImpl threadMBean = null;
  private static RuntimeImpl runtimeMBean = null;
  private static CompilationImpl compileMBean = null;
  private static OperatingSystemImpl osMBean = null;
  private static List<BufferPoolMXBean> bufferPools = null;
  private static final String BUFFER_POOL_MXBEAN_NAME = "java.nio:type=BufferPool";
  private static HotSpotDiagnostic hsDiagMBean = null;
  private static HotspotRuntime hsRuntimeMBean = null;
  private static HotspotClassLoading hsClassMBean = null;
  private static HotspotThread hsThreadMBean = null;
  private static HotspotCompilation hsCompileMBean = null;
  private static HotspotMemory hsMemoryMBean = null;
  private static DiagnosticCommandImpl hsDiagCommandMBean = null;
  private static final String HOTSPOT_CLASS_LOADING_MBEAN_NAME = "sun.management:type=HotspotClassLoading";
  private static final String HOTSPOT_COMPILATION_MBEAN_NAME = "sun.management:type=HotspotCompilation";
  private static final String HOTSPOT_MEMORY_MBEAN_NAME = "sun.management:type=HotspotMemory";
  private static final String HOTSPOT_RUNTIME_MBEAN_NAME = "sun.management:type=HotspotRuntime";
  private static final String HOTSPOT_THREAD_MBEAN_NAME = "sun.management:type=HotspotThreading";
  static final String HOTSPOT_DIAGNOSTIC_COMMAND_MBEAN_NAME = "com.sun.management:type=DiagnosticCommand";
  private static final int JMM_THREAD_STATE_FLAG_MASK = -1048576;
  private static final int JMM_THREAD_STATE_FLAG_SUSPENDED = 1048576;
  private static final int JMM_THREAD_STATE_FLAG_NATIVE = 4194304;
  
  private ManagementFactoryHelper() {}
  
  public static synchronized ClassLoadingMXBean getClassLoadingMXBean()
  {
    if (classMBean == null) {
      classMBean = new ClassLoadingImpl(jvm);
    }
    return classMBean;
  }
  
  public static synchronized MemoryMXBean getMemoryMXBean()
  {
    if (memoryMBean == null) {
      memoryMBean = new MemoryImpl(jvm);
    }
    return memoryMBean;
  }
  
  public static synchronized ThreadMXBean getThreadMXBean()
  {
    if (threadMBean == null) {
      threadMBean = new ThreadImpl(jvm);
    }
    return threadMBean;
  }
  
  public static synchronized RuntimeMXBean getRuntimeMXBean()
  {
    if (runtimeMBean == null) {
      runtimeMBean = new RuntimeImpl(jvm);
    }
    return runtimeMBean;
  }
  
  public static synchronized CompilationMXBean getCompilationMXBean()
  {
    if ((compileMBean == null) && (jvm.getCompilerName() != null)) {
      compileMBean = new CompilationImpl(jvm);
    }
    return compileMBean;
  }
  
  public static synchronized OperatingSystemMXBean getOperatingSystemMXBean()
  {
    if (osMBean == null) {
      osMBean = new OperatingSystemImpl(jvm);
    }
    return osMBean;
  }
  
  public static List<MemoryPoolMXBean> getMemoryPoolMXBeans()
  {
    MemoryPoolMXBean[] arrayOfMemoryPoolMXBean1 = MemoryImpl.getMemoryPools();
    ArrayList localArrayList = new ArrayList(arrayOfMemoryPoolMXBean1.length);
    for (MemoryPoolMXBean localMemoryPoolMXBean : arrayOfMemoryPoolMXBean1) {
      localArrayList.add(localMemoryPoolMXBean);
    }
    return localArrayList;
  }
  
  public static List<MemoryManagerMXBean> getMemoryManagerMXBeans()
  {
    MemoryManagerMXBean[] arrayOfMemoryManagerMXBean1 = MemoryImpl.getMemoryManagers();
    ArrayList localArrayList = new ArrayList(arrayOfMemoryManagerMXBean1.length);
    for (MemoryManagerMXBean localMemoryManagerMXBean : arrayOfMemoryManagerMXBean1) {
      localArrayList.add(localMemoryManagerMXBean);
    }
    return localArrayList;
  }
  
  public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans()
  {
    MemoryManagerMXBean[] arrayOfMemoryManagerMXBean1 = MemoryImpl.getMemoryManagers();
    ArrayList localArrayList = new ArrayList(arrayOfMemoryManagerMXBean1.length);
    for (MemoryManagerMXBean localMemoryManagerMXBean : arrayOfMemoryManagerMXBean1) {
      if (GarbageCollectorMXBean.class.isInstance(localMemoryManagerMXBean)) {
        localArrayList.add(GarbageCollectorMXBean.class.cast(localMemoryManagerMXBean));
      }
    }
    return localArrayList;
  }
  
  public static PlatformLoggingMXBean getPlatformLoggingMXBean()
  {
    if (LoggingSupport.isAvailable()) {
      return PlatformLoggingImpl.instance;
    }
    return null;
  }
  
  public static synchronized List<BufferPoolMXBean> getBufferPoolMXBeans()
  {
    if (bufferPools == null)
    {
      bufferPools = new ArrayList(2);
      bufferPools.add(createBufferPoolMXBean(SharedSecrets.getJavaNioAccess().getDirectBufferPool()));
      bufferPools.add(createBufferPoolMXBean(FileChannelImpl.getMappedBufferPool()));
    }
    return bufferPools;
  }
  
  private static BufferPoolMXBean createBufferPoolMXBean(JavaNioAccess.BufferPool paramBufferPool)
  {
    new BufferPoolMXBean()
    {
      private volatile ObjectName objname;
      
      public ObjectName getObjectName()
      {
        ObjectName localObjectName = objname;
        if (localObjectName == null) {
          synchronized (this)
          {
            localObjectName = objname;
            if (localObjectName == null)
            {
              localObjectName = Util.newObjectName("java.nio:type=BufferPool,name=" + val$pool.getName());
              objname = localObjectName;
            }
          }
        }
        return localObjectName;
      }
      
      public String getName()
      {
        return val$pool.getName();
      }
      
      public long getCount()
      {
        return val$pool.getCount();
      }
      
      public long getTotalCapacity()
      {
        return val$pool.getTotalCapacity();
      }
      
      public long getMemoryUsed()
      {
        return val$pool.getMemoryUsed();
      }
    };
  }
  
  public static synchronized HotSpotDiagnosticMXBean getDiagnosticMXBean()
  {
    if (hsDiagMBean == null) {
      hsDiagMBean = new HotSpotDiagnostic();
    }
    return hsDiagMBean;
  }
  
  public static synchronized HotspotRuntimeMBean getHotspotRuntimeMBean()
  {
    if (hsRuntimeMBean == null) {
      hsRuntimeMBean = new HotspotRuntime(jvm);
    }
    return hsRuntimeMBean;
  }
  
  public static synchronized HotspotClassLoadingMBean getHotspotClassLoadingMBean()
  {
    if (hsClassMBean == null) {
      hsClassMBean = new HotspotClassLoading(jvm);
    }
    return hsClassMBean;
  }
  
  public static synchronized HotspotThreadMBean getHotspotThreadMBean()
  {
    if (hsThreadMBean == null) {
      hsThreadMBean = new HotspotThread(jvm);
    }
    return hsThreadMBean;
  }
  
  public static synchronized HotspotMemoryMBean getHotspotMemoryMBean()
  {
    if (hsMemoryMBean == null) {
      hsMemoryMBean = new HotspotMemory(jvm);
    }
    return hsMemoryMBean;
  }
  
  public static synchronized DiagnosticCommandMBean getDiagnosticCommandMBean()
  {
    if ((hsDiagCommandMBean == null) && (jvm.isRemoteDiagnosticCommandsSupported())) {
      hsDiagCommandMBean = new DiagnosticCommandImpl(jvm);
    }
    return hsDiagCommandMBean;
  }
  
  public static synchronized HotspotCompilationMBean getHotspotCompilationMBean()
  {
    if (hsCompileMBean == null) {
      hsCompileMBean = new HotspotCompilation(jvm);
    }
    return hsCompileMBean;
  }
  
  private static void addMBean(MBeanServer paramMBeanServer, Object paramObject, String paramString)
  {
    try
    {
      final ObjectName localObjectName = Util.newObjectName(paramString);
      MBeanServer localMBeanServer = paramMBeanServer;
      final Object localObject = paramObject;
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws MBeanRegistrationException, NotCompliantMBeanException
        {
          try
          {
            val$mbs0.registerMBean(localObject, localObjectName);
            return null;
          }
          catch (InstanceAlreadyExistsException localInstanceAlreadyExistsException) {}
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw Util.newException(localPrivilegedActionException.getException());
    }
  }
  
  public static HashMap<ObjectName, DynamicMBean> getPlatformDynamicMBeans()
  {
    HashMap localHashMap = new HashMap();
    DiagnosticCommandMBean localDiagnosticCommandMBean = getDiagnosticCommandMBean();
    if (localDiagnosticCommandMBean != null) {
      localHashMap.put(Util.newObjectName("com.sun.management:type=DiagnosticCommand"), localDiagnosticCommandMBean);
    }
    return localHashMap;
  }
  
  static void registerInternalMBeans(MBeanServer paramMBeanServer)
  {
    addMBean(paramMBeanServer, getHotspotClassLoadingMBean(), "sun.management:type=HotspotClassLoading");
    addMBean(paramMBeanServer, getHotspotMemoryMBean(), "sun.management:type=HotspotMemory");
    addMBean(paramMBeanServer, getHotspotRuntimeMBean(), "sun.management:type=HotspotRuntime");
    addMBean(paramMBeanServer, getHotspotThreadMBean(), "sun.management:type=HotspotThreading");
    if (getCompilationMXBean() != null) {
      addMBean(paramMBeanServer, getHotspotCompilationMBean(), "sun.management:type=HotspotCompilation");
    }
  }
  
  private static void unregisterMBean(MBeanServer paramMBeanServer, String paramString)
  {
    try
    {
      final ObjectName localObjectName = Util.newObjectName(paramString);
      MBeanServer localMBeanServer = paramMBeanServer;
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws MBeanRegistrationException, RuntimeOperationsException
        {
          try
          {
            val$mbs0.unregisterMBean(localObjectName);
          }
          catch (InstanceNotFoundException localInstanceNotFoundException) {}
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw Util.newException(localPrivilegedActionException.getException());
    }
  }
  
  static void unregisterInternalMBeans(MBeanServer paramMBeanServer)
  {
    unregisterMBean(paramMBeanServer, "sun.management:type=HotspotClassLoading");
    unregisterMBean(paramMBeanServer, "sun.management:type=HotspotMemory");
    unregisterMBean(paramMBeanServer, "sun.management:type=HotspotRuntime");
    unregisterMBean(paramMBeanServer, "sun.management:type=HotspotThreading");
    if (getCompilationMXBean() != null) {
      unregisterMBean(paramMBeanServer, "sun.management:type=HotspotCompilation");
    }
  }
  
  public static boolean isThreadSuspended(int paramInt)
  {
    return (paramInt & 0x100000) != 0;
  }
  
  public static boolean isThreadRunningNative(int paramInt)
  {
    return (paramInt & 0x400000) != 0;
  }
  
  public static Thread.State toThreadState(int paramInt)
  {
    int i = paramInt & 0xFFFFF;
    return VM.toThreadState(i);
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("management");
        return null;
      }
    });
  }
  
  public static abstract interface LoggingMXBean
    extends PlatformLoggingMXBean, LoggingMXBean
  {}
  
  static class PlatformLoggingImpl
    implements ManagementFactoryHelper.LoggingMXBean
  {
    static final PlatformLoggingMXBean instance = new PlatformLoggingImpl();
    static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";
    private volatile ObjectName objname;
    
    PlatformLoggingImpl() {}
    
    public ObjectName getObjectName()
    {
      ObjectName localObjectName = objname;
      if (localObjectName == null) {
        synchronized (this)
        {
          localObjectName = objname;
          if (localObjectName == null)
          {
            localObjectName = Util.newObjectName("java.util.logging:type=Logging");
            objname = localObjectName;
          }
        }
      }
      return localObjectName;
    }
    
    public List<String> getLoggerNames()
    {
      return LoggingSupport.getLoggerNames();
    }
    
    public String getLoggerLevel(String paramString)
    {
      return LoggingSupport.getLoggerLevel(paramString);
    }
    
    public void setLoggerLevel(String paramString1, String paramString2)
    {
      LoggingSupport.setLoggerLevel(paramString1, paramString2);
    }
    
    public String getParentLoggerName(String paramString)
    {
      return LoggingSupport.getParentLoggerName(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\ManagementFactoryHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */