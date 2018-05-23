package javax.naming.ldap;

import javax.naming.event.NamingListener;

public abstract interface UnsolicitedNotificationListener
  extends NamingListener
{
  public abstract void notificationReceived(UnsolicitedNotificationEvent paramUnsolicitedNotificationEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\UnsolicitedNotificationListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */