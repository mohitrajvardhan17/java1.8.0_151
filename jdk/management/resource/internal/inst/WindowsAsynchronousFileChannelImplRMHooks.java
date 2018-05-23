package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.ThreadPool;

@InstrumentationTarget("sun.nio.ch.WindowsAsynchronousFileChannelImpl")
public final class WindowsAsynchronousFileChannelImplRMHooks
{
  protected final FileDescriptor fdObj = null;
  protected final ReadWriteLock closeLock = new ReentrantReadWriteLock();
  protected volatile boolean closed;
  
  public WindowsAsynchronousFileChannelImplRMHooks() {}
  
  @InstrumentationMethod
  public static AsynchronousFileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ThreadPool paramThreadPool)
  {
    localAsynchronousFileChannel = open(paramFileDescriptor, paramBoolean1, paramBoolean2, paramThreadPool);
    JavaIOFileDescriptorAccess localJavaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    long l1;
    try
    {
      l1 = localJavaIOFileDescriptorAccess.getHandle(paramFileDescriptor);
      if (l1 == -1L) {
        l1 = localJavaIOFileDescriptorAccess.get(paramFileDescriptor);
      }
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      l1 = localJavaIOFileDescriptorAccess.get(paramFileDescriptor);
    }
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    ResourceRequest localResourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor);
    long l2 = 0L;
    int i = 0;
    try
    {
      l2 = localResourceRequest.request(1L, localResourceIdImpl);
      if (l2 < 1L) {
        throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors");
      }
      i = 1;
      if (i == 0)
      {
        localResourceRequest.request(-1L, localResourceIdImpl);
        try
        {
          localAsynchronousFileChannel.close();
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
          localAsynchronousFileChannel.close();
        }
        catch (IOException localIOException4) {}
      }
    }
    localResourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(localAsynchronousFileChannel);
    try
    {
      l2 = localResourceRequest.request(1L, localResourceIdImpl);
      if (l2 < 1L)
      {
        try
        {
          localAsynchronousFileChannel.close();
        }
        catch (IOException localIOException2) {}
        throw new ResourceRequestDeniedException("Resource limited: too many open files");
      }
      i = 1;
      return localAsynchronousFileChannel;
    }
    finally
    {
      if (i == 0)
      {
        localResourceRequest.request(-1L, localResourceIdImpl);
        try
        {
          localAsynchronousFileChannel.close();
        }
        catch (IOException localIOException5) {}
      }
    }
  }
  
  @InstrumentationMethod
  <A> Future<Integer> implRead(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(fdObj);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      if (paramCompletionHandler != null)
      {
        paramCompletionHandler.failed(localResourceRequestDeniedException, paramA);
        return null;
      }
      localObject = new CompletableFuture();
      ((CompletableFuture)localObject).completeExceptionally(localResourceRequestDeniedException);
      return (Future<Integer>)localObject;
    }
    CompletionHandlerWrapper localCompletionHandlerWrapper = null;
    if (paramCompletionHandler != null) {
      localCompletionHandlerWrapper = new CompletionHandlerWrapper(paramCompletionHandler, localResourceIdImpl, localResourceRequest, l);
    }
    Object localObject = implRead(paramByteBuffer, paramLong, paramA, localCompletionHandlerWrapper);
    if (paramCompletionHandler == null) {
      if (((Future)localObject).isDone())
      {
        int j = 0;
        try
        {
          j = ((Integer)((Future)localObject).get()).intValue();
        }
        catch (InterruptedException|ExecutionException localInterruptedException) {}
        j = Math.max(0, j);
        localResourceRequest.request(-(l - j), localResourceIdImpl);
      }
      else
      {
        localObject = new FutureWrapper((Future)localObject, localResourceIdImpl, localResourceRequest, l);
      }
    }
    return (Future<Integer>)localObject;
  }
  
  @InstrumentationMethod
  <A> Future<Integer> implWrite(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(fdObj);
    ResourceRequest localResourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try
    {
      l = Math.max(localResourceRequest.request(i, localResourceIdImpl), 0L);
      if (l < i) {
        throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved");
      }
    }
    catch (ResourceRequestDeniedException localResourceRequestDeniedException)
    {
      if (paramCompletionHandler != null)
      {
        paramCompletionHandler.failed(localResourceRequestDeniedException, paramA);
        return null;
      }
      localObject = new CompletableFuture();
      ((CompletableFuture)localObject).completeExceptionally(localResourceRequestDeniedException);
      return (Future<Integer>)localObject;
    }
    CompletionHandlerWrapper localCompletionHandlerWrapper = null;
    if (paramCompletionHandler != null) {
      localCompletionHandlerWrapper = new CompletionHandlerWrapper(paramCompletionHandler, localResourceIdImpl, localResourceRequest, l);
    }
    Object localObject = implWrite(paramByteBuffer, paramLong, paramA, localCompletionHandlerWrapper);
    if (paramCompletionHandler == null) {
      if (((Future)localObject).isDone())
      {
        int j = 0;
        try
        {
          j = ((Integer)((Future)localObject).get()).intValue();
        }
        catch (InterruptedException|ExecutionException localInterruptedException) {}
        j = Math.max(0, j);
        localResourceRequest.request(-(l - j), localResourceIdImpl);
      }
      else
      {
        localObject = new FutureWrapper((Future)localObject, localResourceIdImpl, localResourceRequest, l);
      }
    }
    return (Future<Integer>)localObject;
  }
  
  /* Error */
  @InstrumentationMethod
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 173	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:closeLock	Ljava/util/concurrent/locks/ReadWriteLock;
    //   4: invokeinterface 200 1 0
    //   9: invokeinterface 198 1 0
    //   14: aload_0
    //   15: getfield 171	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:closed	Z
    //   18: ifeq +18 -> 36
    //   21: aload_0
    //   22: getfield 173	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:closeLock	Ljava/util/concurrent/locks/ReadWriteLock;
    //   25: invokeinterface 200 1 0
    //   30: invokeinterface 199 1 0
    //   35: return
    //   36: aload_0
    //   37: getfield 173	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:closeLock	Ljava/util/concurrent/locks/ReadWriteLock;
    //   40: invokeinterface 200 1 0
    //   45: invokeinterface 199 1 0
    //   50: goto +20 -> 70
    //   53: astore_1
    //   54: aload_0
    //   55: getfield 173	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:closeLock	Ljava/util/concurrent/locks/ReadWriteLock;
    //   58: invokeinterface 200 1 0
    //   63: invokeinterface 199 1 0
    //   68: aload_1
    //   69: athrow
    //   70: aload_0
    //   71: invokevirtual 190	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:close	()V
    //   74: invokestatic 194	sun/misc/SharedSecrets:getJavaIOFileDescriptorAccess	()Lsun/misc/JavaIOFileDescriptorAccess;
    //   77: astore_1
    //   78: aload_1
    //   79: aload_0
    //   80: getfield 172	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:fdObj	Ljava/io/FileDescriptor;
    //   83: invokeinterface 203 2 0
    //   88: lstore_2
    //   89: lload_2
    //   90: ldc2_w 80
    //   93: lcmp
    //   94: ifne +15 -> 109
    //   97: aload_1
    //   98: aload_0
    //   99: getfield 172	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:fdObj	Ljava/io/FileDescriptor;
    //   102: invokeinterface 202 2 0
    //   107: i2l
    //   108: lstore_2
    //   109: lload_2
    //   110: invokestatic 175	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   113: invokestatic 189	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   116: astore 4
    //   118: getstatic 167	jdk/management/resource/internal/ApproverGroup:FILEDESCRIPTOR_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   121: aload_0
    //   122: getfield 172	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:fdObj	Ljava/io/FileDescriptor;
    //   125: invokevirtual 185	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   128: astore 5
    //   130: aload 5
    //   132: ldc2_w 80
    //   135: aload 4
    //   137: invokeinterface 201 4 0
    //   142: pop2
    //   143: getstatic 168	jdk/management/resource/internal/ApproverGroup:FILE_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   146: aload_0
    //   147: invokevirtual 185	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   150: astore 5
    //   152: aload 5
    //   154: ldc2_w 80
    //   157: aload 4
    //   159: invokeinterface 201 4 0
    //   164: pop2
    //   165: goto +106 -> 271
    //   168: astore 6
    //   170: invokestatic 194	sun/misc/SharedSecrets:getJavaIOFileDescriptorAccess	()Lsun/misc/JavaIOFileDescriptorAccess;
    //   173: astore 7
    //   175: aload 7
    //   177: aload_0
    //   178: getfield 172	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:fdObj	Ljava/io/FileDescriptor;
    //   181: invokeinterface 203 2 0
    //   186: lstore 8
    //   188: lload 8
    //   190: ldc2_w 80
    //   193: lcmp
    //   194: ifne +17 -> 211
    //   197: aload 7
    //   199: aload_0
    //   200: getfield 172	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:fdObj	Ljava/io/FileDescriptor;
    //   203: invokeinterface 202 2 0
    //   208: i2l
    //   209: lstore 8
    //   211: lload 8
    //   213: invokestatic 175	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   216: invokestatic 189	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   219: astore 10
    //   221: getstatic 167	jdk/management/resource/internal/ApproverGroup:FILEDESCRIPTOR_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   224: aload_0
    //   225: getfield 172	jdk/management/resource/internal/inst/WindowsAsynchronousFileChannelImplRMHooks:fdObj	Ljava/io/FileDescriptor;
    //   228: invokevirtual 185	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   231: astore 11
    //   233: aload 11
    //   235: ldc2_w 80
    //   238: aload 10
    //   240: invokeinterface 201 4 0
    //   245: pop2
    //   246: getstatic 168	jdk/management/resource/internal/ApproverGroup:FILE_OPEN_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   249: aload_0
    //   250: invokevirtual 185	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   253: astore 11
    //   255: aload 11
    //   257: ldc2_w 80
    //   260: aload 10
    //   262: invokeinterface 201 4 0
    //   267: pop2
    //   268: aload 6
    //   270: athrow
    //   271: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	272	0	this	WindowsAsynchronousFileChannelImplRMHooks
    //   53	16	1	localObject1	Object
    //   77	21	1	localJavaIOFileDescriptorAccess1	JavaIOFileDescriptorAccess
    //   88	22	2	l1	long
    //   116	42	4	localResourceIdImpl1	ResourceIdImpl
    //   128	25	5	localResourceRequest1	ResourceRequest
    //   168	101	6	localObject2	Object
    //   173	25	7	localJavaIOFileDescriptorAccess2	JavaIOFileDescriptorAccess
    //   186	26	8	l2	long
    //   219	42	10	localResourceIdImpl2	ResourceIdImpl
    //   231	25	11	localResourceRequest2	ResourceRequest
    // Exception table:
    //   from	to	target	type
    //   14	21	53	finally
    //   70	74	168	finally
    //   168	170	168	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\WindowsAsynchronousFileChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */