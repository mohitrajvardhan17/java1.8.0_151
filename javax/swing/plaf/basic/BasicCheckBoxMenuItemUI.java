package javax.swing.plaf.basic;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.ComponentUI;

public class BasicCheckBoxMenuItemUI
  extends BasicMenuItemUI
{
  public BasicCheckBoxMenuItemUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicCheckBoxMenuItemUI();
  }
  
  protected String getPropertyPrefix()
  {
    return "CheckBoxMenuItem";
  }
  
  public void processMouseEvent(JMenuItem paramJMenuItem, MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager)
  {
    Point localPoint = paramMouseEvent.getPoint();
    if ((x >= 0) && (x < paramJMenuItem.getWidth()) && (y >= 0) && (y < paramJMenuItem.getHeight()))
    {
      if (paramMouseEvent.getID() == 502)
      {
        paramMenuSelectionManager.clearSelectedPath();
        paramJMenuItem.doClick(0);
      }
      else
      {
        paramMenuSelectionManager.setSelectedPath(paramArrayOfMenuElement);
      }
    }
    else if (paramJMenuItem.getModel().isArmed())
    {
      MenuElement[] arrayOfMenuElement = new MenuElement[paramArrayOfMenuElement.length - 1];
      int i = 0;
      int j = paramArrayOfMenuElement.length - 1;
      while (i < j)
      {
        arrayOfMenuElement[i] = paramArrayOfMenuElement[i];
        i++;
      }
      paramMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicCheckBoxMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */