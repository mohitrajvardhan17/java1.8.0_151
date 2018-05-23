package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import java.util.ArrayList;
import java.util.Collection;

public class CorbaInboundConnectionCacheImpl
  extends CorbaConnectionCacheBase
  implements InboundConnectionCache
{
  protected Collection connectionCache = new ArrayList();
  private Acceptor acceptor;
  
  public CorbaInboundConnectionCacheImpl(ORB paramORB, Acceptor paramAcceptor)
  {
    super(paramORB, paramAcceptor.getConnectionCacheType(), ((CorbaAcceptor)paramAcceptor).getMonitoringName());
    acceptor = paramAcceptor;
    if (transportDebugFlag) {
      dprint(": " + paramAcceptor);
    }
  }
  
  public void close()
  {
    super.close();
    if (orb.transportDebugFlag) {
      dprint(".close: " + acceptor);
    }
    acceptor.close();
  }
  
  public Connection get(Acceptor paramAcceptor)
  {
    throw wrapper.methodShouldNotBeCalled();
  }
  
  public Acceptor getAcceptor()
  {
    return acceptor;
  }
  
  public void put(Acceptor paramAcceptor, Connection paramConnection)
  {
    if (orb.transportDebugFlag) {
      dprint(".put: " + paramAcceptor + " " + paramConnection);
    }
    synchronized (backingStore())
    {
      connectionCache.add(paramConnection);
      paramConnection.setConnectionCache(this);
      dprintStatistics();
    }
  }
  
  public void remove(Connection paramConnection)
  {
    if (orb.transportDebugFlag) {
      dprint(".remove: " + paramConnection);
    }
    synchronized (backingStore())
    {
      connectionCache.remove(paramConnection);
      dprintStatistics();
    }
  }
  
  public Collection values()
  {
    return connectionCache;
  }
  
  protected Object backingStore()
  {
    return connectionCache;
  }
  
  protected void registerWithMonitoring()
  {
    MonitoredObject localMonitoredObject1 = orb.getMonitoringManager().getRootMonitoredObject();
    MonitoredObject localMonitoredObject2 = localMonitoredObject1.getChild("Connections");
    if (localMonitoredObject2 == null)
    {
      localMonitoredObject2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Connections", "Statistics on inbound/outbound connections");
      localMonitoredObject1.addChild(localMonitoredObject2);
    }
    MonitoredObject localMonitoredObject3 = localMonitoredObject2.getChild("Inbound");
    if (localMonitoredObject3 == null)
    {
      localMonitoredObject3 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Inbound", "Statistics on inbound connections");
      localMonitoredObject2.addChild(localMonitoredObject3);
    }
    MonitoredObject localMonitoredObject4 = localMonitoredObject3.getChild(getMonitoringName());
    if (localMonitoredObject4 == null)
    {
      localMonitoredObject4 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(getMonitoringName(), "Connection statistics");
      localMonitoredObject3.addChild(localMonitoredObject4);
    }
    Object localObject = new LongMonitoredAttributeBase("NumberOfConnections", "The total number of connections")
    {
      public Object getValue()
      {
        return new Long(numberOfConnections());
      }
    };
    localMonitoredObject4.addAttribute((MonitoredAttribute)localObject);
    localObject = new LongMonitoredAttributeBase("NumberOfIdleConnections", "The number of idle connections")
    {
      public Object getValue()
      {
        return new Long(numberOfIdleConnections());
      }
    };
    localMonitoredObject4.addAttribute((MonitoredAttribute)localObject);
    localObject = new LongMonitoredAttributeBase("NumberOfBusyConnections", "The number of busy connections")
    {
      public Object getValue()
      {
        return new Long(numberOfBusyConnections());
      }
    };
    localMonitoredObject4.addAttribute((MonitoredAttribute)localObject);
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaInboundConnectionCacheImpl", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\CorbaInboundConnectionCacheImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */