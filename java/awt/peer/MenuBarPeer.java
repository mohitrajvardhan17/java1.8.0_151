package java.awt.peer;

import java.awt.Menu;

public abstract interface MenuBarPeer
  extends MenuComponentPeer
{
  public abstract void addMenu(Menu paramMenu);
  
  public abstract void delMenu(int paramInt);
  
  public abstract void addHelpMenu(Menu paramMenu);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\MenuBarPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */