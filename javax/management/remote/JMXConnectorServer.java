package javax.management.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public abstract class JMXConnectorServer
  extends NotificationBroadcasterSupport
  implements JMXConnectorServerMBean, MBeanRegistration, JMXAddressable
{
  public static final String AUTHENTICATOR = "jmx.remote.authenticator";
  private MBeanServer mbeanServer = null;
  private ObjectName myName;
  private final List<String> connectionIds = new ArrayList();
  private static final int[] sequenceNumberLock = new int[0];
  private static long sequenceNumber;
  
  public JMXConnectorServer()
  {
    this(null);
  }
  
  public JMXConnectorServer(MBeanServer paramMBeanServer)
  {
    mbeanServer = paramMBeanServer;
  }
  
  public synchronized MBeanServer getMBeanServer()
  {
    return mbeanServer;
  }
  
  public synchronized void setMBeanServerForwarder(MBeanServerForwarder paramMBeanServerForwarder)
  {
    if (paramMBeanServerForwarder == null) {
      throw new IllegalArgumentException("Invalid null argument: mbsf");
    }
    if (mbeanServer != null) {
      paramMBeanServerForwarder.setMBeanServer(mbeanServer);
    }
    mbeanServer = paramMBeanServerForwarder;
  }
  
  /* Error */
  public String[] getConnectionIds()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 157	javax/management/remote/JMXConnectorServer:connectionIds	Ljava/util/List;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 157	javax/management/remote/JMXConnectorServer:connectionIds	Ljava/util/List;
    //   11: aload_0
    //   12: getfield 157	javax/management/remote/JMXConnectorServer:connectionIds	Ljava/util/List;
    //   15: invokeinterface 177 1 0
    //   20: anewarray 98	java/lang/String
    //   23: invokeinterface 180 2 0
    //   28: checkcast 90	[Ljava/lang/String;
    //   31: aload_1
    //   32: monitorexit
    //   33: areturn
    //   34: astore_2
    //   35: aload_1
    //   36: monitorexit
    //   37: aload_2
    //   38: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	39	0	this	JMXConnectorServer
    //   5	31	1	Ljava/lang/Object;	Object
    //   34	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	33	34	finally
    //   34	37	34	finally
  }
  
  public JMXConnector toJMXConnector(Map<String, ?> paramMap)
    throws IOException
  {
    if (!isActive()) {
      throw new IllegalStateException("Connector is not active");
    }
    JMXServiceURL localJMXServiceURL = getAddress();
    return JMXConnectorFactory.newJMXConnector(localJMXServiceURL, paramMap);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    String[] arrayOfString = { "jmx.remote.connection.opened", "jmx.remote.connection.closed", "jmx.remote.connection.failed" };
    String str = JMXConnectionNotification.class.getName();
    return new MBeanNotificationInfo[] { new MBeanNotificationInfo(arrayOfString, str, "A client connection has been opened or closed") };
  }
  
  protected void connectionOpened(String paramString1, String paramString2, Object paramObject)
  {
    if (paramString1 == null) {
      throw new NullPointerException("Illegal null argument");
    }
    synchronized (connectionIds)
    {
      connectionIds.add(paramString1);
    }
    sendNotification("jmx.remote.connection.opened", paramString1, paramString2, paramObject);
  }
  
  protected void connectionClosed(String paramString1, String paramString2, Object paramObject)
  {
    if (paramString1 == null) {
      throw new NullPointerException("Illegal null argument");
    }
    synchronized (connectionIds)
    {
      connectionIds.remove(paramString1);
    }
    sendNotification("jmx.remote.connection.closed", paramString1, paramString2, paramObject);
  }
  
  protected void connectionFailed(String paramString1, String paramString2, Object paramObject)
  {
    if (paramString1 == null) {
      throw new NullPointerException("Illegal null argument");
    }
    synchronized (connectionIds)
    {
      connectionIds.remove(paramString1);
    }
    sendNotification("jmx.remote.connection.failed", paramString1, paramString2, paramObject);
  }
  
  private void sendNotification(String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    JMXConnectionNotification localJMXConnectionNotification = new JMXConnectionNotification(paramString1, getNotificationSource(), paramString2, nextSequenceNumber(), paramString3, paramObject);
    sendNotification(localJMXConnectionNotification);
  }
  
  private synchronized Object getNotificationSource()
  {
    if (myName != null) {
      return myName;
    }
    return this;
  }
  
  /* Error */
  private static long nextSequenceNumber()
  {
    // Byte code:
    //   0: getstatic 156	javax/management/remote/JMXConnectorServer:sequenceNumberLock	[I
    //   3: dup
    //   4: astore_0
    //   5: monitorenter
    //   6: getstatic 155	javax/management/remote/JMXConnectorServer:sequenceNumber	J
    //   9: dup2
    //   10: lconst_1
    //   11: ladd
    //   12: putstatic 155	javax/management/remote/JMXConnectorServer:sequenceNumber	J
    //   15: aload_0
    //   16: monitorexit
    //   17: lreturn
    //   18: astore_1
    //   19: aload_0
    //   20: monitorexit
    //   21: aload_1
    //   22: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   4	16	0	Ljava/lang/Object;	Object
    //   18	4	1	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	17	18	finally
    //   18	21	18	finally
  }
  
  public synchronized ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
  {
    if ((paramMBeanServer == null) || (paramObjectName == null)) {
      throw new NullPointerException("Null MBeanServer or ObjectName");
    }
    if (mbeanServer == null)
    {
      mbeanServer = paramMBeanServer;
      myName = paramObjectName;
    }
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public synchronized void preDeregister()
    throws Exception
  {
    if ((myName != null) && (isActive()))
    {
      stop();
      myName = null;
    }
  }
  
  public void postDeregister()
  {
    myName = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXConnectorServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */