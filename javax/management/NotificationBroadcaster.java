package javax.management;

public abstract interface NotificationBroadcaster
{
  public abstract void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws IllegalArgumentException;
  
  public abstract void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException;
  
  public abstract MBeanNotificationInfo[] getNotificationInfo();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\NotificationBroadcaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */