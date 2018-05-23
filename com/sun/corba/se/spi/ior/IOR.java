package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import java.util.List;

public abstract interface IOR
  extends List, Writeable, MakeImmutable
{
  public abstract ORB getORB();
  
  public abstract String getTypeId();
  
  public abstract Iterator iteratorById(int paramInt);
  
  public abstract String stringify();
  
  public abstract org.omg.IOP.IOR getIOPIOR();
  
  public abstract boolean isNil();
  
  public abstract boolean isEquivalent(IOR paramIOR);
  
  public abstract IORTemplateList getIORTemplates();
  
  public abstract IIOPProfile getProfile();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\IOR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */