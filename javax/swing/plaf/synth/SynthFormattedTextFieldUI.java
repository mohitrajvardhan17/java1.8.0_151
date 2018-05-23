package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class SynthFormattedTextFieldUI
  extends SynthTextFieldUI
{
  public SynthFormattedTextFieldUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthFormattedTextFieldUI();
  }
  
  protected String getPropertyPrefix()
  {
    return "FormattedTextField";
  }
  
  void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    paramSynthContext.getPainter().paintFormattedTextFieldBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintFormattedTextFieldBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthFormattedTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */