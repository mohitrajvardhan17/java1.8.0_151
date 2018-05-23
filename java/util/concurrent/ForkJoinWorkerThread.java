package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.ProtectionDomain;
import sun.misc.Unsafe;

public class ForkJoinWorkerThread
  extends Thread
{
  final ForkJoinPool pool;
  final ForkJoinPool.WorkQueue workQueue;
  private static final Unsafe U;
  private static final long THREADLOCALS;
  private static final long INHERITABLETHREADLOCALS;
  private static final long INHERITEDACCESSCONTROLCONTEXT;
  
  protected ForkJoinWorkerThread(ForkJoinPool paramForkJoinPool)
  {
    super("aForkJoinWorkerThread");
    pool = paramForkJoinPool;
    workQueue = paramForkJoinPool.registerWorker(this);
  }
  
  ForkJoinWorkerThread(ForkJoinPool paramForkJoinPool, ThreadGroup paramThreadGroup, AccessControlContext paramAccessControlContext)
  {
    super(paramThreadGroup, null, "aForkJoinWorkerThread");
    U.putOrderedObject(this, INHERITEDACCESSCONTROLCONTEXT, paramAccessControlContext);
    eraseThreadLocals();
    pool = paramForkJoinPool;
    workQueue = paramForkJoinPool.registerWorker(this);
  }
  
  public ForkJoinPool getPool()
  {
    return pool;
  }
  
  public int getPoolIndex()
  {
    return workQueue.getPoolIndex();
  }
  
  protected void onStart() {}
  
  protected void onTermination(Throwable paramThrowable) {}
  
  public void run()
  {
    if (workQueue.array == null)
    {
      Object localObject1 = null;
      try
      {
        onStart();
        pool.runWorker(workQueue);
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
      }
      finally
      {
        try
        {
          onTermination((Throwable)localObject1);
        }
        catch (Throwable localThrowable4)
        {
          if (localObject1 == null) {
            localObject1 = localThrowable4;
          }
        }
        finally
        {
          pool.deregisterWorker(this, (Throwable)localObject1);
        }
      }
    }
  }
  
  final void eraseThreadLocals()
  {
    U.putObject(this, THREADLOCALS, null);
    U.putObject(this, INHERITABLETHREADLOCALS, null);
  }
  
  void afterTopLevelExec() {}
  
  static
  {
    try
    {
      U = Unsafe.getUnsafe();
      Class localClass = Thread.class;
      THREADLOCALS = U.objectFieldOffset(localClass.getDeclaredField("threadLocals"));
      INHERITABLETHREADLOCALS = U.objectFieldOffset(localClass.getDeclaredField("inheritableThreadLocals"));
      INHERITEDACCESSCONTROLCONTEXT = U.objectFieldOffset(localClass.getDeclaredField("inheritedAccessControlContext"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class InnocuousForkJoinWorkerThread
    extends ForkJoinWorkerThread
  {
    private static final ThreadGroup innocuousThreadGroup = ;
    private static final AccessControlContext INNOCUOUS_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
    
    InnocuousForkJoinWorkerThread(ForkJoinPool paramForkJoinPool)
    {
      super(innocuousThreadGroup, INNOCUOUS_ACC);
    }
    
    void afterTopLevelExec()
    {
      eraseThreadLocals();
    }
    
    public ClassLoader getContextClassLoader()
    {
      return ClassLoader.getSystemClassLoader();
    }
    
    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler) {}
    
    public void setContextClassLoader(ClassLoader paramClassLoader)
    {
      throw new SecurityException("setContextClassLoader");
    }
    
    private static ThreadGroup createThreadGroup()
    {
      try
      {
        Unsafe localUnsafe = Unsafe.getUnsafe();
        Class localClass1 = Thread.class;
        Class localClass2 = ThreadGroup.class;
        long l1 = localUnsafe.objectFieldOffset(localClass1.getDeclaredField("group"));
        long l2 = localUnsafe.objectFieldOffset(localClass2.getDeclaredField("parent"));
        ThreadGroup localThreadGroup;
        for (Object localObject = (ThreadGroup)localUnsafe.getObject(Thread.currentThread(), l1); localObject != null; localObject = localThreadGroup)
        {
          localThreadGroup = (ThreadGroup)localUnsafe.getObject(localObject, l2);
          if (localThreadGroup == null) {
            return new ThreadGroup((ThreadGroup)localObject, "InnocuousForkJoinWorkerThreadGroup");
          }
        }
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
      throw new Error("Cannot create ThreadGroup");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ForkJoinWorkerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */