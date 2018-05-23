package com.sun.org.glassfish.external.statistics;

public abstract interface BoundaryStatistic
  extends Statistic
{
  public abstract long getUpperBound();
  
  public abstract long getLowerBound();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\BoundaryStatistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */