package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;

public class EventQueueDelegate
{
  private static final Object EVENT_QUEUE_DELEGATE_KEY = new StringBuilder("EventQueueDelegate.Delegate");
  
  public EventQueueDelegate() {}
  
  public static void setDelegate(Delegate paramDelegate)
  {
    AppContext.getAppContext().put(EVENT_QUEUE_DELEGATE_KEY, paramDelegate);
  }
  
  public static Delegate getDelegate()
  {
    return (Delegate)AppContext.getAppContext().get(EVENT_QUEUE_DELEGATE_KEY);
  }
  
  public static abstract interface Delegate
  {
    public abstract AWTEvent getNextEvent(EventQueue paramEventQueue)
      throws InterruptedException;
    
    public abstract Object beforeDispatch(AWTEvent paramAWTEvent)
      throws InterruptedException;
    
    public abstract void afterDispatch(AWTEvent paramAWTEvent, Object paramObject)
      throws InterruptedException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\EventQueueDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */