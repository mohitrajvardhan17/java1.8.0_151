package sun.awt.event;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;

public class IgnorePaintEvent
  extends PaintEvent
{
  public IgnorePaintEvent(Component paramComponent, int paramInt, Rectangle paramRectangle)
  {
    super(paramComponent, paramInt, paramRectangle);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\event\IgnorePaintEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */