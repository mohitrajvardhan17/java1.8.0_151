package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import sun.net.NetHooks;

class SocketChannelImpl
  extends SocketChannel
  implements SelChImpl
{
  private static NativeDispatcher nd = new SocketDispatcher();
  private final FileDescriptor fd;
  private final int fdVal;
  private volatile long readerThread = 0L;
  private volatile long writerThread = 0L;
  private final Object readLock = new Object();
  private final Object writeLock = new Object();
  private final Object stateLock = new Object();
  private boolean isReuseAddress;
  private static final int ST_UNINITIALIZED = -1;
  private static final int ST_UNCONNECTED = 0;
  private static final int ST_PENDING = 1;
  private static final int ST_CONNECTED = 2;
  private static final int ST_KILLPENDING = 3;
  private static final int ST_KILLED = 4;
  private int state = -1;
  private InetSocketAddress localAddress;
  private InetSocketAddress remoteAddress;
  private boolean isInputOpen = true;
  private boolean isOutputOpen = true;
  private boolean readyToConnect = false;
  private Socket socket;
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider)
    throws IOException
  {
    super(paramSelectorProvider);
    fd = Net.socket(true);
    fdVal = IOUtil.fdVal(fd);
    state = 0;
  }
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, boolean paramBoolean)
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
  
  SocketChannelImpl(SelectorProvider paramSelectorProvider, FileDescriptor paramFileDescriptor, InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    super(paramSelectorProvider);
    fd = paramFileDescriptor;
    fdVal = IOUtil.fdVal(paramFileDescriptor);
    state = 2;
    localAddress = Net.localAddress(paramFileDescriptor);
    remoteAddress = paramInetSocketAddress;
  }
  
  public Socket socket()
  {
    synchronized (stateLock)
    {
      if (socket == null) {
        socket = SocketAdaptor.create(this);
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
      return Net.getRevealedLocalAddress(localAddress);
    }
  }
  
  public SocketAddress getRemoteAddress()
    throws IOException
  {
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      return remoteAddress;
    }
  }
  
  public <T> SocketChannel setOption(SocketOption<T> paramSocketOption, T paramT)
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
      if ((paramSocketOption == StandardSocketOptions.SO_REUSEADDR) && (Net.useExclusiveBind()))
      {
        isReuseAddress = ((Boolean)paramT).booleanValue();
        return this;
      }
      Net.setSocketOption(fd, Net.UNSPEC, paramSocketOption, paramT);
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
      if (paramSocketOption == StandardSocketOptions.IP_TOS)
      {
        StandardProtocolFamily localStandardProtocolFamily = Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
        return (T)Net.getSocketOption(fd, localStandardProtocolFamily, paramSocketOption);
      }
      return (T)Net.getSocketOption(fd, Net.UNSPEC, paramSocketOption);
    }
  }
  
  public final Set<SocketOption<?>> supportedOptions()
  {
    return DefaultOptionsHolder.defaultOptions;
  }
  
  private boolean ensureReadOpen()
    throws ClosedChannelException
  {
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!isConnected()) {
        throw new NotYetConnectedException();
      }
      return isInputOpen;
    }
  }
  
  private void ensureWriteOpen()
    throws ClosedChannelException
  {
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!isOutputOpen) {
        throw new ClosedChannelException();
      }
      if (!isConnected()) {
        throw new NotYetConnectedException();
      }
    }
  }
  
  private void readerCleanup()
    throws IOException
  {
    synchronized (stateLock)
    {
      readerThread = 0L;
      if (state == 3) {
        kill();
      }
    }
  }
  
  private void writerCleanup()
    throws IOException
  {
    synchronized (stateLock)
    {
      writerThread = 0L;
      if (state == 3) {
        kill();
      }
    }
  }
  
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    synchronized (readLock)
    {
      if (!ensureReadOpen()) {
        return -1;
      }
      int i = 0;
      try
      {
        begin();
        synchronized (stateLock)
        {
          if (!isOpen())
          {
            int k = 0;
            readerCleanup();
            end((i > 0) || (i == -2));
            synchronized (stateLock)
            {
              if ((i <= 0) && (!isInputOpen)) {
                return -1;
              }
            }
            assert (IOStatus.check(i));
            return k;
          }
          readerThread = NativeThread.current();
        }
        do
        {
          i = IOUtil.read(fd, paramByteBuffer, -1L, nd);
        } while ((i == -3) && (isOpen()));
        int j = IOStatus.normalize(i);
        readerCleanup();
        end((i > 0) || (i == -2));
        synchronized (stateLock)
        {
          if ((i <= 0) && (!isInputOpen)) {
            return -1;
          }
        }
        assert (IOStatus.check(i));
        return j;
      }
      finally
      {
        readerCleanup();
        end((i > 0) || (i == -2));
        synchronized (stateLock)
        {
          if ((i <= 0) && (!isInputOpen)) {
            return -1;
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    synchronized (readLock)
    {
      if (!ensureReadOpen()) {
        return -1L;
      }
      long l1 = 0L;
      try
      {
        begin();
        synchronized (stateLock)
        {
          if (!isOpen())
          {
            long l3 = 0L;
            readerCleanup();
            end((l1 > 0L) || (l1 == -2L));
            synchronized (stateLock)
            {
              if ((l1 <= 0L) && (!isInputOpen)) {
                return -1L;
              }
            }
            assert (IOStatus.check(l1));
            return l3;
          }
          readerThread = NativeThread.current();
        }
        do
        {
          l1 = IOUtil.read(fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        readerCleanup();
        end((l1 > 0L) || (l1 == -2L));
        synchronized (stateLock)
        {
          if ((l1 <= 0L) && (!isInputOpen)) {
            return -1L;
          }
        }
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        readerCleanup();
        end((l1 > 0L) || (l1 == -2L));
        synchronized (stateLock)
        {
          if ((l1 <= 0L) && (!isInputOpen)) {
            return -1L;
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (paramByteBuffer == null) {
      throw new NullPointerException();
    }
    synchronized (writeLock)
    {
      ensureWriteOpen();
      int i = 0;
      try
      {
        begin();
        synchronized (stateLock)
        {
          if (!isOpen())
          {
            int k = 0;
            writerCleanup();
            end((i > 0) || (i == -2));
            synchronized (stateLock)
            {
              if ((i <= 0) && (!isOutputOpen)) {
                throw new AsynchronousCloseException();
              }
            }
            assert (IOStatus.check(i));
            return k;
          }
          writerThread = NativeThread.current();
        }
        do
        {
          i = IOUtil.write(fd, paramByteBuffer, -1L, nd);
        } while ((i == -3) && (isOpen()));
        int j = IOStatus.normalize(i);
        writerCleanup();
        end((i > 0) || (i == -2));
        synchronized (stateLock)
        {
          if ((i <= 0) && (!isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        assert (IOStatus.check(i));
        return j;
      }
      finally
      {
        writerCleanup();
        end((i > 0) || (i == -2));
        synchronized (stateLock)
        {
          if ((i <= 0) && (!isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    synchronized (writeLock)
    {
      ensureWriteOpen();
      long l1 = 0L;
      try
      {
        begin();
        synchronized (stateLock)
        {
          if (!isOpen())
          {
            long l3 = 0L;
            writerCleanup();
            end((l1 > 0L) || (l1 == -2L));
            synchronized (stateLock)
            {
              if ((l1 <= 0L) && (!isOutputOpen)) {
                throw new AsynchronousCloseException();
              }
            }
            assert (IOStatus.check(l1));
            return l3;
          }
          writerThread = NativeThread.current();
        }
        do
        {
          l1 = IOUtil.write(fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        writerCleanup();
        end((l1 > 0L) || (l1 == -2L));
        synchronized (stateLock)
        {
          if ((l1 <= 0L) && (!isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        writerCleanup();
        end((l1 > 0L) || (l1 == -2L));
        synchronized (stateLock)
        {
          if ((l1 <= 0L) && (!isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  int sendOutOfBandData(byte paramByte)
    throws IOException
  {
    synchronized (writeLock)
    {
      ensureWriteOpen();
      int i = 0;
      try
      {
        begin();
        synchronized (stateLock)
        {
          if (!isOpen())
          {
            int k = 0;
            writerCleanup();
            end((i > 0) || (i == -2));
            synchronized (stateLock)
            {
              if ((i <= 0) && (!isOutputOpen)) {
                throw new AsynchronousCloseException();
              }
            }
            assert (IOStatus.check(i));
            return k;
          }
          writerThread = NativeThread.current();
        }
        do
        {
          i = sendOutOfBandData(fd, paramByte);
        } while ((i == -3) && (isOpen()));
        int j = IOStatus.normalize(i);
        writerCleanup();
        end((i > 0) || (i == -2));
        synchronized (stateLock)
        {
          if ((i <= 0) && (!isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        assert (IOStatus.check(i));
        return j;
      }
      finally
      {
        writerCleanup();
        end((i > 0) || (i == -2));
        synchronized (stateLock)
        {
          if ((i <= 0) && (!isOutputOpen)) {
            throw new AsynchronousCloseException();
          }
        }
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  protected void implConfigureBlocking(boolean paramBoolean)
    throws IOException
  {
    IOUtil.configureBlocking(fd, paramBoolean);
  }
  
  /* Error */
  public InetSocketAddress localAddress()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 463	sun/nio/ch/SocketChannelImpl:stateLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 465	sun/nio/ch/SocketChannelImpl:localAddress	Ljava/net/InetSocketAddress;
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
    //   0	19	0	this	SocketChannelImpl
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public SocketAddress remoteAddress()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 463	sun/nio/ch/SocketChannelImpl:stateLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 466	sun/nio/ch/SocketChannelImpl:remoteAddress	Ljava/net/InetSocketAddress;
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
    //   0	19	0	this	SocketChannelImpl
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public SocketChannel bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    synchronized (readLock)
    {
      synchronized (writeLock)
      {
        synchronized (stateLock)
        {
          if (!isOpen()) {
            throw new ClosedChannelException();
          }
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
    }
    return this;
  }
  
  public boolean isConnected()
  {
    synchronized (stateLock)
    {
      return state == 2;
    }
  }
  
  public boolean isConnectionPending()
  {
    synchronized (stateLock)
    {
      return state == 1;
    }
  }
  
  void ensureOpenAndUnconnected()
    throws IOException
  {
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (state == 2) {
        throw new AlreadyConnectedException();
      }
      if (state == 1) {
        throw new ConnectionPendingException();
      }
    }
  }
  
  public boolean connect(SocketAddress paramSocketAddress)
    throws IOException
  {
    int i = 0;
    synchronized (readLock)
    {
      synchronized (writeLock)
      {
        ensureOpenAndUnconnected();
        InetSocketAddress localInetSocketAddress = Net.checkAddress(paramSocketAddress);
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
          localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
        }
        synchronized (blockingLock())
        {
          int j = 0;
          try
          {
            try
            {
              begin();
              synchronized (stateLock)
              {
                if (!isOpen())
                {
                  boolean bool = false;
                  readerCleanup();
                  end((j > 0) || (j == -2));
                  assert (IOStatus.check(j));
                  return bool;
                }
                if (localAddress == null) {
                  NetHooks.beforeTcpConnect(fd, localInetSocketAddress.getAddress(), localInetSocketAddress.getPort());
                }
                readerThread = NativeThread.current();
              }
              for (;;)
              {
                ??? = localInetSocketAddress.getAddress();
                if (((InetAddress)???).isAnyLocalAddress()) {
                  ??? = InetAddress.getLocalHost();
                }
                j = Net.connect(fd, (InetAddress)???, localInetSocketAddress.getPort());
                if ((j != -3) || (!isOpen())) {
                  break;
                }
              }
            }
            finally
            {
              readerCleanup();
              end((j > 0) || (j == -2));
              if ((!$assertionsDisabled) && (!IOStatus.check(j))) {
                throw new AssertionError();
              }
            }
          }
          catch (IOException ???)
          {
            close();
            throw ((Throwable)???);
          }
          synchronized (stateLock)
          {
            remoteAddress = localInetSocketAddress;
            if (j > 0)
            {
              state = 2;
              if (isOpen()) {
                localAddress = Net.localAddress(fd);
              }
              return true;
            }
            if (!isBlocking()) {
              state = 1;
            } else if (!$assertionsDisabled) {
              throw new AssertionError();
            }
          }
        }
        return false;
      }
    }
  }
  
  public boolean finishConnect()
    throws IOException
  {
    synchronized (readLock)
    {
      synchronized (writeLock)
      {
        synchronized (stateLock)
        {
          if (!isOpen()) {
            throw new ClosedChannelException();
          }
          if (state == 2) {
            return true;
          }
          if (state != 1) {
            throw new NoConnectionPendingException();
          }
        }
        int i = 0;
        try
        {
          try
          {
            begin();
            synchronized (blockingLock())
            {
              synchronized (stateLock)
              {
                if (!isOpen())
                {
                  boolean bool = false;
                  synchronized (stateLock)
                  {
                    readerThread = 0L;
                    if (state == 3)
                    {
                      kill();
                      i = 0;
                    }
                  }
                  end((i > 0) || (i == -2));
                  assert (IOStatus.check(i));
                  return bool;
                }
                readerThread = NativeThread.current();
              }
              if (!isBlocking()) {
                for (;;)
                {
                  i = checkConnect(fd, false, readyToConnect);
                  if ((i != -3) || (!isOpen())) {
                    break;
                  }
                }
              }
              for (;;)
              {
                i = checkConnect(fd, true, readyToConnect);
                if (i != 0) {
                  if ((i != -3) || (!isOpen())) {
                    break;
                  }
                }
              }
            }
          }
          finally
          {
            synchronized (stateLock)
            {
              readerThread = 0L;
              if (state == 3)
              {
                kill();
                i = 0;
              }
            }
            end((i > 0) || (i == -2));
            if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
              throw new AssertionError();
            }
          }
        }
        catch (IOException localObject1)
        {
          close();
          throw ((Throwable)???);
        }
        if (i > 0)
        {
          synchronized (stateLock)
          {
            state = 2;
            if (isOpen()) {
              localAddress = Net.localAddress(fd);
            }
          }
          return true;
        }
        return false;
      }
    }
  }
  
  public SocketChannel shutdownInput()
    throws IOException
  {
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!isConnected()) {
        throw new NotYetConnectedException();
      }
      if (isInputOpen)
      {
        Net.shutdown(fd, 0);
        if (readerThread != 0L) {
          NativeThread.signal(readerThread);
        }
        isInputOpen = false;
      }
      return this;
    }
  }
  
  public SocketChannel shutdownOutput()
    throws IOException
  {
    synchronized (stateLock)
    {
      if (!isOpen()) {
        throw new ClosedChannelException();
      }
      if (!isConnected()) {
        throw new NotYetConnectedException();
      }
      if (isOutputOpen)
      {
        Net.shutdown(fd, 1);
        if (writerThread != 0L) {
          NativeThread.signal(writerThread);
        }
        isOutputOpen = false;
      }
      return this;
    }
  }
  
  /* Error */
  public boolean isInputOpen()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 463	sun/nio/ch/SocketChannelImpl:stateLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 457	sun/nio/ch/SocketChannelImpl:isInputOpen	Z
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
    //   0	19	0	this	SocketChannelImpl
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public boolean isOutputOpen()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 463	sun/nio/ch/SocketChannelImpl:stateLock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 458	sun/nio/ch/SocketChannelImpl:isOutputOpen	Z
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
    //   0	19	0	this	SocketChannelImpl
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  protected void implCloseSelectableChannel()
    throws IOException
  {
    synchronized (stateLock)
    {
      isInputOpen = false;
      isOutputOpen = false;
      if (state != 4) {
        nd.preClose(fd);
      }
      if (readerThread != 0L) {
        NativeThread.signal(readerThread);
      }
      if (writerThread != 0L) {
        NativeThread.signal(writerThread);
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
      if (state == 4) {
        return;
      }
      if (state == -1)
      {
        state = 4;
        return;
      }
      assert ((!isOpen()) && (!isRegistered()));
      if ((readerThread == 0L) && (writerThread == 0L))
      {
        nd.close(fd);
        state = 4;
      }
      else
      {
        state = 3;
      }
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
      readyToConnect = true;
      return (k & (j ^ 0xFFFFFFFF)) != 0;
    }
    if (((paramInt1 & Net.POLLIN) != 0) && ((i & 0x1) != 0) && (state == 2)) {
      k |= 0x1;
    }
    if (((paramInt1 & Net.POLLCONN) != 0) && ((i & 0x8) != 0) && ((state == 0) || (state == 1)))
    {
      k |= 0x8;
      readyToConnect = true;
    }
    if (((paramInt1 & Net.POLLOUT) != 0) && ((i & 0x4) != 0) && (state == 2)) {
      k |= 0x4;
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
    synchronized (readLock)
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
            readerCleanup();
            end(i > 0);
            return j;
          }
          readerThread = NativeThread.current();
        }
        i = Net.poll(fd, paramInt, paramLong);
      }
      finally
      {
        readerCleanup();
        end(i > 0);
      }
      return i;
    }
  }
  
  public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl)
  {
    int i = 0;
    if ((paramInt & 0x1) != 0) {
      i |= Net.POLLIN;
    }
    if ((paramInt & 0x4) != 0) {
      i |= Net.POLLOUT;
    }
    if ((paramInt & 0x8) != 0) {
      i |= Net.POLLCONN;
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
    localStringBuffer.append(getClass().getSuperclass().getName());
    localStringBuffer.append('[');
    if (!isOpen()) {
      localStringBuffer.append("closed");
    } else {
      synchronized (stateLock)
      {
        switch (state)
        {
        case 0: 
          localStringBuffer.append("unconnected");
          break;
        case 1: 
          localStringBuffer.append("connection-pending");
          break;
        case 2: 
          localStringBuffer.append("connected");
          if (!isInputOpen) {
            localStringBuffer.append(" ishut");
          }
          if (!isOutputOpen) {
            localStringBuffer.append(" oshut");
          }
          break;
        }
        InetSocketAddress localInetSocketAddress = localAddress();
        if (localInetSocketAddress != null)
        {
          localStringBuffer.append(" local=");
          localStringBuffer.append(Net.getRevealedLocalAddressAsString(localInetSocketAddress));
        }
        if (remoteAddress() != null)
        {
          localStringBuffer.append(" remote=");
          localStringBuffer.append(remoteAddress().toString());
        }
      }
    }
    localStringBuffer.append(']');
    return localStringBuffer.toString();
  }
  
  private static native int checkConnect(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException;
  
  private static native int sendOutOfBandData(FileDescriptor paramFileDescriptor, byte paramByte)
    throws IOException;
  
  static
  {
    IOUtil.load();
  }
  
  private static class DefaultOptionsHolder
  {
    static final Set<SocketOption<?>> defaultOptions = ;
    
    private DefaultOptionsHolder() {}
    
    private static Set<SocketOption<?>> defaultOptions()
    {
      HashSet localHashSet = new HashSet(8);
      localHashSet.add(StandardSocketOptions.SO_SNDBUF);
      localHashSet.add(StandardSocketOptions.SO_RCVBUF);
      localHashSet.add(StandardSocketOptions.SO_KEEPALIVE);
      localHashSet.add(StandardSocketOptions.SO_REUSEADDR);
      localHashSet.add(StandardSocketOptions.SO_LINGER);
      localHashSet.add(StandardSocketOptions.TCP_NODELAY);
      localHashSet.add(StandardSocketOptions.IP_TOS);
      localHashSet.add(ExtendedSocketOption.SO_OOBINLINE);
      if (ExtendedOptionsImpl.flowSupported()) {
        localHashSet.add(ExtendedSocketOptions.SO_FLOW_SLA);
      }
      return Collections.unmodifiableSet(localHashSet);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\SocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */