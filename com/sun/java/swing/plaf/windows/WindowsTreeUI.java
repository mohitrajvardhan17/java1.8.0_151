package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class WindowsTreeUI
  extends BasicTreeUI
{
  protected static final int HALF_SIZE = 4;
  protected static final int SIZE = 9;
  
  public WindowsTreeUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsTreeUI();
  }
  
  protected void ensureRowsAreVisible(int paramInt1, int paramInt2)
  {
    if ((tree != null) && (paramInt1 >= 0) && (paramInt2 < getRowCount(tree)))
    {
      Rectangle localRectangle1 = tree.getVisibleRect();
      Rectangle localRectangle2;
      if (paramInt1 == paramInt2)
      {
        localRectangle2 = getPathBounds(tree, getPathForRow(tree, paramInt1));
        if (localRectangle2 != null)
        {
          x = x;
          width = width;
          tree.scrollRectToVisible(localRectangle2);
        }
      }
      else
      {
        localRectangle2 = getPathBounds(tree, getPathForRow(tree, paramInt1));
        if (localRectangle2 != null)
        {
          Rectangle localRectangle3 = localRectangle2;
          int i = y;
          int j = i + height;
          for (int k = paramInt1 + 1; k <= paramInt2; k++)
          {
            localRectangle3 = getPathBounds(tree, getPathForRow(tree, k));
            if ((localRectangle3 != null) && (y + height > j)) {
              k = paramInt2;
            }
          }
          if (localRectangle3 == null) {
            return;
          }
          tree.scrollRectToVisible(new Rectangle(x, i, 1, y + height - i));
        }
      }
    }
  }
  
  protected TreeCellRenderer createDefaultCellRenderer()
  {
    return new WindowsTreeCellRenderer();
  }
  
  public static class CollapsedIcon
    extends WindowsTreeUI.ExpandedIcon
  {
    public CollapsedIcon() {}
    
    public static Icon createCollapsedIcon()
    {
      return new CollapsedIcon();
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      XPStyle.Skin localSkin = getSkin(paramComponent);
      if (localSkin != null)
      {
        localSkin.paintSkin(paramGraphics, paramInt1, paramInt2, TMSchema.State.CLOSED);
      }
      else
      {
        super.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
        paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 2, paramInt1 + 4, paramInt2 + 6);
      }
    }
  }
  
  public static class ExpandedIcon
    implements Icon, Serializable
  {
    public ExpandedIcon() {}
    
    public static Icon createExpandedIcon()
    {
      return new ExpandedIcon();
    }
    
    XPStyle.Skin getSkin(Component paramComponent)
    {
      XPStyle localXPStyle = XPStyle.getXP();
      return localXPStyle != null ? localXPStyle.getSkin(paramComponent, TMSchema.Part.TVP_GLYPH) : null;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      XPStyle.Skin localSkin = getSkin(paramComponent);
      if (localSkin != null)
      {
        localSkin.paintSkin(paramGraphics, paramInt1, paramInt2, TMSchema.State.OPENED);
        return;
      }
      Color localColor = paramComponent.getBackground();
      if (localColor != null) {
        paramGraphics.setColor(localColor);
      } else {
        paramGraphics.setColor(Color.white);
      }
      paramGraphics.fillRect(paramInt1, paramInt2, 8, 8);
      paramGraphics.setColor(Color.gray);
      paramGraphics.drawRect(paramInt1, paramInt2, 8, 8);
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 4, paramInt1 + 6, paramInt2 + 4);
    }
    
    public int getIconWidth()
    {
      XPStyle.Skin localSkin = getSkin(null);
      return localSkin != null ? localSkin.getWidth() : 9;
    }
    
    public int getIconHeight()
    {
      XPStyle.Skin localSkin = getSkin(null);
      return localSkin != null ? localSkin.getHeight() : 9;
    }
  }
  
  public class WindowsTreeCellRenderer
    extends DefaultTreeCellRenderer
  {
    public WindowsTreeCellRenderer() {}
    
    public Component getTreeCellRendererComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4)
    {
      super.getTreeCellRendererComponent(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, paramBoolean4);
      if (!paramJTree.isEnabled())
      {
        setEnabled(false);
        if (paramBoolean3) {
          setDisabledIcon(getLeafIcon());
        } else if (paramBoolean1) {
          setDisabledIcon(getOpenIcon());
        } else {
          setDisabledIcon(getClosedIcon());
        }
      }
      else
      {
        setEnabled(true);
        if (paramBoolean3) {
          setIcon(getLeafIcon());
        } else if (paramBoolean1) {
          setIcon(getOpenIcon());
        } else {
          setIcon(getClosedIcon());
        }
      }
      return this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */