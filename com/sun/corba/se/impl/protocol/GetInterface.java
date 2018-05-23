package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;

class GetInterface
  extends SpecialMethod
{
  GetInterface() {}
  
  public boolean isNonExistentMethod()
  {
    return false;
  }
  
  public String getName()
  {
    return "_interface";
  }
  
  public CorbaMessageMediator invoke(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter)
  {
    ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
    ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get(localORB, "oa.invocation");
    if ((paramObject == null) || ((paramObject instanceof NullServant))) {
      return paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, localORBUtilSystemException.badSkeleton(), null);
    }
    return paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, localORBUtilSystemException.getinterfaceNotImplemented(), null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\GetInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */