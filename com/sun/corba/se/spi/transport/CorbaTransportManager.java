package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import java.util.Collection;

public abstract interface CorbaTransportManager
  extends TransportManager
{
  public static final String SOCKET_OR_CHANNEL_CONNECTION_CACHE = "SocketOrChannelConnectionCache";
  
  public abstract Collection getAcceptors(String paramString, ObjectAdapterId paramObjectAdapterId);
  
  public abstract void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString1, String paramString2, ObjectAdapterId paramObjectAdapterId);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\CorbaTransportManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */