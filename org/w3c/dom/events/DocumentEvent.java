package org.w3c.dom.events;

import org.w3c.dom.DOMException;

public abstract interface DocumentEvent
{
  public abstract Event createEvent(String paramString)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\events\DocumentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */