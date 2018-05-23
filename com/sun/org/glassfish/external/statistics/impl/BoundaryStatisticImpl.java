package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.BoundaryStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class BoundaryStatisticImpl
  extends StatisticImpl
  implements BoundaryStatistic, InvocationHandler
{
  private final long lowerBound;
  private final long upperBound;
  private final BoundaryStatistic bs = (BoundaryStatistic)Proxy.newProxyInstance(BoundaryStatistic.class.getClassLoader(), new Class[] { BoundaryStatistic.class }, this);
  
  public BoundaryStatisticImpl(long paramLong1, long paramLong2, String paramString1, String paramString2, String paramString3, long paramLong3, long paramLong4)
  {
    super(paramString1, paramString2, paramString3, paramLong3, paramLong4);
    upperBound = paramLong2;
    lowerBound = paramLong1;
  }
  
  public synchronized BoundaryStatistic getStatistic()
  {
    return bs;
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    localMap.put("lowerbound", Long.valueOf(getLowerBound()));
    localMap.put("upperbound", Long.valueOf(getUpperBound()));
    return localMap;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\BoundaryStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */