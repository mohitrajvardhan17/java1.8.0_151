package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.DatagramChannelImpl")
public final class DatagramChannelImplRMHooks
{
  public DatagramChannelImplRMHooks() {}
  
  @InstrumentationMethod
  public SocketAddress getLocalAddress()
    throws IOException
  {
    return getLocalAddress();
  }
  
  @InstrumentationMethod
  public boolean isConnected()
  {
    return isConnected();
  }
  
  @InstrumentationMethod
  public DatagramChannel bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (getLocalAddress() == null)
    {
      localResourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      localResourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new IOException("Resource limited: too many open datagram channels");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        throw new IOException("Resource limited: too many open datagram channels", localResourceRequestDeniedException);
      }
    }
    int i = 0;
    DatagramChannel localDatagramChannel;
    try
    {
      localDatagramChannel = bind(paramSocketAddress);
      i = 1;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
    return localDatagramChannel;
  }
  
  @InstrumentationMethod
  public DatagramChannel connect(SocketAddress paramSocketAddress)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (getLocalAddress() == null)
    {
      localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
      localResourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new IOException("Resource limited: too many open datagram channels");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        throw new IOException("Resource limited: too many open datagram channels", localResourceRequestDeniedException);
      }
    }
    int i = 0;
    DatagramChannel localDatagramChannel;
    try
    {
      localDatagramChannel = connect(paramSocketAddress);
      i = 1;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
    return localDatagramChannel;
  }
  
  @InstrumentationMethod
  public SocketAddress receive(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    ResourceRequest localResourceRequest1 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
    long l = 0L;
    try
    {
      l = Math.max(localResourceRequest1.request(1L, localResourceIdImpl), 0L);
      if (l < 1L) {
        throw new IOException("Resource limited: too many received datagrams");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
    {
      throw new IOException("Resource limited: too many received datagrams", localResourceRequestDeniedException1);
    }
    localResourceRequest1.request(-(l - 1L), localResourceIdImpl);
    int i = paramByteBuffer.remaining();
    ResourceRequest localResourceRequest2 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
    try
    {
      l = Math.max(localResourceRequest2.request(i, localResourceIdImpl), 0L);
      if (l < i)
      {
        localResourceRequest1.request(-1L, localResourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
    {
      localResourceRequest1.request(-1L, localResourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", localResourceRequestDeniedException2);
    }
    int j = 0;
    SocketAddress localSocketAddress = null;
    try
    {
      int k = paramByteBuffer.position();
      localSocketAddress = receive(paramByteBuffer);
      j = paramByteBuffer.position() - k;
    }
    finally
    {
      if (localSocketAddress == null) {
        localResourceRequest1.request(-1L, localResourceIdImpl);
      }
      localResourceRequest2.request(-(l - j), localResourceIdImpl);
    }
    return localSocketAddress;
  }
  
  @InstrumentationMethod
  public int send(ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    long l = 0L;
    if (getLocalAddress() == null)
    {
      ResourceRequest localResourceRequest1 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest1.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new IOException("Resource limited: too many open datagram channels");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
      {
        throw new IOException("Resource limited: too many open datagram channels", localResourceRequestDeniedException1);
      }
      localResourceRequest1.request(-(l - 1L), localResourceIdImpl);
    }
    int i;
    if (isConnected())
    {
      i = send(paramByteBuffer, paramSocketAddress);
    }
    else
    {
      ResourceRequest localResourceRequest2 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
      l = 0L;
      try
      {
        l = Math.max(localResourceRequest2.request(1L, localResourceIdImpl), 0L);
        if (l < 1L) {
          throw new IOException("Resource limited: too many sent datagrams");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
      {
        throw new IOException("Resource limited: too many sent datagrams", localResourceRequestDeniedException2);
      }
      localResourceRequest2.request(-(l - 1L), localResourceIdImpl);
      int j = paramByteBuffer.remaining();
      ResourceRequest localResourceRequest3 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
      try
      {
        l = Math.max(localResourceRequest3.request(j, localResourceIdImpl), 0L);
        if (l < j)
        {
          localResourceRequest2.request(-1L, localResourceIdImpl);
          throw new IOException("Resource limited: insufficient bytes approved");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException3)
      {
        localResourceRequest2.request(-1L, localResourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved", localResourceRequestDeniedException3);
      }
      i = 0;
      try
      {
        i = send(paramByteBuffer, paramSocketAddress);
      }
      finally
      {
        if (i == 0) {
          localResourceRequest2.request(-1L, localResourceIdImpl);
        }
        localResourceRequest3.request(-(l - i), localResourceIdImpl);
      }
    }
    return i;
  }
  
  @InstrumentationMethod
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    ResourceRequest localResourceRequest1 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
    long l = 0L;
    try
    {
      l = Math.max(localResourceRequest1.request(1L, localResourceIdImpl), 0L);
      if (l < 1L) {
        throw new IOException("Resource limited: too many received datagrams");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
    {
      throw new IOException("Resource limited: too many received datagrams", localResourceRequestDeniedException1);
    }
    localResourceRequest1.request(-(l - 1L), localResourceIdImpl);
    ResourceRequest localResourceRequest2 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest2.request(i, localResourceIdImpl), 0L);
      if (l < i)
      {
        localResourceRequest1.request(-1L, localResourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
    {
      localResourceRequest1.request(-1L, localResourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", localResourceRequestDeniedException2);
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
      localResourceRequest2.request(-(l - j), localResourceIdImpl);
      if (j == 0) {
        localResourceRequest1.request(-1L, localResourceIdImpl);
      }
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
    ResourceRequest localResourceRequest1 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
    long l1 = 0L;
    try
    {
      l1 = Math.max(localResourceRequest1.request(1L, localResourceIdImpl), 0L);
      if (l1 < 1L) {
        throw new IOException("Resource limited: too many received datagrams");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
    {
      throw new IOException("Resource limited: too many received datagrams", localResourceRequestDeniedException1);
    }
    localResourceRequest1.request(-(l1 - 1L), localResourceIdImpl);
    ResourceRequest localResourceRequest2 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
    l1 = 0L;
    int i = 0;
    for (int j = paramInt1; j < paramInt1 + paramInt2; j++) {
      i += paramArrayOfByteBuffer[j].remaining();
    }
    try
    {
      l1 = Math.max(localResourceRequest2.request(i, localResourceIdImpl), 0L);
      if (l1 < i)
      {
        localResourceRequest1.request(-1L, localResourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
    {
      localResourceRequest1.request(-1L, localResourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", localResourceRequestDeniedException2);
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
      localResourceRequest2.request(-(l1 - l2), localResourceIdImpl);
      if (l2 == 0L) {
        localResourceRequest1.request(-1L, localResourceIdImpl);
      }
    }
    return l3;
  }
  
  @InstrumentationMethod
  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    ResourceRequest localResourceRequest1 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
    long l = 0L;
    try
    {
      l = Math.max(localResourceRequest1.request(1L, localResourceIdImpl), 0L);
      if (l < 1L) {
        throw new IOException("Resource limited: too many sent datagrams");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
    {
      throw new IOException("Resource limited: too many sent datagrams", localResourceRequestDeniedException1);
    }
    localResourceRequest1.request(-(l - 1L), localResourceIdImpl);
    ResourceRequest localResourceRequest2 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest2.request(i, localResourceIdImpl), 0L);
      if (l < i)
      {
        localResourceRequest1.request(-1L, localResourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
    {
      localResourceRequest1.request(-1L, localResourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", localResourceRequestDeniedException2);
    }
    int j = 0;
    try
    {
      j = write(paramByteBuffer);
    }
    finally
    {
      localResourceRequest2.request(-(l - j), localResourceIdImpl);
      if (j == 0) {
        localResourceRequest1.request(-1L, localResourceIdImpl);
      }
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
    ResourceRequest localResourceRequest1 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
    long l1 = 0L;
    try
    {
      l1 = Math.max(localResourceRequest1.request(1L, localResourceIdImpl), 0L);
      if (l1 < 1L) {
        throw new IOException("Resource limited: too many sent datagrams");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
    {
      throw new IOException("Resource limited: too many sent datagrams", localResourceRequestDeniedException1);
    }
    localResourceRequest1.request(-(l1 - 1L), localResourceIdImpl);
    ResourceRequest localResourceRequest2 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
    l1 = 0L;
    int i = 0;
    for (int j = paramInt1; j < paramInt1 + paramInt2; j++) {
      i += paramArrayOfByteBuffer[j].remaining();
    }
    try
    {
      l1 = Math.max(localResourceRequest2.request(i, localResourceIdImpl), 0L);
      if (l1 < i)
      {
        localResourceRequest1.request(-1L, localResourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
    {
      localResourceRequest1.request(-1L, localResourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", localResourceRequestDeniedException2);
    }
    long l2 = 0L;
    try
    {
      l2 = write(paramArrayOfByteBuffer, paramInt1, paramInt2);
    }
    finally
    {
      localResourceRequest2.request(-(l1 - l2), localResourceIdImpl);
      if (l2 == 0L) {
        localResourceRequest1.request(-1L, localResourceIdImpl);
      }
    }
    return l2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\DatagramChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */