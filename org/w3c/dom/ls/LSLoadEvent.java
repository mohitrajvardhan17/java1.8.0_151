package org.w3c.dom.ls;

import org.w3c.dom.Document;
import org.w3c.dom.events.Event;

public abstract interface LSLoadEvent
  extends Event
{
  public abstract Document getNewDocument();
  
  public abstract LSInput getInput();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\ls\LSLoadEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */