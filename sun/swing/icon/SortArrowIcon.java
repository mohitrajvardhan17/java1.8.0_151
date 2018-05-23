package sun.swing.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class SortArrowIcon
  implements Icon, UIResource, Serializable
{
  private static final int ARROW_HEIGHT = 5;
  private static final int X_PADDING = 7;
  private boolean ascending;
  private Color color;
  private String colorKey;
  
  public SortArrowIcon(boolean paramBoolean, Color paramColor)
  {
    ascending = paramBoolean;
    color = paramColor;
    if (paramColor == null) {
      throw new IllegalArgumentException();
    }
  }
  
  public SortArrowIcon(boolean paramBoolean, String paramString)
  {
    ascending = paramBoolean;
    colorKey = paramString;
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
  }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    paramGraphics.setColor(getColor());
    int i = 7 + paramInt1 + 2;
    int j;
    int k;
    if (ascending)
    {
      j = paramInt2;
      paramGraphics.fillRect(i, j, 1, 1);
      for (k = 1; k < 5; k++) {
        paramGraphics.fillRect(i - k, j + k, k + k + 1, 1);
      }
    }
    else
    {
      j = paramInt2 + 5 - 1;
      paramGraphics.fillRect(i, j, 1, 1);
      for (k = 1; k < 5; k++) {
        paramGraphics.fillRect(i - k, j - k, k + k + 1, 1);
      }
    }
  }
  
  public int getIconWidth()
  {
    return 17;
  }
  
  public int getIconHeight()
  {
    return 7;
  }
  
  private Color getColor()
  {
    if (color != null) {
      return color;
    }
    return UIManager.getColor(colorKey);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\icon\SortArrowIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */