package javax.swing;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract interface MenuElement
{
  public abstract void processMouseEvent(MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager);
  
  public abstract void processKeyEvent(KeyEvent paramKeyEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager);
  
  public abstract void menuSelectionChanged(boolean paramBoolean);
  
  public abstract MenuElement[] getSubElements();
  
  public abstract Component getComponent();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\MenuElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */