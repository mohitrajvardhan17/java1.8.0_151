package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;

public abstract interface ContactInfo
{
  public abstract Broker getBroker();
  
  public abstract ContactInfoList getContactInfoList();
  
  public abstract ClientRequestDispatcher getClientRequestDispatcher();
  
  public abstract boolean isConnectionBased();
  
  public abstract boolean shouldCacheConnection();
  
  public abstract String getConnectionCacheType();
  
  public abstract void setConnectionCache(OutboundConnectionCache paramOutboundConnectionCache);
  
  public abstract OutboundConnectionCache getConnectionCache();
  
  public abstract Connection createConnection();
  
  public abstract MessageMediator createMessageMediator(Broker paramBroker, ContactInfo paramContactInfo, Connection paramConnection, String paramString, boolean paramBoolean);
  
  public abstract MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection);
  
  public abstract MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator);
  
  public abstract InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator);
  
  public abstract OutputObject createOutputObject(MessageMediator paramMessageMediator);
  
  public abstract int hashCode();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\ContactInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */