package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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

@InstrumentationTarget("sun.nio.ch.UnixAsynchronousSocketChannelImpl")
public class UnixAsynchronousSocketChannelImplRMHooks
{
  protected volatile InetSocketAddress localAddress = null;
  
  public UnixAsynchronousSocketChannelImplRMHooks() {}
  
  @InstrumentationMethod
  <A> Future<Void> implConnect(SocketAddress paramSocketAddress, A paramA, CompletionHandler<Void, ? super A> paramCompletionHandler)
  {
    int i = localAddress != null ? 1 : 0;
    if ((paramCompletionHandler != null) && (i == 0)) {
      paramCompletionHandler = new CompletionHandlerWrapper(paramCompletionHandler, this);
    }
    Object localObject1 = implConnect(paramSocketAddress, paramA, paramCompletionHandler);
    if ((localObject1 != null) && (i == 0)) {
      if (((Future)localObject1).isDone())
      {
        ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(localAddress);
        ResourceRequest localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
        long l = 0L;
        Object localObject2 = null;
        try
        {
          l = localResourceRequest.request(1L, localResourceIdImpl);
          if (l < 1L) {
            localObject2 = new ResourceRequestDeniedException("Resource limited: too many open sockets");
          }
        }
        catch (ResourceRequestDeniedException localResourceRequestDeniedException)
        {
          localObject2 = localResourceRequestDeniedException;
        }
        if (localObject2 != null)
        {
          localResourceRequest.request(-l, localResourceIdImpl);
          CompletableFuture localCompletableFuture = new CompletableFuture();
          localCompletableFuture.completeExceptionally((Throwable)localObject2);
          localObject1 = localCompletableFuture;
          try
          {
            implClose();
          }
          catch (IOException localIOException) {}
        }
        else
        {
          localResourceRequest.request(-(l - 1L), localResourceIdImpl);
        }
      }
      else
      {
        localObject1 = new FutureWrapper((Future)localObject1, this);
      }
    }
    return (Future<Void>)localObject1;
  }
  
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
    //   1: invokevirtual 148	jdk/management/resource/internal/inst/UnixAsynchronousSocketChannelImplRMHooks:implClose	()V
    //   4: aload_0
    //   5: getfield 133	jdk/management/resource/internal/inst/UnixAsynchronousSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   8: ifnull +74 -> 82
    //   11: aload_0
    //   12: getfield 133	jdk/management/resource/internal/inst/UnixAsynchronousSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   15: invokestatic 147	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   18: astore_1
    //   19: getstatic 130	jdk/management/resource/internal/ApproverGroup:SOCKET_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   22: aload_0
    //   23: invokevirtual 142	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   26: astore_2
    //   27: aload_2
    //   28: ldc2_w 62
    //   31: aload_1
    //   32: invokeinterface 155 4 0
    //   37: pop2
    //   38: goto +44 -> 82
    //   41: astore_3
    //   42: aload_0
    //   43: getfield 133	jdk/management/resource/internal/inst/UnixAsynchronousSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   46: ifnull +34 -> 80
    //   49: aload_0
    //   50: getfield 133	jdk/management/resource/internal/inst/UnixAsynchronousSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   53: invokestatic 147	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   56: astore 4
    //   58: getstatic 130	jdk/management/resource/internal/ApproverGroup:SOCKET_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   61: aload_0
    //   62: invokevirtual 142	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   65: astore 5
    //   67: aload 5
    //   69: ldc2_w 62
    //   72: aload 4
    //   74: invokeinterface 155 4 0
    //   79: pop2
    //   80: aload_3
    //   81: athrow
    //   82: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	UnixAsynchronousSocketChannelImplRMHooks
    //   18	14	1	localResourceIdImpl1	ResourceIdImpl
    //   26	2	2	localResourceRequest1	ResourceRequest
    //   41	40	3	localObject	Object
    //   56	17	4	localResourceIdImpl2	ResourceIdImpl
    //   65	3	5	localResourceRequest2	ResourceRequest
    // Exception table:
    //   from	to	target	type
    //   0	4	41	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\UnixAsynchronousSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */