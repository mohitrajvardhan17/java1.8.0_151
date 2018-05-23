package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class SynthRadioButtonUI
  extends SynthToggleButtonUI
{
  public SynthRadioButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthRadioButtonUI();
  }
  
  protected String getPropertyPrefix()
  {
    return "RadioButton.";
  }
  
  protected Icon getSizingIcon(AbstractButton paramAbstractButton)
  {
    return getIcon(paramAbstractButton);
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    paramSynthContext.getPainter().paintRadioButtonBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintRadioButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */