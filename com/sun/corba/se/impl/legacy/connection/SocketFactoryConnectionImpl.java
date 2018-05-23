package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.transport.SocketOrChannelConnectionImpl;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class SocketFactoryConnectionImpl
  extends SocketOrChannelConnectionImpl
{
  public SocketFactoryConnectionImpl(ORB paramORB, CorbaContactInfo paramCorbaContactInfo, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramORB, paramBoolean1, paramBoolean2);
    contactInfo = paramCorbaContactInfo;
    boolean bool = !paramBoolean1;
    SocketInfo localSocketInfo = socketInfo;
    try
    {
      socket = paramORB.getORBData().getLegacySocketFactory().createSocket(localSocketInfo);
      socketChannel = socket.getChannel();
      if (socketChannel != null) {
        socketChannel.configureBlocking(bool);
      } else {
        setUseSelectThreadToWait(false);
      }
      if (transportDebugFlag) {
        dprint(".initialize: connection created: " + socket);
      }
    }
    catch (GetEndPointInfoAgainException localGetEndPointInfoAgainException)
    {
      throw wrapper.connectFailure(localGetEndPointInfoAgainException, localSocketInfo.getType(), localSocketInfo.getHost(), Integer.toString(localSocketInfo.getPort()));
    }
    catch (Exception localException)
    {
      throw wrapper.connectFailure(localException, localSocketInfo.getType(), localSocketInfo.getHost(), Integer.toString(localSocketInfo.getPort()));
    }
    state = 1;
  }
  
  public String toString()
  {
    synchronized (stateEvent)
    {
      return "SocketFactoryConnectionImpl[ " + (socketChannel == null ? socket.toString() : socketChannel.toString()) + " " + getStateString(state) + " " + shouldUseSelectThreadToWait() + " " + shouldUseWorkerThreadForEvent() + "]";
    }
  }
  
  public void dprint(String paramString)
  {
    ORBUtility.dprint("SocketFactoryConnectionImpl", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */