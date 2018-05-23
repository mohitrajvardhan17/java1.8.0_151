package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.InvocationHandler;

public abstract interface InvocationHandlerFactory
{
  public abstract InvocationHandler getInvocationHandler();
  
  public abstract Class[] getProxyInterfaces();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\InvocationHandlerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */