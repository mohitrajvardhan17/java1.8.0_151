package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MotifTreeCellRenderer
  extends DefaultTreeCellRenderer
{
  static final int LEAF_SIZE = 13;
  static final Icon LEAF_ICON = new IconUIResource(new TreeLeafIcon());
  
  public MotifTreeCellRenderer() {}
  
  public static Icon loadLeafIcon()
  {
    return LEAF_ICON;
  }
  
  public static class TreeLeafIcon
    implements Icon, Serializable
  {
    Color bg = UIManager.getColor("Tree.iconBackground");
    Color shadow = UIManager.getColor("Tree.iconShadow");
    Color highlight = UIManager.getColor("Tree.iconHighlight");
    
    public TreeLeafIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.setColor(bg);
      paramInt2 -= 3;
      paramGraphics.fillRect(paramInt1 + 4, paramInt2 + 7, 5, 5);
      paramGraphics.drawLine(paramInt1 + 6, paramInt2 + 6, paramInt1 + 6, paramInt2 + 6);
      paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 9, paramInt1 + 3, paramInt2 + 9);
      paramGraphics.drawLine(paramInt1 + 6, paramInt2 + 12, paramInt1 + 6, paramInt2 + 12);
      paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 9, paramInt1 + 9, paramInt2 + 9);
      paramGraphics.setColor(highlight);
      paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 9, paramInt1 + 5, paramInt2 + 6);
      paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 10, paramInt1 + 5, paramInt2 + 12);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(paramInt1 + 6, paramInt2 + 13, paramInt1 + 10, paramInt2 + 9);
      paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 8, paramInt1 + 7, paramInt2 + 6);
    }
    
    public int getIconWidth()
    {
      return 13;
    }
    
    public int getIconHeight()
    {
      return 13;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifTreeCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */