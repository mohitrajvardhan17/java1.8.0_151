package java.awt.event;

import java.awt.AWTEvent;
import java.util.EventListenerProxy;

public class AWTEventListenerProxy
  extends EventListenerProxy<AWTEventListener>
  implements AWTEventListener
{
  private final long eventMask;
  
  public AWTEventListenerProxy(long paramLong, AWTEventListener paramAWTEventListener)
  {
    super(paramAWTEventListener);
    eventMask = paramLong;
  }
  
  public void eventDispatched(AWTEvent paramAWTEvent)
  {
    ((AWTEventListener)getListener()).eventDispatched(paramAWTEvent);
  }
  
  public long getEventMask()
  {
    return eventMask;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\AWTEventListenerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */