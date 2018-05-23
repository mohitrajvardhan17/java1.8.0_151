package com.sun.corba.se.spi.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public abstract interface LocalResolver
  extends Resolver
{
  public abstract void register(String paramString, Closure paramClosure);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\resolver\LocalResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */