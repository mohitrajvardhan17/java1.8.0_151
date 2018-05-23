package com.sun.corba.se.spi.resolver;

import java.util.Set;

public abstract interface Resolver
{
  public abstract org.omg.CORBA.Object resolve(String paramString);
  
  public abstract Set list();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\resolver\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */