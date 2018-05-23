package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.BoundedRangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class BoundedRangeStatisticImpl
  extends StatisticImpl
  implements BoundedRangeStatistic, InvocationHandler
{
  private long lowerBound = 0L;
  private long upperBound = 0L;
  private long currentVal = 0L;
  private long highWaterMark = Long.MIN_VALUE;
  private long lowWaterMark = Long.MAX_VALUE;
  private final long initLowerBound;
  private final long initUpperBound;
  private final long initCurrentVal;
  private final long initHighWaterMark;
  private final long initLowWaterMark;
  private final BoundedRangeStatistic bs = (BoundedRangeStatistic)Proxy.newProxyInstance(BoundedRangeStatistic.class.getClassLoader(), new Class[] { BoundedRangeStatistic.class }, this);
  
  public synchronized String toString()
  {
    return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark() + NEWLINE + "LowerBound: " + getLowerBound() + NEWLINE + "UpperBound: " + getUpperBound();
  }
  
  public BoundedRangeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, String paramString1, String paramString2, String paramString3, long paramLong6, long paramLong7)
  {
    super(paramString1, paramString2, paramString3, paramLong6, paramLong7);
    currentVal = paramLong1;
    initCurrentVal = paramLong1;
    highWaterMark = paramLong2;
    initHighWaterMark = paramLong2;
    lowWaterMark = paramLong3;
    initLowWaterMark = paramLong3;
    upperBound = paramLong4;
    initUpperBound = paramLong4;
    lowerBound = paramLong5;
    initLowerBound = paramLong5;
  }
  
  public synchronized BoundedRangeStatistic getStatistic()
  {
    return bs;
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    localMap.put("current", Long.valueOf(getCurrent()));
    localMap.put("lowerbound", Long.valueOf(getLowerBound()));
    localMap.put("upperbound", Long.valueOf(getUpperBound()));
    localMap.put("lowwatermark", Long.valueOf(getLowWaterMark()));
    localMap.put("highwatermark", Long.valueOf(getHighWaterMark()));
    return localMap;
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
    sampleTime = System.currentTimeMillis();
  }
  
  public synchronized long getHighWaterMark()
  {
    return highWaterMark;
  }
  
  public synchronized void setHighWaterMark(long paramLong)
  {
    highWaterMark = paramLong;
  }
  
  public synchronized long getLowWaterMark()
  {
    return lowWaterMark;
  }
  
  public synchronized void setLowWaterMark(long paramLong)
  {
    lowWaterMark = paramLong;
  }
  
  public synchronized long getLowerBound()
  {
    return lowerBound;
  }
  
  public synchronized long getUpperBound()
  {
    return upperBound;
  }
  
  public synchronized void reset()
  {
    super.reset();
    lowerBound = initLowerBound;
    upperBound = initUpperBound;
    currentVal = initCurrentVal;
    highWaterMark = initHighWaterMark;
    lowWaterMark = initLowWaterMark;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\BoundedRangeStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */