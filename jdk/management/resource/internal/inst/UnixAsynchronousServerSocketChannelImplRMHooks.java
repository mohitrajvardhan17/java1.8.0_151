package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.UnixAsynchronousServerSocketChannelImpl")
public class UnixAsynchronousServerSocketChannelImplRMHooks
{
  private static final NativeDispatcher nd = null;
  protected volatile InetSocketAddress localAddress = null;
  
  public UnixAsynchronousServerSocketChannelImplRMHooks() {}
  
  @InstrumentationMethod
  private int accept(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress)
    throws IOException
  {
    int i = accept(paramFileDescriptor1, paramFileDescriptor2, paramArrayOfInetSocketAddress);
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(paramFileDescriptor2);
    if (localResourceIdImpl != null)
    {
      ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor2);
      long l1 = 0L;
      long l2 = 0L;
      try
      {
        try
        {
          l1 = localResourceRequest.request(1L, localResourceIdImpl);
          if (l1 < 1L) {
            throw new IOException("Resource limited: too many open file descriptors");
          }
        }
        catch (ResourceRequestDeniedException localResourceRequestDeniedException)
        {
          throw new IOException("Resource limited: too many open file descriptors", localResourceRequestDeniedException);
        }
        l2 = 1L;
      }
      finally
      {
        if (l2 == 0L) {
          try
          {
            nd.close(paramFileDescriptor2);
          }
          catch (IOException localIOException2) {}
        } else {
          localResourceRequest.request(-(l1 - 1L), localResourceIdImpl);
        }
      }
    }
    return i;
  }
  
  /* Error */
  @InstrumentationMethod
  void implClose()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 86	jdk/management/resource/internal/inst/UnixAsynchronousServerSocketChannelImplRMHooks:implClose	()V
    //   4: aload_0
    //   5: getfield 78	jdk/management/resource/internal/inst/UnixAsynchronousServerSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   8: ifnull +74 -> 82
    //   11: aload_0
    //   12: getfield 78	jdk/management/resource/internal/inst/UnixAsynchronousServerSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   15: invokestatic 85	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   18: astore_1
    //   19: getstatic 77	jdk/management/resource/internal/ApproverGroup:SOCKET_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   22: aload_0
    //   23: invokevirtual 83	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   26: astore_2
    //   27: aload_2
    //   28: ldc2_w 36
    //   31: aload_1
    //   32: invokeinterface 89 4 0
    //   37: pop2
    //   38: goto +44 -> 82
    //   41: astore_3
    //   42: aload_0
    //   43: getfield 78	jdk/management/resource/internal/inst/UnixAsynchronousServerSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   46: ifnull +34 -> 80
    //   49: aload_0
    //   50: getfield 78	jdk/management/resource/internal/inst/UnixAsynchronousServerSocketChannelImplRMHooks:localAddress	Ljava/net/InetSocketAddress;
    //   53: invokestatic 85	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   56: astore 4
    //   58: getstatic 77	jdk/management/resource/internal/ApproverGroup:SOCKET_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   61: aload_0
    //   62: invokevirtual 83	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   65: astore 5
    //   67: aload 5
    //   69: ldc2_w 36
    //   72: aload 4
    //   74: invokeinterface 89 4 0
    //   79: pop2
    //   80: aload_3
    //   81: athrow
    //   82: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	UnixAsynchronousServerSocketChannelImplRMHooks
    //   18	14	1	localResourceIdImpl1	ResourceIdImpl
    //   26	2	2	localResourceRequest1	ResourceRequest
    //   41	40	3	localObject	Object
    //   56	17	4	localResourceIdImpl2	ResourceIdImpl
    //   65	3	5	localResourceRequest2	ResourceRequest
    // Exception table:
    //   from	to	target	type
    //   0	4	41	finally
  }
  
  abstract class NativeDispatcher
  {
    NativeDispatcher() {}
    
    abstract void close(FileDescriptor paramFileDescriptor)
      throws IOException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\UnixAsynchronousServerSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */