package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.MemoryNotifInfoCompositeData;

public class MemoryNotificationInfo
{
  private final String poolName;
  private final MemoryUsage usage;
  private final long count;
  public static final String MEMORY_THRESHOLD_EXCEEDED = "java.management.memory.threshold.exceeded";
  public static final String MEMORY_COLLECTION_THRESHOLD_EXCEEDED = "java.management.memory.collection.threshold.exceeded";
  
  public MemoryNotificationInfo(String paramString, MemoryUsage paramMemoryUsage, long paramLong)
  {
    if (paramString == null) {
      throw new NullPointerException("Null poolName");
    }
    if (paramMemoryUsage == null) {
      throw new NullPointerException("Null usage");
    }
    poolName = paramString;
    usage = paramMemoryUsage;
    count = paramLong;
  }
  
  MemoryNotificationInfo(CompositeData paramCompositeData)
  {
    MemoryNotifInfoCompositeData.validateCompositeData(paramCompositeData);
    poolName = MemoryNotifInfoCompositeData.getPoolName(paramCompositeData);
    usage = MemoryNotifInfoCompositeData.getUsage(paramCompositeData);
    count = MemoryNotifInfoCompositeData.getCount(paramCompositeData);
  }
  
  public String getPoolName()
  {
    return poolName;
  }
  
  public MemoryUsage getUsage()
  {
    return usage;
  }
  
  public long getCount()
  {
    return count;
  }
  
  public static MemoryNotificationInfo from(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      return null;
    }
    if ((paramCompositeData instanceof MemoryNotifInfoCompositeData)) {
      return ((MemoryNotifInfoCompositeData)paramCompositeData).getMemoryNotifInfo();
    }
    return new MemoryNotificationInfo(paramCompositeData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\MemoryNotificationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */