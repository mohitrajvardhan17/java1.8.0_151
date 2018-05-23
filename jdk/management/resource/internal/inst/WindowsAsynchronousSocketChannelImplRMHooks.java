package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.WindowsAsynchronousSocketChannelImpl")
public class WindowsAsynchronousSocketChannelImplRMHooks
{
  protected final FileDescriptor fd = null;
  protected volatile InetSocketAddress localAddress = null;
  
  public WindowsAsynchronousSocketChannelImplRMHooks() {}
  
  public final void close()
    throws IOException
  {}
  
  @InstrumentationMethod
  <V extends Number, A> Future<V> implRead(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(localAddress);
    ResourceRequest localResourceRequest = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
    long l = 0L;
    int i;
    if (paramBoolean)
    {
      i = 0;
      for (ByteBuffer localByteBuffer : paramArrayOfByteBuffer) {
        i += localByteBuffer.remaining();
      }
    }
    else
    {
      i = paramByteBuffer.remaining();
    }
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      if (paramCompletionHandler != null)
      {
        paramCompletionHandler.failed(localResourceRequestDeniedException, paramA);
        return null;
      }
      CompletableFuture localCompletableFuture = new CompletableFuture();
      localCompletableFuture.completeExceptionally(localResourceRequestDeniedException);
      return localCompletableFuture;
    }
    if (paramCompletionHandler != null) {
      paramCompletionHandler = new CompletionHandlerWrapper(paramCompletionHandler, localResourceIdImpl, localResourceRequest, l);
    }
    Object localObject = implRead(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
    if (paramCompletionHandler == null) {
      if (((Future)localObject).isDone())
      {
        int k = 0;
        try
        {
          k = ((Number)((Future)localObject).get()).intValue();
        }
        catch (InterruptedException|ExecutionException localInterruptedException) {}
        k = Math.max(0, k);
        localResourceRequest.request(-(l - k), localResourceIdImpl);
      }
      else
      {
        localObject = new FutureWrapper((Future)localObject, localResourceIdImpl, localResourceRequest, l);
      }
    }
    return (Future<V>)localObject;
  }
  
  @InstrumentationMethod
  <V extends Number, A> Future<V> implWrite(boolean paramBoolean, ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, long paramLong, TimeUnit paramTimeUnit, A paramA, CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(localAddress);
    ResourceRequest localResourceRequest = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
    long l = 0L;
    int i;
    if (paramBoolean)
    {
      i = 0;
      for (ByteBuffer localByteBuffer : paramArrayOfByteBuffer) {
        i += localByteBuffer.remaining();
      }
    }
    else
    {
      i = paramByteBuffer.remaining();
    }
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      if (paramCompletionHandler != null)
      {
        paramCompletionHandler.failed(localResourceRequestDeniedException, paramA);
        return null;
      }
      CompletableFuture localCompletableFuture = new CompletableFuture();
      localCompletableFuture.completeExceptionally(localResourceRequestDeniedException);
      return localCompletableFuture;
    }
    if (paramCompletionHandler != null) {
      paramCompletionHandler = new CompletionHandlerWrapper(paramCompletionHandler, localResourceIdImpl, localResourceRequest, l);
    }
    Object localObject = implWrite(paramBoolean, paramByteBuffer, paramArrayOfByteBuffer, paramLong, paramTimeUnit, paramA, paramCompletionHandler);
    if (paramCompletionHandler == null) {
      if (((Future)localObject).isDone())
      {
        int k = 0;
        try
        {
          k = ((Number)((Future)localObject).get()).intValue();
        }
        catch (InterruptedException|ExecutionException localInterruptedException) {}
        k = Math.max(0, k);
        localResourceRequest.request(-(l - k), localResourceIdImpl);
      }
      else
      {
        localObject = new FutureWrapper((Future)localObject, localResourceIdImpl, localResourceRequest, l);
      }
    }
    return (Future<V>)localObject;
  }
  
  /* Error */
  @InstrumentationMethod
  void implClose()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 143	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:implClose	()V
    //   4: aload_0
    //   5: getfield 128	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   8: invokestatic 141	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/io/FileDescriptor;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   11: astore_1
    //   12: aload_1
    //   13: ifnull +25 -> 38
    //   16: getstatic 124	jdk/management/resource/internal/ApproverGroup:FILEDESCRIPTOR_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   19: aload_0
    //   20: getfield 128	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   23: invokevirtual 138	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   26: astore_2
    //   27: aload_2
    //   28: ldc2_w 60
    //   31: aload_1
    //   32: invokeinterface 149 4 0
    //   37: pop2
    //   38: aload_0
    //   39: getfield 129	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   42: ifnull +30 -> 72
    //   45: aload_0
    //   46: getfield 129	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   49: invokestatic 142	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   52: astore_1
    //   53: getstatic 125	jdk/management/resource/internal/ApproverGroup:SOCKET_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   56: aload_0
    //   57: invokevirtual 138	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   60: astore_2
    //   61: aload_2
    //   62: ldc2_w 60
    //   65: aload_1
    //   66: invokeinterface 149 4 0
    //   71: pop2
    //   72: goto +83 -> 155
    //   75: astore_3
    //   76: aload_0
    //   77: getfield 128	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   80: invokestatic 141	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/io/FileDescriptor;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   83: astore 4
    //   85: aload 4
    //   87: ifnull +28 -> 115
    //   90: getstatic 124	jdk/management/resource/internal/ApproverGroup:FILEDESCRIPTOR_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   93: aload_0
    //   94: getfield 128	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   97: invokevirtual 138	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   100: astore 5
    //   102: aload 5
    //   104: ldc2_w 60
    //   107: aload 4
    //   109: invokeinterface 149 4 0
    //   114: pop2
    //   115: aload_0
    //   116: getfield 129	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   119: ifnull +34 -> 153
    //   122: aload_0
    //   123: getfield 129	jdk/management/resource/internal/inst/WindowsAsynchronousSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   126: invokestatic 142	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   129: astore 4
    //   131: getstatic 125	jdk/management/resource/internal/ApproverGroup:SOCKET_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   134: aload_0
    //   135: invokevirtual 138	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   138: astore 5
    //   140: aload 5
    //   142: ldc2_w 60
    //   145: aload 4
    //   147: invokeinterface 149 4 0
    //   152: pop2
    //   153: aload_3
    //   154: athrow
    //   155: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	156	0	this	WindowsAsynchronousSocketChannelImplRMHooks
    //   11	55	1	localResourceIdImpl1	ResourceIdImpl
    //   26	36	2	localResourceRequest1	ResourceRequest
    //   75	79	3	localObject	Object
    //   83	63	4	localResourceIdImpl2	ResourceIdImpl
    //   100	41	5	localResourceRequest2	ResourceRequest
    // Exception table:
    //   from	to	target	type
    //   0	4	75	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\WindowsAsynchronousSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */