package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import org.omg.CORBA.SystemException;

public abstract interface CorbaResponseWaitingRoom
  extends ResponseWaitingRoom
{
  public abstract void signalExceptionToAllWaiters(SystemException paramSystemException);
  
  public abstract MessageMediator getMessageMediator(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\CorbaResponseWaitingRoom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */