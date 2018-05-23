package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.SSLSocketImpl")
public final class SSLSocketImplRMHooks
{
  private final Socket self = null;
  
  public SSLSocketImplRMHooks() {}
  
  public final boolean isBound()
  {
    return isBound();
  }
  
  @InstrumentationMethod
  void waitForClose(boolean paramBoolean)
    throws IOException
  {
    InetAddress localInetAddress = null;
    if ((self != null) && (!self.equals(this))) {
      localInetAddress = self.getLocalAddress();
    }
    if (isBound())
    {
      ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(localInetAddress);
      ResourceRequest localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      localResourceRequest.request(-1L, localResourceIdImpl);
    }
    waitForClose(paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\SSLSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */