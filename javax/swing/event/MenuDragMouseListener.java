package javax.swing.event;

import java.util.EventListener;

public abstract interface MenuDragMouseListener
  extends EventListener
{
  public abstract void menuDragMouseEntered(MenuDragMouseEvent paramMenuDragMouseEvent);
  
  public abstract void menuDragMouseExited(MenuDragMouseEvent paramMenuDragMouseEvent);
  
  public abstract void menuDragMouseDragged(MenuDragMouseEvent paramMenuDragMouseEvent);
  
  public abstract void menuDragMouseReleased(MenuDragMouseEvent paramMenuDragMouseEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\MenuDragMouseListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */