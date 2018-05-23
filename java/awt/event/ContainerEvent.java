package java.awt.event;

import java.awt.Component;
import java.awt.Container;

public class ContainerEvent
  extends ComponentEvent
{
  public static final int CONTAINER_FIRST = 300;
  public static final int CONTAINER_LAST = 301;
  public static final int COMPONENT_ADDED = 300;
  public static final int COMPONENT_REMOVED = 301;
  Component child;
  private static final long serialVersionUID = -4114942250539772041L;
  
  public ContainerEvent(Component paramComponent1, int paramInt, Component paramComponent2)
  {
    super(paramComponent1, paramInt);
    child = paramComponent2;
  }
  
  public Container getContainer()
  {
    return (source instanceof Container) ? (Container)source : null;
  }
  
  public Component getChild()
  {
    return child;
  }
  
  public String paramString()
  {
    String str;
    switch (id)
    {
    case 300: 
      str = "COMPONENT_ADDED";
      break;
    case 301: 
      str = "COMPONENT_REMOVED";
      break;
    default: 
      str = "unknown type";
    }
    return str + ",child=" + child.getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\ContainerEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */