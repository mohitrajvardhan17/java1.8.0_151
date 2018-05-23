package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class SynthToggleButtonUI
  extends SynthButtonUI
{
  public SynthToggleButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthToggleButtonUI();
  }
  
  protected String getPropertyPrefix()
  {
    return "ToggleButton.";
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    if (((AbstractButton)paramJComponent).isContentAreaFilled())
    {
      int i = 0;
      int j = 0;
      int k = paramJComponent.getWidth();
      int m = paramJComponent.getHeight();
      SynthPainter localSynthPainter = paramSynthContext.getPainter();
      localSynthPainter.paintToggleButtonBackground(paramSynthContext, paramGraphics, i, j, k, m);
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintToggleButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */