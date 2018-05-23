package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellRenderer;

public class MotifTreeUI
  extends BasicTreeUI
{
  static final int HALF_SIZE = 7;
  static final int SIZE = 14;
  
  public MotifTreeUI() {}
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
  }
  
  protected void paintVerticalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3)
  {
    if (tree.getComponentOrientation().isLeftToRight()) {
      paramGraphics.fillRect(paramInt1, paramInt2, 2, paramInt3 - paramInt2 + 2);
    } else {
      paramGraphics.fillRect(paramInt1 - 1, paramInt2, 2, paramInt3 - paramInt2 + 2);
    }
  }
  
  protected void paintHorizontalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3)
  {
    paramGraphics.fillRect(paramInt2, paramInt1, paramInt3 - paramInt2 + 1, 2);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifTreeUI();
  }
  
  public TreeCellRenderer createDefaultCellRenderer()
  {
    return new MotifTreeCellRenderer();
  }
  
  public static class MotifCollapsedIcon
    extends MotifTreeUI.MotifExpandedIcon
  {
    public MotifCollapsedIcon() {}
    
    public static Icon createCollapsedIcon()
    {
      return new MotifCollapsedIcon();
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      super.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
      paramGraphics.drawLine(paramInt1 + 7 - 1, paramInt2 + 3, paramInt1 + 7 - 1, paramInt2 + 10);
      paramGraphics.drawLine(paramInt1 + 7, paramInt2 + 3, paramInt1 + 7, paramInt2 + 10);
    }
  }
  
  public static class MotifExpandedIcon
    implements Icon, Serializable
  {
    static Color bg;
    static Color fg;
    static Color highlight;
    static Color shadow;
    
    public MotifExpandedIcon()
    {
      bg = UIManager.getColor("Tree.iconBackground");
      fg = UIManager.getColor("Tree.iconForeground");
      highlight = UIManager.getColor("Tree.iconHighlight");
      shadow = UIManager.getColor("Tree.iconShadow");
    }
    
    public static Icon createExpandedIcon()
    {
      return new MotifExpandedIcon();
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.setColor(highlight);
      paramGraphics.drawLine(paramInt1, paramInt2, paramInt1 + 14 - 1, paramInt2);
      paramGraphics.drawLine(paramInt1, paramInt2 + 1, paramInt1, paramInt2 + 14 - 1);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(paramInt1 + 14 - 1, paramInt2 + 1, paramInt1 + 14 - 1, paramInt2 + 14 - 1);
      paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 14 - 1, paramInt1 + 14 - 1, paramInt2 + 14 - 1);
      paramGraphics.setColor(bg);
      paramGraphics.fillRect(paramInt1 + 1, paramInt2 + 1, 12, 12);
      paramGraphics.setColor(fg);
      paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 7 - 1, paramInt1 + 14 - 4, paramInt2 + 7 - 1);
      paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 7, paramInt1 + 14 - 4, paramInt2 + 7);
    }
    
    public int getIconWidth()
    {
      return 14;
    }
    
    public int getIconHeight()
    {
      return 14;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */