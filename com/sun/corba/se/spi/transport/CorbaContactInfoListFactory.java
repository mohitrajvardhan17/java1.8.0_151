package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public abstract interface CorbaContactInfoListFactory
{
  public abstract void setORB(ORB paramORB);
  
  public abstract CorbaContactInfoList create(IOR paramIOR);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\CorbaContactInfoListFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */