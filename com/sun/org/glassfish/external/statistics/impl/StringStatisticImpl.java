package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.StringStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class StringStatisticImpl
  extends StatisticImpl
  implements StringStatistic, InvocationHandler
{
  private volatile String str = null;
  private final String initStr;
  private final StringStatistic ss = (StringStatistic)Proxy.newProxyInstance(StringStatistic.class.getClassLoader(), new Class[] { StringStatistic.class }, this);
  
  public StringStatisticImpl(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2)
  {
    super(paramString2, paramString3, paramString4, paramLong2, paramLong1);
    str = paramString1;
    initStr = paramString1;
  }
  
  public StringStatisticImpl(String paramString1, String paramString2, String paramString3)
  {
    this("", paramString1, paramString2, paramString3, System.currentTimeMillis(), System.currentTimeMillis());
  }
  
  public synchronized StringStatistic getStatistic()
  {
    return ss;
  }
  
  public synchronized Map getStaticAsMap()
  {
    Map localMap = super.getStaticAsMap();
    if (getCurrent() != null) {
      localMap.put("current", getCurrent());
    }
    return localMap;
  }
  
  public synchronized String toString()
  {
    return super.toString() + NEWLINE + "Current-value: " + getCurrent();
  }
  
  public String getCurrent()
  {
    return str;
  }
  
  public void setCurrent(String paramString)
  {
    str = paramString;
    sampleTime = System.currentTimeMillis();
  }
  
  public synchronized void reset()
  {
    super.reset();
    str = initStr;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\StringStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */