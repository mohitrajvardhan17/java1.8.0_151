package java.awt.event;

import java.util.EventListener;

public abstract interface ContainerListener
  extends EventListener
{
  public abstract void componentAdded(ContainerEvent paramContainerEvent);
  
  public abstract void componentRemoved(ContainerEvent paramContainerEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\ContainerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */