package com.sun.corba.se.spi.monitoring;

public abstract class LongMonitoredAttributeBase
  extends MonitoredAttributeBase
{
  public LongMonitoredAttributeBase(String paramString1, String paramString2)
  {
    super(paramString1);
    MonitoredAttributeInfoFactory localMonitoredAttributeInfoFactory = MonitoringFactories.getMonitoredAttributeInfoFactory();
    MonitoredAttributeInfo localMonitoredAttributeInfo = localMonitoredAttributeInfoFactory.createMonitoredAttributeInfo(paramString2, Long.class, false, false);
    setMonitoredAttributeInfo(localMonitoredAttributeInfo);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\LongMonitoredAttributeBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */