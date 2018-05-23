package com.sun.corba.se.spi.ior;

import org.omg.CORBA.ORB;

public abstract interface TaggedComponent
  extends Identifiable
{
  public abstract org.omg.IOP.TaggedComponent getIOPComponent(ORB paramORB);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\TaggedComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */