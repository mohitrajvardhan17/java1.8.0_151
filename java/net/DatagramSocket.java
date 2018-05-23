package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class DatagramSocket
  implements Closeable
{
  private boolean created = false;
  private boolean bound = false;
  private boolean closed = false;
  private Object closeLock = new Object();
  DatagramSocketImpl impl;
  boolean oldImpl = false;
  private boolean explicitFilter = false;
  private int bytesLeftToFilter;
  static final int ST_NOT_CONNECTED = 0;
  static final int ST_CONNECTED = 1;
  static final int ST_CONNECTED_NO_IMPL = 2;
  int connectState = 0;
  InetAddress connectedAddress = null;
  int connectedPort = -1;
  static Class<?> implClass = null;
  static DatagramSocketImplFactory factory;
  
  private synchronized void connectInternal(InetAddress paramInetAddress, int paramInt)
    throws SocketException
  {
    if ((paramInt < 0) || (paramInt > 65535)) {
      throw new IllegalArgumentException("connect: " + paramInt);
    }
    if (paramInetAddress == null) {
      throw new IllegalArgumentException("connect: null address");
    }
    checkAddress(paramInetAddress, "connect");
    if (isClosed()) {
      return;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      if (paramInetAddress.isMulticastAddress())
      {
        localSecurityManager.checkMulticast(paramInetAddress);
      }
      else
      {
        localSecurityManager.checkConnect(paramInetAddress.getHostAddress(), paramInt);
        localSecurityManager.checkAccept(paramInetAddress.getHostAddress(), paramInt);
      }
    }
    if (!isBound()) {
      bind(new InetSocketAddress(0));
    }
    if ((oldImpl) || (((impl instanceof AbstractPlainDatagramSocketImpl)) && (((AbstractPlainDatagramSocketImpl)impl).nativeConnectDisabled()))) {
      connectState = 2;
    } else {
      try
      {
        getImpl().connect(paramInetAddress, paramInt);
        connectState = 1;
        int i = getImpl().dataAvailable();
        if (i == -1) {
          throw new SocketException();
        }
        explicitFilter = (i > 0);
        if (explicitFilter) {
          bytesLeftToFilter = getReceiveBufferSize();
        }
      }
      catch (SocketException localSocketException)
      {
        connectState = 2;
      }
    }
    connectedAddress = paramInetAddress;
    connectedPort = paramInt;
  }
  
  public DatagramSocket()
    throws SocketException
  {
    this(new InetSocketAddress(0));
  }
  
  protected DatagramSocket(DatagramSocketImpl paramDatagramSocketImpl)
  {
    if (paramDatagramSocketImpl == null) {
      throw new NullPointerException();
    }
    impl = paramDatagramSocketImpl;
    checkOldImpl();
  }
  
  /* Error */
  public DatagramSocket(SocketAddress paramSocketAddress)
    throws SocketException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 332	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: iconst_0
    //   6: putfield 316	java/net/DatagramSocket:created	Z
    //   9: aload_0
    //   10: iconst_0
    //   11: putfield 314	java/net/DatagramSocket:bound	Z
    //   14: aload_0
    //   15: iconst_0
    //   16: putfield 315	java/net/DatagramSocket:closed	Z
    //   19: aload_0
    //   20: new 180	java/lang/Object
    //   23: dup
    //   24: invokespecial 332	java/lang/Object:<init>	()V
    //   27: putfield 320	java/net/DatagramSocket:closeLock	Ljava/lang/Object;
    //   30: aload_0
    //   31: iconst_0
    //   32: putfield 318	java/net/DatagramSocket:oldImpl	Z
    //   35: aload_0
    //   36: iconst_0
    //   37: putfield 317	java/net/DatagramSocket:explicitFilter	Z
    //   40: aload_0
    //   41: iconst_0
    //   42: putfield 312	java/net/DatagramSocket:connectState	I
    //   45: aload_0
    //   46: aconst_null
    //   47: putfield 323	java/net/DatagramSocket:connectedAddress	Ljava/net/InetAddress;
    //   50: aload_0
    //   51: iconst_m1
    //   52: putfield 313	java/net/DatagramSocket:connectedPort	I
    //   55: aload_0
    //   56: invokevirtual 355	java/net/DatagramSocket:createImpl	()V
    //   59: aload_1
    //   60: ifnull +36 -> 96
    //   63: aload_0
    //   64: aload_1
    //   65: invokevirtual 366	java/net/DatagramSocket:bind	(Ljava/net/SocketAddress;)V
    //   68: aload_0
    //   69: invokevirtual 356	java/net/DatagramSocket:isBound	()Z
    //   72: ifne +24 -> 96
    //   75: aload_0
    //   76: invokevirtual 354	java/net/DatagramSocket:close	()V
    //   79: goto +17 -> 96
    //   82: astore_2
    //   83: aload_0
    //   84: invokevirtual 356	java/net/DatagramSocket:isBound	()Z
    //   87: ifne +7 -> 94
    //   90: aload_0
    //   91: invokevirtual 354	java/net/DatagramSocket:close	()V
    //   94: aload_2
    //   95: athrow
    //   96: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	DatagramSocket
    //   0	97	1	paramSocketAddress	SocketAddress
    //   82	13	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   63	68	82	finally
  }
  
  public DatagramSocket(int paramInt)
    throws SocketException
  {
    this(paramInt, null);
  }
  
  public DatagramSocket(int paramInt, InetAddress paramInetAddress)
    throws SocketException
  {
    this(new InetSocketAddress(paramInetAddress, paramInt));
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
          Class[] arrayOfClass = new Class[1];
          arrayOfClass[0] = DatagramPacket.class;
          impl.getClass().getDeclaredMethod("peekData", arrayOfClass);
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      oldImpl = true;
    }
  }
  
  void createImpl()
    throws SocketException
  {
    if (impl == null) {
      if (factory != null)
      {
        impl = factory.createDatagramSocketImpl();
        checkOldImpl();
      }
      else
      {
        boolean bool = (this instanceof MulticastSocket);
        impl = DefaultDatagramSocketImplFactory.createDatagramSocketImpl(bool);
        checkOldImpl();
      }
    }
    impl.create();
    impl.setDatagramSocket(this);
    created = true;
  }
  
  DatagramSocketImpl getImpl()
    throws SocketException
  {
    if (!created) {
      createImpl();
    }
    return impl;
  }
  
  public synchronized void bind(SocketAddress paramSocketAddress)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (isBound()) {
      throw new SocketException("already bound");
    }
    if (paramSocketAddress == null) {
      paramSocketAddress = new InetSocketAddress(0);
    }
    if (!(paramSocketAddress instanceof InetSocketAddress)) {
      throw new IllegalArgumentException("Unsupported address type!");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (localInetSocketAddress.isUnresolved()) {
      throw new SocketException("Unresolved address");
    }
    InetAddress localInetAddress = localInetSocketAddress.getAddress();
    int i = localInetSocketAddress.getPort();
    checkAddress(localInetAddress, "bind");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkListen(i);
    }
    try
    {
      getImpl().bind(i, localInetAddress);
    }
    catch (SocketException localSocketException)
    {
      getImpl().close();
      throw localSocketException;
    }
    bound = true;
  }
  
  void checkAddress(InetAddress paramInetAddress, String paramString)
  {
    if (paramInetAddress == null) {
      return;
    }
    if ((!(paramInetAddress instanceof Inet4Address)) && (!(paramInetAddress instanceof Inet6Address))) {
      throw new IllegalArgumentException(paramString + ": invalid address type");
    }
  }
  
  public void connect(InetAddress paramInetAddress, int paramInt)
  {
    try
    {
      connectInternal(paramInetAddress, paramInt);
    }
    catch (SocketException localSocketException)
    {
      throw new Error("connect failed", localSocketException);
    }
  }
  
  public void connect(SocketAddress paramSocketAddress)
    throws SocketException
  {
    if (paramSocketAddress == null) {
      throw new IllegalArgumentException("Address can't be null");
    }
    if (!(paramSocketAddress instanceof InetSocketAddress)) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (localInetSocketAddress.isUnresolved()) {
      throw new SocketException("Unresolved address");
    }
    connectInternal(localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
  }
  
  public void disconnect()
  {
    synchronized (this)
    {
      if (isClosed()) {
        return;
      }
      if (connectState == 1) {
        impl.disconnect();
      }
      connectedAddress = null;
      connectedPort = -1;
      connectState = 0;
      explicitFilter = false;
    }
  }
  
  public boolean isBound()
  {
    return bound;
  }
  
  public boolean isConnected()
  {
    return connectState != 0;
  }
  
  public InetAddress getInetAddress()
  {
    return connectedAddress;
  }
  
  public int getPort()
  {
    return connectedPort;
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
    if (isClosed()) {
      return null;
    }
    if (!isBound()) {
      return null;
    }
    return new InetSocketAddress(getLocalAddress(), getLocalPort());
  }
  
  public void send(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    InetAddress localInetAddress = null;
    synchronized (paramDatagramPacket)
    {
      if (isClosed()) {
        throw new SocketException("Socket is closed");
      }
      checkAddress(paramDatagramPacket.getAddress(), "send");
      if (connectState == 0)
      {
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
          if (paramDatagramPacket.getAddress().isMulticastAddress()) {
            localSecurityManager.checkMulticast(paramDatagramPacket.getAddress());
          } else {
            localSecurityManager.checkConnect(paramDatagramPacket.getAddress().getHostAddress(), paramDatagramPacket.getPort());
          }
        }
      }
      else
      {
        localInetAddress = paramDatagramPacket.getAddress();
        if (localInetAddress == null)
        {
          paramDatagramPacket.setAddress(connectedAddress);
          paramDatagramPacket.setPort(connectedPort);
        }
        else if ((!localInetAddress.equals(connectedAddress)) || (paramDatagramPacket.getPort() != connectedPort))
        {
          throw new IllegalArgumentException("connected address and packet address differ");
        }
      }
      if (!isBound()) {
        bind(new InetSocketAddress(0));
      }
      getImpl().send(paramDatagramPacket);
    }
  }
  
  public synchronized void receive(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    synchronized (paramDatagramPacket)
    {
      if (!isBound()) {
        bind(new InetSocketAddress(0));
      }
      DatagramPacket localDatagramPacket;
      if (connectState == 0)
      {
        localObject1 = System.getSecurityManager();
        if (localObject1 != null) {
          for (;;)
          {
            String str = null;
            int j = 0;
            Object localObject2;
            if (!oldImpl)
            {
              localObject2 = new DatagramPacket(new byte[1], 1);
              j = getImpl().peekData((DatagramPacket)localObject2);
              str = ((DatagramPacket)localObject2).getAddress().getHostAddress();
            }
            else
            {
              localObject2 = new InetAddress();
              j = getImpl().peek((InetAddress)localObject2);
              str = ((InetAddress)localObject2).getHostAddress();
            }
            try
            {
              ((SecurityManager)localObject1).checkAccept(str, j);
            }
            catch (SecurityException localSecurityException)
            {
              localDatagramPacket = new DatagramPacket(new byte[1], 1);
              getImpl().receive(localDatagramPacket);
            }
          }
        }
      }
      Object localObject1 = null;
      if ((connectState == 2) || (explicitFilter))
      {
        int i = 0;
        while (i == 0)
        {
          InetAddress localInetAddress = null;
          int k = -1;
          if (!oldImpl)
          {
            localDatagramPacket = new DatagramPacket(new byte[1], 1);
            k = getImpl().peekData(localDatagramPacket);
            localInetAddress = localDatagramPacket.getAddress();
          }
          else
          {
            localInetAddress = new InetAddress();
            k = getImpl().peek(localInetAddress);
          }
          if ((!connectedAddress.equals(localInetAddress)) || (connectedPort != k))
          {
            localObject1 = new DatagramPacket(new byte['Ѐ'], 1024);
            getImpl().receive((DatagramPacket)localObject1);
            if ((explicitFilter) && (checkFiltering((DatagramPacket)localObject1))) {
              i = 1;
            }
          }
          else
          {
            i = 1;
          }
        }
      }
      getImpl().receive(paramDatagramPacket);
      if ((explicitFilter) && (localObject1 == null)) {
        checkFiltering(paramDatagramPacket);
      }
    }
  }
  
  private boolean checkFiltering(DatagramPacket paramDatagramPacket)
    throws SocketException
  {
    bytesLeftToFilter -= paramDatagramPacket.getLength();
    if ((bytesLeftToFilter <= 0) || (getImpl().dataAvailable() <= 0))
    {
      explicitFilter = false;
      return true;
    }
    return false;
  }
  
  public InetAddress getLocalAddress()
  {
    if (isClosed()) {
      return null;
    }
    InetAddress localInetAddress = null;
    try
    {
      localInetAddress = (InetAddress)getImpl().getOption(15);
      if (localInetAddress.isAnyLocalAddress()) {
        localInetAddress = InetAddress.anyLocalAddress();
      }
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkConnect(localInetAddress.getHostAddress(), -1);
      }
    }
    catch (Exception localException)
    {
      localInetAddress = InetAddress.anyLocalAddress();
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
      return getImpl().getLocalPort();
    }
    catch (Exception localException) {}
    return 0;
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
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (getImpl() == null) {
      return 0;
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
  
  public synchronized void setReuseAddress(boolean paramBoolean)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if (oldImpl) {
      getImpl().setOption(4, new Integer(paramBoolean ? -1 : 0));
    } else {
      getImpl().setOption(4, Boolean.valueOf(paramBoolean));
    }
  }
  
  public synchronized boolean getReuseAddress()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    Object localObject = getImpl().getOption(4);
    return ((Boolean)localObject).booleanValue();
  }
  
  public synchronized void setBroadcast(boolean paramBoolean)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setOption(32, Boolean.valueOf(paramBoolean));
  }
  
  public synchronized boolean getBroadcast()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return ((Boolean)getImpl().getOption(32)).booleanValue();
  }
  
  public synchronized void setTrafficClass(int paramInt)
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
  
  public synchronized int getTrafficClass()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return ((Integer)getImpl().getOption(3)).intValue();
  }
  
  public void close()
  {
    synchronized (closeLock)
    {
      if (isClosed()) {
        return;
      }
      impl.close();
      closed = true;
    }
  }
  
  /* Error */
  public boolean isClosed()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 320	java/net/DatagramSocket:closeLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 315	java/net/DatagramSocket:closed	Z
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
    //   0	19	0	this	DatagramSocket
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public DatagramChannel getChannel()
  {
    return null;
  }
  
  public static synchronized void setDatagramSocketImplFactory(DatagramSocketImplFactory paramDatagramSocketImplFactory)
    throws IOException
  {
    if (factory != null) {
      throw new SocketException("factory already defined");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    factory = paramDatagramSocketImplFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\DatagramSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */