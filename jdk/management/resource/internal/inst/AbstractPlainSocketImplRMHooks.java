package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.AbstractPlainSocketImpl")
abstract class AbstractPlainSocketImplRMHooks
{
  protected FileDescriptor fd;
  
  AbstractPlainSocketImplRMHooks() {}
  
  abstract void socketClose0(boolean paramBoolean);
  
  @InstrumentationMethod
  protected synchronized void create(boolean paramBoolean)
    throws IOException
  {
    create(paramBoolean);
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(fd);
    ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fd);
    long l = 0L;
    try
    {
      l = localResourceRequest.request(1L, localResourceIdImpl);
      if (l < 1L)
      {
        socketClose0(false);
        throw new IOException("Resource limited: too many open file descriptors");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      localResourceRequest.request(-l, localResourceIdImpl);
      socketClose0(false);
      throw new IOException("Resource limited: too many open file descriptors", localResourceRequestDeniedException);
    }
    localResourceRequest.request(-(l - 1L), localResourceIdImpl);
  }
  
  @InstrumentationMethod
  protected void close()
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    int i = -1;
    if (fd != null)
    {
      localResourceIdImpl = ResourceIdImpl.of(fd);
      localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fd);
    }
    try
    {
      close();
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-1L, localResourceIdImpl);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\AbstractPlainSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */