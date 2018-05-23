package com.sun.corba.se.spi.legacy.connection;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.omg.CORBA.ORB;

public abstract interface ORBSocketFactory
{
  public static final String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";
  
  public abstract ServerSocket createServerSocket(String paramString, int paramInt)
    throws IOException;
  
  public abstract SocketInfo getEndPointInfo(ORB paramORB, IOR paramIOR, SocketInfo paramSocketInfo);
  
  public abstract Socket createSocket(SocketInfo paramSocketInfo)
    throws IOException, GetEndPointInfoAgainException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\legacy\connection\ORBSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */