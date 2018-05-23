package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaConnectionCache;
import java.util.Collection;
import java.util.Iterator;

public abstract class CorbaConnectionCacheBase
  implements ConnectionCache, CorbaConnectionCache
{
  protected ORB orb;
  protected long timestamp = 0L;
  protected String cacheType;
  protected String monitoringName;
  protected ORBUtilSystemException wrapper;
  
  protected CorbaConnectionCacheBase(ORB paramORB, String paramString1, String paramString2)
  {
    orb = paramORB;
    cacheType = paramString1;
    monitoringName = paramString2;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    registerWithMonitoring();
    dprintCreation();
  }
  
  public String getCacheType()
  {
    return cacheType;
  }
  
  public synchronized void stampTime(Connection paramConnection)
  {
    paramConnection.setTimeStamp(timestamp++);
  }
  
  /* Error */
  public long numberOfConnections()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 175	com/sun/corba/se/impl/transport/CorbaConnectionCacheBase:backingStore	()Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: invokevirtual 179	com/sun/corba/se/impl/transport/CorbaConnectionCacheBase:values	()Ljava/util/Collection;
    //   11: invokeinterface 196 1 0
    //   16: i2l
    //   17: aload_1
    //   18: monitorexit
    //   19: lreturn
    //   20: astore_2
    //   21: aload_1
    //   22: monitorexit
    //   23: aload_2
    //   24: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	CorbaConnectionCacheBase
    //   5	17	1	Ljava/lang/Object;	Object
    //   20	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	19	20	finally
    //   20	23	20	finally
  }
  
  public void close()
  {
    synchronized (backingStore())
    {
      Iterator localIterator = values().iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = localIterator.next();
        ((CorbaConnection)localObject1).closeConnectionResources();
      }
    }
  }
  
  public long numberOfIdleConnections()
  {
    long l = 0L;
    synchronized (backingStore())
    {
      Iterator localIterator = values().iterator();
      while (localIterator.hasNext()) {
        if (!((Connection)localIterator.next()).isBusy()) {
          l += 1L;
        }
      }
    }
    return l;
  }
  
  public long numberOfBusyConnections()
  {
    long l = 0L;
    synchronized (backingStore())
    {
      Iterator localIterator = values().iterator();
      while (localIterator.hasNext()) {
        if (((Connection)localIterator.next()).isBusy()) {
          l += 1L;
        }
      }
    }
    return l;
  }
  
  public synchronized boolean reclaim()
  {
    try
    {
      long l1 = numberOfConnections();
      if (orb.transportDebugFlag) {
        dprint(".reclaim->: " + l1 + " (" + orb.getORBData().getHighWaterMark() + "/" + orb.getORBData().getLowWaterMark() + "/" + orb.getORBData().getNumberToReclaim() + ")");
      }
      if ((l1 <= orb.getORBData().getHighWaterMark()) || (l1 < orb.getORBData().getLowWaterMark()))
      {
        boolean bool1 = false;
        return bool1;
      }
      Object localObject1 = backingStore();
      synchronized (localObject1)
      {
        for (int i = 0; i < orb.getORBData().getNumberToReclaim(); i++)
        {
          Object localObject2 = null;
          long l2 = Long.MAX_VALUE;
          Iterator localIterator = values().iterator();
          while (localIterator.hasNext())
          {
            Connection localConnection = (Connection)localIterator.next();
            if ((!localConnection.isBusy()) && (localConnection.getTimeStamp() < l2))
            {
              localObject2 = localConnection;
              l2 = localConnection.getTimeStamp();
            }
          }
          if (localObject2 == null)
          {
            boolean bool3 = false;
            return bool3;
          }
          try
          {
            if (orb.transportDebugFlag) {
              dprint(".reclaim: closing: " + localObject2);
            }
            ((Connection)localObject2).close();
          }
          catch (Exception localException) {}
        }
        if (orb.transportDebugFlag) {
          dprint(".reclaim: connections reclaimed (" + (l1 - numberOfConnections()) + ")");
        }
      }
      boolean bool2 = true;
      return bool2;
    }
    finally
    {
      if (orb.transportDebugFlag) {
        dprint(".reclaim<-: " + numberOfConnections());
      }
    }
  }
  
  public String getMonitoringName()
  {
    return monitoringName;
  }
  
  public abstract Collection values();
  
  protected abstract Object backingStore();
  
  protected abstract void registerWithMonitoring();
  
  protected void dprintCreation()
  {
    if (orb.transportDebugFlag) {
      dprint(".constructor: cacheType: " + getCacheType() + " monitoringName: " + getMonitoringName());
    }
  }
  
  protected void dprintStatistics()
  {
    if (orb.transportDebugFlag) {
      dprint(".stats: " + numberOfConnections() + "/total " + numberOfBusyConnections() + "/busy " + numberOfIdleConnections() + "/idle (" + orb.getORBData().getHighWaterMark() + "/" + orb.getORBData().getLowWaterMark() + "/" + orb.getORBData().getNumberToReclaim() + ")");
    }
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaConnectionCacheBase", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\CorbaConnectionCacheBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */