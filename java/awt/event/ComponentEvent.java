package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Rectangle;

public class ComponentEvent
  extends AWTEvent
{
  public static final int COMPONENT_FIRST = 100;
  public static final int COMPONENT_LAST = 103;
  public static final int COMPONENT_MOVED = 100;
  public static final int COMPONENT_RESIZED = 101;
  public static final int COMPONENT_SHOWN = 102;
  public static final int COMPONENT_HIDDEN = 103;
  private static final long serialVersionUID = 8101406823902992965L;
  
  public ComponentEvent(Component paramComponent, int paramInt)
  {
    super(paramComponent, paramInt);
  }
  
  public Component getComponent()
  {
    return (source instanceof Component) ? (Component)source : null;
  }
  
  public String paramString()
  {
    Object localObject = source != null ? ((Component)source).getBounds() : null;
    String str;
    switch (id)
    {
    case 102: 
      str = "COMPONENT_SHOWN";
      break;
    case 103: 
      str = "COMPONENT_HIDDEN";
      break;
    case 100: 
      str = "COMPONENT_MOVED (" + x + "," + y + " " + width + "x" + height + ")";
      break;
    case 101: 
      str = "COMPONENT_RESIZED (" + x + "," + y + " " + width + "x" + height + ")";
      break;
    default: 
      str = "unknown type";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\ComponentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */