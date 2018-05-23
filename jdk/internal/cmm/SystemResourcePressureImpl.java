package jdk.internal.cmm;

import java.lang.management.ManagementPermission;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import jdk.management.cmm.SystemResourcePressureMXBean;

public final class SystemResourcePressureImpl
  extends NotificationBroadcasterSupport
  implements SystemResourcePressureMXBean
{
  private static final int MIN_PRESSURE_LEVEL = 0;
  private static final int MAX_PRESSURE_LEVEL = 10;
  public static final String RESOURCE_PRESSURE_MXBEAN_NAME = "com.oracle.management:type=ResourcePressureMBean";
  private static final String MEM_PRESSURE_ATTRIBUTE_NAME = "MemoryPressure";
  private long notifSeqNum;
  private static ManagementPermission controlPermission = new ManagementPermission("control");
  
  public SystemResourcePressureImpl()
  {
    super(new MBeanNotificationInfo[] { new MBeanNotificationInfo(new String[] { "jmx.attribute.change" }, AttributeChangeNotification.class.getName(), "Notification that Memory pressure level has changed") });
  }
  
  public synchronized int getMemoryPressure()
  {
    return getVmMemoryPressure();
  }
  
  public void setMemoryPressure(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 10)) {
      throw new IllegalArgumentException("Invalid pressure level: " + paramInt);
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(controlPermission);
    }
    AttributeChangeNotification localAttributeChangeNotification;
    synchronized (this)
    {
      int i = setVmMemoryPressure(paramInt);
      if (paramInt == i) {
        return;
      }
      localAttributeChangeNotification = new AttributeChangeNotification(this, ++notifSeqNum, System.currentTimeMillis(), "Memory pressure level change detected", "MemoryPressure", "int", Integer.valueOf(i), Integer.valueOf(paramInt));
    }
    sendNotification(localAttributeChangeNotification);
  }
  
  private native int setVmMemoryPressure(int paramInt);
  
  private native int getVmMemoryPressure();
  
  public ObjectName getObjectName()
  {
    try
    {
      return ObjectName.getInstance("com.oracle.management:type=ResourcePressureMBean");
    }
    catch (MalformedObjectNameException localMalformedObjectNameException)
    {
      throw new InternalError();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\cmm\SystemResourcePressureImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */