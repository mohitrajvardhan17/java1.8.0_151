package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

public abstract interface Border
{
  public abstract void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract Insets getBorderInsets(Component paramComponent);
  
  public abstract boolean isBorderOpaque();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\border\Border.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */