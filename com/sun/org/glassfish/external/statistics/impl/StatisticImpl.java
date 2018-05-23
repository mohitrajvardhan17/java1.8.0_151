package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.Statistic;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StatisticImpl
  implements Statistic
{
  private final String statisticName;
  private final String statisticUnit;
  private final String statisticDesc;
  protected long sampleTime = -1L;
  private long startTime;
  public static final String UNIT_COUNT = "count";
  public static final String UNIT_SECOND = "second";
  public static final String UNIT_MILLISECOND = "millisecond";
  public static final String UNIT_MICROSECOND = "microsecond";
  public static final String UNIT_NANOSECOND = "nanosecond";
  public static final String START_TIME = "starttime";
  public static final String LAST_SAMPLE_TIME = "lastsampletime";
  protected final Map<String, Object> statMap = new ConcurrentHashMap();
  protected static final String NEWLINE = System.getProperty("line.separator");
  
  protected StatisticImpl(String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2)
  {
    if (isValidString(paramString1)) {
      statisticName = paramString1;
    } else {
      statisticName = "name";
    }
    if (isValidString(paramString2)) {
      statisticUnit = paramString2;
    } else {
      statisticUnit = "unit";
    }
    if (isValidString(paramString3)) {
      statisticDesc = paramString3;
    } else {
      statisticDesc = "description";
    }
    startTime = paramLong1;
    sampleTime = paramLong2;
  }
  
  protected StatisticImpl(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1, paramString2, paramString3, System.currentTimeMillis(), System.currentTimeMillis());
  }
  
  public synchronized Map getStaticAsMap()
  {
    if (isValidString(statisticName)) {
      statMap.put("name", statisticName);
    }
    if (isValidString(statisticUnit)) {
      statMap.put("unit", statisticUnit);
    }
    if (isValidString(statisticDesc)) {
      statMap.put("description", statisticDesc);
    }
    statMap.put("starttime", Long.valueOf(startTime));
    statMap.put("lastsampletime", Long.valueOf(sampleTime));
    return statMap;
  }
  
  public String getName()
  {
    return statisticName;
  }
  
  public String getDescription()
  {
    return statisticDesc;
  }
  
  public String getUnit()
  {
    return statisticUnit;
  }
  
  public synchronized long getLastSampleTime()
  {
    return sampleTime;
  }
  
  public synchronized long getStartTime()
  {
    return startTime;
  }
  
  public synchronized void reset()
  {
    startTime = System.currentTimeMillis();
  }
  
  public synchronized String toString()
  {
    return "Statistic " + getClass().getName() + NEWLINE + "Name: " + getName() + NEWLINE + "Description: " + getDescription() + NEWLINE + "Unit: " + getUnit() + NEWLINE + "LastSampleTime: " + getLastSampleTime() + NEWLINE + "StartTime: " + getStartTime();
  }
  
  protected static boolean isValidString(String paramString)
  {
    return (paramString != null) && (paramString.length() > 0);
  }
  
  protected void checkMethod(Method paramMethod)
  {
    if ((paramMethod == null) || (paramMethod.getDeclaringClass() == null) || (!Statistic.class.isAssignableFrom(paramMethod.getDeclaringClass())) || (Modifier.isStatic(paramMethod.getModifiers()))) {
      throw new RuntimeException("Invalid method on invoke");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\StatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */