package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public abstract interface Acceptor
{
  public abstract boolean initialize();
  
  public abstract boolean initialized();
  
  public abstract String getConnectionCacheType();
  
  public abstract void setConnectionCache(InboundConnectionCache paramInboundConnectionCache);
  
  public abstract InboundConnectionCache getConnectionCache();
  
  public abstract boolean shouldRegisterAcceptEvent();
  
  public abstract void accept();
  
  public abstract void close();
  
  public abstract EventHandler getEventHandler();
  
  public abstract MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection);
  
  public abstract MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator);
  
  public abstract InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator);
  
  public abstract OutputObject createOutputObject(Broker paramBroker, MessageMediator paramMessageMediator);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\Acceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */