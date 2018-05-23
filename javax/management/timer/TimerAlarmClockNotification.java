package javax.management.timer;

import javax.management.Notification;

class TimerAlarmClockNotification
  extends Notification
{
  private static final long serialVersionUID = -4841061275673620641L;
  
  public TimerAlarmClockNotification(TimerAlarmClock paramTimerAlarmClock)
  {
    super("", paramTimerAlarmClock, 0L);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\timer\TimerAlarmClockNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */