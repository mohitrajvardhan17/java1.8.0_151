package java.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Timer
{
  private final TaskQueue queue = new TaskQueue();
  private final TimerThread thread = new TimerThread(queue);
  private final Object threadReaper = new Object()
  {
    protected void finalize()
      throws Throwable
    {
      synchronized (queue)
      {
        thread.newTasksMayBeScheduled = false;
        queue.notify();
      }
    }
  };
  private static final AtomicInteger nextSerialNumber = new AtomicInteger(0);
  
  private static int serialNumber()
  {
    return nextSerialNumber.getAndIncrement();
  }
  
  public Timer()
  {
    this("Timer-" + serialNumber());
  }
  
  public Timer(boolean paramBoolean)
  {
    this("Timer-" + serialNumber(), paramBoolean);
  }
  
  public Timer(String paramString)
  {
    thread.setName(paramString);
    thread.start();
  }
  
  public Timer(String paramString, boolean paramBoolean)
  {
    thread.setName(paramString);
    thread.setDaemon(paramBoolean);
    thread.start();
  }
  
  public void schedule(TimerTask paramTimerTask, long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative delay.");
    }
    sched(paramTimerTask, System.currentTimeMillis() + paramLong, 0L);
  }
  
  public void schedule(TimerTask paramTimerTask, Date paramDate)
  {
    sched(paramTimerTask, paramDate.getTime(), 0L);
  }
  
  public void schedule(TimerTask paramTimerTask, long paramLong1, long paramLong2)
  {
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("Negative delay.");
    }
    if (paramLong2 <= 0L) {
      throw new IllegalArgumentException("Non-positive period.");
    }
    sched(paramTimerTask, System.currentTimeMillis() + paramLong1, -paramLong2);
  }
  
  public void schedule(TimerTask paramTimerTask, Date paramDate, long paramLong)
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("Non-positive period.");
    }
    sched(paramTimerTask, paramDate.getTime(), -paramLong);
  }
  
  public void scheduleAtFixedRate(TimerTask paramTimerTask, long paramLong1, long paramLong2)
  {
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("Negative delay.");
    }
    if (paramLong2 <= 0L) {
      throw new IllegalArgumentException("Non-positive period.");
    }
    sched(paramTimerTask, System.currentTimeMillis() + paramLong1, paramLong2);
  }
  
  public void scheduleAtFixedRate(TimerTask paramTimerTask, Date paramDate, long paramLong)
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("Non-positive period.");
    }
    sched(paramTimerTask, paramDate.getTime(), paramLong);
  }
  
  private void sched(TimerTask paramTimerTask, long paramLong1, long paramLong2)
  {
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("Illegal execution time.");
    }
    if (Math.abs(paramLong2) > 4611686018427387903L) {
      paramLong2 >>= 1;
    }
    synchronized (queue)
    {
      if (!thread.newTasksMayBeScheduled) {
        throw new IllegalStateException("Timer already cancelled.");
      }
      synchronized (lock)
      {
        if (state != 0) {
          throw new IllegalStateException("Task already scheduled or cancelled");
        }
        nextExecutionTime = paramLong1;
        period = paramLong2;
        state = 1;
      }
      queue.add(paramTimerTask);
      if (queue.getMin() == paramTimerTask) {
        queue.notify();
      }
    }
  }
  
  public void cancel()
  {
    synchronized (queue)
    {
      thread.newTasksMayBeScheduled = false;
      queue.clear();
      queue.notify();
    }
  }
  
  public int purge()
  {
    int i = 0;
    synchronized (queue)
    {
      for (int j = queue.size(); j > 0; j--) {
        if (queue.get(j).state == 3)
        {
          queue.quickRemove(j);
          i++;
        }
      }
      if (i != 0) {
        queue.heapify();
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */