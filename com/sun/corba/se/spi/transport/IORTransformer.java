package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.encoding.CorbaInputObject;
import com.sun.corba.se.spi.encoding.CorbaOutputObject;
import com.sun.corba.se.spi.ior.IOR;

public abstract interface IORTransformer
{
  public abstract IOR unmarshal(CorbaInputObject paramCorbaInputObject);
  
  public abstract void marshal(CorbaOutputObject paramCorbaOutputObject, IOR paramIOR);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\IORTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */