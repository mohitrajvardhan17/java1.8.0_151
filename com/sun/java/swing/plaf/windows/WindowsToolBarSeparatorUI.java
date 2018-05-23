package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar.Separator;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

public class WindowsToolBarSeparatorUI
  extends BasicToolBarSeparatorUI
{
  public WindowsToolBarSeparatorUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsToolBarSeparatorUI();
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = ((JToolBar.Separator)paramJComponent).getSeparatorSize();
    if (localDimension != null)
    {
      localDimension = localDimension.getSize();
    }
    else
    {
      localDimension = new Dimension(6, 6);
      XPStyle localXPStyle = XPStyle.getXP();
      if (localXPStyle != null)
      {
        int i = ((JSeparator)paramJComponent).getOrientation() == 1 ? 1 : 0;
        TMSchema.Part localPart = i != 0 ? TMSchema.Part.TP_SEPARATOR : TMSchema.Part.TP_SEPARATORVERT;
        XPStyle.Skin localSkin = localXPStyle.getSkin(paramJComponent, localPart);
        width = localSkin.getWidth();
        height = localSkin.getHeight();
      }
      if (((JSeparator)paramJComponent).getOrientation() == 1) {
        height = 0;
      } else {
        width = 0;
      }
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Dimension localDimension = getPreferredSize(paramJComponent);
    if (((JSeparator)paramJComponent).getOrientation() == 1) {
      return new Dimension(width, 32767);
    }
    return new Dimension(32767, height);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    int i = ((JSeparator)paramJComponent).getOrientation() == 1 ? 1 : 0;
    Dimension localDimension = paramJComponent.getSize();
    XPStyle localXPStyle = XPStyle.getXP();
    Object localObject1;
    Object localObject2;
    int m;
    if (localXPStyle != null)
    {
      localObject1 = i != 0 ? TMSchema.Part.TP_SEPARATOR : TMSchema.Part.TP_SEPARATORVERT;
      localObject2 = localXPStyle.getSkin(paramJComponent, (TMSchema.Part)localObject1);
      int j = i != 0 ? (width - ((XPStyle.Skin)localObject2).getWidth()) / 2 : 0;
      int k = i != 0 ? 0 : (height - ((XPStyle.Skin)localObject2).getHeight()) / 2;
      m = i != 0 ? ((XPStyle.Skin)localObject2).getWidth() : width;
      int n = i != 0 ? height : ((XPStyle.Skin)localObject2).getHeight();
      ((XPStyle.Skin)localObject2).paintSkin(paramGraphics, j, k, m, n, null);
    }
    else
    {
      localObject1 = paramGraphics.getColor();
      localObject2 = UIManager.getLookAndFeelDefaults();
      Color localColor1 = ((UIDefaults)localObject2).getColor("ToolBar.shadow");
      Color localColor2 = ((UIDefaults)localObject2).getColor("ToolBar.highlight");
      if (i != 0)
      {
        m = width / 2 - 1;
        paramGraphics.setColor(localColor1);
        paramGraphics.drawLine(m, 2, m, height - 2);
        paramGraphics.setColor(localColor2);
        paramGraphics.drawLine(m + 1, 2, m + 1, height - 2);
      }
      else
      {
        m = height / 2 - 1;
        paramGraphics.setColor(localColor1);
        paramGraphics.drawLine(2, m, width - 2, m);
        paramGraphics.setColor(localColor2);
        paramGraphics.drawLine(2, m + 1, width - 2, m + 1);
      }
      paramGraphics.setColor((Color)localObject1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsToolBarSeparatorUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */