package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;

public class MonitoredObjectFactoryImpl
  implements MonitoredObjectFactory
{
  public MonitoredObjectFactoryImpl() {}
  
  public MonitoredObject createMonitoredObject(String paramString1, String paramString2)
  {
    return new MonitoredObjectImpl(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoredObjectFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */