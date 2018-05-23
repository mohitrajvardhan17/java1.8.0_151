package jdk.management.resource.internal;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;

public class FutureWrapper<T>
  implements Future<T>
{
  private final Future<T> future;
  private final ResourceId id;
  private final ResourceRequest ra;
  private final long approved;
  private Object clientChannel;
  private boolean isInvoked = false;
  
  public FutureWrapper(Future<T> paramFuture, ResourceId paramResourceId, ResourceRequest paramResourceRequest, long paramLong)
  {
    future = paramFuture;
    id = paramResourceId;
    ra = paramResourceRequest;
    approved = paramLong;
  }
  
  public FutureWrapper(Future<T> paramFuture)
  {
    this(paramFuture, null, null, 0L);
  }
  
  public FutureWrapper(Future<T> paramFuture, Object paramObject)
  {
    this(paramFuture, null, null, 0L);
    clientChannel = paramObject;
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    return future.cancel(paramBoolean);
  }
  
  public boolean isCancelled()
  {
    return future.isCancelled();
  }
  
  public boolean isDone()
  {
    return future.isDone();
  }
  
  public T get()
    throws InterruptedException, ExecutionException
  {
    Object localObject = future.get();
    processResult(localObject);
    return (T)localObject;
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    Object localObject = future.get(paramLong, paramTimeUnit);
    processResult(localObject);
    return (T)localObject;
  }
  
  private synchronized void processResult(T paramT)
  {
    if (isInvoked) {
      return;
    }
    isInvoked = true;
    if ((paramT instanceof Number))
    {
      int i = ((Number)paramT).intValue();
      if (i == -1) {
        ra.request(-approved, id);
      } else {
        ra.request(-(approved - i), id);
      }
    }
    else if (((paramT instanceof AsynchronousSocketChannel)) || (clientChannel != null))
    {
      AsynchronousSocketChannel localAsynchronousSocketChannel = (AsynchronousSocketChannel)paramT;
      if (paramT != null) {
        localAsynchronousSocketChannel = (AsynchronousSocketChannel)paramT;
      } else {
        localAsynchronousSocketChannel = (AsynchronousSocketChannel)clientChannel;
      }
      ResourceIdImpl localResourceIdImpl = null;
      try
      {
        localResourceIdImpl = ResourceIdImpl.of(localAsynchronousSocketChannel.getLocalAddress());
      }
      catch (IOException localIOException1) {}
      ResourceRequest localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(localAsynchronousSocketChannel);
      long l = 0L;
      Object localObject = null;
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          localObject = new ResourceRequestDeniedException("Resource limited: too many open server socket channels");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        localObject = localResourceRequestDeniedException;
      }
      if (localObject == null)
      {
        localResourceRequest.request(-(l - 1L), localResourceIdImpl);
      }
      else
      {
        localResourceRequest.request(-l, localResourceIdImpl);
        try
        {
          localAsynchronousSocketChannel.close();
        }
        catch (IOException localIOException2) {}
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\FutureWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */