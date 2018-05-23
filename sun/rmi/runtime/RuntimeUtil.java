package sun.rmi.runtime;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import sun.security.action.GetIntegerAction;

public final class RuntimeUtil
{
  private static final Log runtimeLog = Log.getLog("sun.rmi.runtime", null, false);
  private static final int schedulerThreads = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.runtime.schedulerThreads", 1))).intValue();
  private static final Permission GET_INSTANCE_PERMISSION = new RuntimePermission("sun.rmi.runtime.RuntimeUtil.getInstance");
  private static final RuntimeUtil instance = new RuntimeUtil();
  private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(schedulerThreads, new ThreadFactory()
  {
    private final AtomicInteger count = new AtomicInteger(0);
    
    public Thread newThread(Runnable paramAnonymousRunnable)
    {
      try
      {
        return (Thread)AccessController.doPrivileged(new NewThreadAction(paramAnonymousRunnable, "Scheduler(" + count.getAndIncrement() + ")", true));
      }
      catch (Throwable localThrowable)
      {
        RuntimeUtil.runtimeLog.log(Level.WARNING, "scheduler thread factory throws", localThrowable);
      }
      return null;
    }
  });
  
  private RuntimeUtil() {}
  
  private static RuntimeUtil getInstance()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(GET_INSTANCE_PERMISSION);
    }
    return instance;
  }
  
  public ScheduledThreadPoolExecutor getScheduler()
  {
    return scheduler;
  }
  
  public static class GetInstanceAction
    implements PrivilegedAction<RuntimeUtil>
  {
    public GetInstanceAction() {}
    
    public RuntimeUtil run()
    {
      return RuntimeUtil.access$100();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\runtime\RuntimeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */