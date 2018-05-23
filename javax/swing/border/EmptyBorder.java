package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class EmptyBorder
  extends AbstractBorder
  implements Serializable
{
  protected int left;
  protected int right;
  protected int top;
  protected int bottom;
  
  public EmptyBorder(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    top = paramInt1;
    right = paramInt4;
    bottom = paramInt3;
    left = paramInt2;
  }
  
  @ConstructorProperties({"borderInsets"})
  public EmptyBorder(Insets paramInsets)
  {
    top = top;
    right = right;
    bottom = bottom;
    left = left;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    left = left;
    top = top;
    right = right;
    bottom = bottom;
    return paramInsets;
  }
  
  public Insets getBorderInsets()
  {
    return new Insets(top, left, bottom, right);
  }
  
  public boolean isBorderOpaque()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\border\EmptyBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */