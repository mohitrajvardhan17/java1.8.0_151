package javax.swing.event;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

public class MenuDragMouseEvent
  extends MouseEvent
{
  private MenuElement[] path;
  private MenuSelectionManager manager;
  
  public MenuDragMouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager)
  {
    super(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean);
    path = paramArrayOfMenuElement;
    manager = paramMenuSelectionManager;
  }
  
  public MenuDragMouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager)
  {
    super(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean, 0);
    path = paramArrayOfMenuElement;
    manager = paramMenuSelectionManager;
  }
  
  public MenuElement[] getPath()
  {
    return path;
  }
  
  public MenuSelectionManager getMenuSelectionManager()
  {
    return manager;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\MenuDragMouseEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */