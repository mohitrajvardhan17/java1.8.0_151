package com.sun.corba.se.spi.orbutil.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

public abstract interface CompositeInvocationHandler
  extends InvocationHandler, Serializable
{
  public abstract void addInvocationHandler(Class paramClass, InvocationHandler paramInvocationHandler);
  
  public abstract void setDefaultHandler(InvocationHandler paramInvocationHandler);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\CompositeInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */