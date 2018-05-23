package sun.awt.windows;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.ScrollPaneAdjustable;
import java.awt.peer.ScrollPanePeer;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ScrollPaneAdjustableAccessor;
import sun.awt.PeerEvent;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

final class WScrollPanePeer
  extends WPanelPeer
  implements ScrollPanePeer
{
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WScrollPanePeer");
  int scrollbarWidth = _getVScrollbarWidth();
  int scrollbarHeight = _getHScrollbarHeight();
  int prevx;
  int prevy;
  
  static native void initIDs();
  
  native void create(WComponentPeer paramWComponentPeer);
  
  native int getOffset(int paramInt);
  
  WScrollPanePeer(Component paramComponent)
  {
    super(paramComponent);
  }
  
  void initialize()
  {
    super.initialize();
    setInsets();
    Insets localInsets = getInsets();
    setScrollPosition(-left, -top);
  }
  
  public void setUnitIncrement(Adjustable paramAdjustable, int paramInt) {}
  
  public Insets insets()
  {
    return getInsets();
  }
  
  private native void setInsets();
  
  public synchronized native void setScrollPosition(int paramInt1, int paramInt2);
  
  public int getHScrollbarHeight()
  {
    return scrollbarHeight;
  }
  
  private native int _getHScrollbarHeight();
  
  public int getVScrollbarWidth()
  {
    return scrollbarWidth;
  }
  
  private native int _getVScrollbarWidth();
  
  public Point getScrollOffset()
  {
    int i = getOffset(0);
    int j = getOffset(1);
    return new Point(i, j);
  }
  
  public void childResized(int paramInt1, int paramInt2)
  {
    ScrollPane localScrollPane = (ScrollPane)target;
    Dimension localDimension = localScrollPane.getSize();
    setSpans(width, height, paramInt1, paramInt2);
    setInsets();
  }
  
  synchronized native void setSpans(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public void setValue(Adjustable paramAdjustable, int paramInt)
  {
    Component localComponent = getScrollChild();
    if (localComponent == null) {
      return;
    }
    Point localPoint = localComponent.getLocation();
    switch (paramAdjustable.getOrientation())
    {
    case 1: 
      setScrollPosition(-x, paramInt);
      break;
    case 0: 
      setScrollPosition(paramInt, -y);
    }
  }
  
  private Component getScrollChild()
  {
    ScrollPane localScrollPane = (ScrollPane)target;
    Component localComponent = null;
    try
    {
      localComponent = localScrollPane.getComponent(0);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return localComponent;
  }
  
  private void postScrollEvent(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    Adjustor localAdjustor = new Adjustor(paramInt1, paramInt2, paramInt3, paramBoolean);
    WToolkit.executeOnEventHandlerThread(new ScrollEvent(target, localAdjustor));
  }
  
  static
  {
    initIDs();
  }
  
  class Adjustor
    implements Runnable
  {
    int orient;
    int type;
    int pos;
    boolean isAdjusting;
    
    Adjustor(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      orient = paramInt1;
      type = paramInt2;
      pos = paramInt3;
      isAdjusting = paramBoolean;
    }
    
    public void run()
    {
      if (WScrollPanePeer.this.getScrollChild() == null) {
        return;
      }
      ScrollPane localScrollPane = (ScrollPane)target;
      ScrollPaneAdjustable localScrollPaneAdjustable = null;
      if (orient == 1) {
        localScrollPaneAdjustable = (ScrollPaneAdjustable)localScrollPane.getVAdjustable();
      } else if (orient == 0) {
        localScrollPaneAdjustable = (ScrollPaneAdjustable)localScrollPane.getHAdjustable();
      } else if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) {
        WScrollPanePeer.log.fine("Assertion failed: unknown orient");
      }
      if (localScrollPaneAdjustable == null) {
        return;
      }
      int i = localScrollPaneAdjustable.getValue();
      switch (type)
      {
      case 2: 
        i -= localScrollPaneAdjustable.getUnitIncrement();
        break;
      case 1: 
        i += localScrollPaneAdjustable.getUnitIncrement();
        break;
      case 3: 
        i -= localScrollPaneAdjustable.getBlockIncrement();
        break;
      case 4: 
        i += localScrollPaneAdjustable.getBlockIncrement();
        break;
      case 5: 
        i = pos;
        break;
      default: 
        if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) {
          WScrollPanePeer.log.fine("Assertion failed: unknown type");
        }
        return;
      }
      i = Math.max(localScrollPaneAdjustable.getMinimum(), i);
      i = Math.min(localScrollPaneAdjustable.getMaximum(), i);
      localScrollPaneAdjustable.setValueIsAdjusting(isAdjusting);
      AWTAccessor.getScrollPaneAdjustableAccessor().setTypedValue(localScrollPaneAdjustable, i, type);
      for (Object localObject = WScrollPanePeer.this.getScrollChild(); (localObject != null) && (!(((Component)localObject).getPeer() instanceof WComponentPeer)); localObject = ((Component)localObject).getParent()) {}
      if ((WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) && (localObject == null)) {
        WScrollPanePeer.log.fine("Assertion (hwAncestor != null) failed, couldn't find heavyweight ancestor of scroll pane child");
      }
      WComponentPeer localWComponentPeer = (WComponentPeer)((Component)localObject).getPeer();
      localWComponentPeer.paintDamagedAreaImmediately();
    }
  }
  
  class ScrollEvent
    extends PeerEvent
  {
    ScrollEvent(Object paramObject, Runnable paramRunnable)
    {
      super(paramRunnable, 0L);
    }
    
    public PeerEvent coalesceEvents(PeerEvent paramPeerEvent)
    {
      if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINEST)) {
        WScrollPanePeer.log.finest("ScrollEvent coalesced: " + paramPeerEvent);
      }
      if ((paramPeerEvent instanceof ScrollEvent)) {
        return paramPeerEvent;
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WScrollPanePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */