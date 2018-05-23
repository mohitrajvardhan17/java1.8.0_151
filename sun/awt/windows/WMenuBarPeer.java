package sun.awt.windows;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.peer.MenuBarPeer;

final class WMenuBarPeer
  extends WMenuPeer
  implements MenuBarPeer
{
  final WFramePeer framePeer;
  
  public native void addMenu(Menu paramMenu);
  
  public native void delMenu(int paramInt);
  
  public void addHelpMenu(Menu paramMenu)
  {
    addMenu(paramMenu);
  }
  
  WMenuBarPeer(MenuBar paramMenuBar)
  {
    target = paramMenuBar;
    framePeer = ((WFramePeer)WToolkit.targetToPeer(paramMenuBar.getParent()));
    if (framePeer != null) {
      framePeer.addChildPeer(this);
    }
    create(framePeer);
    checkMenuCreation();
  }
  
  native void create(WFramePeer paramWFramePeer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WMenuBarPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */