package com.sun.corba.se.pept.transport;

public abstract interface InboundConnectionCache
  extends ConnectionCache
{
  public abstract Connection get(Acceptor paramAcceptor);
  
  public abstract void put(Acceptor paramAcceptor, Connection paramConnection);
  
  public abstract void remove(Connection paramConnection);
  
  public abstract Acceptor getAcceptor();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\InboundConnectionCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */