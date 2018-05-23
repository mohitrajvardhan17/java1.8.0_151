package com.sun.corba.se.spi.monitoring;

public abstract interface MonitoredAttributeInfo
{
  public abstract boolean isWritable();
  
  public abstract boolean isStatistic();
  
  public abstract Class type();
  
  public abstract String getDescription();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoredAttributeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */