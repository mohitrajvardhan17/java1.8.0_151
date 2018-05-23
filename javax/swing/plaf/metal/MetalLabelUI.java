package javax.swing.plaf.metal;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalLabelUI
  extends BasicLabelUI
{
  protected static MetalLabelUI metalLabelUI = new MetalLabelUI();
  private static final Object METAL_LABEL_UI_KEY = new Object();
  
  public MetalLabelUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    if (System.getSecurityManager() != null)
    {
      AppContext localAppContext = AppContext.getAppContext();
      MetalLabelUI localMetalLabelUI = (MetalLabelUI)localAppContext.get(METAL_LABEL_UI_KEY);
      if (localMetalLabelUI == null)
      {
        localMetalLabelUI = new MetalLabelUI();
        localAppContext.put(METAL_LABEL_UI_KEY, localMetalLabelUI);
      }
      return localMetalLabelUI;
    }
    return metalLabelUI;
  }
  
  protected void paintDisabledText(JLabel paramJLabel, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2)
  {
    int i = paramJLabel.getDisplayedMnemonicIndex();
    paramGraphics.setColor(UIManager.getColor("Label.disabledForeground"));
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalLabelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */