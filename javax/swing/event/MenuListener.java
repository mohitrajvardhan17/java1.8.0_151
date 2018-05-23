package javax.swing.event;

import java.util.EventListener;

public abstract interface MenuListener
  extends EventListener
{
  public abstract void menuSelected(MenuEvent paramMenuEvent);
  
  public abstract void menuDeselected(MenuEvent paramMenuEvent);
  
  public abstract void menuCanceled(MenuEvent paramMenuEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\MenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */