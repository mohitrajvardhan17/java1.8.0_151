package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.net.ApplicationProxy;

public class Socket
  implements Closeable
{
  private boolean created = false;
  private boolean bound = false;
  private boolean connected = false;
  private boolean closed = false;
  private Object closeLock = new Object();
  private boolean shutIn = false;
  private boolean shutOut = false;
  SocketImpl impl;
  private boolean oldImpl = false;
  private static SocketImplFactory factory = null;
  
  public Socket()
  {
    setImpl();
  }
  
  public Socket(Proxy paramProxy)
  {
    if (paramProxy == null) {
      throw new IllegalArgumentException("Invalid Proxy");
    }
    ApplicationProxy localApplicationProxy = paramProxy == Proxy.NO_PROXY ? Proxy.NO_PROXY : ApplicationProxy.create(paramProxy);
    Proxy.Type localType = localApplicationProxy.type();
    if ((localType == Proxy.Type.SOCKS) || (localType == Proxy.Type.HTTP))
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      InetSocketAddress localInetSocketAddress = (InetSocketAddress)localApplicationProxy.address();
      if (localInetSocketAddress.getAddress() != null) {
        checkAddress(localInetSocketAddress.getAddress(), "Socket");
      }
      if (localSecurityManager != null)
      {
        if (localInetSocketAddress.isUnresolved()) {
          localInetSocketAddress = new InetSocketAddress(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
        }
        if (localInetSocketAddress.isUnresolved()) {
          localSecurityManager.checkConnect(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
        } else {
          localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
        }
      }
      impl = (localType == Proxy.Type.SOCKS ? new SocksSocketImpl(localApplicationProxy) : new HttpConnectSocketImpl(localApplicationProxy));
      impl.setSocket(this);
    }
    else if (localApplicationProxy == Proxy.NO_PROXY)
    {
      if (factory == null)
      {
        impl = new PlainSocketImpl();
        impl.setSocket(this);
      }
      else
      {
        setImpl();
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid Proxy");
    }
  }
  
  protected Socket(SocketImpl paramSocketImpl)
    throws SocketException
  {
    impl = paramSocketImpl;
    if (paramSocketImpl != null)
    {
      checkOldImpl();
      impl.setSocket(this);
    }
  }
  
  public Socket(String paramString, int paramInt)
    throws UnknownHostException, IOException
  {
    this(paramString != null ? new InetSocketAddress(paramString, paramInt) : new InetSocketAddress(InetAddress.getByName(null), paramInt), (SocketAddress)null, true);
  }
  
  public Socket(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    this(paramInetAddress != null ? new InetSocketAddress(paramInetAddress, paramInt) : null, (SocketAddress)null, true);
  }
  
  public Socket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws IOException
  {
    this(paramString != null ? new InetSocketAddress(paramString, paramInt1) : new InetSocketAddress(InetAddress.getByName(null), paramInt1), new InetSocketAddress(paramInetAddress, paramInt2), true);
  }
  
  public Socket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2)
    throws IOException
  {
    this(paramInetAddress1 != null ? new InetSocketAddress(paramInetAddress1, paramInt1) : null, new InetSocketAddress(paramInetAddress2, paramInt2), true);
  }
  
  @Deprecated
  public Socket(String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    this(paramString != null ? new InetSocketAddress(paramString, paramInt) : new InetSocketAddress(InetAddress.getByName(null), paramInt), (SocketAddress)null, paramBoolean);
  }
  
  @Deprecated
  public Socket(InetAddress paramInetAddress, int paramInt, boolean paramBoolean)
    throws IOException
  {
    this(paramInetAddress != null ? new InetSocketAddress(paramInetAddress, paramInt) : null, new InetSocketAddress(0), paramBoolean);
  }
  
  private Socket(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2, boolean paramBoolean)
    throws IOException
  {
    setImpl();
    if (paramSocketAddress1 == null) {
      throw new NullPointerException();
    }
    try
    {
      createImpl(paramBoolean);
      if (paramSocketAddress2 != null) {
        bind(paramSocketAddress2);
      }
      connect(paramSocketAddress1);
    }
    catch (IOException|IllegalArgumentException|SecurityException localIOException1)
    {
      try
      {
        close();
      }
      catch (IOException localIOException2)
      {
        localIOException1.addSuppressed(localIOException2);
      }
      throw localIOException1;
    }
  }
  
  void createImpl(boolean paramBoolean)
    throws SocketException
  {
    if (impl == null) {
      setImpl();
    }
    try
    {
      impl.create(paramBoolean);
      created = true;
    }
    catch (IOException localIOException)
    {
      throw new SocketException(localIOException.getMessage());
    }
  }
  
  private void checkOldImpl()
  {
    if (impl == null) {
      return;
    }
    oldImpl = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        Class localClass = impl.getClass();
        for (;;)
        {
          try
          {
            localClass.getDeclaredMethod("connect", new Class[] { SocketAddress.class, Integer.TYPE });
            return Boolean.FALSE;
          }
          catch (NoSuchMethodException localNoSuchMethodException)
          {
            localClass = localClass.getSuperclass();
            if (localClass.equals(SocketImpl.class)) {
              return Boolean.TRUE;
            }
          }
        }
      }
    })).booleanValue();
  }
  
  void setImpl()
  {
    if (factory != null)
    {
      impl = factory.createSocketImpl();
      checkOldImpl();
    }
    else
    {
      impl = new SocksSocketImpl();
    }
    if (impl != null) {
      impl.setSocket(this);
    }
  }
  
  SocketImpl getImpl()
    throws SocketException
  {
    if (!created) {
      createImpl(true);
    }
    return impl;
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
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if ((!oldImpl) && (isConnected())) {
      throw new SocketException("already connected");
    }
    if (!(paramSocketAddress instanceof InetSocketAddress)) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
    InetAddress localInetAddress = localInetSocketAddress.getAddress();
    int i = localInetSocketAddress.getPort();
    checkAddress(localInetAddress, "connect");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      if (localInetSocketAddress.isUnresolved()) {
        localSecurityManager.checkConnect(localInetSocketAddress.getHostName(), i);
      } else {
        localSecurityManager.checkConnect(localInetAddress.getHostAddress(), i);
      }
    }
    if (!created) {
      createImpl(true);
    }
    if (!oldImpl) {
      impl.connect(localInetSocketAddress, paramInt);
    } else if (paramInt == 0)
    {
      if (localInetSocketAddress.isUnresolved()) {
        impl.connect(localInetAddress.getHostName(), i);
      } else {
        impl.connect(localInetAddress, i);
      }
    }
    else {
      throw new UnsupportedOperationException("SocketImpl.connect(addr, timeout)");
    }
    connected = true;
    bound = true;
  }
  
  public void bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if ((!oldImpl) && (isBound())) {
      throw new SocketException("Already bound");
    }
    if ((paramSocketAddress != null) && (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if ((localInetSocketAddress != null) && (localInetSocketAddress.isUnresolved())) {
      throw new SocketException("Unresolved address");
    }
    if (localInetSocketAddress == null) {
      localInetSocketAddress = new InetSocketAddress(0);
    }
    InetAddress localInetAddress = localInetSocketAddress.getAddress();
    int i = localInetSocketAddress.getPort();
    checkAddress(localInetAddress, "bind");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkListen(i);
    }
    getImpl().bind(localInetAddress, i);
    bound = true;
  }
  
  private void checkAddress(InetAddress paramInetAddress, String paramString)
  {
    if (paramInetAddress == null) {
      return;
    }
    if ((!(paramInetAddress instanceof Inet4Address)) && (!(paramInetAddress instanceof Inet6Address))) {
      throw new IllegalArgumentException(paramString + ": invalid address type");
    }
  }
  
  final void postAccept()
  {
    connected = true;
    created = true;
    bound = true;
  }
  
  void setCreated()
  {
    created = true;
  }
  
  void setBound()
  {
    bound = true;
  }
  
  void setConnected()
  {
    connected = true;
  }
  
  public InetAddress getInetAddress()
  {
    if (!isConnected()) {
      return null;
    }
    try
    {
      return getImpl().getInetAddress();
    }
    catch (SocketException localSocketException) {}
    return null;
  }
  
  public InetAddress getLocalAddress()
  {
    if (!isBound()) {
      return InetAddress.anyLocalAddress();
    }
    InetAddress localInetAddress = null;
    try
    {
      localInetAddress = (InetAddress)getImpl().getOption(15);
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkConnect(localInetAddress.getHostAddress(), -1);
      }
      if (localInetAddress.isAnyLocalAddress()) {
        localInetAddress = InetAddress.anyLocalAddress();
      }
    }
    catch (SecurityException localSecurityException)
    {
      localInetAddress = InetAddress.getLoopbackAddress();
    }
    catch (Exception localException)
    {
      localInetAddress = InetAddress.anyLocalAddress();
    }
    return localInetAddress;
  }
  
  public int getPort()
  {
    if (!isConnected()) {
      return 0;
    }
    try
    {
      return getImpl().getPort();
    }
    catch (SocketException localSocketException) {}
    return -1;
  }
  
  public int getLocalPort()
  {
    if (!isBound()) {
      return -1;
    }
    try
    {
      return getImpl().getLocalPort();
    }
    catch (SocketException localSocketException) {}
    return -1;
  }
  
  public SocketAddress getRemoteSocketAddress()
  {
    if (!isConnected()) {
      return null;
    }
    return new InetSocketAddress(getInetAddress(), getPort());
  }
  
  public SocketAddress getLocalSocketAddress()
  {
    if (!isBound()) {
      return null;
    }
    return new InetSocketAddress(getLocalAddress(), getLocalPort());
  }
  
  public SocketChannel getChannel()
  {
    return null;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (!isConnected()) {
      throw new SocketException("Socket is not connected");
    }
    if (isInputShutdown()) {
      throw new SocketException("Socket input is shutdown");
    }
    Socket localSocket = this;
    InputStream localInputStream = null;
    try
    {
      localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public InputStream run()
          throws IOException
        {
          return impl.getInputStream();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
    return localInputStream;
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (!isConnected()) {
      throw new SocketException("Socket is not connected");
    }
    if (isOutputShutdown()) {
      throw new SocketException("Socket output is shutdown");
    }
    Socket localSocket = this;
    OutputStream localOutputStream = null;
    try
    {
      localOutputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public OutputStream run()
          throws IOException
        {
          return impl.getOutputStream();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
    return localOutputStream;
  }
  
  public void setTcpNoDelay(boolean paramBoolean)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setOption(1, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getTcpNoDelay()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return ((Boolean)getImpl().getOption(1)).booleanValue();
  }
  
  public void setSoLinger(boolean paramBoolean, int paramInt)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (!paramBoolean)
    {
      getImpl().setOption(128, new Boolean(paramBoolean));
    }
    else
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("invalid value for SO_LINGER");
      }
      if (paramInt > 65535) {
        paramInt = 65535;
      }
      getImpl().setOption(128, new Integer(paramInt));
    }
  }
  
  public int getSoLinger()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    Object localObject = getImpl().getOption(128);
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    return -1;
  }
  
  public void sendUrgentData(int paramInt)
    throws IOException
  {
    if (!getImpl().supportsUrgentData()) {
      throw new SocketException("Urgent data not supported");
    }
    getImpl().sendUrgentData(paramInt);
  }
  
  public void setOOBInline(boolean paramBoolean)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setOption(4099, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getOOBInline()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return ((Boolean)getImpl().getOption(4099)).booleanValue();
  }
  
  public synchronized void setSoTimeout(int paramInt)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("timeout can't be negative");
    }
    getImpl().setOption(4102, new Integer(paramInt));
  }
  
  public synchronized int getSoTimeout()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    Object localObject = getImpl().getOption(4102);
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    return 0;
  }
  
  public synchronized void setSendBufferSize(int paramInt)
    throws SocketException
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("negative send size");
    }
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setOption(4097, new Integer(paramInt));
  }
  
  public synchronized int getSendBufferSize()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    int i = 0;
    Object localObject = getImpl().getOption(4097);
    if ((localObject instanceof Integer)) {
      i = ((Integer)localObject).intValue();
    }
    return i;
  }
  
  public synchronized void setReceiveBufferSize(int paramInt)
    throws SocketException
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("invalid receive size");
    }
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setOption(4098, new Integer(paramInt));
  }
  
  public synchronized int getReceiveBufferSize()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    int i = 0;
    Object localObject = getImpl().getOption(4098);
    if ((localObject instanceof Integer)) {
      i = ((Integer)localObject).intValue();
    }
    return i;
  }
  
  public void setKeepAlive(boolean paramBoolean)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setOption(8, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getKeepAlive()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return ((Boolean)getImpl().getOption(8)).booleanValue();
  }
  
  public void setTrafficClass(int paramInt)
    throws SocketException
  {
    if ((paramInt < 0) || (paramInt > 255)) {
      throw new IllegalArgumentException("tc is not in range 0 -- 255");
    }
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    try
    {
      getImpl().setOption(3, Integer.valueOf(paramInt));
    }
    catch (SocketException localSocketException)
    {
      if (!isConnected()) {
        throw localSocketException;
      }
    }
  }
  
  public int getTrafficClass()
    throws SocketException
  {
    return ((Integer)getImpl().getOption(3)).intValue();
  }
  
  public void setReuseAddress(boolean paramBoolean)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setOption(4, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getReuseAddress()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return ((Boolean)getImpl().getOption(4)).booleanValue();
  }
  
  public synchronized void close()
    throws IOException
  {
    synchronized (closeLock)
    {
      if (isClosed()) {
        return;
      }
      if (created) {
        impl.close();
      }
      closed = true;
    }
  }
  
  public void shutdownInput()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (!isConnected()) {
      throw new SocketException("Socket is not connected");
    }
    if (isInputShutdown()) {
      throw new SocketException("Socket input is already shutdown");
    }
    getImpl().shutdownInput();
    shutIn = true;
  }
  
  public void shutdownOutput()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (!isConnected()) {
      throw new SocketException("Socket is not connected");
    }
    if (isOutputShutdown()) {
      throw new SocketException("Socket output is already shutdown");
    }
    getImpl().shutdownOutput();
    shutOut = true;
  }
  
  public String toString()
  {
    try
    {
      if (isConnected()) {
        return "Socket[addr=" + getImpl().getInetAddress() + ",port=" + getImpl().getPort() + ",localport=" + getImpl().getLocalPort() + "]";
      }
    }
    catch (SocketException localSocketException) {}
    return "Socket[unconnected]";
  }
  
  public boolean isConnected()
  {
    return (connected) || (oldImpl);
  }
  
  public boolean isBound()
  {
    return (bound) || (oldImpl);
  }
  
  /* Error */
  public boolean isClosed()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 377	java/net/Socket:closeLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 371	java/net/Socket:closed	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Socket
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public boolean isInputShutdown()
  {
    return shutIn;
  }
  
  public boolean isOutputShutdown()
  {
    return shutOut;
  }
  
  public static synchronized void setSocketImplFactory(SocketImplFactory paramSocketImplFactory)
    throws IOException
  {
    if (factory != null) {
      throw new SocketException("factory already defined");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    factory = paramSocketImplFactory;
  }
  
  public void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\Socket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */