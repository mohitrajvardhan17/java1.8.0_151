package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.ServerSocket")
final class ServerSocketRMHooks
{
  private Object closeLock;
  
  ServerSocketRMHooks() {}
  
  @InstrumentationMethod
  public Socket accept()
    throws IOException
  {
    long l1 = 0L;
    long l2 = 0L;
    Socket localSocket = null;
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    try
    {
      localSocket = accept();
      l2 = 1L;
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(localSocket);
      localResourceIdImpl = ResourceIdImpl.of(localSocket.getLocalAddress());
      try
      {
        l1 = localResourceRequest.request(1L, localResourceIdImpl);
        if (l1 < 1L)
        {
          try
          {
            localSocket.close();
          }
          catch (IOException localIOException1) {}
          throw new IOException("Resource limited: too many open sockets");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        try
        {
          localSocket.close();
        }
        catch (IOException localIOException2) {}
        throw new IOException("Resource limited: too many open sockets", localResourceRequestDeniedException);
      }
      l2 = 1L;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
      }
    }
    return localSocket;
  }
  
  @InstrumentationMethod
  public InetAddress getInetAddress()
  {
    return getInetAddress();
  }
  
  @InstrumentationMethod
  public void bind(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (!isBound())
    {
      localResourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      l = localResourceRequest.request(1L, localResourceIdImpl);
      if (l < 1L) {
        throw new ResourceRequestDeniedException("Resource limited: too many open sockets");
      }
    }
    int i = 0;
    try
    {
      bind(paramSocketAddress, paramInt);
      i = 1;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
  }
  
  @InstrumentationMethod
  public boolean isBound()
  {
    return isBound();
  }
  
  @InstrumentationMethod
  public boolean isClosed()
  {
    return isClosed();
  }
  
  @InstrumentationMethod
  public void close()
    throws IOException
  {
    synchronized (closeLock)
    {
      if (isClosed()) {
        return;
      }
    }
    boolean bool = isBound();
    InetAddress localInetAddress = getInetAddress();
    try
    {
      close();
    }
    finally
    {
      ResourceIdImpl localResourceIdImpl1;
      ResourceRequest localResourceRequest1;
      if (bool)
      {
        ResourceIdImpl localResourceIdImpl2 = ResourceIdImpl.of(localInetAddress);
        ResourceRequest localResourceRequest2 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
        localResourceRequest2.request(-1L, localResourceIdImpl2);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\ServerSocketRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */