package sun.nio.ch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketOption;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class SocketAdaptor
  extends Socket
{
  private final SocketChannelImpl sc;
  private volatile int timeout = 0;
  private InputStream socketInputStream = null;
  
  private SocketAdaptor(SocketChannelImpl paramSocketChannelImpl)
    throws SocketException
  {
    super((SocketImpl)null);
    sc = paramSocketChannelImpl;
  }
  
  public static Socket create(SocketChannelImpl paramSocketChannelImpl)
  {
    try
    {
      return new SocketAdaptor(paramSocketChannelImpl);
    }
    catch (SocketException localSocketException)
    {
      throw new InternalError("Should not reach here");
    }
  }
  
  public SocketChannel getChannel()
  {
    return sc;
  }
  
  public void connect(SocketAddress paramSocketAddress)
    throws IOException
  {
    connect(paramSocketAddress, 0);
  }
  
  public void connect(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    if (paramSocketAddress == null) {
      throw new IllegalArgumentException("connect: The address can't be null");
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("connect: timeout can't be negative");
    }
    synchronized (sc.blockingLock())
    {
      if (!sc.isBlocking()) {
        throw new IllegalBlockingModeException();
      }
      try
      {
        if (paramInt == 0)
        {
          sc.connect(paramSocketAddress);
          return;
        }
        sc.configureBlocking(false);
        try
        {
          if (sc.connect(paramSocketAddress))
          {
            if (sc.isOpen()) {
              sc.configureBlocking(true);
            }
            return;
          }
          long l1 = paramInt;
          for (;;)
          {
            if (!sc.isOpen()) {
              throw new ClosedChannelException();
            }
            long l2 = System.currentTimeMillis();
            int i = sc.poll(Net.POLLCONN, l1);
            if ((i > 0) && (sc.finishConnect())) {
              break;
            }
            l1 -= System.currentTimeMillis() - l2;
            if (l1 <= 0L)
            {
              try
              {
                sc.close();
              }
              catch (IOException localIOException) {}
              throw new SocketTimeoutException();
            }
          }
        }
        finally
        {
          if (sc.isOpen()) {
            sc.configureBlocking(true);
          }
        }
      }
      catch (Exception localException)
      {
        Net.translateException(localException, true);
      }
    }
  }
  
  public void bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    try
    {
      sc.bind(paramSocketAddress);
    }
    catch (Exception localException)
    {
      Net.translateException(localException);
    }
  }
  
  public InetAddress getInetAddress()
  {
    SocketAddress localSocketAddress = sc.remoteAddress();
    if (localSocketAddress == null) {
      return null;
    }
    return ((InetSocketAddress)localSocketAddress).getAddress();
  }
  
  public InetAddress getLocalAddress()
  {
    if (sc.isOpen())
    {
      InetSocketAddress localInetSocketAddress = sc.localAddress();
      if (localInetSocketAddress != null) {
        return Net.getRevealedLocalAddress(localInetSocketAddress).getAddress();
      }
    }
    return new InetSocketAddress(0).getAddress();
  }
  
  public int getPort()
  {
    SocketAddress localSocketAddress = sc.remoteAddress();
    if (localSocketAddress == null) {
      return 0;
    }
    return ((InetSocketAddress)localSocketAddress).getPort();
  }
  
  public int getLocalPort()
  {
    InetSocketAddress localInetSocketAddress = sc.localAddress();
    if (localInetSocketAddress == null) {
      return -1;
    }
    return ((InetSocketAddress)localInetSocketAddress).getPort();
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (!sc.isOpen()) {
      throw new SocketException("Socket is closed");
    }
    if (!sc.isConnected()) {
      throw new SocketException("Socket is not connected");
    }
    if (!sc.isInputOpen()) {
      throw new SocketException("Socket input is shutdown");
    }
    if (socketInputStream == null) {
      try
      {
        socketInputStream = ((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public InputStream run()
            throws IOException
          {
            return new SocketAdaptor.SocketInputStream(SocketAdaptor.this, null);
          }
        }));
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((IOException)localPrivilegedActionException.getException());
      }
    }
    return socketInputStream;
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (!sc.isOpen()) {
      throw new SocketException("Socket is closed");
    }
    if (!sc.isConnected()) {
      throw new SocketException("Socket is not connected");
    }
    if (!sc.isOutputOpen()) {
      throw new SocketException("Socket output is shutdown");
    }
    OutputStream localOutputStream = null;
    try
    {
      localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public OutputStream run()
          throws IOException
        {
          return Channels.newOutputStream(sc);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
    return localOutputStream;
  }
  
  private void setBooleanOption(SocketOption<Boolean> paramSocketOption, boolean paramBoolean)
    throws SocketException
  {
    try
    {
      sc.setOption(paramSocketOption, Boolean.valueOf(paramBoolean));
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
  }
  
  private void setIntOption(SocketOption<Integer> paramSocketOption, int paramInt)
    throws SocketException
  {
    try
    {
      sc.setOption(paramSocketOption, Integer.valueOf(paramInt));
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
  }
  
  private boolean getBooleanOption(SocketOption<Boolean> paramSocketOption)
    throws SocketException
  {
    try
    {
      return ((Boolean)sc.getOption(paramSocketOption)).booleanValue();
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
    return false;
  }
  
  private int getIntOption(SocketOption<Integer> paramSocketOption)
    throws SocketException
  {
    try
    {
      return ((Integer)sc.getOption(paramSocketOption)).intValue();
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
    return -1;
  }
  
  public void setTcpNoDelay(boolean paramBoolean)
    throws SocketException
  {
    setBooleanOption(StandardSocketOptions.TCP_NODELAY, paramBoolean);
  }
  
  public boolean getTcpNoDelay()
    throws SocketException
  {
    return getBooleanOption(StandardSocketOptions.TCP_NODELAY);
  }
  
  public void setSoLinger(boolean paramBoolean, int paramInt)
    throws SocketException
  {
    if (!paramBoolean) {
      paramInt = -1;
    }
    setIntOption(StandardSocketOptions.SO_LINGER, paramInt);
  }
  
  public int getSoLinger()
    throws SocketException
  {
    return getIntOption(StandardSocketOptions.SO_LINGER);
  }
  
  public void sendUrgentData(int paramInt)
    throws IOException
  {
    int i = sc.sendOutOfBandData((byte)paramInt);
    if (i == 0) {
      throw new IOException("Socket buffer full");
    }
  }
  
  public void setOOBInline(boolean paramBoolean)
    throws SocketException
  {
    setBooleanOption(ExtendedSocketOption.SO_OOBINLINE, paramBoolean);
  }
  
  public boolean getOOBInline()
    throws SocketException
  {
    return getBooleanOption(ExtendedSocketOption.SO_OOBINLINE);
  }
  
  public void setSoTimeout(int paramInt)
    throws SocketException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("timeout can't be negative");
    }
    timeout = paramInt;
  }
  
  public int getSoTimeout()
    throws SocketException
  {
    return timeout;
  }
  
  public void setSendBufferSize(int paramInt)
    throws SocketException
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Invalid send size");
    }
    setIntOption(StandardSocketOptions.SO_SNDBUF, paramInt);
  }
  
  public int getSendBufferSize()
    throws SocketException
  {
    return getIntOption(StandardSocketOptions.SO_SNDBUF);
  }
  
  public void setReceiveBufferSize(int paramInt)
    throws SocketException
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Invalid receive size");
    }
    setIntOption(StandardSocketOptions.SO_RCVBUF, paramInt);
  }
  
  public int getReceiveBufferSize()
    throws SocketException
  {
    return getIntOption(StandardSocketOptions.SO_RCVBUF);
  }
  
  public void setKeepAlive(boolean paramBoolean)
    throws SocketException
  {
    setBooleanOption(StandardSocketOptions.SO_KEEPALIVE, paramBoolean);
  }
  
  public boolean getKeepAlive()
    throws SocketException
  {
    return getBooleanOption(StandardSocketOptions.SO_KEEPALIVE);
  }
  
  public void setTrafficClass(int paramInt)
    throws SocketException
  {
    setIntOption(StandardSocketOptions.IP_TOS, paramInt);
  }
  
  public int getTrafficClass()
    throws SocketException
  {
    return getIntOption(StandardSocketOptions.IP_TOS);
  }
  
  public void setReuseAddress(boolean paramBoolean)
    throws SocketException
  {
    setBooleanOption(StandardSocketOptions.SO_REUSEADDR, paramBoolean);
  }
  
  public boolean getReuseAddress()
    throws SocketException
  {
    return getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
  }
  
  public void close()
    throws IOException
  {
    sc.close();
  }
  
  public void shutdownInput()
    throws IOException
  {
    try
    {
      sc.shutdownInput();
    }
    catch (Exception localException)
    {
      Net.translateException(localException);
    }
  }
  
  public void shutdownOutput()
    throws IOException
  {
    try
    {
      sc.shutdownOutput();
    }
    catch (Exception localException)
    {
      Net.translateException(localException);
    }
  }
  
  public String toString()
  {
    if (sc.isConnected()) {
      return "Socket[addr=" + getInetAddress() + ",port=" + getPort() + ",localport=" + getLocalPort() + "]";
    }
    return "Socket[unconnected]";
  }
  
  public boolean isConnected()
  {
    return sc.isConnected();
  }
  
  public boolean isBound()
  {
    return sc.localAddress() != null;
  }
  
  public boolean isClosed()
  {
    return !sc.isOpen();
  }
  
  public boolean isInputShutdown()
  {
    return !sc.isInputOpen();
  }
  
  public boolean isOutputShutdown()
  {
    return !sc.isOutputOpen();
  }
  
  private class SocketInputStream
    extends ChannelInputStream
  {
    private SocketInputStream()
    {
      super();
    }
    
    protected int read(ByteBuffer paramByteBuffer)
      throws IOException
    {
      synchronized (sc.blockingLock())
      {
        if (!sc.isBlocking()) {
          throw new IllegalBlockingModeException();
        }
        if (timeout == 0) {
          return sc.read(paramByteBuffer);
        }
        sc.configureBlocking(false);
        try
        {
          int i;
          if ((i = sc.read(paramByteBuffer)) != 0)
          {
            int j = i;
            if (sc.isOpen()) {
              sc.configureBlocking(true);
            }
            return j;
          }
          long l1 = timeout;
          for (;;)
          {
            if (!sc.isOpen()) {
              throw new ClosedChannelException();
            }
            long l2 = System.currentTimeMillis();
            int k = sc.poll(Net.POLLIN, l1);
            if ((k > 0) && ((i = sc.read(paramByteBuffer)) != 0))
            {
              int m = i;
              if (sc.isOpen()) {
                sc.configureBlocking(true);
              }
              return m;
            }
            l1 -= System.currentTimeMillis() - l2;
            if (l1 <= 0L) {
              throw new SocketTimeoutException();
            }
          }
          localObject2 = finally;
        }
        finally
        {
          if (sc.isOpen()) {
            sc.configureBlocking(true);
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\SocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */