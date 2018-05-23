package com.sun.corba.se.spi.monitoring;

public abstract interface MonitoredAttribute
{
  public abstract MonitoredAttributeInfo getAttributeInfo();
  
  public abstract void setValue(Object paramObject);
  
  public abstract Object getValue();
  
  public abstract String getName();
  
  public abstract void clearState();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoredAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */