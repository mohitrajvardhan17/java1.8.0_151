package javax.management.remote;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.security.auth.Subject;

public abstract interface JMXConnector
  extends Closeable
{
  public static final String CREDENTIALS = "jmx.remote.credentials";
  
  public abstract void connect()
    throws IOException;
  
  public abstract void connect(Map<String, ?> paramMap)
    throws IOException;
  
  public abstract MBeanServerConnection getMBeanServerConnection()
    throws IOException;
  
  public abstract MBeanServerConnection getMBeanServerConnection(Subject paramSubject)
    throws IOException;
  
  public abstract void close()
    throws IOException;
  
  public abstract void addConnectionNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject);
  
  public abstract void removeConnectionNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException;
  
  public abstract void removeConnectionNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException;
  
  public abstract String getConnectionId()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */