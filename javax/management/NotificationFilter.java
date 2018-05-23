package javax.management;

import java.io.Serializable;

public abstract interface NotificationFilter
  extends Serializable
{
  public abstract boolean isNotificationEnabled(Notification paramNotification);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\NotificationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */