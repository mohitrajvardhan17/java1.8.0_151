package sun.management;

import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

class MemoryManagerImpl
  extends NotificationEmitterSupport
  implements MemoryManagerMXBean
{
  private final String name;
  private final boolean isValid;
  private MemoryPoolMXBean[] pools;
  private MBeanNotificationInfo[] notifInfo = null;
  
  MemoryManagerImpl(String paramString)
  {
    name = paramString;
    isValid = true;
    pools = null;
  }
  
  public String getName()
  {
    return name;
  }
  
  public boolean isValid()
  {
    return isValid;
  }
  
  public String[] getMemoryPoolNames()
  {
    MemoryPoolMXBean[] arrayOfMemoryPoolMXBean = getMemoryPools();
    String[] arrayOfString = new String[arrayOfMemoryPoolMXBean.length];
    for (int i = 0; i < arrayOfMemoryPoolMXBean.length; i++) {
      arrayOfString[i] = arrayOfMemoryPoolMXBean[i].getName();
    }
    return arrayOfString;
  }
  
  synchronized MemoryPoolMXBean[] getMemoryPools()
  {
    if (pools == null) {
      pools = getMemoryPools0();
    }
    return pools;
  }
  
  private native MemoryPoolMXBean[] getMemoryPools0();
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    synchronized (this)
    {
      if (notifInfo == null) {
        notifInfo = new MBeanNotificationInfo[0];
      }
    }
    return notifInfo;
  }
  
  public ObjectName getObjectName()
  {
    return Util.newObjectName("java.lang:type=MemoryManager", getName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\MemoryManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */