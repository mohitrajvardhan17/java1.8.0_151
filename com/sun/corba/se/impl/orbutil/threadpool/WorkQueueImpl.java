package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import java.util.LinkedList;

public class WorkQueueImpl
  implements WorkQueue
{
  private ThreadPool workerThreadPool;
  private LinkedList theWorkQueue = new LinkedList();
  private long workItemsAdded = 0L;
  private long workItemsDequeued = 1L;
  private long totalTimeInQueue = 0L;
  private String name;
  private MonitoredObject workqueueMonitoredObject;
  
  public WorkQueueImpl()
  {
    name = "default-workqueue";
    initializeMonitoring();
  }
  
  public WorkQueueImpl(ThreadPool paramThreadPool)
  {
    this(paramThreadPool, "default-workqueue");
  }
  
  public WorkQueueImpl(ThreadPool paramThreadPool, String paramString)
  {
    workerThreadPool = paramThreadPool;
    name = paramString;
    initializeMonitoring();
  }
  
  private void initializeMonitoring()
  {
    workqueueMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(name, "Monitoring for a Work Queue");
    LongMonitoredAttributeBase local1 = new LongMonitoredAttributeBase("totalWorkItemsAdded", "Total number of Work items added to the Queue")
    {
      public Object getValue()
      {
        return new Long(totalWorkItemsAdded());
      }
    };
    workqueueMonitoredObject.addAttribute(local1);
    LongMonitoredAttributeBase local2 = new LongMonitoredAttributeBase("workItemsInQueue", "Number of Work items in the Queue to be processed")
    {
      public Object getValue()
      {
        return new Long(workItemsInQueue());
      }
    };
    workqueueMonitoredObject.addAttribute(local2);
    LongMonitoredAttributeBase local3 = new LongMonitoredAttributeBase("averageTimeInQueue", "Average time a work item waits in the work queue")
    {
      public Object getValue()
      {
        return new Long(averageTimeInQueue());
      }
    };
    workqueueMonitoredObject.addAttribute(local3);
  }
  
  MonitoredObject getMonitoredObject()
  {
    return workqueueMonitoredObject;
  }
  
  public synchronized void addWork(Work paramWork)
  {
    workItemsAdded += 1L;
    paramWork.setEnqueueTime(System.currentTimeMillis());
    theWorkQueue.addLast(paramWork);
    ((ThreadPoolImpl)workerThreadPool).notifyForAvailableWork(this);
  }
  
  synchronized Work requestWork(long paramLong)
    throws TimeoutException, InterruptedException
  {
    ((ThreadPoolImpl)workerThreadPool).incrementNumberOfAvailableThreads();
    Work localWork;
    if (theWorkQueue.size() != 0)
    {
      localWork = (Work)theWorkQueue.removeFirst();
      totalTimeInQueue += System.currentTimeMillis() - localWork.getEnqueueTime();
      workItemsDequeued += 1L;
      ((ThreadPoolImpl)workerThreadPool).decrementNumberOfAvailableThreads();
      return localWork;
    }
    try
    {
      long l1 = paramLong;
      long l2 = System.currentTimeMillis() + paramLong;
      do
      {
        wait(l1);
        if (theWorkQueue.size() != 0)
        {
          localWork = (Work)theWorkQueue.removeFirst();
          totalTimeInQueue += System.currentTimeMillis() - localWork.getEnqueueTime();
          workItemsDequeued += 1L;
          ((ThreadPoolImpl)workerThreadPool).decrementNumberOfAvailableThreads();
          return localWork;
        }
        l1 = l2 - System.currentTimeMillis();
      } while (l1 > 0L);
      ((ThreadPoolImpl)workerThreadPool).decrementNumberOfAvailableThreads();
      throw new TimeoutException();
    }
    catch (InterruptedException localInterruptedException)
    {
      ((ThreadPoolImpl)workerThreadPool).decrementNumberOfAvailableThreads();
      throw localInterruptedException;
    }
  }
  
  public void setThreadPool(ThreadPool paramThreadPool)
  {
    workerThreadPool = paramThreadPool;
  }
  
  public ThreadPool getThreadPool()
  {
    return workerThreadPool;
  }
  
  public long totalWorkItemsAdded()
  {
    return workItemsAdded;
  }
  
  public int workItemsInQueue()
  {
    return theWorkQueue.size();
  }
  
  public synchronized long averageTimeInQueue()
  {
    return totalTimeInQueue / workItemsDequeued;
  }
  
  public String getName()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\threadpool\WorkQueueImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */