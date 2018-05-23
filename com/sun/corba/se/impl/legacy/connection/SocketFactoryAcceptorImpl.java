package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;

public class SocketFactoryAcceptorImpl
  extends SocketOrChannelAcceptorImpl
{
  public SocketFactoryAcceptorImpl(ORB paramORB, int paramInt, String paramString1, String paramString2)
  {
    super(paramORB, paramInt, paramString1, paramString2);
  }
  
  public boolean initialize()
  {
    if (initialized) {
      return false;
    }
    if (orb.transportDebugFlag) {
      dprint("initialize: " + this);
    }
    try
    {
      serverSocket = orb.getORBData().getLegacySocketFactory().createServerSocket(type, port);
      internalInitialize();
    }
    catch (Throwable localThrowable)
    {
      throw wrapper.createListenerFailed(localThrowable, Integer.toString(port));
    }
    initialized = true;
    return true;
  }
  
  protected String toStringName()
  {
    return "SocketFactoryAcceptorImpl";
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint(toStringName(), paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryAcceptorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */