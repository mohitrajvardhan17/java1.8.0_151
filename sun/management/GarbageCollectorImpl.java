package sun.management;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Iterator;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

class GarbageCollectorImpl
  extends MemoryManagerImpl
  implements GarbageCollectorMXBean
{
  private String[] poolNames = null;
  private GcInfoBuilder gcInfoBuilder;
  private static final String notifName = "javax.management.Notification";
  private static final String[] gcNotifTypes = { "com.sun.management.gc.notification" };
  private static long seqNumber = 0L;
  
  GarbageCollectorImpl(String paramString)
  {
    super(paramString);
  }
  
  public native long getCollectionCount();
  
  public native long getCollectionTime();
  
  synchronized String[] getAllPoolNames()
  {
    if (poolNames == null)
    {
      List localList = ManagementFactory.getMemoryPoolMXBeans();
      poolNames = new String[localList.size()];
      int i = 0;
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        MemoryPoolMXBean localMemoryPoolMXBean = (MemoryPoolMXBean)localIterator.next();
        poolNames[(i++)] = localMemoryPoolMXBean.getName();
      }
    }
    return poolNames;
  }
  
  private synchronized GcInfoBuilder getGcInfoBuilder()
  {
    if (gcInfoBuilder == null) {
      gcInfoBuilder = new GcInfoBuilder(this, getAllPoolNames());
    }
    return gcInfoBuilder;
  }
  
  public GcInfo getLastGcInfo()
  {
    GcInfo localGcInfo = getGcInfoBuilder().getLastGcInfo();
    return localGcInfo;
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return new MBeanNotificationInfo[] { new MBeanNotificationInfo(gcNotifTypes, "javax.management.Notification", "GC Notification") };
  }
  
  private static long getNextSeqNumber()
  {
    return ++seqNumber;
  }
  
  void createGCNotification(long paramLong, String paramString1, String paramString2, String paramString3, GcInfo paramGcInfo)
  {
    if (!hasListeners()) {
      return;
    }
    Notification localNotification = new Notification("com.sun.management.gc.notification", getObjectName(), getNextSeqNumber(), paramLong, paramString1);
    GarbageCollectionNotificationInfo localGarbageCollectionNotificationInfo = new GarbageCollectionNotificationInfo(paramString1, paramString2, paramString3, paramGcInfo);
    CompositeData localCompositeData = GarbageCollectionNotifInfoCompositeData.toCompositeData(localGarbageCollectionNotificationInfo);
    localNotification.setUserData(localCompositeData);
    sendNotification(localNotification);
  }
  
  public synchronized void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    boolean bool1 = hasListeners();
    super.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if ((!bool1) && (bool2)) {
      setNotificationEnabled(this, true);
    }
  }
  
  public synchronized void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener);
    boolean bool2 = hasListeners();
    if ((bool1) && (!bool2)) {
      setNotificationEnabled(this, false);
    }
  }
  
  public synchronized void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException
  {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if ((bool1) && (!bool2)) {
      setNotificationEnabled(this, false);
    }
  }
  
  public ObjectName getObjectName()
  {
    return Util.newObjectName("java.lang:type=GarbageCollector", getName());
  }
  
  native void setNotificationEnabled(GarbageCollectorMXBean paramGarbageCollectorMXBean, boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\GarbageCollectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */