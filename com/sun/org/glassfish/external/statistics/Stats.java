package com.sun.org.glassfish.external.statistics;

public abstract interface Stats
{
  public abstract Statistic getStatistic(String paramString);
  
  public abstract String[] getStatisticNames();
  
  public abstract Statistic[] getStatistics();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\Stats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */