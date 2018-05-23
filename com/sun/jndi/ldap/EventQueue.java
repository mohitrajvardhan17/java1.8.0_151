package com.sun.jndi.ldap;

import java.util.EventObject;
import java.util.Vector;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.ldap.UnsolicitedNotificationListener;

final class EventQueue
  implements Runnable
{
  private static final boolean debug = false;
  private QueueElement head = null;
  private QueueElement tail = null;
  private Thread qThread = Obj.helper.createThread(this);
  
  EventQueue()
  {
    qThread.setDaemon(true);
    qThread.start();
  }
  
  synchronized void enqueue(EventObject paramEventObject, Vector<NamingListener> paramVector)
  {
    QueueElement localQueueElement = new QueueElement(paramEventObject, paramVector);
    if (head == null)
    {
      head = localQueueElement;
      tail = localQueueElement;
    }
    else
    {
      next = head;
      head.prev = localQueueElement;
      head = localQueueElement;
    }
    notify();
  }
  
  private synchronized QueueElement dequeue()
    throws InterruptedException
  {
    while (tail == null) {
      wait();
    }
    QueueElement localQueueElement = tail;
    tail = prev;
    if (tail == null) {
      head = null;
    } else {
      tail.next = null;
    }
    prev = (next = null);
    return localQueueElement;
  }
  
  public void run()
  {
    try
    {
      QueueElement localQueueElement;
      while ((localQueueElement = dequeue()) != null)
      {
        EventObject localEventObject = event;
        Vector localVector = vector;
        for (int i = 0; i < localVector.size(); i++) {
          if ((localEventObject instanceof NamingEvent)) {
            ((NamingEvent)localEventObject).dispatch((NamingListener)localVector.elementAt(i));
          } else if ((localEventObject instanceof NamingExceptionEvent)) {
            ((NamingExceptionEvent)localEventObject).dispatch((NamingListener)localVector.elementAt(i));
          } else if ((localEventObject instanceof UnsolicitedNotificationEvent)) {
            ((UnsolicitedNotificationEvent)localEventObject).dispatch((UnsolicitedNotificationListener)localVector.elementAt(i));
          }
        }
        localQueueElement = null;
        localEventObject = null;
        localVector = null;
      }
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  void stop()
  {
    if (qThread != null)
    {
      qThread.interrupt();
      qThread = null;
    }
  }
  
  private static class QueueElement
  {
    QueueElement next = null;
    QueueElement prev = null;
    EventObject event = null;
    Vector<NamingListener> vector = null;
    
    QueueElement(EventObject paramEventObject, Vector<NamingListener> paramVector)
    {
      event = paramEventObject;
      vector = paramVector;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\EventQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */