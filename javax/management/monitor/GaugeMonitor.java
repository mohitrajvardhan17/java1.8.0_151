package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class GaugeMonitor
  extends Monitor
  implements GaugeMonitorMBean
{
  private Number highThreshold = INTEGER_ZERO;
  private Number lowThreshold = INTEGER_ZERO;
  private boolean notifyHigh = false;
  private boolean notifyLow = false;
  private boolean differenceMode = false;
  private static final String[] types = { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.error.threshold", "jmx.monitor.gauge.high", "jmx.monitor.gauge.low" };
  private static final MBeanNotificationInfo[] notifsInfo = { new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the GaugeMonitor MBean") };
  private static final int RISING = 0;
  private static final int FALLING = 1;
  private static final int RISING_OR_FALLING = 2;
  
  public GaugeMonitor() {}
  
  public synchronized void start()
  {
    if (isActive())
    {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, GaugeMonitor.class.getName(), "start", "the monitor is already active");
      return;
    }
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      GaugeMonitorObservedObject localGaugeMonitorObservedObject = (GaugeMonitorObservedObject)localObservedObject;
      localGaugeMonitorObservedObject.setStatus(2);
      localGaugeMonitorObservedObject.setPreviousScanGauge(null);
    }
    doStart();
  }
  
  public synchronized void stop()
  {
    doStop();
  }
  
  public synchronized Number getDerivedGauge(ObjectName paramObjectName)
  {
    return (Number)super.getDerivedGauge(paramObjectName);
  }
  
  public synchronized long getDerivedGaugeTimeStamp(ObjectName paramObjectName)
  {
    return super.getDerivedGaugeTimeStamp(paramObjectName);
  }
  
  @Deprecated
  public synchronized Number getDerivedGauge()
  {
    if (observedObjects.isEmpty()) {
      return null;
    }
    return (Number)((Monitor.ObservedObject)observedObjects.get(0)).getDerivedGauge();
  }
  
  @Deprecated
  public synchronized long getDerivedGaugeTimeStamp()
  {
    if (observedObjects.isEmpty()) {
      return 0L;
    }
    return ((Monitor.ObservedObject)observedObjects.get(0)).getDerivedGaugeTimeStamp();
  }
  
  public synchronized Number getHighThreshold()
  {
    return highThreshold;
  }
  
  public synchronized Number getLowThreshold()
  {
    return lowThreshold;
  }
  
  public synchronized void setThresholds(Number paramNumber1, Number paramNumber2)
    throws IllegalArgumentException
  {
    if ((paramNumber1 == null) || (paramNumber2 == null)) {
      throw new IllegalArgumentException("Null threshold value");
    }
    if (paramNumber1.getClass() != paramNumber2.getClass()) {
      throw new IllegalArgumentException("Different type threshold values");
    }
    if (isFirstStrictlyGreaterThanLast(paramNumber2, paramNumber1, paramNumber1.getClass().getName())) {
      throw new IllegalArgumentException("High threshold less than low threshold");
    }
    if ((highThreshold.equals(paramNumber1)) && (lowThreshold.equals(paramNumber2))) {
      return;
    }
    highThreshold = paramNumber1;
    lowThreshold = paramNumber2;
    int i = 0;
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      resetAlreadyNotified(localObservedObject, i++, 16);
      GaugeMonitorObservedObject localGaugeMonitorObservedObject = (GaugeMonitorObservedObject)localObservedObject;
      localGaugeMonitorObservedObject.setStatus(2);
    }
  }
  
  public synchronized boolean getNotifyHigh()
  {
    return notifyHigh;
  }
  
  public synchronized void setNotifyHigh(boolean paramBoolean)
  {
    if (notifyHigh == paramBoolean) {
      return;
    }
    notifyHigh = paramBoolean;
  }
  
  public synchronized boolean getNotifyLow()
  {
    return notifyLow;
  }
  
  public synchronized void setNotifyLow(boolean paramBoolean)
  {
    if (notifyLow == paramBoolean) {
      return;
    }
    notifyLow = paramBoolean;
  }
  
  public synchronized boolean getDifferenceMode()
  {
    return differenceMode;
  }
  
  public synchronized void setDifferenceMode(boolean paramBoolean)
  {
    if (differenceMode == paramBoolean) {
      return;
    }
    differenceMode = paramBoolean;
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      GaugeMonitorObservedObject localGaugeMonitorObservedObject = (GaugeMonitorObservedObject)localObservedObject;
      localGaugeMonitorObservedObject.setStatus(2);
      localGaugeMonitorObservedObject.setPreviousScanGauge(null);
    }
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return (MBeanNotificationInfo[])notifsInfo.clone();
  }
  
  private synchronized boolean updateDerivedGauge(Object paramObject, GaugeMonitorObservedObject paramGaugeMonitorObservedObject)
  {
    boolean bool;
    if (differenceMode)
    {
      if (paramGaugeMonitorObservedObject.getPreviousScanGauge() != null)
      {
        setDerivedGaugeWithDifference((Number)paramObject, paramGaugeMonitorObservedObject);
        bool = true;
      }
      else
      {
        bool = false;
      }
      paramGaugeMonitorObservedObject.setPreviousScanGauge((Number)paramObject);
    }
    else
    {
      paramGaugeMonitorObservedObject.setDerivedGauge((Number)paramObject);
      bool = true;
    }
    return bool;
  }
  
  private synchronized MonitorNotification updateNotifications(GaugeMonitorObservedObject paramGaugeMonitorObservedObject)
  {
    MonitorNotification localMonitorNotification = null;
    if (paramGaugeMonitorObservedObject.getStatus() == 2)
    {
      if (isFirstGreaterThanLast((Number)paramGaugeMonitorObservedObject.getDerivedGauge(), highThreshold, paramGaugeMonitorObservedObject.getType()))
      {
        if (notifyHigh) {
          localMonitorNotification = new MonitorNotification("jmx.monitor.gauge.high", this, 0L, 0L, "", null, null, null, highThreshold);
        }
        paramGaugeMonitorObservedObject.setStatus(1);
      }
      else if (isFirstGreaterThanLast(lowThreshold, (Number)paramGaugeMonitorObservedObject.getDerivedGauge(), paramGaugeMonitorObservedObject.getType()))
      {
        if (notifyLow) {
          localMonitorNotification = new MonitorNotification("jmx.monitor.gauge.low", this, 0L, 0L, "", null, null, null, lowThreshold);
        }
        paramGaugeMonitorObservedObject.setStatus(0);
      }
    }
    else if (paramGaugeMonitorObservedObject.getStatus() == 0)
    {
      if (isFirstGreaterThanLast((Number)paramGaugeMonitorObservedObject.getDerivedGauge(), highThreshold, paramGaugeMonitorObservedObject.getType()))
      {
        if (notifyHigh) {
          localMonitorNotification = new MonitorNotification("jmx.monitor.gauge.high", this, 0L, 0L, "", null, null, null, highThreshold);
        }
        paramGaugeMonitorObservedObject.setStatus(1);
      }
    }
    else if ((paramGaugeMonitorObservedObject.getStatus() == 1) && (isFirstGreaterThanLast(lowThreshold, (Number)paramGaugeMonitorObservedObject.getDerivedGauge(), paramGaugeMonitorObservedObject.getType())))
    {
      if (notifyLow) {
        localMonitorNotification = new MonitorNotification("jmx.monitor.gauge.low", this, 0L, 0L, "", null, null, null, lowThreshold);
      }
      paramGaugeMonitorObservedObject.setStatus(0);
    }
    return localMonitorNotification;
  }
  
  private synchronized void setDerivedGaugeWithDifference(Number paramNumber, GaugeMonitorObservedObject paramGaugeMonitorObservedObject)
  {
    Number localNumber = paramGaugeMonitorObservedObject.getPreviousScanGauge();
    Object localObject;
    switch (paramGaugeMonitorObservedObject.getType())
    {
    case INTEGER: 
      localObject = Integer.valueOf(((Integer)paramNumber).intValue() - ((Integer)localNumber).intValue());
      break;
    case BYTE: 
      localObject = Byte.valueOf((byte)(((Byte)paramNumber).byteValue() - ((Byte)localNumber).byteValue()));
      break;
    case SHORT: 
      localObject = Short.valueOf((short)(((Short)paramNumber).shortValue() - ((Short)localNumber).shortValue()));
      break;
    case LONG: 
      localObject = Long.valueOf(((Long)paramNumber).longValue() - ((Long)localNumber).longValue());
      break;
    case FLOAT: 
      localObject = Float.valueOf(((Float)paramNumber).floatValue() - ((Float)localNumber).floatValue());
      break;
    case DOUBLE: 
      localObject = Double.valueOf(((Double)paramNumber).doubleValue() - ((Double)localNumber).doubleValue());
      break;
    default: 
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "setDerivedGaugeWithDifference", "the threshold type is invalid");
      return;
    }
    paramGaugeMonitorObservedObject.setDerivedGauge(localObject);
  }
  
  private boolean isFirstGreaterThanLast(Number paramNumber1, Number paramNumber2, Monitor.NumericalType paramNumericalType)
  {
    switch (paramNumericalType)
    {
    case INTEGER: 
    case BYTE: 
    case SHORT: 
    case LONG: 
      return paramNumber1.longValue() >= paramNumber2.longValue();
    case FLOAT: 
    case DOUBLE: 
      return paramNumber1.doubleValue() >= paramNumber2.doubleValue();
    }
    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "isFirstGreaterThanLast", "the threshold type is invalid");
    return false;
  }
  
  private boolean isFirstStrictlyGreaterThanLast(Number paramNumber1, Number paramNumber2, String paramString)
  {
    if ((paramString.equals("java.lang.Integer")) || (paramString.equals("java.lang.Byte")) || (paramString.equals("java.lang.Short")) || (paramString.equals("java.lang.Long"))) {
      return paramNumber1.longValue() > paramNumber2.longValue();
    }
    if ((paramString.equals("java.lang.Float")) || (paramString.equals("java.lang.Double"))) {
      return paramNumber1.doubleValue() > paramNumber2.doubleValue();
    }
    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "isFirstStrictlyGreaterThanLast", "the threshold type is invalid");
    return false;
  }
  
  Monitor.ObservedObject createObservedObject(ObjectName paramObjectName)
  {
    GaugeMonitorObservedObject localGaugeMonitorObservedObject = new GaugeMonitorObservedObject(paramObjectName);
    localGaugeMonitorObservedObject.setStatus(2);
    localGaugeMonitorObservedObject.setPreviousScanGauge(null);
    return localGaugeMonitorObservedObject;
  }
  
  synchronized boolean isComparableTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    GaugeMonitorObservedObject localGaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramObjectName);
    if (localGaugeMonitorObservedObject == null) {
      return false;
    }
    if ((paramComparable instanceof Integer)) {
      localGaugeMonitorObservedObject.setType(Monitor.NumericalType.INTEGER);
    } else if ((paramComparable instanceof Byte)) {
      localGaugeMonitorObservedObject.setType(Monitor.NumericalType.BYTE);
    } else if ((paramComparable instanceof Short)) {
      localGaugeMonitorObservedObject.setType(Monitor.NumericalType.SHORT);
    } else if ((paramComparable instanceof Long)) {
      localGaugeMonitorObservedObject.setType(Monitor.NumericalType.LONG);
    } else if ((paramComparable instanceof Float)) {
      localGaugeMonitorObservedObject.setType(Monitor.NumericalType.FLOAT);
    } else if ((paramComparable instanceof Double)) {
      localGaugeMonitorObservedObject.setType(Monitor.NumericalType.DOUBLE);
    } else {
      return false;
    }
    return true;
  }
  
  synchronized Comparable<?> getDerivedGaugeFromComparable(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    GaugeMonitorObservedObject localGaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramObjectName);
    if (localGaugeMonitorObservedObject == null) {
      return null;
    }
    localGaugeMonitorObservedObject.setDerivedGaugeValid(updateDerivedGauge(paramComparable, localGaugeMonitorObservedObject));
    return (Comparable)localGaugeMonitorObservedObject.getDerivedGauge();
  }
  
  synchronized void onErrorNotification(MonitorNotification paramMonitorNotification)
  {
    GaugeMonitorObservedObject localGaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramMonitorNotification.getObservedObject());
    if (localGaugeMonitorObservedObject == null) {
      return;
    }
    localGaugeMonitorObservedObject.setStatus(2);
    localGaugeMonitorObservedObject.setPreviousScanGauge(null);
  }
  
  synchronized MonitorNotification buildAlarmNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    GaugeMonitorObservedObject localGaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramObjectName);
    if (localGaugeMonitorObservedObject == null) {
      return null;
    }
    MonitorNotification localMonitorNotification;
    if (localGaugeMonitorObservedObject.getDerivedGaugeValid()) {
      localMonitorNotification = updateNotifications(localGaugeMonitorObservedObject);
    } else {
      localMonitorNotification = null;
    }
    return localMonitorNotification;
  }
  
  synchronized boolean isThresholdTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    GaugeMonitorObservedObject localGaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramObjectName);
    if (localGaugeMonitorObservedObject == null) {
      return false;
    }
    Class localClass = classForType(localGaugeMonitorObservedObject.getType());
    return (isValidForType(highThreshold, localClass)) && (isValidForType(lowThreshold, localClass));
  }
  
  static class GaugeMonitorObservedObject
    extends Monitor.ObservedObject
  {
    private boolean derivedGaugeValid;
    private Monitor.NumericalType type;
    private Number previousScanGauge;
    private int status;
    
    public GaugeMonitorObservedObject(ObjectName paramObjectName)
    {
      super();
    }
    
    public final synchronized boolean getDerivedGaugeValid()
    {
      return derivedGaugeValid;
    }
    
    public final synchronized void setDerivedGaugeValid(boolean paramBoolean)
    {
      derivedGaugeValid = paramBoolean;
    }
    
    public final synchronized Monitor.NumericalType getType()
    {
      return type;
    }
    
    public final synchronized void setType(Monitor.NumericalType paramNumericalType)
    {
      type = paramNumericalType;
    }
    
    public final synchronized Number getPreviousScanGauge()
    {
      return previousScanGauge;
    }
    
    public final synchronized void setPreviousScanGauge(Number paramNumber)
    {
      previousScanGauge = paramNumber;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\monitor\GaugeMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */