package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public abstract interface ResponseWaitingRoom
{
  public abstract void registerWaiter(MessageMediator paramMessageMediator);
  
  public abstract InputObject waitForResponse(MessageMediator paramMessageMediator);
  
  public abstract void responseReceived(InputObject paramInputObject);
  
  public abstract void unregisterWaiter(MessageMediator paramMessageMediator);
  
  public abstract int numberRegistered();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\ResponseWaitingRoom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */