package javax.management;

public abstract interface NotificationEmitter
  extends NotificationBroadcaster
{
  public abstract void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\NotificationEmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */