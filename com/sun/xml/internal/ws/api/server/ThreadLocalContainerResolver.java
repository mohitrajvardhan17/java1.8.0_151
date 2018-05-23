package com.sun.xml.internal.ws.api.server;

import java.util.concurrent.Executor;

public class ThreadLocalContainerResolver
  extends ContainerResolver
{
  private ThreadLocal<Container> containerThreadLocal = new ThreadLocal()
  {
    protected Container initialValue()
    {
      return Container.NONE;
    }
  };
  
  public ThreadLocalContainerResolver() {}
  
  public Container getContainer()
  {
    return (Container)containerThreadLocal.get();
  }
  
  public Container enterContainer(Container paramContainer)
  {
    Container localContainer = (Container)containerThreadLocal.get();
    containerThreadLocal.set(paramContainer);
    return localContainer;
  }
  
  public void exitContainer(Container paramContainer)
  {
    containerThreadLocal.set(paramContainer);
  }
  
  public Executor wrapExecutor(final Container paramContainer, final Executor paramExecutor)
  {
    if (paramExecutor == null) {
      return null;
    }
    new Executor()
    {
      public void execute(final Runnable paramAnonymousRunnable)
      {
        paramExecutor.execute(new Runnable()
        {
          /* Error */
          public void run()
          {
            // Byte code:
            //   0: aload_0
            //   1: getfield 48	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2$1:this$1	Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2;
            //   4: getfield 47	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2:this$0	Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
            //   7: aload_0
            //   8: getfield 48	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2$1:this$1	Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2;
            //   11: getfield 46	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2:val$container	Lcom/sun/xml/internal/ws/api/server/Container;
            //   14: invokevirtual 51	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:enterContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)Lcom/sun/xml/internal/ws/api/server/Container;
            //   17: astore_1
            //   18: aload_0
            //   19: getfield 49	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2$1:val$command	Ljava/lang/Runnable;
            //   22: invokeinterface 53 1 0
            //   27: aload_0
            //   28: getfield 48	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2$1:this$1	Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2;
            //   31: getfield 47	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2:this$0	Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
            //   34: aload_1
            //   35: invokevirtual 50	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:exitContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)V
            //   38: goto +17 -> 55
            //   41: astore_2
            //   42: aload_0
            //   43: getfield 48	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2$1:this$1	Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2;
            //   46: getfield 47	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver$2:this$0	Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
            //   49: aload_1
            //   50: invokevirtual 50	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:exitContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)V
            //   53: aload_2
            //   54: athrow
            //   55: return
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	56	0	this	1
            //   17	33	1	localContainer	Container
            //   41	13	2	localObject	Object
            // Exception table:
            //   from	to	target	type
            //   18	27	41	finally
          }
        });
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\ThreadLocalContainerResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */