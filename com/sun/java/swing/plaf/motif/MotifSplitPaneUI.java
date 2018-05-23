package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MotifSplitPaneUI
  extends BasicSplitPaneUI
{
  public MotifSplitPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifSplitPaneUI();
  }
  
  public BasicSplitPaneDivider createDefaultDivider()
  {
    return new MotifSplitPaneDivider(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */