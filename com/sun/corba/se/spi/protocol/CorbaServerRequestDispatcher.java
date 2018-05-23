package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.pept.protocol.ServerRequestDispatcher;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;

public abstract interface CorbaServerRequestDispatcher
  extends ServerRequestDispatcher
{
  public abstract IOR locate(ObjectKey paramObjectKey);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\CorbaServerRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */