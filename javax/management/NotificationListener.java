package javax.management;

import java.util.EventListener;

public abstract interface NotificationListener
  extends EventListener
{
  public abstract void handleNotification(Notification paramNotification, Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\NotificationListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */