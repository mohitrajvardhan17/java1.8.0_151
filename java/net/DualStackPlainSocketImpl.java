package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

class DualStackPlainSocketImpl
  extends AbstractPlainSocketImpl
{
  static JavaIOFileDescriptorAccess fdAccess = ;
  private final boolean exclusiveBind;
  private boolean isReuseAddress;
  static final int WOULDBLOCK = -2;
  
  public DualStackPlainSocketImpl(boolean paramBoolean)
  {
    exclusiveBind = paramBoolean;
  }
  
  public DualStackPlainSocketImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean)
  {
    fd = paramFileDescriptor;
    exclusiveBind = paramBoolean;
  }
  
  void socketCreate(boolean paramBoolean)
    throws IOException
  {
    if (fd == null) {
      throw new SocketException("Socket closed");
    }
    int i = socket0(paramBoolean, false);
    fdAccess.set(fd, i);
  }
  
  void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null) {
      throw new NullPointerException("inet address argument is null.");
    }
    int j;
    if (paramInt2 <= 0)
    {
      j = connect0(i, paramInetAddress, paramInt1);
    }
    else
    {
      configureBlocking(i, false);
      try
      {
        j = connect0(i, paramInetAddress, paramInt1);
        if (j == -2) {
          waitForConnect(i, paramInt2);
        }
      }
      finally
      {
        configureBlocking(i, true);
      }
    }
    if (localport == 0) {
      localport = localPort0(i);
    }
  }
  
  void socketBind(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    if (paramInetAddress == null) {
      throw new NullPointerException("inet address argument is null.");
    }
    bind0(i, paramInetAddress, paramInt, exclusiveBind);
    if (paramInt == 0) {
      localport = localPort0(i);
    } else {
      localport = paramInt;
    }
    address = paramInetAddress;
  }
  
  void socketListen(int paramInt)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    listen0(i, paramInt);
  }
  
  void socketAccept(SocketImpl paramSocketImpl)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    if (paramSocketImpl == null) {
      throw new NullPointerException("socket is null");
    }
    int j = -1;
    InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
    if (timeout <= 0)
    {
      j = accept0(i, arrayOfInetSocketAddress);
    }
    else
    {
      configureBlocking(i, false);
      try
      {
        waitForNewConnection(i, timeout);
        j = accept0(i, arrayOfInetSocketAddress);
        if (j != -1) {
          configureBlocking(j, true);
        }
      }
      finally
      {
        configureBlocking(i, true);
      }
    }
    fdAccess.set(fd, j);
    InetSocketAddress localInetSocketAddress = arrayOfInetSocketAddress[0];
    port = localInetSocketAddress.getPort();
    address = localInetSocketAddress.getAddress();
    localport = localport;
  }
  
  int socketAvailable()
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    return available0(i);
  }
  
  void socketClose0(boolean paramBoolean)
    throws IOException
  {
    if (fd == null) {
      throw new SocketException("Socket closed");
    }
    if (!fd.valid()) {
      return;
    }
    int i = fdAccess.get(fd);
    fdAccess.set(fd, -1);
    close0(i);
  }
  
  void socketShutdown(int paramInt)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    shutdown0(i, paramInt);
  }
  
  void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject)
    throws SocketException
  {
    int i = checkAndReturnNativeFD();
    if (paramInt == 4102) {
      return;
    }
    int j = 0;
    switch (paramInt)
    {
    case 4: 
      if (exclusiveBind)
      {
        isReuseAddress = paramBoolean;
        return;
      }
    case 1: 
    case 8: 
    case 4099: 
      j = paramBoolean ? 1 : 0;
      break;
    case 3: 
    case 4097: 
    case 4098: 
      j = ((Integer)paramObject).intValue();
      break;
    case 128: 
      if (paramBoolean) {
        j = ((Integer)paramObject).intValue();
      } else {
        j = -1;
      }
      break;
    }
    throw new SocketException("Option not supported");
    setIntOption(i, paramInt, j);
  }
  
  int socketGetOption(int paramInt, Object paramObject)
    throws SocketException
  {
    int i = checkAndReturnNativeFD();
    if (paramInt == 15)
    {
      localAddress(i, (InetAddressContainer)paramObject);
      return 0;
    }
    if ((paramInt == 4) && (exclusiveBind)) {
      return isReuseAddress ? 1 : -1;
    }
    int j = getIntOption(i, paramInt);
    switch (paramInt)
    {
    case 1: 
    case 4: 
    case 8: 
    case 4099: 
      return j == 0 ? -1 : 1;
    }
    return j;
  }
  
  void socketSendUrgentData(int paramInt)
    throws IOException
  {
    int i = checkAndReturnNativeFD();
    sendOOB(i, paramInt);
  }
  
  private int checkAndReturnNativeFD()
    throws SocketException
  {
    if ((fd == null) || (!fd.valid())) {
      throw new SocketException("Socket closed");
    }
    return fdAccess.get(fd);
  }
  
  static native void initIDs();
  
  static native int socket0(boolean paramBoolean1, boolean paramBoolean2)
    throws IOException;
  
  static native void bind0(int paramInt1, InetAddress paramInetAddress, int paramInt2, boolean paramBoolean)
    throws IOException;
  
  static native int connect0(int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws IOException;
  
  static native void waitForConnect(int paramInt1, int paramInt2)
    throws IOException;
  
  static native int localPort0(int paramInt)
    throws IOException;
  
  static native void localAddress(int paramInt, InetAddressContainer paramInetAddressContainer)
    throws SocketException;
  
  static native void listen0(int paramInt1, int paramInt2)
    throws IOException;
  
  static native int accept0(int paramInt, InetSocketAddress[] paramArrayOfInetSocketAddress)
    throws IOException;
  
  static native void waitForNewConnection(int paramInt1, int paramInt2)
    throws IOException;
  
  static native int available0(int paramInt)
    throws IOException;
  
  static native void close0(int paramInt)
    throws IOException;
  
  static native void shutdown0(int paramInt1, int paramInt2)
    throws IOException;
  
  static native void setIntOption(int paramInt1, int paramInt2, int paramInt3)
    throws SocketException;
  
  static native int getIntOption(int paramInt1, int paramInt2)
    throws SocketException;
  
  static native void sendOOB(int paramInt1, int paramInt2)
    throws IOException;
  
  static native void configureBlocking(int paramInt, boolean paramBoolean)
    throws IOException;
  
  static
  {
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\DualStackPlainSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */