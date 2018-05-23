package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;

public abstract interface CorbaContactInfo
  extends ContactInfo
{
  public abstract IOR getTargetIOR();
  
  public abstract IOR getEffectiveTargetIOR();
  
  public abstract IIOPProfile getEffectiveProfile();
  
  public abstract void setAddressingDisposition(short paramShort);
  
  public abstract short getAddressingDisposition();
  
  public abstract String getMonitoringName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\CorbaContactInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */