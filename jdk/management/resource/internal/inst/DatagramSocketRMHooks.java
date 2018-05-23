package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.DatagramSocket")
public final class DatagramSocketRMHooks
{
  public DatagramSocketRMHooks() {}
  
  @InstrumentationMethod
  public InetAddress getLocalAddress()
  {
    return getLocalAddress();
  }
  
  @InstrumentationMethod
  public boolean isBound()
  {
    return isBound();
  }
  
  @InstrumentationMethod
  public synchronized void bind(SocketAddress paramSocketAddress)
    throws SocketException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (!isBound())
    {
      localResourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      localResourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new SocketException("Resource limited: too many open datagram sockets");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        SocketException localSocketException = new SocketException("Resource limited: too many open datagram sockets");
        localSocketException.initCause(localResourceRequestDeniedException);
        throw localSocketException;
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
  private synchronized void connectInternal(InetAddress paramInetAddress, int paramInt)
    throws SocketException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (!isBound())
    {
      localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
      localResourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new SocketException("Resource limited: too many open datagram sockets");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        SocketException localSocketException = new SocketException("Resource limited: too many open datagram sockets");
        localSocketException.initCause(localResourceRequestDeniedException);
        throw localSocketException;
      }
    }
    int i = 0;
    try
    {
      connectInternal(paramInetAddress, paramInt);
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
  public synchronized void receive(DatagramPacket paramDatagramPacket)
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
    int i = Math.max(paramDatagramPacket.getLength(), 0);
    if (i > 0)
    {
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
      int k = 0;
      try
      {
        receive(paramDatagramPacket);
        j = paramDatagramPacket.getLength();
        k = 1;
      }
      finally
      {
        localResourceRequest2.request(-(l - j), localResourceIdImpl);
        localResourceRequest1.request(-(1 - k), localResourceIdImpl);
      }
    }
  }
  
  @InstrumentationMethod
  public void send(DatagramPacket paramDatagramPacket)
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
    int i = Math.max(paramDatagramPacket.getLength(), 0);
    if (i > 0)
    {
      ResourceRequest localResourceRequest2 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
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
        throw new IOException("Resource limited: too many sent datagrams", localResourceRequestDeniedException2);
      }
      int j = 0;
      try
      {
        send(paramDatagramPacket);
        j = paramDatagramPacket.getLength();
      }
      finally
      {
        localResourceRequest2.request(-(l - j), localResourceIdImpl);
      }
    }
  }
  
  @InstrumentationMethod
  public boolean isClosed()
  {
    return isClosed();
  }
  
  @InstrumentationMethod
  public boolean isConnected()
  {
    return isConnected();
  }
  
  @InstrumentationMethod
  public void close()
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
        ResourceRequest localResourceRequest2 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
        localResourceRequest2.request(-1L, localResourceIdImpl2);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\DatagramSocketRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */