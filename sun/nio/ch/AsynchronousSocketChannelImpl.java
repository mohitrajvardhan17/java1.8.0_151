package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.NetHooks;

abstract class AsynchronousSocketChannelImpl
  extends AsynchronousSocketChannel
  implements Cancellable, Groupable
{
  protected final FileDescriptor fd;
  protected final Object stateLock = new Object();
  protected volatile InetSocketAddress localAddress = null;
  protected volatile InetSocketAddress remoteAddress = null;
  static final int ST_UNINITIALIZED = -1;
  static final int ST_UNCONNECTED = 0;
  static final int ST_PENDING = 1;
  static final int ST_CONNECTED = 2;
  protected volatile int state = -1;
  private final Object readLock = new Object();
  private boolean reading;
  private boolean readShutdown;
  private boolean readKilled;
  private final Object writeLock = new Object();
  private boolean writing;
  private boolean writeShutdown;
  private boolean writeKilled;
  private final ReadWriteLock closeLock = new ReentrantReadWriteLock();
  private volatile boolean open = true;
  private boolean isReuseAddress;
  
  AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl)
    throws IOException
  {
    super(paramAsynchronousChannelGroupImpl.provider());
    fd = Net.socket(true);
    state = 0;
  }
  
  AsynchronousSocketChannelImpl(AsynchronousChannelGroupImpl paramAsynchronousChannelGroupImpl, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    super(paramAsynchronousChannelGroupImpl.provider());
    fd = paramFileDescriptor;
    state = 2;
    localAddress = Net.localAddress(paramFileDescriptor);
    remoteAddress = paramInetSocketAddress;
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
  
  final void enableReading(boolean paramBoolean)
  {
    synchronized (readLock)
    {
      reading = false;
      if (paramBoolean) {
        readKilled = true;
      }
    }
  }
  
  final void enableReading()
  {
    enableReading(false);
  }
  
  final void enableWriting(boolean paramBoolean)
  {
    synchronized (writeLock)
    {
      writing = false;
      if (paramBoolean) {
        writeKilled = true;
      }
    }
  }
  
  final void enableWriting()
  {
    enableWriting(false);
  }
  
  final void killReading()
  {
    synchronized (readLock)
    {
      readKilled = true;
    }
  }
  
  final void killWriting()
  {
    synchronized (writeLock)
    {
      writeKilled = true;
    }
  }
  
  final void killConnect()
  {
    killReading();
    killWriting();
  }
  
  abstract <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler);
  
  public final Future<Void> connect(SocketAddress paramSocketAddress)
  {
    return implConnect(paramSocketAddress, null, null);
  }
  
  public final <A> void connect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    implConnect(paramSocketAddress, paramA, paramCompletionHandler);
  }
  
  abstract <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler);
  
  private <V extends Number, A> Future<V> read(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    if (!isOpen())
    {
      ClosedChannelException localClosedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure(localClosedChannelException);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, localClosedChannelException);
      return null;
    }
    if (remoteAddress == null) {
      throw new NotYetConnectedException();
    }
    int i = (paramBoolean) || (paramByteBuffer.hasRemaining()) ? 1 : 0;
    int j = 0;
    synchronized (readLock)
    {
      if (readKilled) {
        throw new IllegalStateException("Reading not allowed due to timeout or cancellation");
      }
      if (reading) {
        throw new ReadPendingException();
      }
      if (readShutdown) {
        j = 1;
      } else if (i != 0) {
        reading = true;
      }
    }
    if ((j != 0) || (i == 0))
    {
      if (paramBoolean) {
        ??? = j != 0 ? Long.valueOf(-1L) : Long.valueOf(0L);
      } else {
        ??? = Integer.valueOf(j != 0 ? -1 : 0);
      }
      if (paramCompletionHandler == null) {
        return CompletedFuture.withResult(???);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, ???, null);
      return null;
    }
    return implRead(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final Future<Integer> read(ByteBuffer paramByteBuffer)
  {
    if (paramByteBuffer.isReadOnly()) {
      throw new IllegalArgumentException("Read-only buffer");
    }
    return read(false, paramByteBuffer, null, 0L, TimeUnit.MILLISECONDS, null, null);
  }
  
  public final <A> void read(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    if (paramByteBuffer.isReadOnly()) {
      throw new IllegalArgumentException("Read-only buffer");
    }
    read(false, paramByteBuffer, null, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final <A> void read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    ByteBuffer[] arrayOfByteBuffer = Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2);
    for (int i = 0; i < arrayOfByteBuffer.length; i++) {
      if (arrayOfByteBuffer[i].isReadOnly()) {
        throw new IllegalArgumentException("Read-only buffer");
      }
    }
    read(true, null, arrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  abstract <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler);
  
  private <V extends Number, A> Future<V> write(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    int i = (paramBoolean) || (paramByteBuffer.hasRemaining()) ? 1 : 0;
    int j = 0;
    if (isOpen())
    {
      if (remoteAddress == null) {
        throw new NotYetConnectedException();
      }
      synchronized (writeLock)
      {
        if (writeKilled) {
          throw new IllegalStateException("Writing not allowed due to timeout or cancellation");
        }
        if (writing) {
          throw new WritePendingException();
        }
        if (writeShutdown) {
          j = 1;
        } else if (i != 0) {
          writing = true;
        }
      }
    }
    else
    {
      j = 1;
    }
    if (j != 0)
    {
      ??? = new ClosedChannelException();
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure((Throwable)???);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)???);
      return null;
    }
    if (i == 0)
    {
      ??? = paramBoolean ? Long.valueOf(0L) : Integer.valueOf(0);
      if (paramCompletionHandler == null) {
        return CompletedFuture.withResult(???);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, ???, null);
      return null;
    }
    return implWrite(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final Future<Integer> write(ByteBuffer paramByteBuffer)
  {
    return write(false, paramByteBuffer, null, 0L, TimeUnit.MILLISECONDS, null, null);
  }
  
  public final <A> void write(ByteBuffer paramByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    write(false, paramByteBuffer, null, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final <A> void write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<Long, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    paramArrayOfByteBuffer = Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2);
    write(true, null, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
  }
  
  public final AsynchronousSocketChannel bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    try
    {
      begin();
      synchronized (stateLock)
      {
        if (state == 1) {
          throw new ConnectionPendingException();
        }
        if (localAddress != null) {
          throw new AlreadyBoundException();
        }
        InetSocketAddress localInetSocketAddress = paramSocketAddress == null ? new InetSocketAddress(0) : Net.checkAddress(paramSocketAddress);
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
          localSecurityManager.checkListen(localInetSocketAddress.getPort());
        }
        NetHooks.beforeTcpBind(fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
        Net.bind(fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
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
  
  public final <T> AsynchronousSocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
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
      if (writeShutdown) {
        throw new IOException("Connection has been shutdown for writing");
      }
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind())) {
        isReuseAddress = ((Boolean)paramT).booleanValue();
      } else {
        Net.setSocketOption(fd, Net.UNSPEC, paramSocketOption, paramT);
      }
      AsynchronousSocketChannelImpl localAsynchronousSocketChannelImpl = this;
      return localAsynchronousSocketChannelImpl;
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
  
  public final SocketAddress getRemoteAddress()
    throws IOException
  {
    if (!isOpen()) {
      throw new ClosedChannelException();
    }
    return remoteAddress;
  }
  
  /* Error */
  public final AsynchronousSocketChannel shutdownInput()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 411	sun/nio/ch/AsynchronousSocketChannelImpl:begin	()V
    //   4: aload_0
    //   5: getfield 370	sun/nio/ch/AsynchronousSocketChannelImpl:remoteAddress	Ljava/net/InetSocketAddress;
    //   8: ifnonnull +11 -> 19
    //   11: new 200	java/nio/channels/NotYetConnectedException
    //   14: dup
    //   15: invokespecial 405	java/nio/channels/NotYetConnectedException:<init>	()V
    //   18: athrow
    //   19: aload_0
    //   20: getfield 366	sun/nio/ch/AsynchronousSocketChannelImpl:readLock	Ljava/lang/Object;
    //   23: dup
    //   24: astore_1
    //   25: monitorenter
    //   26: aload_0
    //   27: getfield 360	sun/nio/ch/AsynchronousSocketChannelImpl:readShutdown	Z
    //   30: ifne +16 -> 46
    //   33: aload_0
    //   34: getfield 365	sun/nio/ch/AsynchronousSocketChannelImpl:fd	Ljava/io/FileDescriptor;
    //   37: iconst_0
    //   38: invokestatic 431	sun/nio/ch/Net:shutdown	(Ljava/io/FileDescriptor;I)V
    //   41: aload_0
    //   42: iconst_1
    //   43: putfield 360	sun/nio/ch/AsynchronousSocketChannelImpl:readShutdown	Z
    //   46: aload_1
    //   47: monitorexit
    //   48: goto +8 -> 56
    //   51: astore_2
    //   52: aload_1
    //   53: monitorexit
    //   54: aload_2
    //   55: athrow
    //   56: aload_0
    //   57: invokevirtual 412	sun/nio/ch/AsynchronousSocketChannelImpl:end	()V
    //   60: goto +10 -> 70
    //   63: astore_3
    //   64: aload_0
    //   65: invokevirtual 412	sun/nio/ch/AsynchronousSocketChannelImpl:end	()V
    //   68: aload_3
    //   69: athrow
    //   70: aload_0
    //   71: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	AsynchronousSocketChannelImpl
    //   51	4	2	localObject1	Object
    //   63	6	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   26	48	51	finally
    //   51	54	51	finally
    //   0	56	63	finally
  }
  
  /* Error */
  public final AsynchronousSocketChannel shutdownOutput()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 411	sun/nio/ch/AsynchronousSocketChannelImpl:begin	()V
    //   4: aload_0
    //   5: getfield 370	sun/nio/ch/AsynchronousSocketChannelImpl:remoteAddress	Ljava/net/InetSocketAddress;
    //   8: ifnonnull +11 -> 19
    //   11: new 200	java/nio/channels/NotYetConnectedException
    //   14: dup
    //   15: invokespecial 405	java/nio/channels/NotYetConnectedException:<init>	()V
    //   18: athrow
    //   19: aload_0
    //   20: getfield 368	sun/nio/ch/AsynchronousSocketChannelImpl:writeLock	Ljava/lang/Object;
    //   23: dup
    //   24: astore_1
    //   25: monitorenter
    //   26: aload_0
    //   27: getfield 363	sun/nio/ch/AsynchronousSocketChannelImpl:writeShutdown	Z
    //   30: ifne +16 -> 46
    //   33: aload_0
    //   34: getfield 365	sun/nio/ch/AsynchronousSocketChannelImpl:fd	Ljava/io/FileDescriptor;
    //   37: iconst_1
    //   38: invokestatic 431	sun/nio/ch/Net:shutdown	(Ljava/io/FileDescriptor;I)V
    //   41: aload_0
    //   42: iconst_1
    //   43: putfield 363	sun/nio/ch/AsynchronousSocketChannelImpl:writeShutdown	Z
    //   46: aload_1
    //   47: monitorexit
    //   48: goto +8 -> 56
    //   51: astore_2
    //   52: aload_1
    //   53: monitorexit
    //   54: aload_2
    //   55: athrow
    //   56: aload_0
    //   57: invokevirtual 412	sun/nio/ch/AsynchronousSocketChannelImpl:end	()V
    //   60: goto +10 -> 70
    //   63: astore_3
    //   64: aload_0
    //   65: invokevirtual 412	sun/nio/ch/AsynchronousSocketChannelImpl:end	()V
    //   68: aload_3
    //   69: athrow
    //   70: aload_0
    //   71: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	AsynchronousSocketChannelImpl
    //   51	4	2	localObject1	Object
    //   63	6	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   26	48	51	finally
    //   51	54	51	finally
    //   0	56	63	finally
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getClass().getName());
    localStringBuilder.append('[');
    synchronized (stateLock)
    {
      if (!isOpen())
      {
        localStringBuilder.append("closed");
      }
      else
      {
        switch (state)
        {
        case 0: 
          localStringBuilder.append("unconnected");
          break;
        case 1: 
          localStringBuilder.append("connection-pending");
          break;
        case 2: 
          localStringBuilder.append("connected");
          if (readShutdown) {
            localStringBuilder.append(" ishut");
          }
          if (writeShutdown) {
            localStringBuilder.append(" oshut");
          }
          break;
        }
        if (localAddress != null)
        {
          localStringBuilder.append(" local=");
          localStringBuilder.append(Net.getRevealedLocalAddressAsString(localAddress));
        }
        if (remoteAddress != null)
        {
          localStringBuilder.append(" remote=");
          localStringBuilder.append(remoteAddress.toString());
        }
      }
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
      HashSet localHashSet = new HashSet(5);
      localHashSet.add(StandardSocketOptions.SO_SNDBUF);
      localHashSet.add(StandardSocketOptions.SO_RCVBUF);
      localHashSet.add(StandardSocketOptions.SO_KEEPALIVE);
      localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
      localHashSet.add(StandardSocketOptions.TCP_NODELAY);
      if (ExtendedOptionsImpl.flowSupported()) {
        localHashSet.add(ExtendedSocketOptions.SO_FLOW_SLA);
      }
      return Collections.unmodifiableSet(localHashSet);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\AsynchronousSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */