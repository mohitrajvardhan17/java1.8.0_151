package sun.swing.plaf.windows;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class ClassicSortArrowIcon
  implements Icon, UIResource, Serializable
{
  private static final int X_OFFSET = 9;
  private boolean ascending;
  
  public ClassicSortArrowIcon(boolean paramBoolean)
  {
    ascending = paramBoolean;
  }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    
    if (ascending)
    {
      paramGraphics.setColor(UIManager.getColor("Table.sortIconHighlight"));
      drawSide(paramGraphics, paramInt1 + 3, paramInt2, -1);
      paramGraphics.setColor(UIManager.getColor("Table.sortIconLight"));
      drawSide(paramGraphics, paramInt1 + 4, paramInt2, 1);
      paramGraphics.fillRect(paramInt1 + 1, paramInt2 + 6, 6, 1);
    }
    else
    {
      paramGraphics.setColor(UIManager.getColor("Table.sortIconHighlight"));
      drawSide(paramGraphics, paramInt1 + 3, paramInt2 + 6, -1);
      paramGraphics.fillRect(paramInt1 + 1, paramInt2, 6, 1);
      paramGraphics.setColor(UIManager.getColor("Table.sortIconLight"));
      drawSide(paramGraphics, paramInt1 + 4, paramInt2 + 6, 1);
    }
  }
  
  private void drawSide(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 2;
    if (ascending)
    {
      paramGraphics.fillRect(paramInt1, paramInt2, 1, 2);
      paramInt2++;
    }
    else
    {
      paramGraphics.fillRect(paramInt1, --paramInt2, 1, 2);
      i = -2;
      paramInt2 -= 2;
    }
    paramInt1 += paramInt3;
    for (int j = 0; j < 2; j++)
    {
      paramGraphics.fillRect(paramInt1, paramInt2, 1, 3);
      paramInt1 += paramInt3;
      paramInt2 += i;
    }
    if (!ascending) {
      paramInt2++;
    }
    paramGraphics.fillRect(paramInt1, paramInt2, 1, 2);
  }
  
  public int getIconWidth()
  {
    return 17;
  }
  
  public int getIconHeight()
  {
    return 9;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\plaf\windows\ClassicSortArrowIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */