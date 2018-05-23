package com.sun.org.glassfish.external.statistics;

public abstract interface TimeStatistic
  extends Statistic
{
  public abstract long getCount();
  
  public abstract long getMaxTime();
  
  public abstract long getMinTime();
  
  public abstract long getTotalTime();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\TimeStatistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */