package javax.swing.plaf.metal;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class MetalProgressBarUI
  extends BasicProgressBarUI
{
  private Rectangle innards;
  private Rectangle box;
  
  public MetalProgressBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalProgressBarUI();
  }
  
  public void paintDeterminate(Graphics paramGraphics, JComponent paramJComponent)
  {
    super.paintDeterminate(paramGraphics, paramJComponent);
    if (!(paramGraphics instanceof Graphics2D)) {
      return;
    }
    if (progressBar.isBorderPainted())
    {
      Insets localInsets = progressBar.getInsets();
      int i = progressBar.getWidth() - (left + right);
      int j = progressBar.getHeight() - (top + bottom);
      int k = getAmountFull(localInsets, i, j);
      boolean bool = MetalUtils.isLeftToRight(paramJComponent);
      int m = left;
      int n = top;
      int i1 = left + i - 1;
      int i2 = top + j - 1;
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      localGraphics2D.setStroke(new BasicStroke(1.0F));
      if (progressBar.getOrientation() == 0)
      {
        localGraphics2D.setColor(MetalLookAndFeel.getControlShadow());
        localGraphics2D.drawLine(m, n, i1, n);
        if (k > 0)
        {
          localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
          if (bool)
          {
            localGraphics2D.drawLine(m, n, m + k - 1, n);
          }
          else
          {
            localGraphics2D.drawLine(i1, n, i1 - k + 1, n);
            if (progressBar.getPercentComplete() != 1.0D) {
              localGraphics2D.setColor(MetalLookAndFeel.getControlShadow());
            }
          }
        }
        localGraphics2D.drawLine(m, n, m, i2);
      }
      else
      {
        localGraphics2D.setColor(MetalLookAndFeel.getControlShadow());
        localGraphics2D.drawLine(m, n, m, i2);
        if (k > 0)
        {
          localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
          localGraphics2D.drawLine(m, i2, m, i2 - k + 1);
        }
        localGraphics2D.setColor(MetalLookAndFeel.getControlShadow());
        if (progressBar.getPercentComplete() == 1.0D) {
          localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        }
        localGraphics2D.drawLine(m, n, i1, n);
      }
    }
  }
  
  public void paintIndeterminate(Graphics paramGraphics, JComponent paramJComponent)
  {
    super.paintIndeterminate(paramGraphics, paramJComponent);
    if ((!progressBar.isBorderPainted()) || (!(paramGraphics instanceof Graphics2D))) {
      return;
    }
    Insets localInsets = progressBar.getInsets();
    int i = progressBar.getWidth() - (left + right);
    int j = progressBar.getHeight() - (top + bottom);
    int k = getAmountFull(localInsets, i, j);
    boolean bool = MetalUtils.isLeftToRight(paramJComponent);
    Rectangle localRectangle = null;
    localRectangle = getBox(localRectangle);
    int m = left;
    int n = top;
    int i1 = left + i - 1;
    int i2 = top + j - 1;
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    localGraphics2D.setStroke(new BasicStroke(1.0F));
    if (progressBar.getOrientation() == 0)
    {
      localGraphics2D.setColor(MetalLookAndFeel.getControlShadow());
      localGraphics2D.drawLine(m, n, i1, n);
      localGraphics2D.drawLine(m, n, m, i2);
      localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      localGraphics2D.drawLine(x, n, x + width - 1, n);
    }
    else
    {
      localGraphics2D.setColor(MetalLookAndFeel.getControlShadow());
      localGraphics2D.drawLine(m, n, m, i2);
      localGraphics2D.drawLine(m, n, i1, n);
      localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      localGraphics2D.drawLine(m, y, m, y + height - 1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalProgressBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */