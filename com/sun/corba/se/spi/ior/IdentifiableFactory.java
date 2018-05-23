package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public abstract interface IdentifiableFactory
{
  public abstract int getId();
  
  public abstract Identifiable create(InputStream paramInputStream);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\IdentifiableFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */