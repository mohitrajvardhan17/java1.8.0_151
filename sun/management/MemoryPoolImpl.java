package sun.management;

import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import javax.management.ObjectName;

class MemoryPoolImpl
  implements MemoryPoolMXBean
{
  private final String name;
  private final boolean isHeap;
  private final boolean isValid;
  private final boolean collectionThresholdSupported;
  private final boolean usageThresholdSupported;
  private MemoryManagerMXBean[] managers;
  private long usageThreshold;
  private long collectionThreshold;
  private boolean usageSensorRegistered;
  private boolean gcSensorRegistered;
  private Sensor usageSensor;
  private Sensor gcSensor;
  
  MemoryPoolImpl(String paramString, boolean paramBoolean, long paramLong1, long paramLong2)
  {
    name = paramString;
    isHeap = paramBoolean;
    isValid = true;
    managers = null;
    usageThreshold = paramLong1;
    collectionThreshold = paramLong2;
    usageThresholdSupported = (paramLong1 >= 0L);
    collectionThresholdSupported = (paramLong2 >= 0L);
    usageSensor = new PoolSensor(this, paramString + " usage sensor");
    gcSensor = new CollectionSensor(this, paramString + " collection sensor");
    usageSensorRegistered = false;
    gcSensorRegistered = false;
  }
  
  public String getName()
  {
    return name;
  }
  
  public boolean isValid()
  {
    return isValid;
  }
  
  public MemoryType getType()
  {
    if (isHeap) {
      return MemoryType.HEAP;
    }
    return MemoryType.NON_HEAP;
  }
  
  public MemoryUsage getUsage()
  {
    return getUsage0();
  }
  
  public synchronized MemoryUsage getPeakUsage()
  {
    return getPeakUsage0();
  }
  
  public synchronized long getUsageThreshold()
  {
    if (!isUsageThresholdSupported()) {
      throw new UnsupportedOperationException("Usage threshold is not supported");
    }
    return usageThreshold;
  }
  
  public void setUsageThreshold(long paramLong)
  {
    if (!isUsageThresholdSupported()) {
      throw new UnsupportedOperationException("Usage threshold is not supported");
    }
    Util.checkControlAccess();
    MemoryUsage localMemoryUsage = getUsage0();
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Invalid threshold: " + paramLong);
    }
    if ((localMemoryUsage.getMax() != -1L) && (paramLong > localMemoryUsage.getMax())) {
      throw new IllegalArgumentException("Invalid threshold: " + paramLong + " must be <= maxSize. Committed = " + localMemoryUsage.getCommitted() + " Max = " + localMemoryUsage.getMax());
    }
    synchronized (this)
    {
      if (!usageSensorRegistered)
      {
        usageSensorRegistered = true;
        setPoolUsageSensor(usageSensor);
      }
      setUsageThreshold0(usageThreshold, paramLong);
      usageThreshold = paramLong;
    }
  }
  
  private synchronized MemoryManagerMXBean[] getMemoryManagers()
  {
    if (managers == null) {
      managers = getMemoryManagers0();
    }
    return managers;
  }
  
  public String[] getMemoryManagerNames()
  {
    MemoryManagerMXBean[] arrayOfMemoryManagerMXBean = getMemoryManagers();
    String[] arrayOfString = new String[arrayOfMemoryManagerMXBean.length];
    for (int i = 0; i < arrayOfMemoryManagerMXBean.length; i++) {
      arrayOfString[i] = arrayOfMemoryManagerMXBean[i].getName();
    }
    return arrayOfString;
  }
  
  public void resetPeakUsage()
  {
    
    synchronized (this)
    {
      resetPeakUsage0();
    }
  }
  
  public boolean isUsageThresholdExceeded()
  {
    if (!isUsageThresholdSupported()) {
      throw new UnsupportedOperationException("Usage threshold is not supported");
    }
    if (usageThreshold == 0L) {
      return false;
    }
    MemoryUsage localMemoryUsage = getUsage0();
    return (localMemoryUsage.getUsed() >= usageThreshold) || (usageSensor.isOn());
  }
  
  public long getUsageThresholdCount()
  {
    if (!isUsageThresholdSupported()) {
      throw new UnsupportedOperationException("Usage threshold is not supported");
    }
    return usageSensor.getCount();
  }
  
  public boolean isUsageThresholdSupported()
  {
    return usageThresholdSupported;
  }
  
  public synchronized long getCollectionUsageThreshold()
  {
    if (!isCollectionUsageThresholdSupported()) {
      throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
    }
    return collectionThreshold;
  }
  
  public void setCollectionUsageThreshold(long paramLong)
  {
    if (!isCollectionUsageThresholdSupported()) {
      throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
    }
    Util.checkControlAccess();
    MemoryUsage localMemoryUsage = getUsage0();
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Invalid threshold: " + paramLong);
    }
    if ((localMemoryUsage.getMax() != -1L) && (paramLong > localMemoryUsage.getMax())) {
      throw new IllegalArgumentException("Invalid threshold: " + paramLong + " > max (" + localMemoryUsage.getMax() + ").");
    }
    synchronized (this)
    {
      if (!gcSensorRegistered)
      {
        gcSensorRegistered = true;
        setPoolCollectionSensor(gcSensor);
      }
      setCollectionThreshold0(collectionThreshold, paramLong);
      collectionThreshold = paramLong;
    }
  }
  
  public boolean isCollectionUsageThresholdExceeded()
  {
    if (!isCollectionUsageThresholdSupported()) {
      throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
    }
    if (collectionThreshold == 0L) {
      return false;
    }
    MemoryUsage localMemoryUsage = getCollectionUsage0();
    return (gcSensor.isOn()) || ((localMemoryUsage != null) && (localMemoryUsage.getUsed() >= collectionThreshold));
  }
  
  public long getCollectionUsageThresholdCount()
  {
    if (!isCollectionUsageThresholdSupported()) {
      throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
    }
    return gcSensor.getCount();
  }
  
  public MemoryUsage getCollectionUsage()
  {
    return getCollectionUsage0();
  }
  
  public boolean isCollectionUsageThresholdSupported()
  {
    return collectionThresholdSupported;
  }
  
  private native MemoryUsage getUsage0();
  
  private native MemoryUsage getPeakUsage0();
  
  private native MemoryUsage getCollectionUsage0();
  
  private native void setUsageThreshold0(long paramLong1, long paramLong2);
  
  private native void setCollectionThreshold0(long paramLong1, long paramLong2);
  
  private native void resetPeakUsage0();
  
  private native MemoryManagerMXBean[] getMemoryManagers0();
  
  private native void setPoolUsageSensor(Sensor paramSensor);
  
  private native void setPoolCollectionSensor(Sensor paramSensor);
  
  public ObjectName getObjectName()
  {
    return Util.newObjectName("java.lang:type=MemoryPool", getName());
  }
  
  class CollectionSensor
    extends Sensor
  {
    MemoryPoolImpl pool;
    
    CollectionSensor(MemoryPoolImpl paramMemoryPoolImpl, String paramString)
    {
      super();
      pool = paramMemoryPoolImpl;
    }
    
    void triggerAction(MemoryUsage paramMemoryUsage)
    {
      MemoryImpl.createNotification("java.management.memory.collection.threshold.exceeded", pool.getName(), paramMemoryUsage, gcSensor.getCount());
    }
    
    void triggerAction()
    {
      throw new AssertionError("Should not reach here");
    }
    
    void clearAction() {}
  }
  
  class PoolSensor
    extends Sensor
  {
    MemoryPoolImpl pool;
    
    PoolSensor(MemoryPoolImpl paramMemoryPoolImpl, String paramString)
    {
      super();
      pool = paramMemoryPoolImpl;
    }
    
    void triggerAction(MemoryUsage paramMemoryUsage)
    {
      MemoryImpl.createNotification("java.management.memory.threshold.exceeded", pool.getName(), paramMemoryUsage, getCount());
    }
    
    void triggerAction()
    {
      throw new AssertionError("Should not reach here");
    }
    
    void clearAction() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\MemoryPoolImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */