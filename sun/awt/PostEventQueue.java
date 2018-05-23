package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;

class PostEventQueue
{
  private EventQueueItem queueHead = null;
  private EventQueueItem queueTail = null;
  private final EventQueue eventQueue;
  private Thread flushThread = null;
  
  PostEventQueue(EventQueue paramEventQueue)
  {
    eventQueue = paramEventQueue;
  }
  
  public void flush()
  {
    Thread localThread = Thread.currentThread();
    try
    {
      EventQueueItem localEventQueueItem;
      synchronized (this)
      {
        if (localThread == flushThread) {
          return;
        }
        while (flushThread != null) {
          wait();
        }
        if (queueHead == null) {
          return;
        }
        flushThread = localThread;
        localEventQueueItem = queueHead;
        queueHead = (queueTail = null);
      }
      try
      {
        while (localEventQueueItem != null)
        {
          eventQueue.postEvent(event);
          localEventQueueItem = next;
        }
      }
      finally
      {
        synchronized (this)
        {
          flushThread = null;
          notifyAll();
        }
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      localThread.interrupt();
    }
  }
  
  void postEvent(AWTEvent paramAWTEvent)
  {
    EventQueueItem localEventQueueItem = new EventQueueItem(paramAWTEvent);
    synchronized (this)
    {
      if (queueHead == null)
      {
        queueHead = (queueTail = localEventQueueItem);
      }
      else
      {
        queueTail.next = localEventQueueItem;
        queueTail = localEventQueueItem;
      }
    }
    SunToolkit.wakeupEventQueue(eventQueue, paramAWTEvent.getSource() == AWTAutoShutdown.getInstance());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\PostEventQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */