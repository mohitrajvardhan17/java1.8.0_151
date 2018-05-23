package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public final class IIOPAddressClosureImpl
  extends IIOPAddressBase
{
  private Closure host;
  private Closure port;
  
  public IIOPAddressClosureImpl(Closure paramClosure1, Closure paramClosure2)
  {
    host = paramClosure1;
    port = paramClosure2;
  }
  
  public String getHost()
  {
    return (String)host.evaluate();
  }
  
  public int getPort()
  {
    Integer localInteger = (Integer)port.evaluate();
    return localInteger.intValue();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPAddressClosureImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */