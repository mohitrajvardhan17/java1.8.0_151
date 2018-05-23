package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import java.nio.channels.SelectionKey;
import org.omg.CORBA.INTERNAL;

public abstract class EventHandlerBase
  implements EventHandler
{
  protected ORB orb;
  protected Work work;
  protected boolean useWorkerThreadForEvent;
  protected boolean useSelectThreadToWait;
  protected SelectionKey selectionKey;
  
  public EventHandlerBase() {}
  
  public void setUseSelectThreadToWait(boolean paramBoolean)
  {
    useSelectThreadToWait = paramBoolean;
  }
  
  public boolean shouldUseSelectThreadToWait()
  {
    return useSelectThreadToWait;
  }
  
  public void setSelectionKey(SelectionKey paramSelectionKey)
  {
    selectionKey = paramSelectionKey;
  }
  
  public SelectionKey getSelectionKey()
  {
    return selectionKey;
  }
  
  public void handleEvent()
  {
    if (orb.transportDebugFlag) {
      dprint(".handleEvent->: " + this);
    }
    getSelectionKey().interestOps(getSelectionKey().interestOps() & (getInterestOps() ^ 0xFFFFFFFF));
    if (shouldUseWorkerThreadForEvent())
    {
      Object localObject = null;
      try
      {
        if (orb.transportDebugFlag) {
          dprint(".handleEvent: addWork to pool: 0");
        }
        orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork(getWork());
      }
      catch (NoSuchThreadPoolException localNoSuchThreadPoolException)
      {
        localObject = localNoSuchThreadPoolException;
      }
      catch (NoSuchWorkQueueException localNoSuchWorkQueueException)
      {
        localObject = localNoSuchWorkQueueException;
      }
      if (localObject != null)
      {
        if (orb.transportDebugFlag) {
          dprint(".handleEvent: " + localObject);
        }
        INTERNAL localINTERNAL = new INTERNAL("NoSuchThreadPoolException");
        localINTERNAL.initCause((Throwable)localObject);
        throw localINTERNAL;
      }
    }
    else
    {
      if (orb.transportDebugFlag) {
        dprint(".handleEvent: doWork");
      }
      getWork().doWork();
    }
    if (orb.transportDebugFlag) {
      dprint(".handleEvent<-: " + this);
    }
  }
  
  public boolean shouldUseWorkerThreadForEvent()
  {
    return useWorkerThreadForEvent;
  }
  
  public void setUseWorkerThreadForEvent(boolean paramBoolean)
  {
    useWorkerThreadForEvent = paramBoolean;
  }
  
  public void setWork(Work paramWork)
  {
    work = paramWork;
  }
  
  public Work getWork()
  {
    return work;
  }
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint("EventHandlerBase", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\EventHandlerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */