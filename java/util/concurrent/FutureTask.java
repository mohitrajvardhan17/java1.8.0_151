package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public class FutureTask<V>
  implements RunnableFuture<V>
{
  private volatile int state;
  private static final int NEW = 0;
  private static final int COMPLETING = 1;
  private static final int NORMAL = 2;
  private static final int EXCEPTIONAL = 3;
  private static final int CANCELLED = 4;
  private static final int INTERRUPTING = 5;
  private static final int INTERRUPTED = 6;
  private Callable<V> callable;
  private Object outcome;
  private volatile Thread runner;
  private volatile WaitNode waiters;
  private static final Unsafe UNSAFE;
  private static final long stateOffset;
  private static final long runnerOffset;
  private static final long waitersOffset;
  
  private V report(int paramInt)
    throws ExecutionException
  {
    Object localObject = outcome;
    if (paramInt == 2) {
      return (V)localObject;
    }
    if (paramInt >= 4) {
      throw new CancellationException();
    }
    throw new ExecutionException((Throwable)localObject);
  }
  
  public FutureTask(Callable<V> paramCallable)
  {
    if (paramCallable == null) {
      throw new NullPointerException();
    }
    callable = paramCallable;
    state = 0;
  }
  
  public FutureTask(Runnable paramRunnable, V paramV)
  {
    callable = Executors.callable(paramRunnable, paramV);
    state = 0;
  }
  
  public boolean isCancelled()
  {
    return state >= 4;
  }
  
  public boolean isDone()
  {
    return state != 0;
  }
  
  /* Error */
  public boolean cancel(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 185	java/util/concurrent/FutureTask:state	I
    //   4: ifne +26 -> 30
    //   7: getstatic 193	java/util/concurrent/FutureTask:UNSAFE	Lsun/misc/Unsafe;
    //   10: aload_0
    //   11: getstatic 187	java/util/concurrent/FutureTask:stateOffset	J
    //   14: iconst_0
    //   15: iload_1
    //   16: ifeq +7 -> 23
    //   19: iconst_5
    //   20: goto +4 -> 24
    //   23: iconst_4
    //   24: invokevirtual 224	sun/misc/Unsafe:compareAndSwapInt	(Ljava/lang/Object;JII)Z
    //   27: ifne +5 -> 32
    //   30: iconst_0
    //   31: ireturn
    //   32: iload_1
    //   33: ifeq +46 -> 79
    //   36: aload_0
    //   37: getfield 190	java/util/concurrent/FutureTask:runner	Ljava/lang/Thread;
    //   40: astore_2
    //   41: aload_2
    //   42: ifnull +7 -> 49
    //   45: aload_2
    //   46: invokevirtual 202	java/lang/Thread:interrupt	()V
    //   49: getstatic 193	java/util/concurrent/FutureTask:UNSAFE	Lsun/misc/Unsafe;
    //   52: aload_0
    //   53: getstatic 187	java/util/concurrent/FutureTask:stateOffset	J
    //   56: bipush 6
    //   58: invokevirtual 223	sun/misc/Unsafe:putOrderedInt	(Ljava/lang/Object;JI)V
    //   61: goto +18 -> 79
    //   64: astore_3
    //   65: getstatic 193	java/util/concurrent/FutureTask:UNSAFE	Lsun/misc/Unsafe;
    //   68: aload_0
    //   69: getstatic 187	java/util/concurrent/FutureTask:stateOffset	J
    //   72: bipush 6
    //   74: invokevirtual 223	sun/misc/Unsafe:putOrderedInt	(Ljava/lang/Object;JI)V
    //   77: aload_3
    //   78: athrow
    //   79: aload_0
    //   80: invokespecial 210	java/util/concurrent/FutureTask:finishCompletion	()V
    //   83: goto +12 -> 95
    //   86: astore 4
    //   88: aload_0
    //   89: invokespecial 210	java/util/concurrent/FutureTask:finishCompletion	()V
    //   92: aload 4
    //   94: athrow
    //   95: iconst_1
    //   96: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	FutureTask
    //   0	97	1	paramBoolean	boolean
    //   40	6	2	localThread	Thread
    //   64	14	3	localObject1	Object
    //   86	7	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   36	49	64	finally
    //   32	79	86	finally
    //   86	88	86	finally
  }
  
  public V get()
    throws InterruptedException, ExecutionException
  {
    int i = state;
    if (i <= 1) {
      i = awaitDone(false, 0L);
    }
    return (V)report(i);
  }
  
  public V get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    if (paramTimeUnit == null) {
      throw new NullPointerException();
    }
    int i = state;
    if ((i <= 1) && ((i = awaitDone(true, paramTimeUnit.toNanos(paramLong))) <= 1)) {
      throw new TimeoutException();
    }
    return (V)report(i);
  }
  
  protected void done() {}
  
  protected void set(V paramV)
  {
    if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, 1))
    {
      outcome = paramV;
      UNSAFE.putOrderedInt(this, stateOffset, 2);
      finishCompletion();
    }
  }
  
  protected void setException(Throwable paramThrowable)
  {
    if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, 1))
    {
      outcome = paramThrowable;
      UNSAFE.putOrderedInt(this, stateOffset, 3);
      finishCompletion();
    }
  }
  
  public void run()
  {
    if ((state != 0) || (!UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))) {
      return;
    }
    try
    {
      Callable localCallable = callable;
      if ((localCallable != null) && (state == 0))
      {
        Object localObject1;
        int j;
        try
        {
          localObject1 = localCallable.call();
          j = 1;
        }
        catch (Throwable localThrowable)
        {
          localObject1 = null;
          j = 0;
          setException(localThrowable);
        }
        if (j != 0) {
          set(localObject1);
        }
      }
    }
    finally
    {
      int i;
      runner = null;
      int k = state;
      if (k >= 5) {
        handlePossibleCancellationInterrupt(k);
      }
    }
  }
  
  protected boolean runAndReset()
  {
    if ((state != 0) || (!UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))) {
      return false;
    }
    int i = 0;
    int j = state;
    try
    {
      Callable localCallable = callable;
      if ((localCallable != null) && (j == 0)) {
        try
        {
          localCallable.call();
          i = 1;
        }
        catch (Throwable localThrowable)
        {
          setException(localThrowable);
        }
      }
    }
    finally
    {
      runner = null;
      j = state;
      if (j >= 5) {
        handlePossibleCancellationInterrupt(j);
      }
    }
    return (i != 0) && (j == 0);
  }
  
  private void handlePossibleCancellationInterrupt(int paramInt)
  {
    if (paramInt == 5) {
      while (state == 5) {
        Thread.yield();
      }
    }
  }
  
  private void finishCompletion()
  {
    Object localObject;
    while ((localObject = waiters) != null) {
      if (UNSAFE.compareAndSwapObject(this, waitersOffset, localObject, null)) {
        for (;;)
        {
          Thread localThread = thread;
          if (localThread != null)
          {
            thread = null;
            LockSupport.unpark(localThread);
          }
          WaitNode localWaitNode = next;
          if (localWaitNode == null) {
            break;
          }
          next = null;
          localObject = localWaitNode;
        }
      }
    }
    done();
    callable = null;
  }
  
  private int awaitDone(boolean paramBoolean, long paramLong)
    throws InterruptedException
  {
    long l = paramBoolean ? System.nanoTime() + paramLong : 0L;
    WaitNode localWaitNode = null;
    boolean bool = false;
    for (;;)
    {
      if (Thread.interrupted())
      {
        removeWaiter(localWaitNode);
        throw new InterruptedException();
      }
      int i = state;
      if (i > 1)
      {
        if (localWaitNode != null) {
          thread = null;
        }
        return i;
      }
      if (i == 1)
      {
        Thread.yield();
      }
      else if (localWaitNode == null)
      {
        localWaitNode = new WaitNode();
      }
      else if (!bool)
      {
        bool = UNSAFE.compareAndSwapObject(this, waitersOffset, next = waiters, localWaitNode);
      }
      else if (paramBoolean)
      {
        paramLong = l - System.nanoTime();
        if (paramLong <= 0L)
        {
          removeWaiter(localWaitNode);
          return state;
        }
        LockSupport.parkNanos(this, paramLong);
      }
      else
      {
        LockSupport.park(this);
      }
    }
  }
  
  private void removeWaiter(WaitNode paramWaitNode)
  {
    if (paramWaitNode != null)
    {
      thread = null;
      Object localObject1 = null;
      WaitNode localWaitNode;
      for (Object localObject2 = waiters;; localObject2 = localWaitNode)
      {
        if (localObject2 == null) {
          return;
        }
        localWaitNode = next;
        if (thread != null)
        {
          localObject1 = localObject2;
        }
        else
        {
          if (localObject1 != null)
          {
            next = localWaitNode;
            if (thread != null) {
              continue;
            }
            break;
          }
          if (!UNSAFE.compareAndSwapObject(this, waitersOffset, localObject2, localWaitNode)) {
            break;
          }
        }
      }
    }
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = FutureTask.class;
      stateOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("state"));
      runnerOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("runner"));
      waitersOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("waiters"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class WaitNode
  {
    volatile Thread thread = Thread.currentThread();
    volatile WaitNode next;
    
    WaitNode() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\FutureTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */