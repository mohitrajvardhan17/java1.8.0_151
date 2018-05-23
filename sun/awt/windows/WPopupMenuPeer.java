package sun.awt.windows;

import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.peer.PopupMenuPeer;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.MenuComponentAccessor;
import sun.awt.AWTAccessor.PopupMenuAccessor;

final class WPopupMenuPeer
  extends WMenuPeer
  implements PopupMenuPeer
{
  WPopupMenuPeer(PopupMenu paramPopupMenu)
  {
    target = paramPopupMenu;
    Object localObject = null;
    boolean bool = AWTAccessor.getPopupMenuAccessor().isTrayIconPopup(paramPopupMenu);
    if (bool) {
      localObject = AWTAccessor.getMenuComponentAccessor().getParent(paramPopupMenu);
    } else {
      localObject = paramPopupMenu.getParent();
    }
    if ((localObject instanceof Component))
    {
      WComponentPeer localWComponentPeer = (WComponentPeer)WToolkit.targetToPeer(localObject);
      if (localWComponentPeer == null)
      {
        localObject = WToolkit.getNativeContainer((Component)localObject);
        localWComponentPeer = (WComponentPeer)WToolkit.targetToPeer(localObject);
      }
      localWComponentPeer.addChildPeer(this);
      createMenu(localWComponentPeer);
      checkMenuCreation();
    }
    else
    {
      throw new IllegalArgumentException("illegal popup menu container class");
    }
  }
  
  private native void createMenu(WComponentPeer paramWComponentPeer);
  
  public void show(Event paramEvent)
  {
    Component localComponent = (Component)target;
    WComponentPeer localWComponentPeer = (WComponentPeer)WToolkit.targetToPeer(localComponent);
    if (localWComponentPeer == null)
    {
      Container localContainer = WToolkit.getNativeContainer(localComponent);
      target = localContainer;
      for (Object localObject = localComponent; localObject != localContainer; localObject = ((Component)localObject).getParent())
      {
        Point localPoint = ((Component)localObject).getLocation();
        x += x;
        y += y;
      }
    }
    _show(paramEvent);
  }
  
  void show(Component paramComponent, Point paramPoint)
  {
    WComponentPeer localWComponentPeer = (WComponentPeer)WToolkit.targetToPeer(paramComponent);
    Event localEvent = new Event(paramComponent, 0L, 501, x, y, 0, 0);
    if (localWComponentPeer == null)
    {
      Container localContainer = WToolkit.getNativeContainer(paramComponent);
      target = localContainer;
    }
    x = x;
    y = y;
    _show(localEvent);
  }
  
  private native void _show(Event paramEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WPopupMenuPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */