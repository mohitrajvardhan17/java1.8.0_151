package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.ThreadLocalContainerResolver;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Engine
{
  private volatile Executor threadPool;
  public final String id;
  private final Container container;
  
  String getId()
  {
    return id;
  }
  
  Container getContainer()
  {
    return container;
  }
  
  Executor getExecutor()
  {
    return threadPool;
  }
  
  public Engine(String paramString, Executor paramExecutor)
  {
    this(paramString, ContainerResolver.getDefault().getContainer(), paramExecutor);
  }
  
  public Engine(String paramString, Container paramContainer, Executor paramExecutor)
  {
    this(paramString, paramContainer);
    threadPool = (paramExecutor != null ? wrap(paramExecutor) : null);
  }
  
  public Engine(String paramString)
  {
    this(paramString, ContainerResolver.getDefault().getContainer());
  }
  
  public Engine(String paramString, Container paramContainer)
  {
    id = paramString;
    container = paramContainer;
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    threadPool = (paramExecutor != null ? wrap(paramExecutor) : null);
  }
  
  void addRunnable(Fiber paramFiber)
  {
    if (threadPool == null) {
      synchronized (this)
      {
        threadPool = wrap(Executors.newCachedThreadPool(new DaemonThreadFactory()));
      }
    }
    threadPool.execute(paramFiber);
  }
  
  private Executor wrap(Executor paramExecutor)
  {
    return ContainerResolver.getDefault().wrapExecutor(container, paramExecutor);
  }
  
  public Fiber createFiber()
  {
    return new Fiber(this);
  }
  
  private static class DaemonThreadFactory
    implements ThreadFactory
  {
    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix = "jaxws-engine-" + poolNumber.getAndIncrement() + "-thread-";
    
    DaemonThreadFactory() {}
    
    public Thread newThread(Runnable paramRunnable)
    {
      Thread localThread = new Thread(null, paramRunnable, namePrefix + threadNumber.getAndIncrement(), 0L);
      if (!localThread.isDaemon()) {
        localThread.setDaemon(true);
      }
      if (localThread.getPriority() != 5) {
        localThread.setPriority(5);
      }
      return localThread;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\Engine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */