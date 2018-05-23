package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;

public abstract interface MessageMediator
{
  public abstract Broker getBroker();
  
  public abstract ContactInfo getContactInfo();
  
  public abstract Connection getConnection();
  
  public abstract void initializeMessage();
  
  public abstract void finishSendingRequest();
  
  @Deprecated
  public abstract InputObject waitForResponse();
  
  public abstract void setOutputObject(OutputObject paramOutputObject);
  
  public abstract OutputObject getOutputObject();
  
  public abstract void setInputObject(InputObject paramInputObject);
  
  public abstract InputObject getInputObject();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\protocol\MessageMediator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */