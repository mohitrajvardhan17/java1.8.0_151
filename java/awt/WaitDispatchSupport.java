package java.awt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.awt.PeerEvent;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

class WaitDispatchSupport
  implements SecondaryLoop
{
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.event.WaitDispatchSupport");
  private EventDispatchThread dispatchThread;
  private EventFilter filter;
  private volatile Conditional extCondition;
  private volatile Conditional condition;
  private long interval;
  private static Timer timer;
  private TimerTask timerTask;
  private AtomicBoolean keepBlockingEDT = new AtomicBoolean(false);
  private AtomicBoolean keepBlockingCT = new AtomicBoolean(false);
  private final Runnable wakingRunnable = new Runnable()
  {
    public void run()
    {
      WaitDispatchSupport.log.fine("Wake up EDT");
      synchronized (WaitDispatchSupport.access$900())
      {
        keepBlockingCT.set(false);
        WaitDispatchSupport.access$900().notifyAll();
      }
      WaitDispatchSupport.log.fine("Wake up EDT done");
    }
  };
  
  private static synchronized void initializeTimer()
  {
    if (timer == null) {
      timer = new Timer("AWT-WaitDispatchSupport-Timer", true);
    }
  }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread)
  {
    this(paramEventDispatchThread, null);
  }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread, Conditional paramConditional)
  {
    if (paramEventDispatchThread == null) {
      throw new IllegalArgumentException("The dispatchThread can not be null");
    }
    dispatchThread = paramEventDispatchThread;
    extCondition = paramConditional;
    condition = new Conditional()
    {
      public boolean evaluate()
      {
        if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINEST)) {
          WaitDispatchSupport.log.finest("evaluate(): blockingEDT=" + keepBlockingEDT.get() + ", blockingCT=" + keepBlockingCT.get());
        }
        int i = extCondition != null ? extCondition.evaluate() : 1;
        if ((!keepBlockingEDT.get()) || (i == 0))
        {
          if (timerTask != null)
          {
            timerTask.cancel();
            timerTask = null;
          }
          return false;
        }
        return true;
      }
    };
  }
  
  public WaitDispatchSupport(EventDispatchThread paramEventDispatchThread, Conditional paramConditional, EventFilter paramEventFilter, long paramLong)
  {
    this(paramEventDispatchThread, paramConditional);
    filter = paramEventFilter;
    if (paramLong < 0L) {
      throw new IllegalArgumentException("The interval value must be >= 0");
    }
    interval = paramLong;
    if (paramLong != 0L) {
      initializeTimer();
    }
  }
  
  public boolean enter()
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("enter(): blockingEDT=" + keepBlockingEDT.get() + ", blockingCT=" + keepBlockingCT.get());
    }
    if (!keepBlockingEDT.compareAndSet(false, true))
    {
      log.fine("The secondary loop is already running, aborting");
      return false;
    }
    final Runnable local2 = new Runnable()
    {
      public void run()
      {
        WaitDispatchSupport.log.fine("Starting a new event pump");
        if (filter == null) {
          dispatchThread.pumpEvents(condition);
        } else {
          dispatchThread.pumpEventsForFilter(condition, filter);
        }
      }
    };
    Thread localThread = Thread.currentThread();
    if (localThread == dispatchThread)
    {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
        log.finest("On dispatch thread: " + dispatchThread);
      }
      if (interval != 0L)
      {
        if (log.isLoggable(PlatformLogger.Level.FINEST)) {
          log.finest("scheduling the timer for " + interval + " ms");
        }
        timer.schedule( = new TimerTask()
        {
          public void run()
          {
            if (keepBlockingEDT.compareAndSet(true, false)) {
              WaitDispatchSupport.this.wakeupEDT();
            }
          }
        }, interval);
      }
      SequencedEvent localSequencedEvent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
      if (localSequencedEvent != null)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("Dispose current SequencedEvent: " + localSequencedEvent);
        }
        localSequencedEvent.dispose();
      }
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          local2.run();
          return null;
        }
      });
    }
    else
    {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
        log.finest("On non-dispatch thread: " + localThread);
      }
      synchronized (getTreeLock())
      {
        if (filter != null) {
          dispatchThread.addEventFilter(filter);
        }
        try
        {
          EventQueue localEventQueue = dispatchThread.getEventQueue();
          localEventQueue.postEvent(new PeerEvent(this, local2, 1L));
          keepBlockingCT.set(true);
          if (interval > 0L)
          {
            long l = System.currentTimeMillis();
            while ((keepBlockingCT.get()) && ((extCondition == null) || (extCondition.evaluate())) && (l + interval > System.currentTimeMillis())) {
              getTreeLock().wait(interval);
            }
          }
          else
          {
            while ((keepBlockingCT.get()) && ((extCondition == null) || (extCondition.evaluate()))) {
              getTreeLock().wait();
            }
          }
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("waitDone " + keepBlockingEDT.get() + " " + keepBlockingCT.get());
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("Exception caught while waiting: " + localInterruptedException);
          }
        }
        finally
        {
          if (filter != null) {
            dispatchThread.removeEventFilter(filter);
          }
        }
        keepBlockingEDT.set(false);
        keepBlockingCT.set(false);
      }
    }
    return true;
  }
  
  public boolean exit()
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("exit(): blockingEDT=" + keepBlockingEDT.get() + ", blockingCT=" + keepBlockingCT.get());
    }
    if (keepBlockingEDT.compareAndSet(true, false))
    {
      wakeupEDT();
      return true;
    }
    return false;
  }
  
  private static final Object getTreeLock()
  {
    return Component.LOCK;
  }
  
  private void wakeupEDT()
  {
    if (log.isLoggable(PlatformLogger.Level.FINEST)) {
      log.finest("wakeupEDT(): EDT == " + dispatchThread);
    }
    EventQueue localEventQueue = dispatchThread.getEventQueue();
    localEventQueue.postEvent(new PeerEvent(this, wakingRunnable, 1L));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\WaitDispatchSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */