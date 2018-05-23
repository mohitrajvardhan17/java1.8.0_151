package sun.awt.windows;

import java.awt.CheckboxMenuItem;
import java.awt.event.ItemEvent;
import java.awt.peer.CheckboxMenuItemPeer;

final class WCheckboxMenuItemPeer
  extends WMenuItemPeer
  implements CheckboxMenuItemPeer
{
  public native void setState(boolean paramBoolean);
  
  WCheckboxMenuItemPeer(CheckboxMenuItem paramCheckboxMenuItem)
  {
    super(paramCheckboxMenuItem, true);
    setState(paramCheckboxMenuItem.getState());
  }
  
  public void handleAction(final boolean paramBoolean)
  {
    final CheckboxMenuItem localCheckboxMenuItem = (CheckboxMenuItem)target;
    WToolkit.executeOnEventHandlerThread(localCheckboxMenuItem, new Runnable()
    {
      public void run()
      {
        localCheckboxMenuItem.setState(paramBoolean);
        postEvent(new ItemEvent(localCheckboxMenuItem, 701, localCheckboxMenuItem.getLabel(), paramBoolean ? 1 : 2));
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WCheckboxMenuItemPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */