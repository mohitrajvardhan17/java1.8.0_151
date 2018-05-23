package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfo;
import java.util.List;

public abstract interface IIOPPrimaryToContactInfo
{
  public abstract void reset(ContactInfo paramContactInfo);
  
  public abstract boolean hasNext(ContactInfo paramContactInfo1, ContactInfo paramContactInfo2, List paramList);
  
  public abstract ContactInfo next(ContactInfo paramContactInfo1, ContactInfo paramContactInfo2, List paramList);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\IIOPPrimaryToContactInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */