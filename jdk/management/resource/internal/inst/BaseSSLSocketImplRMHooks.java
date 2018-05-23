package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.BaseSSLSocketImpl")
abstract class BaseSSLSocketImplRMHooks
{
  BaseSSLSocketImplRMHooks() {}
  
  @InstrumentationMethod
  boolean isLayered()
  {
    return isLayered();
  }
  
  @InstrumentationMethod
  public final InetAddress getLocalAddress()
  {
    return getLocalAddress();
  }
  
  @InstrumentationMethod
  public final boolean isBound()
  {
    return isBound();
  }
  
  @InstrumentationMethod
  public synchronized void close()
    throws IOException
  {
    if ((isLayered()) && (isBound()))
    {
      ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(getLocalAddress());
      ResourceRequest localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      localResourceRequest.request(-1L, localResourceIdImpl);
    }
    close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\BaseSSLSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */