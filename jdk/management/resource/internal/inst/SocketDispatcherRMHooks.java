package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.Exported;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@Exported(false)
@InstrumentationTarget("sun.nio.ch.SocketDispatcher")
public class SocketDispatcherRMHooks
{
  public SocketDispatcherRMHooks() {}
  
  @InstrumentationMethod
  void close(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    long l = 0L;
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(paramFileDescriptor);
    try
    {
      close(paramFileDescriptor);
      l = 1L;
    }
    finally
    {
      ResourceRequest localResourceRequest1;
      ResourceRequest localResourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor);
      localResourceRequest2.request(-l, localResourceIdImpl);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\SocketDispatcherRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */