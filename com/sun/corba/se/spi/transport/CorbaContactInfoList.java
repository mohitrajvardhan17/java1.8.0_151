package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;

public abstract interface CorbaContactInfoList
  extends ContactInfoList
{
  public abstract void setTargetIOR(IOR paramIOR);
  
  public abstract IOR getTargetIOR();
  
  public abstract void setEffectiveTargetIOR(IOR paramIOR);
  
  public abstract IOR getEffectiveTargetIOR();
  
  public abstract LocalClientRequestDispatcher getLocalClientRequestDispatcher();
  
  public abstract int hashCode();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\CorbaContactInfoList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */