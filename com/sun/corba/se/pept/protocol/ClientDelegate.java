package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.ContactInfoList;

public abstract interface ClientDelegate
{
  public abstract Broker getBroker();
  
  public abstract ContactInfoList getContactInfoList();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\protocol\ClientDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */