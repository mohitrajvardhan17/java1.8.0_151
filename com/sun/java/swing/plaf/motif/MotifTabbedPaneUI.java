package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class MotifTabbedPaneUI
  extends BasicTabbedPaneUI
{
  protected Color unselectedTabBackground;
  protected Color unselectedTabForeground;
  protected Color unselectedTabShadow;
  protected Color unselectedTabHighlight;
  
  public MotifTabbedPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifTabbedPaneUI();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    unselectedTabBackground = UIManager.getColor("TabbedPane.unselectedTabBackground");
    unselectedTabForeground = UIManager.getColor("TabbedPane.unselectedTabForeground");
    unselectedTabShadow = UIManager.getColor("TabbedPane.unselectedTabShadow");
    unselectedTabHighlight = UIManager.getColor("TabbedPane.unselectedTabHighlight");
  }
  
  protected void uninstallDefaults()
  {
    super.uninstallDefaults();
    unselectedTabBackground = null;
    unselectedTabForeground = null;
    unselectedTabShadow = null;
    unselectedTabHighlight = null;
  }
  
  protected void paintContentBorderTopEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(lightHighlight);
    if ((paramInt1 != 1) || (paramInt2 < 0) || (x < paramInt3) || (x > paramInt3 + paramInt5))
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
    }
    else
    {
      paramGraphics.drawLine(paramInt3, paramInt4, x - 1, paramInt4);
      if (x + width < paramInt3 + paramInt5 - 2) {
        paramGraphics.drawLine(x + width, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      }
    }
  }
  
  protected void paintContentBorderBottomEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(shadow);
    if ((paramInt1 != 3) || (paramInt2 < 0) || (x < paramInt3) || (x > paramInt3 + paramInt5))
    {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
    else
    {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 1, x - 1, paramInt4 + paramInt6 - 1);
      if (x + width < paramInt3 + paramInt5 - 2) {
        paramGraphics.drawLine(x + width, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 1);
      }
    }
  }
  
  protected void paintContentBorderRightEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(shadow);
    if ((paramInt1 != 4) || (paramInt2 < 0) || (y < paramInt4) || (y > paramInt4 + paramInt6))
    {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
    else
    {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 1, paramInt3 + paramInt5 - 1, y - 1);
      if (y + height < paramInt4 + paramInt6 - 2) {
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, y + height, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
      }
    }
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    paramGraphics.setColor(paramBoolean ? tabPane.getBackgroundAt(paramInt2) : unselectedTabBackground);
    switch (paramInt1)
    {
    case 2: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 1, paramInt6 - 2);
      break;
    case 4: 
      paramGraphics.fillRect(paramInt3, paramInt4 + 1, paramInt5 - 1, paramInt6 - 2);
      break;
    case 3: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4, paramInt5 - 2, paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 3, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 4, paramInt4 + paramInt6 - 2);
      break;
    case 1: 
    default: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 3, paramInt5 - 2, paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + 2, paramInt3 + paramInt5 - 3, paramInt4 + 2);
      paramGraphics.drawLine(paramInt3 + 3, paramInt4 + 1, paramInt3 + paramInt5 - 4, paramInt4 + 1);
    }
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    paramGraphics.setColor(paramBoolean ? lightHighlight : unselectedTabHighlight);
    switch (paramInt1)
    {
    case 2: 
      paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 2);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + 2, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + 3, paramInt4, paramInt3 + paramInt5 - 1, paramInt4);
      paramGraphics.setColor(paramBoolean ? shadow : unselectedTabShadow);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 3, paramInt3 + 1, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + 2, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + 3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
      break;
    case 4: 
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
      paramGraphics.setColor(paramBoolean ? shadow : unselectedTabShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
      break;
    case 3: 
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 3, paramInt3 + 1, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + 2, paramInt4 + paramInt6 - 1);
      paramGraphics.setColor(paramBoolean ? shadow : unselectedTabShadow);
      paramGraphics.drawLine(paramInt3 + 3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 4, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
      break;
    case 1: 
    default: 
      paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 2);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + 2, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + 3, paramInt4, paramInt3 + paramInt5 - 4, paramInt4);
      paramGraphics.setColor(paramBoolean ? shadow : unselectedTabShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
  }
  
  protected void paintFocusIndicator(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2, boolean paramBoolean)
  {
    Rectangle localRectangle = paramArrayOfRectangle[paramInt2];
    if ((tabPane.hasFocus()) && (paramBoolean))
    {
      paramGraphics.setColor(focus);
      int i;
      int j;
      int k;
      int m;
      switch (paramInt1)
      {
      case 2: 
        i = x + 3;
        j = y + 3;
        k = width - 6;
        m = height - 7;
        break;
      case 4: 
        i = x + 2;
        j = y + 3;
        k = width - 6;
        m = height - 7;
        break;
      case 3: 
        i = x + 3;
        j = y + 2;
        k = width - 7;
        m = height - 6;
        break;
      case 1: 
      default: 
        i = x + 3;
        j = y + 3;
        k = width - 7;
        m = height - 6;
      }
      paramGraphics.drawRect(i, j, k, m);
    }
  }
  
  protected int getTabRunIndent(int paramInt1, int paramInt2)
  {
    return paramInt2 * 3;
  }
  
  protected int getTabRunOverlay(int paramInt)
  {
    tabRunOverlay = ((paramInt == 2) || (paramInt == 4) ? (int)Math.round(maxTabWidth * 0.1D) : (int)Math.round(maxTabHeight * 0.22D));
    switch (paramInt)
    {
    case 2: 
      if (tabRunOverlay > tabInsets.right - 2) {
        tabRunOverlay = (tabInsets.right - 2);
      }
      break;
    case 4: 
      if (tabRunOverlay > tabInsets.left - 2) {
        tabRunOverlay = (tabInsets.left - 2);
      }
      break;
    case 1: 
      if (tabRunOverlay > tabInsets.bottom - 2) {
        tabRunOverlay = (tabInsets.bottom - 2);
      }
      break;
    case 3: 
      if (tabRunOverlay > tabInsets.top - 2) {
        tabRunOverlay = (tabInsets.top - 2);
      }
      break;
    }
    return tabRunOverlay;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */