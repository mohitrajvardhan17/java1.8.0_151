package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.spi.transport.CorbaContactInfoList;

public abstract interface ClientDelegateFactory
{
  public abstract CorbaClientDelegate create(CorbaContactInfoList paramCorbaContactInfoList);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\ClientDelegateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */