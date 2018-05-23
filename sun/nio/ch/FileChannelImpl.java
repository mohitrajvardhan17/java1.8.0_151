package sun.nio.ch;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.WritableByteChannel;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.misc.Cleaner;
import sun.misc.JavaNioAccess.BufferPool;
import sun.security.action.GetPropertyAction;

public class FileChannelImpl
  extends FileChannel
{
  private static final long allocationGranularity = initIDs();
  private final FileDispatcher nd;
  private final FileDescriptor fd;
  private final boolean writable;
  private final boolean readable;
  private final boolean append;
  private final Object parent;
  private final String path;
  private final NativeThreadSet threads = new NativeThreadSet(2);
  private final Object positionLock = new Object();
  private static volatile boolean transferSupported;
  private static volatile boolean pipeSupported;
  private static volatile boolean fileSupported;
  private static final long MAPPED_TRANSFER_SIZE = 8388608L;
  private static final int TRANSFER_SIZE = 8192;
  private static final int MAP_RO = 0;
  private static final int MAP_RW = 1;
  private static final int MAP_PV = 2;
  private volatile FileLockTable fileLockTable;
  private static boolean isSharedFileLockTable;
  private static volatile boolean propertyChecked;
  
  private FileChannelImpl(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject)
  {
    fd = paramFileDescriptor;
    readable = paramBoolean1;
    writable = paramBoolean2;
    append = paramBoolean3;
    parent = paramObject;
    path = paramString;
    nd = new FileDispatcherImpl(paramBoolean3);
  }
  
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, Object paramObject)
  {
    return new FileChannelImpl(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, false, paramObject);
  }
  
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject)
  {
    return new FileChannelImpl(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, paramBoolean3, paramObject);
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (!isOpen()) {
      throw new ClosedChannelException();
    }
  }
  
  protected void implCloseChannel()
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
            nd.release(fd, localFileLock.position(), localFileLock.size());
            ((FileLockImpl)localFileLock).invalidate();
          }
        }
      }
    }
    threads.signalAndWait();
    if (parent != null) {
      ((Closeable)parent).close();
    } else {
      nd.close(fd);
    }
  }
  
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ensureOpen();
    if (!readable) {
      throw new NonReadableChannelException();
    }
    synchronized (positionLock)
    {
      int i = 0;
      int j = -1;
      try
      {
        begin();
        j = threads.add();
        if (!isOpen())
        {
          k = 0;
          threads.remove(j);
          end(i > 0);
          assert (IOStatus.check(i));
          return k;
        }
        do
        {
          i = IOUtil.read(fd, paramByteBuffer, -1L, nd);
        } while ((i == -3) && (isOpen()));
        int k = IOStatus.normalize(i);
        threads.remove(j);
        end(i > 0);
        assert (IOStatus.check(i));
        return k;
      }
      finally
      {
        threads.remove(j);
        end(i > 0);
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
    ensureOpen();
    if (!readable) {
      throw new NonReadableChannelException();
    }
    synchronized (positionLock)
    {
      long l1 = 0L;
      int i = -1;
      try
      {
        begin();
        i = threads.add();
        if (!isOpen())
        {
          l2 = 0L;
          threads.remove(i);
          end(l1 > 0L);
          assert (IOStatus.check(l1));
          return l2;
        }
        do
        {
          l1 = IOUtil.read(fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        threads.remove(i);
        end(l1 > 0L);
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        threads.remove(i);
        end(l1 > 0L);
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ensureOpen();
    if (!writable) {
      throw new NonWritableChannelException();
    }
    synchronized (positionLock)
    {
      int i = 0;
      int j = -1;
      try
      {
        begin();
        j = threads.add();
        if (!isOpen())
        {
          k = 0;
          threads.remove(j);
          end(i > 0);
          assert (IOStatus.check(i));
          return k;
        }
        do
        {
          i = IOUtil.write(fd, paramByteBuffer, -1L, nd);
        } while ((i == -3) && (isOpen()));
        int k = IOStatus.normalize(i);
        threads.remove(j);
        end(i > 0);
        assert (IOStatus.check(i));
        return k;
      }
      finally
      {
        threads.remove(j);
        end(i > 0);
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
    ensureOpen();
    if (!writable) {
      throw new NonWritableChannelException();
    }
    synchronized (positionLock)
    {
      long l1 = 0L;
      int i = -1;
      try
      {
        begin();
        i = threads.add();
        if (!isOpen())
        {
          l2 = 0L;
          threads.remove(i);
          end(l1 > 0L);
          assert (IOStatus.check(l1));
          return l2;
        }
        do
        {
          l1 = IOUtil.write(fd, paramArrayOfByteBuffer, paramInt1, paramInt2, nd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        threads.remove(i);
        end(l1 > 0L);
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        threads.remove(i);
        end(l1 > 0L);
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public long position()
    throws IOException
  {
    ensureOpen();
    synchronized (positionLock)
    {
      long l1 = -1L;
      int i = -1;
      try
      {
        begin();
        i = threads.add();
        if (!isOpen())
        {
          l2 = 0L;
          threads.remove(i);
          end(l1 > -1L);
          assert (IOStatus.check(l1));
          return l2;
        }
        do
        {
          l1 = append ? nd.size(fd) : position0(fd, -1L);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        threads.remove(i);
        end(l1 > -1L);
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        threads.remove(i);
        end(l1 > -1L);
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public FileChannel position(long paramLong)
    throws IOException
  {
    ensureOpen();
    if (paramLong < 0L) {
      throw new IllegalArgumentException();
    }
    synchronized (positionLock)
    {
      long l = -1L;
      int i = -1;
      try
      {
        begin();
        i = threads.add();
        if (!isOpen())
        {
          localObject1 = null;
          threads.remove(i);
          end(l > -1L);
          assert (IOStatus.check(l));
          return (FileChannel)localObject1;
        }
        do
        {
          l = position0(fd, paramLong);
        } while ((l == -3L) && (isOpen()));
        Object localObject1 = this;
        threads.remove(i);
        end(l > -1L);
        assert (IOStatus.check(l));
        return (FileChannel)localObject1;
      }
      finally
      {
        threads.remove(i);
        end(l > -1L);
        if ((!$assertionsDisabled) && (!IOStatus.check(l))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public long size()
    throws IOException
  {
    ensureOpen();
    synchronized (positionLock)
    {
      long l1 = -1L;
      int i = -1;
      try
      {
        begin();
        i = threads.add();
        if (!isOpen())
        {
          l2 = -1L;
          threads.remove(i);
          end(l1 > -1L);
          assert (IOStatus.check(l1));
          return l2;
        }
        do
        {
          l1 = nd.size(fd);
        } while ((l1 == -3L) && (isOpen()));
        long l2 = IOStatus.normalize(l1);
        threads.remove(i);
        end(l1 > -1L);
        assert (IOStatus.check(l1));
        return l2;
      }
      finally
      {
        threads.remove(i);
        end(l1 > -1L);
        if ((!$assertionsDisabled) && (!IOStatus.check(l1))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public FileChannel truncate(long paramLong)
    throws IOException
  {
    ensureOpen();
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative size");
    }
    if (!writable) {
      throw new NonWritableChannelException();
    }
    synchronized (positionLock)
    {
      int i = -1;
      long l1 = -1L;
      int j = -1;
      long l2 = -1L;
      try
      {
        begin();
        j = threads.add();
        if (!isOpen())
        {
          FileChannel localFileChannel = null;
          threads.remove(j);
          end(i > -1);
          assert (IOStatus.check(i));
          return localFileChannel;
        }
        long l3;
        do
        {
          l3 = nd.size(fd);
        } while ((l3 == -3L) && (isOpen()));
        if (!isOpen())
        {
          localObject1 = null;
          threads.remove(j);
          end(i > -1);
          assert (IOStatus.check(i));
          return (FileChannel)localObject1;
        }
        do
        {
          l1 = position0(fd, -1L);
        } while ((l1 == -3L) && (isOpen()));
        if (!isOpen())
        {
          localObject1 = null;
          threads.remove(j);
          end(i > -1);
          assert (IOStatus.check(i));
          return (FileChannel)localObject1;
        }
        assert (l1 >= 0L);
        if (paramLong < l3)
        {
          do
          {
            i = nd.truncate(fd, paramLong);
          } while ((i == -3) && (isOpen()));
          if (!isOpen())
          {
            localObject1 = null;
            threads.remove(j);
            end(i > -1);
            assert (IOStatus.check(i));
            return (FileChannel)localObject1;
          }
        }
        if (l1 > paramLong) {
          l1 = paramLong;
        }
        do
        {
          l2 = position0(fd, l1);
        } while ((l2 == -3L) && (isOpen()));
        Object localObject1 = this;
        threads.remove(j);
        end(i > -1);
        assert (IOStatus.check(i));
        return (FileChannel)localObject1;
      }
      finally
      {
        threads.remove(j);
        end(i > -1);
        if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
          throw new AssertionError();
        }
      }
    }
  }
  
  public void force(boolean paramBoolean)
    throws IOException
  {
    ensureOpen();
    int i = -1;
    int j = -1;
    try
    {
      begin();
      j = threads.add();
      if (!isOpen()) {
        return;
      }
      do
      {
        i = nd.force(fd, paramBoolean);
        if (i != -3) {
          break;
        }
      } while (isOpen());
    }
    finally
    {
      threads.remove(j);
      end(i > -1);
      if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
        throw new AssertionError();
      }
    }
  }
  
  private long transferToDirectlyInternal(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel, FileDescriptor paramFileDescriptor)
    throws IOException
  {
    assert ((!nd.transferToDirectlyNeedsPositionLock()) || (Thread.holdsLock(positionLock)));
    long l1 = -1L;
    int i = -1;
    try
    {
      begin();
      i = threads.add();
      if (!isOpen())
      {
        l2 = -1L;
        return l2;
      }
      do
      {
        l1 = transferTo0(fd, paramLong, paramInt, paramFileDescriptor);
      } while ((l1 == -3L) && (isOpen()));
      if (l1 == -6L)
      {
        if ((paramWritableByteChannel instanceof SinkChannelImpl)) {
          pipeSupported = false;
        }
        if ((paramWritableByteChannel instanceof FileChannelImpl)) {
          fileSupported = false;
        }
        l2 = -6L;
        return l2;
      }
      if (l1 == -4L)
      {
        transferSupported = false;
        l2 = -4L;
        return l2;
      }
      long l2 = IOStatus.normalize(l1);
      return l2;
    }
    finally
    {
      threads.remove(i);
      end(l1 > -1L);
    }
  }
  
  private long transferToDirectly(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    if (!transferSupported) {
      return -4L;
    }
    FileDescriptor localFileDescriptor = null;
    if ((paramWritableByteChannel instanceof FileChannelImpl))
    {
      if (!fileSupported) {
        return -6L;
      }
      localFileDescriptor = fd;
    }
    else if ((paramWritableByteChannel instanceof SelChImpl))
    {
      if (((paramWritableByteChannel instanceof SinkChannelImpl)) && (!pipeSupported)) {
        return -6L;
      }
      SelectableChannel localSelectableChannel = (SelectableChannel)paramWritableByteChannel;
      if (!nd.canTransferToDirectly(localSelectableChannel)) {
        return -6L;
      }
      localFileDescriptor = ((SelChImpl)paramWritableByteChannel).getFD();
    }
    if (localFileDescriptor == null) {
      return -4L;
    }
    int i = IOUtil.fdVal(fd);
    int j = IOUtil.fdVal(localFileDescriptor);
    if (i == j) {
      return -4L;
    }
    if (nd.transferToDirectlyNeedsPositionLock()) {
      synchronized (positionLock)
      {
        long l1 = position();
        try
        {
          long l2 = transferToDirectlyInternal(paramLong, paramInt, paramWritableByteChannel, localFileDescriptor);
          position(l1);
          return l2;
        }
        finally
        {
          position(l1);
        }
      }
    }
    return transferToDirectlyInternal(paramLong, paramInt, paramWritableByteChannel, localFileDescriptor);
  }
  
  private long transferToTrustedChannel(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    boolean bool = paramWritableByteChannel instanceof SelChImpl;
    if ((!(paramWritableByteChannel instanceof FileChannelImpl)) && (!bool)) {
      return -4L;
    }
    long l1 = paramLong2;
    while (l1 > 0L)
    {
      long l2 = Math.min(l1, 8388608L);
      try
      {
        MappedByteBuffer localMappedByteBuffer = map(FileChannel.MapMode.READ_ONLY, paramLong1, l2);
        try
        {
          int i = paramWritableByteChannel.write(localMappedByteBuffer);
          assert (i >= 0);
          l1 -= i;
          if (bool)
          {
            unmap(localMappedByteBuffer);
            break;
          }
          assert (i > 0);
          paramLong1 += i;
        }
        finally
        {
          unmap(localMappedByteBuffer);
        }
      }
      catch (ClosedByInterruptException localClosedByInterruptException)
      {
        assert (!paramWritableByteChannel.isOpen());
        try
        {
          close();
        }
        catch (Throwable localThrowable)
        {
          localClosedByInterruptException.addSuppressed(localThrowable);
        }
        throw localClosedByInterruptException;
      }
      catch (IOException localIOException)
      {
        if (l1 == paramLong2) {
          throw localIOException;
        }
        break;
      }
    }
    return paramLong2 - l1;
  }
  
  private long transferToArbitraryChannel(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    int i = Math.min(paramInt, 8192);
    ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(i);
    long l1 = 0L;
    long l2 = paramLong;
    try
    {
      Util.erase(localByteBuffer);
      while (l1 < paramInt)
      {
        localByteBuffer.limit(Math.min((int)(paramInt - l1), 8192));
        int j = read(localByteBuffer, l2);
        if (j <= 0) {
          break;
        }
        localByteBuffer.flip();
        int k = paramWritableByteChannel.write(localByteBuffer);
        l1 += k;
        if (k != j) {
          break;
        }
        l2 += k;
        localByteBuffer.clear();
      }
      long l3 = l1;
      return l3;
    }
    catch (IOException localIOException)
    {
      if (l1 > 0L)
      {
        long l4 = l1;
        return l4;
      }
      throw localIOException;
    }
    finally
    {
      Util.releaseTemporaryDirectBuffer(localByteBuffer);
    }
  }
  
  public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    ensureOpen();
    if (!paramWritableByteChannel.isOpen()) {
      throw new ClosedChannelException();
    }
    if (!readable) {
      throw new NonReadableChannelException();
    }
    if (((paramWritableByteChannel instanceof FileChannelImpl)) && (!writable)) {
      throw new NonWritableChannelException();
    }
    if ((paramLong1 < 0L) || (paramLong2 < 0L)) {
      throw new IllegalArgumentException();
    }
    long l1 = size();
    if (paramLong1 > l1) {
      return 0L;
    }
    int i = (int)Math.min(paramLong2, 2147483647L);
    if (l1 - paramLong1 < i) {
      i = (int)(l1 - paramLong1);
    }
    long l2;
    if ((l2 = transferToDirectly(paramLong1, i, paramWritableByteChannel)) >= 0L) {
      return l2;
    }
    if ((l2 = transferToTrustedChannel(paramLong1, i, paramWritableByteChannel)) >= 0L) {
      return l2;
    }
    return transferToArbitraryChannel(paramLong1, i, paramWritableByteChannel);
  }
  
  /* Error */
  private long transferFromFileChannel(FileChannelImpl paramFileChannelImpl, long paramLong1, long paramLong2)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 454	sun/nio/ch/FileChannelImpl:readable	Z
    //   4: ifne +11 -> 15
    //   7: new 250	java/nio/channels/NonReadableChannelException
    //   10: dup
    //   11: invokespecial 493	java/nio/channels/NonReadableChannelException:<init>	()V
    //   14: athrow
    //   15: aload_1
    //   16: getfield 459	sun/nio/ch/FileChannelImpl:positionLock	Ljava/lang/Object;
    //   19: dup
    //   20: astore 6
    //   22: monitorenter
    //   23: aload_1
    //   24: invokevirtual 498	sun/nio/ch/FileChannelImpl:position	()J
    //   27: lstore 7
    //   29: lload 4
    //   31: aload_1
    //   32: invokevirtual 499	sun/nio/ch/FileChannelImpl:size	()J
    //   35: lload 7
    //   37: lsub
    //   38: invokestatic 473	java/lang/Math:min	(JJ)J
    //   41: lstore 9
    //   43: lload 9
    //   45: lstore 11
    //   47: lload 7
    //   49: lstore 13
    //   51: lload 11
    //   53: lconst_0
    //   54: lcmp
    //   55: ifle +118 -> 173
    //   58: lload 11
    //   60: ldc2_w 221
    //   63: invokestatic 473	java/lang/Math:min	(JJ)J
    //   66: lstore 15
    //   68: aload_1
    //   69: getstatic 445	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   72: lload 13
    //   74: lload 15
    //   76: invokevirtual 524	sun/nio/ch/FileChannelImpl:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   79: astore 17
    //   81: aload_0
    //   82: aload 17
    //   84: lload_2
    //   85: invokevirtual 511	sun/nio/ch/FileChannelImpl:write	(Ljava/nio/ByteBuffer;J)I
    //   88: i2l
    //   89: lstore 18
    //   91: getstatic 448	sun/nio/ch/FileChannelImpl:$assertionsDisabled	Z
    //   94: ifne +18 -> 112
    //   97: lload 18
    //   99: lconst_0
    //   100: lcmp
    //   101: ifgt +11 -> 112
    //   104: new 229	java/lang/AssertionError
    //   107: dup
    //   108: invokespecial 467	java/lang/AssertionError:<init>	()V
    //   111: athrow
    //   112: lload 13
    //   114: lload 18
    //   116: ladd
    //   117: lstore 13
    //   119: lload_2
    //   120: lload 18
    //   122: ladd
    //   123: lstore_2
    //   124: lload 11
    //   126: lload 18
    //   128: lsub
    //   129: lstore 11
    //   131: aload 17
    //   133: invokestatic 513	sun/nio/ch/FileChannelImpl:unmap	(Ljava/nio/MappedByteBuffer;)V
    //   136: goto +34 -> 170
    //   139: astore 18
    //   141: lload 11
    //   143: lload 9
    //   145: lcmp
    //   146: ifne +6 -> 152
    //   149: aload 18
    //   151: athrow
    //   152: aload 17
    //   154: invokestatic 513	sun/nio/ch/FileChannelImpl:unmap	(Ljava/nio/MappedByteBuffer;)V
    //   157: goto +16 -> 173
    //   160: astore 20
    //   162: aload 17
    //   164: invokestatic 513	sun/nio/ch/FileChannelImpl:unmap	(Ljava/nio/MappedByteBuffer;)V
    //   167: aload 20
    //   169: athrow
    //   170: goto -119 -> 51
    //   173: lload 9
    //   175: lload 11
    //   177: lsub
    //   178: lstore 15
    //   180: aload_1
    //   181: lload 7
    //   183: lload 15
    //   185: ladd
    //   186: invokevirtual 514	sun/nio/ch/FileChannelImpl:position	(J)Ljava/nio/channels/FileChannel;
    //   189: pop
    //   190: lload 15
    //   192: aload 6
    //   194: monitorexit
    //   195: lreturn
    //   196: astore 21
    //   198: aload 6
    //   200: monitorexit
    //   201: aload 21
    //   203: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	204	0	this	FileChannelImpl
    //   0	204	1	paramFileChannelImpl	FileChannelImpl
    //   0	204	2	paramLong1	long
    //   0	204	4	paramLong2	long
    //   20	179	6	Ljava/lang/Object;	Object
    //   27	155	7	l1	long
    //   41	133	9	l2	long
    //   45	131	11	l3	long
    //   49	69	13	l4	long
    //   66	125	15	l5	long
    //   79	84	17	localMappedByteBuffer	MappedByteBuffer
    //   89	38	18	l6	long
    //   139	11	18	localIOException	IOException
    //   160	8	20	localObject1	Object
    //   196	6	21	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   81	131	139	java/io/IOException
    //   81	131	160	finally
    //   139	152	160	finally
    //   160	162	160	finally
    //   23	195	196	finally
    //   196	201	196	finally
  }
  
  private long transferFromArbitraryChannel(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2)
    throws IOException
  {
    int i = (int)Math.min(paramLong2, 8192L);
    ByteBuffer localByteBuffer = Util.getTemporaryDirectBuffer(i);
    long l1 = 0L;
    long l2 = paramLong1;
    try
    {
      Util.erase(localByteBuffer);
      while (l1 < paramLong2)
      {
        localByteBuffer.limit((int)Math.min(paramLong2 - l1, 8192L));
        int j = paramReadableByteChannel.read(localByteBuffer);
        if (j <= 0) {
          break;
        }
        localByteBuffer.flip();
        int k = write(localByteBuffer, l2);
        l1 += k;
        if (k != j) {
          break;
        }
        l2 += k;
        localByteBuffer.clear();
      }
      long l3 = l1;
      return l3;
    }
    catch (IOException localIOException)
    {
      if (l1 > 0L)
      {
        long l4 = l1;
        return l4;
      }
      throw localIOException;
    }
    finally
    {
      Util.releaseTemporaryDirectBuffer(localByteBuffer);
    }
  }
  
  public long transferFrom(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2)
    throws IOException
  {
    ensureOpen();
    if (!paramReadableByteChannel.isOpen()) {
      throw new ClosedChannelException();
    }
    if (!writable) {
      throw new NonWritableChannelException();
    }
    if ((paramLong1 < 0L) || (paramLong2 < 0L)) {
      throw new IllegalArgumentException();
    }
    if (paramLong1 > size()) {
      return 0L;
    }
    if ((paramReadableByteChannel instanceof FileChannelImpl)) {
      return transferFromFileChannel((FileChannelImpl)paramReadableByteChannel, paramLong1, paramLong2);
    }
    return transferFromArbitraryChannel(paramReadableByteChannel, paramLong1, paramLong2);
  }
  
  /* Error */
  public int read(ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +11 -> 12
    //   4: new 235	java/lang/NullPointerException
    //   7: dup
    //   8: invokespecial 474	java/lang/NullPointerException:<init>	()V
    //   11: athrow
    //   12: lload_2
    //   13: lconst_0
    //   14: lcmp
    //   15: ifge +13 -> 28
    //   18: new 231	java/lang/IllegalArgumentException
    //   21: dup
    //   22: ldc 4
    //   24: invokespecial 470	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   27: athrow
    //   28: aload_0
    //   29: getfield 454	sun/nio/ch/FileChannelImpl:readable	Z
    //   32: ifne +11 -> 43
    //   35: new 250	java/nio/channels/NonReadableChannelException
    //   38: dup
    //   39: invokespecial 493	java/nio/channels/NonReadableChannelException:<init>	()V
    //   42: athrow
    //   43: aload_0
    //   44: invokespecial 502	sun/nio/ch/FileChannelImpl:ensureOpen	()V
    //   47: aload_0
    //   48: getfield 461	sun/nio/ch/FileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
    //   51: invokevirtual 529	sun/nio/ch/FileDispatcher:needsPositionLock	()Z
    //   54: ifeq +29 -> 83
    //   57: aload_0
    //   58: getfield 459	sun/nio/ch/FileChannelImpl:positionLock	Ljava/lang/Object;
    //   61: dup
    //   62: astore 4
    //   64: monitorenter
    //   65: aload_0
    //   66: aload_1
    //   67: lload_2
    //   68: invokespecial 510	sun/nio/ch/FileChannelImpl:readInternal	(Ljava/nio/ByteBuffer;J)I
    //   71: aload 4
    //   73: monitorexit
    //   74: ireturn
    //   75: astore 5
    //   77: aload 4
    //   79: monitorexit
    //   80: aload 5
    //   82: athrow
    //   83: aload_0
    //   84: aload_1
    //   85: lload_2
    //   86: invokespecial 510	sun/nio/ch/FileChannelImpl:readInternal	(Ljava/nio/ByteBuffer;J)I
    //   89: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	this	FileChannelImpl
    //   0	90	1	paramByteBuffer	ByteBuffer
    //   0	90	2	paramLong	long
    //   62	16	4	Ljava/lang/Object;	Object
    //   75	6	5	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   65	74	75	finally
    //   75	80	75	finally
  }
  
  private int readInternal(ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    assert ((!nd.needsPositionLock()) || (Thread.holdsLock(positionLock)));
    int i = 0;
    int j = -1;
    try
    {
      begin();
      j = threads.add();
      if (!isOpen())
      {
        k = -1;
        return k;
      }
      do
      {
        i = IOUtil.read(fd, paramByteBuffer, paramLong, nd);
      } while ((i == -3) && (isOpen()));
      int k = IOStatus.normalize(i);
      return k;
    }
    finally
    {
      threads.remove(j);
      end(i > 0);
      if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
        throw new AssertionError();
      }
    }
  }
  
  /* Error */
  public int write(ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +11 -> 12
    //   4: new 235	java/lang/NullPointerException
    //   7: dup
    //   8: invokespecial 474	java/lang/NullPointerException:<init>	()V
    //   11: athrow
    //   12: lload_2
    //   13: lconst_0
    //   14: lcmp
    //   15: ifge +13 -> 28
    //   18: new 231	java/lang/IllegalArgumentException
    //   21: dup
    //   22: ldc 4
    //   24: invokespecial 470	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   27: athrow
    //   28: aload_0
    //   29: getfield 456	sun/nio/ch/FileChannelImpl:writable	Z
    //   32: ifne +11 -> 43
    //   35: new 251	java/nio/channels/NonWritableChannelException
    //   38: dup
    //   39: invokespecial 494	java/nio/channels/NonWritableChannelException:<init>	()V
    //   42: athrow
    //   43: aload_0
    //   44: invokespecial 502	sun/nio/ch/FileChannelImpl:ensureOpen	()V
    //   47: aload_0
    //   48: getfield 461	sun/nio/ch/FileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
    //   51: invokevirtual 529	sun/nio/ch/FileDispatcher:needsPositionLock	()Z
    //   54: ifeq +29 -> 83
    //   57: aload_0
    //   58: getfield 459	sun/nio/ch/FileChannelImpl:positionLock	Ljava/lang/Object;
    //   61: dup
    //   62: astore 4
    //   64: monitorenter
    //   65: aload_0
    //   66: aload_1
    //   67: lload_2
    //   68: invokespecial 512	sun/nio/ch/FileChannelImpl:writeInternal	(Ljava/nio/ByteBuffer;J)I
    //   71: aload 4
    //   73: monitorexit
    //   74: ireturn
    //   75: astore 5
    //   77: aload 4
    //   79: monitorexit
    //   80: aload 5
    //   82: athrow
    //   83: aload_0
    //   84: aload_1
    //   85: lload_2
    //   86: invokespecial 512	sun/nio/ch/FileChannelImpl:writeInternal	(Ljava/nio/ByteBuffer;J)I
    //   89: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	this	FileChannelImpl
    //   0	90	1	paramByteBuffer	ByteBuffer
    //   0	90	2	paramLong	long
    //   62	16	4	Ljava/lang/Object;	Object
    //   75	6	5	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   65	74	75	finally
    //   75	80	75	finally
  }
  
  private int writeInternal(ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    assert ((!nd.needsPositionLock()) || (Thread.holdsLock(positionLock)));
    int i = 0;
    int j = -1;
    try
    {
      begin();
      j = threads.add();
      if (!isOpen())
      {
        k = -1;
        return k;
      }
      do
      {
        i = IOUtil.write(fd, paramByteBuffer, paramLong, nd);
      } while ((i == -3) && (isOpen()));
      int k = IOStatus.normalize(i);
      return k;
    }
    finally
    {
      threads.remove(j);
      end(i > 0);
      if ((!$assertionsDisabled) && (!IOStatus.check(i))) {
        throw new AssertionError();
      }
    }
  }
  
  private static void unmap(MappedByteBuffer paramMappedByteBuffer)
  {
    Cleaner localCleaner = ((DirectBuffer)paramMappedByteBuffer).cleaner();
    if (localCleaner != null) {
      localCleaner.clean();
    }
  }
  
  public MappedByteBuffer map(FileChannel.MapMode paramMapMode, long paramLong1, long paramLong2)
    throws IOException
  {
    ensureOpen();
    if (paramMapMode == null) {
      throw new NullPointerException("Mode is null");
    }
    if (paramLong1 < 0L) {
      throw new IllegalArgumentException("Negative position");
    }
    if (paramLong2 < 0L) {
      throw new IllegalArgumentException("Negative size");
    }
    if (paramLong1 + paramLong2 < 0L) {
      throw new IllegalArgumentException("Position + size overflow");
    }
    if (paramLong2 > 2147483647L) {
      throw new IllegalArgumentException("Size exceeds Integer.MAX_VALUE");
    }
    int i = -1;
    if (paramMapMode == FileChannel.MapMode.READ_ONLY) {
      i = 0;
    } else if (paramMapMode == FileChannel.MapMode.READ_WRITE) {
      i = 1;
    } else if (paramMapMode == FileChannel.MapMode.PRIVATE) {
      i = 2;
    }
    assert (i >= 0);
    if ((paramMapMode != FileChannel.MapMode.READ_ONLY) && (!writable)) {
      throw new NonWritableChannelException();
    }
    if (!readable) {
      throw new NonReadableChannelException();
    }
    long l1 = -1L;
    int j = -1;
    try
    {
      begin();
      j = threads.add();
      if (!isOpen())
      {
        MappedByteBuffer localMappedByteBuffer1 = null;
        return localMappedByteBuffer1;
      }
      long l2;
      do
      {
        l2 = nd.size(fd);
      } while ((l2 == -3L) && (isOpen()));
      if (!isOpen())
      {
        MappedByteBuffer localMappedByteBuffer2 = null;
        return localMappedByteBuffer2;
      }
      MappedByteBuffer localMappedByteBuffer3;
      if (l2 < paramLong1 + paramLong2)
      {
        if (!writable) {
          throw new IOException("Channel not open for writing - cannot extend file to required size");
        }
        int k;
        do
        {
          k = nd.truncate(fd, paramLong1 + paramLong2);
        } while ((k == -3) && (isOpen()));
        if (!isOpen())
        {
          localMappedByteBuffer3 = null;
          return localMappedByteBuffer3;
        }
      }
      if (paramLong2 == 0L)
      {
        l1 = 0L;
        FileDescriptor localFileDescriptor1 = new FileDescriptor();
        if ((!writable) || (i == 0))
        {
          localMappedByteBuffer3 = Util.newMappedByteBufferR(0, 0L, localFileDescriptor1, null);
          return localMappedByteBuffer3;
        }
        localMappedByteBuffer3 = Util.newMappedByteBuffer(0, 0L, localFileDescriptor1, null);
        return localMappedByteBuffer3;
      }
      int m = (int)(paramLong1 % allocationGranularity);
      long l3 = paramLong1 - m;
      long l4 = paramLong2 + m;
      try
      {
        l1 = map0(i, l3, l4);
      }
      catch (OutOfMemoryError localOutOfMemoryError1)
      {
        System.gc();
        try
        {
          Thread.sleep(100L);
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
        }
        try
        {
          l1 = map0(i, l3, l4);
        }
        catch (OutOfMemoryError localOutOfMemoryError2)
        {
          throw new IOException("Map failed", localOutOfMemoryError2);
        }
      }
      FileDescriptor localFileDescriptor2;
      try
      {
        localFileDescriptor2 = nd.duplicateForMapping(fd);
      }
      catch (IOException localIOException)
      {
        unmap0(l1, l4);
        throw localIOException;
      }
      assert (IOStatus.checkAll(l1));
      assert (l1 % allocationGranularity == 0L);
      int n = (int)paramLong2;
      Unmapper localUnmapper = new Unmapper(l1, l4, n, localFileDescriptor2, null);
      if ((!writable) || (i == 0))
      {
        localMappedByteBuffer4 = Util.newMappedByteBufferR(n, l1 + m, localFileDescriptor2, localUnmapper);
        return localMappedByteBuffer4;
      }
      MappedByteBuffer localMappedByteBuffer4 = Util.newMappedByteBuffer(n, l1 + m, localFileDescriptor2, localUnmapper);
      return localMappedByteBuffer4;
    }
    finally
    {
      threads.remove(j);
      end(IOStatus.checkAll(l1));
    }
  }
  
  public static JavaNioAccess.BufferPool getMappedBufferPool()
  {
    new JavaNioAccess.BufferPool()
    {
      public String getName()
      {
        return "mapped";
      }
      
      public long getCount()
      {
        return FileChannelImpl.Unmapper.count;
      }
      
      public long getTotalCapacity()
      {
        return FileChannelImpl.Unmapper.totalCapacity;
      }
      
      public long getMemoryUsed()
      {
        return FileChannelImpl.Unmapper.totalSize;
      }
    };
  }
  
  private static boolean isSharedFileLockTable()
  {
    if (!propertyChecked) {
      synchronized (FileChannelImpl.class)
      {
        if (!propertyChecked)
        {
          String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.ch.disableSystemWideOverlappingFileLockCheck"));
          isSharedFileLockTable = (str == null) || (str.equals("false"));
          propertyChecked = true;
        }
      }
    }
    return isSharedFileLockTable;
  }
  
  /* Error */
  private FileLockTable fileLockTable()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 462	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
    //   4: ifnonnull +92 -> 96
    //   7: aload_0
    //   8: dup
    //   9: astore_1
    //   10: monitorenter
    //   11: aload_0
    //   12: getfield 462	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
    //   15: ifnonnull +69 -> 84
    //   18: invokestatic 504	sun/nio/ch/FileChannelImpl:isSharedFileLockTable	()Z
    //   21: ifeq +52 -> 73
    //   24: aload_0
    //   25: getfield 463	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
    //   28: invokevirtual 560	sun/nio/ch/NativeThreadSet:add	()I
    //   31: istore_2
    //   32: aload_0
    //   33: invokespecial 502	sun/nio/ch/FileChannelImpl:ensureOpen	()V
    //   36: aload_0
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 457	sun/nio/ch/FileChannelImpl:fd	Ljava/io/FileDescriptor;
    //   42: invokestatic 548	sun/nio/ch/FileLockTable:newSharedFileLockTable	(Ljava/nio/channels/Channel;Ljava/io/FileDescriptor;)Lsun/nio/ch/FileLockTable;
    //   45: putfield 462	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
    //   48: aload_0
    //   49: getfield 463	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
    //   52: iload_2
    //   53: invokevirtual 563	sun/nio/ch/NativeThreadSet:remove	(I)V
    //   56: goto +14 -> 70
    //   59: astore_3
    //   60: aload_0
    //   61: getfield 463	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
    //   64: iload_2
    //   65: invokevirtual 563	sun/nio/ch/NativeThreadSet:remove	(I)V
    //   68: aload_3
    //   69: athrow
    //   70: goto +14 -> 84
    //   73: aload_0
    //   74: new 263	sun/nio/ch/FileChannelImpl$SimpleFileLockTable
    //   77: dup
    //   78: invokespecial 527	sun/nio/ch/FileChannelImpl$SimpleFileLockTable:<init>	()V
    //   81: putfield 462	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
    //   84: aload_1
    //   85: monitorexit
    //   86: goto +10 -> 96
    //   89: astore 4
    //   91: aload_1
    //   92: monitorexit
    //   93: aload 4
    //   95: athrow
    //   96: aload_0
    //   97: getfield 462	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
    //   100: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	101	0	this	FileChannelImpl
    //   9	83	1	Ljava/lang/Object;	Object
    //   31	34	2	i	int
    //   59	10	3	localObject1	Object
    //   89	5	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   32	48	59	finally
    //   11	86	89	finally
    //   89	93	89	finally
  }
  
  public FileLock lock(long paramLong1, long paramLong2, boolean paramBoolean)
    throws IOException
  {
    ensureOpen();
    if ((paramBoolean) && (!readable)) {
      throw new NonReadableChannelException();
    }
    if ((!paramBoolean) && (!writable)) {
      throw new NonWritableChannelException();
    }
    localObject1 = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
    FileLockTable localFileLockTable = fileLockTable();
    localFileLockTable.add((FileLock)localObject1);
    boolean bool = false;
    int i = -1;
    try
    {
      begin();
      i = threads.add();
      if (!isOpen())
      {
        FileLock localFileLock = null;
        return localFileLock;
      }
      int j;
      do
      {
        j = nd.lock(fd, true, paramLong1, paramLong2, paramBoolean);
      } while ((j == 2) && (isOpen()));
      if (isOpen())
      {
        if (j == 1)
        {
          assert (paramBoolean);
          FileLockImpl localFileLockImpl = new FileLockImpl(this, paramLong1, paramLong2, false);
          localFileLockTable.replace((FileLock)localObject1, localFileLockImpl);
          localObject1 = localFileLockImpl;
        }
        bool = true;
      }
      return (FileLock)localObject1;
    }
    finally
    {
      if (!bool) {
        localFileLockTable.remove((FileLock)localObject1);
      }
      threads.remove(i);
      try
      {
        end(bool);
      }
      catch (ClosedByInterruptException localClosedByInterruptException3)
      {
        throw new FileLockInterruptionException();
      }
    }
  }
  
  public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean)
    throws IOException
  {
    ensureOpen();
    if ((paramBoolean) && (!readable)) {
      throw new NonReadableChannelException();
    }
    if ((!paramBoolean) && (!writable)) {
      throw new NonWritableChannelException();
    }
    FileLockImpl localFileLockImpl = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
    FileLockTable localFileLockTable = fileLockTable();
    localFileLockTable.add(localFileLockImpl);
    int j = threads.add();
    try
    {
      int i;
      try
      {
        ensureOpen();
        i = nd.lock(fd, false, paramLong1, paramLong2, paramBoolean);
      }
      catch (IOException localIOException)
      {
        localFileLockTable.remove(localFileLockImpl);
        throw localIOException;
      }
      if (i == -1)
      {
        localFileLockTable.remove(localFileLockImpl);
        localObject1 = null;
        return (FileLock)localObject1;
      }
      if (i == 1)
      {
        assert (paramBoolean);
        localObject1 = new FileLockImpl(this, paramLong1, paramLong2, false);
        localFileLockTable.replace(localFileLockImpl, (FileLock)localObject1);
        Object localObject2 = localObject1;
        return (FileLock)localObject2;
      }
      Object localObject1 = localFileLockImpl;
      return (FileLock)localObject1;
    }
    finally
    {
      threads.remove(j);
    }
  }
  
  /* Error */
  void release(FileLockImpl paramFileLockImpl)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 463	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
    //   4: invokevirtual 560	sun/nio/ch/NativeThreadSet:add	()I
    //   7: istore_2
    //   8: aload_0
    //   9: invokespecial 502	sun/nio/ch/FileChannelImpl:ensureOpen	()V
    //   12: aload_0
    //   13: getfield 461	sun/nio/ch/FileChannelImpl:nd	Lsun/nio/ch/FileDispatcher;
    //   16: aload_0
    //   17: getfield 457	sun/nio/ch/FileChannelImpl:fd	Ljava/io/FileDescriptor;
    //   20: aload_1
    //   21: invokevirtual 540	sun/nio/ch/FileLockImpl:position	()J
    //   24: aload_1
    //   25: invokevirtual 541	sun/nio/ch/FileLockImpl:size	()J
    //   28: invokevirtual 534	sun/nio/ch/FileDispatcher:release	(Ljava/io/FileDescriptor;JJ)V
    //   31: aload_0
    //   32: getfield 463	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
    //   35: iload_2
    //   36: invokevirtual 563	sun/nio/ch/NativeThreadSet:remove	(I)V
    //   39: goto +14 -> 53
    //   42: astore_3
    //   43: aload_0
    //   44: getfield 463	sun/nio/ch/FileChannelImpl:threads	Lsun/nio/ch/NativeThreadSet;
    //   47: iload_2
    //   48: invokevirtual 563	sun/nio/ch/NativeThreadSet:remove	(I)V
    //   51: aload_3
    //   52: athrow
    //   53: getstatic 448	sun/nio/ch/FileChannelImpl:$assertionsDisabled	Z
    //   56: ifne +18 -> 74
    //   59: aload_0
    //   60: getfield 462	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
    //   63: ifnonnull +11 -> 74
    //   66: new 229	java/lang/AssertionError
    //   69: dup
    //   70: invokespecial 467	java/lang/AssertionError:<init>	()V
    //   73: athrow
    //   74: aload_0
    //   75: getfield 462	sun/nio/ch/FileChannelImpl:fileLockTable	Lsun/nio/ch/FileLockTable;
    //   78: aload_1
    //   79: invokevirtual 545	sun/nio/ch/FileLockTable:remove	(Ljava/nio/channels/FileLock;)V
    //   82: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	FileChannelImpl
    //   0	83	1	paramFileLockImpl	FileLockImpl
    //   7	41	2	i	int
    //   42	10	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	31	42	finally
  }
  
  private native long map0(int paramInt, long paramLong1, long paramLong2)
    throws IOException;
  
  private static native int unmap0(long paramLong1, long paramLong2);
  
  private native long transferTo0(FileDescriptor paramFileDescriptor1, long paramLong1, long paramLong2, FileDescriptor paramFileDescriptor2);
  
  private native long position0(FileDescriptor paramFileDescriptor, long paramLong);
  
  private static native long initIDs();
  
  static
  {
    transferSupported = true;
    pipeSupported = true;
    fileSupported = true;
    IOUtil.load();
  }
  
  private static class SimpleFileLockTable
    extends FileLockTable
  {
    private final List<FileLock> lockList = new ArrayList(2);
    
    public SimpleFileLockTable() {}
    
    private void checkList(long paramLong1, long paramLong2)
      throws OverlappingFileLockException
    {
      assert (Thread.holdsLock(lockList));
      Iterator localIterator = lockList.iterator();
      while (localIterator.hasNext())
      {
        FileLock localFileLock = (FileLock)localIterator.next();
        if (localFileLock.overlaps(paramLong1, paramLong2)) {
          throw new OverlappingFileLockException();
        }
      }
    }
    
    public void add(FileLock paramFileLock)
      throws OverlappingFileLockException
    {
      synchronized (lockList)
      {
        checkList(paramFileLock.position(), paramFileLock.size());
        lockList.add(paramFileLock);
      }
    }
    
    public void remove(FileLock paramFileLock)
    {
      synchronized (lockList)
      {
        lockList.remove(paramFileLock);
      }
    }
    
    public List<FileLock> removeAll()
    {
      synchronized (lockList)
      {
        ArrayList localArrayList = new ArrayList(lockList);
        lockList.clear();
        return localArrayList;
      }
    }
    
    public void replace(FileLock paramFileLock1, FileLock paramFileLock2)
    {
      synchronized (lockList)
      {
        lockList.remove(paramFileLock1);
        lockList.add(paramFileLock2);
      }
    }
  }
  
  private static class Unmapper
    implements Runnable
  {
    private static final NativeDispatcher nd = new FileDispatcherImpl();
    static volatile int count;
    static volatile long totalSize;
    static volatile long totalCapacity;
    private volatile long address;
    private final long size;
    private final int cap;
    private final FileDescriptor fd;
    
    private Unmapper(long paramLong1, long paramLong2, int paramInt, FileDescriptor paramFileDescriptor)
    {
      assert (paramLong1 != 0L);
      address = paramLong1;
      size = paramLong2;
      cap = paramInt;
      fd = paramFileDescriptor;
      synchronized (Unmapper.class)
      {
        count += 1;
        totalSize += paramLong2;
        totalCapacity += paramInt;
      }
    }
    
    public void run()
    {
      if (address == 0L) {
        return;
      }
      FileChannelImpl.unmap0(address, size);
      address = 0L;
      if (fd.valid()) {
        try
        {
          nd.close(fd);
        }
        catch (IOException localIOException) {}
      }
      synchronized (Unmapper.class)
      {
        count -= 1;
        totalSize -= size;
        totalCapacity -= cap;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\FileChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */