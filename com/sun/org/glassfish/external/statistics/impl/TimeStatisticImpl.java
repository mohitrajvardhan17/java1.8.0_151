package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.TimeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class TimeStatisticImpl
  extends StatisticImpl
  implements TimeStatistic, InvocationHandler
{
  private long count = 0L;
  private long maxTime = 0L;
  private long minTime = 0L;
  private long totTime = 0L;
  private final long initCount;
  private final long initMaxTime;
  private final long initMinTime;
  private final long initTotTime;
  private final TimeStatistic ts = (TimeStatistic)Proxy.newProxyInstance(TimeStatistic.class.getClassLoader(), new Class[] { TimeStatistic.class }, this);
  
  public final synchronized String toString()
  {
    return super.toString() + NEWLINE + "Count: " + getCount() + NEWLINE + "MinTime: " + getMinTime() + NEWLINE + "MaxTime: " + getMaxTime() + NEWLINE + "TotalTime: " + getTotalTime();
  }
  
  public TimeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, long paramLong4, String paramString1, String paramString2, String paramString3, long paramLong5, long paramLong6)
  {
    super(paramString1, paramString2, paramString3, paramLong5, paramLong6);
    count = paramLong1;
    initCount = paramLong1;
    maxTime = paramLong2;
    initMaxTime = paramLong2;
    minTime = paramLong3;
    initMinTime = paramLong3;
    totTime = paramLong4;
    initTotTime = paramLong4;
  }
  
  public synchronized TimeStatistic getStatistic()
  {
    return ts;
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    localMap.put("count", Long.valueOf(getCount()));
    localMap.put("maxtime", Long.valueOf(getMaxTime()));
    localMap.put("mintime", Long.valueOf(getMinTime()));
    localMap.put("totaltime", Long.valueOf(getTotalTime()));
    return localMap;
  }
  
  public synchronized void incrementCount(long paramLong)
  {
    if (count == 0L)
    {
      totTime = paramLong;
      maxTime = paramLong;
      minTime = paramLong;
    }
    else
    {
      totTime += paramLong;
      maxTime = (paramLong >= maxTime ? paramLong : maxTime);
      minTime = (paramLong >= minTime ? minTime : paramLong);
    }
    count += 1L;
    sampleTime = System.currentTimeMillis();
  }
  
  public synchronized long getCount()
  {
    return count;
  }
  
  public synchronized long getMaxTime()
  {
    return maxTime;
  }
  
  public synchronized long getMinTime()
  {
    return minTime;
  }
  
  public synchronized long getTotalTime()
  {
    return totTime;
  }
  
  public synchronized void reset()
  {
    super.reset();
    count = initCount;
    maxTime = initMaxTime;
    minTime = initMinTime;
    totTime = initTotTime;
    sampleTime = -1L;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\TimeStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */