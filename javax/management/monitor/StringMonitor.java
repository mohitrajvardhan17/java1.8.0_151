package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class StringMonitor
  extends Monitor
  implements StringMonitorMBean
{
  private String stringToCompare = "";
  private boolean notifyMatch = false;
  private boolean notifyDiffer = false;
  private static final String[] types = { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.string.matches", "jmx.monitor.string.differs" };
  private static final MBeanNotificationInfo[] notifsInfo = { new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the StringMonitor MBean") };
  private static final int MATCHING = 0;
  private static final int DIFFERING = 1;
  private static final int MATCHING_OR_DIFFERING = 2;
  
  public StringMonitor() {}
  
  public synchronized void start()
  {
    if (isActive())
    {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, StringMonitor.class.getName(), "start", "the monitor is already active");
      return;
    }
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      StringMonitorObservedObject localStringMonitorObservedObject = (StringMonitorObservedObject)localObservedObject;
      localStringMonitorObservedObject.setStatus(2);
    }
    doStart();
  }
  
  public synchronized void stop()
  {
    doStop();
  }
  
  public synchronized String getDerivedGauge(ObjectName paramObjectName)
  {
    return (String)super.getDerivedGauge(paramObjectName);
  }
  
  public synchronized long getDerivedGaugeTimeStamp(ObjectName paramObjectName)
  {
    return super.getDerivedGaugeTimeStamp(paramObjectName);
  }
  
  @Deprecated
  public synchronized String getDerivedGauge()
  {
    if (observedObjects.isEmpty()) {
      return null;
    }
    return (String)((Monitor.ObservedObject)observedObjects.get(0)).getDerivedGauge();
  }
  
  @Deprecated
  public synchronized long getDerivedGaugeTimeStamp()
  {
    if (observedObjects.isEmpty()) {
      return 0L;
    }
    return ((Monitor.ObservedObject)observedObjects.get(0)).getDerivedGaugeTimeStamp();
  }
  
  public synchronized String getStringToCompare()
  {
    return stringToCompare;
  }
  
  public synchronized void setStringToCompare(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Null string to compare");
    }
    if (stringToCompare.equals(paramString)) {
      return;
    }
    stringToCompare = paramString;
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      StringMonitorObservedObject localStringMonitorObservedObject = (StringMonitorObservedObject)localObservedObject;
      localStringMonitorObservedObject.setStatus(2);
    }
  }
  
  public synchronized boolean getNotifyMatch()
  {
    return notifyMatch;
  }
  
  public synchronized void setNotifyMatch(boolean paramBoolean)
  {
    if (notifyMatch == paramBoolean) {
      return;
    }
    notifyMatch = paramBoolean;
  }
  
  public synchronized boolean getNotifyDiffer()
  {
    return notifyDiffer;
  }
  
  public synchronized void setNotifyDiffer(boolean paramBoolean)
  {
    if (notifyDiffer == paramBoolean) {
      return;
    }
    notifyDiffer = paramBoolean;
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return (MBeanNotificationInfo[])notifsInfo.clone();
  }
  
  Monitor.ObservedObject createObservedObject(ObjectName paramObjectName)
  {
    StringMonitorObservedObject localStringMonitorObservedObject = new StringMonitorObservedObject(paramObjectName);
    localStringMonitorObservedObject.setStatus(2);
    return localStringMonitorObservedObject;
  }
  
  synchronized boolean isComparableTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    return (paramComparable instanceof String);
  }
  
  synchronized void onErrorNotification(MonitorNotification paramMonitorNotification)
  {
    StringMonitorObservedObject localStringMonitorObservedObject = (StringMonitorObservedObject)getObservedObject(paramMonitorNotification.getObservedObject());
    if (localStringMonitorObservedObject == null) {
      return;
    }
    localStringMonitorObservedObject.setStatus(2);
  }
  
  synchronized MonitorNotification buildAlarmNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    StringMonitorObservedObject localStringMonitorObservedObject = (StringMonitorObservedObject)getObservedObject(paramObjectName);
    if (localStringMonitorObservedObject == null) {
      return null;
    }
    if (localStringMonitorObservedObject.getStatus() == 2)
    {
      if (localStringMonitorObservedObject.getDerivedGauge().equals(stringToCompare))
      {
        if (notifyMatch)
        {
          str1 = "jmx.monitor.string.matches";
          str2 = "";
          str3 = stringToCompare;
        }
        localStringMonitorObservedObject.setStatus(1);
      }
      else
      {
        if (notifyDiffer)
        {
          str1 = "jmx.monitor.string.differs";
          str2 = "";
          str3 = stringToCompare;
        }
        localStringMonitorObservedObject.setStatus(0);
      }
    }
    else if (localStringMonitorObservedObject.getStatus() == 0)
    {
      if (localStringMonitorObservedObject.getDerivedGauge().equals(stringToCompare))
      {
        if (notifyMatch)
        {
          str1 = "jmx.monitor.string.matches";
          str2 = "";
          str3 = stringToCompare;
        }
        localStringMonitorObservedObject.setStatus(1);
      }
    }
    else if ((localStringMonitorObservedObject.getStatus() == 1) && (!localStringMonitorObservedObject.getDerivedGauge().equals(stringToCompare)))
    {
      if (notifyDiffer)
      {
        str1 = "jmx.monitor.string.differs";
        str2 = "";
        str3 = stringToCompare;
      }
      localStringMonitorObservedObject.setStatus(0);
    }
    return new MonitorNotification(str1, this, 0L, 0L, str2, null, null, null, str3);
  }
  
  static class StringMonitorObservedObject
    extends Monitor.ObservedObject
  {
    private int status;
    
    public StringMonitorObservedObject(ObjectName paramObjectName)
    {
      super();
    }
    
    public final synchronized int getStatus()
    {
      return status;
    }
    
    public final synchronized void setStatus(int paramInt)
    {
      status = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\monitor\StringMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */