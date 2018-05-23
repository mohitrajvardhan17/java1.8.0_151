package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.SocketChannelImpl")
public final class SocketChannelImplRMHooks
{
  public SocketChannelImplRMHooks() {}
  
  @InstrumentationMethod
  public SocketAddress getLocalAddress()
    throws IOException
  {
    return getLocalAddress();
  }
  
  @InstrumentationMethod
  public SocketChannel bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (getLocalAddress() == null)
    {
      localResourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new IOException("Resource limited: too many open socket channels");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        throw new IOException("Resource limited: too many open socket channels", localResourceRequestDeniedException);
      }
    }
    int i = 0;
    SocketChannel localSocketChannel = null;
    try
    {
      localSocketChannel = bind(paramSocketAddress);
      i = 1;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
    return localSocketChannel;
  }
  
  @InstrumentationMethod
  public boolean connect(SocketAddress paramSocketAddress)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (getLocalAddress() == null)
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
    boolean bool = false;
    try
    {
      bool = connect(paramSocketAddress);
      i = 1;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
    return bool;
  }
  
  @InstrumentationMethod
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    ResourceRequest localResourceRequest = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    int k = 0;
    try
    {
      k = read(paramByteBuffer);
      j = Math.max(k, 0);
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
    return k;
  }
  
  @InstrumentationMethod
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      return read(paramArrayOfByteBuffer, paramInt1, paramInt2);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    ResourceRequest localResourceRequest = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
    long l1 = 0L;
    int i = 0;
    for (int j = paramInt1; j < paramInt1 + paramInt2; j++) {
      i += paramArrayOfByteBuffer[j].remaining();
    }
    try
    {
      l1 = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l1 < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    long l2 = 0L;
    long l3 = 0L;
    try
    {
      l3 = read(paramArrayOfByteBuffer, paramInt1, paramInt2);
      l2 = Math.max(l3, 0L);
    }
    finally
    {
      localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
    }
    return l3;
  }
  
  @InstrumentationMethod
  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    ResourceRequest localResourceRequest = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    try
    {
      j = write(paramByteBuffer);
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
    return j;
  }
  
  @InstrumentationMethod
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      return write(paramArrayOfByteBuffer, paramInt1, paramInt2);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    ResourceRequest localResourceRequest = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
    long l1 = 0L;
    int i = 0;
    for (int j = paramInt1; j < paramInt1 + paramInt2; j++) {
      i += paramArrayOfByteBuffer[j].remaining();
    }
    try
    {
      l1 = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l1 < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    long l2 = 0L;
    try
    {
      l2 = write(paramArrayOfByteBuffer, paramInt1, paramInt2);
    }
    finally
    {
      localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
    }
    return l2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\SocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */