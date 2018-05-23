package com.sun.corba.se.spi.monitoring;

public abstract class MonitoredAttributeBase
  implements MonitoredAttribute
{
  String name;
  MonitoredAttributeInfo attributeInfo;
  
  public MonitoredAttributeBase(String paramString, MonitoredAttributeInfo paramMonitoredAttributeInfo)
  {
    name = paramString;
    attributeInfo = paramMonitoredAttributeInfo;
  }
  
  MonitoredAttributeBase(String paramString)
  {
    name = paramString;
  }
  
  void setMonitoredAttributeInfo(MonitoredAttributeInfo paramMonitoredAttributeInfo)
  {
    attributeInfo = paramMonitoredAttributeInfo;
  }
  
  public void clearState() {}
  
  public abstract Object getValue();
  
  public void setValue(Object paramObject)
  {
    if (!attributeInfo.isWritable()) {
      throw new IllegalStateException("The Attribute " + name + " is not Writable...");
    }
    throw new IllegalStateException("The method implementation is not provided for the attribute " + name);
  }
  
  public MonitoredAttributeInfo getAttributeInfo()
  {
    return attributeInfo;
  }
  
  public String getName()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoredAttributeBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */