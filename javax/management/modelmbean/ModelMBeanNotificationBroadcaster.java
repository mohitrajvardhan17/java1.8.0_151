package javax.management.modelmbean;

import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.RuntimeOperationsException;

public abstract interface ModelMBeanNotificationBroadcaster
  extends NotificationBroadcaster
{
  public abstract void sendNotification(Notification paramNotification)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract void sendNotification(String paramString)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract void sendAttributeChangeNotification(AttributeChangeNotification paramAttributeChangeNotification)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract void sendAttributeChangeNotification(Attribute paramAttribute1, Attribute paramAttribute2)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract void addAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString, Object paramObject)
    throws MBeanException, RuntimeOperationsException, IllegalArgumentException;
  
  public abstract void removeAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString)
    throws MBeanException, RuntimeOperationsException, ListenerNotFoundException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\modelmbean\ModelMBeanNotificationBroadcaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */