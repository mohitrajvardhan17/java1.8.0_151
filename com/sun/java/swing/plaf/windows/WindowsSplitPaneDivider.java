package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class WindowsSplitPaneDivider
  extends BasicSplitPaneDivider
{
  public WindowsSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    super(paramBasicSplitPaneUI);
  }
  
  public void paint(Graphics paramGraphics)
  {
    Color localColor = splitPane.hasFocus() ? UIManager.getColor("SplitPane.shadow") : getBackground();
    Dimension localDimension = getSize();
    if (localColor != null)
    {
      paramGraphics.setColor(localColor);
      paramGraphics.fillRect(0, 0, width, height);
    }
    super.paint(paramGraphics);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */