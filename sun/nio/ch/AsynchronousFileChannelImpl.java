package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

abstract class AsynchronousFileChannelImpl
  extends AsynchronousFileChannel
{
  protected final ReadWriteLock closeLock = new ReentrantReadWriteLock();
  protected volatile boolean closed;
  protected final FileDescriptor fdObj;
  protected final boolean reading;
  protected final boolean writing;
  protected final ExecutorService executor;
  private volatile FileLockTable fileLockTable;
  
  protected AsynchronousFileChannelImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ExecutorService paramExecutorService)
  {
    fdObj = paramFileDescriptor;
    reading = paramBoolean1;
    writing = paramBoolean2;
    executor = paramExecutorService;
  }
  
  final ExecutorService executor()
  {
    return executor;
  }
  
  public final boolean isOpen()
  {
    return !closed;
  }
  
  protected final void begin()
    throws IOException
  {
    closeLock.readLock().lock();
    if (closed) {
      throw new ClosedChannelException();
    }
  }
  
  protected final void end()
  {
    closeLock.readLock().unlock();
  }
  
  protected final void end(boolean paramBoolean)
    throws IOException
  {
    end();
    if ((!paramBoolean) && (!isOpen())) {
      throw new AsynchronousCloseException();
    }
  }
  
  abstract <A> Future<FileLock> implLock(long paramLong1, long paramLong2, boolean paramBoolean, A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler);
  
  public final Future<FileLock> lock(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    return implLock(paramLong1, paramLong2, paramBoolean, null, null);
  }
  
  public final <A> void lock(long paramLong1, long paramLong2, boolean paramBoolean, A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    implLock(paramLong1, paramLong2, paramBoolean, paramA, paramCompletionHandler);
  }
  
  final void ensureFileLockTableInitialized()
    throws IOException
  {
    if (fileLockTable == null) {
      synchronized (this)
      {
        if (fileLockTable == null) {
          fileLockTable = FileLockTable.newSharedFileLockTable(this, fdObj);
        }
      }
    }
  }
  
  final void invalidateAllLocks()
    throws IOException
  {
    if (fileLockTable != null)
    {
      Iterator localIterator = fileLockTable.removeAll().iterator();
      while (localIterator.hasNext())
      {
        FileLock localFileLock = (FileLock)localIterator.next();
        synchronized (localFileLock)
        {
          if (localFileLock.isValid())
          {
            FileLockImpl localFileLockImpl = (FileLockImpl)localFileLock;
            implRelease(localFileLockImpl);
            localFileLockImpl.invalidate();
          }
        }
      }
    }
  }
  
  protected final FileLockImpl addToFileLockTable(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    FileLockImpl localFileLockImpl1;
    try
    {
      closeLock.readLock().lock();
      if (closed)
      {
        FileLockImpl localFileLockImpl2 = null;
        return localFileLockImpl2;
      }
      try {}catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
      localFileLockImpl1 = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
      fileLockTable.add(localFileLockImpl1);
    }
    finally
    {
      end();
    }
    return localFileLockImpl1;
  }
  
  protected final void removeFromFileLockTable(FileLockImpl paramFileLockImpl)
  {
    fileLockTable.remove(paramFileLockImpl);
  }
  
  protected abstract void implRelease(FileLockImpl paramFileLockImpl)
    throws IOException;
  
  /* Error */
  final void release(FileLockImpl paramFileLockImpl)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 154	sun/nio/ch/AsynchronousFileChannelImpl:begin	()V
    //   4: aload_0
    //   5: aload_1
    //   6: invokevirtual 158	sun/nio/ch/AsynchronousFileChannelImpl:implRelease	(Lsun/nio/ch/FileLockImpl;)V
    //   9: aload_0
    //   10: aload_1
    //   11: invokevirtual 159	sun/nio/ch/AsynchronousFileChannelImpl:removeFromFileLockTable	(Lsun/nio/ch/FileLockImpl;)V
    //   14: aload_0
    //   15: invokevirtual 155	sun/nio/ch/AsynchronousFileChannelImpl:end	()V
    //   18: goto +10 -> 28
    //   21: astore_2
    //   22: aload_0
    //   23: invokevirtual 155	sun/nio/ch/AsynchronousFileChannelImpl:end	()V
    //   26: aload_2
    //   27: athrow
    //   28: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	29	0	this	AsynchronousFileChannelImpl
    //   0	29	1	paramFileLockImpl	FileLockImpl
    //   21	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	14	21	finally
  }
  
  abstract <A> Future<Integer> implRead(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public final Future<Integer> read(ByteBuffer paramByteBuffer, long paramLong)
  {
    return implRead(paramByteBuffer, paramLong, null, null);
  }
  
  public final <A> void read(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    implRead(paramByteBuffer, paramLong, paramA, paramCompletionHandler);
  }
  
  abstract <A> Future<Integer> implWrite(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public final Future<Integer> write(ByteBuffer paramByteBuffer, long paramLong)
  {
    return implWrite(paramByteBuffer, paramLong, null, null);
  }
  
  public final <A> void write(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    implWrite(paramByteBuffer, paramLong, paramA, paramCompletionHandler);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\AsynchronousFileChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */