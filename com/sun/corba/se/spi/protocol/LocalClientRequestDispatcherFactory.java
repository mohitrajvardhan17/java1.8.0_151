package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.spi.ior.IOR;

public abstract interface LocalClientRequestDispatcherFactory
{
  public abstract LocalClientRequestDispatcher create(int paramInt, IOR paramIOR);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\LocalClientRequestDispatcherFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */