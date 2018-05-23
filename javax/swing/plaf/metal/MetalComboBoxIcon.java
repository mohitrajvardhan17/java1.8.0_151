package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComponent;

public class MetalComboBoxIcon
  implements Icon, Serializable
{
  public MetalComboBoxIcon() {}
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    JComponent localJComponent = (JComponent)paramComponent;
    int i = getIconWidth();
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(localJComponent.isEnabled() ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlShadow());
    paramGraphics.drawLine(0, 0, i - 1, 0);
    paramGraphics.drawLine(1, 1, 1 + (i - 3), 1);
    paramGraphics.drawLine(2, 2, 2 + (i - 5), 2);
    paramGraphics.drawLine(3, 3, 3 + (i - 7), 3);
    paramGraphics.drawLine(4, 4, 4 + (i - 9), 4);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  public int getIconWidth()
  {
    return 10;
  }
  
  public int getIconHeight()
  {
    return 5;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalComboBoxIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */