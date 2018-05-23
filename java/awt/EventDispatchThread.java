package java.awt;

import java.util.ArrayList;
import sun.awt.EventQueueDelegate;
import sun.awt.EventQueueDelegate.Delegate;
import sun.awt.ModalExclude;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDragSourceContextPeer;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

class EventDispatchThread
  extends Thread
{
  private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.EventDispatchThread");
  private EventQueue theQueue;
  private volatile boolean doDispatch = true;
  private static final int ANY_EVENT = -1;
  private ArrayList<EventFilter> eventFilters = new ArrayList();
  
  EventDispatchThread(ThreadGroup paramThreadGroup, String paramString, EventQueue paramEventQueue)
  {
    super(paramThreadGroup, paramString);
    setEventQueue(paramEventQueue);
  }
  
  public void stopDispatching()
  {
    doDispatch = false;
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: aload_0
    //   1: new 100	java/awt/EventDispatchThread$1
    //   4: dup
    //   5: aload_0
    //   6: invokespecial 228	java/awt/EventDispatchThread$1:<init>	(Ljava/awt/EventDispatchThread;)V
    //   9: invokevirtual 219	java/awt/EventDispatchThread:pumpEvents	(Ljava/awt/Conditional;)V
    //   12: aload_0
    //   13: invokevirtual 222	java/awt/EventDispatchThread:getEventQueue	()Ljava/awt/EventQueue;
    //   16: aload_0
    //   17: invokevirtual 233	java/awt/EventQueue:detachDispatchThread	(Ljava/awt/EventDispatchThread;)V
    //   20: goto +14 -> 34
    //   23: astore_1
    //   24: aload_0
    //   25: invokevirtual 222	java/awt/EventDispatchThread:getEventQueue	()Ljava/awt/EventQueue;
    //   28: aload_0
    //   29: invokevirtual 233	java/awt/EventQueue:detachDispatchThread	(Ljava/awt/EventDispatchThread;)V
    //   32: aload_1
    //   33: athrow
    //   34: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	EventDispatchThread
    //   23	10	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	12	23	finally
  }
  
  void pumpEvents(Conditional paramConditional)
  {
    pumpEvents(-1, paramConditional);
  }
  
  void pumpEventsForHierarchy(Conditional paramConditional, Component paramComponent)
  {
    pumpEventsForHierarchy(-1, paramConditional, paramComponent);
  }
  
  void pumpEvents(int paramInt, Conditional paramConditional)
  {
    pumpEventsForHierarchy(paramInt, paramConditional, null);
  }
  
  void pumpEventsForHierarchy(int paramInt, Conditional paramConditional, Component paramComponent)
  {
    pumpEventsForFilter(paramInt, paramConditional, new HierarchyEventFilter(paramComponent));
  }
  
  void pumpEventsForFilter(Conditional paramConditional, EventFilter paramEventFilter)
  {
    pumpEventsForFilter(-1, paramConditional, paramEventFilter);
  }
  
  void pumpEventsForFilter(int paramInt, Conditional paramConditional, EventFilter paramEventFilter)
  {
    addEventFilter(paramEventFilter);
    doDispatch = true;
    while ((doDispatch) && (!isInterrupted()) && (paramConditional.evaluate())) {
      pumpOneEventForFilters(paramInt);
    }
    removeEventFilter(paramEventFilter);
  }
  
  void addEventFilter(EventFilter paramEventFilter)
  {
    if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
      eventLog.finest("adding the event filter: " + paramEventFilter);
    }
    synchronized (eventFilters)
    {
      if (!eventFilters.contains(paramEventFilter)) {
        if ((paramEventFilter instanceof ModalEventFilter))
        {
          ModalEventFilter localModalEventFilter1 = (ModalEventFilter)paramEventFilter;
          int i = 0;
          for (i = 0; i < eventFilters.size(); i++)
          {
            EventFilter localEventFilter = (EventFilter)eventFilters.get(i);
            if ((localEventFilter instanceof ModalEventFilter))
            {
              ModalEventFilter localModalEventFilter2 = (ModalEventFilter)localEventFilter;
              if (localModalEventFilter2.compareTo(localModalEventFilter1) > 0) {
                break;
              }
            }
          }
          eventFilters.add(i, paramEventFilter);
        }
        else
        {
          eventFilters.add(paramEventFilter);
        }
      }
    }
  }
  
  void removeEventFilter(EventFilter paramEventFilter)
  {
    if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
      eventLog.finest("removing the event filter: " + paramEventFilter);
    }
    synchronized (eventFilters)
    {
      eventFilters.remove(paramEventFilter);
    }
  }
  
  void pumpOneEventForFilters(int paramInt)
  {
    AWTEvent localAWTEvent = null;
    int i = 0;
    try
    {
      EventQueue localEventQueue = null;
      EventQueueDelegate.Delegate localDelegate = null;
      do
      {
        localEventQueue = getEventQueue();
        localDelegate = EventQueueDelegate.getDelegate();
        if ((localDelegate != null) && (paramInt == -1)) {
          localAWTEvent = localDelegate.getNextEvent(localEventQueue);
        } else {
          localAWTEvent = paramInt == -1 ? localEventQueue.getNextEvent() : localEventQueue.getNextEvent(paramInt);
        }
        i = 1;
        synchronized (eventFilters)
        {
          for (int j = eventFilters.size() - 1; j >= 0; j--)
          {
            EventFilter localEventFilter = (EventFilter)eventFilters.get(j);
            EventFilter.FilterAction localFilterAction = localEventFilter.acceptEvent(localAWTEvent);
            if (localFilterAction == EventFilter.FilterAction.REJECT) {
              i = 0;
            } else {
              if (localFilterAction == EventFilter.FilterAction.ACCEPT_IMMEDIATELY) {
                break;
              }
            }
          }
        }
        i = (i != 0) && (SunDragSourceContextPeer.checkEvent(localAWTEvent)) ? 1 : 0;
        if (i == 0) {
          localAWTEvent.consume();
        }
      } while (i == 0);
      if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
        eventLog.finest("Dispatching: " + localAWTEvent);
      }
      ??? = null;
      if (localDelegate != null) {
        ??? = localDelegate.beforeDispatch(localAWTEvent);
      }
      localEventQueue.dispatchEvent(localAWTEvent);
      if (localDelegate != null) {
        localDelegate.afterDispatch(localAWTEvent, ???);
      }
    }
    catch (ThreadDeath localThreadDeath)
    {
      doDispatch = false;
      throw localThreadDeath;
    }
    catch (InterruptedException localInterruptedException)
    {
      doDispatch = false;
    }
    catch (Throwable localThrowable)
    {
      processException(localThrowable);
    }
  }
  
  private void processException(Throwable paramThrowable)
  {
    if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
      eventLog.fine("Processing exception: " + paramThrowable);
    }
    getUncaughtExceptionHandler().uncaughtException(this, paramThrowable);
  }
  
  public synchronized EventQueue getEventQueue()
  {
    return theQueue;
  }
  
  public synchronized void setEventQueue(EventQueue paramEventQueue)
  {
    theQueue = paramEventQueue;
  }
  
  private static class HierarchyEventFilter
    implements EventFilter
  {
    private Component modalComponent;
    
    public HierarchyEventFilter(Component paramComponent)
    {
      modalComponent = paramComponent;
    }
    
    public EventFilter.FilterAction acceptEvent(AWTEvent paramAWTEvent)
    {
      if (modalComponent != null)
      {
        int i = paramAWTEvent.getID();
        int j = (i >= 500) && (i <= 507) ? 1 : 0;
        int k = (i >= 1001) && (i <= 1001) ? 1 : 0;
        int m = i == 201 ? 1 : 0;
        if (Component.isInstanceOf(modalComponent, "javax.swing.JInternalFrame")) {
          return m != 0 ? EventFilter.FilterAction.REJECT : EventFilter.FilterAction.ACCEPT;
        }
        if ((j != 0) || (k != 0) || (m != 0))
        {
          Object localObject1 = paramAWTEvent.getSource();
          if ((localObject1 instanceof ModalExclude)) {
            return EventFilter.FilterAction.ACCEPT;
          }
          if ((localObject1 instanceof Component))
          {
            Object localObject2 = (Component)localObject1;
            int n = 0;
            if ((modalComponent instanceof Container)) {
              while ((localObject2 != modalComponent) && (localObject2 != null))
              {
                if (((localObject2 instanceof Window)) && (SunToolkit.isModalExcluded((Window)localObject2)))
                {
                  n = 1;
                  break;
                }
                localObject2 = ((Component)localObject2).getParent();
              }
            }
            if ((n == 0) && (localObject2 != modalComponent)) {
              return EventFilter.FilterAction.REJECT;
            }
          }
        }
      }
      return EventFilter.FilterAction.ACCEPT;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\EventDispatchThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */