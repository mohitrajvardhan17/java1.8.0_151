package javax.swing.event;

import java.util.EventListener;

public abstract interface PopupMenuListener
  extends EventListener
{
  public abstract void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent);
  
  public abstract void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent);
  
  public abstract void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\PopupMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */