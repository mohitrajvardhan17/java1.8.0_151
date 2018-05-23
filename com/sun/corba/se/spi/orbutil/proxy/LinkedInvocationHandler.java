package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public abstract interface LinkedInvocationHandler
  extends InvocationHandler
{
  public abstract void setProxy(Proxy paramProxy);
  
  public abstract Proxy getProxy();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\LinkedInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */