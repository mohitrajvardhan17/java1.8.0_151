package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.FileChannelImpl")
public final class FileChannelImplRMHooks
{
  private final FileDescriptor fd = null;
  private String path = null;
  
  public FileChannelImplRMHooks() {}
  
  @InstrumentationMethod
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, Object paramObject)
  {
    long l = 0L;
    int i = 0;
    FileChannel localFileChannel = null;
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    try
    {
      localFileChannel = open(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, paramObject);
      localResourceIdImpl = ResourceIdImpl.of(paramString);
      localResourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(localFileChannel);
      int j = 0;
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new ResourceRequestDeniedException(paramString + ": resource limited: too many open files");
        }
        j = 1;
        if (j == 0) {
          try
          {
            localFileChannel.close();
          }
          catch (IOException localIOException1) {}
        }
        i = 1;
      }
      finally
      {
        if (j == 0) {
          try
          {
            localFileChannel.close();
          }
          catch (IOException localIOException2) {}
        }
      }
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
    return localFileChannel;
  }
  
  @InstrumentationMethod
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject)
  {
    localFileChannel = open(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, paramBoolean3, paramObject);
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(paramString);
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    int i = 0;
    if (paramObject == null)
    {
      localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new ResourceRequestDeniedException(paramString + ": resource limited: too many open file descriptors");
        }
        i = 1;
        if (i == 0)
        {
          localResourceRequest.request(-1L, localResourceIdImpl);
          try
          {
            localFileChannel.close();
          }
          catch (IOException localIOException1) {}
        }
        i = 0;
      }
      finally
      {
        if (i == 0)
        {
          localResourceRequest.request(-1L, localResourceIdImpl);
          try
          {
            localFileChannel.close();
          }
          catch (IOException localIOException4) {}
        }
      }
    }
    localResourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(localFileChannel);
    try
    {
      l = localResourceRequest.request(1L, localResourceIdImpl);
      if (l < 1L)
      {
        try
        {
          localFileChannel.close();
        }
        catch (IOException localIOException2) {}
        throw new ResourceRequestDeniedException(paramString + ": resource limited: too many open files");
      }
      i = 1;
      return localFileChannel;
    }
    finally
    {
      if (i == 0)
      {
        localResourceRequest.request(-1L, localResourceIdImpl);
        try
        {
          localFileChannel.close();
        }
        catch (IOException localIOException5) {}
      }
    }
  }
  
  @InstrumentationMethod
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    int k = 0;
    try
    {
      k = read(paramByteBuffer);
      j = Math.max(k, 0);
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
    return k;
  }
  
  @InstrumentationMethod
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      return read(paramArrayOfByteBuffer, paramInt1, paramInt2);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    long l1 = 0L;
    int i = 0;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++) {
      i += paramArrayOfByteBuffer[k].remaining();
    }
    try
    {
      l1 = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l1 < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    long l2 = 0L;
    long l3 = 0L;
    try
    {
      l3 = read(paramArrayOfByteBuffer, paramInt1, paramInt2);
      l2 = Math.max(l3, 0L);
    }
    finally
    {
      localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
    }
    return l3;
  }
  
  @InstrumentationMethod
  public int read(ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    int k = 0;
    try
    {
      k = read(paramByteBuffer, paramLong);
      j = Math.max(k, 0);
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
    return k;
  }
  
  @InstrumentationMethod
  public int write(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    try
    {
      j = write(paramByteBuffer);
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
    return j;
  }
  
  @InstrumentationMethod
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByteBuffer.length - paramInt2)) {
      return write(paramArrayOfByteBuffer, paramInt1, paramInt2);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    long l1 = 0L;
    int i = 0;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++) {
      i += paramArrayOfByteBuffer[k].remaining();
    }
    try
    {
      l1 = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l1 < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    long l2 = 0L;
    try
    {
      l2 = Math.max(write(paramArrayOfByteBuffer, paramInt1, paramInt2), 0L);
    }
    finally
    {
      localResourceRequest.request(-(l1 - l2), localResourceIdImpl);
    }
    return l2;
  }
  
  @InstrumentationMethod
  public int write(ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(path);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new IOException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      throw new IOException("Resource limited", localResourceRequestDeniedException);
    }
    int j = 0;
    try
    {
      j = write(paramByteBuffer, paramLong);
    }
    finally
    {
      localResourceRequest.request(-(l - j), localResourceIdImpl);
    }
    return j;
  }
  
  /* Error */
  @InstrumentationMethod
  protected void implCloseChannel()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 135	jdk/management/resource/internal/inst/FileChannelImplRMHooks:implCloseChannel	()V
    //   4: aload_0
    //   5: getfield 121	jdk/management/resource/internal/inst/FileChannelImplRMHooks:path	Ljava/lang/String;
    //   8: invokestatic 134	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   11: astore_1
    //   12: getstatic 116	jdk/management/resource/internal/ApproverGroup:FILEDESCRIPTOR_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   15: aload_0
    //   16: getfield 120	jdk/management/resource/internal/inst/FileChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   19: invokevirtual 133	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   22: astore_2
    //   23: aload_2
    //   24: ldc2_w 57
    //   27: aload_1
    //   28: invokeinterface 144 4 0
    //   33: pop2
    //   34: getstatic 117	jdk/management/resource/internal/ApproverGroup:FILE_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   37: aload_0
    //   38: invokevirtual 133	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   41: astore_2
    //   42: aload_2
    //   43: ldc2_w 57
    //   46: aload_1
    //   47: invokeinterface 144 4 0
    //   52: pop2
    //   53: goto +62 -> 115
    //   56: astore_3
    //   57: aload_0
    //   58: getfield 121	jdk/management/resource/internal/inst/FileChannelImplRMHooks:path	Ljava/lang/String;
    //   61: invokestatic 134	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   64: astore 4
    //   66: getstatic 116	jdk/management/resource/internal/ApproverGroup:FILEDESCRIPTOR_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   69: aload_0
    //   70: getfield 120	jdk/management/resource/internal/inst/FileChannelImplRMHooks:fd	Ljava/io/FileDescriptor;
    //   73: invokevirtual 133	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   76: astore 5
    //   78: aload 5
    //   80: ldc2_w 57
    //   83: aload 4
    //   85: invokeinterface 144 4 0
    //   90: pop2
    //   91: getstatic 117	jdk/management/resource/internal/ApproverGroup:FILE_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   94: aload_0
    //   95: invokevirtual 133	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   98: astore 5
    //   100: aload 5
    //   102: ldc2_w 57
    //   105: aload 4
    //   107: invokeinterface 144 4 0
    //   112: pop2
    //   113: aload_3
    //   114: athrow
    //   115: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	116	0	this	FileChannelImplRMHooks
    //   11	36	1	localResourceIdImpl1	ResourceIdImpl
    //   22	21	2	localResourceRequest1	ResourceRequest
    //   56	58	3	localObject	Object
    //   64	42	4	localResourceIdImpl2	ResourceIdImpl
    //   76	25	5	localResourceRequest2	ResourceRequest
    // Exception table:
    //   from	to	target	type
    //   0	4	56	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\FileChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */