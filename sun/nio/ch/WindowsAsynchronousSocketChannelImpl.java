package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

class WindowsAsynchronousSocketChannelImpl
  extends AsynchronousSocketChannelImpl
  implements Iocp.OverlappedChannel
{
  private static final Unsafe unsafe = ;
  private static int addressSize = unsafe.addressSize();
  private static final int SIZEOF_WSABUF = dependsArch(8, 16);
  private static final int OFFSETOF_LEN = 0;
  private static final int OFFSETOF_BUF = dependsArch(4, 8);
  private static final int MAX_WSABUF = 16;
  private static final int SIZEOF_WSABUFARRAY = 16 * SIZEOF_WSABUF;
  final long handle;
  private final Iocp iocp;
  private final int completionKey;
  private final PendingIoCache ioCache;
  private final long readBufferArray;
  private final long writeBufferArray;
  
  private static int dependsArch(int paramInt1, int paramInt2)
  {
    return addressSize == 4 ? paramInt1 : paramInt2;
  }
  
  WindowsAsynchronousSocketChannelImpl(Iocp paramIocp, boolean paramBoolean)
    throws IOException
  {
    super(paramIocp);
    long l = IOUtil.fdVal(fd);
    int i = 0;
    try
    {
      i = paramIocp.associate(this, l);
    }
    catch (ShutdownChannelGroupException localShutdownChannelGroupException)
    {
      if (paramBoolean)
      {
        closesocket0(l);
        throw localShutdownChannelGroupException;
      }
    }
    catch (IOException localIOException)
    {
      closesocket0(l);
      throw localIOException;
    }
    handle = l;
    iocp = paramIocp;
    completionKey = i;
    ioCache = new PendingIoCache();
    readBufferArray = unsafe.allocateMemory(SIZEOF_WSABUFARRAY);
    writeBufferArray = unsafe.allocateMemory(SIZEOF_WSABUFARRAY);
  }
  
  WindowsAsynchronousSocketChannelImpl(Iocp paramIocp)
    throws IOException
  {
    this(paramIocp, true);
  }
  
  public AsynchronousChannelGroupImpl group()
  {
    return iocp;
  }
  
  public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong)
  {
    return ioCache.remove(paramLong);
  }
  
  long handle()
  {
    return handle;
  }
  
  void setConnected(InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2)
  {
    synchronized (stateLock)
    {
      state = 2;
      localAddress = paramInetSocketAddress1;
      remoteAddress = paramInetSocketAddress2;
    }
  }
  
  void implClose()
    throws IOException
  {
    closesocket0(handle);
    ioCache.close();
    unsafe.freeMemory(readBufferArray);
    unsafe.freeMemory(writeBufferArray);
    if (completionKey != 0) {
      iocp.disassociate(completionKey);
    }
  }
  
  public void onCancel(PendingFuture<?, ?> paramPendingFuture)
  {
    if ((paramPendingFuture.getContext() instanceof ConnectTask)) {
      killConnect();
    }
    if ((paramPendingFuture.getContext() instanceof ReadTask)) {
      killReading();
    }
    if ((paramPendingFuture.getContext() instanceof WriteTask)) {
      killWriting();
    }
  }
  
  private void doPrivilegedBind(final SocketAddress paramSocketAddress)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws IOException
        {
          bind(paramSocketAddress);
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler)
  {
    if (!isOpen())
    {
      localObject1 = new ClosedChannelException();
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure((Throwable)localObject1);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)localObject1);
      return null;
    }
    Object localObject1 = Net.checkAddress(paramSocketAddress);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkConnect(((InetSocketAddress)localObject1).getAddress().getHostAddress(), ((InetSocketAddress)localObject1).getPort());
    }
    Object localObject2 = null;
    synchronized (stateLock)
    {
      if (state == 2) {
        throw new AlreadyConnectedException();
      }
      if (state == 1) {
        throw new ConnectionPendingException();
      }
      if (localAddress == null) {
        try
        {
          InetSocketAddress localInetSocketAddress = new InetSocketAddress(0);
          if (localSecurityManager == null) {
            bind(localInetSocketAddress);
          } else {
            doPrivilegedBind(localInetSocketAddress);
          }
        }
        catch (IOException localIOException2)
        {
          localObject2 = localIOException2;
        }
      }
      if (localObject2 == null) {
        state = 1;
      }
    }
    if (localObject2 != null)
    {
      try
      {
        close();
      }
      catch (IOException localIOException1) {}
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure((Throwable)localObject2);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)localObject2);
      return null;
    }
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ConnectTask localConnectTask = new ConnectTask((InetSocketAddress)localObject1, localPendingFuture);
    localPendingFuture.setContext(localConnectTask);
    if (Iocp.supportsThreadAgnosticIo()) {
      localConnectTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localConnectTask);
    }
    return localPendingFuture;
  }
  
  <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ByteBuffer[] arrayOfByteBuffer;
    if (paramBoolean)
    {
      arrayOfByteBuffer = paramArrayOfByteBuffer;
    }
    else
    {
      arrayOfByteBuffer = new ByteBuffer[1];
      arrayOfByteBuffer[0] = paramByteBuffer;
    }
    final ReadTask localReadTask = new ReadTask(arrayOfByteBuffer, paramBoolean, localPendingFuture);
    localPendingFuture.setContext(localReadTask);
    if (paramLong > 0L)
    {
      Future localFuture = iocp.schedule(new Runnable()
      {
        public void run()
        {
          localReadTask.timeout();
        }
      }, paramLong, paramTimeUnit);
      localPendingFuture.setTimeoutTask(localFuture);
    }
    if (Iocp.supportsThreadAgnosticIo()) {
      localReadTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localReadTask);
    }
    return localPendingFuture;
  }
  
  <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ByteBuffer[] arrayOfByteBuffer;
    if (paramBoolean)
    {
      arrayOfByteBuffer = paramArrayOfByteBuffer;
    }
    else
    {
      arrayOfByteBuffer = new ByteBuffer[1];
      arrayOfByteBuffer[0] = paramByteBuffer;
    }
    final WriteTask localWriteTask = new WriteTask(arrayOfByteBuffer, paramBoolean, localPendingFuture);
    localPendingFuture.setContext(localWriteTask);
    if (paramLong > 0L)
    {
      Future localFuture = iocp.schedule(new Runnable()
      {
        public void run()
        {
          localWriteTask.timeout();
        }
      }, paramLong, paramTimeUnit);
      localPendingFuture.setTimeoutTask(localFuture);
    }
    if (Iocp.supportsThreadAgnosticIo()) {
      localWriteTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localWriteTask);
    }
    return localPendingFuture;
  }
  
  private static native void initIDs();
  
  private static native int connect0(long paramLong1, boolean paramBoolean, InetAddress paramInetAddress, int paramInt, long paramLong2)
    throws IOException;
  
  private static native void updateConnectContext(long paramLong)
    throws IOException;
  
  private static native int read0(long paramLong1, int paramInt, long paramLong2, long paramLong3)
    throws IOException;
  
  private static native int write0(long paramLong1, int paramInt, long paramLong2, long paramLong3)
    throws IOException;
  
  private static native void shutdown0(long paramLong, int paramInt)
    throws IOException;
  
  private static native void closesocket0(long paramLong)
    throws IOException;
  
  static
  {
    IOUtil.load();
    initIDs();
  }
  
  private class ConnectTask<A>
    implements Runnable, Iocp.ResultHandler
  {
    private final InetSocketAddress remote;
    private final PendingFuture<Void, A> result;
    
    ConnectTask(PendingFuture<Void, A> paramPendingFuture)
    {
      remote = paramPendingFuture;
      PendingFuture localPendingFuture;
      result = localPendingFuture;
    }
    
    private void closeChannel()
    {
      try
      {
        close();
      }
      catch (IOException localIOException) {}
    }
    
    private IOException toIOException(Throwable paramThrowable)
    {
      if ((paramThrowable instanceof IOException))
      {
        if ((paramThrowable instanceof ClosedChannelException)) {
          paramThrowable = new AsynchronousCloseException();
        }
        return (IOException)paramThrowable;
      }
      return new IOException(paramThrowable);
    }
    
    private void afterConnect()
      throws IOException
    {
      WindowsAsynchronousSocketChannelImpl.updateConnectContext(handle);
      synchronized (stateLock)
      {
        state = 2;
        remoteAddress = remote;
      }
    }
    
    public void run()
    {
      long l = 0L;
      Object localObject1 = null;
      try
      {
        begin();
        synchronized (result)
        {
          l = ioCache.add(result);
          int i = WindowsAsynchronousSocketChannelImpl.connect0(handle, Net.isIPv6Available(), remote.getAddress(), remote.getPort(), l);
          if (i == -2) {
            return;
          }
          afterConnect();
          result.setResult(null);
        }
      }
      catch (Throwable localThrowable)
      {
        if (l != 0L) {
          ioCache.remove(l);
        }
        localObject1 = localThrowable;
      }
      finally
      {
        end();
      }
      if (localObject1 != null)
      {
        closeChannel();
        result.setFailure(toIOException((Throwable)localObject1));
      }
      Invoker.invoke(result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      Object localObject1 = null;
      try
      {
        begin();
        afterConnect();
        result.setResult(null);
      }
      catch (Throwable localThrowable)
      {
        localObject1 = localThrowable;
      }
      finally
      {
        end();
      }
      if (localObject1 != null)
      {
        closeChannel();
        result.setFailure(toIOException((Throwable)localObject1));
      }
      if (paramBoolean) {
        Invoker.invokeUnchecked(result);
      } else {
        Invoker.invoke(result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      if (isOpen())
      {
        closeChannel();
        result.setFailure(paramIOException);
      }
      else
      {
        result.setFailure(new AsynchronousCloseException());
      }
      Invoker.invoke(result);
    }
  }
  
  private class ReadTask<V, A>
    implements Runnable, Iocp.ResultHandler
  {
    private final ByteBuffer[] bufs;
    private final int numBufs;
    private final boolean scatteringRead;
    private final PendingFuture<V, A> result;
    private ByteBuffer[] shadow;
    
    ReadTask(boolean paramBoolean, PendingFuture<V, A> paramPendingFuture)
    {
      bufs = paramBoolean;
      numBufs = (paramBoolean.length > 16 ? 16 : paramBoolean.length);
      scatteringRead = paramPendingFuture;
      PendingFuture localPendingFuture;
      result = localPendingFuture;
    }
    
    void prepareBuffers()
    {
      shadow = new ByteBuffer[numBufs];
      long l1 = readBufferArray;
      for (int i = 0; i < numBufs; i++)
      {
        ByteBuffer localByteBuffer1 = bufs[i];
        int j = localByteBuffer1.position();
        int k = localByteBuffer1.limit();
        assert (j <= k);
        int m = j <= k ? k - j : 0;
        long l2;
        if (!(localByteBuffer1 instanceof DirectBuffer))
        {
          ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(m);
          shadow[i] = localByteBuffer2;
          l2 = ((DirectBuffer)localByteBuffer2).address();
        }
        else
        {
          shadow[i] = localByteBuffer1;
          l2 = ((DirectBuffer)localByteBuffer1).address() + j;
        }
        WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(l1 + WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, l2);
        WindowsAsynchronousSocketChannelImpl.unsafe.putInt(l1 + 0L, m);
        l1 += WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
      }
    }
    
    void updateBuffers(int paramInt)
    {
      for (int i = 0; i < numBufs; i++)
      {
        ByteBuffer localByteBuffer = shadow[i];
        int j = localByteBuffer.position();
        int k = localByteBuffer.remaining();
        int m;
        if (paramInt >= k)
        {
          paramInt -= k;
          m = j + k;
          try
          {
            localByteBuffer.position(m);
          }
          catch (IllegalArgumentException localIllegalArgumentException1) {}
        }
        else
        {
          if (paramInt <= 0) {
            break;
          }
          assert (j + paramInt < 2147483647L);
          m = j + paramInt;
          try
          {
            localByteBuffer.position(m);
          }
          catch (IllegalArgumentException localIllegalArgumentException2) {}
          break;
        }
      }
      for (i = 0; i < numBufs; i++) {
        if (!(bufs[i] instanceof DirectBuffer))
        {
          shadow[i].flip();
          try
          {
            bufs[i].put(shadow[i]);
          }
          catch (BufferOverflowException localBufferOverflowException) {}
        }
      }
    }
    
    void releaseBuffers()
    {
      for (int i = 0; i < numBufs; i++) {
        if (!(bufs[i] instanceof DirectBuffer)) {
          Util.releaseTemporaryDirectBuffer(shadow[i]);
        }
      }
    }
    
    public void run()
    {
      long l = 0L;
      int i = 0;
      int j = 0;
      try
      {
        begin();
        prepareBuffers();
        i = 1;
        l = ioCache.add(result);
        int k = WindowsAsynchronousSocketChannelImpl.read0(handle, numBufs, readBufferArray, l);
        if (k == -2)
        {
          j = 1;
          return;
        }
        if (k == -1)
        {
          enableReading();
          if (scatteringRead) {
            result.setResult(Long.valueOf(-1L));
          } else {
            result.setResult(Integer.valueOf(-1));
          }
        }
        else
        {
          throw new InternalError("Read completed immediately");
        }
      }
      catch (Throwable localThrowable)
      {
        enableReading();
        Object localObject1;
        if ((localThrowable instanceof ClosedChannelException)) {
          localObject1 = new AsynchronousCloseException();
        }
        if (!(localObject1 instanceof IOException)) {
          localObject1 = new IOException((Throwable)localObject1);
        }
        result.setFailure((Throwable)localObject1);
      }
      finally
      {
        if (j == 0)
        {
          if (l != 0L) {
            ioCache.remove(l);
          }
          if (i != 0) {
            releaseBuffers();
          }
        }
        end();
      }
      Invoker.invoke(result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      if (paramInt == 0) {
        paramInt = -1;
      } else {
        updateBuffers(paramInt);
      }
      releaseBuffers();
      synchronized (result)
      {
        if (result.isDone()) {
          return;
        }
        enableReading();
        if (scatteringRead) {
          result.setResult(Long.valueOf(paramInt));
        } else {
          result.setResult(Integer.valueOf(paramInt));
        }
      }
      if (paramBoolean) {
        Invoker.invokeUnchecked(result);
      } else {
        Invoker.invoke(result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      releaseBuffers();
      if (!isOpen()) {
        paramIOException = new AsynchronousCloseException();
      }
      synchronized (result)
      {
        if (result.isDone()) {
          return;
        }
        enableReading();
        result.setFailure(paramIOException);
      }
      Invoker.invoke(result);
    }
    
    void timeout()
    {
      synchronized (result)
      {
        if (result.isDone()) {
          return;
        }
        enableReading(true);
        result.setFailure(new InterruptedByTimeoutException());
      }
      Invoker.invoke(result);
    }
  }
  
  private class WriteTask<V, A>
    implements Runnable, Iocp.ResultHandler
  {
    private final ByteBuffer[] bufs;
    private final int numBufs;
    private final boolean gatheringWrite;
    private final PendingFuture<V, A> result;
    private ByteBuffer[] shadow;
    
    WriteTask(boolean paramBoolean, PendingFuture<V, A> paramPendingFuture)
    {
      bufs = paramBoolean;
      numBufs = (paramBoolean.length > 16 ? 16 : paramBoolean.length);
      gatheringWrite = paramPendingFuture;
      PendingFuture localPendingFuture;
      result = localPendingFuture;
    }
    
    void prepareBuffers()
    {
      shadow = new ByteBuffer[numBufs];
      long l1 = writeBufferArray;
      for (int i = 0; i < numBufs; i++)
      {
        ByteBuffer localByteBuffer1 = bufs[i];
        int j = localByteBuffer1.position();
        int k = localByteBuffer1.limit();
        assert (j <= k);
        int m = j <= k ? k - j : 0;
        long l2;
        if (!(localByteBuffer1 instanceof DirectBuffer))
        {
          ByteBuffer localByteBuffer2 = Util.getTemporaryDirectBuffer(m);
          localByteBuffer2.put(localByteBuffer1);
          localByteBuffer2.flip();
          localByteBuffer1.position(j);
          shadow[i] = localByteBuffer2;
          l2 = ((DirectBuffer)localByteBuffer2).address();
        }
        else
        {
          shadow[i] = localByteBuffer1;
          l2 = ((DirectBuffer)localByteBuffer1).address() + j;
        }
        WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(l1 + WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, l2);
        WindowsAsynchronousSocketChannelImpl.unsafe.putInt(l1 + 0L, m);
        l1 += WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
      }
    }
    
    void updateBuffers(int paramInt)
    {
      for (int i = 0; i < numBufs; i++)
      {
        ByteBuffer localByteBuffer = bufs[i];
        int j = localByteBuffer.position();
        int k = localByteBuffer.limit();
        int m = j <= k ? k - j : k;
        int n;
        if (paramInt >= m)
        {
          paramInt -= m;
          n = j + m;
          try
          {
            localByteBuffer.position(n);
          }
          catch (IllegalArgumentException localIllegalArgumentException1) {}
        }
        else
        {
          if (paramInt <= 0) {
            break;
          }
          assert (j + paramInt < 2147483647L);
          n = j + paramInt;
          try
          {
            localByteBuffer.position(n);
          }
          catch (IllegalArgumentException localIllegalArgumentException2) {}
          break;
        }
      }
    }
    
    void releaseBuffers()
    {
      for (int i = 0; i < numBufs; i++) {
        if (!(bufs[i] instanceof DirectBuffer)) {
          Util.releaseTemporaryDirectBuffer(shadow[i]);
        }
      }
    }
    
    public void run()
    {
      long l = 0L;
      int i = 0;
      int j = 0;
      int k = 0;
      try
      {
        begin();
        prepareBuffers();
        i = 1;
        l = ioCache.add(result);
        int m = WindowsAsynchronousSocketChannelImpl.write0(handle, numBufs, writeBufferArray, l);
        if (m == -2)
        {
          j = 1;
          return;
        }
        if (m == -1)
        {
          k = 1;
          throw new ClosedChannelException();
        }
        throw new InternalError("Write completed immediately");
      }
      catch (Throwable localThrowable)
      {
        enableWriting();
        Object localObject1;
        if ((k == 0) && ((localThrowable instanceof ClosedChannelException))) {
          localObject1 = new AsynchronousCloseException();
        }
        if (!(localObject1 instanceof IOException)) {
          localObject1 = new IOException((Throwable)localObject1);
        }
        result.setFailure((Throwable)localObject1);
      }
      finally
      {
        if (j == 0)
        {
          if (l != 0L) {
            ioCache.remove(l);
          }
          if (i != 0) {
            releaseBuffers();
          }
        }
        end();
      }
      Invoker.invoke(result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      updateBuffers(paramInt);
      releaseBuffers();
      synchronized (result)
      {
        if (result.isDone()) {
          return;
        }
        enableWriting();
        if (gatheringWrite) {
          result.setResult(Long.valueOf(paramInt));
        } else {
          result.setResult(Integer.valueOf(paramInt));
        }
      }
      if (paramBoolean) {
        Invoker.invokeUnchecked(result);
      } else {
        Invoker.invoke(result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      releaseBuffers();
      if (!isOpen()) {
        paramIOException = new AsynchronousCloseException();
      }
      synchronized (result)
      {
        if (result.isDone()) {
          return;
        }
        enableWriting();
        result.setFailure(paramIOException);
      }
      Invoker.invoke(result);
    }
    
    void timeout()
    {
      synchronized (result)
      {
        if (result.isDone()) {
          return;
        }
        enableWriting(true);
        result.setFailure(new InterruptedByTimeoutException());
      }
      Invoker.invoke(result);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\WindowsAsynchronousSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */