package com.sun.corba.se.pept.transport;

public abstract interface OutboundConnectionCache
  extends ConnectionCache
{
  public abstract Connection get(ContactInfo paramContactInfo);
  
  public abstract void put(ContactInfo paramContactInfo, Connection paramConnection);
  
  public abstract void remove(ContactInfo paramContactInfo);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\pept\transport\OutboundConnectionCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */