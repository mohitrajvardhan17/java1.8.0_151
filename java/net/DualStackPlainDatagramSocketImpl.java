package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

class DualStackPlainDatagramSocketImpl
  extends AbstractPlainDatagramSocketImpl
{
  static JavaIOFileDescriptorAccess fdAccess = ;
  private final boolean exclusiveBind;
  private boolean reuseAddressEmulated;
  private boolean isReuseAddress;
  
  DualStackPlainDatagramSocketImpl(boolean paramBoolean)
  {
    exclusiveBind = paramBoolean;
  }
  
  protected void datagramSocketCreate()
    throws SocketException
  {
    if (fd == null) {
      throw new SocketException("Socket closed");
    }
    int i = socketCreate(false);
    fdAccess.set(fd, i);
  }
  
  protected synchronized void bind0(int paramInt, InetAddress paramInetAddress)
    throws SocketException
  {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null) {
      throw new NullPointerException("argument address");
    }
    socketBind(i, paramInetAddress, paramInt, exclusiveBind);
    if (paramInt == 0) {
      localPort = socketLocalPort(i);
    } else {
      localPort = paramInt;
    }
  }
  
  protected synchronized int peek(InetAddress paramInetAddress)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null) {
      throw new NullPointerException("Null address in peek()");
    }
    DatagramPacket localDatagramPacket = new DatagramPacket(new byte[1], 1);
    int j = peekData(localDatagramPacket);
    paramInetAddress = localDatagramPacket.getAddress();
    return j;
  }
  
  protected synchronized int peekData(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    if (paramDatagramPacket == null) {
      throw new NullPointerException("packet");
    }
    if (paramDatagramPacket.getData() == null) {
      throw new NullPointerException("packet buffer");
    }
    return socketReceiveOrPeekData(i, paramDatagramPacket, timeout, connected, true);
  }
  
  protected synchronized void receive0(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    if (paramDatagramPacket == null) {
      throw new NullPointerException("packet");
    }
    if (paramDatagramPacket.getData() == null) {
      throw new NullPointerException("packet buffer");
    }
    socketReceiveOrPeekData(i, paramDatagramPacket, timeout, connected, false);
  }
  
  protected void send(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    if (paramDatagramPacket == null) {
      throw new NullPointerException("null packet");
    }
    if ((paramDatagramPacket.getAddress() == null) || (paramDatagramPacket.getData() == null)) {
      throw new NullPointerException("null address || null buffer");
    }
    socketSend(i, paramDatagramPacket.getData(), paramDatagramPacket.getOffset(), paramDatagramPacket.getLength(), paramDatagramPacket.getAddress(), paramDatagramPacket.getPort(), connected);
  }
  
  protected void connect0(InetAddress paramInetAddress, int paramInt)
    throws SocketException
  {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null) {
      throw new NullPointerException("address");
    }
    socketConnect(i, paramInetAddress, paramInt);
  }
  
  protected void disconnect0(int paramInt)
  {
    if ((fd == null) || (!fd.valid())) {
      return;
    }
    socketDisconnect(fdAccess.get(fd));
  }
  
  protected void datagramSocketClose()
  {
    if ((fd == null) || (!fd.valid())) {
      return;
    }
    socketClose(fdAccess.get(fd));
    fdAccess.set(fd, -1);
  }
  
  protected void socketSetOption(int paramInt, Object paramObject)
    throws SocketException
  {
    int i = checkAndReturnNativeFD();
    int j = 0;
    switch (paramInt)
    {
    case 3: 
    case 4097: 
    case 4098: 
      j = ((Integer)paramObject).intValue();
      break;
    case 4: 
      if ((exclusiveBind) && (localPort != 0))
      {
        reuseAddressEmulated = true;
        isReuseAddress = ((Boolean)paramObject).booleanValue();
        return;
      }
    case 32: 
      j = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      break;
    }
    throw new SocketException("Option not supported");
    socketSetIntOption(i, paramInt, j);
  }
  
  protected Object socketGetOption(int paramInt)
    throws SocketException
  {
    int i = checkAndReturnNativeFD();
    if (paramInt == 15) {
      return socketLocalAddress(i);
    }
    if ((paramInt == 4) && (reuseAddressEmulated)) {
      return Boolean.valueOf(isReuseAddress);
    }
    int j = socketGetIntOption(i, paramInt);
    Object localObject = null;
    switch (paramInt)
    {
    case 4: 
    case 32: 
      localObject = j == 0 ? Boolean.FALSE : Boolean.TRUE;
      break;
    case 3: 
    case 4097: 
    case 4098: 
      localObject = new Integer(j);
      break;
    default: 
      throw new SocketException("Option not supported");
    }
    return localObject;
  }
  
  protected void join(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    throw new IOException("Method not implemented!");
  }
  
  protected void leave(InetAddress paramInetAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    throw new IOException("Method not implemented!");
  }
  
  protected void setTimeToLive(int paramInt)
    throws IOException
  {
    throw new IOException("Method not implemented!");
  }
  
  protected int getTimeToLive()
    throws IOException
  {
    throw new IOException("Method not implemented!");
  }
  
  @Deprecated
  protected void setTTL(byte paramByte)
    throws IOException
  {
    throw new IOException("Method not implemented!");
  }
  
  @Deprecated
  protected byte getTTL()
    throws IOException
  {
    throw new IOException("Method not implemented!");
  }
  
  private int checkAndReturnNativeFD()
    throws SocketException
  {
    if ((fd == null) || (!fd.valid())) {
      throw new SocketException("Socket closed");
    }
    return fdAccess.get(fd);
  }
  
  private static native void initIDs();
  
  private static native int socketCreate(boolean paramBoolean);
  
  private static native void socketBind(int paramInt1, InetAddress paramInetAddress, int paramInt2, boolean paramBoolean)
    throws SocketException;
  
  private static native void socketConnect(int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws SocketException;
  
  private static native void socketDisconnect(int paramInt);
  
  private static native void socketClose(int paramInt);
  
  private static native int socketLocalPort(int paramInt)
    throws SocketException;
  
  private static native Object socketLocalAddress(int paramInt)
    throws SocketException;
  
  private static native int socketReceiveOrPeekData(int paramInt1, DatagramPacket paramDatagramPacket, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException;
  
  private static native void socketSend(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, InetAddress paramInetAddress, int paramInt4, boolean paramBoolean)
    throws IOException;
  
  private static native void socketSetIntOption(int paramInt1, int paramInt2, int paramInt3)
    throws SocketException;
  
  private static native int socketGetIntOption(int paramInt1, int paramInt2)
    throws SocketException;
  
  native int dataAvailable();
  
  static
  {
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\DualStackPlainDatagramSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */