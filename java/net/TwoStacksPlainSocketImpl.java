package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import sun.net.ResourceManager;

class TwoStacksPlainSocketImpl
  extends AbstractPlainSocketImpl
{
  private FileDescriptor fd1;
  private InetAddress anyLocalBoundAddr = null;
  private int lastfd = -1;
  private final boolean exclusiveBind;
  private boolean isReuseAddress;
  
  public TwoStacksPlainSocketImpl(boolean paramBoolean)
  {
    exclusiveBind = paramBoolean;
  }
  
  public TwoStacksPlainSocketImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean)
  {
    fd = paramFileDescriptor;
    exclusiveBind = paramBoolean;
  }
  
  protected synchronized void create(boolean paramBoolean)
    throws IOException
  {
    fd1 = new FileDescriptor();
    try
    {
      super.create(paramBoolean);
    }
    catch (IOException localIOException)
    {
      fd1 = null;
      throw localIOException;
    }
  }
  
  protected synchronized void bind(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    super.bind(paramInetAddress, paramInt);
    if (paramInetAddress.isAnyLocalAddress()) {
      anyLocalBoundAddr = paramInetAddress;
    }
  }
  
  public Object getOption(int paramInt)
    throws SocketException
  {
    if (isClosedOrPending()) {
      throw new SocketException("Socket Closed");
    }
    if (paramInt == 15)
    {
      if ((fd != null) && (fd1 != null)) {
        return anyLocalBoundAddr;
      }
      InetAddressContainer localInetAddressContainer = new InetAddressContainer();
      socketGetOption(paramInt, localInetAddressContainer);
      return addr;
    }
    if ((paramInt == 4) && (exclusiveBind)) {
      return Boolean.valueOf(isReuseAddress);
    }
    return super.getOption(paramInt);
  }
  
  void socketBind(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    socketBind(paramInetAddress, paramInt, exclusiveBind);
  }
  
  void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject)
    throws SocketException
  {
    if ((paramInt == 4) && (exclusiveBind)) {
      isReuseAddress = paramBoolean;
    } else {
      socketNativeSetOption(paramInt, paramBoolean, paramObject);
    }
  }
  
  protected void close()
    throws IOException
  {
    synchronized (fdLock)
    {
      if ((fd != null) || (fd1 != null))
      {
        if (!stream) {
          ResourceManager.afterUdpClose();
        }
        if (fdUseCount == 0)
        {
          if (closePending) {
            return;
          }
          closePending = true;
          socketClose();
          fd = null;
          fd1 = null;
          return;
        }
        if (!closePending)
        {
          closePending = true;
          fdUseCount -= 1;
          socketClose();
        }
      }
    }
  }
  
  void reset()
    throws IOException
  {
    if ((fd != null) || (fd1 != null)) {
      socketClose();
    }
    fd = null;
    fd1 = null;
    super.reset();
  }
  
  public boolean isClosedOrPending()
  {
    synchronized (fdLock)
    {
      return (closePending) || ((fd == null) && (fd1 == null));
    }
  }
  
  static native void initProto();
  
  native void socketCreate(boolean paramBoolean)
    throws IOException;
  
  native void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2)
    throws IOException;
  
  native void socketBind(InetAddress paramInetAddress, int paramInt, boolean paramBoolean)
    throws IOException;
  
  native void socketListen(int paramInt)
    throws IOException;
  
  native void socketAccept(SocketImpl paramSocketImpl)
    throws IOException;
  
  native int socketAvailable()
    throws IOException;
  
  native void socketClose0(boolean paramBoolean)
    throws IOException;
  
  native void socketShutdown(int paramInt)
    throws IOException;
  
  native void socketNativeSetOption(int paramInt, boolean paramBoolean, Object paramObject)
    throws SocketException;
  
  native int socketGetOption(int paramInt, Object paramObject)
    throws SocketException;
  
  native void socketSendUrgentData(int paramInt)
    throws IOException;
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\TwoStacksPlainSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */