package sun.awt;

import java.awt.AWTEvent;

public class EventQueueItem
{
  public AWTEvent event;
  public EventQueueItem next;
  
  public EventQueueItem(AWTEvent paramAWTEvent)
  {
    event = paramAWTEvent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\EventQueueItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */