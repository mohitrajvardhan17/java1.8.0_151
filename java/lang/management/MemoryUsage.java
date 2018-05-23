package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.MemoryUsageCompositeData;

public class MemoryUsage
{
  private final long init;
  private final long used;
  private final long committed;
  private final long max;
  
  public MemoryUsage(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    if (paramLong1 < -1L) {
      throw new IllegalArgumentException("init parameter = " + paramLong1 + " is negative but not -1.");
    }
    if (paramLong4 < -1L) {
      throw new IllegalArgumentException("max parameter = " + paramLong4 + " is negative but not -1.");
    }
    if (paramLong2 < 0L) {
      throw new IllegalArgumentException("used parameter = " + paramLong2 + " is negative.");
    }
    if (paramLong3 < 0L) {
      throw new IllegalArgumentException("committed parameter = " + paramLong3 + " is negative.");
    }
    if (paramLong2 > paramLong3) {
      throw new IllegalArgumentException("used = " + paramLong2 + " should be <= committed = " + paramLong3);
    }
    if ((paramLong4 >= 0L) && (paramLong3 > paramLong4)) {
      throw new IllegalArgumentException("committed = " + paramLong3 + " should be < max = " + paramLong4);
    }
    init = paramLong1;
    used = paramLong2;
    committed = paramLong3;
    max = paramLong4;
  }
  
  private MemoryUsage(CompositeData paramCompositeData)
  {
    MemoryUsageCompositeData.validateCompositeData(paramCompositeData);
    init = MemoryUsageCompositeData.getInit(paramCompositeData);
    used = MemoryUsageCompositeData.getUsed(paramCompositeData);
    committed = MemoryUsageCompositeData.getCommitted(paramCompositeData);
    max = MemoryUsageCompositeData.getMax(paramCompositeData);
  }
  
  public long getInit()
  {
    return init;
  }
  
  public long getUsed()
  {
    return used;
  }
  
  public long getCommitted()
  {
    return committed;
  }
  
  public long getMax()
  {
    return max;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("init = " + init + "(" + (init >> 10) + "K) ");
    localStringBuffer.append("used = " + used + "(" + (used >> 10) + "K) ");
    localStringBuffer.append("committed = " + committed + "(" + (committed >> 10) + "K) ");
    localStringBuffer.append("max = " + max + "(" + (max >> 10) + "K)");
    return localStringBuffer.toString();
  }
  
  public static MemoryUsage from(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      return null;
    }
    if ((paramCompositeData instanceof MemoryUsageCompositeData)) {
      return ((MemoryUsageCompositeData)paramCompositeData).getMemoryUsage();
    }
    return new MemoryUsage(paramCompositeData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\MemoryUsage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */