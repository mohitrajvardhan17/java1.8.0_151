package com.sun.corba.se.pept.broker;

import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.transport.TransportManager;

public abstract interface Broker
{
  public abstract ClientInvocationInfo createOrIncrementInvocationInfo();
  
  public abstract ClientInvocationInfo getInvocationInfo();
  
  public abstract void releaseOrDecrementInvocationInfo();
  
  public abstract TransportManager getTransportManager();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\broker\Broker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */