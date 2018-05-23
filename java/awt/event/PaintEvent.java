package java.awt.event;

import java.awt.Component;
import java.awt.Rectangle;

public class PaintEvent
  extends ComponentEvent
{
  public static final int PAINT_FIRST = 800;
  public static final int PAINT_LAST = 801;
  public static final int PAINT = 800;
  public static final int UPDATE = 801;
  Rectangle updateRect;
  private static final long serialVersionUID = 1267492026433337593L;
  
  public PaintEvent(Component paramComponent, int paramInt, Rectangle paramRectangle)
  {
    super(paramComponent, paramInt);
    updateRect = paramRectangle;
  }
  
  public Rectangle getUpdateRect()
  {
    return updateRect;
  }
  
  public void setUpdateRect(Rectangle paramRectangle)
  {
    updateRect = paramRectangle;
  }
  
  public String paramString()
  {
    String str;
    switch (id)
    {
    case 800: 
      str = "PAINT";
      break;
    case 801: 
      str = "UPDATE";
      break;
    default: 
      str = "unknown type";
    }
    return str + ",updateRect=" + (updateRect != null ? updateRect.toString() : "null");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\PaintEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */