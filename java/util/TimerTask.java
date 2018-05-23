package java.util;

public abstract class TimerTask
  implements Runnable
{
  final Object lock = new Object();
  int state = 0;
  static final int VIRGIN = 0;
  static final int SCHEDULED = 1;
  static final int EXECUTED = 2;
  static final int CANCELLED = 3;
  long nextExecutionTime;
  long period = 0L;
  
  protected TimerTask() {}
  
  public abstract void run();
  
  public boolean cancel()
  {
    synchronized (lock)
    {
      boolean bool = state == 1;
      state = 3;
      return bool;
    }
  }
  
  public long scheduledExecutionTime()
  {
    synchronized (lock)
    {
      return period < 0L ? nextExecutionTime + period : nextExecutionTime - period;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\TimerTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */