package sun.misc;

public class Timer
{
  public Timeable owner;
  long interval;
  long sleepUntil;
  long remainingTime;
  boolean regular;
  boolean stopped;
  Timer next;
  static TimerThread timerThread = null;
  
  public Timer(Timeable paramTimeable, long paramLong)
  {
    owner = paramTimeable;
    interval = paramLong;
    remainingTime = paramLong;
    regular = true;
    sleepUntil = System.currentTimeMillis();
    stopped = true;
    synchronized (getClass())
    {
      if (timerThread == null) {
        timerThread = new TimerThread();
      }
    }
  }
  
  public synchronized boolean isStopped()
  {
    return stopped;
  }
  
  public void stop()
  {
    long l = System.currentTimeMillis();
    synchronized (timerThread)
    {
      synchronized (this)
      {
        if (!stopped)
        {
          TimerThread.dequeue(this);
          remainingTime = Math.max(0L, sleepUntil - l);
          sleepUntil = l;
          stopped = true;
        }
      }
    }
  }
  
  public void cont()
  {
    synchronized (timerThread)
    {
      synchronized (this)
      {
        if (stopped)
        {
          sleepUntil = Math.max(sleepUntil + 1L, System.currentTimeMillis() + remainingTime);
          TimerThread.enqueue(this);
          stopped = false;
        }
      }
    }
  }
  
  public void reset()
  {
    synchronized (timerThread)
    {
      synchronized (this)
      {
        setRemainingTime(interval);
      }
    }
  }
  
  public synchronized long getStopTime()
  {
    return sleepUntil;
  }
  
  public synchronized long getInterval()
  {
    return interval;
  }
  
  public synchronized void setInterval(long paramLong)
  {
    interval = paramLong;
  }
  
  public synchronized long getRemainingTime()
  {
    return remainingTime;
  }
  
  public void setRemainingTime(long paramLong)
  {
    synchronized (timerThread)
    {
      synchronized (this)
      {
        if (stopped)
        {
          remainingTime = paramLong;
        }
        else
        {
          stop();
          remainingTime = paramLong;
          cont();
        }
      }
    }
  }
  
  public synchronized void setRegular(boolean paramBoolean)
  {
    regular = paramBoolean;
  }
  
  protected Thread getTimerThread()
  {
    return TimerThread.timerThread;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */