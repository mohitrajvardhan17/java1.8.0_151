package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.util.Collection;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;

public class LegacyServerSocketManagerImpl
  implements LegacyServerSocketManager
{
  protected ORB orb;
  private ORBUtilSystemException wrapper;
  
  public LegacyServerSocketManagerImpl(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
  }
  
  public int legacyGetTransientServerPort(String paramString)
  {
    return legacyGetServerPort(paramString, false);
  }
  
  public synchronized int legacyGetPersistentServerPort(String paramString)
  {
    if (orb.getORBData().getServerIsORBActivated()) {
      return legacyGetServerPort(paramString, true);
    }
    if (orb.getORBData().getPersistentPortInitialized()) {
      return orb.getORBData().getPersistentServerPort();
    }
    throw wrapper.persistentServerportNotSet(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public synchronized int legacyGetTransientOrPersistentServerPort(String paramString)
  {
    return legacyGetServerPort(paramString, orb.getORBData().getServerIsORBActivated());
  }
  
  public synchronized LegacyServerSocketEndPointInfo legacyGetEndpoint(String paramString)
  {
    Iterator localIterator = getAcceptorIterator();
    while (localIterator.hasNext())
    {
      LegacyServerSocketEndPointInfo localLegacyServerSocketEndPointInfo = cast(localIterator.next());
      if ((localLegacyServerSocketEndPointInfo != null) && (paramString.equals(localLegacyServerSocketEndPointInfo.getName()))) {
        return localLegacyServerSocketEndPointInfo;
      }
    }
    throw new INTERNAL("No acceptor for: " + paramString);
  }
  
  public boolean legacyIsLocalServerPort(int paramInt)
  {
    Iterator localIterator = getAcceptorIterator();
    while (localIterator.hasNext())
    {
      LegacyServerSocketEndPointInfo localLegacyServerSocketEndPointInfo = cast(localIterator.next());
      if ((localLegacyServerSocketEndPointInfo != null) && (localLegacyServerSocketEndPointInfo.getPort() == paramInt)) {
        return true;
      }
    }
    return false;
  }
  
  private int legacyGetServerPort(String paramString, boolean paramBoolean)
  {
    Iterator localIterator = getAcceptorIterator();
    while (localIterator.hasNext())
    {
      LegacyServerSocketEndPointInfo localLegacyServerSocketEndPointInfo = cast(localIterator.next());
      if ((localLegacyServerSocketEndPointInfo != null) && (localLegacyServerSocketEndPointInfo.getType().equals(paramString)))
      {
        if (paramBoolean) {
          return localLegacyServerSocketEndPointInfo.getLocatorPort();
        }
        return localLegacyServerSocketEndPointInfo.getPort();
      }
    }
    return -1;
  }
  
  private Iterator getAcceptorIterator()
  {
    Collection localCollection = orb.getCorbaTransportManager().getAcceptors(null, null);
    if (localCollection != null) {
      return localCollection.iterator();
    }
    throw wrapper.getServerPortCalledBeforeEndpointsInitialized();
  }
  
  private LegacyServerSocketEndPointInfo cast(Object paramObject)
  {
    if ((paramObject instanceof LegacyServerSocketEndPointInfo)) {
      return (LegacyServerSocketEndPointInfo)paramObject;
    }
    return null;
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("LegacyServerSocketManagerImpl", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\LegacyServerSocketManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */