package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.AverageRangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class AverageRangeStatisticImpl
  extends StatisticImpl
  implements AverageRangeStatistic, InvocationHandler
{
  private long currentVal = 0L;
  private long highWaterMark = Long.MIN_VALUE;
  private long lowWaterMark = Long.MAX_VALUE;
  private long numberOfSamples = 0L;
  private long runningTotal = 0L;
  private final long initCurrentVal;
  private final long initHighWaterMark;
  private final long initLowWaterMark;
  private final long initNumberOfSamples;
  private final long initRunningTotal;
  private final AverageRangeStatistic as = (AverageRangeStatistic)Proxy.newProxyInstance(AverageRangeStatistic.class.getClassLoader(), new Class[] { AverageRangeStatistic.class }, this);
  
  public AverageRangeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, String paramString1, String paramString2, String paramString3, long paramLong4, long paramLong5)
  {
    super(paramString1, paramString2, paramString3, paramLong4, paramLong5);
    currentVal = paramLong1;
    initCurrentVal = paramLong1;
    highWaterMark = paramLong2;
    initHighWaterMark = paramLong2;
    lowWaterMark = paramLong3;
    initLowWaterMark = paramLong3;
    numberOfSamples = 0L;
    initNumberOfSamples = numberOfSamples;
    runningTotal = 0L;
    initRunningTotal = runningTotal;
  }
  
  public synchronized AverageRangeStatistic getStatistic()
  {
    return as;
  }
  
  public synchronized String toString()
  {
    return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark() + NEWLINE + "Average:" + getAverage();
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    localMap.put("current", Long.valueOf(getCurrent()));
    localMap.put("lowwatermark", Long.valueOf(getLowWaterMark()));
    localMap.put("highwatermark", Long.valueOf(getHighWaterMark()));
    localMap.put("average", Long.valueOf(getAverage()));
    return localMap;
  }
  
  public synchronized void reset()
  {
    super.reset();
    currentVal = initCurrentVal;
    highWaterMark = initHighWaterMark;
    lowWaterMark = initLowWaterMark;
    numberOfSamples = initNumberOfSamples;
    runningTotal = initRunningTotal;
    sampleTime = -1L;
  }
  
  public synchronized long getAverage()
  {
    if (numberOfSamples == 0L) {
      return -1L;
    }
    return runningTotal / numberOfSamples;
  }
  
  public synchronized long getCurrent()
  {
    return currentVal;
  }
  
  public synchronized void setCurrent(long paramLong)
  {
    currentVal = paramLong;
    lowWaterMark = (paramLong >= lowWaterMark ? lowWaterMark : paramLong);
    highWaterMark = (paramLong >= highWaterMark ? paramLong : highWaterMark);
    numberOfSamples += 1L;
    runningTotal += paramLong;
    sampleTime = System.currentTimeMillis();
  }
  
  public synchronized long getHighWaterMark()
  {
    return highWaterMark;
  }
  
  public synchronized long getLowWaterMark()
  {
    return lowWaterMark;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    checkMethod(paramMethod);
    Object localObject;
    try
    {
      localObject = paramMethod.invoke(this, paramArrayOfObject);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw localInvocationTargetException.getTargetException();
    }
    catch (Exception localException)
    {
      throw new RuntimeException("unexpected invocation exception: " + localException.getMessage());
    }
    return localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\AverageRangeStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */