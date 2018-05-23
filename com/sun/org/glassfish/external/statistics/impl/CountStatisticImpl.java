package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.CountStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class CountStatisticImpl
  extends StatisticImpl
  implements CountStatistic, InvocationHandler
{
  private long count = 0L;
  private final long initCount;
  private final CountStatistic cs = (CountStatistic)Proxy.newProxyInstance(CountStatistic.class.getClassLoader(), new Class[] { CountStatistic.class }, this);
  
  public CountStatisticImpl(long paramLong1, String paramString1, String paramString2, String paramString3, long paramLong2, long paramLong3)
  {
    super(paramString1, paramString2, paramString3, paramLong3, paramLong2);
    count = paramLong1;
    initCount = paramLong1;
  }
  
  public CountStatisticImpl(String paramString1, String paramString2, String paramString3)
  {
    this(0L, paramString1, paramString2, paramString3, -1L, System.currentTimeMillis());
  }
  
  public synchronized CountStatistic getStatistic()
  {
    return cs;
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    localMap.put("count", Long.valueOf(getCount()));
    return localMap;
  }
  
  public synchronized String toString()
  {
    return super.toString() + NEWLINE + "Count: " + getCount();
  }
  
  public synchronized long getCount()
  {
    return count;
  }
  
  public synchronized void setCount(long paramLong)
  {
    count = paramLong;
    sampleTime = System.currentTimeMillis();
  }
  
  public synchronized void increment()
  {
    count += 1L;
    sampleTime = System.currentTimeMillis();
  }
  
  public synchronized void increment(long paramLong)
  {
    count += paramLong;
    sampleTime = System.currentTimeMillis();
  }
  
  public synchronized void decrement()
  {
    count -= 1L;
    sampleTime = System.currentTimeMillis();
  }
  
  public synchronized void reset()
  {
    super.reset();
    count = initCount;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\CountStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */