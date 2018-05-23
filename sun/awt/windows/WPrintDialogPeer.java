package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.Font;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

class WPrintDialogPeer
  extends WWindowPeer
  implements DialogPeer
{
  private WComponentPeer parent;
  private Vector<WWindowPeer> blockedWindows = new Vector();
  
  WPrintDialogPeer(WPrintDialog paramWPrintDialog)
  {
    super(paramWPrintDialog);
  }
  
  void create(WComponentPeer paramWComponentPeer)
  {
    parent = paramWComponentPeer;
  }
  
  protected void checkCreation() {}
  
  protected void disposeImpl()
  {
    WToolkit.targetDisposedPeer(target, this);
  }
  
  private native boolean _show();
  
  public void show()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          ((WPrintDialog)target).setRetVal(WPrintDialogPeer.this._show());
        }
        catch (Exception localException) {}
        ((WPrintDialog)target).setVisible(false);
      }
    }).start();
  }
  
  synchronized void setHWnd(long paramLong)
  {
    hwnd = paramLong;
    Iterator localIterator = blockedWindows.iterator();
    while (localIterator.hasNext())
    {
      WWindowPeer localWWindowPeer = (WWindowPeer)localIterator.next();
      if (paramLong != 0L) {
        localWWindowPeer.modalDisable((Dialog)target, paramLong);
      } else {
        localWWindowPeer.modalEnable((Dialog)target);
      }
    }
  }
  
  synchronized void blockWindow(WWindowPeer paramWWindowPeer)
  {
    blockedWindows.add(paramWWindowPeer);
    if (hwnd != 0L) {
      paramWWindowPeer.modalDisable((Dialog)target, hwnd);
    }
  }
  
  synchronized void unblockWindow(WWindowPeer paramWWindowPeer)
  {
    blockedWindows.remove(paramWWindowPeer);
    if (hwnd != 0L) {
      paramWWindowPeer.modalEnable((Dialog)target);
    }
  }
  
  public void blockWindows(List<Window> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Window localWindow = (Window)localIterator.next();
      WWindowPeer localWWindowPeer = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(localWindow);
      if (localWWindowPeer != null) {
        blockWindow(localWWindowPeer);
      }
    }
  }
  
  public native void toFront();
  
  public native void toBack();
  
  void initialize() {}
  
  public void updateAlwaysOnTopState() {}
  
  public void setResizable(boolean paramBoolean) {}
  
  void hide() {}
  
  void enable() {}
  
  void disable() {}
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public boolean handleEvent(Event paramEvent)
  {
    return false;
  }
  
  public void setForeground(Color paramColor) {}
  
  public void setBackground(Color paramColor) {}
  
  public void setFont(Font paramFont) {}
  
  public void updateMinimumSize() {}
  
  public void updateIconImages() {}
  
  public boolean requestFocus(boolean paramBoolean1, boolean paramBoolean2)
  {
    return false;
  }
  
  public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
  {
    return false;
  }
  
  public void updateFocusableWindowState() {}
  
  void start() {}
  
  public void beginValidate() {}
  
  public void endValidate() {}
  
  void invalidate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void addDropTarget(DropTarget paramDropTarget) {}
  
  public void removeDropTarget(DropTarget paramDropTarget) {}
  
  public void setZOrder(ComponentPeer paramComponentPeer) {}
  
  private static native void initIDs();
  
  public void applyShape(Region paramRegion) {}
  
  public void setOpacity(float paramFloat) {}
  
  public void setOpaque(boolean paramBoolean) {}
  
  public void updateWindow(BufferedImage paramBufferedImage) {}
  
  public void createScreenSurface(boolean paramBoolean) {}
  
  public void replaceSurfaceData() {}
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WPrintDialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */