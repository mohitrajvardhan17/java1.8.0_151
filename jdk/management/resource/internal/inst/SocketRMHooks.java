package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketOptions;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.net.Socket")
@TypeMapping(from="jdk.management.resource.internal.inst.SocketRMHooks$SocketImpl", to="java.net.SocketImpl")
public final class SocketRMHooks
{
  private boolean created = false;
  SocketImpl impl;
  
  public SocketRMHooks() {}
  
  public InetAddress getLocalAddress()
  {
    return getLocalAddress();
  }
  
  @InstrumentationMethod
  public void bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (!isBound())
    {
      localResourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new IOException("Resource limited: too many open sockets");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        throw new IOException("Resource limited: too many open sockets", localResourceRequestDeniedException);
      }
    }
    int i = 0;
    try
    {
      bind(paramSocketAddress);
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
  public void connect(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (!isBound())
    {
      localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new IOException("Resource limited: too many open sockets");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        throw new IOException("Resource limited: too many open sockets", localResourceRequestDeniedException);
      }
    }
    int i = 0;
    try
    {
      connect(paramSocketAddress, paramInt);
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
  final void postAccept()
  {
    postAccept();
    FileDescriptor localFileDescriptor = impl.getFileDescriptor();
    JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    long l1;
    try
    {
      l1 = localJavaIOFileDescriptorAccess.getHandle(localFileDescriptor);
      if (l1 == -1L) {
        l1 = localJavaIOFileDescriptorAccess.get(localFileDescriptor);
      }
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      l1 = localJavaIOFileDescriptorAccess.get(localFileDescriptor);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(localFileDescriptor);
    long l2 = 0L;
    int i = 0;
    try
    {
      l2 = localResourceRequest.request(1L, localResourceIdImpl);
      if (l2 < 1L) {
        throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors");
      }
      i = 1;
    }
    finally
    {
      if (i == 0)
      {
        try
        {
          close();
        }
        catch (IOException localIOException2) {}
        localResourceRequest.request(-Math.max(0L, l2 - 1L), localResourceIdImpl);
      }
    }
  }
  
  @InstrumentationMethod
  public boolean isClosed()
  {
    return isClosed();
  }
  
  @InstrumentationMethod
  public synchronized void close()
    throws IOException
  {
    if (isClosed()) {
      return;
    }
    boolean bool = isBound();
    InetAddress localInetAddress = getLocalAddress();
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
  
  abstract class SocketImpl
    implements SocketOptions
  {
    protected FileDescriptor fd;
    
    SocketImpl() {}
    
    protected FileDescriptor getFileDescriptor()
    {
      return fd;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\SocketRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */