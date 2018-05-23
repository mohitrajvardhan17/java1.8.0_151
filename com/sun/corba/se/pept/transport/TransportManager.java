package com.sun.corba.se.pept.transport;

import java.util.Collection;

public abstract interface TransportManager
{
  public abstract ByteBufferPool getByteBufferPool(int paramInt);
  
  public abstract OutboundConnectionCache getOutboundConnectionCache(ContactInfo paramContactInfo);
  
  public abstract Collection getOutboundConnectionCaches();
  
  public abstract InboundConnectionCache getInboundConnectionCache(Acceptor paramAcceptor);
  
  public abstract Collection getInboundConnectionCaches();
  
  public abstract Selector getSelector(int paramInt);
  
  public abstract void registerAcceptor(Acceptor paramAcceptor);
  
  public abstract Collection getAcceptors();
  
  public abstract void unregisterAcceptor(Acceptor paramAcceptor);
  
  public abstract void close();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\TransportManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */