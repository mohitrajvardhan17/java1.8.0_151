package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Introspector;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public abstract class Monitor
  extends NotificationBroadcasterSupport
  implements MonitorMBean, MBeanRegistration
{
  private String observedAttribute;
  private long granularityPeriod = 10000L;
  private boolean isActive = false;
  private final AtomicLong sequenceNumber = new AtomicLong();
  private boolean isComplexTypeAttribute = false;
  private String firstAttribute;
  private final List<String> remainingAttributes = new CopyOnWriteArrayList();
  private static final AccessControlContext noPermissionsACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
  private volatile AccessControlContext acc = noPermissionsACC;
  private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("Scheduler"));
  private static final Map<ThreadPoolExecutor, Void> executors = new WeakHashMap();
  private static final Object executorsLock = new Object();
  private static final int maximumPoolSize;
  private Future<?> monitorFuture;
  private final SchedulerTask schedulerTask = new SchedulerTask();
  private ScheduledFuture<?> schedulerFuture;
  protected static final int capacityIncrement = 16;
  protected int elementCount = 0;
  @Deprecated
  protected int alreadyNotified = 0;
  protected int[] alreadyNotifieds = new int[16];
  protected MBeanServer server;
  protected static final int RESET_FLAGS_ALREADY_NOTIFIED = 0;
  protected static final int OBSERVED_OBJECT_ERROR_NOTIFIED = 1;
  protected static final int OBSERVED_ATTRIBUTE_ERROR_NOTIFIED = 2;
  protected static final int OBSERVED_ATTRIBUTE_TYPE_ERROR_NOTIFIED = 4;
  protected static final int RUNTIME_ERROR_NOTIFIED = 8;
  @Deprecated
  protected String dbgTag = Monitor.class.getName();
  final List<ObservedObject> observedObjects = new CopyOnWriteArrayList();
  static final int THRESHOLD_ERROR_NOTIFIED = 16;
  static final Integer INTEGER_ZERO = Integer.valueOf(0);
  
  public Monitor() {}
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "preRegister(MBeanServer, ObjectName)", "initialize the reference on the MBean server");
    server = paramMBeanServer;
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister()
    throws Exception
  {
    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "preDeregister()", "stop the monitor");
    stop();
  }
  
  public void postDeregister() {}
  
  public abstract void start();
  
  public abstract void stop();
  
  @Deprecated
  public synchronized ObjectName getObservedObject()
  {
    if (observedObjects.isEmpty()) {
      return null;
    }
    return ((ObservedObject)observedObjects.get(0)).getObservedObject();
  }
  
  @Deprecated
  public synchronized void setObservedObject(ObjectName paramObjectName)
    throws IllegalArgumentException
  {
    if (paramObjectName == null) {
      throw new IllegalArgumentException("Null observed object");
    }
    if ((observedObjects.size() == 1) && (containsObservedObject(paramObjectName))) {
      return;
    }
    observedObjects.clear();
    addObservedObject(paramObjectName);
  }
  
  public synchronized void addObservedObject(ObjectName paramObjectName)
    throws IllegalArgumentException
  {
    if (paramObjectName == null) {
      throw new IllegalArgumentException("Null observed object");
    }
    if (containsObservedObject(paramObjectName)) {
      return;
    }
    ObservedObject localObservedObject = createObservedObject(paramObjectName);
    localObservedObject.setAlreadyNotified(0);
    localObservedObject.setDerivedGauge(INTEGER_ZERO);
    localObservedObject.setDerivedGaugeTimeStamp(System.currentTimeMillis());
    observedObjects.add(localObservedObject);
    createAlreadyNotified();
  }
  
  public synchronized void removeObservedObject(ObjectName paramObjectName)
  {
    if (paramObjectName == null) {
      return;
    }
    ObservedObject localObservedObject = getObservedObject(paramObjectName);
    if (localObservedObject != null)
    {
      observedObjects.remove(localObservedObject);
      createAlreadyNotified();
    }
  }
  
  public synchronized boolean containsObservedObject(ObjectName paramObjectName)
  {
    return getObservedObject(paramObjectName) != null;
  }
  
  public synchronized ObjectName[] getObservedObjects()
  {
    ObjectName[] arrayOfObjectName = new ObjectName[observedObjects.size()];
    for (int i = 0; i < arrayOfObjectName.length; i++) {
      arrayOfObjectName[i] = ((ObservedObject)observedObjects.get(i)).getObservedObject();
    }
    return arrayOfObjectName;
  }
  
  public synchronized String getObservedAttribute()
  {
    return observedAttribute;
  }
  
  public void setObservedAttribute(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Null observed attribute");
    }
    synchronized (this)
    {
      if ((observedAttribute != null) && (observedAttribute.equals(paramString))) {
        return;
      }
      observedAttribute = paramString;
      cleanupIsComplexTypeAttribute();
      int i = 0;
      Iterator localIterator = observedObjects.iterator();
      while (localIterator.hasNext())
      {
        ObservedObject localObservedObject = (ObservedObject)localIterator.next();
        resetAlreadyNotified(localObservedObject, i++, 6);
      }
    }
  }
  
  public synchronized long getGranularityPeriod()
  {
    return granularityPeriod;
  }
  
  public synchronized void setGranularityPeriod(long paramLong)
    throws IllegalArgumentException
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("Nonpositive granularity period");
    }
    if (granularityPeriod == paramLong) {
      return;
    }
    granularityPeriod = paramLong;
    if (isActive())
    {
      cleanupFutures();
      schedulerFuture = scheduler.schedule(schedulerTask, paramLong, TimeUnit.MILLISECONDS);
    }
  }
  
  public synchronized boolean isActive()
  {
    return isActive;
  }
  
  void doStart()
  {
    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStart()", "start the monitor");
    synchronized (this)
    {
      if (isActive())
      {
        JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStart()", "the monitor is already active");
        return;
      }
      isActive = true;
      cleanupIsComplexTypeAttribute();
      acc = AccessController.getContext();
      cleanupFutures();
      schedulerTask.setMonitorTask(new MonitorTask());
      schedulerFuture = scheduler.schedule(schedulerTask, getGranularityPeriod(), TimeUnit.MILLISECONDS);
    }
  }
  
  void doStop()
  {
    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStop()", "stop the monitor");
    synchronized (this)
    {
      if (!isActive())
      {
        JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStop()", "the monitor is not active");
        return;
      }
      isActive = false;
      cleanupFutures();
      acc = noPermissionsACC;
      cleanupIsComplexTypeAttribute();
    }
  }
  
  synchronized Object getDerivedGauge(ObjectName paramObjectName)
  {
    ObservedObject localObservedObject = getObservedObject(paramObjectName);
    return localObservedObject == null ? null : localObservedObject.getDerivedGauge();
  }
  
  synchronized long getDerivedGaugeTimeStamp(ObjectName paramObjectName)
  {
    ObservedObject localObservedObject = getObservedObject(paramObjectName);
    return localObservedObject == null ? 0L : localObservedObject.getDerivedGaugeTimeStamp();
  }
  
  Object getAttribute(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, String paramString)
    throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException
  {
    int i;
    synchronized (this)
    {
      if (!isActive()) {
        throw new IllegalArgumentException("The monitor has been stopped");
      }
      if (!paramString.equals(getObservedAttribute())) {
        throw new IllegalArgumentException("The observed attribute has been changed");
      }
      i = (firstAttribute == null) && (paramString.indexOf('.') != -1) ? 1 : 0;
    }
    if (i != 0) {
      try
      {
        ??? = paramMBeanServerConnection.getMBeanInfo(paramObjectName);
      }
      catch (IntrospectionException localIntrospectionException)
      {
        throw new IllegalArgumentException(localIntrospectionException);
      }
    } else {
      ??? = null;
    }
    String str;
    synchronized (this)
    {
      if (!isActive()) {
        throw new IllegalArgumentException("The monitor has been stopped");
      }
      if (!paramString.equals(getObservedAttribute())) {
        throw new IllegalArgumentException("The observed attribute has been changed");
      }
      if (firstAttribute == null) {
        if (paramString.indexOf('.') != -1)
        {
          MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = ((MBeanInfo)???).getAttributes();
          for (Object localObject3 : arrayOfMBeanAttributeInfo) {
            if (paramString.equals(((MBeanAttributeInfo)localObject3).getName()))
            {
              firstAttribute = paramString;
              break;
            }
          }
          if (firstAttribute == null)
          {
            ??? = paramString.split("\\.", -1);
            firstAttribute = ???[0];
            for (??? = 1; ??? < ???.length; ???++) {
              remainingAttributes.add(???[???]);
            }
            isComplexTypeAttribute = true;
          }
        }
        else
        {
          firstAttribute = paramString;
        }
      }
      str = firstAttribute;
    }
    return paramMBeanServerConnection.getAttribute(paramObjectName, str);
  }
  
  Comparable<?> getComparableFromAttribute(ObjectName paramObjectName, String paramString, Object paramObject)
    throws AttributeNotFoundException
  {
    if (isComplexTypeAttribute)
    {
      Object localObject = paramObject;
      Iterator localIterator = remainingAttributes.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localObject = Introspector.elementFromComplex(localObject, str);
      }
      return (Comparable)localObject;
    }
    return (Comparable)paramObject;
  }
  
  boolean isComparableTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    return true;
  }
  
  String buildErrorNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    return null;
  }
  
  void onErrorNotification(MonitorNotification paramMonitorNotification) {}
  
  Comparable<?> getDerivedGaugeFromComparable(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    return paramComparable;
  }
  
  MonitorNotification buildAlarmNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    return null;
  }
  
  boolean isThresholdTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable)
  {
    return true;
  }
  
  static Class<? extends Number> classForType(NumericalType paramNumericalType)
  {
    switch (paramNumericalType)
    {
    case BYTE: 
      return Byte.class;
    case SHORT: 
      return Short.class;
    case INTEGER: 
      return Integer.class;
    case LONG: 
      return Long.class;
    case FLOAT: 
      return Float.class;
    case DOUBLE: 
      return Double.class;
    }
    throw new IllegalArgumentException("Unsupported numerical type");
  }
  
  static boolean isValidForType(Object paramObject, Class<? extends Number> paramClass)
  {
    return (paramObject == INTEGER_ZERO) || (paramClass.isInstance(paramObject));
  }
  
  synchronized ObservedObject getObservedObject(ObjectName paramObjectName)
  {
    Iterator localIterator = observedObjects.iterator();
    while (localIterator.hasNext())
    {
      ObservedObject localObservedObject = (ObservedObject)localIterator.next();
      if (localObservedObject.getObservedObject().equals(paramObjectName)) {
        return localObservedObject;
      }
    }
    return null;
  }
  
  ObservedObject createObservedObject(ObjectName paramObjectName)
  {
    return new ObservedObject(paramObjectName);
  }
  
  synchronized void createAlreadyNotified()
  {
    elementCount = observedObjects.size();
    alreadyNotifieds = new int[elementCount];
    for (int i = 0; i < elementCount; i++) {
      alreadyNotifieds[i] = ((ObservedObject)observedObjects.get(i)).getAlreadyNotified();
    }
    updateDeprecatedAlreadyNotified();
  }
  
  synchronized void updateDeprecatedAlreadyNotified()
  {
    if (elementCount > 0) {
      alreadyNotified = alreadyNotifieds[0];
    } else {
      alreadyNotified = 0;
    }
  }
  
  synchronized void updateAlreadyNotified(ObservedObject paramObservedObject, int paramInt)
  {
    alreadyNotifieds[paramInt] = paramObservedObject.getAlreadyNotified();
    if (paramInt == 0) {
      updateDeprecatedAlreadyNotified();
    }
  }
  
  synchronized boolean isAlreadyNotified(ObservedObject paramObservedObject, int paramInt)
  {
    return (paramObservedObject.getAlreadyNotified() & paramInt) != 0;
  }
  
  synchronized void setAlreadyNotified(ObservedObject paramObservedObject, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    int i = computeAlreadyNotifiedIndex(paramObservedObject, paramInt1, paramArrayOfInt);
    if (i == -1) {
      return;
    }
    paramObservedObject.setAlreadyNotified(paramObservedObject.getAlreadyNotified() | paramInt2);
    updateAlreadyNotified(paramObservedObject, i);
  }
  
  synchronized void resetAlreadyNotified(ObservedObject paramObservedObject, int paramInt1, int paramInt2)
  {
    paramObservedObject.setAlreadyNotified(paramObservedObject.getAlreadyNotified() & (paramInt2 ^ 0xFFFFFFFF));
    updateAlreadyNotified(paramObservedObject, paramInt1);
  }
  
  synchronized void resetAllAlreadyNotified(ObservedObject paramObservedObject, int paramInt, int[] paramArrayOfInt)
  {
    int i = computeAlreadyNotifiedIndex(paramObservedObject, paramInt, paramArrayOfInt);
    if (i == -1) {
      return;
    }
    paramObservedObject.setAlreadyNotified(0);
    updateAlreadyNotified(paramObservedObject, paramInt);
  }
  
  synchronized int computeAlreadyNotifiedIndex(ObservedObject paramObservedObject, int paramInt, int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == alreadyNotifieds) {
      return paramInt;
    }
    return observedObjects.indexOf(paramObservedObject);
  }
  
  private void sendNotification(String paramString1, long paramLong, String paramString2, Object paramObject1, Object paramObject2, ObjectName paramObjectName, boolean paramBoolean)
  {
    if (!isActive()) {
      return;
    }
    if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "sendNotification", "send notification: \n\tNotification observed object = " + paramObjectName + "\n\tNotification observed attribute = " + observedAttribute + "\n\tNotification derived gauge = " + paramObject1);
    }
    long l = sequenceNumber.getAndIncrement();
    MonitorNotification localMonitorNotification = new MonitorNotification(paramString1, this, l, paramLong, paramString2, paramObjectName, observedAttribute, paramObject1, paramObject2);
    if (paramBoolean) {
      onErrorNotification(localMonitorNotification);
    }
    sendNotification(localMonitorNotification);
  }
  
  private void monitor(ObservedObject paramObservedObject, int paramInt, int[] paramArrayOfInt)
  {
    String str2 = null;
    String str3 = null;
    Comparable localComparable1 = null;
    Object localObject1 = null;
    Comparable localComparable2 = null;
    MonitorNotification localMonitorNotification = null;
    if (!isActive()) {
      return;
    }
    ObjectName localObjectName;
    String str1;
    synchronized (this)
    {
      localObjectName = paramObservedObject.getObservedObject();
      str1 = getObservedAttribute();
      if ((localObjectName == null) || (str1 == null)) {
        return;
      }
    }
    ??? = null;
    try
    {
      ??? = getAttribute(server, localObjectName, str1);
      if (??? == null)
      {
        if (isAlreadyNotified(paramObservedObject, 4)) {
          return;
        }
        str2 = "jmx.monitor.error.type";
        setAlreadyNotified(paramObservedObject, paramInt, 4, paramArrayOfInt);
        str3 = "The observed attribute value is null.";
        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      }
    }
    catch (NullPointerException localNullPointerException)
    {
      if (isAlreadyNotified(paramObservedObject, 8)) {
        return;
      }
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = "The monitor must be registered in the MBean server or an MBeanServerConnection must be explicitly supplied.";
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localNullPointerException.toString());
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      if (isAlreadyNotified(paramObservedObject, 1)) {
        return;
      }
      str2 = "jmx.monitor.error.mbean";
      setAlreadyNotified(paramObservedObject, paramInt, 1, paramArrayOfInt);
      str3 = "The observed object must be accessible in the MBeanServerConnection.";
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localInstanceNotFoundException.toString());
    }
    catch (AttributeNotFoundException localAttributeNotFoundException1)
    {
      if (isAlreadyNotified(paramObservedObject, 2)) {
        return;
      }
      str2 = "jmx.monitor.error.attribute";
      setAlreadyNotified(paramObservedObject, paramInt, 2, paramArrayOfInt);
      str3 = "The observed attribute must be accessible in the observed object.";
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localAttributeNotFoundException1.toString());
    }
    catch (MBeanException localMBeanException)
    {
      if (isAlreadyNotified(paramObservedObject, 8)) {
        return;
      }
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = localMBeanException.getMessage() == null ? "" : localMBeanException.getMessage();
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localMBeanException.toString());
    }
    catch (ReflectionException localReflectionException)
    {
      if (isAlreadyNotified(paramObservedObject, 8)) {
        return;
      }
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = localReflectionException.getMessage() == null ? "" : localReflectionException.getMessage();
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localReflectionException.toString());
    }
    catch (IOException localIOException)
    {
      if (isAlreadyNotified(paramObservedObject, 8)) {
        return;
      }
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = localIOException.getMessage() == null ? "" : localIOException.getMessage();
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localIOException.toString());
    }
    catch (RuntimeException localRuntimeException1)
    {
      if (isAlreadyNotified(paramObservedObject, 8)) {
        return;
      }
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = localRuntimeException1.getMessage() == null ? "" : localRuntimeException1.getMessage();
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localRuntimeException1.toString());
    }
    synchronized (this)
    {
      if (!isActive()) {
        return;
      }
      if (!str1.equals(getObservedAttribute())) {
        return;
      }
      if (str3 == null) {
        try
        {
          localComparable2 = getComparableFromAttribute(localObjectName, str1, ???);
        }
        catch (ClassCastException localClassCastException)
        {
          if (isAlreadyNotified(paramObservedObject, 4)) {
            return;
          }
          str2 = "jmx.monitor.error.type";
          setAlreadyNotified(paramObservedObject, paramInt, 4, paramArrayOfInt);
          str3 = "The observed attribute value does not implement the Comparable interface.";
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localClassCastException.toString());
        }
        catch (AttributeNotFoundException localAttributeNotFoundException2)
        {
          if (isAlreadyNotified(paramObservedObject, 2)) {
            return;
          }
          str2 = "jmx.monitor.error.attribute";
          setAlreadyNotified(paramObservedObject, paramInt, 2, paramArrayOfInt);
          str3 = "The observed attribute must be accessible in the observed object.";
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localAttributeNotFoundException2.toString());
        }
        catch (RuntimeException localRuntimeException2)
        {
          if (isAlreadyNotified(paramObservedObject, 8)) {
            return;
          }
          str2 = "jmx.monitor.error.runtime";
          setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
          str3 = localRuntimeException2.getMessage() == null ? "" : localRuntimeException2.getMessage();
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", localRuntimeException2.toString());
        }
      }
      if ((str3 == null) && (!isComparableTypeValid(localObjectName, str1, localComparable2)))
      {
        if (isAlreadyNotified(paramObservedObject, 4)) {
          return;
        }
        str2 = "jmx.monitor.error.type";
        setAlreadyNotified(paramObservedObject, paramInt, 4, paramArrayOfInt);
        str3 = "The observed attribute type is not valid.";
        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      }
      if ((str3 == null) && (!isThresholdTypeValid(localObjectName, str1, localComparable2)))
      {
        if (isAlreadyNotified(paramObservedObject, 16)) {
          return;
        }
        str2 = "jmx.monitor.error.threshold";
        setAlreadyNotified(paramObservedObject, paramInt, 16, paramArrayOfInt);
        str3 = "The threshold type is not valid.";
        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      }
      if (str3 == null)
      {
        str3 = buildErrorNotification(localObjectName, str1, localComparable2);
        if (str3 != null)
        {
          if (isAlreadyNotified(paramObservedObject, 8)) {
            return;
          }
          str2 = "jmx.monitor.error.runtime";
          setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
        }
      }
      if (str3 == null)
      {
        resetAllAlreadyNotified(paramObservedObject, paramInt, paramArrayOfInt);
        localComparable1 = getDerivedGaugeFromComparable(localObjectName, str1, localComparable2);
        paramObservedObject.setDerivedGauge(localComparable1);
        paramObservedObject.setDerivedGaugeTimeStamp(System.currentTimeMillis());
        localMonitorNotification = buildAlarmNotification(localObjectName, str1, (Comparable)localComparable1);
      }
    }
    if (str3 != null) {
      sendNotification(str2, System.currentTimeMillis(), str3, localComparable1, localObject1, localObjectName, true);
    }
    if ((localMonitorNotification != null) && (localMonitorNotification.getType() != null)) {
      sendNotification(localMonitorNotification.getType(), System.currentTimeMillis(), localMonitorNotification.getMessage(), localComparable1, localMonitorNotification.getTrigger(), localObjectName, false);
    }
  }
  
  private synchronized void cleanupFutures()
  {
    if (schedulerFuture != null)
    {
      schedulerFuture.cancel(false);
      schedulerFuture = null;
    }
    if (monitorFuture != null)
    {
      monitorFuture.cancel(false);
      monitorFuture = null;
    }
  }
  
  private synchronized void cleanupIsComplexTypeAttribute()
  {
    firstAttribute = null;
    remainingAttributes.clear();
    isComplexTypeAttribute = false;
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jmx.x.monitor.maximum.pool.size"));
    if ((str == null) || (str.trim().length() == 0))
    {
      maximumPoolSize = 10;
    }
    else
    {
      int i = 10;
      try
      {
        i = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER))
        {
          JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "<static initializer>", "Wrong value for jmx.x.monitor.maximum.pool.size system property", localNumberFormatException);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "<static initializer>", "jmx.x.monitor.maximum.pool.size defaults to 10");
        }
        i = 10;
      }
      if (i < 1) {
        maximumPoolSize = 1;
      } else {
        maximumPoolSize = i;
      }
    }
  }
  
  private static class DaemonThreadFactory
    implements ThreadFactory
  {
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;
    static final String nameSuffix = "]";
    
    public DaemonThreadFactory(String paramString)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      group = (localSecurityManager != null ? localSecurityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
      namePrefix = ("JMX Monitor " + paramString + " Pool [Thread-");
    }
    
    public DaemonThreadFactory(String paramString, ThreadGroup paramThreadGroup)
    {
      group = paramThreadGroup;
      namePrefix = ("JMX Monitor " + paramString + " Pool [Thread-");
    }
    
    public ThreadGroup getThreadGroup()
    {
      return group;
    }
    
    public Thread newThread(Runnable paramRunnable)
    {
      Thread localThread = new Thread(group, paramRunnable, namePrefix + threadNumber.getAndIncrement() + "]", 0L);
      localThread.setDaemon(true);
      if (localThread.getPriority() != 5) {
        localThread.setPriority(5);
      }
      return localThread;
    }
  }
  
  private class MonitorTask
    implements Runnable
  {
    private ThreadPoolExecutor executor;
    
    public MonitorTask()
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      ThreadGroup localThreadGroup1 = localSecurityManager != null ? localSecurityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
      synchronized (Monitor.executorsLock)
      {
        Iterator localIterator = Monitor.executors.keySet().iterator();
        while (localIterator.hasNext())
        {
          ThreadPoolExecutor localThreadPoolExecutor = (ThreadPoolExecutor)localIterator.next();
          Monitor.DaemonThreadFactory localDaemonThreadFactory = (Monitor.DaemonThreadFactory)localThreadPoolExecutor.getThreadFactory();
          ThreadGroup localThreadGroup2 = localDaemonThreadFactory.getThreadGroup();
          if (localThreadGroup2 == localThreadGroup1)
          {
            executor = localThreadPoolExecutor;
            break;
          }
        }
        if (executor == null)
        {
          executor = new ThreadPoolExecutor(Monitor.maximumPoolSize, Monitor.maximumPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new Monitor.DaemonThreadFactory("ThreadGroup<" + localThreadGroup1.getName() + "> Executor", localThreadGroup1));
          executor.allowCoreThreadTimeOut(true);
          Monitor.executors.put(executor, null);
        }
      }
    }
    
    public Future<?> submit()
    {
      return executor.submit(this);
    }
    
    public void run()
    {
      ScheduledFuture localScheduledFuture;
      AccessControlContext localAccessControlContext;
      synchronized (Monitor.this)
      {
        localScheduledFuture = schedulerFuture;
        localAccessControlContext = acc;
      }
      ??? = new PrivilegedAction()
      {
        public Void run()
        {
          if (isActive())
          {
            int[] arrayOfInt = alreadyNotifieds;
            int i = 0;
            Iterator localIterator = observedObjects.iterator();
            while (localIterator.hasNext())
            {
              Monitor.ObservedObject localObservedObject = (Monitor.ObservedObject)localIterator.next();
              if (isActive()) {
                Monitor.this.monitor(localObservedObject, i++, arrayOfInt);
              }
            }
          }
          return null;
        }
      };
      if (localAccessControlContext == null) {
        throw new SecurityException("AccessControlContext cannot be null");
      }
      AccessController.doPrivileged((PrivilegedAction)???, localAccessControlContext);
      synchronized (Monitor.this)
      {
        if ((isActive()) && (schedulerFuture == localScheduledFuture))
        {
          monitorFuture = null;
          schedulerFuture = Monitor.scheduler.schedule(schedulerTask, getGranularityPeriod(), TimeUnit.MILLISECONDS);
        }
      }
    }
  }
  
  static enum NumericalType
  {
    BYTE,  SHORT,  INTEGER,  LONG,  FLOAT,  DOUBLE;
    
    private NumericalType() {}
  }
  
  static class ObservedObject
  {
    private final ObjectName observedObject;
    private int alreadyNotified;
    private Object derivedGauge;
    private long derivedGaugeTimeStamp;
    
    public ObservedObject(ObjectName paramObjectName)
    {
      observedObject = paramObjectName;
    }
    
    public final ObjectName getObservedObject()
    {
      return observedObject;
    }
    
    public final synchronized int getAlreadyNotified()
    {
      return alreadyNotified;
    }
    
    public final synchronized void setAlreadyNotified(int paramInt)
    {
      alreadyNotified = paramInt;
    }
    
    public final synchronized Object getDerivedGauge()
    {
      return derivedGauge;
    }
    
    public final synchronized void setDerivedGauge(Object paramObject)
    {
      derivedGauge = paramObject;
    }
    
    public final synchronized long getDerivedGaugeTimeStamp()
    {
      return derivedGaugeTimeStamp;
    }
    
    public final synchronized void setDerivedGaugeTimeStamp(long paramLong)
    {
      derivedGaugeTimeStamp = paramLong;
    }
  }
  
  private class SchedulerTask
    implements Runnable
  {
    private Monitor.MonitorTask task;
    
    public SchedulerTask() {}
    
    public void setMonitorTask(Monitor.MonitorTask paramMonitorTask)
    {
      task = paramMonitorTask;
    }
    
    public void run()
    {
      synchronized (Monitor.this)
      {
        monitorFuture = task.submit();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\monitor\Monitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */