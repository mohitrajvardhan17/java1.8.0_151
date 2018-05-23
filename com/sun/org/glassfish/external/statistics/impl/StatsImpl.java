package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.Statistic;
import com.sun.org.glassfish.external.statistics.Stats;
import java.util.ArrayList;

public final class StatsImpl
  implements Stats
{
  private final StatisticImpl[] statArray;
  
  protected StatsImpl(StatisticImpl[] paramArrayOfStatisticImpl)
  {
    statArray = paramArrayOfStatisticImpl;
  }
  
  public synchronized Statistic getStatistic(String paramString)
  {
    Object localObject = null;
    for (StatisticImpl localStatisticImpl : statArray) {
      if (localStatisticImpl.getName().equals(paramString))
      {
        localObject = localStatisticImpl;
        break;
      }
    }
    return (Statistic)localObject;
  }
  
  public synchronized String[] getStatisticNames()
  {
    ArrayList localArrayList = new ArrayList();
    for (Object localObject2 : statArray) {
      localArrayList.add(((Statistic)localObject2).getName());
    }
    ??? = new String[localArrayList.size()];
    return (String[])localArrayList.toArray((Object[])???);
  }
  
  public synchronized Statistic[] getStatistics()
  {
    return statArray;
  }
  
  public synchronized void reset()
  {
    for (StatisticImpl localStatisticImpl : statArray) {
      localStatisticImpl.reset();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\impl\StatsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */