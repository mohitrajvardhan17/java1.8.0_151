package com.sun.org.glassfish.external.statistics;

public abstract interface Statistic
{
  public abstract String getName();
  
  public abstract String getUnit();
  
  public abstract String getDescription();
  
  public abstract long getStartTime();
  
  public abstract long getLastSampleTime();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\statistics\Statistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */