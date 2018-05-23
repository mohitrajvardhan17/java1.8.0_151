package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class SynthRadioButtonMenuItemUI
  extends SynthMenuItemUI
{
  public SynthRadioButtonMenuItemUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthRadioButtonMenuItemUI();
  }
  
  protected String getPropertyPrefix()
  {
    return "RadioButtonMenuItem";
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    paramSynthContext.getPainter().paintRadioButtonMenuItemBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintRadioButtonMenuItemBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthRadioButtonMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */