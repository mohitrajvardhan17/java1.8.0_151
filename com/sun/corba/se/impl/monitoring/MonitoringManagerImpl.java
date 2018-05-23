package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;

public class MonitoringManagerImpl
  implements MonitoringManager
{
  private final MonitoredObject rootMonitoredObject;
  
  MonitoringManagerImpl(String paramString1, String paramString2)
  {
    MonitoredObjectFactory localMonitoredObjectFactory = MonitoringFactories.getMonitoredObjectFactory();
    rootMonitoredObject = localMonitoredObjectFactory.createMonitoredObject(paramString1, paramString2);
  }
  
  public void clearState()
  {
    rootMonitoredObject.clearState();
  }
  
  public MonitoredObject getRootMonitoredObject()
  {
    return rootMonitoredObject;
  }
  
  public void close()
  {
    MonitoringManagerFactory localMonitoringManagerFactory = MonitoringFactories.getMonitoringManagerFactory();
    localMonitoringManagerFactory.remove(rootMonitoredObject.getName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoringManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */