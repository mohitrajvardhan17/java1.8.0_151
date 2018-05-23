package javax.management.timer;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class TimerAlarmClock
  extends TimerTask
{
  Timer listener = null;
  long timeout = 10000L;
  Date next = null;
  
  public TimerAlarmClock(Timer paramTimer, long paramLong)
  {
    listener = paramTimer;
    timeout = Math.max(0L, paramLong);
  }
  
  public TimerAlarmClock(Timer paramTimer, Date paramDate)
  {
    listener = paramTimer;
    next = paramDate;
  }
  
  public void run()
  {
    try
    {
      TimerAlarmClockNotification localTimerAlarmClockNotification = new TimerAlarmClockNotification(this);
      listener.notifyAlarmClock(localTimerAlarmClockNotification);
    }
    catch (Exception localException)
    {
      JmxProperties.TIMER_LOGGER.logp(Level.FINEST, Timer.class.getName(), "run", "Got unexpected exception when sending a notification", localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\timer\TimerAlarmClock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */