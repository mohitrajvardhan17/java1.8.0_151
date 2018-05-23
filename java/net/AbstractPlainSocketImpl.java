package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ConnectionResetException;
import sun.net.NetHooks;
import sun.net.ResourceManager;

abstract class AbstractPlainSocketImpl
  extends SocketImpl
{
  int timeout;
  private int trafficClass;
  private boolean shut_rd = false;
  private boolean shut_wr = false;
  private SocketInputStream socketInputStream = null;
  private SocketOutputStream socketOutputStream = null;
  protected int fdUseCount = 0;
  protected final Object fdLock = new Object();
  protected boolean closePending = false;
  private int CONNECTION_NOT_RESET = 0;
  private int CONNECTION_RESET_PENDING = 1;
  private int CONNECTION_RESET = 2;
  private int resetState;
  private final Object resetLock = new Object();
  protected boolean stream;
  public static final int SHUT_RD = 0;
  public static final int SHUT_WR = 1;
  
  AbstractPlainSocketImpl() {}
  
  protected synchronized void create(boolean paramBoolean)
    throws IOException
  {
    stream = paramBoolean;
    if (!paramBoolean)
    {
      ResourceManager.beforeUdpCreate();
      fd = new FileDescriptor();
      try
      {
        socketCreate(false);
      }
      catch (IOException localIOException)
      {
        ResourceManager.afterUdpClose();
        fd = null;
        throw localIOException;
      }
    }
    else
    {
      fd = new FileDescriptor();
      socketCreate(true);
    }
    if (socket != null) {
      socket.setCreated();
    }
    if (serverSocket != null) {
      serverSocket.setCreated();
    }
  }
  
  protected void connect(String paramString, int paramInt)
    throws UnknownHostException, IOException
  {
    int i = 0;
    try
    {
      InetAddress localInetAddress = InetAddress.getByName(paramString);
      port = paramInt;
      address = localInetAddress;
      connectToAddress(localInetAddress, paramInt, timeout);
      i = 1;
      return;
    }
    finally
    {
      if (i == 0) {
        try
        {
          close();
        }
        catch (IOException localIOException2) {}
      }
    }
  }
  
  protected void connect(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    port = paramInt;
    address = paramInetAddress;
    try
    {
      connectToAddress(paramInetAddress, paramInt, timeout);
      return;
    }
    catch (IOException localIOException)
    {
      close();
      throw localIOException;
    }
  }
  
  protected void connect(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    int i = 0;
    try
    {
      if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
        throw new IllegalArgumentException("unsupported address type");
      }
      InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
      if (localInetSocketAddress.isUnresolved()) {
        throw new UnknownHostException(localInetSocketAddress.getHostName());
      }
      port = localInetSocketAddress.getPort();
      address = localInetSocketAddress.getAddress();
      connectToAddress(address, port, paramInt);
      i = 1;
      return;
    }
    finally
    {
      if (i == 0) {
        try
        {
          close();
        }
        catch (IOException localIOException2) {}
      }
    }
  }
  
  private void connectToAddress(InetAddress paramInetAddress, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInetAddress.isAnyLocalAddress()) {
      doConnect(InetAddress.getLocalHost(), paramInt1, paramInt2);
    } else {
      doConnect(paramInetAddress, paramInt1, paramInt2);
    }
  }
  
  public void setOption(int paramInt, Object paramObject)
    throws SocketException
  {
    if (isClosedOrPending()) {
      throw new SocketException("Socket Closed");
    }
    boolean bool = true;
    switch (paramInt)
    {
    case 128: 
      if ((paramObject == null) || ((!(paramObject instanceof Integer)) && (!(paramObject instanceof Boolean)))) {
        throw new SocketException("Bad parameter for option");
      }
      if ((paramObject instanceof Boolean)) {
        bool = false;
      }
      break;
    case 4102: 
      if ((paramObject == null) || (!(paramObject instanceof Integer))) {
        throw new SocketException("Bad parameter for SO_TIMEOUT");
      }
      int i = ((Integer)paramObject).intValue();
      if (i < 0) {
        throw new IllegalArgumentException("timeout < 0");
      }
      timeout = i;
      break;
    case 3: 
      if ((paramObject == null) || (!(paramObject instanceof Integer))) {
        throw new SocketException("bad argument for IP_TOS");
      }
      trafficClass = ((Integer)paramObject).intValue();
      break;
    case 15: 
      throw new SocketException("Cannot re-bind socket");
    case 1: 
      if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
        throw new SocketException("bad parameter for TCP_NODELAY");
      }
      bool = ((Boolean)paramObject).booleanValue();
      break;
    case 4097: 
    case 4098: 
      if ((paramObject == null) || (!(paramObject instanceof Integer)) || (((Integer)paramObject).intValue() <= 0)) {
        throw new SocketException("bad parameter for SO_SNDBUF or SO_RCVBUF");
      }
      break;
    case 8: 
      if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
        throw new SocketException("bad parameter for SO_KEEPALIVE");
      }
      bool = ((Boolean)paramObject).booleanValue();
      break;
    case 4099: 
      if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
        throw new SocketException("bad parameter for SO_OOBINLINE");
      }
      bool = ((Boolean)paramObject).booleanValue();
      break;
    case 4: 
      if ((paramObject == null) || (!(paramObject instanceof Boolean))) {
        throw new SocketException("bad parameter for SO_REUSEADDR");
      }
      bool = ((Boolean)paramObject).booleanValue();
      break;
    default: 
      throw new SocketException("unrecognized TCP option: " + paramInt);
    }
    socketSetOption(paramInt, bool, paramObject);
  }
  
  public Object getOption(int paramInt)
    throws SocketException
  {
    if (isClosedOrPending()) {
      throw new SocketException("Socket Closed");
    }
    if (paramInt == 4102) {
      return new Integer(timeout);
    }
    int i = 0;
    switch (paramInt)
    {
    case 1: 
      i = socketGetOption(paramInt, null);
      return Boolean.valueOf(i != -1);
    case 4099: 
      i = socketGetOption(paramInt, null);
      return Boolean.valueOf(i != -1);
    case 128: 
      i = socketGetOption(paramInt, null);
      return i == -1 ? Boolean.FALSE : new Integer(i);
    case 4: 
      i = socketGetOption(paramInt, null);
      return Boolean.valueOf(i != -1);
    case 15: 
      InetAddressContainer localInetAddressContainer = new InetAddressContainer();
      i = socketGetOption(paramInt, localInetAddressContainer);
      return addr;
    case 4097: 
    case 4098: 
      i = socketGetOption(paramInt, null);
      return new Integer(i);
    case 3: 
      try
      {
        i = socketGetOption(paramInt, null);
        if (i == -1) {
          return Integer.valueOf(trafficClass);
        }
        return Integer.valueOf(i);
      }
      catch (SocketException localSocketException)
      {
        return Integer.valueOf(trafficClass);
      }
    case 8: 
      i = socketGetOption(paramInt, null);
      return Boolean.valueOf(i != -1);
    }
    return null;
  }
  
  synchronized void doConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (fdLock)
    {
      if ((!closePending) && ((socket == null) || (!socket.isBound()))) {
        NetHooks.beforeTcpConnect(fd, paramInetAddress, paramInt1);
      }
    }
    try
    {
      acquireFD();
      try
      {
        socketConnect(paramInetAddress, paramInt1, paramInt2);
        synchronized (fdLock)
        {
          if (closePending) {
            throw new SocketException("Socket closed");
          }
        }
        if (socket != null)
        {
          socket.setBound();
          socket.setConnected();
        }
      }
      finally
      {
        releaseFD();
      }
    }
    catch (IOException localIOException)
    {
      close();
      throw localIOException;
    }
  }
  
  protected synchronized void bind(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    synchronized (fdLock)
    {
      if ((!closePending) && ((socket == null) || (!socket.isBound()))) {
        NetHooks.beforeTcpBind(fd, paramInetAddress, paramInt);
      }
    }
    socketBind(paramInetAddress, paramInt);
    if (socket != null) {
      socket.setBound();
    }
    if (serverSocket != null) {
      serverSocket.setBound();
    }
  }
  
  protected synchronized void listen(int paramInt)
    throws IOException
  {
    socketListen(paramInt);
  }
  
  /* Error */
  protected void accept(SocketImpl paramSocketImpl)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 355	java/net/AbstractPlainSocketImpl:acquireFD	()Ljava/io/FileDescriptor;
    //   4: pop
    //   5: aload_0
    //   6: aload_1
    //   7: invokevirtual 362	java/net/AbstractPlainSocketImpl:socketAccept	(Ljava/net/SocketImpl;)V
    //   10: aload_0
    //   11: invokevirtual 342	java/net/AbstractPlainSocketImpl:releaseFD	()V
    //   14: goto +10 -> 24
    //   17: astore_2
    //   18: aload_0
    //   19: invokevirtual 342	java/net/AbstractPlainSocketImpl:releaseFD	()V
    //   22: aload_2
    //   23: athrow
    //   24: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	AbstractPlainSocketImpl
    //   0	25	1	paramSocketImpl	SocketImpl
    //   17	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	10	17	finally
  }
  
  protected synchronized InputStream getInputStream()
    throws IOException
  {
    synchronized (fdLock)
    {
      if (isClosedOrPending()) {
        throw new IOException("Socket Closed");
      }
      if (shut_rd) {
        throw new IOException("Socket input is shutdown");
      }
      if (socketInputStream == null) {
        socketInputStream = new SocketInputStream(this);
      }
    }
    return socketInputStream;
  }
  
  void setInputStream(SocketInputStream paramSocketInputStream)
  {
    socketInputStream = paramSocketInputStream;
  }
  
  protected synchronized OutputStream getOutputStream()
    throws IOException
  {
    synchronized (fdLock)
    {
      if (isClosedOrPending()) {
        throw new IOException("Socket Closed");
      }
      if (shut_wr) {
        throw new IOException("Socket output is shutdown");
      }
      if (socketOutputStream == null) {
        socketOutputStream = new SocketOutputStream(this);
      }
    }
    return socketOutputStream;
  }
  
  void setFileDescriptor(FileDescriptor paramFileDescriptor)
  {
    fd = paramFileDescriptor;
  }
  
  void setAddress(InetAddress paramInetAddress)
  {
    address = paramInetAddress;
  }
  
  void setPort(int paramInt)
  {
    port = paramInt;
  }
  
  void setLocalPort(int paramInt)
  {
    localport = paramInt;
  }
  
  protected synchronized int available()
    throws IOException
  {
    if (isClosedOrPending()) {
      throw new IOException("Stream closed.");
    }
    if ((isConnectionReset()) || (shut_rd)) {
      return 0;
    }
    int i = 0;
    try
    {
      i = socketAvailable();
      if ((i == 0) && (isConnectionResetPending())) {
        setConnectionReset();
      }
    }
    catch (ConnectionResetException localConnectionResetException1)
    {
      setConnectionResetPending();
      try
      {
        i = socketAvailable();
        if (i == 0) {
          setConnectionReset();
        }
      }
      catch (ConnectionResetException localConnectionResetException2) {}
    }
    return i;
  }
  
  /* Error */
  protected void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 319	java/net/AbstractPlainSocketImpl:fdLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 318	java/net/AbstractPlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   11: ifnull +87 -> 98
    //   14: aload_0
    //   15: getfield 317	java/net/AbstractPlainSocketImpl:stream	Z
    //   18: ifne +6 -> 24
    //   21: invokestatic 388	sun/net/ResourceManager:afterUdpClose	()V
    //   24: aload_0
    //   25: getfield 308	java/net/AbstractPlainSocketImpl:fdUseCount	I
    //   28: ifne +44 -> 72
    //   31: aload_0
    //   32: getfield 314	java/net/AbstractPlainSocketImpl:closePending	Z
    //   35: ifeq +6 -> 41
    //   38: aload_1
    //   39: monitorexit
    //   40: return
    //   41: aload_0
    //   42: iconst_1
    //   43: putfield 314	java/net/AbstractPlainSocketImpl:closePending	Z
    //   46: aload_0
    //   47: invokespecial 346	java/net/AbstractPlainSocketImpl:socketPreClose	()V
    //   50: aload_0
    //   51: invokevirtual 345	java/net/AbstractPlainSocketImpl:socketClose	()V
    //   54: goto +10 -> 64
    //   57: astore_2
    //   58: aload_0
    //   59: invokevirtual 345	java/net/AbstractPlainSocketImpl:socketClose	()V
    //   62: aload_2
    //   63: athrow
    //   64: aload_0
    //   65: aconst_null
    //   66: putfield 318	java/net/AbstractPlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   69: aload_1
    //   70: monitorexit
    //   71: return
    //   72: aload_0
    //   73: getfield 314	java/net/AbstractPlainSocketImpl:closePending	Z
    //   76: ifne +22 -> 98
    //   79: aload_0
    //   80: iconst_1
    //   81: putfield 314	java/net/AbstractPlainSocketImpl:closePending	Z
    //   84: aload_0
    //   85: dup
    //   86: getfield 308	java/net/AbstractPlainSocketImpl:fdUseCount	I
    //   89: iconst_1
    //   90: isub
    //   91: putfield 308	java/net/AbstractPlainSocketImpl:fdUseCount	I
    //   94: aload_0
    //   95: invokespecial 346	java/net/AbstractPlainSocketImpl:socketPreClose	()V
    //   98: aload_1
    //   99: monitorexit
    //   100: goto +8 -> 108
    //   103: astore_3
    //   104: aload_1
    //   105: monitorexit
    //   106: aload_3
    //   107: athrow
    //   108: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	109	0	this	AbstractPlainSocketImpl
    //   5	100	1	Ljava/lang/Object;	Object
    //   57	6	2	localObject1	Object
    //   103	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   46	50	57	finally
    //   7	40	103	finally
    //   41	71	103	finally
    //   72	100	103	finally
    //   103	106	103	finally
  }
  
  void reset()
    throws IOException
  {
    if (fd != null) {
      socketClose();
    }
    fd = null;
    super.reset();
  }
  
  protected void shutdownInput()
    throws IOException
  {
    if (fd != null)
    {
      socketShutdown(0);
      if (socketInputStream != null) {
        socketInputStream.setEOF(true);
      }
      shut_rd = true;
    }
  }
  
  protected void shutdownOutput()
    throws IOException
  {
    if (fd != null)
    {
      socketShutdown(1);
      shut_wr = true;
    }
  }
  
  protected boolean supportsUrgentData()
  {
    return true;
  }
  
  protected void sendUrgentData(int paramInt)
    throws IOException
  {
    if (fd == null) {
      throw new IOException("Socket Closed");
    }
    socketSendUrgentData(paramInt);
  }
  
  protected void finalize()
    throws IOException
  {
    close();
  }
  
  FileDescriptor acquireFD()
  {
    synchronized (fdLock)
    {
      fdUseCount += 1;
      return fd;
    }
  }
  
  /* Error */
  void releaseFD()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 319	java/net/AbstractPlainSocketImpl:fdLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: dup
    //   9: getfield 308	java/net/AbstractPlainSocketImpl:fdUseCount	I
    //   12: iconst_1
    //   13: isub
    //   14: putfield 308	java/net/AbstractPlainSocketImpl:fdUseCount	I
    //   17: aload_0
    //   18: getfield 308	java/net/AbstractPlainSocketImpl:fdUseCount	I
    //   21: iconst_m1
    //   22: if_icmpne +39 -> 61
    //   25: aload_0
    //   26: getfield 318	java/net/AbstractPlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   29: ifnull +32 -> 61
    //   32: aload_0
    //   33: invokevirtual 345	java/net/AbstractPlainSocketImpl:socketClose	()V
    //   36: aload_0
    //   37: aconst_null
    //   38: putfield 318	java/net/AbstractPlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   41: goto +20 -> 61
    //   44: astore_2
    //   45: aload_0
    //   46: aconst_null
    //   47: putfield 318	java/net/AbstractPlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   50: goto +11 -> 61
    //   53: astore_3
    //   54: aload_0
    //   55: aconst_null
    //   56: putfield 318	java/net/AbstractPlainSocketImpl:fd	Ljava/io/FileDescriptor;
    //   59: aload_3
    //   60: athrow
    //   61: aload_1
    //   62: monitorexit
    //   63: goto +10 -> 73
    //   66: astore 4
    //   68: aload_1
    //   69: monitorexit
    //   70: aload 4
    //   72: athrow
    //   73: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	AbstractPlainSocketImpl
    //   5	64	1	Ljava/lang/Object;	Object
    //   44	1	2	localIOException	IOException
    //   53	7	3	localObject1	Object
    //   66	5	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   32	36	44	java/io/IOException
    //   32	36	53	finally
    //   7	63	66	finally
    //   66	70	66	finally
  }
  
  public boolean isConnectionReset()
  {
    synchronized (resetLock)
    {
      return resetState == CONNECTION_RESET;
    }
  }
  
  public boolean isConnectionResetPending()
  {
    synchronized (resetLock)
    {
      return resetState == CONNECTION_RESET_PENDING;
    }
  }
  
  public void setConnectionReset()
  {
    synchronized (resetLock)
    {
      resetState = CONNECTION_RESET;
    }
  }
  
  public void setConnectionResetPending()
  {
    synchronized (resetLock)
    {
      if (resetState == CONNECTION_NOT_RESET) {
        resetState = CONNECTION_RESET_PENDING;
      }
    }
  }
  
  public boolean isClosedOrPending()
  {
    synchronized (fdLock)
    {
      return (closePending) || (fd == null);
    }
  }
  
  public int getTimeout()
  {
    return timeout;
  }
  
  private void socketPreClose()
    throws IOException
  {
    socketClose0(true);
  }
  
  protected void socketClose()
    throws IOException
  {
    socketClose0(false);
  }
  
  abstract void socketCreate(boolean paramBoolean)
    throws IOException;
  
  abstract void socketConnect(InetAddress paramInetAddress, int paramInt1, int paramInt2)
    throws IOException;
  
  abstract void socketBind(InetAddress paramInetAddress, int paramInt)
    throws IOException;
  
  abstract void socketListen(int paramInt)
    throws IOException;
  
  abstract void socketAccept(SocketImpl paramSocketImpl)
    throws IOException;
  
  abstract int socketAvailable()
    throws IOException;
  
  abstract void socketClose0(boolean paramBoolean)
    throws IOException;
  
  abstract void socketShutdown(int paramInt)
    throws IOException;
  
  abstract void socketSetOption(int paramInt, boolean paramBoolean, Object paramObject)
    throws SocketException;
  
  abstract int socketGetOption(int paramInt, Object paramObject)
    throws SocketException;
  
  abstract void socketSendUrgentData(int paramInt)
    throws IOException;
  
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\AbstractPlainSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */