package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.transport.CorbaContactInfoListIteratorImpl;
import com.sun.corba.se.impl.transport.SharedCDRContactInfoImpl;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class SocketFactoryContactInfoListIteratorImpl
  extends CorbaContactInfoListIteratorImpl
{
  private SocketInfo socketInfoCookie;
  
  public SocketFactoryContactInfoListIteratorImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList)
  {
    super(paramORB, paramCorbaContactInfoList, null, null);
  }
  
  public boolean hasNext()
  {
    return true;
  }
  
  public Object next()
  {
    if (contactInfoList.getEffectiveTargetIOR().getProfile().isLocal()) {
      return new SharedCDRContactInfoImpl(orb, contactInfoList, contactInfoList.getEffectiveTargetIOR(), orb.getORBData().getGIOPAddressDisposition());
    }
    return new SocketFactoryContactInfoImpl(orb, contactInfoList, contactInfoList.getEffectiveTargetIOR(), orb.getORBData().getGIOPAddressDisposition(), socketInfoCookie);
  }
  
  public boolean reportException(ContactInfo paramContactInfo, RuntimeException paramRuntimeException)
  {
    failureContactInfo = ((CorbaContactInfo)paramContactInfo);
    failureException = paramRuntimeException;
    if ((paramRuntimeException instanceof COMM_FAILURE))
    {
      if ((paramRuntimeException.getCause() instanceof GetEndPointInfoAgainException))
      {
        socketInfoCookie = ((GetEndPointInfoAgainException)paramRuntimeException.getCause()).getEndPointInfo();
        return true;
      }
      SystemException localSystemException = (SystemException)paramRuntimeException;
      if ((completed == CompletionStatus.COMPLETED_NO) && (contactInfoList.getEffectiveTargetIOR() != contactInfoList.getTargetIOR()))
      {
        contactInfoList.setEffectiveTargetIOR(contactInfoList.getTargetIOR());
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryContactInfoListIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */