package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class CompoundBorder
  extends AbstractBorder
{
  protected Border outsideBorder;
  protected Border insideBorder;
  
  public CompoundBorder()
  {
    outsideBorder = null;
    insideBorder = null;
  }
  
  @ConstructorProperties({"outsideBorder", "insideBorder"})
  public CompoundBorder(Border paramBorder1, Border paramBorder2)
  {
    outsideBorder = paramBorder1;
    insideBorder = paramBorder2;
  }
  
  public boolean isBorderOpaque()
  {
    return ((outsideBorder == null) || (outsideBorder.isBorderOpaque())) && ((insideBorder == null) || (insideBorder.isBorderOpaque()));
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt1;
    int j = paramInt2;
    int k = paramInt3;
    int m = paramInt4;
    if (outsideBorder != null)
    {
      outsideBorder.paintBorder(paramComponent, paramGraphics, i, j, k, m);
      Insets localInsets = outsideBorder.getBorderInsets(paramComponent);
      i += left;
      j += top;
      k = k - right - left;
      m = m - bottom - top;
    }
    if (insideBorder != null) {
      insideBorder.paintBorder(paramComponent, paramGraphics, i, j, k, m);
    }
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    top = (left = right = bottom = 0);
    Insets localInsets;
    if (outsideBorder != null)
    {
      localInsets = outsideBorder.getBorderInsets(paramComponent);
      top += top;
      left += left;
      right += right;
      bottom += bottom;
    }
    if (insideBorder != null)
    {
      localInsets = insideBorder.getBorderInsets(paramComponent);
      top += top;
      left += left;
      right += right;
      bottom += bottom;
    }
    return paramInsets;
  }
  
  public Border getOutsideBorder()
  {
    return outsideBorder;
  }
  
  public Border getInsideBorder()
  {
    return insideBorder;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\border\CompoundBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */