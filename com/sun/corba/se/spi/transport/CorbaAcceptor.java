package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.IORTemplate;

public abstract interface CorbaAcceptor
  extends Acceptor
{
  public abstract String getObjectAdapterId();
  
  public abstract String getObjectAdapterManagerId();
  
  public abstract void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString);
  
  public abstract String getMonitoringName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\CorbaAcceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */