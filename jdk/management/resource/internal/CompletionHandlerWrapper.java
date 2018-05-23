package jdk.management.resource.internal;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;

public class CompletionHandlerWrapper<V, A>
  implements CompletionHandler<V, A>
{
  private final CompletionHandler<V, ? super A> handler;
  private final ResourceId id;
  private final ResourceRequest ra;
  private final long approved;
  private Object clientChannel;
  
  public CompletionHandlerWrapper(CompletionHandler<V, ? super A> paramCompletionHandler, ResourceId paramResourceId, ResourceRequest paramResourceRequest, long paramLong)
  {
    handler = paramCompletionHandler;
    id = paramResourceId;
    ra = paramResourceRequest;
    approved = paramLong;
  }
  
  public CompletionHandlerWrapper(CompletionHandler<V, ? super A> paramCompletionHandler)
  {
    this(paramCompletionHandler, null, null, 0L);
  }
  
  public CompletionHandlerWrapper(CompletionHandler<V, ? super A> paramCompletionHandler, Object paramObject)
  {
    this(paramCompletionHandler, null, null, 0L);
    clientChannel = paramObject;
  }
  
  public void completed(V paramV, A paramA)
  {
    if ((paramV instanceof Number))
    {
      int i = ((Number)paramV).intValue();
      if (i == -1) {
        ra.request(-approved, id);
      } else {
        ra.request(-(approved - i), id);
      }
    }
    else if (((paramV instanceof AsynchronousSocketChannel)) || (clientChannel != null))
    {
      AsynchronousSocketChannel localAsynchronousSocketChannel;
      if (paramV != null) {
        localAsynchronousSocketChannel = (AsynchronousSocketChannel)paramV;
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
        if (handler != null) {
          handler.failed((Throwable)localObject, paramA);
        }
        return;
      }
    }
    if (handler != null) {
      handler.completed(paramV, paramA);
    }
  }
  
  public void failed(Throwable paramThrowable, A paramA)
  {
    if ((ra != null) && (id != null)) {
      ra.request(-approved, id);
    }
    if (handler != null) {
      handler.failed(paramThrowable, paramA);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\CompletionHandlerWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */