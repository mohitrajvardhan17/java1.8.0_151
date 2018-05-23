package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.ComponentPeer;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.EventQueueAccessor;
import sun.awt.AWTAccessor.InvocationEventAccessor;
import sun.awt.AWTAutoShutdown;
import sun.awt.AppContext;
import sun.awt.EventQueueItem;
import sun.awt.FwDispatcher;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetEvent;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class EventQueue
{
  private static final AtomicInteger threadInitNumber = new AtomicInteger(0);
  private static final int LOW_PRIORITY = 0;
  private static final int NORM_PRIORITY = 1;
  private static final int HIGH_PRIORITY = 2;
  private static final int ULTIMATE_PRIORITY = 3;
  private static final int NUM_PRIORITIES = 4;
  private Queue[] queues = new Queue[4];
  private EventQueue nextQueue;
  private EventQueue previousQueue;
  private final Lock pushPopLock;
  private final Condition pushPopCond;
  private static final Runnable dummyRunnable = new Runnable()
  {
    public void run() {}
  };
  private EventDispatchThread dispatchThread;
  private final ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  private long mostRecentEventTime = System.currentTimeMillis();
  private long mostRecentKeyEventTime = System.currentTimeMillis();
  private WeakReference<AWTEvent> currentEvent;
  private volatile int waitForID;
  private final AppContext appContext;
  private final String name = "AWT-EventQueue-" + threadInitNumber.getAndIncrement();
  private FwDispatcher fwDispatcher;
  private static volatile PlatformLogger eventLog;
  private static final int PAINT = 0;
  private static final int UPDATE = 1;
  private static final int MOVE = 2;
  private static final int DRAG = 3;
  private static final int PEER = 4;
  private static final int CACHE_LENGTH = 5;
  private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
  
  private static final PlatformLogger getEventLog()
  {
    if (eventLog == null) {
      eventLog = PlatformLogger.getLogger("java.awt.event.EventQueue");
    }
    return eventLog;
  }
  
  public EventQueue()
  {
    for (int i = 0; i < 4; i++) {
      queues[i] = new Queue();
    }
    appContext = AppContext.getAppContext();
    pushPopLock = ((Lock)appContext.get(AppContext.EVENT_QUEUE_LOCK_KEY));
    pushPopCond = ((Condition)appContext.get(AppContext.EVENT_QUEUE_COND_KEY));
  }
  
  public void postEvent(AWTEvent paramAWTEvent)
  {
    SunToolkit.flushPendingEvents(appContext);
    postEventPrivate(paramAWTEvent);
  }
  
  private final void postEventPrivate(AWTEvent paramAWTEvent)
  {
    isPosted = true;
    pushPopLock.lock();
    try
    {
      if (nextQueue != null)
      {
        nextQueue.postEventPrivate(paramAWTEvent);
        return;
      }
      if (dispatchThread == null)
      {
        if (paramAWTEvent.getSource() == AWTAutoShutdown.getInstance()) {
          return;
        }
        initDispatchThread();
      }
      postEvent(paramAWTEvent, getPriority(paramAWTEvent));
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  private static int getPriority(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof PeerEvent))
    {
      PeerEvent localPeerEvent = (PeerEvent)paramAWTEvent;
      if ((localPeerEvent.getFlags() & 0x2) != 0L) {
        return 3;
      }
      if ((localPeerEvent.getFlags() & 1L) != 0L) {
        return 2;
      }
      if ((localPeerEvent.getFlags() & 0x4) != 0L) {
        return 0;
      }
    }
    int i = paramAWTEvent.getID();
    if ((i >= 800) && (i <= 801)) {
      return 0;
    }
    return 1;
  }
  
  private void postEvent(AWTEvent paramAWTEvent, int paramInt)
  {
    if (coalesceEvent(paramAWTEvent, paramInt)) {
      return;
    }
    EventQueueItem localEventQueueItem = new EventQueueItem(paramAWTEvent);
    cacheEQItem(localEventQueueItem);
    int i = paramAWTEvent.getID() == waitForID ? 1 : 0;
    if (queues[paramInt].head == null)
    {
      boolean bool = noEvents();
      queues[paramInt].head = (queues[paramInt].tail = localEventQueueItem);
      if (bool)
      {
        if (paramAWTEvent.getSource() != AWTAutoShutdown.getInstance()) {
          AWTAutoShutdown.getInstance().notifyThreadBusy(dispatchThread);
        }
        pushPopCond.signalAll();
      }
      else if (i != 0)
      {
        pushPopCond.signalAll();
      }
    }
    else
    {
      queues[paramInt].tail.next = localEventQueueItem;
      queues[paramInt].tail = localEventQueueItem;
      if (i != 0) {
        pushPopCond.signalAll();
      }
    }
  }
  
  private boolean coalescePaintEvent(PaintEvent paramPaintEvent)
  {
    ComponentPeer localComponentPeer = getSourcepeer;
    if (localComponentPeer != null) {
      localComponentPeer.coalescePaintEvent(paramPaintEvent);
    }
    EventQueueItem[] arrayOfEventQueueItem = getSourceeventCache;
    if (arrayOfEventQueueItem == null) {
      return false;
    }
    int i = eventToCacheIndex(paramPaintEvent);
    if ((i != -1) && (arrayOfEventQueueItem[i] != null))
    {
      PaintEvent localPaintEvent = mergePaintEvents(paramPaintEvent, (PaintEvent)event);
      if (localPaintEvent != null)
      {
        event = localPaintEvent;
        return true;
      }
    }
    return false;
  }
  
  private PaintEvent mergePaintEvents(PaintEvent paramPaintEvent1, PaintEvent paramPaintEvent2)
  {
    Rectangle localRectangle1 = paramPaintEvent1.getUpdateRect();
    Rectangle localRectangle2 = paramPaintEvent2.getUpdateRect();
    if (localRectangle2.contains(localRectangle1)) {
      return paramPaintEvent2;
    }
    if (localRectangle1.contains(localRectangle2)) {
      return paramPaintEvent1;
    }
    return null;
  }
  
  private boolean coalesceMouseEvent(MouseEvent paramMouseEvent)
  {
    EventQueueItem[] arrayOfEventQueueItem = getSourceeventCache;
    if (arrayOfEventQueueItem == null) {
      return false;
    }
    int i = eventToCacheIndex(paramMouseEvent);
    if ((i != -1) && (arrayOfEventQueueItem[i] != null))
    {
      event = paramMouseEvent;
      return true;
    }
    return false;
  }
  
  private boolean coalescePeerEvent(PeerEvent paramPeerEvent)
  {
    EventQueueItem[] arrayOfEventQueueItem = getSourceeventCache;
    if (arrayOfEventQueueItem == null) {
      return false;
    }
    int i = eventToCacheIndex(paramPeerEvent);
    if ((i != -1) && (arrayOfEventQueueItem[i] != null))
    {
      paramPeerEvent = paramPeerEvent.coalesceEvents((PeerEvent)event);
      if (paramPeerEvent != null)
      {
        event = paramPeerEvent;
        return true;
      }
      arrayOfEventQueueItem[i] = null;
    }
    return false;
  }
  
  private boolean coalesceOtherEvent(AWTEvent paramAWTEvent, int paramInt)
  {
    int i = paramAWTEvent.getID();
    Component localComponent = (Component)paramAWTEvent.getSource();
    for (EventQueueItem localEventQueueItem = queues[paramInt].head; localEventQueueItem != null; localEventQueueItem = next) {
      if ((event.getSource() == localComponent) && (event.getID() == i))
      {
        AWTEvent localAWTEvent = localComponent.coalesceEvents(event, paramAWTEvent);
        if (localAWTEvent != null)
        {
          event = localAWTEvent;
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean coalesceEvent(AWTEvent paramAWTEvent, int paramInt)
  {
    if (!(paramAWTEvent.getSource() instanceof Component)) {
      return false;
    }
    if ((paramAWTEvent instanceof PeerEvent)) {
      return coalescePeerEvent((PeerEvent)paramAWTEvent);
    }
    if ((((Component)paramAWTEvent.getSource()).isCoalescingEnabled()) && (coalesceOtherEvent(paramAWTEvent, paramInt))) {
      return true;
    }
    if ((paramAWTEvent instanceof PaintEvent)) {
      return coalescePaintEvent((PaintEvent)paramAWTEvent);
    }
    if ((paramAWTEvent instanceof MouseEvent)) {
      return coalesceMouseEvent((MouseEvent)paramAWTEvent);
    }
    return false;
  }
  
  private void cacheEQItem(EventQueueItem paramEventQueueItem)
  {
    int i = eventToCacheIndex(event);
    if ((i != -1) && ((event.getSource() instanceof Component)))
    {
      Component localComponent = (Component)event.getSource();
      if (eventCache == null) {
        eventCache = new EventQueueItem[5];
      }
      eventCache[i] = paramEventQueueItem;
    }
  }
  
  private void uncacheEQItem(EventQueueItem paramEventQueueItem)
  {
    int i = eventToCacheIndex(event);
    if ((i != -1) && ((event.getSource() instanceof Component)))
    {
      Component localComponent = (Component)event.getSource();
      if (eventCache == null) {
        return;
      }
      eventCache[i] = null;
    }
  }
  
  private static int eventToCacheIndex(AWTEvent paramAWTEvent)
  {
    switch (paramAWTEvent.getID())
    {
    case 800: 
      return 0;
    case 801: 
      return 1;
    case 503: 
      return 2;
    case 506: 
      return (paramAWTEvent instanceof SunDropTargetEvent) ? -1 : 3;
    }
    return (paramAWTEvent instanceof PeerEvent) ? 4 : -1;
  }
  
  private boolean noEvents()
  {
    for (int i = 0; i < 4; i++) {
      if (queues[i].head != null) {
        return false;
      }
    }
    return true;
  }
  
  public AWTEvent getNextEvent()
    throws InterruptedException
  {
    for (;;)
    {
      SunToolkit.flushPendingEvents(appContext);
      pushPopLock.lock();
      try
      {
        AWTEvent localAWTEvent1 = getNextEventPrivate();
        if (localAWTEvent1 != null)
        {
          AWTEvent localAWTEvent2 = localAWTEvent1;
          return localAWTEvent2;
        }
        AWTAutoShutdown.getInstance().notifyThreadFree(dispatchThread);
        pushPopCond.await();
      }
      finally
      {
        pushPopLock.unlock();
      }
    }
  }
  
  AWTEvent getNextEventPrivate()
    throws InterruptedException
  {
    for (int i = 3; i >= 0; i--) {
      if (queues[i].head != null)
      {
        EventQueueItem localEventQueueItem = queues[i].head;
        queues[i].head = next;
        if (next == null) {
          queues[i].tail = null;
        }
        uncacheEQItem(localEventQueueItem);
        return event;
      }
    }
    return null;
  }
  
  AWTEvent getNextEvent(int paramInt)
    throws InterruptedException
  {
    for (;;)
    {
      SunToolkit.flushPendingEvents(appContext);
      pushPopLock.lock();
      try
      {
        for (int i = 0; i < 4; i++)
        {
          EventQueueItem localEventQueueItem1 = queues[i].head;
          EventQueueItem localEventQueueItem2 = null;
          while (localEventQueueItem1 != null)
          {
            if (event.getID() == paramInt)
            {
              if (localEventQueueItem2 == null) {
                queues[i].head = next;
              } else {
                next = next;
              }
              if (queues[i].tail == localEventQueueItem1) {
                queues[i].tail = localEventQueueItem2;
              }
              uncacheEQItem(localEventQueueItem1);
              AWTEvent localAWTEvent = event;
              return localAWTEvent;
            }
            localEventQueueItem2 = localEventQueueItem1;
            localEventQueueItem1 = next;
          }
        }
        waitForID = paramInt;
        pushPopCond.await();
        waitForID = 0;
      }
      finally
      {
        pushPopLock.unlock();
      }
    }
  }
  
  public AWTEvent peekEvent()
  {
    pushPopLock.lock();
    try
    {
      for (int i = 3; i >= 0; i--) {
        if (queues[i].head != null)
        {
          AWTEvent localAWTEvent = queues[i].head.event;
          return localAWTEvent;
        }
      }
    }
    finally
    {
      pushPopLock.unlock();
    }
    return null;
  }
  
  public AWTEvent peekEvent(int paramInt)
  {
    pushPopLock.lock();
    try
    {
      for (int i = 3; i >= 0; i--) {
        for (EventQueueItem localEventQueueItem = queues[i].head; localEventQueueItem != null; localEventQueueItem = next) {
          if (event.getID() == paramInt)
          {
            AWTEvent localAWTEvent = event;
            return localAWTEvent;
          }
        }
      }
    }
    finally
    {
      pushPopLock.unlock();
    }
    return null;
  }
  
  protected void dispatchEvent(final AWTEvent paramAWTEvent)
  {
    final Object localObject = paramAWTEvent.getSource();
    final PrivilegedAction local3 = new PrivilegedAction()
    {
      public Void run()
      {
        if ((fwDispatcher == null) || (isDispatchThreadImpl())) {
          EventQueue.this.dispatchEventImpl(paramAWTEvent, localObject);
        } else {
          fwDispatcher.scheduleDispatch(new Runnable()
          {
            public void run()
            {
              EventQueue.this.dispatchEventImpl(val$event, val$src);
            }
          });
        }
        return null;
      }
    };
    AccessControlContext localAccessControlContext1 = AccessController.getContext();
    AccessControlContext localAccessControlContext2 = getAccessControlContextFrom(localObject);
    final AccessControlContext localAccessControlContext3 = paramAWTEvent.getAccessControlContext();
    if (localAccessControlContext2 == null) {
      javaSecurityAccess.doIntersectionPrivilege(local3, localAccessControlContext1, localAccessControlContext3);
    } else {
      javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
      {
        public Void run()
        {
          EventQueue.javaSecurityAccess.doIntersectionPrivilege(local3, localAccessControlContext3);
          return null;
        }
      }, localAccessControlContext1, localAccessControlContext2);
    }
  }
  
  private static AccessControlContext getAccessControlContextFrom(Object paramObject)
  {
    return (paramObject instanceof TrayIcon) ? ((TrayIcon)paramObject).getAccessControlContext() : (paramObject instanceof MenuComponent) ? ((MenuComponent)paramObject).getAccessControlContext() : (paramObject instanceof Component) ? ((Component)paramObject).getAccessControlContext() : null;
  }
  
  private void dispatchEventImpl(AWTEvent paramAWTEvent, Object paramObject)
  {
    isPosted = true;
    if ((paramAWTEvent instanceof ActiveEvent))
    {
      setCurrentEventAndMostRecentTimeImpl(paramAWTEvent);
      ((ActiveEvent)paramAWTEvent).dispatch();
    }
    else if ((paramObject instanceof Component))
    {
      ((Component)paramObject).dispatchEvent(paramAWTEvent);
      paramAWTEvent.dispatched();
    }
    else if ((paramObject instanceof MenuComponent))
    {
      ((MenuComponent)paramObject).dispatchEvent(paramAWTEvent);
    }
    else if ((paramObject instanceof TrayIcon))
    {
      ((TrayIcon)paramObject).dispatchEvent(paramAWTEvent);
    }
    else if ((paramObject instanceof AWTAutoShutdown))
    {
      if (noEvents()) {
        dispatchThread.stopDispatching();
      }
    }
    else if (getEventLog().isLoggable(PlatformLogger.Level.FINE))
    {
      getEventLog().fine("Unable to dispatch event: " + paramAWTEvent);
    }
  }
  
  public static long getMostRecentEventTime()
  {
    return Toolkit.getEventQueue().getMostRecentEventTimeImpl();
  }
  
  private long getMostRecentEventTimeImpl()
  {
    pushPopLock.lock();
    try
    {
      long l = Thread.currentThread() == dispatchThread ? mostRecentEventTime : System.currentTimeMillis();
      return l;
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  long getMostRecentEventTimeEx()
  {
    pushPopLock.lock();
    try
    {
      long l = mostRecentEventTime;
      return l;
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  public static AWTEvent getCurrentEvent()
  {
    return Toolkit.getEventQueue().getCurrentEventImpl();
  }
  
  private AWTEvent getCurrentEventImpl()
  {
    pushPopLock.lock();
    try
    {
      AWTEvent localAWTEvent = Thread.currentThread() == dispatchThread ? (AWTEvent)currentEvent.get() : null;
      return localAWTEvent;
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  public void push(EventQueue paramEventQueue)
  {
    if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
      getEventLog().fine("EventQueue.push(" + paramEventQueue + ")");
    }
    pushPopLock.lock();
    try
    {
      for (EventQueue localEventQueue = this; nextQueue != null; localEventQueue = nextQueue) {}
      if (fwDispatcher != null) {
        throw new RuntimeException("push() to queue with fwDispatcher");
      }
      if ((dispatchThread != null) && (dispatchThread.getEventQueue() == this))
      {
        dispatchThread = dispatchThread;
        dispatchThread.setEventQueue(paramEventQueue);
      }
      while (localEventQueue.peekEvent() != null) {
        try
        {
          paramEventQueue.postEventPrivate(localEventQueue.getNextEventPrivate());
        }
        catch (InterruptedException localInterruptedException)
        {
          if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
            getEventLog().fine("Interrupted push", localInterruptedException);
          }
        }
      }
      if (dispatchThread != null) {
        localEventQueue.postEventPrivate(new InvocationEvent(localEventQueue, dummyRunnable));
      }
      previousQueue = localEventQueue;
      nextQueue = paramEventQueue;
      if (appContext.get(AppContext.EVENT_QUEUE_KEY) == localEventQueue) {
        appContext.put(AppContext.EVENT_QUEUE_KEY, paramEventQueue);
      }
      pushPopCond.signalAll();
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  protected void pop()
    throws EmptyStackException
  {
    if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
      getEventLog().fine("EventQueue.pop(" + this + ")");
    }
    pushPopLock.lock();
    try
    {
      for (EventQueue localEventQueue1 = this; nextQueue != null; localEventQueue1 = nextQueue) {}
      EventQueue localEventQueue2 = previousQueue;
      if (localEventQueue2 == null) {
        throw new EmptyStackException();
      }
      previousQueue = null;
      nextQueue = null;
      while (localEventQueue1.peekEvent() != null) {
        try
        {
          localEventQueue2.postEventPrivate(localEventQueue1.getNextEventPrivate());
        }
        catch (InterruptedException localInterruptedException)
        {
          if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
            getEventLog().fine("Interrupted pop", localInterruptedException);
          }
        }
      }
      if ((dispatchThread != null) && (dispatchThread.getEventQueue() == this))
      {
        dispatchThread = dispatchThread;
        dispatchThread.setEventQueue(localEventQueue2);
      }
      if (appContext.get(AppContext.EVENT_QUEUE_KEY) == this) {
        appContext.put(AppContext.EVENT_QUEUE_KEY, localEventQueue2);
      }
      localEventQueue1.postEventPrivate(new InvocationEvent(localEventQueue1, dummyRunnable));
      pushPopCond.signalAll();
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  public SecondaryLoop createSecondaryLoop()
  {
    return createSecondaryLoop(null, null, 0L);
  }
  
  SecondaryLoop createSecondaryLoop(Conditional paramConditional, EventFilter paramEventFilter, long paramLong)
  {
    pushPopLock.lock();
    try
    {
      if (nextQueue != null)
      {
        localObject1 = nextQueue.createSecondaryLoop(paramConditional, paramEventFilter, paramLong);
        return (SecondaryLoop)localObject1;
      }
      if (fwDispatcher != null)
      {
        localObject1 = fwDispatcher.createSecondaryLoop();
        return (SecondaryLoop)localObject1;
      }
      if (dispatchThread == null) {
        initDispatchThread();
      }
      Object localObject1 = new WaitDispatchSupport(dispatchThread, paramConditional, paramEventFilter, paramLong);
      return (SecondaryLoop)localObject1;
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  public static boolean isDispatchThread()
  {
    EventQueue localEventQueue = Toolkit.getEventQueue();
    return localEventQueue.isDispatchThreadImpl();
  }
  
  final boolean isDispatchThreadImpl()
  {
    Object localObject1 = this;
    pushPopLock.lock();
    try
    {
      for (EventQueue localEventQueue = nextQueue; localEventQueue != null; localEventQueue = nextQueue) {
        localObject1 = localEventQueue;
      }
      if (fwDispatcher != null)
      {
        bool = fwDispatcher.isDispatchThread();
        return bool;
      }
      boolean bool = Thread.currentThread() == dispatchThread;
      return bool;
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  /* Error */
  final void initDispatchThread()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 686 1 0
    //   9: aload_0
    //   10: getfield 547	java/awt/EventQueue:dispatchThread	Ljava/awt/EventDispatchThread;
    //   13: ifnonnull +48 -> 61
    //   16: aload_0
    //   17: getfield 554	java/awt/EventQueue:threadGroup	Ljava/lang/ThreadGroup;
    //   20: invokevirtual 654	java/lang/ThreadGroup:isDestroyed	()Z
    //   23: ifne +38 -> 61
    //   26: aload_0
    //   27: getfield 559	java/awt/EventQueue:appContext	Lsun/awt/AppContext;
    //   30: invokevirtual 668	sun/awt/AppContext:isDisposed	()Z
    //   33: ifne +28 -> 61
    //   36: aload_0
    //   37: new 275	java/awt/EventQueue$5
    //   40: dup
    //   41: aload_0
    //   42: invokespecial 617	java/awt/EventQueue$5:<init>	(Ljava/awt/EventQueue;)V
    //   45: invokestatic 659	java/security/AccessController:doPrivileged	(Ljava/security/PrivilegedAction;)Ljava/lang/Object;
    //   48: checkcast 268	java/awt/EventDispatchThread
    //   51: putfield 547	java/awt/EventQueue:dispatchThread	Ljava/awt/EventDispatchThread;
    //   54: aload_0
    //   55: getfield 547	java/awt/EventQueue:dispatchThread	Ljava/awt/EventDispatchThread;
    //   58: invokevirtual 579	java/awt/EventDispatchThread:start	()V
    //   61: aload_0
    //   62: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   65: invokeinterface 687 1 0
    //   70: goto +15 -> 85
    //   73: astore_1
    //   74: aload_0
    //   75: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   78: invokeinterface 687 1 0
    //   83: aload_1
    //   84: athrow
    //   85: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	86	0	this	EventQueue
    //   73	11	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	61	73	finally
  }
  
  /* Error */
  final void detachDispatchThread(EventDispatchThread paramEventDispatchThread)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 559	java/awt/EventQueue:appContext	Lsun/awt/AppContext;
    //   4: invokestatic 676	sun/awt/SunToolkit:flushPendingEvents	(Lsun/awt/AppContext;)V
    //   7: aload_0
    //   8: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   11: invokeinterface 686 1 0
    //   16: aload_1
    //   17: aload_0
    //   18: getfield 547	java/awt/EventQueue:dispatchThread	Ljava/awt/EventDispatchThread;
    //   21: if_acmpne +8 -> 29
    //   24: aload_0
    //   25: aconst_null
    //   26: putfield 547	java/awt/EventQueue:dispatchThread	Ljava/awt/EventDispatchThread;
    //   29: invokestatic 667	sun/awt/AWTAutoShutdown:getInstance	()Lsun/awt/AWTAutoShutdown;
    //   32: aload_1
    //   33: invokevirtual 666	sun/awt/AWTAutoShutdown:notifyThreadFree	(Ljava/lang/Thread;)V
    //   36: aload_0
    //   37: invokevirtual 591	java/awt/EventQueue:peekEvent	()Ljava/awt/AWTEvent;
    //   40: ifnull +7 -> 47
    //   43: aload_0
    //   44: invokevirtual 584	java/awt/EventQueue:initDispatchThread	()V
    //   47: aload_0
    //   48: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   51: invokeinterface 687 1 0
    //   56: goto +15 -> 71
    //   59: astore_2
    //   60: aload_0
    //   61: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   64: invokeinterface 687 1 0
    //   69: aload_2
    //   70: athrow
    //   71: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	EventQueue
    //   0	72	1	paramEventDispatchThread	EventDispatchThread
    //   59	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   16	47	59	finally
  }
  
  final EventDispatchThread getDispatchThread()
  {
    pushPopLock.lock();
    try
    {
      EventDispatchThread localEventDispatchThread = dispatchThread;
      return localEventDispatchThread;
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  final void removeSourceEvents(Object paramObject, boolean paramBoolean)
  {
    SunToolkit.flushPendingEvents(appContext);
    pushPopLock.lock();
    try
    {
      for (int i = 0; i < 4; i++)
      {
        EventQueueItem localEventQueueItem1 = queues[i].head;
        EventQueueItem localEventQueueItem2 = null;
        while (localEventQueueItem1 != null)
        {
          if ((event.getSource() == paramObject) && ((paramBoolean) || ((!(event instanceof SequencedEvent)) && (!(event instanceof SentEvent)) && (!(event instanceof FocusEvent)) && (!(event instanceof WindowEvent)) && (!(event instanceof KeyEvent)) && (!(event instanceof InputMethodEvent)))))
          {
            if ((event instanceof SequencedEvent)) {
              ((SequencedEvent)event).dispose();
            }
            if ((event instanceof SentEvent)) {
              ((SentEvent)event).dispose();
            }
            if ((event instanceof InvocationEvent)) {
              AWTAccessor.getInvocationEventAccessor().dispose((InvocationEvent)event);
            }
            if (localEventQueueItem2 == null) {
              queues[i].head = next;
            } else {
              next = next;
            }
            uncacheEQItem(localEventQueueItem1);
          }
          else
          {
            localEventQueueItem2 = localEventQueueItem1;
          }
          localEventQueueItem1 = next;
        }
        queues[i].tail = localEventQueueItem2;
      }
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  synchronized long getMostRecentKeyEventTime()
  {
    pushPopLock.lock();
    try
    {
      long l = mostRecentKeyEventTime;
      return l;
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  static void setCurrentEventAndMostRecentTime(AWTEvent paramAWTEvent)
  {
    Toolkit.getEventQueue().setCurrentEventAndMostRecentTimeImpl(paramAWTEvent);
  }
  
  private void setCurrentEventAndMostRecentTimeImpl(AWTEvent paramAWTEvent)
  {
    pushPopLock.lock();
    try
    {
      if (Thread.currentThread() != dispatchThread) {
        return;
      }
      currentEvent = new WeakReference(paramAWTEvent);
      long l = Long.MIN_VALUE;
      Object localObject1;
      if ((paramAWTEvent instanceof InputEvent))
      {
        localObject1 = (InputEvent)paramAWTEvent;
        l = ((InputEvent)localObject1).getWhen();
        if ((paramAWTEvent instanceof KeyEvent)) {
          mostRecentKeyEventTime = ((InputEvent)localObject1).getWhen();
        }
      }
      else if ((paramAWTEvent instanceof InputMethodEvent))
      {
        localObject1 = (InputMethodEvent)paramAWTEvent;
        l = ((InputMethodEvent)localObject1).getWhen();
      }
      else if ((paramAWTEvent instanceof ActionEvent))
      {
        localObject1 = (ActionEvent)paramAWTEvent;
        l = ((ActionEvent)localObject1).getWhen();
      }
      else if ((paramAWTEvent instanceof InvocationEvent))
      {
        localObject1 = (InvocationEvent)paramAWTEvent;
        l = ((InvocationEvent)localObject1).getWhen();
      }
      mostRecentEventTime = Math.max(mostRecentEventTime, l);
    }
    finally
    {
      pushPopLock.unlock();
    }
  }
  
  public static void invokeLater(Runnable paramRunnable)
  {
    Toolkit.getEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), paramRunnable));
  }
  
  public static void invokeAndWait(Runnable paramRunnable)
    throws InterruptedException, InvocationTargetException
  {
    invokeAndWait(Toolkit.getDefaultToolkit(), paramRunnable);
  }
  
  static void invokeAndWait(Object paramObject, Runnable paramRunnable)
    throws InterruptedException, InvocationTargetException
  {
    if (isDispatchThread()) {
      throw new Error("Cannot call invokeAndWait from the event dispatcher thread");
    }
    Object local1AWTInvocationLock = new Object() {};
    InvocationEvent localInvocationEvent = new InvocationEvent(paramObject, paramRunnable, local1AWTInvocationLock, true);
    synchronized (local1AWTInvocationLock)
    {
      Toolkit.getEventQueue().postEvent(localInvocationEvent);
      while (!localInvocationEvent.isDispatched()) {
        local1AWTInvocationLock.wait();
      }
    }
    ??? = localInvocationEvent.getThrowable();
    if (??? != null) {
      throw new InvocationTargetException((Throwable)???);
    }
  }
  
  /* Error */
  private void wakeup(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 686 1 0
    //   9: aload_0
    //   10: getfield 548	java/awt/EventQueue:nextQueue	Ljava/awt/EventQueue;
    //   13: ifnull +14 -> 27
    //   16: aload_0
    //   17: getfield 548	java/awt/EventQueue:nextQueue	Ljava/awt/EventQueue;
    //   20: iload_1
    //   21: invokespecial 588	java/awt/EventQueue:wakeup	(Z)V
    //   24: goto +30 -> 54
    //   27: aload_0
    //   28: getfield 547	java/awt/EventQueue:dispatchThread	Ljava/awt/EventDispatchThread;
    //   31: ifnull +15 -> 46
    //   34: aload_0
    //   35: getfield 557	java/awt/EventQueue:pushPopCond	Ljava/util/concurrent/locks/Condition;
    //   38: invokeinterface 685 1 0
    //   43: goto +11 -> 54
    //   46: iload_1
    //   47: ifne +7 -> 54
    //   50: aload_0
    //   51: invokevirtual 584	java/awt/EventQueue:initDispatchThread	()V
    //   54: aload_0
    //   55: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   58: invokeinterface 687 1 0
    //   63: goto +15 -> 78
    //   66: astore_2
    //   67: aload_0
    //   68: getfield 558	java/awt/EventQueue:pushPopLock	Ljava/util/concurrent/locks/Lock;
    //   71: invokeinterface 687 1 0
    //   76: aload_2
    //   77: athrow
    //   78: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	79	0	this	EventQueue
    //   0	79	1	paramBoolean	boolean
    //   66	11	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	54	66	finally
  }
  
  private void setFwDispatcher(FwDispatcher paramFwDispatcher)
  {
    if (nextQueue != null) {
      nextQueue.setFwDispatcher(paramFwDispatcher);
    } else {
      fwDispatcher = paramFwDispatcher;
    }
  }
  
  static
  {
    AWTAccessor.setEventQueueAccessor(new AWTAccessor.EventQueueAccessor()
    {
      public Thread getDispatchThread(EventQueue paramAnonymousEventQueue)
      {
        return paramAnonymousEventQueue.getDispatchThread();
      }
      
      public boolean isDispatchThreadImpl(EventQueue paramAnonymousEventQueue)
      {
        return paramAnonymousEventQueue.isDispatchThreadImpl();
      }
      
      public void removeSourceEvents(EventQueue paramAnonymousEventQueue, Object paramAnonymousObject, boolean paramAnonymousBoolean)
      {
        paramAnonymousEventQueue.removeSourceEvents(paramAnonymousObject, paramAnonymousBoolean);
      }
      
      public boolean noEvents(EventQueue paramAnonymousEventQueue)
      {
        return paramAnonymousEventQueue.noEvents();
      }
      
      public void wakeup(EventQueue paramAnonymousEventQueue, boolean paramAnonymousBoolean)
      {
        paramAnonymousEventQueue.wakeup(paramAnonymousBoolean);
      }
      
      public void invokeAndWait(Object paramAnonymousObject, Runnable paramAnonymousRunnable)
        throws InterruptedException, InvocationTargetException
      {
        EventQueue.invokeAndWait(paramAnonymousObject, paramAnonymousRunnable);
      }
      
      public void setFwDispatcher(EventQueue paramAnonymousEventQueue, FwDispatcher paramAnonymousFwDispatcher)
      {
        paramAnonymousEventQueue.setFwDispatcher(paramAnonymousFwDispatcher);
      }
      
      public long getMostRecentEventTime(EventQueue paramAnonymousEventQueue)
      {
        return paramAnonymousEventQueue.getMostRecentEventTimeImpl();
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\EventQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */