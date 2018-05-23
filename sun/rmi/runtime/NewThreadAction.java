package sun.rmi.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.util.SecurityConstants;

public final class NewThreadAction
  implements PrivilegedAction<Thread>
{
  static final ThreadGroup systemThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ThreadGroup run()
    {
      ThreadGroup localThreadGroup;
      for (Object localObject = Thread.currentThread().getThreadGroup(); (localThreadGroup = ((ThreadGroup)localObject).getParent()) != null; localObject = localThreadGroup) {}
      return (ThreadGroup)localObject;
    }
  });
  static final ThreadGroup userThreadGroup = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ThreadGroup run()
    {
      return new ThreadGroup(NewThreadAction.systemThreadGroup, "RMI Runtime");
    }
  });
  private final ThreadGroup group;
  private final Runnable runnable;
  private final String name;
  private final boolean daemon;
  
  NewThreadAction(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, boolean paramBoolean)
  {
    group = paramThreadGroup;
    runnable = paramRunnable;
    name = paramString;
    daemon = paramBoolean;
  }
  
  public NewThreadAction(Runnable paramRunnable, String paramString, boolean paramBoolean)
  {
    this(systemThreadGroup, paramRunnable, paramString, paramBoolean);
  }
  
  public NewThreadAction(Runnable paramRunnable, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramBoolean2 ? userThreadGroup : systemThreadGroup, paramRunnable, paramString, paramBoolean1);
  }
  
  public Thread run()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
    }
    Thread localThread = new Thread(group, runnable, "RMI " + name);
    localThread.setContextClassLoader(ClassLoader.getSystemClassLoader());
    localThread.setDaemon(daemon);
    return localThread;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\runtime\NewThreadAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */