package java.util;

import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class LongSummaryStatistics
  implements LongConsumer, IntConsumer
{
  private long count;
  private long sum;
  private long min = Long.MAX_VALUE;
  private long max = Long.MIN_VALUE;
  
  public LongSummaryStatistics() {}
  
  public void accept(int paramInt)
  {
    accept(paramInt);
  }
  
  public void accept(long paramLong)
  {
    count += 1L;
    sum += paramLong;
    min = Math.min(min, paramLong);
    max = Math.max(max, paramLong);
  }
  
  public void combine(LongSummaryStatistics paramLongSummaryStatistics)
  {
    count += count;
    sum += sum;
    min = Math.min(min, min);
    max = Math.max(max, max);
  }
  
  public final long getCount()
  {
    return count;
  }
  
  public final long getSum()
  {
    return sum;
  }
  
  public final long getMin()
  {
    return min;
  }
  
  public final long getMax()
  {
    return max;
  }
  
  public final double getAverage()
  {
    return getCount() > 0L ? getSum() / getCount() : 0.0D;
  }
  
  public String toString()
  {
    return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", new Object[] { getClass().getSimpleName(), Long.valueOf(getCount()), Long.valueOf(getSum()), Long.valueOf(getMin()), Double.valueOf(getAverage()), Long.valueOf(getMax()) });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\LongSummaryStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */