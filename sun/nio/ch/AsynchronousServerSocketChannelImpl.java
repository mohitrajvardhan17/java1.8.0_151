package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import sun.net.NetHooks;

abstract class AsynchronousServerSocketChannelImpl
  extends AsynchronousServerSocketChannel
  implements Cancellable, Groupable
{
  protected final FileDescriptor fd = Net.serverSocket(true);
  protected volatile InetSocketAddress localAddress = null;
  private final Object stateLock = new Object();
  private ReadWriteLock closeLock = new ReentrantReadWriteLock();
  private volatile boolean open = true;
  private volatile boolean acceptKilled;
  private boolean isReuseAddress;
  
  AsynchronousServerSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
  {
    super(paramAsynchronousChannelGroupImpl.provider());
  }
  
  public final boolean isOpen()
  {
    return open;
  }
  
  final void begin()
    throws IOException
  {
    closeLock.readLock().lock();
    if (!isOpen()) {
      throw new ClosedChannelException();
    }
  }
  
  final void end()
  {
    closeLock.readLock().unlock();
  }
  
  abstract void implClose()
    throws IOException;
  
  public final void close()
    throws IOException
  {
    closeLock.writeLock().lock();
    try
    {
      if (!open) {
        return;
      }
      open = false;
    }
    finally
    {
      closeLock.writeLock().unlock();
    }
    implClose();
  }
  
  abstract Future<AsynchronousSocketChannel> implAccept(Object paramObject, CompletionHandler<AsynchronousSocketChannel, Object> paramCompletionHandler);
  
  public final Future<AsynchronousSocketChannel> accept()
  {
    return implAccept(null, null);
  }
  
  public final <A> void accept(A paramA, CompletionHandler<AsynchronousSocketChannel, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    implAccept(paramA, paramCompletionHandler);
  }
  
  final boolean isAcceptKilled()
  {
    return acceptKilled;
  }
  
  public final void onCancel(PendingFuture<?, ?> paramPendingFuture)
  {
    acceptKilled = true;
  }
  
  public final AsynchronousServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkListen(localInetSocketAddress.getPort());
    }
    try
    {
      begin();
      synchronized (stateLock)
      {
        if (localAddress != null) {
          throw new AlreadyBoundException();
        }
        NetHooks.beforeTcpBind(fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
        Net.bind(fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
        Net.listen(fd, paramInt < 1 ? 50 : paramInt);
        localAddress = Net.localAddress(fd);
      }
    }
    finally
    {
      end();
    }
    return this;
  }
  
  public final SocketAddress getLocalAddress()
    throws IOException
  {
    if (!isOpen()) {
      throw new ClosedChannelException();
    }
    return Net.getRevealedLocalAddress(localAddress);
  }
  
  public final <T> AsynchronousServerSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    if (paramSocketOption == null) {
      throw new NullPointerException();
    }
    if (!supportedOptions().contains(paramSocketOption)) {
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
    }
    try
    {
      begin();
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind())) {
        isReuseAddress = ((Boolean)paramT).booleanValue();
      } else {
        Net.setSocketOption(fd, Net.UNSPEC, paramSocketOption, paramT);
      }
      AsynchronousServerSocketChannelImpl localAsynchronousServerSocketChannelImpl = this;
      return localAsynchronousServerSocketChannelImpl;
    }
    finally
    {
      end();
    }
  }
  
  public final <T> T getOption(SocketOption<T> paramSocketOption)
    throws IOException
  {
    if (paramSocketOption == null) {
      throw new NullPointerException();
    }
    if (!supportedOptions().contains(paramSocketOption)) {
      throw new UnsupportedOperationException("'" + paramSocketOption + "' not supported");
    }
    try
    {
      begin();
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind()))
      {
        localObject1 = Boolean.valueOf(isReuseAddress);
        return (T)localObject1;
      }
      Object localObject1 = Net.getSocketOption(fd, Net.UNSPEC, paramSocketOption);
      return (T)localObject1;
    }
    finally
    {
      end();
    }
  }
  
  public final Set<SocketOption<?>> supportedOptions()
  {
    return DefaultOptionsHolder.defaultOptions;
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getClass().getName());
    localStringBuilder.append('[');
    if (!isOpen()) {
      localStringBuilder.append("closed");
    } else if (localAddress == null) {
      localStringBuilder.append("unbound");
    } else {
      localStringBuilder.append(Net.getRevealedLocalAddressAsString(localAddress));
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
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
      return Collections.unmodifiableSet(localHashSet);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\AsynchronousServerSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */