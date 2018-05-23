package javax.swing.event;

import java.util.EventListener;

public abstract interface MenuKeyListener
  extends EventListener
{
  public abstract void menuKeyTyped(MenuKeyEvent paramMenuKeyEvent);
  
  public abstract void menuKeyPressed(MenuKeyEvent paramMenuKeyEvent);
  
  public abstract void menuKeyReleased(MenuKeyEvent paramMenuKeyEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\MenuKeyListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */