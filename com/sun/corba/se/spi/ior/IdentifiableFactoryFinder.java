package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public abstract interface IdentifiableFactoryFinder
{
  public abstract Identifiable create(int paramInt, InputStream paramInputStream);
  
  public abstract void registerFactory(IdentifiableFactory paramIdentifiableFactory);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\IdentifiableFactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */