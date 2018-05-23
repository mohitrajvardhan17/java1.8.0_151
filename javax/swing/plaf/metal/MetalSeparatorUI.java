package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

public class MetalSeparatorUI
  extends BasicSeparatorUI
{
  public MetalSeparatorUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalSeparatorUI();
  }
  
  protected void installDefaults(JSeparator paramJSeparator)
  {
    LookAndFeel.installColors(paramJSeparator, "Separator.background", "Separator.foreground");
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Dimension localDimension = paramJComponent.getSize();
    if (((JSeparator)paramJComponent).getOrientation() == 1)
    {
      paramGraphics.setColor(paramJComponent.getForeground());
      paramGraphics.drawLine(0, 0, 0, height);
      paramGraphics.setColor(paramJComponent.getBackground());
      paramGraphics.drawLine(1, 0, 1, height);
    }
    else
    {
      paramGraphics.setColor(paramJComponent.getForeground());
      paramGraphics.drawLine(0, 0, width, 0);
      paramGraphics.setColor(paramJComponent.getBackground());
      paramGraphics.drawLine(0, 1, width, 1);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    if (((JSeparator)paramJComponent).getOrientation() == 1) {
      return new Dimension(2, 0);
    }
    return new Dimension(0, 2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */