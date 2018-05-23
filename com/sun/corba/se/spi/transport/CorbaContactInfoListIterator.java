package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfoListIterator;
import com.sun.corba.se.spi.ior.IOR;

public abstract interface CorbaContactInfoListIterator
  extends ContactInfoListIterator
{
  public abstract void reportAddrDispositionRetry(CorbaContactInfo paramCorbaContactInfo, short paramShort);
  
  public abstract void reportRedirect(CorbaContactInfo paramCorbaContactInfo, IOR paramIOR);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\CorbaContactInfoListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */