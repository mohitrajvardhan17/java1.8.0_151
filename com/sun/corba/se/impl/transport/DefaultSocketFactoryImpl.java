package com.sun.corba.se.impl.transport;

import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.ORBSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class DefaultSocketFactoryImpl
  implements ORBSocketFactory
{
  private ORB orb;
  private static final boolean keepAlive = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      String str = System.getProperty("com.sun.CORBA.transport.enableTcpKeepAlive");
      if (str != null) {
        return new Boolean(!"false".equalsIgnoreCase(str));
      }
      return Boolean.FALSE;
    }
  })).booleanValue();
  
  public DefaultSocketFactoryImpl() {}
  
  public void setORB(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public ServerSocket createServerSocket(String paramString, InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    ServerSocketChannel localServerSocketChannel = null;
    ServerSocket localServerSocket = null;
    if (orb.getORBData().acceptorSocketType().equals("SocketChannel"))
    {
      localServerSocketChannel = ServerSocketChannel.open();
      localServerSocket = localServerSocketChannel.socket();
    }
    else
    {
      localServerSocket = new ServerSocket();
    }
    localServerSocket.bind(paramInetSocketAddress);
    return localServerSocket;
  }
  
  public Socket createSocket(String paramString, InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    SocketChannel localSocketChannel = null;
    Socket localSocket = null;
    if (orb.getORBData().connectionSocketType().equals("SocketChannel"))
    {
      localSocketChannel = SocketChannel.open(paramInetSocketAddress);
      localSocket = localSocketChannel.socket();
    }
    else
    {
      localSocket = new Socket(paramInetSocketAddress.getHostName(), paramInetSocketAddress.getPort());
    }
    localSocket.setTcpNoDelay(true);
    if (keepAlive) {
      localSocket.setKeepAlive(true);
    }
    return localSocket;
  }
  
  public void setAcceptedSocketOptions(Acceptor paramAcceptor, ServerSocket paramServerSocket, Socket paramSocket)
    throws SocketException
  {
    paramSocket.setTcpNoDelay(true);
    if (keepAlive) {
      paramSocket.setKeepAlive(true);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\DefaultSocketFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */