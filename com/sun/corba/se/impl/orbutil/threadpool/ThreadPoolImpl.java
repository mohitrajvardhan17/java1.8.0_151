package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import java.io.Closeable;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPoolImpl
  implements ThreadPool
{
  private static AtomicInteger threadCounter = new AtomicInteger(0);
  private static final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");
  private WorkQueue workQueue;
  private int availableWorkerThreads = 0;
  private int currentThreadCount = 0;
  private int minWorkerThreads = 0;
  private int maxWorkerThreads = 0;
  private long inactivityTimeout;
  private boolean boundedThreadPool = false;
  private AtomicLong processedCount = new AtomicLong(1L);
  private AtomicLong totalTimeTaken = new AtomicLong(0L);
  private String name;
  private MonitoredObject threadpoolMonitoredObject;
  private ThreadGroup threadGroup;
  Object workersLock = new Object();
  List<WorkerThread> workers = new ArrayList();
  
  public ThreadPoolImpl(ThreadGroup paramThreadGroup, String paramString)
  {
    inactivityTimeout = 120000L;
    maxWorkerThreads = Integer.MAX_VALUE;
    workQueue = new WorkQueueImpl(this);
    threadGroup = paramThreadGroup;
    name = paramString;
    initializeMonitoring();
  }
  
  public ThreadPoolImpl(String paramString)
  {
    this(Thread.currentThread().getThreadGroup(), paramString);
  }
  
  public ThreadPoolImpl(int paramInt1, int paramInt2, long paramLong, String paramString)
  {
    minWorkerThreads = paramInt1;
    maxWorkerThreads = paramInt2;
    inactivityTimeout = paramLong;
    boundedThreadPool = true;
    workQueue = new WorkQueueImpl(this);
    name = paramString;
    for (int i = 0; i < minWorkerThreads; i++) {
      createWorkerThread();
    }
    initializeMonitoring();
  }
  
  public void close()
    throws IOException
  {
    ArrayList localArrayList = null;
    synchronized (workersLock)
    {
      localArrayList = new ArrayList(workers);
    }
    ??? = localArrayList.iterator();
    while (((Iterator)???).hasNext())
    {
      WorkerThread localWorkerThread = (WorkerThread)((Iterator)???).next();
      localWorkerThread.close();
      while (localWorkerThread.getState() != Thread.State.TERMINATED) {
        try
        {
          localWorkerThread.join();
        }
        catch (InterruptedException localInterruptedException)
        {
          wrapper.interruptedJoinCallWhileClosingThreadPool(localInterruptedException, localWorkerThread, this);
        }
      }
    }
    threadGroup = null;
  }
  
  private void initializeMonitoring()
  {
    MonitoredObject localMonitoredObject1 = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", null).getRootMonitoredObject();
    MonitoredObject localMonitoredObject2 = localMonitoredObject1.getChild("threadpool");
    if (localMonitoredObject2 == null)
    {
      localMonitoredObject2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("threadpool", "Monitoring for all ThreadPool instances");
      localMonitoredObject1.addChild(localMonitoredObject2);
    }
    threadpoolMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(name, "Monitoring for a ThreadPool");
    localMonitoredObject2.addChild(threadpoolMonitoredObject);
    LongMonitoredAttributeBase local1 = new LongMonitoredAttributeBase("currentNumberOfThreads", "Current number of total threads in the ThreadPool")
    {
      public Object getValue()
      {
        return new Long(currentNumberOfThreads());
      }
    };
    threadpoolMonitoredObject.addAttribute(local1);
    LongMonitoredAttributeBase local2 = new LongMonitoredAttributeBase("numberOfAvailableThreads", "Current number of total threads in the ThreadPool")
    {
      public Object getValue()
      {
        return new Long(numberOfAvailableThreads());
      }
    };
    threadpoolMonitoredObject.addAttribute(local2);
    LongMonitoredAttributeBase local3 = new LongMonitoredAttributeBase("numberOfBusyThreads", "Number of busy threads in the ThreadPool")
    {
      public Object getValue()
      {
        return new Long(numberOfBusyThreads());
      }
    };
    threadpoolMonitoredObject.addAttribute(local3);
    LongMonitoredAttributeBase local4 = new LongMonitoredAttributeBase("averageWorkCompletionTime", "Average elapsed time taken to complete a work item by the ThreadPool")
    {
      public Object getValue()
      {
        return new Long(averageWorkCompletionTime());
      }
    };
    threadpoolMonitoredObject.addAttribute(local4);
    LongMonitoredAttributeBase local5 = new LongMonitoredAttributeBase("currentProcessedCount", "Number of Work items processed by the ThreadPool")
    {
      public Object getValue()
      {
        return new Long(currentProcessedCount());
      }
    };
    threadpoolMonitoredObject.addAttribute(local5);
    threadpoolMonitoredObject.addChild(((WorkQueueImpl)workQueue).getMonitoredObject());
  }
  
  MonitoredObject getMonitoredObject()
  {
    return threadpoolMonitoredObject;
  }
  
  public WorkQueue getAnyWorkQueue()
  {
    return workQueue;
  }
  
  public WorkQueue getWorkQueue(int paramInt)
    throws NoSuchWorkQueueException
  {
    if (paramInt != 0) {
      throw new NoSuchWorkQueueException();
    }
    return workQueue;
  }
  
  void notifyForAvailableWork(WorkQueue paramWorkQueue)
  {
    synchronized (paramWorkQueue)
    {
      if (availableWorkerThreads < paramWorkQueue.workItemsInQueue()) {
        createWorkerThread();
      } else {
        paramWorkQueue.notify();
      }
    }
  }
  
  private Thread createWorkerThreadHelper(String paramString)
  {
    WorkerThread localWorkerThread = new WorkerThread(threadGroup, paramString);
    synchronized (workersLock)
    {
      workers.add(localWorkerThread);
    }
    localWorkerThread.setDaemon(true);
    wrapper.workerThreadCreated(localWorkerThread, localWorkerThread.getContextClassLoader());
    localWorkerThread.start();
    return null;
  }
  
  void createWorkerThread()
  {
    final String str = getName();
    synchronized (workQueue)
    {
      try
      {
        if (System.getSecurityManager() == null) {
          createWorkerThreadHelper(str);
        } else {
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Object run()
            {
              return ThreadPoolImpl.this.createWorkerThreadHelper(str);
            }
          });
        }
      }
      catch (Throwable localThrowable)
      {
        decrementCurrentNumberOfThreads();
        wrapper.workerThreadCreationFailure(localThrowable);
      }
      finally
      {
        incrementCurrentNumberOfThreads();
      }
    }
  }
  
  public int minimumNumberOfThreads()
  {
    return minWorkerThreads;
  }
  
  public int maximumNumberOfThreads()
  {
    return maxWorkerThreads;
  }
  
  public long idleTimeoutForThreads()
  {
    return inactivityTimeout;
  }
  
  /* Error */
  public int currentNumberOfThreads()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 306	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:workQueue	Lcom/sun/corba/se/spi/orbutil/threadpool/WorkQueue;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 299	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:currentThreadCount	I
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	ThreadPoolImpl
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  void decrementCurrentNumberOfThreads()
  {
    synchronized (workQueue)
    {
      currentThreadCount -= 1;
    }
  }
  
  void incrementCurrentNumberOfThreads()
  {
    synchronized (workQueue)
    {
      currentThreadCount += 1;
    }
  }
  
  /* Error */
  public int numberOfAvailableThreads()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 306	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:workQueue	Lcom/sun/corba/se/spi/orbutil/threadpool/WorkQueue;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 298	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:availableWorkerThreads	I
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	ThreadPoolImpl
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public int numberOfBusyThreads()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 306	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:workQueue	Lcom/sun/corba/se/spi/orbutil/threadpool/WorkQueue;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 299	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:currentThreadCount	I
    //   11: aload_0
    //   12: getfield 298	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:availableWorkerThreads	I
    //   15: isub
    //   16: aload_1
    //   17: monitorexit
    //   18: ireturn
    //   19: astore_2
    //   20: aload_1
    //   21: monitorexit
    //   22: aload_2
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	ThreadPoolImpl
    //   5	16	1	Ljava/lang/Object;	Object
    //   19	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	18	19	finally
    //   19	22	19	finally
  }
  
  /* Error */
  public long averageWorkCompletionTime()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 306	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:workQueue	Lcom/sun/corba/se/spi/orbutil/threadpool/WorkQueue;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 313	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:totalTimeTaken	Ljava/util/concurrent/atomic/AtomicLong;
    //   11: invokevirtual 355	java/util/concurrent/atomic/AtomicLong:get	()J
    //   14: aload_0
    //   15: getfield 312	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:processedCount	Ljava/util/concurrent/atomic/AtomicLong;
    //   18: invokevirtual 355	java/util/concurrent/atomic/AtomicLong:get	()J
    //   21: ldiv
    //   22: aload_1
    //   23: monitorexit
    //   24: lreturn
    //   25: astore_2
    //   26: aload_1
    //   27: monitorexit
    //   28: aload_2
    //   29: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	ThreadPoolImpl
    //   5	22	1	Ljava/lang/Object;	Object
    //   25	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	24	25	finally
    //   25	28	25	finally
  }
  
  /* Error */
  public long currentProcessedCount()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 306	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:workQueue	Lcom/sun/corba/se/spi/orbutil/threadpool/WorkQueue;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 312	com/sun/corba/se/impl/orbutil/threadpool/ThreadPoolImpl:processedCount	Ljava/util/concurrent/atomic/AtomicLong;
    //   11: invokevirtual 355	java/util/concurrent/atomic/AtomicLong:get	()J
    //   14: aload_1
    //   15: monitorexit
    //   16: lreturn
    //   17: astore_2
    //   18: aload_1
    //   19: monitorexit
    //   20: aload_2
    //   21: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	22	0	this	ThreadPoolImpl
    //   5	14	1	Ljava/lang/Object;	Object
    //   17	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	17	finally
    //   17	20	17	finally
  }
  
  public String getName()
  {
    return name;
  }
  
  public int numberOfWorkQueues()
  {
    return 1;
  }
  
  private static synchronized int getUniqueThreadId()
  {
    return threadCounter.incrementAndGet();
  }
  
  void decrementNumberOfAvailableThreads()
  {
    synchronized (workQueue)
    {
      availableWorkerThreads -= 1;
    }
  }
  
  void incrementNumberOfAvailableThreads()
  {
    synchronized (workQueue)
    {
      availableWorkerThreads += 1;
    }
  }
  
  private class WorkerThread
    extends Thread
    implements Closeable
  {
    private Work currentWork;
    private int threadId = 0;
    private volatile boolean closeCalled = false;
    private String threadPoolName;
    private StringBuffer workerThreadName = new StringBuffer();
    
    WorkerThread(ThreadGroup paramThreadGroup, String paramString)
    {
      super("Idle");
      threadPoolName = paramString;
      setName(composeWorkerThreadName(paramString, "Idle"));
    }
    
    public synchronized void close()
    {
      closeCalled = true;
      interrupt();
    }
    
    private void resetClassLoader() {}
    
    private void performWork()
    {
      long l1 = System.currentTimeMillis();
      try
      {
        currentWork.doWork();
      }
      catch (Throwable localThrowable)
      {
        ThreadPoolImpl.wrapper.workerThreadDoWorkThrowable(this, localThrowable);
      }
      long l2 = System.currentTimeMillis() - l1;
      totalTimeTaken.addAndGet(l2);
      processedCount.incrementAndGet();
    }
    
    public void run()
    {
      try
      {
        while (!closeCalled)
        {
          try
          {
            currentWork = ((WorkQueueImpl)workQueue).requestWork(inactivityTimeout);
            if (currentWork == null) {
              continue;
            }
          }
          catch (InterruptedException localInterruptedException)
          {
            ThreadPoolImpl.wrapper.workQueueThreadInterrupted(localInterruptedException, getName(), Boolean.valueOf(closeCalled));
            continue;
          }
          catch (Throwable localThrowable)
          {
            ThreadPoolImpl.wrapper.workerThreadThrowableFromRequestWork(this, localThrowable, workQueue.getName());
          }
          continue;
          performWork();
          currentWork = null;
          resetClassLoader();
        }
      }
      catch (Throwable localThrowable)
      {
        ThreadPoolImpl.wrapper.workerThreadCaughtUnexpectedThrowable(this, ???);
      }
      finally
      {
        synchronized (workersLock)
        {
          workers.remove(this);
        }
      }
    }
    
    private String composeWorkerThreadName(String paramString1, String paramString2)
    {
      workerThreadName.setLength(0);
      workerThreadName.append("p: ").append(paramString1);
      workerThreadName.append("; w: ").append(paramString2);
      return workerThreadName.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\threadpool\ThreadPoolImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */