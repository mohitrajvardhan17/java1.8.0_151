package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import java.util.Set;

public abstract interface RequestDispatcherRegistry
{
  public abstract void registerClientRequestDispatcher(ClientRequestDispatcher paramClientRequestDispatcher, int paramInt);
  
  public abstract ClientRequestDispatcher getClientRequestDispatcher(int paramInt);
  
  public abstract void registerLocalClientRequestDispatcherFactory(LocalClientRequestDispatcherFactory paramLocalClientRequestDispatcherFactory, int paramInt);
  
  public abstract LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory(int paramInt);
  
  public abstract void registerServerRequestDispatcher(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher, int paramInt);
  
  public abstract CorbaServerRequestDispatcher getServerRequestDispatcher(int paramInt);
  
  public abstract void registerServerRequestDispatcher(CorbaServerRequestDispatcher paramCorbaServerRequestDispatcher, String paramString);
  
  public abstract CorbaServerRequestDispatcher getServerRequestDispatcher(String paramString);
  
  public abstract void registerObjectAdapterFactory(ObjectAdapterFactory paramObjectAdapterFactory, int paramInt);
  
  public abstract ObjectAdapterFactory getObjectAdapterFactory(int paramInt);
  
  public abstract Set<ObjectAdapterFactory> getObjectAdapterFactories();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\RequestDispatcherRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */