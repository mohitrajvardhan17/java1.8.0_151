package javax.swing.plaf;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;

public class IconUIResource
  implements Icon, UIResource, Serializable
{
  private Icon delegate;
  
  public IconUIResource(Icon paramIcon)
  {
    if (paramIcon == null) {
      throw new IllegalArgumentException("null delegate icon argument");
    }
    delegate = paramIcon;
  }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    delegate.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
  }
  
  public int getIconWidth()
  {
    return delegate.getIconWidth();
  }
  
  public int getIconHeight()
  {
    return delegate.getIconHeight();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\IconUIResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */