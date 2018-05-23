package jdk.management.resource.internal.inst;

import java.security.AccessControlContext;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import jdk.management.resource.internal.SimpleResourceContext;

@InstrumentationTarget("java.lang.Thread")
public final class ThreadRMHooks
{
  private long tid;
  
  public ThreadRMHooks() {}
  
  private static synchronized long nextThreadID()
  {
    return 0L;
  }
  
  @InstrumentationMethod
  private void init(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, long paramLong, AccessControlContext paramAccessControlContext, boolean paramBoolean)
  {
    long l1 = nextThreadID();
    ResourceIdImpl localResourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    ResourceRequest localResourceRequest = ApproverGroup.THREAD_CREATED_GROUP.getApprover(this);
    long l2 = 1L;
    long l3 = 0L;
    try
    {
      l3 = localResourceRequest.request(l2, localResourceIdImpl);
      if (l3 == 0L) {
        throw new ResourceRequestDeniedException("Resource limited: thread creation denied by resource manager");
      }
      init(paramThreadGroup, paramRunnable, paramString, paramLong, paramAccessControlContext, paramBoolean);
      SimpleResourceContext localSimpleResourceContext = (SimpleResourceContext)SimpleResourceContext.getThreadContext(Thread.currentThread());
      ThreadRMHooks localThreadRMHooks = this;
      localSimpleResourceContext.bindNewThreadContext((Thread)localThreadRMHooks);
    }
    finally
    {
      localResourceRequest.request(-(l3 - l2), localResourceIdImpl);
    }
    tid = l1;
  }
  
  /* Error */
  @InstrumentationMethod
  private void exit()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 85	jdk/management/resource/internal/inst/ThreadRMHooks:tid	J
    //   4: invokestatic 86	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   7: invokestatic 91	jdk/management/resource/internal/ResourceIdImpl:of	(Ljava/lang/Object;)Ljdk/management/resource/internal/ResourceIdImpl;
    //   10: astore_1
    //   11: getstatic 84	jdk/management/resource/internal/ApproverGroup:THREAD_CREATED_GROUP	Ljdk/management/resource/internal/ApproverGroup;
    //   14: aload_0
    //   15: invokevirtual 90	jdk/management/resource/internal/ApproverGroup:getApprover	(Ljava/lang/Object;)Ljdk/management/resource/ResourceRequest;
    //   18: astore_2
    //   19: aload_2
    //   20: ldc2_w 41
    //   23: aload_1
    //   24: invokeinterface 98 4 0
    //   29: pop2
    //   30: invokestatic 92	jdk/management/resource/internal/SimpleResourceContext:removeThreadContext	()V
    //   33: aload_0
    //   34: invokespecial 96	jdk/management/resource/internal/inst/ThreadRMHooks:exit	()V
    //   37: goto +10 -> 47
    //   40: astore_3
    //   41: aload_0
    //   42: invokespecial 96	jdk/management/resource/internal/inst/ThreadRMHooks:exit	()V
    //   45: aload_3
    //   46: athrow
    //   47: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	ThreadRMHooks
    //   10	14	1	localResourceIdImpl	ResourceIdImpl
    //   18	2	2	localResourceRequest	ResourceRequest
    //   40	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   19	33	40	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\ThreadRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */