package java.util;

import java.util.function.DoubleConsumer;

public class DoubleSummaryStatistics
  implements DoubleConsumer
{
  private long count;
  private double sum;
  private double sumCompensation;
  private double simpleSum;
  private double min = Double.POSITIVE_INFINITY;
  private double max = Double.NEGATIVE_INFINITY;
  
  public DoubleSummaryStatistics() {}
  
  public void accept(double paramDouble)
  {
    count += 1L;
    simpleSum += paramDouble;
    sumWithCompensation(paramDouble);
    min = Math.min(min, paramDouble);
    max = Math.max(max, paramDouble);
  }
  
  public void combine(DoubleSummaryStatistics paramDoubleSummaryStatistics)
  {
    count += count;
    simpleSum += simpleSum;
    sumWithCompensation(sum);
    sumWithCompensation(sumCompensation);
    min = Math.min(min, min);
    max = Math.max(max, max);
  }
  
  private void sumWithCompensation(double paramDouble)
  {
    double d1 = paramDouble - sumCompensation;
    double d2 = sum + d1;
    sumCompensation = (d2 - sum - d1);
    sum = d2;
  }
  
  public final long getCount()
  {
    return count;
  }
  
  public final double getSum()
  {
    double d = sum + sumCompensation;
    if ((Double.isNaN(d)) && (Double.isInfinite(simpleSum))) {
      return simpleSum;
    }
    return d;
  }
  
  public final double getMin()
  {
    return min;
  }
  
  public final double getMax()
  {
    return max;
  }
  
  public final double getAverage()
  {
    return getCount() > 0L ? getSum() / getCount() : 0.0D;
  }
  
  public String toString()
  {
    return String.format("%s{count=%d, sum=%f, min=%f, average=%f, max=%f}", new Object[] { getClass().getSimpleName(), Long.valueOf(getCount()), Double.valueOf(getSum()), Double.valueOf(getMin()), Double.valueOf(getAverage()), Double.valueOf(getMax()) });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\DoubleSummaryStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */