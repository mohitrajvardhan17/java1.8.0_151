package com.sun.corba.se.spi.monitoring;

import java.io.Closeable;

public abstract interface MonitoringManager
  extends Closeable
{
  public abstract MonitoredObject getRootMonitoredObject();
  
  public abstract void clearState();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoringManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */