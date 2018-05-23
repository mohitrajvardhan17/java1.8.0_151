package javax.swing.plaf;

import java.awt.Rectangle;
import javax.swing.JTabbedPane;

public abstract class TabbedPaneUI
  extends ComponentUI
{
  public TabbedPaneUI() {}
  
  public abstract int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2);
  
  public abstract Rectangle getTabBounds(JTabbedPane paramJTabbedPane, int paramInt);
  
  public abstract int getTabRunCount(JTabbedPane paramJTabbedPane);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\TabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */