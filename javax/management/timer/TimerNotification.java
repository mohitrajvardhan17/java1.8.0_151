package javax.management.timer;

import javax.management.Notification;

public class TimerNotification
  extends Notification
{
  private static final long serialVersionUID = 1798492029603825750L;
  private Integer notificationID;
  
  public TimerNotification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, Integer paramInteger)
  {
    super(paramString1, paramObject, paramLong1, paramLong2, paramString2);
    notificationID = paramInteger;
  }
  
  public Integer getNotificationID()
  {
    return notificationID;
  }
  
  Object cloneTimerNotification()
  {
    TimerNotification localTimerNotification = new TimerNotification(getType(), getSource(), getSequenceNumber(), getTimeStamp(), getMessage(), notificationID);
    localTimerNotification.setUserData(getUserData());
    return localTimerNotification;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\timer\TimerNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */