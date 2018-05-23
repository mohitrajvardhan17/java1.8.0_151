package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;
import java.util.HashMap;

public class MonitoringManagerFactoryImpl
  implements MonitoringManagerFactory
{
  private HashMap monitoringManagerTable = new HashMap();
  
  public MonitoringManagerFactoryImpl() {}
  
  public synchronized MonitoringManager createMonitoringManager(String paramString1, String paramString2)
  {
    MonitoringManagerImpl localMonitoringManagerImpl = null;
    localMonitoringManagerImpl = (MonitoringManagerImpl)monitoringManagerTable.get(paramString1);
    if (localMonitoringManagerImpl == null)
    {
      localMonitoringManagerImpl = new MonitoringManagerImpl(paramString1, paramString2);
      monitoringManagerTable.put(paramString1, localMonitoringManagerImpl);
    }
    return localMonitoringManagerImpl;
  }
  
  public synchronized void remove(String paramString)
  {
    monitoringManagerTable.remove(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoringManagerFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */