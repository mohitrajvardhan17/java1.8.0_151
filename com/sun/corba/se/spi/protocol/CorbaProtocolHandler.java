package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.pept.protocol.ProtocolHandler;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.UnknownException;

public abstract interface CorbaProtocolHandler
  extends ProtocolHandler
{
  public abstract void handleRequest(RequestMessage paramRequestMessage, CorbaMessageMediator paramCorbaMessageMediator);
  
  public abstract void handleRequest(LocateRequestMessage paramLocateRequestMessage, CorbaMessageMediator paramCorbaMessageMediator);
  
  public abstract CorbaMessageMediator createResponse(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts);
  
  public abstract CorbaMessageMediator createUserExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts);
  
  public abstract CorbaMessageMediator createUnknownExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, UnknownException paramUnknownException);
  
  public abstract CorbaMessageMediator createSystemExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, SystemException paramSystemException, ServiceContexts paramServiceContexts);
  
  public abstract CorbaMessageMediator createLocationForward(CorbaMessageMediator paramCorbaMessageMediator, IOR paramIOR, ServiceContexts paramServiceContexts);
  
  public abstract void handleThrowableDuringServerDispatch(CorbaMessageMediator paramCorbaMessageMediator, Throwable paramThrowable, CompletionStatus paramCompletionStatus);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\CorbaProtocolHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */