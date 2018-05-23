package com.sun.corba.se.pept.transport;

import java.util.Iterator;

public abstract interface ContactInfoListIterator
  extends Iterator
{
  public abstract ContactInfoList getContactInfoList();
  
  public abstract void reportSuccess(ContactInfo paramContactInfo);
  
  public abstract boolean reportException(ContactInfo paramContactInfo, RuntimeException paramRuntimeException);
  
  public abstract RuntimeException getFailureException();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\ContactInfoListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */