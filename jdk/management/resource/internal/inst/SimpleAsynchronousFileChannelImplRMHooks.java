package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.ThreadPool;

@InstrumentationTarget("sun.nio.ch.SimpleAsynchronousFileChannelImpl")
public final class SimpleAsynchronousFileChannelImplRMHooks
{
  protected final FileDescriptor fdObj = null;
  protected volatile boolean closed;
  
  public SimpleAsynchronousFileChannelImplRMHooks() {}
  
  @InstrumentationMethod
  public static AsynchronousFileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ThreadPool paramThreadPool)
  {
    localAsynchronousFileChannel = open(paramFileDescriptor, paramBoolean1, paramBoolean2, paramThreadPool);
    JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    long l1;
    try
    {
      l1 = localJavaIOFileDescriptorAccess.getHandle(paramFileDescriptor);
      if (l1 == -1L) {
        l1 = localJavaIOFileDescriptorAccess.get(paramFileDescriptor);
      }
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      l1 = localJavaIOFileDescriptorAccess.get(paramFileDescriptor);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor);
    long l2 = 0L;
    int i = 0;
    try
    {
      l2 = localResourceRequest.request(1L, localResourceIdImpl);
      if (l2 < 1L) {
        throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors");
      }
      i = 1;
      if (i == 0)
      {
        localResourceRequest.request(-1L, localResourceIdImpl);
        try
        {
          localAsynchronousFileChannel.close();
        }
        catch (IOException localIOException1) {}
      }
      i = 0;
    }
    finally
    {
      if (i == 0)
      {
        localResourceRequest.request(-1L, localResourceIdImpl);
        try
        {
          localAsynchronousFileChannel.close();
        }
        catch (IOException localIOException4) {}
      }
    }
    localResourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(localAsynchronousFileChannel);
    try
    {
      l2 = localResourceRequest.request(1L, localResourceIdImpl);
      if (l2 < 1L)
      {
        try
        {
          localAsynchronousFileChannel.close();
        }
        catch (IOException localIOException2) {}
        throw new ResourceRequestDeniedException("Resource limited: too many open files");
      }
      i = 1;
      return localAsynchronousFileChannel;
    }
    finally
    {
      if (i == 0)
      {
        localResourceRequest.request(-1L, localResourceIdImpl);
        try
        {
          localAsynchronousFileChannel.close();
        }
        catch (IOException localIOException5) {}
      }
    }
  }
  
  @InstrumentationMethod
  <A> Future<Integer> implRead(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(fdObj);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
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
      localObject = new CompletableFuture();
      ((CompletableFuture)localObject).completeExceptionally(localResourceRequestDeniedException);
      return (Future<Integer>)localObject;
    }
    CompletionHandlerWrapper localCompletionHandlerWrapper = null;
    if (paramCompletionHandler != null) {
      localCompletionHandlerWrapper = new CompletionHandlerWrapper(paramCompletionHandler, localResourceIdImpl, localResourceRequest, l);
    }
    Object localObject = implRead(paramByteBuffer, paramLong, paramA, localCompletionHandlerWrapper);
    if (paramCompletionHandler == null) {
      if (((Future)localObject).isDone())
      {
        int j = 0;
        try
        {
          j = ((Integer)((Future)localObject).get()).intValue();
        }
        catch (InterruptedException|ExecutionException localInterruptedException) {}
        j = Math.max(0, j);
        localResourceRequest.request(-(l - j), localResourceIdImpl);
      }
      else
      {
        localObject = new FutureWrapper((Future)localObject, localResourceIdImpl, localResourceRequest, l);
      }
    }
    return (Future<Integer>)localObject;
  }
  
  @InstrumentationMethod
  <A> Future<Integer> implWrite(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(fdObj);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
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
      localObject = new CompletableFuture();
      ((CompletableFuture)localObject).completeExceptionally(localResourceRequestDeniedException);
      return (Future<Integer>)localObject;
    }
    CompletionHandlerWrapper localCompletionHandlerWrapper = null;
    if (paramCompletionHandler != null) {
      localCompletionHandlerWrapper = new CompletionHandlerWrapper(paramCompletionHandler, localResourceIdImpl, localResourceRequest, l);
    }
    Object localObject = implWrite(paramByteBuffer, paramLong, paramA, localCompletionHandlerWrapper);
    if (paramCompletionHandler == null) {
      if (((Future)localObject).isDone())
      {
        int j = 0;
        try
        {
          j = ((Integer)((Future)localObject).get()).intValue();
        }
        catch (InterruptedException|ExecutionException localInterruptedException) {}
        j = Math.max(0, j);
        localResourceRequest.request(-(l - j), localResourceIdImpl);
      }
      else
      {
        localObject = new FutureWrapper((Future)localObject, localResourceIdImpl, localResourceRequest, l);
      }
    }
    return (Future<Integer>)localObject;
  }
  
  @InstrumentationMethod
  public void close()
    throws IOException
  {
    synchronized (fdObj)
    {
      if (closed) {
        return;
      }
    }
    try
    {
      close();
      ??? = SharedSecrets.getJavaIOFileDescriptorAccess();
      ResourceIdImpl localResourceIdImpl1 = ResourceIdImpl.of(Integer.valueOf(((JavaIOFileDescriptorAccess)???).get(fdObj)));
      ResourceRequest localResourceRequest1 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fdObj);
      localResourceRequest1.request(-1L, localResourceIdImpl1);
      localResourceRequest1 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      localResourceRequest1.request(-1L, localResourceIdImpl1);
    }
    finally
    {
      JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
      ResourceIdImpl localResourceIdImpl2 = ResourceIdImpl.of(Integer.valueOf(localJavaIOFileDescriptorAccess.get(fdObj)));
      ResourceRequest localResourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fdObj);
      localResourceRequest2.request(-1L, localResourceIdImpl2);
      localResourceRequest2 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      localResourceRequest2.request(-1L, localResourceIdImpl2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\SimpleAsynchronousFileChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */