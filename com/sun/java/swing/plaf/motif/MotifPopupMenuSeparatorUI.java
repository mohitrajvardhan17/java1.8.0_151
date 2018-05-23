package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class MotifPopupMenuSeparatorUI
  extends MotifSeparatorUI
{
  public MotifPopupMenuSeparatorUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifPopupMenuSeparatorUI();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Dimension localDimension = paramJComponent.getSize();
    paramGraphics.setColor(paramJComponent.getForeground());
    paramGraphics.drawLine(0, 0, width, 0);
    paramGraphics.setColor(paramJComponent.getBackground());
    paramGraphics.drawLine(0, 1, width, 1);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return new Dimension(0, 2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifPopupMenuSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */