package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.net.ResourceManager;

class TwoStacksPlainDatagramSocketImpl
  extends AbstractPlainDatagramSocketImpl
{
  private FileDescriptor fd1;
  private InetAddress anyLocalBoundAddr = null;
  private int fduse = -1;
  private int lastfd = -1;
  private final boolean exclusiveBind;
  private boolean reuseAddressEmulated;
  private boolean isReuseAddress;
  
  TwoStacksPlainDatagramSocketImpl(boolean paramBoolean)
  {
    exclusiveBind = paramBoolean;
  }
  
  protected synchronized void create()
    throws SocketException
  {
    fd1 = new FileDescriptor();
    try
    {
      super.create();
    }
    catch (SocketException localSocketException)
    {
      fd1 = null;
      throw localSocketException;
    }
  }
  
  protected synchronized void bind(int paramInt, InetAddress paramInetAddress)
    throws SocketException
  {
    super.bind(paramInt, paramInetAddress);
    if (paramInetAddress.isAnyLocalAddress()) {
      anyLocalBoundAddr = paramInetAddress;
    }
  }
  
  protected synchronized void bind0(int paramInt, InetAddress paramInetAddress)
    throws SocketException
  {
    bind0(paramInt, paramInetAddress, exclusiveBind);
  }
  
  /* Error */
  protected synchronized void receive(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokevirtual 155	java/net/TwoStacksPlainDatagramSocketImpl:receive0	(Ljava/net/DatagramPacket;)V
    //   5: aload_0
    //   6: iconst_m1
    //   7: putfield 128	java/net/TwoStacksPlainDatagramSocketImpl:fduse	I
    //   10: goto +11 -> 21
    //   13: astore_2
    //   14: aload_0
    //   15: iconst_m1
    //   16: putfield 128	java/net/TwoStacksPlainDatagramSocketImpl:fduse	I
    //   19: aload_2
    //   20: athrow
    //   21: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	22	0	this	TwoStacksPlainDatagramSocketImpl
    //   0	22	1	paramDatagramPacket	DatagramPacket
    //   13	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	5	13	finally
  }
  
  public Object getOption(int paramInt)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket Closed");
    }
    if (paramInt == 15)
    {
      if ((fd != null) && (fd1 != null) && (!connected)) {
        return anyLocalBoundAddr;
      }
      int i = connectedAddress == null ? -1 : connectedAddress.holder().getFamily();
      return socketLocalAddress(i);
    }
    if ((paramInt == 4) && (reuseAddressEmulated)) {
      return Boolean.valueOf(isReuseAddress);
    }
    return super.getOption(paramInt);
  }
  
  protected void socketSetOption(int paramInt, Object paramObject)
    throws SocketException
  {
    if ((paramInt == 4) && (exclusiveBind) && (localPort != 0))
    {
      reuseAddressEmulated = true;
      isReuseAddress = ((Boolean)paramObject).booleanValue();
    }
    else
    {
      socketNativeSetOption(paramInt, paramObject);
    }
  }
  
  protected boolean isClosed()
  {
    return (fd == null) && (fd1 == null);
  }
  
  protected void close()
  {
    if ((fd != null) || (fd1 != null))
    {
      datagramSocketClose();
      ResourceManager.afterUdpClose();
      fd = null;
      fd1 = null;
    }
  }
  
  protected synchronized native void bind0(int paramInt, InetAddress paramInetAddress, boolean paramBoolean)
    throws SocketException;
  
  protected native void send(DatagramPacket paramDatagramPacket)
    throws IOException;
  
  protected synchronized native int peek(InetAddress paramInetAddress)
    throws IOException;
  
  protected synchronized native int peekData(DatagramPacket paramDatagramPacket)
    throws IOException;
  
  protected synchronized native void receive0(DatagramPacket paramDatagramPacket)
    throws IOException;
  
  protected native void setTimeToLive(int paramInt)
    throws IOException;
  
  protected native int getTimeToLive()
    throws IOException;
  
  @Deprecated
  protected native void setTTL(byte paramByte)
    throws IOException;
  
  @Deprecated
  protected native byte getTTL()
    throws IOException;
  
  protected native void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
    throws IOException;
  
  protected native void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
    throws IOException;
  
  protected native void datagramSocketCreate()
    throws SocketException;
  
  protected native void datagramSocketClose();
  
  protected native void socketNativeSetOption(int paramInt, Object paramObject)
    throws SocketException;
  
  protected native Object socketGetOption(int paramInt)
    throws SocketException;
  
  protected native void connect0(InetAddress paramInetAddress, int paramInt)
    throws SocketException;
  
  protected native Object socketLocalAddress(int paramInt)
    throws SocketException;
  
  protected native void disconnect0(int paramInt);
  
  native int dataAvailable();
  
  private static native void init();
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\TwoStacksPlainDatagramSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */