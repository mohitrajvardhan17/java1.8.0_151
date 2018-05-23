package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.util.SecurityConstants;

public class Executors
{
  public static ExecutorService newFixedThreadPool(int paramInt)
  {
    return new ThreadPoolExecutor(paramInt, paramInt, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
  }
  
  public static ExecutorService newWorkStealingPool(int paramInt)
  {
    return new ForkJoinPool(paramInt, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
  }
  
  public static ExecutorService newWorkStealingPool()
  {
    return new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
  }
  
  public static ExecutorService newFixedThreadPool(int paramInt, ThreadFactory paramThreadFactory)
  {
    return new ThreadPoolExecutor(paramInt, paramInt, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), paramThreadFactory);
  }
  
  public static ExecutorService newSingleThreadExecutor()
  {
    return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue()));
  }
  
  public static ExecutorService newSingleThreadExecutor(ThreadFactory paramThreadFactory)
  {
    return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), paramThreadFactory));
  }
  
  public static ExecutorService newCachedThreadPool()
  {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue());
  }
  
  public static ExecutorService newCachedThreadPool(ThreadFactory paramThreadFactory)
  {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), paramThreadFactory);
  }
  
  public static ScheduledExecutorService newSingleThreadScheduledExecutor()
  {
    return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1));
  }
  
  public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory paramThreadFactory)
  {
    return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1, paramThreadFactory));
  }
  
  public static ScheduledExecutorService newScheduledThreadPool(int paramInt)
  {
    return new ScheduledThreadPoolExecutor(paramInt);
  }
  
  public static ScheduledExecutorService newScheduledThreadPool(int paramInt, ThreadFactory paramThreadFactory)
  {
    return new ScheduledThreadPoolExecutor(paramInt, paramThreadFactory);
  }
  
  public static ExecutorService unconfigurableExecutorService(ExecutorService paramExecutorService)
  {
    if (paramExecutorService == null) {
      throw new NullPointerException();
    }
    return new DelegatedExecutorService(paramExecutorService);
  }
  
  public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService paramScheduledExecutorService)
  {
    if (paramScheduledExecutorService == null) {
      throw new NullPointerException();
    }
    return new DelegatedScheduledExecutorService(paramScheduledExecutorService);
  }
  
  public static ThreadFactory defaultThreadFactory()
  {
    return new DefaultThreadFactory();
  }
  
  public static ThreadFactory privilegedThreadFactory()
  {
    return new PrivilegedThreadFactory();
  }
  
  public static <T> Callable<T> callable(Runnable paramRunnable, T paramT)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    return new RunnableAdapter(paramRunnable, paramT);
  }
  
  public static Callable<Object> callable(Runnable paramRunnable)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    return new RunnableAdapter(paramRunnable, null);
  }
  
  public static Callable<Object> callable(PrivilegedAction<?> paramPrivilegedAction)
  {
    if (paramPrivilegedAction == null) {
      throw new NullPointerException();
    }
    new Callable()
    {
      public Object call()
      {
        return val$action.run();
      }
    };
  }
  
  public static Callable<Object> callable(PrivilegedExceptionAction<?> paramPrivilegedExceptionAction)
  {
    if (paramPrivilegedExceptionAction == null) {
      throw new NullPointerException();
    }
    new Callable()
    {
      public Object call()
        throws Exception
      {
        return val$action.run();
      }
    };
  }
  
  public static <T> Callable<T> privilegedCallable(Callable<T> paramCallable)
  {
    if (paramCallable == null) {
      throw new NullPointerException();
    }
    return new PrivilegedCallable(paramCallable);
  }
  
  public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> paramCallable)
  {
    if (paramCallable == null) {
      throw new NullPointerException();
    }
    return new PrivilegedCallableUsingCurrentClassLoader(paramCallable);
  }
  
  private Executors() {}
  
  static class DefaultThreadFactory
    implements ThreadFactory
  {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    
    DefaultThreadFactory()
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      group = (localSecurityManager != null ? localSecurityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
      namePrefix = ("pool-" + poolNumber.getAndIncrement() + "-thread-");
    }
    
    public Thread newThread(Runnable paramRunnable)
    {
      Thread localThread = new Thread(group, paramRunnable, namePrefix + threadNumber.getAndIncrement(), 0L);
      if (localThread.isDaemon()) {
        localThread.setDaemon(false);
      }
      if (localThread.getPriority() != 5) {
        localThread.setPriority(5);
      }
      return localThread;
    }
  }
  
  static class DelegatedExecutorService
    extends AbstractExecutorService
  {
    private final ExecutorService e;
    
    DelegatedExecutorService(ExecutorService paramExecutorService)
    {
      e = paramExecutorService;
    }
    
    public void execute(Runnable paramRunnable)
    {
      e.execute(paramRunnable);
    }
    
    public void shutdown()
    {
      e.shutdown();
    }
    
    public List<Runnable> shutdownNow()
    {
      return e.shutdownNow();
    }
    
    public boolean isShutdown()
    {
      return e.isShutdown();
    }
    
    public boolean isTerminated()
    {
      return e.isTerminated();
    }
    
    public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return e.awaitTermination(paramLong, paramTimeUnit);
    }
    
    public Future<?> submit(Runnable paramRunnable)
    {
      return e.submit(paramRunnable);
    }
    
    public <T> Future<T> submit(Callable<T> paramCallable)
    {
      return e.submit(paramCallable);
    }
    
    public <T> Future<T> submit(Runnable paramRunnable, T paramT)
    {
      return e.submit(paramRunnable, paramT);
    }
    
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection)
      throws InterruptedException
    {
      return e.invokeAll(paramCollection);
    }
    
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return e.invokeAll(paramCollection, paramLong, paramTimeUnit);
    }
    
    public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection)
      throws InterruptedException, ExecutionException
    {
      return (T)e.invokeAny(paramCollection);
    }
    
    public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException, ExecutionException, TimeoutException
    {
      return (T)e.invokeAny(paramCollection, paramLong, paramTimeUnit);
    }
  }
  
  static class DelegatedScheduledExecutorService
    extends Executors.DelegatedExecutorService
    implements ScheduledExecutorService
  {
    private final ScheduledExecutorService e;
    
    DelegatedScheduledExecutorService(ScheduledExecutorService paramScheduledExecutorService)
    {
      super();
      e = paramScheduledExecutorService;
    }
    
    public ScheduledFuture<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit)
    {
      return e.schedule(paramRunnable, paramLong, paramTimeUnit);
    }
    
    public <V> ScheduledFuture<V> schedule(Callable<V> paramCallable, long paramLong, TimeUnit paramTimeUnit)
    {
      return e.schedule(paramCallable, paramLong, paramTimeUnit);
    }
    
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
    {
      return e.scheduleAtFixedRate(paramRunnable, paramLong1, paramLong2, paramTimeUnit);
    }
    
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
    {
      return e.scheduleWithFixedDelay(paramRunnable, paramLong1, paramLong2, paramTimeUnit);
    }
  }
  
  static class FinalizableDelegatedExecutorService
    extends Executors.DelegatedExecutorService
  {
    FinalizableDelegatedExecutorService(ExecutorService paramExecutorService)
    {
      super();
    }
    
    protected void finalize()
    {
      super.shutdown();
    }
  }
  
  static final class PrivilegedCallable<T>
    implements Callable<T>
  {
    private final Callable<T> task;
    private final AccessControlContext acc;
    
    PrivilegedCallable(Callable<T> paramCallable)
    {
      task = paramCallable;
      acc = AccessController.getContext();
    }
    
    public T call()
      throws Exception
    {
      try
      {
        (T)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public T run()
            throws Exception
          {
            return (T)task.call();
          }
        }, acc);
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw localPrivilegedActionException.getException();
      }
    }
  }
  
  static final class PrivilegedCallableUsingCurrentClassLoader<T>
    implements Callable<T>
  {
    private final Callable<T> task;
    private final AccessControlContext acc;
    private final ClassLoader ccl;
    
    PrivilegedCallableUsingCurrentClassLoader(Callable<T> paramCallable)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
        localSecurityManager.checkPermission(new RuntimePermission("setContextClassLoader"));
      }
      task = paramCallable;
      acc = AccessController.getContext();
      ccl = Thread.currentThread().getContextClassLoader();
    }
    
    public T call()
      throws Exception
    {
      try
      {
        (T)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public T run()
            throws Exception
          {
            Thread localThread = Thread.currentThread();
            ClassLoader localClassLoader = localThread.getContextClassLoader();
            if (ccl == localClassLoader) {
              return (T)task.call();
            }
            localThread.setContextClassLoader(ccl);
            try
            {
              Object localObject1 = task.call();
              return (T)localObject1;
            }
            finally
            {
              localThread.setContextClassLoader(localClassLoader);
            }
          }
        }, acc);
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw localPrivilegedActionException.getException();
      }
    }
  }
  
  static class PrivilegedThreadFactory
    extends Executors.DefaultThreadFactory
  {
    private final AccessControlContext acc;
    private final ClassLoader ccl;
    
    PrivilegedThreadFactory()
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        localSecurityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
        localSecurityManager.checkPermission(new RuntimePermission("setContextClassLoader"));
      }
      acc = AccessController.getContext();
      ccl = Thread.currentThread().getContextClassLoader();
    }
    
    public Thread newThread(final Runnable paramRunnable)
    {
      super.newThread(new Runnable()
      {
        public void run()
        {
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Void run()
            {
              Thread.currentThread().setContextClassLoader(ccl);
              val$r.run();
              return null;
            }
          }, acc);
        }
      });
    }
  }
  
  static final class RunnableAdapter<T>
    implements Callable<T>
  {
    final Runnable task;
    final T result;
    
    RunnableAdapter(Runnable paramRunnable, T paramT)
    {
      task = paramRunnable;
      result = paramT;
    }
    
    public T call()
    {
      task.run();
      return (T)result;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\Executors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */