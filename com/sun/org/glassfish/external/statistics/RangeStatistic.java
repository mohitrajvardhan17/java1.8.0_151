package com.sun.org.glassfish.external.statistics;

public abstract interface RangeStatistic
  extends Statistic
{
  public abstract long getHighWaterMark();
  
  public abstract long getLowWaterMark();
  
  public abstract long getCurrent();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\RangeStatistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */