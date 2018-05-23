package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import sun.net.NetHooks;

class ServerSocketChannelImpl
  extends ServerSocketChannel
  implements SelChImpl
{
  private static NativeDispatcher nd = new SocketDispatcher();
  private final FileDescriptor fd;
  private int fdVal;
  private volatile long thread = 0L;
  private final Object lock = new Object();
  private final Object stateLock = new Object();
  private static final int ST_UNINITIALIZED = -1;
  private static final int ST_INUSE = 0;
  private static final int ST_KILLED = 1;
  private int state = -1;
  private InetSocketAddress localAddress;
  private boolean isReuseAddress;
  ServerSocket socket;
  
  ServerSocketChannelImpl(SelectorProvider paramSelectorProvider)
    throws IOException
  {
    super(paramSelectorProvider);
    fd = Net.serverSocket(true);
    fdVal = IOUtil.fdVal(fd);
    state = 0;
  }
  
  ServerSocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean)
    throws IOException
  {
    super(paramSelectorProvider);
    fd = paramFileDescriptor;
    fdVal = IOUtil.fdVal(paramFileDescriptor);
    state = 0;
    if (paramBoolean) {
      localAddress = Net.localAddress(paramFileDescriptor);
    }
  }
  
  public ServerSocket socket()
  {
    synchronized (stateLock)
    {
      if (socket == null) {
        socket = ServerSocketAdaptor.create(this);
      }
      return socket;
    }
  }
  
  public SocketAddress getLocalAddress()
    throws IOException
  {
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      return localAddress == null ? localAddress : Net.getRevealedLocalAddress(Net.asInetSocketAddress(localAddress));
    }
  }
  
  public <T> ServerSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    if (paramSocketOption == null) {
      throw new NullPointerException();
    }
    if (!supportedOptions().contains(paramSocketOption)) {
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
    }
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (paramSocketOption == StandardSocketOptions.IP_TOS)
      {
        StandardProtocolFamily localStandardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
        Net.setSocketOption(fd, localStandardProtocolFamily, paramSocketOption, paramT);
        return this;
      }
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind())) {
        isReuseAddress = ((Boolean)paramT).booleanValue();
      } else {
        Net.setSocketOption(fd, Net.UNSPEC, paramSocketOption, paramT);
      }
      return this;
    }
  }
  
  public <T> T getOption(SocketOption<T> paramSocketOption)
    throws IOException
  {
    if (paramSocketOption == null) {
      throw new NullPointerException();
    }
    if (!supportedOptions().contains(paramSocketOption)) {
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
    }
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind())) {
        return Boolean.valueOf(isReuseAddress);
      }
      return (T)Net.getSocketOption(fd, Net.UNSPEC, paramSocketOption);
    }
  }
  
  public final Set<SocketOption<?>> supportedOptions()
  {
    return DefaultOptionsHolder.defaultOptions;
  }
  
  public boolean isBound()
  {
    synchronized (stateLock)
    {
      return localAddress != null;
    }
  }
  
  /* Error */
  public InetSocketAddress localAddress()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 372	sun/nio/ch/ServerSocketChannelImpl:stateLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 373	sun/nio/ch/ServerSocketChannelImpl:localAddress	Ljava/net/InetSocketAddress;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	ServerSocketChannelImpl
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public ServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    synchronized (lock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (isBound()) {
        throw new AlreadyBoundException();
      }
      InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkListen(localInetSocketAddress.getPort());
      }
      NetHooks.beforeTcpBind(fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
      Net.bind(fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
      Net.listen(fd, paramInt < 1 ? 50 : paramInt);
      synchronized (stateLock)
      {
        localAddress = Net.localAddress(fd);
      }
    }
    return this;
  }
  
  public SocketChannel accept()
    throws IOException
  {
    synchronized (lock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!isBound()) {
        throw new NotYetBoundException();
      }
      SocketChannelImpl localSocketChannelImpl = null;
      int i = 0;
      FileDescriptor localFileDescriptor = new FileDescriptor();
      InetSocketAddress[] arrayOfInetSocketAddress = new InetSocketAddress[1];
      try
      {
        begin();
        if (!isOpen())
        {
          localObject1 = null;
          thread = 0L;
          end(i > 0);
          assert (IOStatus.check(i));
          return (SocketChannel)localObject1;
        }
        thread = NativeThread.current();
        for (;;)
        {
          i = accept(fd, localFileDescriptor, arrayOfInetSocketAddress);
          if ((i != -3) || (!isOpen())) {
            break;
          }
        }
      }
      finally
      {
        thread = 0L;
        end(i > 0);
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
      if (i < 1) {
        return null;
      }
      IOUtil.configureBlocking(localFileDescriptor, true);
      Object localObject1 = arrayOfInetSocketAddress[0];
      localSocketChannelImpl = new SocketChannelImpl(provider(), localFileDescriptor, (InetSocketAddress)localObject1);
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        try
        {
          localSecurityManager.checkAccept(((InetSocketAddress)localObject1).getAddress().getHostAddress(), ((InetSocketAddress)localObject1).getPort());
        }
        catch (SecurityException localSecurityException)
        {
          localSocketChannelImpl.close();
          throw localSecurityException;
        }
      }
      return localSocketChannelImpl;
    }
  }
  
  protected void implConfigureBlocking(boolean paramBoolean)
    throws IOException
  {
    IOUtil.configureBlocking(fd, paramBoolean);
  }
  
  protected void implCloseSelectableChannel()
    throws IOException
  {
    synchronized (stateLock)
    {
      if (state != 1) {
        nd.preClose(fd);
      }
      long l = thread;
      if (l != 0L) {
        NativeThread.signal(l);
      }
      if (!isRegistered()) {
        kill();
      }
    }
  }
  
  public void kill()
    throws IOException
  {
    synchronized (stateLock)
    {
      if (state == 1) {
        return;
      }
      if (state == -1)
      {
        state = 1;
        return;
      }
      assert ((!isOpen()) && (!isRegistered()));
      nd.close(fd);
      state = 1;
    }
  }
  
  public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl)
  {
    int i = paramSelectionKeyImpl.nioInterestOps();
    int j = paramSelectionKeyImpl.nioReadyOps();
    int k = paramInt2;
    if ((paramInt1 & Net.POLLNVAL) != 0) {
      return false;
    }
    if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0)
    {
      k = i;
      paramSelectionKeyImpl.nioReadyOps(k);
      return (k & (j ^ 0xFFFFFFFF)) != 0;
    }
    if (((paramInt1 & Net.POLLIN) != 0) && ((i & 0x10) != 0)) {
      k |= 0x10;
    }
    paramSelectionKeyImpl.nioReadyOps(k);
    return (k & (j ^ 0xFFFFFFFF)) != 0;
  }
  
  public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
  {
    return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
  }
  
  public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
  {
    return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
  }
  
  int poll(int paramInt, long paramLong)
    throws IOException
  {
    assert ((Thread.holdsLock(blockingLock())) && (!isBlocking()));
    synchronized (lock)
    {
      int i = 0;
      try
      {
        begin();
        synchronized (stateLock)
        {
          if (!isOpen())
          {
            int j = 0;
            thread = 0L;
            end(i > 0);
            return j;
          }
          thread = NativeThread.current();
        }
        i = Net.poll(fd, paramInt, paramLong);
      }
      finally
      {
        thread = 0L;
        end(i > 0);
      }
      return i;
    }
  }
  
  public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
  {
    int i = 0;
    if ((paramInt & 0x10) != 0) {
      i |= Net.POLLIN;
    }
    selector.putEventOps(paramSelectionKeyImpl, i);
  }
  
  public FileDescriptor getFD()
  {
    return fd;
  }
  
  public int getFDVal()
  {
    return fdVal;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getClass().getName());
    localStringBuffer.append('[');
    if (!isOpen()) {
      localStringBuffer.append("closed");
    } else {
      synchronized (stateLock)
      {
        InetSocketAddress localInetSocketAddress = localAddress();
        if (localInetSocketAddress == null) {
          localStringBuffer.append("unbound");
        } else {
          localStringBuffer.append(Net.getRevealedLocalAddressAsString(localInetSocketAddress));
        }
      }
    }
    localStringBuffer.append(']');
    return localStringBuffer.toString();
  }
  
  private int accept(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
    throws IOException
  {
    return accept0(paramFileDescriptor1, paramFileDescriptor2, paramArrayOfInetSocketAddress);
  }
  
  private native int accept0(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
    throws IOException;
  
  private static native void initIDs();
  
  static
  {
    IOUtil.load();
    initIDs();
  }
  
  private static class DefaultOptionsHolder
  {
    static final Set<SocketOption<?>> defaultOptions = ;
    
    private DefaultOptionsHolder() {}
    
    private static Set<SocketOption<?>> defaultOptions()
    {
      HashSet localHashSet = new HashSet(2);
      localHashSet.add(StandardSocketOptions.SO_RCVBUF);
      localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
      localHashSet.add(StandardSocketOptions.IP_TOS);
      return Collections.unmodifiableSet(localHashSet);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\ServerSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */