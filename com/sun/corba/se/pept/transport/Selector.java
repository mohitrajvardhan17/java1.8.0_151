package com.sun.corba.se.pept.transport;

public abstract interface Selector
{
  public abstract void setTimeout(long paramLong);
  
  public abstract long getTimeout();
  
  public abstract void registerInterestOps(EventHandler paramEventHandler);
  
  public abstract void registerForEvent(EventHandler paramEventHandler);
  
  public abstract void unregisterForEvent(EventHandler paramEventHandler);
  
  public abstract void close();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\Selector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */