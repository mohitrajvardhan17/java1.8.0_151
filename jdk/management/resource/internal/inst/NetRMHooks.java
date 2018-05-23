package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.ProtocolFamily;
import jdk.Exported;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@Exported(false)
@InstrumentationTarget("sun.nio.ch.Net")
public class NetRMHooks
{
  public NetRMHooks() {}
  
  @InstrumentationMethod
  static FileDescriptor socket(ProtocolFamily paramProtocolFamily, boolean paramBoolean)
    throws IOException
  {
    FileDescriptor localFileDescriptor = socket(paramProtocolFamily, paramBoolean);
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(localFileDescriptor);
    ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(localFileDescriptor);
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
      localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
    }
    return localFileDescriptor;
  }
  
  @InstrumentationMethod
  static FileDescriptor serverSocket(boolean paramBoolean)
  {
    FileDescriptor localFileDescriptor = serverSocket(paramBoolean);
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(localFileDescriptor);
    ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(localFileDescriptor);
    long l1 = 0L;
    long l2 = 0L;
    try
    {
      l1 = localResourceRequest.request(1L, localResourceIdImpl);
      if (l1 < 1L) {
        throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors");
      }
      l2 = 1L;
    }
    finally
    {
      localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
    }
    return localFileDescriptor;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\NetRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */