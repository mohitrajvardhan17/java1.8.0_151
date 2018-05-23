package sun.misc;

import java.util.Properties;

public class VM
{
  private static boolean suspended = false;
  @Deprecated
  public static final int STATE_GREEN = 1;
  @Deprecated
  public static final int STATE_YELLOW = 2;
  @Deprecated
  public static final int STATE_RED = 3;
  private static volatile boolean booted = false;
  private static final Object lock = new Object();
  private static long directMemory = 67108864L;
  private static boolean pageAlignDirectMemory;
  private static boolean defaultAllowArraySyntax = false;
  private static boolean allowArraySyntax = defaultAllowArraySyntax;
  private static final Properties savedProps = new Properties();
  private static volatile int finalRefCount = 0;
  private static volatile int peakFinalRefCount = 0;
  private static final int JVMTI_THREAD_STATE_ALIVE = 1;
  private static final int JVMTI_THREAD_STATE_TERMINATED = 2;
  private static final int JVMTI_THREAD_STATE_RUNNABLE = 4;
  private static final int JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER = 1024;
  private static final int JVMTI_THREAD_STATE_WAITING_INDEFINITELY = 16;
  private static final int JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT = 32;
  
  public VM() {}
  
  @Deprecated
  public static boolean threadsSuspended()
  {
    return suspended;
  }
  
  public static boolean allowThreadSuspension(ThreadGroup paramThreadGroup, boolean paramBoolean)
  {
    return paramThreadGroup.allowThreadSuspension(paramBoolean);
  }
  
  @Deprecated
  public static boolean suspendThreads()
  {
    suspended = true;
    return true;
  }
  
  @Deprecated
  public static void unsuspendThreads()
  {
    suspended = false;
  }
  
  @Deprecated
  public static void unsuspendSomeThreads() {}
  
  @Deprecated
  public static final int getState()
  {
    return 1;
  }
  
  @Deprecated
  public static void registerVMNotification(VMNotification paramVMNotification) {}
  
  @Deprecated
  public static void asChange(int paramInt1, int paramInt2) {}
  
  @Deprecated
  public static void asChange_otherthread(int paramInt1, int paramInt2) {}
  
  public static void booted()
  {
    synchronized (lock)
    {
      booted = true;
      lock.notifyAll();
    }
  }
  
  public static boolean isBooted()
  {
    return booted;
  }
  
  public static void awaitBooted()
    throws InterruptedException
  {
    synchronized (lock)
    {
      while (!booted) {
        lock.wait();
      }
    }
  }
  
  public static long maxDirectMemory()
  {
    return directMemory;
  }
  
  public static boolean isDirectMemoryPageAligned()
  {
    return pageAlignDirectMemory;
  }
  
  public static boolean allowArraySyntax()
  {
    return allowArraySyntax;
  }
  
  public static boolean isSystemDomainLoader(ClassLoader paramClassLoader)
  {
    return paramClassLoader == null;
  }
  
  public static String getSavedProperty(String paramString)
  {
    if (savedProps.isEmpty()) {
      throw new IllegalStateException("Should be non-empty if initialized");
    }
    return savedProps.getProperty(paramString);
  }
  
  public static void saveAndRemoveProperties(Properties paramProperties)
  {
    if (booted) {
      throw new IllegalStateException("System initialization has completed");
    }
    savedProps.putAll(paramProperties);
    String str = (String)paramProperties.remove("sun.nio.MaxDirectMemorySize");
    if (str != null) {
      if (str.equals("-1"))
      {
        directMemory = Runtime.getRuntime().maxMemory();
      }
      else
      {
        long l = Long.parseLong(str);
        if (l > -1L) {
          directMemory = l;
        }
      }
    }
    str = (String)paramProperties.remove("sun.nio.PageAlignDirectMemory");
    if ("true".equals(str)) {
      pageAlignDirectMemory = true;
    }
    str = paramProperties.getProperty("sun.lang.ClassLoader.allowArraySyntax");
    allowArraySyntax = str == null ? defaultAllowArraySyntax : Boolean.parseBoolean(str);
    paramProperties.remove("java.lang.Integer.IntegerCache.high");
    paramProperties.remove("sun.zip.disableMemoryMapping");
    paramProperties.remove("sun.java.launcher.diag");
    paramProperties.remove("sun.cds.enableSharedLookupCache");
  }
  
  public static void initializeOSEnvironment()
  {
    if (!booted) {
      OSEnvironment.initialize();
    }
  }
  
  public static int getFinalRefCount()
  {
    return finalRefCount;
  }
  
  public static int getPeakFinalRefCount()
  {
    return peakFinalRefCount;
  }
  
  public static void addFinalRefCount(int paramInt)
  {
    finalRefCount += paramInt;
    if (finalRefCount > peakFinalRefCount) {
      peakFinalRefCount = finalRefCount;
    }
  }
  
  public static Thread.State toThreadState(int paramInt)
  {
    if ((paramInt & 0x4) != 0) {
      return Thread.State.RUNNABLE;
    }
    if ((paramInt & 0x400) != 0) {
      return Thread.State.BLOCKED;
    }
    if ((paramInt & 0x10) != 0) {
      return Thread.State.WAITING;
    }
    if ((paramInt & 0x20) != 0) {
      return Thread.State.TIMED_WAITING;
    }
    if ((paramInt & 0x2) != 0) {
      return Thread.State.TERMINATED;
    }
    if ((paramInt & 0x1) == 0) {
      return Thread.State.NEW;
    }
    return Thread.State.RUNNABLE;
  }
  
  public static native ClassLoader latestUserDefinedLoader();
  
  private static native void initialize();
  
  static
  {
    initialize();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\VM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */