package jdk.management.resource.internal.inst;

import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.SocketOutputStream")
@TypeMapping(from="jdk.management.resource.internal.inst.SocketOutputStreamRMHooks$AbstractPlainSocketImpl", to="java.net.AbstractPlainSocketImpl")
public final class SocketOutputStreamRMHooks
{
  private AbstractPlainSocketImpl impl = null;
  
  public SocketOutputStreamRMHooks() {}
  
  @InstrumentationMethod
  private void socketWrite(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0)
    {
      socketWrite(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(Integer.valueOf(impl.localport));
    ResourceRequest localResourceRequest = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
    long l = 0L;
    try
    {
      l = localResourceRequest.request(paramInt2, localResourceIdImpl);
      if (l < paramInt2) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int i = 0;
    try
    {
      socketWrite(paramArrayOfByte, paramInt1, paramInt2);
      i = paramInt2;
    }
    finally
    {
      localResourceRequest.request(-(l - i), localResourceIdImpl);
    }
  }
  
  static class AbstractPlainSocketImpl
  {
    protected int localport;
    
    AbstractPlainSocketImpl() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\SocketOutputStreamRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */