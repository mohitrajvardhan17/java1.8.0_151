package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.net.SocketException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.net.AbstractPlainDatagramSocketImpl")
public class AbstractPlainDatagramSocketImplRMHooks
{
  protected FileDescriptor fd;
  
  public AbstractPlainDatagramSocketImplRMHooks() {}
  
  @InstrumentationMethod
  protected synchronized void create()
    throws SocketException
  {
    create();
    JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    long l1;
    try
    {
      l1 = localJavaIOFileDescriptorAccess.getHandle(fd);
      if (l1 == -1L) {
        l1 = localJavaIOFileDescriptorAccess.get(fd);
      }
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      l1 = localJavaIOFileDescriptorAccess.get(fd);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fd);
    long l2 = 0L;
    try
    {
      l2 = localResourceRequest.request(1L, localResourceIdImpl);
      if (l2 < 1L) {
        throw new SocketException("Resource limited: too many open file descriptors");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      localResourceRequest.request(-l2, localResourceIdImpl);
      SocketException localSocketException = new SocketException("Resource limited: too many open file descriptors");
      localSocketException.initCause(localResourceRequestDeniedException);
      throw localSocketException;
    }
    localResourceRequest.request(-(l2 - 1L), localResourceIdImpl);
  }
  
  @InstrumentationMethod
  protected void close()
  {
    if (fd != null)
    {
      JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
      long l;
      try
      {
        l = localJavaIOFileDescriptorAccess.getHandle(fd);
        if (l == -1L) {
          l = localJavaIOFileDescriptorAccess.get(fd);
        }
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        l = localJavaIOFileDescriptorAccess.get(fd);
      }
      if (l != -1L)
      {
        ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(Long.valueOf(l));
        ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fd);
        localResourceRequest.request(-1L, localResourceIdImpl);
      }
    }
    close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\AbstractPlainDatagramSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */