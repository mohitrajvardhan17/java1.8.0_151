package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.ListenerThread;
import com.sun.corba.se.pept.transport.ReaderThread;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SelectorImpl
  extends Thread
  implements com.sun.corba.se.pept.transport.Selector
{
  private ORB orb;
  private java.nio.channels.Selector selector;
  private long timeout;
  private List deferredRegistrations;
  private List interestOpsList;
  private HashMap listenerThreads;
  private Map readerThreads;
  private boolean selectorStarted;
  private volatile boolean closed;
  private ORBUtilSystemException wrapper;
  
  public SelectorImpl(ORB paramORB)
  {
    orb = paramORB;
    selector = null;
    selectorStarted = false;
    timeout = 60000L;
    deferredRegistrations = new ArrayList();
    interestOpsList = new ArrayList();
    listenerThreads = new HashMap();
    readerThreads = Collections.synchronizedMap(new HashMap());
    closed = false;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
  }
  
  public void setTimeout(long paramLong)
  {
    timeout = paramLong;
  }
  
  public long getTimeout()
  {
    return timeout;
  }
  
  public void registerInterestOps(EventHandler paramEventHandler)
  {
    if (orb.transportDebugFlag) {
      dprint(".registerInterestOps:-> " + paramEventHandler);
    }
    SelectionKey localSelectionKey = paramEventHandler.getSelectionKey();
    if (localSelectionKey.isValid())
    {
      int i = paramEventHandler.getInterestOps();
      SelectionKeyAndOp localSelectionKeyAndOp = new SelectionKeyAndOp(localSelectionKey, i);
      synchronized (interestOpsList)
      {
        interestOpsList.add(localSelectionKeyAndOp);
      }
      try
      {
        if (selector != null) {
          selector.wakeup();
        }
      }
      catch (Throwable localThrowable)
      {
        if (orb.transportDebugFlag) {
          dprint(".registerInterestOps: selector.wakeup: ", localThrowable);
        }
      }
    }
    else
    {
      wrapper.selectionKeyInvalid(paramEventHandler.toString());
      if (orb.transportDebugFlag) {
        dprint(".registerInterestOps: EventHandler SelectionKey not valid " + paramEventHandler);
      }
    }
    if (orb.transportDebugFlag) {
      dprint(".registerInterestOps:<- ");
    }
  }
  
  public void registerForEvent(EventHandler paramEventHandler)
  {
    if (orb.transportDebugFlag) {
      dprint(".registerForEvent: " + paramEventHandler);
    }
    if (isClosed())
    {
      if (orb.transportDebugFlag) {
        dprint(".registerForEvent: closed: " + paramEventHandler);
      }
      return;
    }
    if (paramEventHandler.shouldUseSelectThreadToWait())
    {
      synchronized (deferredRegistrations)
      {
        deferredRegistrations.add(paramEventHandler);
      }
      if (!selectorStarted) {
        startSelector();
      }
      selector.wakeup();
      return;
    }
    switch (paramEventHandler.getInterestOps())
    {
    case 16: 
      createListenerThread(paramEventHandler);
      break;
    case 1: 
      createReaderThread(paramEventHandler);
      break;
    default: 
      if (orb.transportDebugFlag) {
        dprint(".registerForEvent: default: " + paramEventHandler);
      }
      throw new RuntimeException("SelectorImpl.registerForEvent: unknown interest ops");
    }
  }
  
  public void unregisterForEvent(EventHandler paramEventHandler)
  {
    if (orb.transportDebugFlag) {
      dprint(".unregisterForEvent: " + paramEventHandler);
    }
    if (isClosed())
    {
      if (orb.transportDebugFlag) {
        dprint(".unregisterForEvent: closed: " + paramEventHandler);
      }
      return;
    }
    if (paramEventHandler.shouldUseSelectThreadToWait())
    {
      SelectionKey localSelectionKey;
      synchronized (deferredRegistrations)
      {
        localSelectionKey = paramEventHandler.getSelectionKey();
      }
      if (localSelectionKey != null) {
        localSelectionKey.cancel();
      }
      if (selector != null) {
        selector.wakeup();
      }
      return;
    }
    switch (paramEventHandler.getInterestOps())
    {
    case 16: 
      destroyListenerThread(paramEventHandler);
      break;
    case 1: 
      destroyReaderThread(paramEventHandler);
      break;
    default: 
      if (orb.transportDebugFlag) {
        dprint(".unregisterForEvent: default: " + paramEventHandler);
      }
      throw new RuntimeException("SelectorImpl.uregisterForEvent: unknown interest ops");
    }
  }
  
  public void close()
  {
    if (orb.transportDebugFlag) {
      dprint(".close");
    }
    if (isClosed())
    {
      if (orb.transportDebugFlag) {
        dprint(".close: already closed");
      }
      return;
    }
    setClosed(true);
    Iterator localIterator = listenerThreads.values().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (ListenerThread)localIterator.next();
      ((ListenerThread)localObject).close();
    }
    localIterator = readerThreads.values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (ReaderThread)localIterator.next();
      ((ReaderThread)localObject).close();
    }
    clearDeferredRegistrations();
    try
    {
      if (selector != null) {
        selector.wakeup();
      }
    }
    catch (Throwable localThrowable)
    {
      if (orb.transportDebugFlag) {
        dprint(".close: selector.wakeup: ", localThrowable);
      }
    }
  }
  
  public void run()
  {
    setName("SelectorThread");
    while (!closed) {
      try
      {
        int i = 0;
        if ((timeout == 0L) && (orb.transportDebugFlag)) {
          dprint(".run: Beginning of selection cycle");
        }
        handleDeferredRegistrations();
        enableInterestOps();
        try
        {
          i = selector.select(timeout);
        }
        catch (IOException localIOException)
        {
          if (orb.transportDebugFlag) {
            dprint(".run: selector.select: ", localIOException);
          }
        }
        catch (ClosedSelectorException localClosedSelectorException)
        {
          if (orb.transportDebugFlag) {
            dprint(".run: selector.select: ", localClosedSelectorException);
          }
          break;
        }
        if (closed) {
          break;
        }
        Iterator localIterator = selector.selectedKeys().iterator();
        if ((orb.transportDebugFlag) && (localIterator.hasNext())) {
          dprint(".run: n = " + i);
        }
        while (localIterator.hasNext())
        {
          SelectionKey localSelectionKey = (SelectionKey)localIterator.next();
          localIterator.remove();
          EventHandler localEventHandler = (EventHandler)localSelectionKey.attachment();
          try
          {
            localEventHandler.handleEvent();
          }
          catch (Throwable localThrowable3)
          {
            if (orb.transportDebugFlag) {
              dprint(".run: eventHandler.handleEvent", localThrowable3);
            }
          }
        }
        if ((timeout == 0L) && (orb.transportDebugFlag)) {
          dprint(".run: End of selection cycle");
        }
      }
      catch (Throwable localThrowable1)
      {
        if (orb.transportDebugFlag) {
          dprint(".run: ignoring", localThrowable1);
        }
      }
    }
    try
    {
      if (selector != null)
      {
        if (orb.transportDebugFlag) {
          dprint(".run: selector.close ");
        }
        selector.close();
      }
    }
    catch (Throwable localThrowable2)
    {
      if (orb.transportDebugFlag) {
        dprint(".run: selector.close: ", localThrowable2);
      }
    }
  }
  
  private void clearDeferredRegistrations()
  {
    synchronized (deferredRegistrations)
    {
      int i = deferredRegistrations.size();
      if (orb.transportDebugFlag) {
        dprint(".clearDeferredRegistrations:deferred list size == " + i);
      }
      for (int j = 0; j < i; j++)
      {
        EventHandler localEventHandler = (EventHandler)deferredRegistrations.get(j);
        if (orb.transportDebugFlag) {
          dprint(".clearDeferredRegistrations: " + localEventHandler);
        }
        SelectableChannel localSelectableChannel = localEventHandler.getChannel();
        SelectionKey localSelectionKey = null;
        try
        {
          if (orb.transportDebugFlag)
          {
            dprint(".clearDeferredRegistrations:close channel == " + localSelectableChannel);
            dprint(".clearDeferredRegistrations:close channel class == " + localSelectableChannel.getClass().getName());
          }
          localSelectableChannel.close();
          localSelectionKey = localEventHandler.getSelectionKey();
          if (localSelectionKey != null)
          {
            localSelectionKey.cancel();
            localSelectionKey.attach(null);
          }
        }
        catch (IOException localIOException)
        {
          if (orb.transportDebugFlag) {
            dprint(".clearDeferredRegistrations: ", localIOException);
          }
        }
      }
      deferredRegistrations.clear();
    }
  }
  
  private synchronized boolean isClosed()
  {
    return closed;
  }
  
  private synchronized void setClosed(boolean paramBoolean)
  {
    closed = paramBoolean;
  }
  
  private void startSelector()
  {
    try
    {
      selector = java.nio.channels.Selector.open();
    }
    catch (IOException localIOException)
    {
      if (orb.transportDebugFlag) {
        dprint(".startSelector: Selector.open: IOException: ", localIOException);
      }
      RuntimeException localRuntimeException = new RuntimeException(".startSelector: Selector.open exception");
      localRuntimeException.initCause(localIOException);
      throw localRuntimeException;
    }
    setDaemon(true);
    start();
    selectorStarted = true;
    if (orb.transportDebugFlag) {
      dprint(".startSelector: selector.start completed.");
    }
  }
  
  private void handleDeferredRegistrations()
  {
    synchronized (deferredRegistrations)
    {
      int i = deferredRegistrations.size();
      for (int j = 0; j < i; j++)
      {
        EventHandler localEventHandler = (EventHandler)deferredRegistrations.get(j);
        if (orb.transportDebugFlag) {
          dprint(".handleDeferredRegistrations: " + localEventHandler);
        }
        SelectableChannel localSelectableChannel = localEventHandler.getChannel();
        SelectionKey localSelectionKey = null;
        try
        {
          localSelectionKey = localSelectableChannel.register(selector, localEventHandler.getInterestOps(), localEventHandler);
        }
        catch (ClosedChannelException localClosedChannelException)
        {
          if (orb.transportDebugFlag) {
            dprint(".handleDeferredRegistrations: ", localClosedChannelException);
          }
        }
        localEventHandler.setSelectionKey(localSelectionKey);
      }
      deferredRegistrations.clear();
    }
  }
  
  private void enableInterestOps()
  {
    synchronized (interestOpsList)
    {
      int i = interestOpsList.size();
      if (i > 0)
      {
        if (orb.transportDebugFlag) {
          dprint(".enableInterestOps:->");
        }
        SelectionKey localSelectionKey = null;
        SelectionKeyAndOp localSelectionKeyAndOp = null;
        int k = 0;
        for (int m = 0; m < i; m++)
        {
          localSelectionKeyAndOp = (SelectionKeyAndOp)interestOpsList.get(m);
          localSelectionKey = selectionKey;
          if (localSelectionKey.isValid())
          {
            if (orb.transportDebugFlag) {
              dprint(".enableInterestOps: " + localSelectionKeyAndOp);
            }
            int j = keyOp;
            k = localSelectionKey.interestOps();
            localSelectionKey.interestOps(k | j);
          }
        }
        interestOpsList.clear();
        if (orb.transportDebugFlag) {
          dprint(".enableInterestOps:<-");
        }
      }
    }
  }
  
  private void createListenerThread(EventHandler paramEventHandler)
  {
    if (orb.transportDebugFlag) {
      dprint(".createListenerThread: " + paramEventHandler);
    }
    Acceptor localAcceptor = paramEventHandler.getAcceptor();
    ListenerThreadImpl localListenerThreadImpl = new ListenerThreadImpl(orb, localAcceptor, this);
    listenerThreads.put(paramEventHandler, localListenerThreadImpl);
    Object localObject = null;
    try
    {
      orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork((Work)localListenerThreadImpl);
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
      RuntimeException localRuntimeException = new RuntimeException(((Throwable)localObject).toString());
      localRuntimeException.initCause((Throwable)localObject);
      throw localRuntimeException;
    }
  }
  
  private void destroyListenerThread(EventHandler paramEventHandler)
  {
    if (orb.transportDebugFlag) {
      dprint(".destroyListenerThread: " + paramEventHandler);
    }
    ListenerThread localListenerThread = (ListenerThread)listenerThreads.get(paramEventHandler);
    if (localListenerThread == null)
    {
      if (orb.transportDebugFlag) {
        dprint(".destroyListenerThread: cannot find ListenerThread - ignoring.");
      }
      return;
    }
    listenerThreads.remove(paramEventHandler);
    localListenerThread.close();
  }
  
  private void createReaderThread(EventHandler paramEventHandler)
  {
    if (orb.transportDebugFlag) {
      dprint(".createReaderThread: " + paramEventHandler);
    }
    Connection localConnection = paramEventHandler.getConnection();
    ReaderThreadImpl localReaderThreadImpl = new ReaderThreadImpl(orb, localConnection, this);
    readerThreads.put(paramEventHandler, localReaderThreadImpl);
    Object localObject = null;
    try
    {
      orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork((Work)localReaderThreadImpl);
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
      RuntimeException localRuntimeException = new RuntimeException(((Throwable)localObject).toString());
      localRuntimeException.initCause((Throwable)localObject);
      throw localRuntimeException;
    }
  }
  
  private void destroyReaderThread(EventHandler paramEventHandler)
  {
    if (orb.transportDebugFlag) {
      dprint(".destroyReaderThread: " + paramEventHandler);
    }
    ReaderThread localReaderThread = (ReaderThread)readerThreads.get(paramEventHandler);
    if (localReaderThread == null)
    {
      if (orb.transportDebugFlag) {
        dprint(".destroyReaderThread: cannot find ReaderThread - ignoring.");
      }
      return;
    }
    readerThreads.remove(paramEventHandler);
    localReaderThread.close();
  }
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint("SelectorImpl", paramString);
  }
  
  protected void dprint(String paramString, Throwable paramThrowable)
  {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
  
  private class SelectionKeyAndOp
  {
    public int keyOp;
    public SelectionKey selectionKey;
    
    public SelectionKeyAndOp(SelectionKey paramSelectionKey, int paramInt)
    {
      selectionKey = paramSelectionKey;
      keyOp = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\transport\SelectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */