package jdk.management.resource;

public class ThrottledMeter
  extends NotifyingMeter
{
  private volatile long ratePerSec;
  private final Object mutex;
  private long availableBytes;
  private long availableTimestamp;
  
  public static ThrottledMeter create(ResourceType paramResourceType, long paramLong, ResourceApprover paramResourceApprover)
  {
    return new ThrottledMeter(paramResourceType, paramLong, null, paramResourceApprover);
  }
  
  public static ThrottledMeter create(ResourceType paramResourceType, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover)
  {
    return new ThrottledMeter(paramResourceType, Long.MAX_VALUE, paramResourceRequest, paramResourceApprover);
  }
  
  public static ThrottledMeter create(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover)
  {
    return new ThrottledMeter(paramResourceType, paramLong, paramResourceRequest, paramResourceApprover);
  }
  
  ThrottledMeter(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover)
  {
    super(paramResourceType, paramResourceRequest, paramResourceApprover);
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("ratePerSec must be greater than zero");
    }
    ratePerSec = paramLong;
    mutex = new Object();
    availableBytes = 0L;
    availableTimestamp = 0L;
  }
  
  public long validate(long paramLong1, long paramLong2, ResourceId paramResourceId)
  {
    long l1 = super.validate(paramLong1, paramLong2, paramResourceId);
    if (l1 <= 0L) {
      return l1;
    }
    synchronized (mutex)
    {
      while (availableBytes - paramLong2 < 0L)
      {
        long l2 = ratePerSec;
        long l3 = availableBytes;
        long l4 = System.currentTimeMillis();
        long l5 = Math.max(l4 - availableTimestamp, 0L);
        long l6 = l2 * l5 / 1000L;
        availableBytes = Math.min(availableBytes + l6, l2);
        availableTimestamp = l4;
        if ((availableBytes - paramLong2 >= 0L) || ((paramLong2 > l2) && (l3 > 0L))) {
          break;
        }
        long l7 = Math.min(paramLong2 - availableBytes, l2);
        l5 = l7 * 1000L / l2;
        try
        {
          mutex.wait(Math.max(l5, 10L));
        }
        catch (InterruptedException localInterruptedException)
        {
          return 0L;
        }
      }
      availableBytes -= paramLong2;
    }
    return paramLong2;
  }
  
  public final long getCurrentRate()
  {
    synchronized (mutex)
    {
      long l1 = ratePerSec;
      long l2 = System.currentTimeMillis();
      long l3 = l2 - availableTimestamp;
      long l4 = l1 * l3 / 1000L;
      availableBytes = Math.min(availableBytes + l4, l1);
      availableTimestamp = l2;
      long l5 = l1 - availableBytes;
      return l5;
    }
  }
  
  public final synchronized long getRatePerSec()
  {
    return ratePerSec;
  }
  
  public final synchronized long setRatePerSec(long paramLong)
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("ratePerSec must be greater than zero");
    }
    long l = paramLong;
    ratePerSec = paramLong;
    return l;
  }
  
  public String toString()
  {
    return super.toString() + "; ratePerSec: " + Long.toString(ratePerSec) + "; currentRate: " + Long.toString(getCurrentRate());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\ThrottledMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */