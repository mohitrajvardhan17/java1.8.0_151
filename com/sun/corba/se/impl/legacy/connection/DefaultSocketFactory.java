package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class DefaultSocketFactory
  implements ORBSocketFactory
{
  private com.sun.corba.se.spi.orb.ORB orb;
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");
  
  public DefaultSocketFactory() {}
  
  public void setORB(com.sun.corba.se.spi.orb.ORB paramORB)
  {
    orb = paramORB;
  }
  
  public ServerSocket createServerSocket(String paramString, int paramInt)
    throws IOException
  {
    if (!paramString.equals("IIOP_CLEAR_TEXT")) {
      throw wrapper.defaultCreateServerSocketGivenNonIiopClearText(paramString);
    }
    ServerSocket localServerSocket;
    if (orb.getORBData().acceptorSocketType().equals("SocketChannel"))
    {
      ServerSocketChannel localServerSocketChannel = ServerSocketChannel.open();
      localServerSocket = localServerSocketChannel.socket();
    }
    else
    {
      localServerSocket = new ServerSocket();
    }
    localServerSocket.bind(new InetSocketAddress(paramInt));
    return localServerSocket;
  }
  
  public SocketInfo getEndPointInfo(org.omg.CORBA.ORB paramORB, IOR paramIOR, SocketInfo paramSocketInfo)
  {
    IIOPProfileTemplate localIIOPProfileTemplate = (IIOPProfileTemplate)paramIOR.getProfile().getTaggedProfileTemplate();
    IIOPAddress localIIOPAddress = localIIOPProfileTemplate.getPrimaryAddress();
    return new EndPointInfoImpl("IIOP_CLEAR_TEXT", localIIOPAddress.getPort(), localIIOPAddress.getHost().toLowerCase());
  }
  
  public Socket createSocket(SocketInfo paramSocketInfo)
    throws IOException, GetEndPointInfoAgainException
  {
    Socket localSocket;
    if (orb.getORBData().acceptorSocketType().equals("SocketChannel"))
    {
      InetSocketAddress localInetSocketAddress = new InetSocketAddress(paramSocketInfo.getHost(), paramSocketInfo.getPort());
      SocketChannel localSocketChannel = SocketChannel.open(localInetSocketAddress);
      localSocket = localSocketChannel.socket();
    }
    else
    {
      localSocket = new Socket(paramSocketInfo.getHost(), paramSocketInfo.getPort());
    }
    try
    {
      localSocket.setTcpNoDelay(true);
    }
    catch (Exception localException) {}
    return localSocket;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\legacy\connection\DefaultSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */