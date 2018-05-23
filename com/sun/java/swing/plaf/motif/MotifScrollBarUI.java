package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import sun.swing.SwingUtilities2;

public class MotifScrollBarUI
  extends BasicScrollBarUI
{
  public MotifScrollBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifScrollBarUI();
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Insets localInsets = paramJComponent.getInsets();
    int i = left + right;
    int j = top + bottom;
    return scrollbar.getOrientation() == 1 ? new Dimension(i + 11, j + 33) : new Dimension(i + 33, j + 11);
  }
  
  protected JButton createDecreaseButton(int paramInt)
  {
    return new MotifScrollBarButton(paramInt);
  }
  
  protected JButton createIncreaseButton(int paramInt)
  {
    return new MotifScrollBarButton(paramInt);
  }
  
  public void paintTrack(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    paramGraphics.setColor(trackColor);
    paramGraphics.fillRect(x, y, width, height);
  }
  
  public void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    if ((paramRectangle.isEmpty()) || (!scrollbar.isEnabled())) {
      return;
    }
    int i = width;
    int j = height;
    paramGraphics.translate(x, y);
    paramGraphics.setColor(thumbColor);
    paramGraphics.fillRect(0, 0, i - 1, j - 1);
    paramGraphics.setColor(thumbHighlightColor);
    SwingUtilities2.drawVLine(paramGraphics, 0, 0, j - 1);
    SwingUtilities2.drawHLine(paramGraphics, 1, i - 1, 0);
    paramGraphics.setColor(thumbLightShadowColor);
    SwingUtilities2.drawHLine(paramGraphics, 1, i - 1, j - 1);
    SwingUtilities2.drawVLine(paramGraphics, i - 1, 1, j - 2);
    paramGraphics.translate(-x, -y);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */