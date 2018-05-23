package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ResourceManager;
import sun.security.action.GetPropertyAction;

abstract class AbstractPlainDatagramSocketImpl
  extends DatagramSocketImpl
{
  int timeout = 0;
  boolean connected = false;
  private int trafficClass = 0;
  protected InetAddress connectedAddress = null;
  private int connectedPort = -1;
  private static final String os = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
  private static final boolean connectDisabled = os.contains("OS X");
  
  AbstractPlainDatagramSocketImpl() {}
  
  protected synchronized void create()
    throws SocketException
  {
    ResourceManager.beforeUdpCreate();
    fd = new FileDescriptor();
    try
    {
      datagramSocketCreate();
    }
    catch (SocketException localSocketException)
    {
      ResourceManager.afterUdpClose();
      fd = null;
      throw localSocketException;
    }
  }
  
  protected synchronized void bind(int paramInt, InetAddress paramInetAddress)
    throws SocketException
  {
    bind0(paramInt, paramInetAddress);
  }
  
  protected abstract void bind0(int paramInt, InetAddress paramInetAddress)
    throws SocketException;
  
  protected abstract void send(DatagramPacket paramDatagramPacket)
    throws IOException;
  
  protected void connect(InetAddress paramInetAddress, int paramInt)
    throws SocketException
  {
    connect0(paramInetAddress, paramInt);
    connectedAddress = paramInetAddress;
    connectedPort = paramInt;
    connected = true;
  }
  
  protected void disconnect()
  {
    disconnect0(connectedAddress.holder().getFamily());
    connected = false;
    connectedAddress = null;
    connectedPort = -1;
  }
  
  protected abstract int peek(InetAddress paramInetAddress)
    throws IOException;
  
  protected abstract int peekData(DatagramPacket paramDatagramPacket)
    throws IOException;
  
  protected synchronized void receive(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    receive0(paramDatagramPacket);
  }
  
  protected abstract void receive0(DatagramPacket paramDatagramPacket)
    throws IOException;
  
  protected abstract void setTimeToLive(int paramInt)
    throws IOException;
  
  protected abstract int getTimeToLive()
    throws IOException;
  
  @Deprecated
  protected abstract void setTTL(byte paramByte)
    throws IOException;
  
  @Deprecated
  protected abstract byte getTTL()
    throws IOException;
  
  protected void join(InetAddress paramInetAddress)
    throws IOException
  {
    join(paramInetAddress, null);
  }
  
  protected void leave(InetAddress paramInetAddress)
    throws IOException
  {
    leave(paramInetAddress, null);
  }
  
  protected void joinGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    join(((InetSocketAddress)paramSocketAddress).getAddress(), paramNetworkInterface);
  }
  
  protected abstract void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
    throws IOException;
  
  protected void leaveGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    leave(((InetSocketAddress)paramSocketAddress).getAddress(), paramNetworkInterface);
  }
  
  protected abstract void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
    throws IOException;
  
  protected void close()
  {
    if (fd != null)
    {
      datagramSocketClose();
      ResourceManager.afterUdpClose();
      fd = null;
    }
  }
  
  protected boolean isClosed()
  {
    return fd == null;
  }
  
  protected void finalize()
  {
    close();
  }
  
  public void setOption(int paramInt, Object paramObject)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket Closed");
    }
    switch (paramInt)
    {
    case 4102: 
      if ((paramObject == null) || (!(paramObject instanceof Integer))) {
        throw new SocketException("bad argument for SO_TIMEOUT");
      }
      int i = ((Integer)paramObject).intValue();
      if (i < 0) {
        throw new IllegalArgumentException("timeout < 0");
      }
      timeout = i;
      return;
    case 3: 
      if ((paramObject == null) || (!(paramObject instanceof Integer))) {
        throw new SocketException("bad argument for IP_TOS");
      }
      trafficClass = ((Integer)paramObject).intValue();
      break;
    case 4: 
      if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
        throw new SocketException("bad argument for SO_REUSEADDR");
      }
      break;
    case 32: 
      if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
        throw new SocketException("bad argument for SO_BROADCAST");
      }
      break;
    case 15: 
      throw new SocketException("Cannot re-bind Socket");
    case 4097: 
    case 4098: 
      if ((paramObject == null) || (!(paramObject instanceof Integer)) || (((Integer)paramObject).intValue() < 0)) {
        throw new SocketException("bad argument for SO_SNDBUF or SO_RCVBUF");
      }
      break;
    case 16: 
      if ((paramObject == null) || (!(paramObject instanceof InetAddress))) {
        throw new SocketException("bad argument for IP_MULTICAST_IF");
      }
      break;
    case 31: 
      if ((paramObject == null) || (!(paramObject instanceof NetworkInterface))) {
        throw new SocketException("bad argument for IP_MULTICAST_IF2");
      }
      break;
    case 18: 
      if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
        throw new SocketException("bad argument for IP_MULTICAST_LOOP");
      }
      break;
    default: 
      throw new SocketException("invalid option: " + paramInt);
    }
    socketSetOption(paramInt, paramObject);
  }
  
  public Object getOption(int paramInt)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket Closed");
    }
    Object localObject;
    switch (paramInt)
    {
    case 4102: 
      localObject = new Integer(timeout);
      break;
    case 3: 
      localObject = socketGetOption(paramInt);
      if (((Integer)localObject).intValue() == -1) {
        localObject = new Integer(trafficClass);
      }
      break;
    case 4: 
    case 15: 
    case 16: 
    case 18: 
    case 31: 
    case 32: 
    case 4097: 
    case 4098: 
      localObject = socketGetOption(paramInt);
      break;
    default: 
      throw new SocketException("invalid option: " + paramInt);
    }
    return localObject;
  }
  
  protected abstract void datagramSocketCreate()
    throws SocketException;
  
  protected abstract void datagramSocketClose();
  
  protected abstract void socketSetOption(int paramInt, Object paramObject)
    throws SocketException;
  
  protected abstract Object socketGetOption(int paramInt)
    throws SocketException;
  
  protected abstract void connect0(InetAddress paramInetAddress, int paramInt)
    throws SocketException;
  
  protected abstract void disconnect0(int paramInt);
  
  protected boolean nativeConnectDisabled()
  {
    return connectDisabled;
  }
  
  abstract int dataAvailable();
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\AbstractPlainDatagramSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */