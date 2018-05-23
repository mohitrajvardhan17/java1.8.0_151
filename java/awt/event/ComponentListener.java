package java.awt.event;

import java.util.EventListener;

public abstract interface ComponentListener
  extends EventListener
{
  public abstract void componentResized(ComponentEvent paramComponentEvent);
  
  public abstract void componentMoved(ComponentEvent paramComponentEvent);
  
  public abstract void componentShown(ComponentEvent paramComponentEvent);
  
  public abstract void componentHidden(ComponentEvent paramComponentEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\ComponentListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */