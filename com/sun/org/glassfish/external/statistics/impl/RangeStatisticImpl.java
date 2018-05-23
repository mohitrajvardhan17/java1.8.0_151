package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.RangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class RangeStatisticImpl
  extends StatisticImpl
  implements RangeStatistic, InvocationHandler
{
  private long currentVal = 0L;
  private long highWaterMark = Long.MIN_VALUE;
  private long lowWaterMark = Long.MAX_VALUE;
  private final long initCurrentVal;
  private final long initHighWaterMark;
  private final long initLowWaterMark;
  private final RangeStatistic rs = (RangeStatistic)Proxy.newProxyInstance(RangeStatistic.class.getClassLoader(), new Class[] { RangeStatistic.class }, this);
  
  public RangeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, String paramString1, String paramString2, String paramString3, long paramLong4, long paramLong5)
  {
    super(paramString1, paramString2, paramString3, paramLong4, paramLong5);
    currentVal = paramLong1;
    initCurrentVal = paramLong1;
    highWaterMark = paramLong2;
    initHighWaterMark = paramLong2;
    lowWaterMark = paramLong3;
    initLowWaterMark = paramLong3;
  }
  
  public synchronized RangeStatistic getStatistic()
  {
    return rs;
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    localMap.put("current", Long.valueOf(getCurrent()));
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
  
  public synchronized void reset()
  {
    super.reset();
    currentVal = initCurrentVal;
    highWaterMark = initHighWaterMark;
    lowWaterMark = initLowWaterMark;
    sampleTime = -1L;
  }
  
  public synchronized String toString()
  {
    return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\RangeStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */