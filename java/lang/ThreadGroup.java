package java.lang;

import java.io.PrintStream;
import java.util.Arrays;
import sun.misc.VM;

public class ThreadGroup
  implements Thread.UncaughtExceptionHandler
{
  private final ThreadGroup parent;
  String name;
  int maxPriority;
  boolean destroyed;
  boolean daemon;
  boolean vmAllowSuspension;
  int nUnstartedThreads = 0;
  int nthreads;
  Thread[] threads;
  int ngroups;
  ThreadGroup[] groups;
  
  private ThreadGroup()
  {
    name = "system";
    maxPriority = 10;
    parent = null;
  }
  
  public ThreadGroup(String paramString)
  {
    this(Thread.currentThread().getThreadGroup(), paramString);
  }
  
  public ThreadGroup(ThreadGroup paramThreadGroup, String paramString)
  {
    this(checkParentAccess(paramThreadGroup), paramThreadGroup, paramString);
  }
  
  private ThreadGroup(Void paramVoid, ThreadGroup paramThreadGroup, String paramString)
  {
    name = paramString;
    maxPriority = maxPriority;
    daemon = daemon;
    vmAllowSuspension = vmAllowSuspension;
    parent = paramThreadGroup;
    paramThreadGroup.add(this);
  }
  
  private static Void checkParentAccess(ThreadGroup paramThreadGroup)
  {
    paramThreadGroup.checkAccess();
    return null;
  }
  
  public final String getName()
  {
    return name;
  }
  
  public final ThreadGroup getParent()
  {
    if (parent != null) {
      parent.checkAccess();
    }
    return parent;
  }
  
  public final int getMaxPriority()
  {
    return maxPriority;
  }
  
  public final boolean isDaemon()
  {
    return daemon;
  }
  
  public synchronized boolean isDestroyed()
  {
    return destroyed;
  }
  
  public final void setDaemon(boolean paramBoolean)
  {
    checkAccess();
    daemon = paramBoolean;
  }
  
  public final void setMaxPriority(int paramInt)
  {
    Object localObject1;
    ThreadGroup[] arrayOfThreadGroup;
    synchronized (this)
    {
      checkAccess();
      if ((paramInt < 1) || (paramInt > 10)) {
        return;
      }
      maxPriority = (parent != null ? Math.min(paramInt, parent.maxPriority) : paramInt);
      localObject1 = ngroups;
      if (groups != null) {
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
      } else {
        arrayOfThreadGroup = null;
      }
    }
    for (??? = 0; ??? < localObject1; ???++) {
      arrayOfThreadGroup[???].setMaxPriority(paramInt);
    }
  }
  
  public final boolean parentOf(ThreadGroup paramThreadGroup)
  {
    while (paramThreadGroup != null)
    {
      if (paramThreadGroup == this) {
        return true;
      }
      paramThreadGroup = parent;
    }
    return false;
  }
  
  public final void checkAccess()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkAccess(this);
    }
  }
  
  public int activeCount()
  {
    int i;
    Object localObject1;
    ThreadGroup[] arrayOfThreadGroup;
    synchronized (this)
    {
      if (destroyed) {
        return 0;
      }
      i = nthreads;
      localObject1 = ngroups;
      if (groups != null) {
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
      } else {
        arrayOfThreadGroup = null;
      }
    }
    for (??? = 0; ??? < localObject1; ???++) {
      i += arrayOfThreadGroup[???].activeCount();
    }
    return i;
  }
  
  public int enumerate(Thread[] paramArrayOfThread)
  {
    checkAccess();
    return enumerate(paramArrayOfThread, 0, true);
  }
  
  public int enumerate(Thread[] paramArrayOfThread, boolean paramBoolean)
  {
    checkAccess();
    return enumerate(paramArrayOfThread, 0, paramBoolean);
  }
  
  private int enumerate(Thread[] paramArrayOfThread, int paramInt, boolean paramBoolean)
  {
    Object localObject1 = 0;
    ThreadGroup[] arrayOfThreadGroup = null;
    synchronized (this)
    {
      if (destroyed) {
        return 0;
      }
      int i = nthreads;
      if (i > paramArrayOfThread.length - paramInt) {
        i = paramArrayOfThread.length - paramInt;
      }
      for (int j = 0; j < i; j++) {
        if (threads[j].isAlive()) {
          paramArrayOfThread[(paramInt++)] = threads[j];
        }
      }
      if (paramBoolean)
      {
        localObject1 = ngroups;
        if (groups != null) {
          arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
        } else {
          arrayOfThreadGroup = null;
        }
      }
    }
    if (paramBoolean) {
      for (??? = 0; ??? < localObject1; ???++) {
        paramInt = arrayOfThreadGroup[???].enumerate(paramArrayOfThread, paramInt, true);
      }
    }
    return paramInt;
  }
  
  public int activeGroupCount()
  {
    Object localObject1;
    ThreadGroup[] arrayOfThreadGroup;
    synchronized (this)
    {
      if (destroyed) {
        return 0;
      }
      localObject1 = ngroups;
      if (groups != null) {
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
      } else {
        arrayOfThreadGroup = null;
      }
    }
    ??? = localObject1;
    int i;
    for (int j = 0; j < localObject1; j++) {
      ??? += arrayOfThreadGroup[j].activeGroupCount();
    }
    return i;
  }
  
  public int enumerate(ThreadGroup[] paramArrayOfThreadGroup)
  {
    checkAccess();
    return enumerate(paramArrayOfThreadGroup, 0, true);
  }
  
  public int enumerate(ThreadGroup[] paramArrayOfThreadGroup, boolean paramBoolean)
  {
    checkAccess();
    return enumerate(paramArrayOfThreadGroup, 0, paramBoolean);
  }
  
  private int enumerate(ThreadGroup[] paramArrayOfThreadGroup, int paramInt, boolean paramBoolean)
  {
    Object localObject1 = 0;
    ThreadGroup[] arrayOfThreadGroup = null;
    synchronized (this)
    {
      if (destroyed) {
        return 0;
      }
      int i = ngroups;
      if (i > paramArrayOfThreadGroup.length - paramInt) {
        i = paramArrayOfThreadGroup.length - paramInt;
      }
      if (i > 0)
      {
        System.arraycopy(groups, 0, paramArrayOfThreadGroup, paramInt, i);
        paramInt += i;
      }
      if (paramBoolean)
      {
        localObject1 = ngroups;
        if (groups != null) {
          arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
        } else {
          arrayOfThreadGroup = null;
        }
      }
    }
    if (paramBoolean) {
      for (??? = 0; ??? < localObject1; ???++) {
        paramInt = arrayOfThreadGroup[???].enumerate(paramArrayOfThreadGroup, paramInt, true);
      }
    }
    return paramInt;
  }
  
  @Deprecated
  public final void stop()
  {
    if (stopOrSuspend(false)) {
      Thread.currentThread().stop();
    }
  }
  
  public final void interrupt()
  {
    Object localObject1;
    ThreadGroup[] arrayOfThreadGroup;
    synchronized (this)
    {
      checkAccess();
      for (int i = 0; i < nthreads; i++) {
        threads[i].interrupt();
      }
      localObject1 = ngroups;
      if (groups != null) {
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
      } else {
        arrayOfThreadGroup = null;
      }
    }
    for (??? = 0; ??? < localObject1; ???++) {
      arrayOfThreadGroup[???].interrupt();
    }
  }
  
  @Deprecated
  public final void suspend()
  {
    if (stopOrSuspend(true)) {
      Thread.currentThread().suspend();
    }
  }
  
  private boolean stopOrSuspend(boolean paramBoolean)
  {
    boolean bool = false;
    Thread localThread = Thread.currentThread();
    ThreadGroup[] arrayOfThreadGroup = null;
    Object localObject1;
    synchronized (this)
    {
      checkAccess();
      for (int i = 0; i < nthreads; i++) {
        if (threads[i] == localThread) {
          bool = true;
        } else if (paramBoolean) {
          threads[i].suspend();
        } else {
          threads[i].stop();
        }
      }
      localObject1 = ngroups;
      if (groups != null) {
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
      }
    }
    for (??? = 0; ??? < localObject1; ???++) {
      bool = (arrayOfThreadGroup[???].stopOrSuspend(paramBoolean)) || (bool);
    }
    return bool;
  }
  
  @Deprecated
  public final void resume()
  {
    Object localObject1;
    ThreadGroup[] arrayOfThreadGroup;
    synchronized (this)
    {
      checkAccess();
      for (int i = 0; i < nthreads; i++) {
        threads[i].resume();
      }
      localObject1 = ngroups;
      if (groups != null) {
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
      } else {
        arrayOfThreadGroup = null;
      }
    }
    for (??? = 0; ??? < localObject1; ???++) {
      arrayOfThreadGroup[???].resume();
    }
  }
  
  public final void destroy()
  {
    Object localObject1;
    ThreadGroup[] arrayOfThreadGroup;
    synchronized (this)
    {
      checkAccess();
      if ((destroyed) || (nthreads > 0)) {
        throw new IllegalThreadStateException();
      }
      localObject1 = ngroups;
      if (groups != null) {
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
      } else {
        arrayOfThreadGroup = null;
      }
      if (parent != null)
      {
        destroyed = true;
        ngroups = 0;
        groups = null;
        nthreads = 0;
        threads = null;
      }
    }
    for (??? = 0; ??? < localObject1; ???++) {
      arrayOfThreadGroup[???].destroy();
    }
    if (parent != null) {
      parent.remove(this);
    }
  }
  
  private final void add(ThreadGroup paramThreadGroup)
  {
    synchronized (this)
    {
      if (destroyed) {
        throw new IllegalThreadStateException();
      }
      if (groups == null) {
        groups = new ThreadGroup[4];
      } else if (ngroups == groups.length) {
        groups = ((ThreadGroup[])Arrays.copyOf(groups, ngroups * 2));
      }
      groups[ngroups] = paramThreadGroup;
      ngroups += 1;
    }
  }
  
  private void remove(ThreadGroup paramThreadGroup)
  {
    synchronized (this)
    {
      if (destroyed) {
        return;
      }
      for (int i = 0; i < ngroups; i++) {
        if (groups[i] == paramThreadGroup)
        {
          ngroups -= 1;
          System.arraycopy(groups, i + 1, groups, i, ngroups - i);
          groups[ngroups] = null;
          break;
        }
      }
      if (nthreads == 0) {
        notifyAll();
      }
      if ((daemon) && (nthreads == 0) && (nUnstartedThreads == 0) && (ngroups == 0)) {
        destroy();
      }
    }
  }
  
  void addUnstarted()
  {
    synchronized (this)
    {
      if (destroyed) {
        throw new IllegalThreadStateException();
      }
      nUnstartedThreads += 1;
    }
  }
  
  void add(Thread paramThread)
  {
    synchronized (this)
    {
      if (destroyed) {
        throw new IllegalThreadStateException();
      }
      if (threads == null) {
        threads = new Thread[4];
      } else if (nthreads == threads.length) {
        threads = ((Thread[])Arrays.copyOf(threads, nthreads * 2));
      }
      threads[nthreads] = paramThread;
      nthreads += 1;
      nUnstartedThreads -= 1;
    }
  }
  
  void threadStartFailed(Thread paramThread)
  {
    synchronized (this)
    {
      remove(paramThread);
      nUnstartedThreads += 1;
    }
  }
  
  void threadTerminated(Thread paramThread)
  {
    synchronized (this)
    {
      remove(paramThread);
      if (nthreads == 0) {
        notifyAll();
      }
      if ((daemon) && (nthreads == 0) && (nUnstartedThreads == 0) && (ngroups == 0)) {
        destroy();
      }
    }
  }
  
  private void remove(Thread paramThread)
  {
    synchronized (this)
    {
      if (destroyed) {
        return;
      }
      for (int i = 0; i < nthreads; i++) {
        if (threads[i] == paramThread)
        {
          System.arraycopy(threads, i + 1, threads, i, --nthreads - i);
          threads[nthreads] = null;
          break;
        }
      }
    }
  }
  
  public void list()
  {
    list(System.out, 0);
  }
  
  void list(PrintStream paramPrintStream, int paramInt)
  {
    Object localObject1;
    ThreadGroup[] arrayOfThreadGroup;
    synchronized (this)
    {
      for (int i = 0; i < paramInt; i++) {
        paramPrintStream.print(" ");
      }
      paramPrintStream.println(this);
      paramInt += 4;
      for (i = 0; i < nthreads; i++)
      {
        for (int j = 0; j < paramInt; j++) {
          paramPrintStream.print(" ");
        }
        paramPrintStream.println(threads[i]);
      }
      localObject1 = ngroups;
      if (groups != null) {
        arrayOfThreadGroup = (ThreadGroup[])Arrays.copyOf(groups, localObject1);
      } else {
        arrayOfThreadGroup = null;
      }
    }
    for (??? = 0; ??? < localObject1; ???++) {
      arrayOfThreadGroup[???].list(paramPrintStream, paramInt);
    }
  }
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    if (parent != null)
    {
      parent.uncaughtException(paramThread, paramThrowable);
    }
    else
    {
      Thread.UncaughtExceptionHandler localUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
      if (localUncaughtExceptionHandler != null)
      {
        localUncaughtExceptionHandler.uncaughtException(paramThread, paramThrowable);
      }
      else if (!(paramThrowable instanceof ThreadDeath))
      {
        System.err.print("Exception in thread \"" + paramThread.getName() + "\" ");
        paramThrowable.printStackTrace(System.err);
      }
    }
  }
  
  @Deprecated
  public boolean allowThreadSuspension(boolean paramBoolean)
  {
    vmAllowSuspension = paramBoolean;
    if (!paramBoolean) {
      VM.unsuspendSomeThreads();
    }
    return true;
  }
  
  public String toString()
  {
    return getClass().getName() + "[name=" + getName() + ",maxpri=" + maxPriority + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ThreadGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */