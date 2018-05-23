package com.sun.corba.se.spi.monitoring;

import java.util.Collection;

public abstract interface MonitoredObject
{
  public abstract String getName();
  
  public abstract String getDescription();
  
  public abstract void addChild(MonitoredObject paramMonitoredObject);
  
  public abstract void removeChild(String paramString);
  
  public abstract MonitoredObject getChild(String paramString);
  
  public abstract Collection getChildren();
  
  public abstract void setParent(MonitoredObject paramMonitoredObject);
  
  public abstract MonitoredObject getParent();
  
  public abstract void addAttribute(MonitoredAttribute paramMonitoredAttribute);
  
  public abstract void removeAttribute(String paramString);
  
  public abstract MonitoredAttribute getAttribute(String paramString);
  
  public abstract Collection getAttributes();
  
  public abstract void clearState();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoredObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */