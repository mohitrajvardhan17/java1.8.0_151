package org.w3c.dom.ls;

import org.w3c.dom.events.Event;

public abstract interface LSProgressEvent
  extends Event
{
  public abstract LSInput getInput();
  
  public abstract int getPosition();
  
  public abstract int getTotalSize();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\ls\LSProgressEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */