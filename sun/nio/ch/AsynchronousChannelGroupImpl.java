package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.Channel;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.action.GetIntegerAction;

abstract class AsynchronousChannelGroupImpl
  extends AsynchronousChannelGroup
  implements Executor
{
  private static final int internalThreadCount = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.nio.ch.internalThreadPoolSize", 1))).intValue();
  private final ThreadPool pool;
  private final AtomicInteger threadCount = new AtomicInteger();
  private ScheduledThreadPoolExecutor timeoutExecutor;
  private final Queue<Runnable> taskQueue;
  private final AtomicBoolean shutdown = new AtomicBoolean();
  private final Object shutdownNowLock = new Object();
  private volatile boolean terminateInitiated;
  
  AsynchronousChannelGroupImpl(AsynchronousChannelProvider paramAsynchronousChannelProvider, ThreadPool paramThreadPool)
  {
    super(paramAsynchronousChannelProvider);
    pool = paramThreadPool;
    if (paramThreadPool.isFixedThreadPool()) {
      taskQueue = new ConcurrentLinkedQueue();
    } else {
      taskQueue = null;
    }
    timeoutExecutor = ((ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1, ThreadPool.defaultThreadFactory()));
    timeoutExecutor.setRemoveOnCancelPolicy(true);
  }
  
  final ExecutorService executor()
  {
    return pool.executor();
  }
  
  final boolean isFixedThreadPool()
  {
    return pool.isFixedThreadPool();
  }
  
  final int fixedThreadCount()
  {
    if (isFixedThreadPool()) {
      return pool.poolSize();
    }
    return pool.poolSize() + internalThreadCount;
  }
  
  private Runnable bindToGroup(final Runnable paramRunnable)
  {
    final AsynchronousChannelGroupImpl localAsynchronousChannelGroupImpl = this;
    new Runnable()
    {
      public void run()
      {
        Invoker.bindToGroup(localAsynchronousChannelGroupImpl);
        paramRunnable.run();
      }
    };
  }
  
  private void startInternalThread(final Runnable paramRunnable)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        ThreadPool.defaultThreadFactory().newThread(paramRunnable).start();
        return null;
      }
    });
  }
  
  protected final void startThreads(Runnable paramRunnable)
  {
    int i;
    if (!isFixedThreadPool()) {
      for (i = 0; i < internalThreadCount; i++)
      {
        startInternalThread(paramRunnable);
        threadCount.incrementAndGet();
      }
    }
    if (pool.poolSize() > 0)
    {
      paramRunnable = bindToGroup(paramRunnable);
      try
      {
        for (i = 0; i < pool.poolSize(); i++)
        {
          pool.executor().execute(paramRunnable);
          threadCount.incrementAndGet();
        }
      }
      catch (RejectedExecutionException localRejectedExecutionException) {}
    }
  }
  
  final int threadCount()
  {
    return threadCount.get();
  }
  
  final int threadExit(Runnable paramRunnable, boolean paramBoolean)
  {
    if (paramBoolean) {
      try
      {
        if (Invoker.isBoundToAnyGroup()) {
          pool.executor().execute(bindToGroup(paramRunnable));
        } else {
          startInternalThread(paramRunnable);
        }
        return threadCount.get();
      }
      catch (RejectedExecutionException localRejectedExecutionException) {}
    }
    return threadCount.decrementAndGet();
  }
  
  abstract void executeOnHandlerTask(Runnable paramRunnable);
  
  final void executeOnPooledThread(Runnable paramRunnable)
  {
    if (isFixedThreadPool()) {
      executeOnHandlerTask(paramRunnable);
    } else {
      pool.executor().execute(bindToGroup(paramRunnable));
    }
  }
  
  final void offerTask(Runnable paramRunnable)
  {
    taskQueue.offer(paramRunnable);
  }
  
  final Runnable pollTask()
  {
    return taskQueue == null ? null : (Runnable)taskQueue.poll();
  }
  
  final Future<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit)
  {
    try
    {
      return timeoutExecutor.schedule(paramRunnable, paramLong, paramTimeUnit);
    }
    catch (RejectedExecutionException localRejectedExecutionException)
    {
      if (terminateInitiated) {
        return null;
      }
      throw new AssertionError(localRejectedExecutionException);
    }
  }
  
  public final boolean isShutdown()
  {
    return shutdown.get();
  }
  
  public final boolean isTerminated()
  {
    return pool.executor().isTerminated();
  }
  
  abstract boolean isEmpty();
  
  abstract Object attachForeignChannel(Channel paramChannel, FileDescriptor paramFileDescriptor)
    throws IOException;
  
  abstract void detachForeignChannel(Object paramObject);
  
  abstract void closeAllChannels()
    throws IOException;
  
  abstract void shutdownHandlerTasks();
  
  private void shutdownExecutors()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        pool.executor().shutdown();
        timeoutExecutor.shutdown();
        return null;
      }
    }, null, new Permission[] { new RuntimePermission("modifyThread") });
  }
  
  public final void shutdown()
  {
    if (shutdown.getAndSet(true)) {
      return;
    }
    if (!isEmpty()) {
      return;
    }
    synchronized (shutdownNowLock)
    {
      if (!terminateInitiated)
      {
        terminateInitiated = true;
        shutdownHandlerTasks();
        shutdownExecutors();
      }
    }
  }
  
  public final void shutdownNow()
    throws IOException
  {
    shutdown.set(true);
    synchronized (shutdownNowLock)
    {
      if (!terminateInitiated)
      {
        terminateInitiated = true;
        closeAllChannels();
        shutdownHandlerTasks();
        shutdownExecutors();
      }
    }
  }
  
  final void detachFromThreadPool()
  {
    if (shutdown.getAndSet(true)) {
      throw new AssertionError("Already shutdown");
    }
    if (!isEmpty()) {
      throw new AssertionError("Group not empty");
    }
    shutdownHandlerTasks();
  }
  
  public final boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return pool.executor().awaitTermination(paramLong, paramTimeUnit);
  }
  
  public final void execute(Runnable paramRunnable)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      final AccessControlContext localAccessControlContext = AccessController.getContext();
      final Runnable localRunnable = paramRunnable;
      paramRunnable = new Runnable()
      {
        public void run()
        {
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Void run()
            {
              val$delegate.run();
              return null;
            }
          }, localAccessControlContext);
        }
      };
    }
    executeOnPooledThread(paramRunnable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\AsynchronousChannelGroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */