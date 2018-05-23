package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;

public class MatteBorder
  extends EmptyBorder
{
  protected Color color;
  protected Icon tileIcon;
  
  public MatteBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    color = paramColor;
  }
  
  public MatteBorder(Insets paramInsets, Color paramColor)
  {
    super(paramInsets);
    color = paramColor;
  }
  
  public MatteBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Icon paramIcon)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    tileIcon = paramIcon;
  }
  
  public MatteBorder(Insets paramInsets, Icon paramIcon)
  {
    super(paramInsets);
    tileIcon = paramIcon;
  }
  
  public MatteBorder(Icon paramIcon)
  {
    this(-1, -1, -1, -1, paramIcon);
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Insets localInsets = getBorderInsets(paramComponent);
    Color localColor = paramGraphics.getColor();
    paramGraphics.translate(paramInt1, paramInt2);
    if (tileIcon != null) {
      color = (tileIcon.getIconWidth() == -1 ? Color.gray : null);
    }
    if (color != null)
    {
      paramGraphics.setColor(color);
      paramGraphics.fillRect(0, 0, paramInt3 - right, top);
      paramGraphics.fillRect(0, top, left, paramInt4 - top);
      paramGraphics.fillRect(left, paramInt4 - bottom, paramInt3 - left, bottom);
      paramGraphics.fillRect(paramInt3 - right, 0, right, paramInt4 - bottom);
    }
    else if (tileIcon != null)
    {
      int i = tileIcon.getIconWidth();
      int j = tileIcon.getIconHeight();
      paintEdge(paramComponent, paramGraphics, 0, 0, paramInt3 - right, top, i, j);
      paintEdge(paramComponent, paramGraphics, 0, top, left, paramInt4 - top, i, j);
      paintEdge(paramComponent, paramGraphics, left, paramInt4 - bottom, paramInt3 - left, bottom, i, j);
      paintEdge(paramComponent, paramGraphics, paramInt3 - right, 0, right, paramInt4 - bottom, i, j);
    }
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(localColor);
  }
  
  private void paintEdge(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    paramGraphics = paramGraphics.create(paramInt1, paramInt2, paramInt3, paramInt4);
    int i = -(paramInt2 % paramInt6);
    paramInt1 = -(paramInt1 % paramInt5);
    while (paramInt1 < paramInt3)
    {
      paramInt2 = i;
      while (paramInt2 < paramInt4)
      {
        tileIcon.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
        paramInt2 += paramInt6;
      }
      paramInt1 += paramInt5;
    }
    paramGraphics.dispose();
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    return computeInsets(paramInsets);
  }
  
  public Insets getBorderInsets()
  {
    return computeInsets(new Insets(0, 0, 0, 0));
  }
  
  private Insets computeInsets(Insets paramInsets)
  {
    if ((tileIcon != null) && (top == -1) && (bottom == -1) && (left == -1) && (right == -1))
    {
      int i = tileIcon.getIconWidth();
      int j = tileIcon.getIconHeight();
      top = j;
      right = i;
      bottom = j;
      left = i;
    }
    else
    {
      left = left;
      top = top;
      right = right;
      bottom = bottom;
    }
    return paramInsets;
  }
  
  public Color getMatteColor()
  {
    return color;
  }
  
  public Icon getTileIcon()
  {
    return tileIcon;
  }
  
  public boolean isBorderOpaque()
  {
    return color != null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\border\MatteBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */