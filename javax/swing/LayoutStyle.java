package javax.swing;

import java.awt.Container;
import sun.awt.AppContext;

public abstract class LayoutStyle
{
  public static void setInstance(LayoutStyle paramLayoutStyle)
  {
    synchronized (LayoutStyle.class)
    {
      if (paramLayoutStyle == null) {
        AppContext.getAppContext().remove(LayoutStyle.class);
      } else {
        AppContext.getAppContext().put(LayoutStyle.class, paramLayoutStyle);
      }
    }
  }
  
  public static LayoutStyle getInstance()
  {
    LayoutStyle localLayoutStyle;
    synchronized (LayoutStyle.class)
    {
      localLayoutStyle = (LayoutStyle)AppContext.getAppContext().get(LayoutStyle.class);
    }
    if (localLayoutStyle == null) {
      return UIManager.getLookAndFeel().getLayoutStyle();
    }
    return localLayoutStyle;
  }
  
  public LayoutStyle() {}
  
  public abstract int getPreferredGap(JComponent paramJComponent1, JComponent paramJComponent2, ComponentPlacement paramComponentPlacement, int paramInt, Container paramContainer);
  
  public abstract int getContainerGap(JComponent paramJComponent, int paramInt, Container paramContainer);
  
  public static enum ComponentPlacement
  {
    RELATED,  UNRELATED,  INDENT;
    
    private ComponentPlacement() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\LayoutStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */