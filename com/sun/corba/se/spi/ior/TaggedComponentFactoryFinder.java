package com.sun.corba.se.spi.ior;

import org.omg.CORBA.ORB;

public abstract interface TaggedComponentFactoryFinder
  extends IdentifiableFactoryFinder
{
  public abstract TaggedComponent create(ORB paramORB, org.omg.IOP.TaggedComponent paramTaggedComponent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\TaggedComponentFactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */