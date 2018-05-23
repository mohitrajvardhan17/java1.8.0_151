package sun.awt.windows;

import java.awt.Dimension;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.peer.ScrollbarPeer;

final class WScrollbarPeer
  extends WComponentPeer
  implements ScrollbarPeer
{
  private boolean dragInProgress = false;
  
  static native int getScrollbarSize(int paramInt);
  
  public Dimension getMinimumSize()
  {
    if (((Scrollbar)target).getOrientation() == 1) {
      return new Dimension(getScrollbarSize(1), 50);
    }
    return new Dimension(50, getScrollbarSize(0));
  }
  
  public native void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public native void setLineIncrement(int paramInt);
  
  public native void setPageIncrement(int paramInt);
  
  WScrollbarPeer(Scrollbar paramScrollbar)
  {
    super(paramScrollbar);
  }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize()
  {
    Scrollbar localScrollbar = (Scrollbar)target;
    setValues(localScrollbar.getValue(), localScrollbar.getVisibleAmount(), localScrollbar.getMinimum(), localScrollbar.getMaximum());
    super.initialize();
  }
  
  private void postAdjustmentEvent(final int paramInt1, final int paramInt2, final boolean paramBoolean)
  {
    final Scrollbar localScrollbar = (Scrollbar)target;
    WToolkit.executeOnEventHandlerThread(localScrollbar, new Runnable()
    {
      public void run()
      {
        localScrollbar.setValueIsAdjusting(paramBoolean);
        localScrollbar.setValue(paramInt2);
        postEvent(new AdjustmentEvent(localScrollbar, 601, paramInt1, paramInt2, paramBoolean));
      }
    });
  }
  
  void lineUp(int paramInt)
  {
    postAdjustmentEvent(2, paramInt, false);
  }
  
  void lineDown(int paramInt)
  {
    postAdjustmentEvent(1, paramInt, false);
  }
  
  void pageUp(int paramInt)
  {
    postAdjustmentEvent(3, paramInt, false);
  }
  
  void pageDown(int paramInt)
  {
    postAdjustmentEvent(4, paramInt, false);
  }
  
  void warp(int paramInt)
  {
    postAdjustmentEvent(5, paramInt, false);
  }
  
  void drag(int paramInt)
  {
    if (!dragInProgress) {
      dragInProgress = true;
    }
    postAdjustmentEvent(5, paramInt, true);
  }
  
  void dragEnd(final int paramInt)
  {
    final Scrollbar localScrollbar = (Scrollbar)target;
    if (!dragInProgress) {
      return;
    }
    dragInProgress = false;
    WToolkit.executeOnEventHandlerThread(localScrollbar, new Runnable()
    {
      public void run()
      {
        localScrollbar.setValueIsAdjusting(false);
        postEvent(new AdjustmentEvent(localScrollbar, 601, 5, paramInt, false));
      }
    });
  }
  
  public boolean shouldClearRectBeforePaint()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WScrollbarPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */