package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.spi.resolver.Resolver;

public abstract interface InitialServerRequestDispatcher
  extends CorbaServerRequestDispatcher
{
  public abstract void init(Resolver paramResolver);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\InitialServerRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */