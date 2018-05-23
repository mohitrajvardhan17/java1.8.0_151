package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.io.FileInputStream")
public final class FileInputStreamRMHooks
{
  private final FileDescriptor fd = null;
  private String path = null;
  private final Object closeLock = new Object();
  private volatile boolean closed = false;
  
  public FileInputStreamRMHooks() {}
  
  @InstrumentationMethod
  public final FileDescriptor getFD()
    throws IOException
  {
    return getFD();
  }
  
  @InstrumentationMethod
  private void open(String paramString)
    throws FileNotFoundException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest1 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
    long l1 = 0L;
    try
    {
      l1 = localResourceRequest1.request(1L, localResourceIdImpl);
      if (l1 < 1L) {
        throw new FileNotFoundException(paramString + ": resource limited: too many open files");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException1)
    {
      FileNotFoundException localFileNotFoundException1 = new FileNotFoundException(paramString + ": resource limited: too many open files");
      localFileNotFoundException1.initCause(localResourceRequestDeniedException1);
      throw localFileNotFoundException1;
    }
    ResourceRequest localResourceRequest2 = null;
    long l2 = 0L;
    int i = 0;
    try
    {
      FileDescriptor localFileDescriptor = null;
      try
      {
        localFileDescriptor = getFD();
      }
      catch (IOException localIOException) {}
      localResourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(localFileDescriptor);
      try
      {
        l2 = localResourceRequest2.request(1L, localResourceIdImpl);
        if (l2 < 1L) {
          throw new FileNotFoundException(paramString + ": resource limited: too many open file descriptors");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException2)
      {
        FileNotFoundException localFileNotFoundException2 = new FileNotFoundException(paramString + ": resource limited: too many open file descriptors");
        localFileNotFoundException2.initCause(localResourceRequestDeniedException2);
        throw localFileNotFoundException2;
      }
      open(paramString);
      i = 1;
    }
    finally
    {
      localResourceRequest2.request(-(l2 - i), localResourceIdImpl);
      localResourceRequest1.request(-(l1 - i), localResourceIdImpl);
    }
  }
  
  @InstrumentationMethod
  public int read()
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest;
    if (getFD() == FileDescriptor.in) {
      localResourceRequest = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
    } else {
      localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    }
    long l = 0L;
    try
    {
      l = localResourceRequest.request(1L, localResourceIdImpl);
      if (l < 1L) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int i = -1;
    try
    {
      i = read();
    }
    finally
    {
      localResourceRequest.request(-(l - (i == -1 ? 0 : 1)), localResourceIdImpl);
    }
    return i;
  }
  
  @InstrumentationMethod
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      return read(paramArrayOfByte);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest;
    if (getFD() == FileDescriptor.in) {
      localResourceRequest = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
    } else {
      localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    }
    int i = paramArrayOfByte.length;
    long l = 0L;
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    int k = 0;
    try
    {
      if (l < i)
      {
        localResourceRequest.request(-l, localResourceIdImpl);
        k = read(paramArrayOfByte, 0, paramArrayOfByte.length);
        j = Math.max(k, 0);
      }
      else
      {
        k = read(paramArrayOfByte);
        j = Math.max(k, 0);
      }
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
    return k;
  }
  
  @InstrumentationMethod
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0) {
      return read(paramArrayOfByte, paramInt1, paramInt2);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest;
    if (getFD() == FileDescriptor.in) {
      localResourceRequest = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
    } else {
      localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    }
    long l = 0L;
    try
    {
      l = Math.max(localResourceRequest.request(paramInt2, localResourceIdImpl), 0L);
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    paramInt2 = Math.min(paramInt2, (int)l);
    int i = 0;
    int j;
    try
    {
      j = read(paramArrayOfByte, paramInt1, paramInt2);
      i = Math.max(j, 0);
    }
    finally
    {
      localResourceRequest.request(-(l - i), localResourceIdImpl);
    }
    return j;
  }
  
  @InstrumentationMethod
  public void close()
    throws IOException
  {
    synchronized (closeLock)
    {
      if (closed) {
        return;
      }
    }
    JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    long l1;
    try
    {
      l1 = localJavaIOFileDescriptorAccess.getHandle(fd);
      if (l1 == -1L) {
        l1 = localJavaIOFileDescriptorAccess.get(fd);
      }
    }
    catch (UnsupportedOperationException localUnsupportedOperationException1)
    {
      l1 = localJavaIOFileDescriptorAccess.get(fd);
    }
    try
    {
      close();
    }
    finally
    {
      long l3;
      try
      {
        long l2;
        ResourceIdImpl localResourceIdImpl1;
        ResourceRequest localResourceRequest1;
        l3 = localJavaIOFileDescriptorAccess.getHandle(fd);
        if (l3 == -1L) {
          l3 = localJavaIOFileDescriptorAccess.get(fd);
        }
      }
      catch (UnsupportedOperationException localUnsupportedOperationException3)
      {
        l3 = localJavaIOFileDescriptorAccess.get(fd);
      }
      ResourceIdImpl localResourceIdImpl2 = ResourceIdImpl.of(path);
      if (l3 != l1)
      {
        localResourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fd);
        localResourceRequest2.request(-1L, localResourceIdImpl2);
      }
      ResourceRequest localResourceRequest2 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      localResourceRequest2.request(-1L, localResourceIdImpl2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\FileInputStreamRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */