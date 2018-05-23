package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;

public abstract interface ObjectAdapterFactory
{
  public abstract void init(ORB paramORB);
  
  public abstract void shutdown(boolean paramBoolean);
  
  public abstract ObjectAdapter find(ObjectAdapterId paramObjectAdapterId);
  
  public abstract ORB getORB();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\oa\ObjectAdapterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */