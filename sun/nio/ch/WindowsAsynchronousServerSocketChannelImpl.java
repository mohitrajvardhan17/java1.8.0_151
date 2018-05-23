package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NotYetBoundException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.misc.Unsafe;

class WindowsAsynchronousServerSocketChannelImpl
  extends AsynchronousServerSocketChannelImpl
  implements Iocp.OverlappedChannel
{
  private static final Unsafe unsafe = ;
  private static final int DATA_BUFFER_SIZE = 88;
  private final long handle;
  private final int completionKey;
  private final Iocp iocp;
  private final PendingIoCache ioCache;
  private final long dataBuffer;
  private AtomicBoolean accepting = new AtomicBoolean();
  
  WindowsAsynchronousServerSocketChannelImpl(Iocp paramIocp)
    throws IOException
  {
    super(paramIocp);
    long l = IOUtil.fdVal(fd);
    int i;
    try
    {
      i = paramIocp.associate(this, l);
    }
    catch (IOException localIOException)
    {
      closesocket0(l);
      throw localIOException;
    }
    handle = l;
    completionKey = i;
    iocp = paramIocp;
    ioCache = new PendingIoCache();
    dataBuffer = unsafe.allocateMemory(88L);
  }
  
  public <V, A> PendingFuture<V, A> getByOverlapped(long paramLong)
  {
    return ioCache.remove(paramLong);
  }
  
  void implClose()
    throws IOException
  {
    closesocket0(handle);
    ioCache.close();
    iocp.disassociate(completionKey);
    unsafe.freeMemory(dataBuffer);
  }
  
  public AsynchronousChannelGroupImpl group()
  {
    return iocp;
  }
  
  Future<AsynchronousSocketChannel> implAccept(Object paramObject, CompletionHandler<AsynchronousSocketChannel, Object> paramCompletionHandler)
  {
    if (!isOpen())
    {
      localObject1 = new ClosedChannelException();
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure((Throwable)localObject1);
      }
      Invoker.invokeIndirectly(this, paramCompletionHandler, paramObject, null, (Throwable)localObject1);
      return null;
    }
    if (isAcceptKilled()) {
      throw new RuntimeException("Accept not allowed due to cancellation");
    }
    if (localAddress == null) {
      throw new NotYetBoundException();
    }
    Object localObject1 = null;
    Object localObject2 = null;
    try
    {
      begin();
      localObject1 = new WindowsAsynchronousSocketChannelImpl(iocp, false);
    }
    catch (IOException localIOException)
    {
      localObject2 = localIOException;
    }
    finally
    {
      end();
    }
    if (localObject2 != null)
    {
      if (paramCompletionHandler == null) {
        return CompletedFuture.withFailure((Throwable)localObject2);
      }
      Invoker.invokeIndirectly(this, paramCompletionHandler, paramObject, null, (Throwable)localObject2);
      return null;
    }
    AccessControlContext localAccessControlContext = System.getSecurityManager() == null ? null : AccessController.getContext();
    PendingFuture localPendingFuture = new PendingFuture(this, paramCompletionHandler, paramObject);
    AcceptTask localAcceptTask = new AcceptTask((WindowsAsynchronousSocketChannelImpl)localObject1, localAccessControlContext, localPendingFuture);
    localPendingFuture.setContext(localAcceptTask);
    if (!accepting.compareAndSet(false, true)) {
      throw new AcceptPendingException();
    }
    if (Iocp.supportsThreadAgnosticIo()) {
      localAcceptTask.run();
    } else {
      Invoker.invokeOnThreadInThreadPool(this, localAcceptTask);
    }
    return localPendingFuture;
  }
  
  private static native void initIDs();
  
  private static native int accept0(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    throws IOException;
  
  private static native void updateAcceptContext(long paramLong1, long paramLong2)
    throws IOException;
  
  private static native void closesocket0(long paramLong)
    throws IOException;
  
  static
  {
    IOUtil.load();
    initIDs();
  }
  
  private class AcceptTask
    implements Runnable, Iocp.ResultHandler
  {
    private final WindowsAsynchronousSocketChannelImpl channel;
    private final AccessControlContext acc;
    private final PendingFuture<AsynchronousSocketChannel, Object> result;
    
    AcceptTask(AccessControlContext paramAccessControlContext, PendingFuture<AsynchronousSocketChannel, Object> paramPendingFuture)
    {
      channel = paramAccessControlContext;
      acc = paramPendingFuture;
      PendingFuture localPendingFuture;
      result = localPendingFuture;
    }
    
    void enableAccept()
    {
      accepting.set(false);
    }
    
    void closeChildChannel()
    {
      try
      {
        channel.close();
      }
      catch (IOException localIOException) {}
    }
    
    void finishAccept()
      throws IOException
    {
      WindowsAsynchronousServerSocketChannelImpl.updateAcceptContext(handle, channel.handle());
      InetSocketAddress localInetSocketAddress1 = Net.localAddress(channel.fd);
      final InetSocketAddress localInetSocketAddress2 = Net.remoteAddress(channel.fd);
      channel.setConnected(localInetSocketAddress1, localInetSocketAddress2);
      if (acc != null) {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            SecurityManager localSecurityManager = System.getSecurityManager();
            localSecurityManager.checkAccept(localInetSocketAddress2.getAddress().getHostAddress(), localInetSocketAddress2.getPort());
            return null;
          }
        }, acc);
      }
    }
    
    public void run()
    {
      long l = 0L;
      try
      {
        begin();
        try
        {
          channel.begin();
          synchronized (result)
          {
            l = ioCache.add(result);
            int i = WindowsAsynchronousServerSocketChannelImpl.accept0(handle, channel.handle(), l, dataBuffer);
            if (i == -2)
            {
              channel.end();
              return;
            }
            finishAccept();
            enableAccept();
            result.setResult(channel);
          }
        }
        finally
        {
          channel.end();
        }
      }
      catch (Throwable localThrowable)
      {
        if (l != 0L) {
          ioCache.remove(l);
        }
        closeChildChannel();
        Object localObject1;
        if ((localThrowable instanceof ClosedChannelException)) {
          localObject1 = new AsynchronousCloseException();
        }
        if ((!(localObject1 instanceof IOException)) && (!(localObject1 instanceof SecurityException))) {
          localObject1 = new IOException((Throwable)localObject1);
        }
        enableAccept();
        result.setFailure((Throwable)localObject1);
      }
      finally
      {
        end();
      }
      if (result.isCancelled()) {
        closeChildChannel();
      }
      Invoker.invokeIndirectly(result);
    }
    
    /* Error */
    public void completed(int paramInt, boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 150	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:this$0	Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;
      //   4: invokestatic 176	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl:access$600	(Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;)Lsun/nio/ch/Iocp;
      //   7: invokevirtual 160	sun/nio/ch/Iocp:isShutdown	()Z
      //   10: ifeq +18 -> 28
      //   13: new 71	java/io/IOException
      //   16: dup
      //   17: new 79	java/nio/channels/ShutdownChannelGroupException
      //   20: dup
      //   21: invokespecial 156	java/nio/channels/ShutdownChannelGroupException:<init>	()V
      //   24: invokespecial 153	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
      //   27: athrow
      //   28: aload_0
      //   29: getfield 150	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:this$0	Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;
      //   32: invokevirtual 168	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl:begin	()V
      //   35: aload_0
      //   36: getfield 151	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:channel	Lsun/nio/ch/WindowsAsynchronousSocketChannelImpl;
      //   39: invokevirtual 183	sun/nio/ch/WindowsAsynchronousSocketChannelImpl:begin	()V
      //   42: aload_0
      //   43: invokevirtual 180	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:finishAccept	()V
      //   46: aload_0
      //   47: getfield 151	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:channel	Lsun/nio/ch/WindowsAsynchronousSocketChannelImpl;
      //   50: invokevirtual 185	sun/nio/ch/WindowsAsynchronousSocketChannelImpl:end	()V
      //   53: goto +13 -> 66
      //   56: astore_3
      //   57: aload_0
      //   58: getfield 151	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:channel	Lsun/nio/ch/WindowsAsynchronousSocketChannelImpl;
      //   61: invokevirtual 185	sun/nio/ch/WindowsAsynchronousSocketChannelImpl:end	()V
      //   64: aload_3
      //   65: athrow
      //   66: aload_0
      //   67: getfield 150	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:this$0	Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;
      //   70: invokevirtual 169	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl:end	()V
      //   73: goto +15 -> 88
      //   76: astore 4
      //   78: aload_0
      //   79: getfield 150	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:this$0	Lsun/nio/ch/WindowsAsynchronousServerSocketChannelImpl;
      //   82: invokevirtual 169	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl:end	()V
      //   85: aload 4
      //   87: athrow
      //   88: aload_0
      //   89: invokevirtual 179	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:enableAccept	()V
      //   92: aload_0
      //   93: getfield 149	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:result	Lsun/nio/ch/PendingFuture;
      //   96: aload_0
      //   97: getfield 151	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:channel	Lsun/nio/ch/WindowsAsynchronousSocketChannelImpl;
      //   100: invokevirtual 164	sun/nio/ch/PendingFuture:setResult	(Ljava/lang/Object;)V
      //   103: goto +58 -> 161
      //   106: astore_3
      //   107: aload_0
      //   108: invokevirtual 179	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:enableAccept	()V
      //   111: aload_0
      //   112: invokevirtual 178	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:closeChildChannel	()V
      //   115: aload_3
      //   116: instanceof 78
      //   119: ifeq +11 -> 130
      //   122: new 77	java/nio/channels/AsynchronousCloseException
      //   125: dup
      //   126: invokespecial 155	java/nio/channels/AsynchronousCloseException:<init>	()V
      //   129: astore_3
      //   130: aload_3
      //   131: instanceof 71
      //   134: ifne +19 -> 153
      //   137: aload_3
      //   138: instanceof 74
      //   141: ifne +12 -> 153
      //   144: new 71	java/io/IOException
      //   147: dup
      //   148: aload_3
      //   149: invokespecial 153	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
      //   152: astore_3
      //   153: aload_0
      //   154: getfield 149	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:result	Lsun/nio/ch/PendingFuture;
      //   157: aload_3
      //   158: invokevirtual 165	sun/nio/ch/PendingFuture:setFailure	(Ljava/lang/Throwable;)V
      //   161: aload_0
      //   162: getfield 149	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:result	Lsun/nio/ch/PendingFuture;
      //   165: invokevirtual 163	sun/nio/ch/PendingFuture:isCancelled	()Z
      //   168: ifeq +7 -> 175
      //   171: aload_0
      //   172: invokevirtual 178	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:closeChildChannel	()V
      //   175: aload_0
      //   176: getfield 149	sun/nio/ch/WindowsAsynchronousServerSocketChannelImpl$AcceptTask:result	Lsun/nio/ch/PendingFuture;
      //   179: invokestatic 159	sun/nio/ch/Invoker:invokeIndirectly	(Lsun/nio/ch/PendingFuture;)V
      //   182: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	183	0	this	AcceptTask
      //   0	183	1	paramInt	int
      //   0	183	2	paramBoolean	boolean
      //   56	9	3	localObject1	Object
      //   106	10	3	localThrowable	Throwable
      //   129	29	3	localObject2	Object
      //   76	10	4	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   35	46	56	finally
      //   28	66	76	finally
      //   76	78	76	finally
      //   0	103	106	java/lang/Throwable
    }
    
    public void failed(int paramInt, IOException paramIOException)
    {
      enableAccept();
      closeChildChannel();
      if (isOpen()) {
        result.setFailure(paramIOException);
      } else {
        result.setFailure(new AsynchronousCloseException());
      }
      Invoker.invokeIndirectly(result);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\WindowsAsynchronousServerSocketChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */