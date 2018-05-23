package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduledThreadPoolExecutor
  extends ThreadPoolExecutor
  implements ScheduledExecutorService
{
  private volatile boolean continueExistingPeriodicTasksAfterShutdown;
  private volatile boolean executeExistingDelayedTasksAfterShutdown = true;
  private volatile boolean removeOnCancel = false;
  private static final AtomicLong sequencer = new AtomicLong();
  
  final long now()
  {
    return System.nanoTime();
  }
  
  boolean canRunInCurrentRunState(boolean paramBoolean)
  {
    return isRunningOrShutdown(paramBoolean ? continueExistingPeriodicTasksAfterShutdown : executeExistingDelayedTasksAfterShutdown);
  }
  
  private void delayedExecute(RunnableScheduledFuture<?> paramRunnableScheduledFuture)
  {
    if (isShutdown())
    {
      reject(paramRunnableScheduledFuture);
    }
    else
    {
      super.getQueue().add(paramRunnableScheduledFuture);
      if ((isShutdown()) && (!canRunInCurrentRunState(paramRunnableScheduledFuture.isPeriodic())) && (remove(paramRunnableScheduledFuture))) {
        paramRunnableScheduledFuture.cancel(false);
      } else {
        ensurePrestart();
      }
    }
  }
  
  void reExecutePeriodic(RunnableScheduledFuture<?> paramRunnableScheduledFuture)
  {
    if (canRunInCurrentRunState(true))
    {
      super.getQueue().add(paramRunnableScheduledFuture);
      if ((!canRunInCurrentRunState(true)) && (remove(paramRunnableScheduledFuture))) {
        paramRunnableScheduledFuture.cancel(false);
      } else {
        ensurePrestart();
      }
    }
  }
  
  void onShutdown()
  {
    BlockingQueue localBlockingQueue = super.getQueue();
    boolean bool1 = getExecuteExistingDelayedTasksAfterShutdownPolicy();
    boolean bool2 = getContinueExistingPeriodicTasksAfterShutdownPolicy();
    Object localObject;
    if ((!bool1) && (!bool2))
    {
      for (localObject : localBlockingQueue.toArray()) {
        if ((localObject instanceof RunnableScheduledFuture)) {
          ((RunnableScheduledFuture)localObject).cancel(false);
        }
      }
      localBlockingQueue.clear();
    }
    else
    {
      for (localObject : localBlockingQueue.toArray()) {
        if ((localObject instanceof RunnableScheduledFuture))
        {
          RunnableScheduledFuture localRunnableScheduledFuture = (RunnableScheduledFuture)localObject;
          if (localRunnableScheduledFuture.isPeriodic() ? bool2 : bool1)
          {
            if (!localRunnableScheduledFuture.isCancelled()) {}
          }
          else if (localBlockingQueue.remove(localRunnableScheduledFuture)) {
            localRunnableScheduledFuture.cancel(false);
          }
        }
      }
    }
    tryTerminate();
  }
  
  protected <V> RunnableScheduledFuture<V> decorateTask(Runnable paramRunnable, RunnableScheduledFuture<V> paramRunnableScheduledFuture)
  {
    return paramRunnableScheduledFuture;
  }
  
  protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> paramCallable, RunnableScheduledFuture<V> paramRunnableScheduledFuture)
  {
    return paramRunnableScheduledFuture;
  }
  
  public ScheduledThreadPoolExecutor(int paramInt)
  {
    super(paramInt, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue());
  }
  
  public ScheduledThreadPoolExecutor(int paramInt, ThreadFactory paramThreadFactory)
  {
    super(paramInt, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), paramThreadFactory);
  }
  
  public ScheduledThreadPoolExecutor(int paramInt, RejectedExecutionHandler paramRejectedExecutionHandler)
  {
    super(paramInt, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), paramRejectedExecutionHandler);
  }
  
  public ScheduledThreadPoolExecutor(int paramInt, ThreadFactory paramThreadFactory, RejectedExecutionHandler paramRejectedExecutionHandler)
  {
    super(paramInt, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), paramThreadFactory, paramRejectedExecutionHandler);
  }
  
  private long triggerTime(long paramLong, TimeUnit paramTimeUnit)
  {
    return triggerTime(paramTimeUnit.toNanos(paramLong < 0L ? 0L : paramLong));
  }
  
  long triggerTime(long paramLong)
  {
    return now() + (paramLong < 4611686018427387903L ? paramLong : overflowFree(paramLong));
  }
  
  private long overflowFree(long paramLong)
  {
    Delayed localDelayed = (Delayed)super.getQueue().peek();
    if (localDelayed != null)
    {
      long l = localDelayed.getDelay(TimeUnit.NANOSECONDS);
      if ((l < 0L) && (paramLong - l < 0L)) {
        paramLong = Long.MAX_VALUE + l;
      }
    }
    return paramLong;
  }
  
  public ScheduledFuture<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit)
  {
    if ((paramRunnable == null) || (paramTimeUnit == null)) {
      throw new NullPointerException();
    }
    RunnableScheduledFuture localRunnableScheduledFuture = decorateTask(paramRunnable, new ScheduledFutureTask(paramRunnable, null, triggerTime(paramLong, paramTimeUnit)));
    delayedExecute(localRunnableScheduledFuture);
    return localRunnableScheduledFuture;
  }
  
  public <V> ScheduledFuture<V> schedule(Callable<V> paramCallable, long paramLong, TimeUnit paramTimeUnit)
  {
    if ((paramCallable == null) || (paramTimeUnit == null)) {
      throw new NullPointerException();
    }
    RunnableScheduledFuture localRunnableScheduledFuture = decorateTask(paramCallable, new ScheduledFutureTask(paramCallable, triggerTime(paramLong, paramTimeUnit)));
    delayedExecute(localRunnableScheduledFuture);
    return localRunnableScheduledFuture;
  }
  
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
  {
    if ((paramRunnable == null) || (paramTimeUnit == null)) {
      throw new NullPointerException();
    }
    if (paramLong2 <= 0L) {
      throw new IllegalArgumentException();
    }
    ScheduledFutureTask localScheduledFutureTask = new ScheduledFutureTask(paramRunnable, null, triggerTime(paramLong1, paramTimeUnit), paramTimeUnit.toNanos(paramLong2));
    RunnableScheduledFuture localRunnableScheduledFuture = decorateTask(paramRunnable, localScheduledFutureTask);
    outerTask = localRunnableScheduledFuture;
    delayedExecute(localRunnableScheduledFuture);
    return localRunnableScheduledFuture;
  }
  
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
  {
    if ((paramRunnable == null) || (paramTimeUnit == null)) {
      throw new NullPointerException();
    }
    if (paramLong2 <= 0L) {
      throw new IllegalArgumentException();
    }
    ScheduledFutureTask localScheduledFutureTask = new ScheduledFutureTask(paramRunnable, null, triggerTime(paramLong1, paramTimeUnit), paramTimeUnit.toNanos(-paramLong2));
    RunnableScheduledFuture localRunnableScheduledFuture = decorateTask(paramRunnable, localScheduledFutureTask);
    outerTask = localRunnableScheduledFuture;
    delayedExecute(localRunnableScheduledFuture);
    return localRunnableScheduledFuture;
  }
  
  public void execute(Runnable paramRunnable)
  {
    schedule(paramRunnable, 0L, TimeUnit.NANOSECONDS);
  }
  
  public Future<?> submit(Runnable paramRunnable)
  {
    return schedule(paramRunnable, 0L, TimeUnit.NANOSECONDS);
  }
  
  public <T> Future<T> submit(Runnable paramRunnable, T paramT)
  {
    return schedule(Executors.callable(paramRunnable, paramT), 0L, TimeUnit.NANOSECONDS);
  }
  
  public <T> Future<T> submit(Callable<T> paramCallable)
  {
    return schedule(paramCallable, 0L, TimeUnit.NANOSECONDS);
  }
  
  public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean paramBoolean)
  {
    continueExistingPeriodicTasksAfterShutdown = paramBoolean;
    if ((!paramBoolean) && (isShutdown())) {
      onShutdown();
    }
  }
  
  public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy()
  {
    return continueExistingPeriodicTasksAfterShutdown;
  }
  
  public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean paramBoolean)
  {
    executeExistingDelayedTasksAfterShutdown = paramBoolean;
    if ((!paramBoolean) && (isShutdown())) {
      onShutdown();
    }
  }
  
  public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy()
  {
    return executeExistingDelayedTasksAfterShutdown;
  }
  
  public void setRemoveOnCancelPolicy(boolean paramBoolean)
  {
    removeOnCancel = paramBoolean;
  }
  
  public boolean getRemoveOnCancelPolicy()
  {
    return removeOnCancel;
  }
  
  public void shutdown()
  {
    super.shutdown();
  }
  
  public List<Runnable> shutdownNow()
  {
    return super.shutdownNow();
  }
  
  public BlockingQueue<Runnable> getQueue()
  {
    return super.getQueue();
  }
  
  static class DelayedWorkQueue
    extends AbstractQueue<Runnable>
    implements BlockingQueue<Runnable>
  {
    private static final int INITIAL_CAPACITY = 16;
    private RunnableScheduledFuture<?>[] queue = new RunnableScheduledFuture[16];
    private final ReentrantLock lock = new ReentrantLock();
    private int size = 0;
    private Thread leader = null;
    private final Condition available = lock.newCondition();
    
    DelayedWorkQueue() {}
    
    private void setIndex(RunnableScheduledFuture<?> paramRunnableScheduledFuture, int paramInt)
    {
      if ((paramRunnableScheduledFuture instanceof ScheduledThreadPoolExecutor.ScheduledFutureTask)) {
        heapIndex = paramInt;
      }
    }
    
    private void siftUp(int paramInt, RunnableScheduledFuture<?> paramRunnableScheduledFuture)
    {
      while (paramInt > 0)
      {
        int i = paramInt - 1 >>> 1;
        RunnableScheduledFuture localRunnableScheduledFuture = queue[i];
        if (paramRunnableScheduledFuture.compareTo(localRunnableScheduledFuture) >= 0) {
          break;
        }
        queue[paramInt] = localRunnableScheduledFuture;
        setIndex(localRunnableScheduledFuture, paramInt);
        paramInt = i;
      }
      queue[paramInt] = paramRunnableScheduledFuture;
      setIndex(paramRunnableScheduledFuture, paramInt);
    }
    
    private void siftDown(int paramInt, RunnableScheduledFuture<?> paramRunnableScheduledFuture)
    {
      int i = size >>> 1;
      while (paramInt < i)
      {
        int j = (paramInt << 1) + 1;
        RunnableScheduledFuture localRunnableScheduledFuture = queue[j];
        int k = j + 1;
        if ((k < size) && (localRunnableScheduledFuture.compareTo(queue[k]) > 0)) {
          localRunnableScheduledFuture = queue[(j = k)];
        }
        if (paramRunnableScheduledFuture.compareTo(localRunnableScheduledFuture) <= 0) {
          break;
        }
        queue[paramInt] = localRunnableScheduledFuture;
        setIndex(localRunnableScheduledFuture, paramInt);
        paramInt = j;
      }
      queue[paramInt] = paramRunnableScheduledFuture;
      setIndex(paramRunnableScheduledFuture, paramInt);
    }
    
    private void grow()
    {
      int i = queue.length;
      int j = i + (i >> 1);
      if (j < 0) {
        j = Integer.MAX_VALUE;
      }
      queue = ((RunnableScheduledFuture[])Arrays.copyOf(queue, j));
    }
    
    private int indexOf(Object paramObject)
    {
      if (paramObject != null)
      {
        int i;
        if ((paramObject instanceof ScheduledThreadPoolExecutor.ScheduledFutureTask))
        {
          i = heapIndex;
          if ((i >= 0) && (i < size) && (queue[i] == paramObject)) {
            return i;
          }
        }
        else
        {
          for (i = 0; i < size; i++) {
            if (paramObject.equals(queue[i])) {
              return i;
            }
          }
        }
      }
      return -1;
    }
    
    public boolean contains(Object paramObject)
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        boolean bool = indexOf(paramObject) != -1;
        return bool;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public boolean remove(Object paramObject)
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        int i = indexOf(paramObject);
        if (i < 0)
        {
          boolean bool1 = false;
          return bool1;
        }
        setIndex(queue[i], -1);
        int j = --size;
        RunnableScheduledFuture localRunnableScheduledFuture = queue[j];
        queue[j] = null;
        if (j != i)
        {
          siftDown(i, localRunnableScheduledFuture);
          if (queue[i] == localRunnableScheduledFuture) {
            siftUp(i, localRunnableScheduledFuture);
          }
        }
        boolean bool2 = true;
        return bool2;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public int size()
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        int i = size;
        return i;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public boolean isEmpty()
    {
      return size() == 0;
    }
    
    public int remainingCapacity()
    {
      return Integer.MAX_VALUE;
    }
    
    public RunnableScheduledFuture<?> peek()
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        RunnableScheduledFuture localRunnableScheduledFuture = queue[0];
        return localRunnableScheduledFuture;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public boolean offer(Runnable paramRunnable)
    {
      if (paramRunnable == null) {
        throw new NullPointerException();
      }
      RunnableScheduledFuture localRunnableScheduledFuture = (RunnableScheduledFuture)paramRunnable;
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        int i = size;
        if (i >= queue.length) {
          grow();
        }
        size = (i + 1);
        if (i == 0)
        {
          queue[0] = localRunnableScheduledFuture;
          setIndex(localRunnableScheduledFuture, 0);
        }
        else
        {
          siftUp(i, localRunnableScheduledFuture);
        }
        if (queue[0] == localRunnableScheduledFuture)
        {
          leader = null;
          available.signal();
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
      return true;
    }
    
    public void put(Runnable paramRunnable)
    {
      offer(paramRunnable);
    }
    
    public boolean add(Runnable paramRunnable)
    {
      return offer(paramRunnable);
    }
    
    public boolean offer(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit)
    {
      return offer(paramRunnable);
    }
    
    private RunnableScheduledFuture<?> finishPoll(RunnableScheduledFuture<?> paramRunnableScheduledFuture)
    {
      int i = --size;
      RunnableScheduledFuture localRunnableScheduledFuture = queue[i];
      queue[i] = null;
      if (i != 0) {
        siftDown(0, localRunnableScheduledFuture);
      }
      setIndex(paramRunnableScheduledFuture, -1);
      return paramRunnableScheduledFuture;
    }
    
    public RunnableScheduledFuture<?> poll()
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        RunnableScheduledFuture localRunnableScheduledFuture = queue[0];
        if ((localRunnableScheduledFuture == null) || (localRunnableScheduledFuture.getDelay(TimeUnit.NANOSECONDS) > 0L))
        {
          localObject1 = null;
          return (RunnableScheduledFuture<?>)localObject1;
        }
        Object localObject1 = finishPoll(localRunnableScheduledFuture);
        return (RunnableScheduledFuture<?>)localObject1;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    /* Error */
    public RunnableScheduledFuture<?> take()
      throws java.lang.InterruptedException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 190	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
      //   4: astore_1
      //   5: aload_1
      //   6: invokevirtual 222	java/util/concurrent/locks/ReentrantLock:lockInterruptibly	()V
      //   9: aload_0
      //   10: getfield 188	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:queue	[Ljava/util/concurrent/RunnableScheduledFuture;
      //   13: iconst_0
      //   14: aaload
      //   15: astore_2
      //   16: aload_2
      //   17: ifnonnull +15 -> 32
      //   20: aload_0
      //   21: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   24: invokeinterface 228 1 0
      //   29: goto +137 -> 166
      //   32: aload_2
      //   33: getstatic 192	java/util/concurrent/TimeUnit:NANOSECONDS	Ljava/util/concurrent/TimeUnit;
      //   36: invokeinterface 227 2 0
      //   41: lstore_3
      //   42: lload_3
      //   43: lconst_0
      //   44: lcmp
      //   45: ifgt +42 -> 87
      //   48: aload_0
      //   49: aload_2
      //   50: invokespecial 215	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:finishPoll	(Ljava/util/concurrent/RunnableScheduledFuture;)Ljava/util/concurrent/RunnableScheduledFuture;
      //   53: astore 5
      //   55: aload_0
      //   56: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   59: ifnonnull +21 -> 80
      //   62: aload_0
      //   63: getfield 188	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:queue	[Ljava/util/concurrent/RunnableScheduledFuture;
      //   66: iconst_0
      //   67: aaload
      //   68: ifnull +12 -> 80
      //   71: aload_0
      //   72: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   75: invokeinterface 229 1 0
      //   80: aload_1
      //   81: invokevirtual 223	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   84: aload 5
      //   86: areturn
      //   87: aconst_null
      //   88: astore_2
      //   89: aload_0
      //   90: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   93: ifnull +15 -> 108
      //   96: aload_0
      //   97: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   100: invokeinterface 228 1 0
      //   105: goto +61 -> 166
      //   108: invokestatic 198	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   111: astore 5
      //   113: aload_0
      //   114: aload 5
      //   116: putfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   119: aload_0
      //   120: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   123: lload_3
      //   124: invokeinterface 230 3 0
      //   129: pop2
      //   130: aload_0
      //   131: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   134: aload 5
      //   136: if_acmpne +30 -> 166
      //   139: aload_0
      //   140: aconst_null
      //   141: putfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   144: goto +22 -> 166
      //   147: astore 6
      //   149: aload_0
      //   150: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   153: aload 5
      //   155: if_acmpne +8 -> 163
      //   158: aload_0
      //   159: aconst_null
      //   160: putfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   163: aload 6
      //   165: athrow
      //   166: goto -157 -> 9
      //   169: astore 7
      //   171: aload_0
      //   172: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   175: ifnonnull +21 -> 196
      //   178: aload_0
      //   179: getfield 188	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:queue	[Ljava/util/concurrent/RunnableScheduledFuture;
      //   182: iconst_0
      //   183: aaload
      //   184: ifnull +12 -> 196
      //   187: aload_0
      //   188: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   191: invokeinterface 229 1 0
      //   196: aload_1
      //   197: invokevirtual 223	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   200: aload 7
      //   202: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	203	0	this	DelayedWorkQueue
      //   4	193	1	localReentrantLock	ReentrantLock
      //   15	74	2	localRunnableScheduledFuture	RunnableScheduledFuture
      //   41	83	3	l	long
      //   53	101	5	localObject1	Object
      //   147	17	6	localObject2	Object
      //   169	32	7	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   119	130	147	finally
      //   147	149	147	finally
      //   9	55	169	finally
      //   87	171	169	finally
    }
    
    /* Error */
    public RunnableScheduledFuture<?> poll(long paramLong, TimeUnit paramTimeUnit)
      throws java.lang.InterruptedException
    {
      // Byte code:
      //   0: aload_3
      //   1: lload_1
      //   2: invokevirtual 219	java/util/concurrent/TimeUnit:toNanos	(J)J
      //   5: lstore 4
      //   7: aload_0
      //   8: getfield 190	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
      //   11: astore 6
      //   13: aload 6
      //   15: invokevirtual 222	java/util/concurrent/locks/ReentrantLock:lockInterruptibly	()V
      //   18: aload_0
      //   19: getfield 188	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:queue	[Ljava/util/concurrent/RunnableScheduledFuture;
      //   22: iconst_0
      //   23: aaload
      //   24: astore 7
      //   26: aload 7
      //   28: ifnonnull +62 -> 90
      //   31: lload 4
      //   33: lconst_0
      //   34: lcmp
      //   35: ifgt +39 -> 74
      //   38: aconst_null
      //   39: astore 8
      //   41: aload_0
      //   42: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   45: ifnonnull +21 -> 66
      //   48: aload_0
      //   49: getfield 188	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:queue	[Ljava/util/concurrent/RunnableScheduledFuture;
      //   52: iconst_0
      //   53: aaload
      //   54: ifnull +12 -> 66
      //   57: aload_0
      //   58: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   61: invokeinterface 229 1 0
      //   66: aload 6
      //   68: invokevirtual 223	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   71: aload 8
      //   73: areturn
      //   74: aload_0
      //   75: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   78: lload 4
      //   80: invokeinterface 230 3 0
      //   85: lstore 4
      //   87: goto +210 -> 297
      //   90: aload 7
      //   92: getstatic 192	java/util/concurrent/TimeUnit:NANOSECONDS	Ljava/util/concurrent/TimeUnit;
      //   95: invokeinterface 227 2 0
      //   100: lstore 8
      //   102: lload 8
      //   104: lconst_0
      //   105: lcmp
      //   106: ifgt +44 -> 150
      //   109: aload_0
      //   110: aload 7
      //   112: invokespecial 215	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:finishPoll	(Ljava/util/concurrent/RunnableScheduledFuture;)Ljava/util/concurrent/RunnableScheduledFuture;
      //   115: astore 10
      //   117: aload_0
      //   118: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   121: ifnonnull +21 -> 142
      //   124: aload_0
      //   125: getfield 188	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:queue	[Ljava/util/concurrent/RunnableScheduledFuture;
      //   128: iconst_0
      //   129: aaload
      //   130: ifnull +12 -> 142
      //   133: aload_0
      //   134: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   137: invokeinterface 229 1 0
      //   142: aload 6
      //   144: invokevirtual 223	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   147: aload 10
      //   149: areturn
      //   150: lload 4
      //   152: lconst_0
      //   153: lcmp
      //   154: ifgt +39 -> 193
      //   157: aconst_null
      //   158: astore 10
      //   160: aload_0
      //   161: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   164: ifnonnull +21 -> 185
      //   167: aload_0
      //   168: getfield 188	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:queue	[Ljava/util/concurrent/RunnableScheduledFuture;
      //   171: iconst_0
      //   172: aaload
      //   173: ifnull +12 -> 185
      //   176: aload_0
      //   177: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   180: invokeinterface 229 1 0
      //   185: aload 6
      //   187: invokevirtual 223	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   190: aload 10
      //   192: areturn
      //   193: aconst_null
      //   194: astore 7
      //   196: lload 4
      //   198: lload 8
      //   200: lcmp
      //   201: iflt +10 -> 211
      //   204: aload_0
      //   205: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   208: ifnull +19 -> 227
      //   211: aload_0
      //   212: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   215: lload 4
      //   217: invokeinterface 230 3 0
      //   222: lstore 4
      //   224: goto +73 -> 297
      //   227: invokestatic 198	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   230: astore 10
      //   232: aload_0
      //   233: aload 10
      //   235: putfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   238: aload_0
      //   239: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   242: lload 8
      //   244: invokeinterface 230 3 0
      //   249: lstore 11
      //   251: lload 4
      //   253: lload 8
      //   255: lload 11
      //   257: lsub
      //   258: lsub
      //   259: lstore 4
      //   261: aload_0
      //   262: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   265: aload 10
      //   267: if_acmpne +30 -> 297
      //   270: aload_0
      //   271: aconst_null
      //   272: putfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   275: goto +22 -> 297
      //   278: astore 13
      //   280: aload_0
      //   281: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   284: aload 10
      //   286: if_acmpne +8 -> 294
      //   289: aload_0
      //   290: aconst_null
      //   291: putfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   294: aload 13
      //   296: athrow
      //   297: goto -279 -> 18
      //   300: astore 14
      //   302: aload_0
      //   303: getfield 187	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:leader	Ljava/lang/Thread;
      //   306: ifnonnull +21 -> 327
      //   309: aload_0
      //   310: getfield 188	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:queue	[Ljava/util/concurrent/RunnableScheduledFuture;
      //   313: iconst_0
      //   314: aaload
      //   315: ifnull +12 -> 327
      //   318: aload_0
      //   319: getfield 189	java/util/concurrent/ScheduledThreadPoolExecutor$DelayedWorkQueue:available	Ljava/util/concurrent/locks/Condition;
      //   322: invokeinterface 229 1 0
      //   327: aload 6
      //   329: invokevirtual 223	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   332: aload 14
      //   334: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	335	0	this	DelayedWorkQueue
      //   0	335	1	paramLong	long
      //   0	335	3	paramTimeUnit	TimeUnit
      //   5	255	4	l1	long
      //   11	317	6	localReentrantLock	ReentrantLock
      //   24	171	7	localRunnableScheduledFuture	RunnableScheduledFuture
      //   39	33	8	localRunnableScheduledFuture1	RunnableScheduledFuture<?>
      //   100	154	8	l2	long
      //   115	170	10	localObject1	Object
      //   249	7	11	l3	long
      //   278	17	13	localObject2	Object
      //   300	33	14	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   238	261	278	finally
      //   278	280	278	finally
      //   18	41	300	finally
      //   74	117	300	finally
      //   150	160	300	finally
      //   193	302	300	finally
    }
    
    public void clear()
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        for (int i = 0; i < size; i++)
        {
          RunnableScheduledFuture localRunnableScheduledFuture = queue[i];
          if (localRunnableScheduledFuture != null)
          {
            queue[i] = null;
            setIndex(localRunnableScheduledFuture, -1);
          }
        }
        size = 0;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    private RunnableScheduledFuture<?> peekExpired()
    {
      RunnableScheduledFuture localRunnableScheduledFuture = queue[0];
      return (localRunnableScheduledFuture == null) || (localRunnableScheduledFuture.getDelay(TimeUnit.NANOSECONDS) > 0L) ? null : localRunnableScheduledFuture;
    }
    
    public int drainTo(Collection<? super Runnable> paramCollection)
    {
      if (paramCollection == null) {
        throw new NullPointerException();
      }
      if (paramCollection == this) {
        throw new IllegalArgumentException();
      }
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        RunnableScheduledFuture localRunnableScheduledFuture;
        for (int i = 0; (localRunnableScheduledFuture = peekExpired()) != null; i++)
        {
          paramCollection.add(localRunnableScheduledFuture);
          finishPoll(localRunnableScheduledFuture);
        }
        int j = i;
        return j;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public int drainTo(Collection<? super Runnable> paramCollection, int paramInt)
    {
      if (paramCollection == null) {
        throw new NullPointerException();
      }
      if (paramCollection == this) {
        throw new IllegalArgumentException();
      }
      if (paramInt <= 0) {
        return 0;
      }
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        RunnableScheduledFuture localRunnableScheduledFuture;
        for (int i = 0; (i < paramInt) && ((localRunnableScheduledFuture = peekExpired()) != null); i++)
        {
          paramCollection.add(localRunnableScheduledFuture);
          finishPoll(localRunnableScheduledFuture);
        }
        int j = i;
        return j;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public Object[] toArray()
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        Object[] arrayOfObject = Arrays.copyOf(queue, size, Object[].class);
        return arrayOfObject;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        if (paramArrayOfT.length < size)
        {
          localObject1 = (Object[])Arrays.copyOf(queue, size, paramArrayOfT.getClass());
          return (T[])localObject1;
        }
        System.arraycopy(queue, 0, paramArrayOfT, 0, size);
        if (paramArrayOfT.length > size) {
          paramArrayOfT[size] = null;
        }
        Object localObject1 = paramArrayOfT;
        return (T[])localObject1;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public Iterator<Runnable> iterator()
    {
      return new Itr((RunnableScheduledFuture[])Arrays.copyOf(queue, size));
    }
    
    private class Itr
      implements Iterator<Runnable>
    {
      final RunnableScheduledFuture<?>[] array;
      int cursor = 0;
      int lastRet = -1;
      
      Itr()
      {
        RunnableScheduledFuture[] arrayOfRunnableScheduledFuture;
        array = arrayOfRunnableScheduledFuture;
      }
      
      public boolean hasNext()
      {
        return cursor < array.length;
      }
      
      public Runnable next()
      {
        if (cursor >= array.length) {
          throw new NoSuchElementException();
        }
        lastRet = cursor;
        return array[(cursor++)];
      }
      
      public void remove()
      {
        if (lastRet < 0) {
          throw new IllegalStateException();
        }
        remove(array[lastRet]);
        lastRet = -1;
      }
    }
  }
  
  private class ScheduledFutureTask<V>
    extends FutureTask<V>
    implements RunnableScheduledFuture<V>
  {
    private final long sequenceNumber;
    private long time;
    private final long period;
    RunnableScheduledFuture<V> outerTask = this;
    int heapIndex;
    
    ScheduledFutureTask(V paramV, long paramLong)
    {
      super(paramLong);
      Object localObject;
      time = localObject;
      period = 0L;
      sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
    }
    
    ScheduledFutureTask(V paramV, long paramLong1, long paramLong2)
    {
      super(paramLong1);
      time = ???;
      Object localObject;
      period = localObject;
      sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
    }
    
    ScheduledFutureTask(long paramLong)
    {
      super();
      Object localObject;
      time = localObject;
      period = 0L;
      sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
    }
    
    public long getDelay(TimeUnit paramTimeUnit)
    {
      return paramTimeUnit.convert(time - now(), TimeUnit.NANOSECONDS);
    }
    
    public int compareTo(Delayed paramDelayed)
    {
      if (paramDelayed == this) {
        return 0;
      }
      if ((paramDelayed instanceof ScheduledFutureTask))
      {
        ScheduledFutureTask localScheduledFutureTask = (ScheduledFutureTask)paramDelayed;
        long l2 = time - time;
        if (l2 < 0L) {
          return -1;
        }
        if (l2 > 0L) {
          return 1;
        }
        if (sequenceNumber < sequenceNumber) {
          return -1;
        }
        return 1;
      }
      long l1 = getDelay(TimeUnit.NANOSECONDS) - paramDelayed.getDelay(TimeUnit.NANOSECONDS);
      return l1 > 0L ? 1 : l1 < 0L ? -1 : 0;
    }
    
    public boolean isPeriodic()
    {
      return period != 0L;
    }
    
    private void setNextRunTime()
    {
      long l = period;
      if (l > 0L) {
        time += l;
      } else {
        time = triggerTime(-l);
      }
    }
    
    public boolean cancel(boolean paramBoolean)
    {
      boolean bool = super.cancel(paramBoolean);
      if ((bool) && (removeOnCancel) && (heapIndex >= 0)) {
        remove(this);
      }
      return bool;
    }
    
    public void run()
    {
      boolean bool = isPeriodic();
      if (!canRunInCurrentRunState(bool))
      {
        cancel(false);
      }
      else if (!bool)
      {
        super.run();
      }
      else if (super.runAndReset())
      {
        setNextRunTime();
        reExecutePeriodic(outerTask);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ScheduledThreadPoolExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */