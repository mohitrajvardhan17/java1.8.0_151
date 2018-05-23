package sun.misc;

class TimerTickThread
  extends Thread
{
  static final int MAX_POOL_SIZE = 3;
  static int curPoolSize = 0;
  static TimerTickThread pool = null;
  TimerTickThread next = null;
  Timer timer;
  long lastSleepUntil;
  
  TimerTickThread() {}
  
  protected static synchronized TimerTickThread call(Timer paramTimer, long paramLong)
  {
    TimerTickThread localTimerTickThread = pool;
    if (localTimerTickThread == null)
    {
      localTimerTickThread = new TimerTickThread();
      timer = paramTimer;
      lastSleepUntil = paramLong;
      localTimerTickThread.start();
    }
    else
    {
      pool = poolnext;
      timer = paramTimer;
      lastSleepUntil = paramLong;
      synchronized (localTimerTickThread)
      {
        localTimerTickThread.notify();
      }
    }
    return localTimerTickThread;
  }
  
  private boolean returnToPool()
  {
    synchronized (getClass())
    {
      if (curPoolSize >= 3) {
        return false;
      }
      next = pool;
      pool = this;
      curPoolSize += 1;
      timer = null;
    }
    while (timer == null) {
      synchronized (this)
      {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    synchronized (getClass())
    {
      curPoolSize -= 1;
    }
    return true;
  }
  
  public void run()
  {
    do
    {
      timer.owner.tick(timer);
      synchronized (TimerThread.timerThread)
      {
        synchronized (timer)
        {
          if (lastSleepUntil == timer.sleepUntil) {
            TimerThread.requeue(timer);
          }
        }
      }
    } while (returnToPool());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\TimerTickThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */