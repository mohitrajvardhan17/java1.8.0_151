package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import java.util.Iterator;
import java.util.List;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class CorbaContactInfoListIteratorImpl
  implements CorbaContactInfoListIterator
{
  protected ORB orb;
  protected CorbaContactInfoList contactInfoList;
  protected CorbaContactInfo successContactInfo;
  protected CorbaContactInfo failureContactInfo;
  protected RuntimeException failureException;
  protected Iterator effectiveTargetIORIterator;
  protected CorbaContactInfo previousContactInfo;
  protected boolean isAddrDispositionRetry;
  protected IIOPPrimaryToContactInfo primaryToContactInfo;
  protected ContactInfo primaryContactInfo;
  protected List listOfContactInfos;
  
  public CorbaContactInfoListIteratorImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, ContactInfo paramContactInfo, List paramList)
  {
    orb = paramORB;
    contactInfoList = paramCorbaContactInfoList;
    primaryContactInfo = paramContactInfo;
    if (paramList != null) {
      effectiveTargetIORIterator = paramList.iterator();
    }
    listOfContactInfos = paramList;
    previousContactInfo = null;
    isAddrDispositionRetry = false;
    successContactInfo = null;
    failureContactInfo = null;
    failureException = null;
    primaryToContactInfo = paramORB.getORBData().getIIOPPrimaryToContactInfo();
  }
  
  public boolean hasNext()
  {
    if (isAddrDispositionRetry) {
      return true;
    }
    boolean bool;
    if (primaryToContactInfo != null) {
      bool = primaryToContactInfo.hasNext(primaryContactInfo, previousContactInfo, listOfContactInfos);
    } else {
      bool = effectiveTargetIORIterator.hasNext();
    }
    return bool;
  }
  
  public Object next()
  {
    if (isAddrDispositionRetry)
    {
      isAddrDispositionRetry = false;
      return previousContactInfo;
    }
    if (primaryToContactInfo != null) {
      previousContactInfo = ((CorbaContactInfo)primaryToContactInfo.next(primaryContactInfo, previousContactInfo, listOfContactInfos));
    } else {
      previousContactInfo = ((CorbaContactInfo)effectiveTargetIORIterator.next());
    }
    return previousContactInfo;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  public ContactInfoList getContactInfoList()
  {
    return contactInfoList;
  }
  
  public void reportSuccess(ContactInfo paramContactInfo)
  {
    successContactInfo = ((CorbaContactInfo)paramContactInfo);
  }
  
  public boolean reportException(ContactInfo paramContactInfo, RuntimeException paramRuntimeException)
  {
    failureContactInfo = ((CorbaContactInfo)paramContactInfo);
    failureException = paramRuntimeException;
    if ((paramRuntimeException instanceof COMM_FAILURE))
    {
      SystemException localSystemException = (SystemException)paramRuntimeException;
      if (completed == CompletionStatus.COMPLETED_NO)
      {
        if (hasNext()) {
          return true;
        }
        if (contactInfoList.getEffectiveTargetIOR() != contactInfoList.getTargetIOR())
        {
          updateEffectiveTargetIOR(contactInfoList.getTargetIOR());
          return true;
        }
      }
    }
    return false;
  }
  
  public RuntimeException getFailureException()
  {
    if (failureException == null) {
      return ORBUtilSystemException.get(orb, "rpc.transport").invalidContactInfoListIteratorFailureException();
    }
    return failureException;
  }
  
  public void reportAddrDispositionRetry(CorbaContactInfo paramCorbaContactInfo, short paramShort)
  {
    previousContactInfo.setAddressingDisposition(paramShort);
    isAddrDispositionRetry = true;
  }
  
  public void reportRedirect(CorbaContactInfo paramCorbaContactInfo, IOR paramIOR)
  {
    updateEffectiveTargetIOR(paramIOR);
  }
  
  public void updateEffectiveTargetIOR(IOR paramIOR)
  {
    contactInfoList.setEffectiveTargetIOR(paramIOR);
    ((CorbaInvocationInfo)orb.getInvocationInfo()).setContactInfoListIterator(contactInfoList.iterator());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\CorbaContactInfoListIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */