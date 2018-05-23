package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;

public class HierarchyEvent
  extends AWTEvent
{
  private static final long serialVersionUID = -5337576970038043990L;
  public static final int HIERARCHY_FIRST = 1400;
  public static final int HIERARCHY_CHANGED = 1400;
  public static final int ANCESTOR_MOVED = 1401;
  public static final int ANCESTOR_RESIZED = 1402;
  public static final int HIERARCHY_LAST = 1402;
  public static final int PARENT_CHANGED = 1;
  public static final int DISPLAYABILITY_CHANGED = 2;
  public static final int SHOWING_CHANGED = 4;
  Component changed;
  Container changedParent;
  long changeFlags;
  
  public HierarchyEvent(Component paramComponent1, int paramInt, Component paramComponent2, Container paramContainer)
  {
    super(paramComponent1, paramInt);
    changed = paramComponent2;
    changedParent = paramContainer;
  }
  
  public HierarchyEvent(Component paramComponent1, int paramInt, Component paramComponent2, Container paramContainer, long paramLong)
  {
    super(paramComponent1, paramInt);
    changed = paramComponent2;
    changedParent = paramContainer;
    changeFlags = paramLong;
  }
  
  public Component getComponent()
  {
    return (source instanceof Component) ? (Component)source : null;
  }
  
  public Component getChanged()
  {
    return changed;
  }
  
  public Container getChangedParent()
  {
    return changedParent;
  }
  
  public long getChangeFlags()
  {
    return changeFlags;
  }
  
  public String paramString()
  {
    String str;
    switch (id)
    {
    case 1401: 
      str = "ANCESTOR_MOVED (" + changed + "," + changedParent + ")";
      break;
    case 1402: 
      str = "ANCESTOR_RESIZED (" + changed + "," + changedParent + ")";
      break;
    case 1400: 
      str = "HIERARCHY_CHANGED (";
      int i = 1;
      if ((changeFlags & 1L) != 0L)
      {
        i = 0;
        str = str + "PARENT_CHANGED";
      }
      if ((changeFlags & 0x2) != 0L)
      {
        if (i != 0) {
          i = 0;
        } else {
          str = str + ",";
        }
        str = str + "DISPLAYABILITY_CHANGED";
      }
      if ((changeFlags & 0x4) != 0L)
      {
        if (i != 0) {
          i = 0;
        } else {
          str = str + ",";
        }
        str = str + "SHOWING_CHANGED";
      }
      if (i == 0) {
        str = str + ",";
      }
      str = str + changed + "," + changedParent + ")";
      break;
    default: 
      str = "unknown type";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\HierarchyEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */