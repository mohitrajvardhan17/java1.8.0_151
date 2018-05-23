package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.JComponent;

public abstract interface SynthUI
  extends SynthConstants
{
  public abstract SynthContext getContext(JComponent paramJComponent);
  
  public abstract void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */