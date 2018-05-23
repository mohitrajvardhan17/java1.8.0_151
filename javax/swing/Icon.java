package javax.swing;

import java.awt.Component;
import java.awt.Graphics;

public abstract interface Icon
{
  public abstract void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2);
  
  public abstract int getIconWidth();
  
  public abstract int getIconHeight();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\Icon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */