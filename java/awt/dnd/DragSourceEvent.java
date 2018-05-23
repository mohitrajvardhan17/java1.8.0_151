package java.awt.dnd;

import java.awt.Point;
import java.util.EventObject;

public class DragSourceEvent
  extends EventObject
{
  private static final long serialVersionUID = -763287114604032641L;
  private final boolean locationSpecified;
  private final int x;
  private final int y;
  
  public DragSourceEvent(DragSourceContext paramDragSourceContext)
  {
    super(paramDragSourceContext);
    locationSpecified = false;
    x = 0;
    y = 0;
  }
  
  public DragSourceEvent(DragSourceContext paramDragSourceContext, int paramInt1, int paramInt2)
  {
    super(paramDragSourceContext);
    locationSpecified = true;
    x = paramInt1;
    y = paramInt2;
  }
  
  public DragSourceContext getDragSourceContext()
  {
    return (DragSourceContext)getSource();
  }
  
  public Point getLocation()
  {
    if (locationSpecified) {
      return new Point(x, y);
    }
    return null;
  }
  
  public int getX()
  {
    return x;
  }
  
  public int getY()
  {
    return y;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DragSourceEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */