package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Map;
import sun.management.snmp.jvmmib.EnumJvmMemPoolCollectThreshdSupport;
import sun.management.snmp.jvmmib.EnumJvmMemPoolState;
import sun.management.snmp.jvmmib.EnumJvmMemPoolThreshdSupport;
import sun.management.snmp.jvmmib.EnumJvmMemPoolType;
import sun.management.snmp.jvmmib.JvmMemPoolEntryMBean;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;

public class JvmMemPoolEntryImpl
  implements JvmMemPoolEntryMBean
{
  protected final int jvmMemPoolIndex;
  static final String memoryTag = "jvmMemPoolEntry.getUsage";
  static final String peakMemoryTag = "jvmMemPoolEntry.getPeakUsage";
  static final String collectMemoryTag = "jvmMemPoolEntry.getCollectionUsage";
  static final MemoryUsage ZEROS = new MemoryUsage(0L, 0L, 0L, 0L);
  final String entryMemoryTag;
  final String entryPeakMemoryTag;
  final String entryCollectMemoryTag;
  final MemoryPoolMXBean pool;
  private long jvmMemPoolPeakReset = 0L;
  private static final EnumJvmMemPoolState JvmMemPoolStateValid = new EnumJvmMemPoolState("valid");
  private static final EnumJvmMemPoolState JvmMemPoolStateInvalid = new EnumJvmMemPoolState("invalid");
  private static final EnumJvmMemPoolType EnumJvmMemPoolTypeHeap = new EnumJvmMemPoolType("heap");
  private static final EnumJvmMemPoolType EnumJvmMemPoolTypeNonHeap = new EnumJvmMemPoolType("nonheap");
  private static final EnumJvmMemPoolThreshdSupport EnumJvmMemPoolThreshdSupported = new EnumJvmMemPoolThreshdSupport("supported");
  private static final EnumJvmMemPoolThreshdSupport EnumJvmMemPoolThreshdUnsupported = new EnumJvmMemPoolThreshdSupport("unsupported");
  private static final EnumJvmMemPoolCollectThreshdSupport EnumJvmMemPoolCollectThreshdSupported = new EnumJvmMemPoolCollectThreshdSupport("supported");
  private static final EnumJvmMemPoolCollectThreshdSupport EnumJvmMemPoolCollectThreshdUnsupported = new EnumJvmMemPoolCollectThreshdSupport("unsupported");
  static final MibLogger log = new MibLogger(JvmMemPoolEntryImpl.class);
  
  MemoryUsage getMemoryUsage()
  {
    try
    {
      Map localMap = JvmContextFactory.getUserData();
      if (localMap != null)
      {
        MemoryUsage localMemoryUsage1 = (MemoryUsage)localMap.get(entryMemoryTag);
        if (localMemoryUsage1 != null)
        {
          log.debug("getMemoryUsage", entryMemoryTag + " found in cache.");
          return localMemoryUsage1;
        }
        MemoryUsage localMemoryUsage2 = pool.getUsage();
        if (localMemoryUsage2 == null) {
          localMemoryUsage2 = ZEROS;
        }
        localMap.put(entryMemoryTag, localMemoryUsage2);
        return localMemoryUsage2;
      }
      log.trace("getMemoryUsage", "ERROR: should never come here!");
      return pool.getUsage();
    }
    catch (RuntimeException localRuntimeException)
    {
      log.trace("getMemoryUsage", "Failed to get MemoryUsage: " + localRuntimeException);
      log.debug("getMemoryUsage", localRuntimeException);
      throw localRuntimeException;
    }
  }
  
  MemoryUsage getPeakMemoryUsage()
  {
    try
    {
      Map localMap = JvmContextFactory.getUserData();
      if (localMap != null)
      {
        MemoryUsage localMemoryUsage1 = (MemoryUsage)localMap.get(entryPeakMemoryTag);
        if (localMemoryUsage1 != null)
        {
          if (log.isDebugOn()) {
            log.debug("getPeakMemoryUsage", entryPeakMemoryTag + " found in cache.");
          }
          return localMemoryUsage1;
        }
        MemoryUsage localMemoryUsage2 = pool.getPeakUsage();
        if (localMemoryUsage2 == null) {
          localMemoryUsage2 = ZEROS;
        }
        localMap.put(entryPeakMemoryTag, localMemoryUsage2);
        return localMemoryUsage2;
      }
      log.trace("getPeakMemoryUsage", "ERROR: should never come here!");
      return ZEROS;
    }
    catch (RuntimeException localRuntimeException)
    {
      log.trace("getPeakMemoryUsage", "Failed to get MemoryUsage: " + localRuntimeException);
      log.debug("getPeakMemoryUsage", localRuntimeException);
      throw localRuntimeException;
    }
  }
  
  MemoryUsage getCollectMemoryUsage()
  {
    try
    {
      Map localMap = JvmContextFactory.getUserData();
      if (localMap != null)
      {
        MemoryUsage localMemoryUsage1 = (MemoryUsage)localMap.get(entryCollectMemoryTag);
        if (localMemoryUsage1 != null)
        {
          if (log.isDebugOn()) {
            log.debug("getCollectMemoryUsage", entryCollectMemoryTag + " found in cache.");
          }
          return localMemoryUsage1;
        }
        MemoryUsage localMemoryUsage2 = pool.getCollectionUsage();
        if (localMemoryUsage2 == null) {
          localMemoryUsage2 = ZEROS;
        }
        localMap.put(entryCollectMemoryTag, localMemoryUsage2);
        return localMemoryUsage2;
      }
      log.trace("getCollectMemoryUsage", "ERROR: should never come here!");
      return ZEROS;
    }
    catch (RuntimeException localRuntimeException)
    {
      log.trace("getPeakMemoryUsage", "Failed to get MemoryUsage: " + localRuntimeException);
      log.debug("getPeakMemoryUsage", localRuntimeException);
      throw localRuntimeException;
    }
  }
  
  public JvmMemPoolEntryImpl(MemoryPoolMXBean paramMemoryPoolMXBean, int paramInt)
  {
    pool = paramMemoryPoolMXBean;
    jvmMemPoolIndex = paramInt;
    entryMemoryTag = ("jvmMemPoolEntry.getUsage." + paramInt);
    entryPeakMemoryTag = ("jvmMemPoolEntry.getPeakUsage." + paramInt);
    entryCollectMemoryTag = ("jvmMemPoolEntry.getCollectionUsage." + paramInt);
  }
  
  public Long getJvmMemPoolMaxSize()
    throws SnmpStatusException
  {
    long l = getMemoryUsage().getMax();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolUsed()
    throws SnmpStatusException
  {
    long l = getMemoryUsage().getUsed();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolInitSize()
    throws SnmpStatusException
  {
    long l = getMemoryUsage().getInit();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCommitted()
    throws SnmpStatusException
  {
    long l = getMemoryUsage().getCommitted();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolPeakMaxSize()
    throws SnmpStatusException
  {
    long l = getPeakMemoryUsage().getMax();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolPeakUsed()
    throws SnmpStatusException
  {
    long l = getPeakMemoryUsage().getUsed();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolPeakCommitted()
    throws SnmpStatusException
  {
    long l = getPeakMemoryUsage().getCommitted();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCollectMaxSize()
    throws SnmpStatusException
  {
    long l = getCollectMemoryUsage().getMax();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCollectUsed()
    throws SnmpStatusException
  {
    long l = getCollectMemoryUsage().getUsed();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCollectCommitted()
    throws SnmpStatusException
  {
    long l = getCollectMemoryUsage().getCommitted();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolThreshold()
    throws SnmpStatusException
  {
    if (!pool.isUsageThresholdSupported()) {
      return JvmMemoryImpl.Long0;
    }
    long l = pool.getUsageThreshold();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public void setJvmMemPoolThreshold(Long paramLong)
    throws SnmpStatusException
  {
    long l = paramLong.longValue();
    if (l < 0L) {
      throw new SnmpStatusException(10);
    }
    pool.setUsageThreshold(l);
  }
  
  public void checkJvmMemPoolThreshold(Long paramLong)
    throws SnmpStatusException
  {
    if (!pool.isUsageThresholdSupported()) {
      throw new SnmpStatusException(12);
    }
    long l = paramLong.longValue();
    if (l < 0L) {
      throw new SnmpStatusException(10);
    }
  }
  
  public EnumJvmMemPoolThreshdSupport getJvmMemPoolThreshdSupport()
    throws SnmpStatusException
  {
    if (pool.isUsageThresholdSupported()) {
      return EnumJvmMemPoolThreshdSupported;
    }
    return EnumJvmMemPoolThreshdUnsupported;
  }
  
  public Long getJvmMemPoolThreshdCount()
    throws SnmpStatusException
  {
    if (!pool.isUsageThresholdSupported()) {
      return JvmMemoryImpl.Long0;
    }
    long l = pool.getUsageThresholdCount();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCollectThreshold()
    throws SnmpStatusException
  {
    if (!pool.isCollectionUsageThresholdSupported()) {
      return JvmMemoryImpl.Long0;
    }
    long l = pool.getCollectionUsageThreshold();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public void setJvmMemPoolCollectThreshold(Long paramLong)
    throws SnmpStatusException
  {
    long l = paramLong.longValue();
    if (l < 0L) {
      throw new SnmpStatusException(10);
    }
    pool.setCollectionUsageThreshold(l);
  }
  
  public void checkJvmMemPoolCollectThreshold(Long paramLong)
    throws SnmpStatusException
  {
    if (!pool.isCollectionUsageThresholdSupported()) {
      throw new SnmpStatusException(12);
    }
    long l = paramLong.longValue();
    if (l < 0L) {
      throw new SnmpStatusException(10);
    }
  }
  
  public EnumJvmMemPoolCollectThreshdSupport getJvmMemPoolCollectThreshdSupport()
    throws SnmpStatusException
  {
    if (pool.isCollectionUsageThresholdSupported()) {
      return EnumJvmMemPoolCollectThreshdSupported;
    }
    return EnumJvmMemPoolCollectThreshdUnsupported;
  }
  
  public Long getJvmMemPoolCollectThreshdCount()
    throws SnmpStatusException
  {
    if (!pool.isCollectionUsageThresholdSupported()) {
      return JvmMemoryImpl.Long0;
    }
    long l = pool.getCollectionUsageThresholdCount();
    if (l > -1L) {
      return new Long(l);
    }
    return JvmMemoryImpl.Long0;
  }
  
  public static EnumJvmMemPoolType jvmMemPoolType(MemoryType paramMemoryType)
    throws SnmpStatusException
  {
    if (paramMemoryType.equals(MemoryType.HEAP)) {
      return EnumJvmMemPoolTypeHeap;
    }
    if (paramMemoryType.equals(MemoryType.NON_HEAP)) {
      return EnumJvmMemPoolTypeNonHeap;
    }
    throw new SnmpStatusException(10);
  }
  
  public EnumJvmMemPoolType getJvmMemPoolType()
    throws SnmpStatusException
  {
    return jvmMemPoolType(pool.getType());
  }
  
  public String getJvmMemPoolName()
    throws SnmpStatusException
  {
    return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(pool.getName());
  }
  
  public Integer getJvmMemPoolIndex()
    throws SnmpStatusException
  {
    return new Integer(jvmMemPoolIndex);
  }
  
  public EnumJvmMemPoolState getJvmMemPoolState()
    throws SnmpStatusException
  {
    if (pool.isValid()) {
      return JvmMemPoolStateValid;
    }
    return JvmMemPoolStateInvalid;
  }
  
  public synchronized Long getJvmMemPoolPeakReset()
    throws SnmpStatusException
  {
    return new Long(jvmMemPoolPeakReset);
  }
  
  public synchronized void setJvmMemPoolPeakReset(Long paramLong)
    throws SnmpStatusException
  {
    long l1 = paramLong.longValue();
    if (l1 > jvmMemPoolPeakReset)
    {
      long l2 = System.currentTimeMillis();
      pool.resetPeakUsage();
      jvmMemPoolPeakReset = l2;
      log.debug("setJvmMemPoolPeakReset", "jvmMemPoolPeakReset=" + l2);
    }
  }
  
  public void checkJvmMemPoolPeakReset(Long paramLong)
    throws SnmpStatusException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmMemPoolEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */