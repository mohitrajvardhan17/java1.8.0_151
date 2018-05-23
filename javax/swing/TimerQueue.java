package javax.swing;

import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import sun.awt.AppContext;

class TimerQueue
  implements Runnable
{
  private static final Object sharedInstanceKey = new StringBuffer("TimerQueue.sharedInstanceKey");
  private static final Object expiredTimersKey = new StringBuffer("TimerQueue.expiredTimersKey");
  private final DelayQueue<DelayedTimer> queue = new DelayQueue();
  private volatile boolean running;
  private final Lock runningLock = new ReentrantLock();
  private static final Object classLock = new Object();
  private static final long NANO_ORIGIN = System.nanoTime();
  
  public TimerQueue()
  {
    startIfNeeded();
  }
  
  public static TimerQueue sharedInstance()
  {
    synchronized (classLock)
    {
      TimerQueue localTimerQueue = (TimerQueue)SwingUtilities.appContextGet(sharedInstanceKey);
      if (localTimerQueue == null)
      {
        localTimerQueue = new TimerQueue();
        SwingUtilities.appContextPut(sharedInstanceKey, localTimerQueue);
      }
      return localTimerQueue;
    }
  }
  
  /* Error */
  void startIfNeeded()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 197	javax/swing/TimerQueue:running	Z
    //   4: ifne +69 -> 73
    //   7: aload_0
    //   8: getfield 202	javax/swing/TimerQueue:runningLock	Ljava/util/concurrent/locks/Lock;
    //   11: invokeinterface 242 1 0
    //   16: aload_0
    //   17: getfield 197	javax/swing/TimerQueue:running	Z
    //   20: ifeq +4 -> 24
    //   23: return
    //   24: invokestatic 238	sun/awt/AppContext:getAppContext	()Lsun/awt/AppContext;
    //   27: invokevirtual 237	sun/awt/AppContext:getThreadGroup	()Ljava/lang/ThreadGroup;
    //   30: astore_1
    //   31: new 123	javax/swing/TimerQueue$1
    //   34: dup
    //   35: aload_0
    //   36: aload_1
    //   37: invokespecial 232	javax/swing/TimerQueue$1:<init>	(Ljavax/swing/TimerQueue;Ljava/lang/ThreadGroup;)V
    //   40: invokestatic 212	java/security/AccessController:doPrivileged	(Ljava/security/PrivilegedAction;)Ljava/lang/Object;
    //   43: pop
    //   44: aload_0
    //   45: iconst_1
    //   46: putfield 197	javax/swing/TimerQueue:running	Z
    //   49: aload_0
    //   50: getfield 202	javax/swing/TimerQueue:runningLock	Ljava/util/concurrent/locks/Lock;
    //   53: invokeinterface 243 1 0
    //   58: goto +15 -> 73
    //   61: astore_2
    //   62: aload_0
    //   63: getfield 202	javax/swing/TimerQueue:runningLock	Ljava/util/concurrent/locks/Lock;
    //   66: invokeinterface 243 1 0
    //   71: aload_2
    //   72: athrow
    //   73: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	TimerQueue
    //   30	7	1	localThreadGroup	ThreadGroup
    //   61	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   24	49	61	finally
  }
  
  void addTimer(Timer paramTimer, long paramLong)
  {
    paramTimer.getLock().lock();
    try
    {
      if (!containsTimer(paramTimer)) {
        addTimer(new DelayedTimer(paramTimer, TimeUnit.MILLISECONDS.toNanos(paramLong) + now()));
      }
    }
    finally
    {
      paramTimer.getLock().unlock();
    }
  }
  
  /* Error */
  private void addTimer(DelayedTimer paramDelayedTimer)
  {
    // Byte code:
    //   0: getstatic 196	javax/swing/TimerQueue:$assertionsDisabled	Z
    //   3: ifne +26 -> 29
    //   6: aload_1
    //   7: ifnull +14 -> 21
    //   10: aload_0
    //   11: aload_1
    //   12: invokevirtual 234	javax/swing/TimerQueue$DelayedTimer:getTimer	()Ljavax/swing/Timer;
    //   15: invokevirtual 230	javax/swing/TimerQueue:containsTimer	(Ljavax/swing/Timer;)Z
    //   18: ifeq +11 -> 29
    //   21: new 103	java/lang/AssertionError
    //   24: dup
    //   25: invokespecial 203	java/lang/AssertionError:<init>	()V
    //   28: athrow
    //   29: aload_1
    //   30: invokevirtual 234	javax/swing/TimerQueue$DelayedTimer:getTimer	()Ljavax/swing/Timer;
    //   33: astore_2
    //   34: aload_2
    //   35: invokevirtual 226	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   38: invokeinterface 242 1 0
    //   43: aload_2
    //   44: aload_1
    //   45: putfield 194	javax/swing/Timer:delayedTimer	Ljavax/swing/TimerQueue$DelayedTimer;
    //   48: aload_0
    //   49: getfield 201	javax/swing/TimerQueue:queue	Ljava/util/concurrent/DelayQueue;
    //   52: aload_1
    //   53: invokevirtual 217	java/util/concurrent/DelayQueue:add	(Ljava/util/concurrent/Delayed;)Z
    //   56: pop
    //   57: aload_2
    //   58: invokevirtual 226	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   61: invokeinterface 243 1 0
    //   66: goto +15 -> 81
    //   69: astore_3
    //   70: aload_2
    //   71: invokevirtual 226	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   74: invokeinterface 243 1 0
    //   79: aload_3
    //   80: athrow
    //   81: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	82	0	this	TimerQueue
    //   0	82	1	paramDelayedTimer	DelayedTimer
    //   33	38	2	localTimer	Timer
    //   69	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   43	57	69	finally
  }
  
  /* Error */
  void removeTimer(Timer paramTimer)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 226	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 242 1 0
    //   9: aload_1
    //   10: getfield 194	javax/swing/Timer:delayedTimer	Ljavax/swing/TimerQueue$DelayedTimer;
    //   13: ifnull +20 -> 33
    //   16: aload_0
    //   17: getfield 201	javax/swing/TimerQueue:queue	Ljava/util/concurrent/DelayQueue;
    //   20: aload_1
    //   21: getfield 194	javax/swing/Timer:delayedTimer	Ljavax/swing/TimerQueue$DelayedTimer;
    //   24: invokevirtual 214	java/util/concurrent/DelayQueue:remove	(Ljava/lang/Object;)Z
    //   27: pop
    //   28: aload_1
    //   29: aconst_null
    //   30: putfield 194	javax/swing/Timer:delayedTimer	Ljavax/swing/TimerQueue$DelayedTimer;
    //   33: aload_1
    //   34: invokevirtual 226	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   37: invokeinterface 243 1 0
    //   42: goto +15 -> 57
    //   45: astore_2
    //   46: aload_1
    //   47: invokevirtual 226	javax/swing/Timer:getLock	()Ljava/util/concurrent/locks/Lock;
    //   50: invokeinterface 243 1 0
    //   55: aload_2
    //   56: athrow
    //   57: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	this	TimerQueue
    //   0	58	1	paramTimer	Timer
    //   45	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	33	45	finally
  }
  
  boolean containsTimer(Timer paramTimer)
  {
    paramTimer.getLock().lock();
    try
    {
      boolean bool = delayedTimer != null;
      return bool;
    }
    finally
    {
      paramTimer.getLock().unlock();
    }
  }
  
  public void run()
  {
    runningLock.lock();
    try
    {
      for (;;)
      {
        if (running) {
          try
          {
            DelayedTimer localDelayedTimer1 = (DelayedTimer)queue.take();
            localObject1 = localDelayedTimer1.getTimer();
            ((Timer)localObject1).getLock().lock();
            try
            {
              DelayedTimer localDelayedTimer2 = delayedTimer;
              if (localDelayedTimer2 == localDelayedTimer1)
              {
                ((Timer)localObject1).post();
                delayedTimer = null;
                if (((Timer)localObject1).isRepeats())
                {
                  localDelayedTimer2.setTime(now() + TimeUnit.MILLISECONDS.toNanos(((Timer)localObject1).getDelay()));
                  addTimer(localDelayedTimer2);
                }
              }
              ((Timer)localObject1).getLock().newCondition().awaitNanos(1L);
            }
            catch (SecurityException localSecurityException) {}finally
            {
              ((Timer)localObject1).getLock().unlock();
            }
          }
          catch (InterruptedException localInterruptedException)
          {
            if (AppContext.getAppContext().isDisposed()) {
              break label165;
            }
          }
        }
      }
    }
    catch (ThreadDeath localThreadDeath)
    {
      label165:
      Object localObject1 = queue.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        DelayedTimer localDelayedTimer3 = (DelayedTimer)((Iterator)localObject1).next();
        localDelayedTimer3.getTimer().cancelEvent();
      }
      throw localThreadDeath;
    }
    finally
    {
      running = false;
      runningLock.unlock();
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TimerQueue (");
    int i = 1;
    Iterator localIterator = queue.iterator();
    while (localIterator.hasNext())
    {
      DelayedTimer localDelayedTimer = (DelayedTimer)localIterator.next();
      if (i == 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(localDelayedTimer.getTimer().toString());
      i = 0;
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  private static long now()
  {
    return System.nanoTime() - NANO_ORIGIN;
  }
  
  static class DelayedTimer
    implements Delayed
  {
    private static final AtomicLong sequencer = new AtomicLong(0L);
    private final long sequenceNumber;
    private volatile long time;
    private final Timer timer;
    
    DelayedTimer(Timer paramTimer, long paramLong)
    {
      timer = paramTimer;
      time = paramLong;
      sequenceNumber = sequencer.getAndIncrement();
    }
    
    public final long getDelay(TimeUnit paramTimeUnit)
    {
      return paramTimeUnit.convert(time - TimerQueue.access$000(), TimeUnit.NANOSECONDS);
    }
    
    final void setTime(long paramLong)
    {
      time = paramLong;
    }
    
    final Timer getTimer()
    {
      return timer;
    }
    
    public int compareTo(Delayed paramDelayed)
    {
      if (paramDelayed == this) {
        return 0;
      }
      if ((paramDelayed instanceof DelayedTimer))
      {
        DelayedTimer localDelayedTimer = (DelayedTimer)paramDelayed;
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
      return l1 < 0L ? -1 : l1 == 0L ? 0 : 1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\TimerQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */