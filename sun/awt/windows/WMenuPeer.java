package sun.awt.windows;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.peer.MenuPeer;

class WMenuPeer
  extends WMenuItemPeer
  implements MenuPeer
{
  public native void addSeparator();
  
  public void addItem(MenuItem paramMenuItem)
  {
    WMenuItemPeer localWMenuItemPeer = (WMenuItemPeer)WToolkit.targetToPeer(paramMenuItem);
  }
  
  public native void delItem(int paramInt);
  
  WMenuPeer() {}
  
  WMenuPeer(Menu paramMenu)
  {
    target = paramMenu;
    MenuContainer localMenuContainer = paramMenu.getParent();
    if ((localMenuContainer instanceof MenuBar))
    {
      WMenuBarPeer localWMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(localMenuContainer);
      parent = localWMenuBarPeer;
      localWMenuBarPeer.addChildPeer(this);
      createMenu(localWMenuBarPeer);
    }
    else if ((localMenuContainer instanceof Menu))
    {
      parent = ((WMenuPeer)WToolkit.targetToPeer(localMenuContainer));
      parent.addChildPeer(this);
      createSubMenu(parent);
    }
    else
    {
      throw new IllegalArgumentException("unknown menu container class");
    }
    checkMenuCreation();
  }
  
  native void createMenu(WMenuBarPeer paramWMenuBarPeer);
  
  native void createSubMenu(WMenuPeer paramWMenuPeer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WMenuPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */