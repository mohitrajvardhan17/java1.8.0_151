package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;

public abstract interface IORFactory
  extends Writeable, MakeImmutable
{
  public abstract IOR makeIOR(ORB paramORB, String paramString, ObjectId paramObjectId);
  
  public abstract boolean isEquivalent(IORFactory paramIORFactory);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\IORFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */