package java.util;

import java.util.function.IntConsumer;

public class IntSummaryStatistics
  implements IntConsumer
{
  private long count;
  private long sum;
  private int min = Integer.MAX_VALUE;
  private int max = Integer.MIN_VALUE;
  
  public IntSummaryStatistics() {}
  
  public void accept(int paramInt)
  {
    count += 1L;
    sum += paramInt;
    min = Math.min(min, paramInt);
    max = Math.max(max, paramInt);
  }
  
  public void combine(IntSummaryStatistics paramIntSummaryStatistics)
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
  
  public final int getMin()
  {
    return min;
  }
  
  public final int getMax()
  {
    return max;
  }
  
  public final double getAverage()
  {
    return getCount() > 0L ? getSum() / getCount() : 0.0D;
  }
  
  public String toString()
  {
    return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", new Object[] { getClass().getSimpleName(), Long.valueOf(getCount()), Long.valueOf(getSum()), Integer.valueOf(getMin()), Double.valueOf(getAverage()), Integer.valueOf(getMax()) });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\IntSummaryStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */