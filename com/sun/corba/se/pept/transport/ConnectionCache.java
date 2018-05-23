package com.sun.corba.se.pept.transport;

public abstract interface ConnectionCache
{
  public abstract String getCacheType();
  
  public abstract void stampTime(Connection paramConnection);
  
  public abstract long numberOfConnections();
  
  public abstract long numberOfIdleConnections();
  
  public abstract long numberOfBusyConnections();
  
  public abstract boolean reclaim();
  
  public abstract void close();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\ConnectionCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */