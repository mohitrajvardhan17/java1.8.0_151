package java.net;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class ServerSocket
  implements Closeable
{
  private boolean created = false;
  private boolean bound = false;
  private boolean closed = false;
  private Object closeLock = new Object();
  private SocketImpl impl;
  private boolean oldImpl = false;
  private static SocketImplFactory factory = null;
  
  ServerSocket(SocketImpl paramSocketImpl)
  {
    impl = paramSocketImpl;
    paramSocketImpl.setServerSocket(this);
  }
  
  public ServerSocket()
    throws IOException
  {
    setImpl();
  }
  
  public ServerSocket(int paramInt)
    throws IOException
  {
    this(paramInt, 50, null);
  }
  
  public ServerSocket(int paramInt1, int paramInt2)
    throws IOException
  {
    this(paramInt1, paramInt2, null);
  }
  
  public ServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress)
    throws IOException
  {
    setImpl();
    if ((paramInt1 < 0) || (paramInt1 > 65535)) {
      throw new IllegalArgumentException("Port value out of range: " + paramInt1);
    }
    if (paramInt2 < 1) {
      paramInt2 = 50;
    }
    try
    {
      bind(new InetSocketAddress(paramInetAddress, paramInt1), paramInt2);
    }
    catch (SecurityException localSecurityException)
    {
      close();
      throw localSecurityException;
    }
    catch (IOException localIOException)
    {
      close();
      throw localIOException;
    }
  }
  
  SocketImpl getImpl()
    throws SocketException
  {
    if (!created) {
      createImpl();
    }
    return impl;
  }
  
  private void checkOldImpl()
  {
    if (impl == null) {
      return;
    }
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws NoSuchMethodException
        {
          impl.getClass().getDeclaredMethod("connect", new Class[] { SocketAddress.class, Integer.TYPE });
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      oldImpl = true;
    }
  }
  
  private void setImpl()
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
      impl.setServerSocket(this);
    }
  }
  
  void createImpl()
    throws SocketException
  {
    if (impl == null) {
      setImpl();
    }
    try
    {
      impl.create(true);
      created = true;
    }
    catch (IOException localIOException)
    {
      throw new SocketException(localIOException.getMessage());
    }
  }
  
  public void bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    bind(paramSocketAddress, 50);
  }
  
  public void bind(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if ((!oldImpl) && (isBound())) {
      throw new SocketException("Already bound");
    }
    if (paramSocketAddress == null) {
      paramSocketAddress = new InetSocketAddress(0);
    }
    if (!(paramSocketAddress instanceof InetSocketAddress)) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (localInetSocketAddress.isUnresolved()) {
      throw new SocketException("Unresolved address");
    }
    if (paramInt < 1) {
      paramInt = 50;
    }
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkListen(localInetSocketAddress.getPort());
      }
      getImpl().bind(localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
      getImpl().listen(paramInt);
      bound = true;
    }
    catch (SecurityException localSecurityException)
    {
      bound = false;
      throw localSecurityException;
    }
    catch (IOException localIOException)
    {
      bound = false;
      throw localIOException;
    }
  }
  
  public InetAddress getInetAddress()
  {
    if (!isBound()) {
      return null;
    }
    try
    {
      InetAddress localInetAddress = getImpl().getInetAddress();
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkConnect(localInetAddress.getHostAddress(), -1);
      }
      return localInetAddress;
    }
    catch (SecurityException localSecurityException)
    {
      return InetAddress.getLoopbackAddress();
    }
    catch (SocketException localSocketException) {}
    return null;
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
  
  public SocketAddress getLocalSocketAddress()
  {
    if (!isBound()) {
      return null;
    }
    return new InetSocketAddress(getInetAddress(), getLocalPort());
  }
  
  public Socket accept()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (!isBound()) {
      throw new SocketException("Socket is not bound yet");
    }
    Socket localSocket = new Socket((SocketImpl)null);
    implAccept(localSocket);
    return localSocket;
  }
  
  protected final void implAccept(Socket paramSocket)
    throws IOException
  {
    SocketImpl localSocketImpl = null;
    try
    {
      if (impl == null) {
        paramSocket.setImpl();
      } else {
        impl.reset();
      }
      localSocketImpl = impl;
      impl = null;
      address = new InetAddress();
      fd = new FileDescriptor();
      getImpl().accept(localSocketImpl);
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkAccept(localSocketImpl.getInetAddress().getHostAddress(), localSocketImpl.getPort());
      }
    }
    catch (IOException localIOException)
    {
      if (localSocketImpl != null) {
        localSocketImpl.reset();
      }
      impl = localSocketImpl;
      throw localIOException;
    }
    catch (SecurityException localSecurityException)
    {
      if (localSocketImpl != null) {
        localSocketImpl.reset();
      }
      impl = localSocketImpl;
      throw localSecurityException;
    }
    impl = localSocketImpl;
    paramSocket.postAccept();
  }
  
  public void close()
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
  
  public ServerSocketChannel getChannel()
  {
    return null;
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
    //   1: getfield 237	java/net/ServerSocket:closeLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 234	java/net/ServerSocket:closed	Z
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
    //   0	19	0	this	ServerSocket
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public synchronized void setSoTimeout(int paramInt)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setOption(4102, new Integer(paramInt));
  }
  
  public synchronized int getSoTimeout()
    throws IOException
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
  
  public String toString()
  {
    if (!isBound()) {
      return "ServerSocket[unbound]";
    }
    InetAddress localInetAddress;
    if (System.getSecurityManager() != null) {
      localInetAddress = InetAddress.getLoopbackAddress();
    } else {
      localInetAddress = impl.getInetAddress();
    }
    return "ServerSocket[addr=" + localInetAddress + ",localport=" + impl.getLocalPort() + "]";
  }
  
  void setBound()
  {
    bound = true;
  }
  
  void setCreated()
  {
    created = true;
  }
  
  public static synchronized void setSocketFactory(SocketImplFactory paramSocketImplFactory)
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
  
  public synchronized void setReceiveBufferSize(int paramInt)
    throws SocketException
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("negative receive size");
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
  
  public void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\ServerSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */