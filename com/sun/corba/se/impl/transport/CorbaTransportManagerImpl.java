package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CorbaTransportManagerImpl
  implements CorbaTransportManager
{
  protected ORB orb;
  protected List acceptors;
  protected Map outboundConnectionCaches;
  protected Map inboundConnectionCaches;
  protected Selector selector;
  
  public CorbaTransportManagerImpl(ORB paramORB)
  {
    orb = paramORB;
    acceptors = new ArrayList();
    outboundConnectionCaches = new HashMap();
    inboundConnectionCaches = new HashMap();
    selector = new SelectorImpl(paramORB);
  }
  
  public ByteBufferPool getByteBufferPool(int paramInt)
  {
    throw new RuntimeException();
  }
  
  public OutboundConnectionCache getOutboundConnectionCache(ContactInfo paramContactInfo)
  {
    synchronized (paramContactInfo)
    {
      if (paramContactInfo.getConnectionCache() == null)
      {
        Object localObject1 = null;
        synchronized (outboundConnectionCaches)
        {
          localObject1 = (OutboundConnectionCache)outboundConnectionCaches.get(paramContactInfo.getConnectionCacheType());
          if (localObject1 == null)
          {
            localObject1 = new CorbaOutboundConnectionCacheImpl(orb, paramContactInfo);
            outboundConnectionCaches.put(paramContactInfo.getConnectionCacheType(), localObject1);
          }
        }
        paramContactInfo.setConnectionCache((OutboundConnectionCache)localObject1);
      }
      return paramContactInfo.getConnectionCache();
    }
  }
  
  public Collection getOutboundConnectionCaches()
  {
    return outboundConnectionCaches.values();
  }
  
  public InboundConnectionCache getInboundConnectionCache(Acceptor paramAcceptor)
  {
    synchronized (paramAcceptor)
    {
      if (paramAcceptor.getConnectionCache() == null)
      {
        Object localObject1 = null;
        synchronized (inboundConnectionCaches)
        {
          localObject1 = (InboundConnectionCache)inboundConnectionCaches.get(paramAcceptor.getConnectionCacheType());
          if (localObject1 == null)
          {
            localObject1 = new CorbaInboundConnectionCacheImpl(orb, paramAcceptor);
            inboundConnectionCaches.put(paramAcceptor.getConnectionCacheType(), localObject1);
          }
        }
        paramAcceptor.setConnectionCache((InboundConnectionCache)localObject1);
      }
      return paramAcceptor.getConnectionCache();
    }
  }
  
  public Collection getInboundConnectionCaches()
  {
    return inboundConnectionCaches.values();
  }
  
  public Selector getSelector(int paramInt)
  {
    return selector;
  }
  
  public synchronized void registerAcceptor(Acceptor paramAcceptor)
  {
    if (orb.transportDebugFlag) {
      dprint(".registerAcceptor->: " + paramAcceptor);
    }
    acceptors.add(paramAcceptor);
    if (orb.transportDebugFlag) {
      dprint(".registerAcceptor<-: " + paramAcceptor);
    }
  }
  
  public Collection getAcceptors()
  {
    return getAcceptors(null, null);
  }
  
  public synchronized void unregisterAcceptor(Acceptor paramAcceptor)
  {
    acceptors.remove(paramAcceptor);
  }
  
  /* Error */
  public void close()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 178	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   4: getfield 182	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   7: ifeq +9 -> 16
    //   10: aload_0
    //   11: ldc 1
    //   13: invokevirtual 188	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:dprint	(Ljava/lang/String;)V
    //   16: aload_0
    //   17: getfield 181	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:outboundConnectionCaches	Ljava/util/Map;
    //   20: invokeinterface 221 1 0
    //   25: invokeinterface 215 1 0
    //   30: astore_1
    //   31: aload_1
    //   32: invokeinterface 216 1 0
    //   37: ifeq +22 -> 59
    //   40: aload_1
    //   41: invokeinterface 217 1 0
    //   46: astore_2
    //   47: aload_2
    //   48: checkcast 91	com/sun/corba/se/pept/transport/ConnectionCache
    //   51: invokeinterface 206 1 0
    //   56: goto -25 -> 31
    //   59: aload_0
    //   60: getfield 180	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:inboundConnectionCaches	Ljava/util/Map;
    //   63: invokeinterface 221 1 0
    //   68: invokeinterface 215 1 0
    //   73: astore_1
    //   74: aload_1
    //   75: invokeinterface 216 1 0
    //   80: ifeq +35 -> 115
    //   83: aload_1
    //   84: invokeinterface 217 1 0
    //   89: astore_2
    //   90: aload_2
    //   91: checkcast 91	com/sun/corba/se/pept/transport/ConnectionCache
    //   94: invokeinterface 206 1 0
    //   99: aload_0
    //   100: aload_2
    //   101: checkcast 93	com/sun/corba/se/pept/transport/InboundConnectionCache
    //   104: invokeinterface 210 1 0
    //   109: invokevirtual 186	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:unregisterAcceptor	(Lcom/sun/corba/se/pept/transport/Acceptor;)V
    //   112: goto -38 -> 74
    //   115: aload_0
    //   116: iconst_0
    //   117: invokevirtual 187	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:getSelector	(I)Lcom/sun/corba/se/pept/transport/Selector;
    //   120: invokeinterface 211 1 0
    //   125: aload_0
    //   126: getfield 178	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   129: getfield 182	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   132: ifeq +31 -> 163
    //   135: aload_0
    //   136: ldc 2
    //   138: invokevirtual 188	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:dprint	(Ljava/lang/String;)V
    //   141: goto +22 -> 163
    //   144: astore_3
    //   145: aload_0
    //   146: getfield 178	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:orb	Lcom/sun/corba/se/spi/orb/ORB;
    //   149: getfield 182	com/sun/corba/se/spi/orb/ORB:transportDebugFlag	Z
    //   152: ifeq +9 -> 161
    //   155: aload_0
    //   156: ldc 2
    //   158: invokevirtual 188	com/sun/corba/se/impl/transport/CorbaTransportManagerImpl:dprint	(Ljava/lang/String;)V
    //   161: aload_3
    //   162: athrow
    //   163: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	164	0	this	CorbaTransportManagerImpl
    //   30	54	1	localIterator	Iterator
    //   46	55	2	localObject1	Object
    //   144	18	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	125	144	finally
  }
  
  public Collection getAcceptors(String paramString, ObjectAdapterId paramObjectAdapterId)
  {
    Iterator localIterator = acceptors.iterator();
    while (localIterator.hasNext())
    {
      Acceptor localAcceptor = (Acceptor)localIterator.next();
      if ((localAcceptor.initialize()) && (localAcceptor.shouldRegisterAcceptEvent())) {
        orb.getTransportManager().getSelector(0).registerForEvent(localAcceptor.getEventHandler());
      }
    }
    return acceptors;
  }
  
  public void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString1, String paramString2, ObjectAdapterId paramObjectAdapterId)
  {
    Iterator localIterator = getAcceptors(paramString2, paramObjectAdapterId).iterator();
    while (localIterator.hasNext())
    {
      CorbaAcceptor localCorbaAcceptor = (CorbaAcceptor)localIterator.next();
      localCorbaAcceptor.addToIORTemplate(paramIORTemplate, paramPolicies, paramString1);
    }
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaTransportManagerImpl", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\CorbaTransportManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */