package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.Writeable;

public abstract interface IIOPAddress
  extends Writeable
{
  public abstract String getHost();
  
  public abstract int getPort();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\iiop\IIOPAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */