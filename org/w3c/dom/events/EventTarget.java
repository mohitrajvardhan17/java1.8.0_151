package org.w3c.dom.events;

public abstract interface EventTarget
{
  public abstract void addEventListener(String paramString, EventListener paramEventListener, boolean paramBoolean);
  
  public abstract void removeEventListener(String paramString, EventListener paramEventListener, boolean paramBoolean);
  
  public abstract boolean dispatchEvent(Event paramEvent)
    throws EventException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\events\EventTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */