package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class CounterMonitor
  extends Monitor
  implements CounterMonitorMBean
{
  private Number modulus = INTEGER_ZERO;
  private Number offset = INTEGER_ZERO;
  private boolean notify = false;
  private boolean differenceMode = false;
  private Number initThreshold = INTEGER_ZERO;
  private static final String[] types = { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.error.threshold", "jmx.monitor.counter.threshold" };
  private static final MBeanNotificationInfo[] notifsInfo = { new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the CounterMonitor MBean") };
  
  public CounterMonitor() {}
  
  public synchronized void start()
  {
    if (isActive())
    {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, CounterMonitor.class.getName(), "start", "the monitor is already active");
      return;
    }
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)localObservedObject;
      localCounterMonitorObservedObject.setThreshold(initThreshold);
      localCounterMonitorObservedObject.setModulusExceeded(false);
      localCounterMonitorObservedObject.setEventAlreadyNotified(false);
      localCounterMonitorObservedObject.setPreviousScanCounter(null);
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
  
  public synchronized Number getThreshold(ObjectName paramObjectName)
  {
    CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (localCounterMonitorObservedObject == null) {
      return null;
    }
    if ((offset.longValue() > 0L) && (modulus.longValue() > 0L) && (localCounterMonitorObservedObject.getThreshold().longValue() > modulus.longValue())) {
      return initThreshold;
    }
    return localCounterMonitorObservedObject.getThreshold();
  }
  
  public synchronized Number getInitThreshold()
  {
    return initThreshold;
  }
  
  public synchronized void setInitThreshold(Number paramNumber)
    throws IllegalArgumentException
  {
    if (paramNumber == null) {
      throw new IllegalArgumentException("Null threshold");
    }
    if (paramNumber.longValue() < 0L) {
      throw new IllegalArgumentException("Negative threshold");
    }
    if (initThreshold.equals(paramNumber)) {
      return;
    }
    initThreshold = paramNumber;
    int i = 0;
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      resetAlreadyNotified(localObservedObject, i++, 16);
      CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)localObservedObject;
      localCounterMonitorObservedObject.setThreshold(paramNumber);
      localCounterMonitorObservedObject.setModulusExceeded(false);
      localCounterMonitorObservedObject.setEventAlreadyNotified(false);
    }
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
  
  @Deprecated
  public synchronized Number getThreshold()
  {
    return getThreshold(getObservedObject());
  }
  
  @Deprecated
  public synchronized void setThreshold(Number paramNumber)
    throws IllegalArgumentException
  {
    setInitThreshold(paramNumber);
  }
  
  public synchronized Number getOffset()
  {
    return offset;
  }
  
  public synchronized void setOffset(Number paramNumber)
    throws IllegalArgumentException
  {
    if (paramNumber == null) {
      throw new IllegalArgumentException("Null offset");
    }
    if (paramNumber.longValue() < 0L) {
      throw new IllegalArgumentException("Negative offset");
    }
    if (offset.equals(paramNumber)) {
      return;
    }
    offset = paramNumber;
    int i = 0;
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      resetAlreadyNotified(localObservedObject, i++, 16);
    }
  }
  
  public synchronized Number getModulus()
  {
    return modulus;
  }
  
  public synchronized void setModulus(Number paramNumber)
    throws IllegalArgumentException
  {
    if (paramNumber == null) {
      throw new IllegalArgumentException("Null modulus");
    }
    if (paramNumber.longValue() < 0L) {
      throw new IllegalArgumentException("Negative modulus");
    }
    if (modulus.equals(paramNumber)) {
      return;
    }
    modulus = paramNumber;
    int i = 0;
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
      resetAlreadyNotified(localObservedObject, i++, 16);
      CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)localObservedObject;
      localCounterMonitorObservedObject.setModulusExceeded(false);
    }
  }
  
  public synchronized boolean getNotify()
  {
    return notify;
  }
  
  public synchronized void setNotify(boolean paramBoolean)
  {
    if (notify == paramBoolean) {
      return;
    }
    notify = paramBoolean;
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
      CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)localObservedObject;
      localCounterMonitorObservedObject.setThreshold(initThreshold);
      localCounterMonitorObservedObject.setModulusExceeded(false);
      localCounterMonitorObservedObject.setEventAlreadyNotified(false);
      localCounterMonitorObservedObject.setPreviousScanCounter(null);
    }
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return (MBeanNotificationInfo[])notifsInfo.clone();
  }
  
  private synchronized boolean updateDerivedGauge(Object paramObject, CounterMonitorObservedObject paramCounterMonitorObservedObject)
  {
    boolean bool;
    if (differenceMode)
    {
      if (paramCounterMonitorObservedObject.getPreviousScanCounter() != null)
      {
        setDerivedGaugeWithDifference((Number)paramObject, null, paramCounterMonitorObservedObject);
        if (((Number)paramCounterMonitorObservedObject.getDerivedGauge()).longValue() < 0L)
        {
          if (modulus.longValue() > 0L) {
            setDerivedGaugeWithDifference((Number)paramObject, modulus, paramCounterMonitorObservedObject);
          }
          paramCounterMonitorObservedObject.setThreshold(initThreshold);
          paramCounterMonitorObservedObject.setEventAlreadyNotified(false);
        }
        bool = true;
      }
      else
      {
        bool = false;
      }
      paramCounterMonitorObservedObject.setPreviousScanCounter((Number)paramObject);
    }
    else
    {
      paramCounterMonitorObservedObject.setDerivedGauge((Number)paramObject);
      bool = true;
    }
    return bool;
  }
  
  private synchronized MonitorNotification updateNotifications(CounterMonitorObservedObject paramCounterMonitorObservedObject)
  {
    MonitorNotification localMonitorNotification = null;
    if (!paramCounterMonitorObservedObject.getEventAlreadyNotified())
    {
      if (((Number)paramCounterMonitorObservedObject.getDerivedGauge()).longValue() >= paramCounterMonitorObservedObject.getThreshold().longValue())
      {
        if (notify) {
          localMonitorNotification = new MonitorNotification("jmx.monitor.counter.threshold", this, 0L, 0L, "", null, null, null, paramCounterMonitorObservedObject.getThreshold());
        }
        if (!differenceMode) {
          paramCounterMonitorObservedObject.setEventAlreadyNotified(true);
        }
      }
    }
    else if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("The notification:").append("\n\tNotification observed object = ").append(paramCounterMonitorObservedObject.getObservedObject()).append("\n\tNotification observed attribute = ").append(getObservedAttribute()).append("\n\tNotification threshold level = ").append(paramCounterMonitorObservedObject.getThreshold()).append("\n\tNotification derived gauge = ").append(paramCounterMonitorObservedObject.getDerivedGauge()).append("\nhas already been sent");
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, CounterMonitor.class.getName(), "updateNotifications", localStringBuilder.toString());
    }
    return localMonitorNotification;
  }
  
  private synchronized void updateThreshold(CounterMonitorObservedObject paramCounterMonitorObservedObject)
  {
    if (((Number)paramCounterMonitorObservedObject.getDerivedGauge()).longValue() >= paramCounterMonitorObservedObject.getThreshold().longValue()) {
      if (offset.longValue() > 0L)
      {
        for (long l = paramCounterMonitorObservedObject.getThreshold().longValue(); ((Number)paramCounterMonitorObservedObject.getDerivedGauge()).longValue() >= l; l += offset.longValue()) {}
        switch (paramCounterMonitorObservedObject.getType())
        {
        case INTEGER: 
          paramCounterMonitorObservedObject.setThreshold(Integer.valueOf((int)l));
          break;
        case BYTE: 
          paramCounterMonitorObservedObject.setThreshold(Byte.valueOf((byte)(int)l));
          break;
        case SHORT: 
          paramCounterMonitorObservedObject.setThreshold(Short.valueOf((short)(int)l));
          break;
        case LONG: 
          paramCounterMonitorObservedObject.setThreshold(Long.valueOf(l));
          break;
        default: 
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, CounterMonitor.class.getName(), "updateThreshold", "the threshold type is invalid");
        }
        if ((!differenceMode) && (modulus.longValue() > 0L) && (paramCounterMonitorObservedObject.getThreshold().longValue() > modulus.longValue()))
        {
          paramCounterMonitorObservedObject.setModulusExceeded(true);
          paramCounterMonitorObservedObject.setDerivedGaugeExceeded((Number)paramCounterMonitorObservedObject.getDerivedGauge());
        }
        paramCounterMonitorObservedObject.setEventAlreadyNotified(false);
      }
      else
      {
        paramCounterMonitorObservedObject.setModulusExceeded(true);
        paramCounterMonitorObservedObject.setDerivedGaugeExceeded((Number)paramCounterMonitorObservedObject.getDerivedGauge());
      }
    }
  }
  
  private synchronized void setDerivedGaugeWithDifference(Number paramNumber1, Number paramNumber2, CounterMonitorObservedObject paramCounterMonitorObservedObject)
  {
    long l = paramNumber1.longValue() - paramCounterMonitorObservedObject.getPreviousScanCounter().longValue();
    if (paramNumber2 != null) {
      l += modulus.longValue();
    }
    switch (paramCounterMonitorObservedObject.getType())
    {
    case INTEGER: 
      paramCounterMonitorObservedObject.setDerivedGauge(Integer.valueOf((int)l));
      break;
    case BYTE: 
      paramCounterMonitorObservedObject.setDerivedGauge(Byte.valueOf((byte)(int)l));
      break;
    case SHORT: 
      paramCounterMonitorObservedObject.setDerivedGauge(Short.valueOf((short)(int)l));
      break;
    case LONG: 
      paramCounterMonitorObservedObject.setDerivedGauge(Long.valueOf(l));
      break;
    default: 
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, CounterMonitor.class.getName(), "setDerivedGaugeWithDifference", "the threshold type is invalid");
    }
  }
  
  Monitor.ObservedObject createObservedObject(ObjectName paramObjectName)
  {
    CounterMonitorObservedObject localCounterMonitorObservedObject = new CounterMonitorObservedObject(paramObjectName);
    localCounterMonitorObservedObject.setThreshold(initThreshold);
    localCounterMonitorObservedObject.setModulusExceeded(false);
    localCounterMonitorObservedObject.setEventAlreadyNotified(false);
    localCounterMonitorObservedObject.setPreviousScanCounter(null);
    return localCounterMonitorObservedObject;
  }
  
  synchronized boolean isComparableTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (localCounterMonitorObservedObject == null) {
      return false;
    }
    if ((paramComparable instanceof Integer)) {
      localCounterMonitorObservedObject.setType(Monitor.NumericalType.INTEGER);
    } else if ((paramComparable instanceof Byte)) {
      localCounterMonitorObservedObject.setType(Monitor.NumericalType.BYTE);
    } else if ((paramComparable instanceof Short)) {
      localCounterMonitorObservedObject.setType(Monitor.NumericalType.SHORT);
    } else if ((paramComparable instanceof Long)) {
      localCounterMonitorObservedObject.setType(Monitor.NumericalType.LONG);
    } else {
      return false;
    }
    return true;
  }
  
  synchronized Comparable<?> getDerivedGaugeFromComparable(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (localCounterMonitorObservedObject == null) {
      return null;
    }
    if ((localCounterMonitorObservedObject.getModulusExceeded()) && (((Number)localCounterMonitorObservedObject.getDerivedGauge()).longValue() < localCounterMonitorObservedObject.getDerivedGaugeExceeded().longValue()))
    {
      localCounterMonitorObservedObject.setThreshold(initThreshold);
      localCounterMonitorObservedObject.setModulusExceeded(false);
      localCounterMonitorObservedObject.setEventAlreadyNotified(false);
    }
    localCounterMonitorObservedObject.setDerivedGaugeValid(updateDerivedGauge(paramComparable, localCounterMonitorObservedObject));
    return (Comparable)localCounterMonitorObservedObject.getDerivedGauge();
  }
  
  synchronized void onErrorNotification(MonitorNotification paramMonitorNotification)
  {
    CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramMonitorNotification.getObservedObject());
    if (localCounterMonitorObservedObject == null) {
      return;
    }
    localCounterMonitorObservedObject.setModulusExceeded(false);
    localCounterMonitorObservedObject.setEventAlreadyNotified(false);
    localCounterMonitorObservedObject.setPreviousScanCounter(null);
  }
  
  synchronized MonitorNotification buildAlarmNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (localCounterMonitorObservedObject == null) {
      return null;
    }
    MonitorNotification localMonitorNotification;
    if (localCounterMonitorObservedObject.getDerivedGaugeValid())
    {
      localMonitorNotification = updateNotifications(localCounterMonitorObservedObject);
      updateThreshold(localCounterMonitorObservedObject);
    }
    else
    {
      localMonitorNotification = null;
    }
    return localMonitorNotification;
  }
  
  synchronized boolean isThresholdTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    CounterMonitorObservedObject localCounterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (localCounterMonitorObservedObject == null) {
      return false;
    }
    Class localClass = classForType(localCounterMonitorObservedObject.getType());
    return (localClass.isInstance(localCounterMonitorObservedObject.getThreshold())) && (isValidForType(offset, localClass)) && (isValidForType(modulus, localClass));
  }
  
  static class CounterMonitorObservedObject
    extends Monitor.ObservedObject
  {
    private Number threshold;
    private Number previousScanCounter;
    private boolean modulusExceeded;
    private Number derivedGaugeExceeded;
    private boolean derivedGaugeValid;
    private boolean eventAlreadyNotified;
    private Monitor.NumericalType type;
    
    public CounterMonitorObservedObject(ObjectName paramObjectName)
    {
      super();
    }
    
    public final synchronized Number getThreshold()
    {
      return threshold;
    }
    
    public final synchronized void setThreshold(Number paramNumber)
    {
      threshold = paramNumber;
    }
    
    public final synchronized Number getPreviousScanCounter()
    {
      return previousScanCounter;
    }
    
    public final synchronized void setPreviousScanCounter(Number paramNumber)
    {
      previousScanCounter = paramNumber;
    }
    
    public final synchronized boolean getModulusExceeded()
    {
      return modulusExceeded;
    }
    
    public final synchronized void setModulusExceeded(boolean paramBoolean)
    {
      modulusExceeded = paramBoolean;
    }
    
    public final synchronized Number getDerivedGaugeExceeded()
    {
      return derivedGaugeExceeded;
    }
    
    public final synchronized void setDerivedGaugeExceeded(Number paramNumber)
    {
      derivedGaugeExceeded = paramNumber;
    }
    
    public final synchronized boolean getDerivedGaugeValid()
    {
      return derivedGaugeValid;
    }
    
    public final synchronized void setDerivedGaugeValid(boolean paramBoolean)
    {
      derivedGaugeValid = paramBoolean;
    }
    
    public final synchronized boolean getEventAlreadyNotified()
    {
      return eventAlreadyNotified;
    }
    
    public final synchronized void setEventAlreadyNotified(boolean paramBoolean)
    {
      eventAlreadyNotified = paramBoolean;
    }
    
    public final synchronized Monitor.NumericalType getType()
    {
      return type;
    }
    
    public final synchronized void setType(Monitor.NumericalType paramNumericalType)
    {
      type = paramNumericalType;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\monitor\CounterMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */