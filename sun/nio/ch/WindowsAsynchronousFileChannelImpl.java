package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

public class WindowsAsynchronousFileChannelImpl
  extends AsynchronousFileChannelImpl
  implements Iocp.OverlappedChannel, Groupable
{
  private static final JavaIOFileDescriptorAccess fdAccess;
  private static final int ERROR_HANDLE_EOF = 38;
  private static final FileDispatcher nd;
  private final long handle;
  private final int completionKey;
  private final Iocp iocp;
  private final boolean isDefaultIocp;
  private final PendingIoCache ioCache;
  static final int NO_LOCK = -1;
  static final int LOCKED = 0;
  
  private WindowsAsynchronousFileChannelImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, Iocp paramIocp, boolean paramBoolean3)
    throws IOException
  {
    super(paramFileDescriptor, paramBoolean1, paramBoolean2, paramIocp.executor());
    handle = fdAccess.getHandle(paramFileDescriptor);
    iocp = paramIocp;
    isDefaultIocp = paramBoolean3;
    ioCache = new PendingIoCache();
    completionKey = paramIocp.associate(this, handle);
  }
  
  public static AsynchronousFileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ThreadPool paramThreadPool)
    throws IOException
  {
    Iocp localIocp;
    boolean bool;
    if (paramThreadPool == null)
    {
      localIocp = DefaultIocpHolder.defaultIocp;
      bool = true;
    }
    else
    {
      localIocp = new Iocp(null, paramThreadPool).start();
      bool = false;
    }
    try
    {
      return new WindowsAsynchronousFileChannelImpl(paramFileDescriptor, paramBoolean1, paramBoolean2, localIocp, bool);
    }
    catch (IOException localIOException)
    {
      if (!bool) {
        localIocp.implClose();
      }
      throw localIOException;
    }
  }
  
  public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong)
  {
    return ioCache.remove(paramLong);
  }
  
  public void close()
    throws IOException
  {
    closeLock.writeLock().lock();
    try
    {
      if (closed) {
        return;
      }
      closed = true;
    }
    finally
    {
      closeLock.writeLock().unlock();
    }
    invalidateAllLocks();
    close0(handle);
    ioCache.close();
    iocp.disassociate(completionKey);
    if (!isDefaultIocp) {
      iocp.detachFromThreadPool();
    }
  }
  
  public AsynchronousChannelGroupImpl group()
  {
    return iocp;
  }
  
  private static IOException toIOException(Throwable paramThrowable)
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
  
  public long size()
    throws IOException
  {
    try
    {
      begin();
      long l = nd.size(fdObj);
      return l;
    }
    finally
    {
      end();
    }
  }
  
  public AsynchronousFileChannel truncate(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative size");
    }
    if (!writing) {
      throw new NonWritableChannelException();
    }
    try
    {
      begin();
      if (paramLong > nd.size(fdObj))
      {
        WindowsAsynchronousFileChannelImpl localWindowsAsynchronousFileChannelImpl = this;
        return localWindowsAsynchronousFileChannelImpl;
      }
      nd.truncate(fdObj, paramLong);
    }
    finally
    {
      end();
    }
    return this;
  }
  
  /* Error */
  public void force(boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 338	sun/nio/ch/WindowsAsynchronousFileChannelImpl:begin	()V
    //   4: getstatic 294	sun/nio/ch/WindowsAsynchronousFileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
    //   7: aload_0
    //   8: getfield 291	sun/nio/ch/WindowsAsynchronousFileChannelImpl:fdObj	Ljava/io/FileDescriptor;
    //   11: iload_1
    //   12: invokevirtual 317	sun/nio/ch/FileDispatcher:force	(Ljava/io/FileDescriptor;Z)I
    //   15: pop
    //   16: aload_0
    //   17: invokevirtual 339	sun/nio/ch/WindowsAsynchronousFileChannelImpl:end	()V
    //   20: goto +10 -> 30
    //   23: astore_2
    //   24: aload_0
    //   25: invokevirtual 339	sun/nio/ch/WindowsAsynchronousFileChannelImpl:end	()V
    //   28: aload_2
    //   29: athrow
    //   30: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	WindowsAsynchronousFileChannelImpl
    //   0	31	1	paramBoolean	boolean
    //   23	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	16	23	finally
  }
  
  <A> Future<FileLock> implLock(long paramLong1, long paramLong2, boolean paramBoolean, A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler)
  {
    if ((paramBoolean) && (!reading)) {
      throw new NonReadableChannelException();
    }
    if ((!paramBoolean) && (!writing)) {
      throw new NonWritableChannelException();
    }
    FileLockImpl localFileLockImpl = addToFileLockTable(paramLong1, paramLong2, paramBoolean);
    if (localFileLockImpl == null)
    {
      localObject1 = new ClosedChannelException();
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure((Throwable)localObject1);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, (Throwable)localObject1);
      return null;
    }
    Object localObject1 = new PendingFuture(this, paramCompletionHandler, paramA);
    LockTask localLockTask = new LockTask(paramLong1, localFileLockImpl, (PendingFuture)localObject1);
    ((PendingFuture)localObject1).setContext(localLockTask);
    if (Iocp.supportsThreadAgnosticIo())
    {
      localLockTask.run();
    }
    else
    {
      int i = 0;
      try
      {
        Invoker.invokeOnThreadInThreadPool(this, localLockTask);
        i = 1;
      }
      finally
      {
        if (i == 0) {
          removeFromFileLockTable(localFileLockImpl);
        }
      }
    }
    return (Future<FileLock>)localObject1;
  }
  
  public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean)
    throws IOException
  {
    if ((paramBoolean) && (!reading)) {
      throw new NonReadableChannelException();
    }
    if ((!paramBoolean) && (!writing)) {
      throw new NonWritableChannelException();
    }
    FileLockImpl localFileLockImpl = addToFileLockTable(paramLong1, paramLong2, paramBoolean);
    if (localFileLockImpl == null) {
      throw new ClosedChannelException();
    }
    int i = 0;
    try
    {
      begin();
      int j = nd.lock(fdObj, false, paramLong1, paramLong2, paramBoolean);
      if (j == -1)
      {
        localObject1 = null;
        return (FileLock)localObject1;
      }
      i = 1;
      Object localObject1 = localFileLockImpl;
      return (FileLock)localObject1;
    }
    finally
    {
      if (i == 0) {
        removeFromFileLockTable(localFileLockImpl);
      }
      end();
    }
  }
  
  protected void implRelease(FileLockImpl paramFileLockImpl)
    throws IOException
  {
    nd.release(fdObj, paramFileLockImpl.position(), paramFileLockImpl.size());
  }
  
  <A> Future<Integer> implRead(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    if (!reading) {
      throw new NonReadableChannelException();
    }
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative position");
    }
    if (paramByteBuffer.isReadOnly()) {
      throw new IllegalArgumentException("Read-only buffer");
    }
    if (!isOpen())
    {
      ClosedChannelException localClosedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure(localClosedChannelException);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, localClosedChannelException);
      return null;
    }
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    if (k == 0)
    {
      if (paramCompletionHandler == null) {
        return CompletedFuture.withResult(Integer.valueOf(0));
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, Integer.valueOf(0), null);
      return null;
    }
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    ReadTask localReadTask = new ReadTask(paramByteBuffer, i, k, paramLong, localPendingFuture);
    localPendingFuture.setContext(localReadTask);
    if (Iocp.supportsThreadAgnosticIo()) {
      localReadTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localReadTask);
    }
    return localPendingFuture;
  }
  
  <A> Future<Integer> implWrite(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    if (!writing) {
      throw new NonWritableChannelException();
    }
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative position");
    }
    if (!isOpen())
    {
      ClosedChannelException localClosedChannelException = new ClosedChannelException();
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure(localClosedChannelException);
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, null, localClosedChannelException);
      return null;
    }
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    if (k == 0)
    {
      if (paramCompletionHandler == null) {
        return CompletedFuture.withResult(Integer.valueOf(0));
      }
      Invoker.invoke(this, paramCompletionHandler, paramA, Integer.valueOf(0), null);
      return null;
    }
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramA);
    WriteTask localWriteTask = new WriteTask(paramByteBuffer, i, k, paramLong, localPendingFuture);
    localPendingFuture.setContext(localWriteTask);
    if (Iocp.supportsThreadAgnosticIo()) {
      localWriteTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localWriteTask);
    }
    return localPendingFuture;
  }
  
  private static native int readFile(long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4)
    throws IOException;
  
  private static native int writeFile(long paramLong1, long paramLong2, int paramInt, long paramLong3, long paramLong4)
    throws IOException;
  
  private static native int lockFile(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4)
    throws IOException;
  
  private static native void close0(long paramLong);
  
  static
  {
    fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    nd = new FileDispatcherImpl();
    IOUtil.load();
  }
  
  private static class DefaultIocpHolder
  {
    static final Iocp defaultIocp = ;
    
    private DefaultIocpHolder() {}
    
    private static Iocp defaultIocp()
    {
      try
      {
        return new Iocp(null, ThreadPool.createDefault()).start();
      }
      catch (IOException localIOException)
      {
        throw new InternalError(localIOException);
      }
    }
  }
  
  private class LockTask<A>
    implements Runnable, Iocp.ResultHandler
  {
    private final long position;
    private final FileLockImpl fli;
    private final PendingFuture<FileLock, A> result;
    
    LockTask(FileLockImpl paramFileLockImpl, PendingFuture<FileLock, A> paramPendingFuture)
    {
      position = ???;
      fli = paramPendingFuture;
      PendingFuture localPendingFuture;
      result = localPendingFuture;
    }
    
    public void run()
    {
      long l = 0L;
      int i = 0;
      try
      {
        begin();
        l = ioCache.add(result);
        synchronized (result)
        {
          int j = WindowsAsynchronousFileChannelImpl.lockFile(handle, position, fli.size(), fli.isShared(), l);
          if (j == -2)
          {
            i = 1;
            if ((i == 0) && (l != 0L)) {
              ioCache.remove(l);
            }
            end();
            return;
          }
          result.setResult(fli);
        }
      }
      catch (Throwable localThrowable)
      {
        removeFromFileLockTable(fli);
        result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(localThrowable));
      }
      finally
      {
        if ((i == 0) && (l != 0L)) {
          ioCache.remove(l);
        }
        end();
      }
      Invoker.invoke(result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      result.setResult(fli);
      if (paramBoolean) {
        Invoker.invokeUnchecked(result);
      } else {
        Invoker.invoke(result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      removeFromFileLockTable(fli);
      if (isOpen()) {
        result.setFailure(paramIOException);
      } else {
        result.setFailure(new AsynchronousCloseException());
      }
      Invoker.invoke(result);
    }
  }
  
  private class ReadTask<A>
    implements Runnable, Iocp.ResultHandler
  {
    private final ByteBuffer dst;
    private final int pos;
    private final int rem;
    private final long position;
    private final PendingFuture<Integer, A> result;
    private volatile ByteBuffer buf;
    
    ReadTask(int paramInt1, int paramInt2, long paramLong, PendingFuture<Integer, A> paramPendingFuture)
    {
      dst = paramInt1;
      pos = paramInt2;
      rem = paramLong;
      position = ???;
      PendingFuture localPendingFuture;
      result = localPendingFuture;
    }
    
    void releaseBufferIfSubstituted()
    {
      if (buf != dst) {
        Util.releaseTemporaryDirectBuffer(buf);
      }
    }
    
    void updatePosition(int paramInt)
    {
      if (paramInt > 0) {
        if (buf == dst)
        {
          try
          {
            dst.position(pos + paramInt);
          }
          catch (IllegalArgumentException localIllegalArgumentException) {}
        }
        else
        {
          buf.position(paramInt).flip();
          try
          {
            dst.put(buf);
          }
          catch (BufferOverflowException localBufferOverflowException) {}
        }
      }
    }
    
    public void run()
    {
      int i = -1;
      long l1 = 0L;
      long l2;
      if ((dst instanceof DirectBuffer))
      {
        buf = dst;
        l2 = ((DirectBuffer)dst).address() + pos;
      }
      else
      {
        buf = Util.getTemporaryDirectBuffer(rem);
        l2 = ((DirectBuffer)buf).address();
      }
      int j = 0;
      try
      {
        begin();
        l1 = ioCache.add(result);
        i = WindowsAsynchronousFileChannelImpl.readFile(handle, l2, rem, position, l1);
        if (i == -2)
        {
          j = 1;
          return;
        }
        if (i == -1) {
          result.setResult(Integer.valueOf(i));
        } else {
          throw new InternalError("Unexpected result: " + i);
        }
      }
      catch (Throwable localThrowable)
      {
        result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(localThrowable));
      }
      finally
      {
        if (j == 0)
        {
          if (l1 != 0L) {
            ioCache.remove(l1);
          }
          releaseBufferIfSubstituted();
        }
        end();
      }
      Invoker.invoke(result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      updatePosition(paramInt);
      releaseBufferIfSubstituted();
      result.setResult(Integer.valueOf(paramInt));
      if (paramBoolean) {
        Invoker.invokeUnchecked(result);
      } else {
        Invoker.invoke(result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      if (paramInt == 38)
      {
        completed(-1, false);
      }
      else
      {
        releaseBufferIfSubstituted();
        if (isOpen()) {
          result.setFailure(paramIOException);
        } else {
          result.setFailure(new AsynchronousCloseException());
        }
        Invoker.invoke(result);
      }
    }
  }
  
  private class WriteTask<A>
    implements Runnable, Iocp.ResultHandler
  {
    private final ByteBuffer src;
    private final int pos;
    private final int rem;
    private final long position;
    private final PendingFuture<Integer, A> result;
    private volatile ByteBuffer buf;
    
    WriteTask(int paramInt1, int paramInt2, long paramLong, PendingFuture<Integer, A> paramPendingFuture)
    {
      src = paramInt1;
      pos = paramInt2;
      rem = paramLong;
      position = ???;
      PendingFuture localPendingFuture;
      result = localPendingFuture;
    }
    
    void releaseBufferIfSubstituted()
    {
      if (buf != src) {
        Util.releaseTemporaryDirectBuffer(buf);
      }
    }
    
    void updatePosition(int paramInt)
    {
      if (paramInt > 0) {
        try
        {
          src.position(pos + paramInt);
        }
        catch (IllegalArgumentException localIllegalArgumentException) {}
      }
    }
    
    public void run()
    {
      int i = -1;
      long l1 = 0L;
      long l2;
      if ((src instanceof DirectBuffer))
      {
        buf = src;
        l2 = ((DirectBuffer)src).address() + pos;
      }
      else
      {
        buf = Util.getTemporaryDirectBuffer(rem);
        buf.put(src);
        buf.flip();
        src.position(pos);
        l2 = ((DirectBuffer)buf).address();
      }
      try
      {
        begin();
        l1 = ioCache.add(result);
        i = WindowsAsynchronousFileChannelImpl.writeFile(handle, l2, rem, position, l1);
        if (i == -2) {
          return;
        }
        throw new InternalError("Unexpected result: " + i);
      }
      catch (Throwable localThrowable)
      {
        result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(localThrowable));
        if (l1 != 0L) {
          ioCache.remove(l1);
        }
        releaseBufferIfSubstituted();
      }
      finally
      {
        end();
      }
      Invoker.invoke(result);
    }
    
    public void completed(int paramInt, boolean paramBoolean)
    {
      updatePosition(paramInt);
      releaseBufferIfSubstituted();
      result.setResult(Integer.valueOf(paramInt));
      if (paramBoolean) {
        Invoker.invokeUnchecked(result);
      } else {
        Invoker.invoke(result);
      }
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      releaseBufferIfSubstituted();
      if (isOpen()) {
        result.setFailure(paramIOException);
      } else {
        result.setFailure(new AsynchronousCloseException());
      }
      Invoker.invoke(result);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\WindowsAsynchronousFileChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */