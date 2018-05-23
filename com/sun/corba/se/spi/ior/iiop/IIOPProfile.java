package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.orb.ORBVersion;

public abstract interface IIOPProfile
  extends TaggedProfile
{
  public abstract ORBVersion getORBVersion();
  
  public abstract Object getServant();
  
  public abstract GIOPVersion getGIOPVersion();
  
  public abstract String getCodebase();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\iiop\IIOPProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */