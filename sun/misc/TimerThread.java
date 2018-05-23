package sun.misc;

import java.io.PrintStream;

class TimerThread
  extends Thread
{
  public static boolean debug = false;
  static TimerThread timerThread;
  static boolean notified = false;
  static Timer timerQueue = null;
  
  protected TimerThread()
  {
    super("TimerThread");
    timerThread = this;
    start();
  }
  
  public synchronized void run()
  {
    for (;;)
    {
      if (timerQueue == null)
      {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException1) {}
      }
      else
      {
        notified = false;
        long l1 = timerQueuesleepUntil - System.currentTimeMillis();
        if (l1 > 0L) {
          try
          {
            wait(l1);
          }
          catch (InterruptedException localInterruptedException2) {}
        }
        if (!notified)
        {
          Timer localTimer = timerQueue;
          timerQueue = timerQueuenext;
          TimerTickThread localTimerTickThread = TimerTickThread.call(localTimer, sleepUntil);
          if (debug)
          {
            long l2 = System.currentTimeMillis() - sleepUntil;
            System.out.println("tick(" + localTimerTickThread.getName() + "," + interval + "," + l2 + ")");
            if (l2 > 250L) {
              System.out.println("*** BIG DELAY ***");
            }
          }
        }
      }
    }
  }
  
  protected static void enqueue(Timer paramTimer)
  {
    Object localObject = null;
    Timer localTimer = timerQueue;
    if ((localTimer == null) || (sleepUntil <= sleepUntil))
    {
      next = timerQueue;
      timerQueue = paramTimer;
      notified = true;
      timerThread.notify();
    }
    else
    {
      do
      {
        localObject = localTimer;
        localTimer = next;
      } while ((localTimer != null) && (sleepUntil > sleepUntil));
      next = localTimer;
      next = paramTimer;
    }
    if (debug)
    {
      long l1 = System.currentTimeMillis();
      System.out.print(Thread.currentThread().getName() + ": enqueue " + interval + ": ");
      for (localTimer = timerQueue; localTimer != null; localTimer = next)
      {
        long l2 = sleepUntil - l1;
        System.out.print(interval + "(" + l2 + ") ");
      }
      System.out.println();
    }
  }
  
  protected static boolean dequeue(Timer paramTimer)
  {
    Object localObject = null;
    for (Timer localTimer = timerQueue; (localTimer != null) && (localTimer != paramTimer); localTimer = next) {
      localObject = localTimer;
    }
    if (localTimer == null)
    {
      if (debug) {
        System.out.println(Thread.currentThread().getName() + ": dequeue " + interval + ": no-op");
      }
      return false;
    }
    if (localObject == null)
    {
      timerQueue = next;
      notified = true;
      timerThread.notify();
    }
    else
    {
      next = next;
    }
    next = null;
    if (debug)
    {
      long l1 = System.currentTimeMillis();
      System.out.print(Thread.currentThread().getName() + ": dequeue " + interval + ": ");
      for (localTimer = timerQueue; localTimer != null; localTimer = next)
      {
        long l2 = sleepUntil - l1;
        System.out.print(interval + "(" + l2 + ") ");
      }
      System.out.println();
    }
    return true;
  }
  
  protected static void requeue(Timer paramTimer)
  {
    if (!stopped)
    {
      long l = System.currentTimeMillis();
      if (regular) {
        sleepUntil += interval;
      } else {
        sleepUntil = (l + interval);
      }
      enqueue(paramTimer);
    }
    else if (debug)
    {
      System.out.println(Thread.currentThread().getName() + ": requeue " + interval + ": no-op");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\TimerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */