package javax.swing.plaf;

import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public abstract class PopupMenuUI
  extends ComponentUI
{
  public PopupMenuUI() {}
  
  public boolean isPopupTrigger(MouseEvent paramMouseEvent)
  {
    return paramMouseEvent.isPopupTrigger();
  }
  
  public Popup getPopup(JPopupMenu paramJPopupMenu, int paramInt1, int paramInt2)
  {
    PopupFactory localPopupFactory = PopupFactory.getSharedInstance();
    return localPopupFactory.getPopup(paramJPopupMenu.getInvoker(), paramJPopupMenu, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\PopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */