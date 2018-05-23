package java.net;

import java.io.IOException;
import java.util.Enumeration;

public class MulticastSocket
  extends DatagramSocket
{
  private boolean interfaceSet;
  private Object ttlLock;
  private Object infLock;
  private InetAddress infAddress;
  
  public MulticastSocket()
    throws IOException
  {
    this(new InetSocketAddress(0));
  }
  
  public MulticastSocket(int paramInt)
    throws IOException
  {
    this(new InetSocketAddress(paramInt));
  }
  
  /* Error */
  public MulticastSocket(SocketAddress paramSocketAddress)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: checkcast 124	java/net/SocketAddress
    //   5: invokespecial 223	java/net/DatagramSocket:<init>	(Ljava/net/SocketAddress;)V
    //   8: aload_0
    //   9: new 111	java/lang/Object
    //   12: dup
    //   13: invokespecial 212	java/lang/Object:<init>	()V
    //   16: putfield 206	java/net/MulticastSocket:ttlLock	Ljava/lang/Object;
    //   19: aload_0
    //   20: new 111	java/lang/Object
    //   23: dup
    //   24: invokespecial 212	java/lang/Object:<init>	()V
    //   27: putfield 205	java/net/MulticastSocket:infLock	Ljava/lang/Object;
    //   30: aload_0
    //   31: aconst_null
    //   32: putfield 208	java/net/MulticastSocket:infAddress	Ljava/net/InetAddress;
    //   35: aload_0
    //   36: iconst_1
    //   37: invokevirtual 246	java/net/MulticastSocket:setReuseAddress	(Z)V
    //   40: aload_1
    //   41: ifnull +36 -> 77
    //   44: aload_0
    //   45: aload_1
    //   46: invokevirtual 250	java/net/MulticastSocket:bind	(Ljava/net/SocketAddress;)V
    //   49: aload_0
    //   50: invokevirtual 244	java/net/MulticastSocket:isBound	()Z
    //   53: ifne +24 -> 77
    //   56: aload_0
    //   57: invokevirtual 243	java/net/MulticastSocket:close	()V
    //   60: goto +17 -> 77
    //   63: astore_2
    //   64: aload_0
    //   65: invokevirtual 244	java/net/MulticastSocket:isBound	()Z
    //   68: ifne +7 -> 75
    //   71: aload_0
    //   72: invokevirtual 243	java/net/MulticastSocket:close	()V
    //   75: aload_2
    //   76: athrow
    //   77: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	78	0	this	MulticastSocket
    //   0	78	1	paramSocketAddress	SocketAddress
    //   63	13	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   44	49	63	finally
  }
  
  @Deprecated
  public void setTTL(byte paramByte)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setTTL(paramByte);
  }
  
  public void setTimeToLive(int paramInt)
    throws IOException
  {
    if ((paramInt < 0) || (paramInt > 255)) {
      throw new IllegalArgumentException("ttl out of range");
    }
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setTimeToLive(paramInt);
  }
  
  @Deprecated
  public byte getTTL()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return getImpl().getTTL();
  }
  
  public int getTimeToLive()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return getImpl().getTimeToLive();
  }
  
  public void joinGroup(InetAddress paramInetAddress)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    checkAddress(paramInetAddress, "joinGroup");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(paramInetAddress);
    }
    if (!paramInetAddress.isMulticastAddress()) {
      throw new SocketException("Not a multicast address");
    }
    NetworkInterface localNetworkInterface = NetworkInterface.getDefault();
    if ((!interfaceSet) && (localNetworkInterface != null)) {
      setNetworkInterface(localNetworkInterface);
    }
    getImpl().join(paramInetAddress);
  }
  
  public void leaveGroup(InetAddress paramInetAddress)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    checkAddress(paramInetAddress, "leaveGroup");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(paramInetAddress);
    }
    if (!paramInetAddress.isMulticastAddress()) {
      throw new SocketException("Not a multicast address");
    }
    getImpl().leave(paramInetAddress);
  }
  
  public void joinGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    if (oldImpl) {
      throw new UnsupportedOperationException();
    }
    checkAddress(((InetSocketAddress)paramSocketAddress).getAddress(), "joinGroup");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(((InetSocketAddress)paramSocketAddress).getAddress());
    }
    if (!((InetSocketAddress)paramSocketAddress).getAddress().isMulticastAddress()) {
      throw new SocketException("Not a multicast address");
    }
    getImpl().joinGroup(paramSocketAddress, paramNetworkInterface);
  }
  
  public void leaveGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    if (oldImpl) {
      throw new UnsupportedOperationException();
    }
    checkAddress(((InetSocketAddress)paramSocketAddress).getAddress(), "leaveGroup");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(((InetSocketAddress)paramSocketAddress).getAddress());
    }
    if (!((InetSocketAddress)paramSocketAddress).getAddress().isMulticastAddress()) {
      throw new SocketException("Not a multicast address");
    }
    getImpl().leaveGroup(paramSocketAddress, paramNetworkInterface);
  }
  
  public void setInterface(InetAddress paramInetAddress)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    checkAddress(paramInetAddress, "setInterface");
    synchronized (infLock)
    {
      getImpl().setOption(16, paramInetAddress);
      infAddress = paramInetAddress;
      interfaceSet = true;
    }
  }
  
  public InetAddress getInterface()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    synchronized (infLock)
    {
      InetAddress localInetAddress1 = (InetAddress)getImpl().getOption(16);
      if (infAddress == null) {
        return localInetAddress1;
      }
      if (localInetAddress1.equals(infAddress)) {
        return localInetAddress1;
      }
      try
      {
        NetworkInterface localNetworkInterface = NetworkInterface.getByInetAddress(localInetAddress1);
        Enumeration localEnumeration = localNetworkInterface.getInetAddresses();
        while (localEnumeration.hasMoreElements())
        {
          InetAddress localInetAddress2 = (InetAddress)localEnumeration.nextElement();
          if (localInetAddress2.equals(infAddress)) {
            return infAddress;
          }
        }
        infAddress = null;
        return localInetAddress1;
      }
      catch (Exception localException)
      {
        return localInetAddress1;
      }
    }
  }
  
  public void setNetworkInterface(NetworkInterface paramNetworkInterface)
    throws SocketException
  {
    synchronized (infLock)
    {
      getImpl().setOption(31, paramNetworkInterface);
      infAddress = null;
      interfaceSet = true;
    }
  }
  
  public NetworkInterface getNetworkInterface()
    throws SocketException
  {
    NetworkInterface localNetworkInterface = (NetworkInterface)getImpl().getOption(31);
    if ((localNetworkInterface.getIndex() == 0) || (localNetworkInterface.getIndex() == -1))
    {
      InetAddress[] arrayOfInetAddress = new InetAddress[1];
      arrayOfInetAddress[0] = InetAddress.anyLocalAddress();
      return new NetworkInterface(arrayOfInetAddress[0].getHostName(), 0, arrayOfInetAddress);
    }
    return localNetworkInterface;
  }
  
  public void setLoopbackMode(boolean paramBoolean)
    throws SocketException
  {
    getImpl().setOption(18, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getLoopbackMode()
    throws SocketException
  {
    return ((Boolean)getImpl().getOption(18)).booleanValue();
  }
  
  @Deprecated
  public void send(DatagramPacket paramDatagramPacket, byte paramByte)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    checkAddress(paramDatagramPacket.getAddress(), "send");
    synchronized (ttlLock)
    {
      synchronized (paramDatagramPacket)
      {
        Object localObject1;
        if (connectState == 0)
        {
          localObject1 = System.getSecurityManager();
          if (localObject1 != null) {
            if (paramDatagramPacket.getAddress().isMulticastAddress()) {
              ((SecurityManager)localObject1).checkMulticast(paramDatagramPacket.getAddress(), paramByte);
            } else {
              ((SecurityManager)localObject1).checkConnect(paramDatagramPacket.getAddress().getHostAddress(), paramDatagramPacket.getPort());
            }
          }
        }
        else
        {
          localObject1 = null;
          localObject1 = paramDatagramPacket.getAddress();
          if (localObject1 == null)
          {
            paramDatagramPacket.setAddress(connectedAddress);
            paramDatagramPacket.setPort(connectedPort);
          }
          else if ((!((InetAddress)localObject1).equals(connectedAddress)) || (paramDatagramPacket.getPort() != connectedPort))
          {
            throw new SecurityException("connected address and packet address differ");
          }
        }
        byte b = getTTL();
        try
        {
          if (paramByte != b) {
            getImpl().setTTL(paramByte);
          }
          getImpl().send(paramDatagramPacket);
        }
        finally
        {
          if (paramByte != b) {
            getImpl().setTTL(b);
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\MulticastSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */