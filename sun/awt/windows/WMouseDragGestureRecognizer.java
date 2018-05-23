package sun.awt.windows;

import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import sun.awt.dnd.SunDragSourceContextPeer;

final class WMouseDragGestureRecognizer
  extends MouseDragGestureRecognizer
{
  private static final long serialVersionUID = -3527844310018033570L;
  protected static int motionThreshold;
  protected static final int ButtonMask = 7168;
  
  protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
  {
    super(paramDragSource, paramComponent, paramInt, paramDragGestureListener);
  }
  
  protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent, int paramInt)
  {
    this(paramDragSource, paramComponent, paramInt, null);
  }
  
  protected WMouseDragGestureRecognizer(DragSource paramDragSource, Component paramComponent)
  {
    this(paramDragSource, paramComponent, 0);
  }
  
  protected WMouseDragGestureRecognizer(DragSource paramDragSource)
  {
    this(paramDragSource, null);
  }
  
  protected int mapDragOperationFromModifiers(MouseEvent paramMouseEvent)
  {
    int i = paramMouseEvent.getModifiersEx();
    int j = i & 0x1C00;
    if ((j != 1024) && (j != 2048) && (j != 4096)) {
      return 0;
    }
    return SunDragSourceContextPeer.convertModifiersToDropAction(i, getSourceActions());
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    events.clear();
    if (mapDragOperationFromModifiers(paramMouseEvent) != 0)
    {
      try
      {
        motionThreshold = DragSource.getDragThreshold();
      }
      catch (Exception localException)
      {
        motionThreshold = 5;
      }
      appendEvent(paramMouseEvent);
    }
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    events.clear();
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent)
  {
    events.clear();
  }
  
  public void mouseExited(MouseEvent paramMouseEvent)
  {
    if (!events.isEmpty())
    {
      int i = mapDragOperationFromModifiers(paramMouseEvent);
      if (i == 0) {
        events.clear();
      }
    }
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    if (!events.isEmpty())
    {
      int i = mapDragOperationFromModifiers(paramMouseEvent);
      if (i == 0) {
        return;
      }
      MouseEvent localMouseEvent = (MouseEvent)events.get(0);
      Point localPoint1 = localMouseEvent.getPoint();
      Point localPoint2 = paramMouseEvent.getPoint();
      int j = Math.abs(x - x);
      int k = Math.abs(y - y);
      if ((j > motionThreshold) || (k > motionThreshold)) {
        fireDragGestureRecognized(i, ((MouseEvent)getTriggerEvent()).getPoint());
      } else {
        appendEvent(paramMouseEvent);
      }
    }
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WMouseDragGestureRecognizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */