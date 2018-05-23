package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public abstract interface ORBSocketFactory
{
  public abstract void setORB(ORB paramORB);
  
  public abstract ServerSocket createServerSocket(String paramString, InetSocketAddress paramInetSocketAddress)
    throws IOException;
  
  public abstract Socket createSocket(String paramString, InetSocketAddress paramInetSocketAddress)
    throws IOException;
  
  public abstract void setAcceptedSocketOptions(Acceptor paramAcceptor, ServerSocket paramServerSocket, Socket paramSocket)
    throws SocketException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\ORBSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */