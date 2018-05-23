package javax.swing.event;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

public class MenuKeyEvent
  extends KeyEvent
{
  private MenuElement[] path;
  private MenuSelectionManager manager;
  
  public MenuKeyEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, char paramChar, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager)
  {
    super(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramChar);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\MenuKeyEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */