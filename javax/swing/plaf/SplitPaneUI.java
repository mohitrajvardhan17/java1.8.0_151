package javax.swing.plaf;

import java.awt.Graphics;
import javax.swing.JSplitPane;

public abstract class SplitPaneUI
  extends ComponentUI
{
  public SplitPaneUI() {}
  
  public abstract void resetToPreferredSizes(JSplitPane paramJSplitPane);
  
  public abstract void setDividerLocation(JSplitPane paramJSplitPane, int paramInt);
  
  public abstract int getDividerLocation(JSplitPane paramJSplitPane);
  
  public abstract int getMinimumDividerLocation(JSplitPane paramJSplitPane);
  
  public abstract int getMaximumDividerLocation(JSplitPane paramJSplitPane);
  
  public abstract void finishedPaintingChildren(JSplitPane paramJSplitPane, Graphics paramGraphics);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\SplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */