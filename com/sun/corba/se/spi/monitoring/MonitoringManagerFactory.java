package com.sun.corba.se.spi.monitoring;

public abstract interface MonitoringManagerFactory
{
  public abstract MonitoringManager createMonitoringManager(String paramString1, String paramString2);
  
  public abstract void remove(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoringManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */