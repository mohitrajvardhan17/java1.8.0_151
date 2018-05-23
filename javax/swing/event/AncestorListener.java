package javax.swing.event;

import java.util.EventListener;

public abstract interface AncestorListener
  extends EventListener
{
  public abstract void ancestorAdded(AncestorEvent paramAncestorEvent);
  
  public abstract void ancestorRemoved(AncestorEvent paramAncestorEvent);
  
  public abstract void ancestorMoved(AncestorEvent paramAncestorEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\AncestorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */