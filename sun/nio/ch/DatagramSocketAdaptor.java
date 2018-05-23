package sun.nio.ch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;

public class DatagramSocketAdaptor
  extends DatagramSocket
{
  private final DatagramChannelImpl dc;
  private volatile int timeout = 0;
  private static final DatagramSocketImpl dummyDatagramSocket = new DatagramSocketImpl()
  {
    protected void create()
      throws SocketException
    {}
    
    protected void bind(int paramAnonymousInt, InetAddress paramAnonymousInetAddress)
      throws SocketException
    {}
    
    protected void send(DatagramPacket paramAnonymousDatagramPacket)
      throws IOException
    {}
    
    protected int peek(InetAddress paramAnonymousInetAddress)
      throws IOException
    {
      return 0;
    }
    
    protected int peekData(DatagramPacket paramAnonymousDatagramPacket)
      throws IOException
    {
      return 0;
    }
    
    protected void receive(DatagramPacket paramAnonymousDatagramPacket)
      throws IOException
    {}
    
    @Deprecated
    protected void setTTL(byte paramAnonymousByte)
      throws IOException
    {}
    
    @Deprecated
    protected byte getTTL()
      throws IOException
    {
      return 0;
    }
    
    protected void setTimeToLive(int paramAnonymousInt)
      throws IOException
    {}
    
    protected int getTimeToLive()
      throws IOException
    {
      return 0;
    }
    
    protected void join(InetAddress paramAnonymousInetAddress)
      throws IOException
    {}
    
    protected void leave(InetAddress paramAnonymousInetAddress)
      throws IOException
    {}
    
    protected void joinGroup(SocketAddress paramAnonymousSocketAddress, NetworkInterface paramAnonymousNetworkInterface)
      throws IOException
    {}
    
    protected void leaveGroup(SocketAddress paramAnonymousSocketAddress, NetworkInterface paramAnonymousNetworkInterface)
      throws IOException
    {}
    
    protected void close() {}
    
    public Object getOption(int paramAnonymousInt)
      throws SocketException
    {
      return null;
    }
    
    public void setOption(int paramAnonymousInt, Object paramAnonymousObject)
      throws SocketException
    {}
  };
  
  private DatagramSocketAdaptor(DatagramChannelImpl paramDatagramChannelImpl)
    throws IOException
  {
    super(dummyDatagramSocket);
    dc = paramDatagramChannelImpl;
  }
  
  public static DatagramSocket create(DatagramChannelImpl paramDatagramChannelImpl)
  {
    try
    {
      return new DatagramSocketAdaptor(paramDatagramChannelImpl);
    }
    catch (IOException localIOException)
    {
      throw new Error(localIOException);
    }
  }
  
  private void connectInternal(SocketAddress paramSocketAddress)
    throws SocketException
  {
    InetSocketAddress localInetSocketAddress = Net.asInetSocketAddress(paramSocketAddress);
    int i = localInetSocketAddress.getPort();
    if ((i < 0) || (i > 65535)) {
      throw new IllegalArgumentException("connect: " + i);
    }
    if (paramSocketAddress == null) {
      throw new IllegalArgumentException("connect: null address");
    }
    if (isClosed()) {
      return;
    }
    try
    {
      dc.connect(paramSocketAddress);
    }
    catch (Exception localException)
    {
      Net.translateToSocketException(localException);
    }
  }
  
  public void bind(SocketAddress paramSocketAddress)
    throws SocketException
  {
    try
    {
      if (paramSocketAddress == null) {
        paramSocketAddress = new InetSocketAddress(0);
      }
      dc.bind(paramSocketAddress);
    }
    catch (Exception localException)
    {
      Net.translateToSocketException(localException);
    }
  }
  
  public void connect(InetAddress paramInetAddress, int paramInt)
  {
    try
    {
      connectInternal(new InetSocketAddress(paramInetAddress, paramInt));
    }
    catch (SocketException localSocketException) {}
  }
  
  public void connect(SocketAddress paramSocketAddress)
    throws SocketException
  {
    if (paramSocketAddress == null) {
      throw new IllegalArgumentException("Address can't be null");
    }
    connectInternal(paramSocketAddress);
  }
  
  public void disconnect()
  {
    try
    {
      dc.disconnect();
    }
    catch (IOException localIOException)
    {
      throw new Error(localIOException);
    }
  }
  
  public boolean isBound()
  {
    return dc.localAddress() != null;
  }
  
  public boolean isConnected()
  {
    return dc.remoteAddress() != null;
  }
  
  public InetAddress getInetAddress()
  {
    return isConnected() ? Net.asInetSocketAddress(dc.remoteAddress()).getAddress() : null;
  }
  
  public int getPort()
  {
    return isConnected() ? Net.asInetSocketAddress(dc.remoteAddress()).getPort() : -1;
  }
  
  public void send(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    synchronized (dc.blockingLock())
    {
      if (!dc.isBlocking()) {
        throw new IllegalBlockingModeException();
      }
      try
      {
        synchronized (paramDatagramPacket)
        {
          ByteBuffer localByteBuffer = ByteBuffer.wrap(paramDatagramPacket.getData(), paramDatagramPacket.getOffset(), paramDatagramPacket.getLength());
          if (dc.isConnected())
          {
            if (paramDatagramPacket.getAddress() == null)
            {
              InetSocketAddress localInetSocketAddress = (InetSocketAddress)dc.remoteAddress();
              paramDatagramPacket.setPort(localInetSocketAddress.getPort());
              paramDatagramPacket.setAddress(localInetSocketAddress.getAddress());
              dc.write(localByteBuffer);
            }
            else
            {
              dc.send(localByteBuffer, paramDatagramPacket.getSocketAddress());
            }
          }
          else {
            dc.send(localByteBuffer, paramDatagramPacket.getSocketAddress());
          }
        }
      }
      catch (IOException localIOException)
      {
        Net.translateException(localIOException);
      }
    }
  }
  
  /* Error */
  private SocketAddress receive(ByteBuffer paramByteBuffer)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 269	sun/nio/ch/DatagramSocketAdaptor:timeout	I
    //   4: ifne +12 -> 16
    //   7: aload_0
    //   8: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   11: aload_1
    //   12: invokevirtual 319	sun/nio/ch/DatagramChannelImpl:receive	(Ljava/nio/ByteBuffer;)Ljava/net/SocketAddress;
    //   15: areturn
    //   16: aload_0
    //   17: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   20: iconst_0
    //   21: invokevirtual 317	sun/nio/ch/DatagramChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   24: pop
    //   25: aload_0
    //   26: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   29: aload_1
    //   30: invokevirtual 319	sun/nio/ch/DatagramChannelImpl:receive	(Ljava/nio/ByteBuffer;)Ljava/net/SocketAddress;
    //   33: dup
    //   34: astore_3
    //   35: ifnull +28 -> 63
    //   38: aload_3
    //   39: astore 4
    //   41: aload_0
    //   42: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   45: invokevirtual 309	sun/nio/ch/DatagramChannelImpl:isOpen	()Z
    //   48: ifeq +12 -> 60
    //   51: aload_0
    //   52: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   55: iconst_1
    //   56: invokevirtual 317	sun/nio/ch/DatagramChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   59: pop
    //   60: aload 4
    //   62: areturn
    //   63: aload_0
    //   64: getfield 269	sun/nio/ch/DatagramSocketAdaptor:timeout	I
    //   67: i2l
    //   68: lstore 4
    //   70: aload_0
    //   71: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   74: invokevirtual 309	sun/nio/ch/DatagramChannelImpl:isOpen	()Z
    //   77: ifne +11 -> 88
    //   80: new 155	java/nio/channels/ClosedChannelException
    //   83: dup
    //   84: invokespecial 304	java/nio/channels/ClosedChannelException:<init>	()V
    //   87: athrow
    //   88: invokestatic 284	java/lang/System:currentTimeMillis	()J
    //   91: lstore 6
    //   93: aload_0
    //   94: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   97: getstatic 272	sun/nio/ch/Net:POLLIN	S
    //   100: lload 4
    //   102: invokevirtual 310	sun/nio/ch/DatagramChannelImpl:poll	(IJ)I
    //   105: istore 8
    //   107: iload 8
    //   109: ifle +50 -> 159
    //   112: iload 8
    //   114: getstatic 272	sun/nio/ch/Net:POLLIN	S
    //   117: iand
    //   118: ifeq +41 -> 159
    //   121: aload_0
    //   122: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   125: aload_1
    //   126: invokevirtual 319	sun/nio/ch/DatagramChannelImpl:receive	(Ljava/nio/ByteBuffer;)Ljava/net/SocketAddress;
    //   129: dup
    //   130: astore_3
    //   131: ifnull +28 -> 159
    //   134: aload_3
    //   135: astore 9
    //   137: aload_0
    //   138: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   141: invokevirtual 309	sun/nio/ch/DatagramChannelImpl:isOpen	()Z
    //   144: ifeq +12 -> 156
    //   147: aload_0
    //   148: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   151: iconst_1
    //   152: invokevirtual 317	sun/nio/ch/DatagramChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   155: pop
    //   156: aload 9
    //   158: areturn
    //   159: lload 4
    //   161: invokestatic 284	java/lang/System:currentTimeMillis	()J
    //   164: lload 6
    //   166: lsub
    //   167: lsub
    //   168: lstore 4
    //   170: lload 4
    //   172: lconst_0
    //   173: lcmp
    //   174: ifgt +11 -> 185
    //   177: new 152	java/net/SocketTimeoutException
    //   180: dup
    //   181: invokespecial 301	java/net/SocketTimeoutException:<init>	()V
    //   184: athrow
    //   185: goto -115 -> 70
    //   188: astore 10
    //   190: aload_0
    //   191: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   194: invokevirtual 309	sun/nio/ch/DatagramChannelImpl:isOpen	()Z
    //   197: ifeq +12 -> 209
    //   200: aload_0
    //   201: getfield 271	sun/nio/ch/DatagramSocketAdaptor:dc	Lsun/nio/ch/DatagramChannelImpl;
    //   204: iconst_1
    //   205: invokevirtual 317	sun/nio/ch/DatagramChannelImpl:configureBlocking	(Z)Ljava/nio/channels/SelectableChannel;
    //   208: pop
    //   209: aload 10
    //   211: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	212	0	this	DatagramSocketAdaptor
    //   0	212	1	paramByteBuffer	ByteBuffer
    //   34	101	3	localSocketAddress1	SocketAddress
    //   39	22	4	localSocketAddress2	SocketAddress
    //   68	103	4	l1	long
    //   91	74	6	l2	long
    //   105	13	8	i	int
    //   135	22	9	localSocketAddress3	SocketAddress
    //   188	22	10	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   25	41	188	finally
    //   63	137	188	finally
    //   159	190	188	finally
  }
  
  public void receive(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    synchronized (dc.blockingLock())
    {
      if (!dc.isBlocking()) {
        throw new IllegalBlockingModeException();
      }
      try
      {
        synchronized (paramDatagramPacket)
        {
          ByteBuffer localByteBuffer = ByteBuffer.wrap(paramDatagramPacket.getData(), paramDatagramPacket.getOffset(), paramDatagramPacket.getLength());
          SocketAddress localSocketAddress = receive(localByteBuffer);
          paramDatagramPacket.setSocketAddress(localSocketAddress);
          paramDatagramPacket.setLength(localByteBuffer.position() - paramDatagramPacket.getOffset());
        }
      }
      catch (IOException localIOException)
      {
        Net.translateException(localIOException);
      }
    }
  }
  
  public InetAddress getLocalAddress()
  {
    if (isClosed()) {
      return null;
    }
    Object localObject = dc.localAddress();
    if (localObject == null) {
      localObject = new InetSocketAddress(0);
    }
    InetAddress localInetAddress = ((InetSocketAddress)localObject).getAddress();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkConnect(localInetAddress.getHostAddress(), -1);
      }
      catch (SecurityException localSecurityException)
      {
        return new InetSocketAddress(0).getAddress();
      }
    }
    return localInetAddress;
  }
  
  public int getLocalPort()
  {
    if (isClosed()) {
      return -1;
    }
    try
    {
      SocketAddress localSocketAddress = dc.getLocalAddress();
      if (localSocketAddress != null) {
        return ((InetSocketAddress)localSocketAddress).getPort();
      }
    }
    catch (Exception localException) {}
    return 0;
  }
  
  public void setSoTimeout(int paramInt)
    throws SocketException
  {
    timeout = paramInt;
  }
  
  public int getSoTimeout()
    throws SocketException
  {
    return timeout;
  }
  
  private void setBooleanOption(SocketOption<Boolean> paramSocketOption, boolean paramBoolean)
    throws SocketException
  {
    try
    {
      dc.setOption(paramSocketOption, Boolean.valueOf(paramBoolean));
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
      dc.setOption(paramSocketOption, Integer.valueOf(paramInt));
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
      return ((Boolean)dc.getOption(paramSocketOption)).booleanValue();
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
      return ((Integer)dc.getOption(paramSocketOption)).intValue();
    }
    catch (IOException localIOException)
    {
      Net.translateToSocketException(localIOException);
    }
    return -1;
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
  
  public void setBroadcast(boolean paramBoolean)
    throws SocketException
  {
    setBooleanOption(StandardSocketOptions.SO_BROADCAST, paramBoolean);
  }
  
  public boolean getBroadcast()
    throws SocketException
  {
    return getBooleanOption(StandardSocketOptions.SO_BROADCAST);
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
  
  public void close()
  {
    try
    {
      dc.close();
    }
    catch (IOException localIOException)
    {
      throw new Error(localIOException);
    }
  }
  
  public boolean isClosed()
  {
    return !dc.isOpen();
  }
  
  public DatagramChannel getChannel()
  {
    return dc;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\DatagramSocketAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */