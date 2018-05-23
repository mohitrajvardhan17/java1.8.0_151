package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestDispatcherRegistryImpl
  implements RequestDispatcherRegistry
{
  private ORB orb;
  protected int defaultId;
  private DenseIntMapImpl SDRegistry;
  private DenseIntMapImpl CSRegistry;
  private DenseIntMapImpl OAFRegistry;
  private DenseIntMapImpl LCSFRegistry;
  private Set objectAdapterFactories;
  private Set objectAdapterFactoriesView;
  private Map stringToServerSubcontract;
  
  public RequestDispatcherRegistryImpl(ORB paramORB, int paramInt)
  {
    orb = paramORB;
    defaultId = paramInt;
    SDRegistry = new DenseIntMapImpl();
    CSRegistry = new DenseIntMapImpl();
    OAFRegistry = new DenseIntMapImpl();
    LCSFRegistry = new DenseIntMapImpl();
    objectAdapterFactories = new HashSet();
    objectAdapterFactoriesView = Collections.unmodifiableSet(objectAdapterFactories);
    stringToServerSubcontract = new HashMap();
  }
  
  public synchronized void registerClientRequestDispatcher(ClientRequestDispatcher paramClientRequestDispatcher, int paramInt)
  {
    CSRegistry.set(paramInt, paramClientRequestDispatcher);
  }
  
  public synchronized void registerLocalClientRequestDispatcherFactory(LocalClientRequestDispatcherFactory paramLocalClientRequestDispatcherFactory, int paramInt)
  {
    LCSFRegistry.set(paramInt, paramLocalClientRequestDispatcherFactory);
  }
  
  public synchronized void registerServerRequestDispatcher(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher, int paramInt)
  {
    SDRegistry.set(paramInt, paramCorbaServerRequestDispatcher);
  }
  
  public synchronized void registerServerRequestDispatcher(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher, String paramString)
  {
    stringToServerSubcontract.put(paramString, paramCorbaServerRequestDispatcher);
  }
  
  public synchronized void registerObjectAdapterFactory(ObjectAdapterFactory paramObjectAdapterFactory, int paramInt)
  {
    objectAdapterFactories.add(paramObjectAdapterFactory);
    OAFRegistry.set(paramInt, paramObjectAdapterFactory);
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(int paramInt)
  {
    CorbaServerRequestDispatcher localCorbaServerRequestDispatcher = (CorbaServerRequestDispatcher)SDRegistry.get(paramInt);
    if (localCorbaServerRequestDispatcher == null) {
      localCorbaServerRequestDispatcher = (CorbaServerRequestDispatcher)SDRegistry.get(defaultId);
    }
    return localCorbaServerRequestDispatcher;
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(String paramString)
  {
    CorbaServerRequestDispatcher localCorbaServerRequestDispatcher = (CorbaServerRequestDispatcher)stringToServerSubcontract.get(paramString);
    if (localCorbaServerRequestDispatcher == null) {
      localCorbaServerRequestDispatcher = (CorbaServerRequestDispatcher)SDRegistry.get(defaultId);
    }
    return localCorbaServerRequestDispatcher;
  }
  
  public LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory(int paramInt)
  {
    LocalClientRequestDispatcherFactory localLocalClientRequestDispatcherFactory = (LocalClientRequestDispatcherFactory)LCSFRegistry.get(paramInt);
    if (localLocalClientRequestDispatcherFactory == null) {
      localLocalClientRequestDispatcherFactory = (LocalClientRequestDispatcherFactory)LCSFRegistry.get(defaultId);
    }
    return localLocalClientRequestDispatcherFactory;
  }
  
  public ClientRequestDispatcher getClientRequestDispatcher(int paramInt)
  {
    ClientRequestDispatcher localClientRequestDispatcher = (ClientRequestDispatcher)CSRegistry.get(paramInt);
    if (localClientRequestDispatcher == null) {
      localClientRequestDispatcher = (ClientRequestDispatcher)CSRegistry.get(defaultId);
    }
    return localClientRequestDispatcher;
  }
  
  public ObjectAdapterFactory getObjectAdapterFactory(int paramInt)
  {
    ObjectAdapterFactory localObjectAdapterFactory = (ObjectAdapterFactory)OAFRegistry.get(paramInt);
    if (localObjectAdapterFactory == null) {
      localObjectAdapterFactory = (ObjectAdapterFactory)OAFRegistry.get(defaultId);
    }
    return localObjectAdapterFactory;
  }
  
  public Set getObjectAdapterFactories()
  {
    return objectAdapterFactoriesView;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\RequestDispatcherRegistryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */