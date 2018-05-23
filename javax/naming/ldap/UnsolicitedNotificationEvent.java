package javax.naming.ldap;

import java.util.EventObject;

public class UnsolicitedNotificationEvent
  extends EventObject
{
  private UnsolicitedNotification notice;
  private static final long serialVersionUID = -2382603380799883705L;
  
  public UnsolicitedNotificationEvent(Object paramObject, UnsolicitedNotification paramUnsolicitedNotification)
  {
    super(paramObject);
    notice = paramUnsolicitedNotification;
  }
  
  public UnsolicitedNotification getNotification()
  {
    return notice;
  }
  
  public void dispatch(UnsolicitedNotificationListener paramUnsolicitedNotificationListener)
  {
    paramUnsolicitedNotificationListener.notificationReceived(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\UnsolicitedNotificationEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */