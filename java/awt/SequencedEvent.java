package java.awt;

import java.util.LinkedList;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.SequencedEventAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

class SequencedEvent
  extends AWTEvent
  implements ActiveEvent
{
  private static final long serialVersionUID = 547742659238625067L;
  private static final int ID = 1006;
  private static final LinkedList<SequencedEvent> list = new LinkedList();
  private final AWTEvent nested;
  private AppContext appContext;
  private boolean disposed;
  
  public SequencedEvent(AWTEvent paramAWTEvent)
  {
    super(paramAWTEvent.getSource(), 1006);
    nested = paramAWTEvent;
    SunToolkit.setSystemGenerated(paramAWTEvent);
    synchronized (SequencedEvent.class)
    {
      list.add(this);
    }
  }
  
  public final void dispatch()
  {
    try
    {
      appContext = AppContext.getAppContext();
      if (getFirst() != this) {
        if (EventQueue.isDispatchThread())
        {
          EventDispatchThread localEventDispatchThread = (EventDispatchThread)Thread.currentThread();
          localEventDispatchThread.pumpEvents(1007, new Conditional()
          {
            public boolean evaluate()
            {
              return !isFirstOrDisposed();
            }
          });
        }
        else
        {
          while (!isFirstOrDisposed()) {
            synchronized (SequencedEvent.class)
            {
              try
              {
                SequencedEvent.class.wait(1000L);
              }
              catch (InterruptedException localInterruptedException)
              {
                break;
              }
            }
          }
        }
      }
      if (!disposed)
      {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setCurrentSequencedEvent(this);
        Toolkit.getEventQueue().dispatchEvent(nested);
      }
    }
    finally
    {
      dispose();
    }
  }
  
  private static final boolean isOwnerAppContextDisposed(SequencedEvent paramSequencedEvent)
  {
    if (paramSequencedEvent != null)
    {
      Object localObject = nested.getSource();
      if ((localObject instanceof Component)) {
        return appContext.isDisposed();
      }
    }
    return false;
  }
  
  public final boolean isFirstOrDisposed()
  {
    if (disposed) {
      return true;
    }
    return (this == getFirstWithContext()) || (disposed);
  }
  
  private static final synchronized SequencedEvent getFirst()
  {
    return (SequencedEvent)list.getFirst();
  }
  
  private static final SequencedEvent getFirstWithContext()
  {
    for (SequencedEvent localSequencedEvent = getFirst(); isOwnerAppContextDisposed(localSequencedEvent); localSequencedEvent = getFirst()) {
      localSequencedEvent.dispose();
    }
    return localSequencedEvent;
  }
  
  final void dispose()
  {
    synchronized (SequencedEvent.class)
    {
      if (disposed) {
        return;
      }
      if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent() == this) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setCurrentSequencedEvent(null);
      }
      disposed = true;
    }
    if (appContext != null) {
      SunToolkit.postEvent(appContext, new SentEvent());
    }
    ??? = null;
    synchronized (SequencedEvent.class)
    {
      SequencedEvent.class.notifyAll();
      if (list.getFirst() == this)
      {
        list.removeFirst();
        if (!list.isEmpty()) {
          ??? = (SequencedEvent)list.getFirst();
        }
      }
      else
      {
        list.remove(this);
      }
    }
    if ((??? != null) && (appContext != null)) {
      SunToolkit.postEvent(appContext, new SentEvent());
    }
  }
  
  static
  {
    AWTAccessor.setSequencedEventAccessor(new AWTAccessor.SequencedEventAccessor()
    {
      public AWTEvent getNested(AWTEvent paramAnonymousAWTEvent)
      {
        return nested;
      }
      
      public boolean isSequencedEvent(AWTEvent paramAnonymousAWTEvent)
      {
        return paramAnonymousAWTEvent instanceof SequencedEvent;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\SequencedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */