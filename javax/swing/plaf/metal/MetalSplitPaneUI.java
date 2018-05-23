package javax.swing.plaf.metal;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MetalSplitPaneUI
  extends BasicSplitPaneUI
{
  public MetalSplitPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalSplitPaneUI();
  }
  
  public BasicSplitPaneDivider createDefaultDivider()
  {
    return new MetalSplitPaneDivider(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */