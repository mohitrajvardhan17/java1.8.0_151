package javax.management;

public class StandardEmitterMBean
  extends StandardMBean
  implements NotificationEmitter
{
  private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
  private final NotificationEmitter emitter;
  private final MBeanNotificationInfo[] notificationInfo;
  
  public <T> StandardEmitterMBean(T paramT, Class<T> paramClass, NotificationEmitter paramNotificationEmitter)
  {
    this(paramT, paramClass, false, paramNotificationEmitter);
  }
  
  public <T> StandardEmitterMBean(T paramT, Class<T> paramClass, boolean paramBoolean, NotificationEmitter paramNotificationEmitter)
  {
    super(paramT, paramClass, paramBoolean);
    if (paramNotificationEmitter == null) {
      throw new IllegalArgumentException("Null emitter");
    }
    emitter = paramNotificationEmitter;
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = paramNotificationEmitter.getNotificationInfo();
    if ((arrayOfMBeanNotificationInfo == null) || (arrayOfMBeanNotificationInfo.length == 0)) {
      notificationInfo = NO_NOTIFICATION_INFO;
    } else {
      notificationInfo = ((MBeanNotificationInfo[])arrayOfMBeanNotificationInfo.clone());
    }
  }
  
  protected StandardEmitterMBean(Class<?> paramClass, NotificationEmitter paramNotificationEmitter)
  {
    this(paramClass, false, paramNotificationEmitter);
  }
  
  protected StandardEmitterMBean(Class<?> paramClass, boolean paramBoolean, NotificationEmitter paramNotificationEmitter)
  {
    super(paramClass, paramBoolean);
    if (paramNotificationEmitter == null) {
      throw new IllegalArgumentException("Null emitter");
    }
    emitter = paramNotificationEmitter;
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = paramNotificationEmitter.getNotificationInfo();
    if ((arrayOfMBeanNotificationInfo == null) || (arrayOfMBeanNotificationInfo.length == 0)) {
      notificationInfo = NO_NOTIFICATION_INFO;
    } else {
      notificationInfo = ((MBeanNotificationInfo[])arrayOfMBeanNotificationInfo.clone());
    }
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    emitter.removeNotificationListener(paramNotificationListener);
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException
  {
    emitter.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    emitter.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    if (notificationInfo == null) {
      return NO_NOTIFICATION_INFO;
    }
    if (notificationInfo.length == 0) {
      return notificationInfo;
    }
    return (MBeanNotificationInfo[])notificationInfo.clone();
  }
  
  public void sendNotification(Notification paramNotification)
  {
    if ((emitter instanceof NotificationBroadcasterSupport))
    {
      ((NotificationBroadcasterSupport)emitter).sendNotification(paramNotification);
    }
    else
    {
      String str = "Cannot sendNotification when emitter is not an instance of NotificationBroadcasterSupport: " + emitter.getClass().getName();
      throw new ClassCastException(str);
    }
  }
  
  MBeanNotificationInfo[] getNotifications(MBeanInfo paramMBeanInfo)
  {
    return getNotificationInfo();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\StandardEmitterMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */