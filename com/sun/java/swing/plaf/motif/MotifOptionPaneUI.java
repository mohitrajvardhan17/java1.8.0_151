package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonAreaLayout;

public class MotifOptionPaneUI
  extends BasicOptionPaneUI
{
  public MotifOptionPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifOptionPaneUI();
  }
  
  protected Container createButtonArea()
  {
    Container localContainer = super.createButtonArea();
    if ((localContainer != null) && ((localContainer.getLayout() instanceof BasicOptionPaneUI.ButtonAreaLayout))) {
      ((BasicOptionPaneUI.ButtonAreaLayout)localContainer.getLayout()).setCentersChildren(false);
    }
    return localContainer;
  }
  
  public Dimension getMinimumOptionPaneSize()
  {
    return null;
  }
  
  protected Container createSeparator()
  {
    new JPanel()
    {
      public Dimension getPreferredSize()
      {
        return new Dimension(10, 2);
      }
      
      public void paint(Graphics paramAnonymousGraphics)
      {
        int i = getWidth();
        paramAnonymousGraphics.setColor(Color.darkGray);
        paramAnonymousGraphics.drawLine(0, 0, i, 0);
        paramAnonymousGraphics.setColor(Color.white);
        paramAnonymousGraphics.drawLine(0, 1, i, 1);
      }
    };
  }
  
  protected void addIcon(Container paramContainer)
  {
    Icon localIcon = getIcon();
    if (localIcon != null)
    {
      JLabel localJLabel = new JLabel(localIcon);
      localJLabel.setVerticalAlignment(0);
      paramContainer.add(localJLabel, "West");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifOptionPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */