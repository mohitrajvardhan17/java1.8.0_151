package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class WindowsTabbedPaneUI
  extends BasicTabbedPaneUI
{
  private static Set<KeyStroke> managingFocusForwardTraversalKeys;
  private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
  private boolean contentOpaque = true;
  
  public WindowsTabbedPaneUI() {}
  
  protected void installDefaults()
  {
    super.installDefaults();
    contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
    if (managingFocusForwardTraversalKeys == null)
    {
      managingFocusForwardTraversalKeys = new HashSet();
      managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
    }
    tabPane.setFocusTraversalKeys(0, managingFocusForwardTraversalKeys);
    if (managingFocusBackwardTraversalKeys == null)
    {
      managingFocusBackwardTraversalKeys = new HashSet();
      managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
    }
    tabPane.setFocusTraversalKeys(1, managingFocusBackwardTraversalKeys);
  }
  
  protected void uninstallDefaults()
  {
    tabPane.setFocusTraversalKeys(0, null);
    tabPane.setFocusTraversalKeys(1, null);
    super.uninstallDefaults();
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsTabbedPaneUI();
  }
  
  protected void setRolloverTab(int paramInt)
  {
    if (XPStyle.getXP() != null)
    {
      int i = getRolloverTab();
      super.setRolloverTab(paramInt);
      Rectangle localRectangle1 = null;
      Rectangle localRectangle2 = null;
      if ((i >= 0) && (i < tabPane.getTabCount())) {
        localRectangle1 = getTabBounds(tabPane, i);
      }
      if (paramInt >= 0) {
        localRectangle2 = getTabBounds(tabPane, paramInt);
      }
      if (localRectangle1 != null)
      {
        if (localRectangle2 != null) {
          tabPane.repaint(localRectangle1.union(localRectangle2));
        } else {
          tabPane.repaint(localRectangle1);
        }
      }
      else if (localRectangle2 != null) {
        tabPane.repaint(localRectangle2);
      }
    }
  }
  
  protected void paintContentBorder(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if ((localXPStyle != null) && ((contentOpaque) || (tabPane.isOpaque())))
    {
      XPStyle.Skin localSkin = localXPStyle.getSkin(tabPane, TMSchema.Part.TABP_PANE);
      if (localSkin != null)
      {
        Insets localInsets1 = tabPane.getInsets();
        Insets localInsets2 = UIManager.getInsets("TabbedPane.tabAreaInsets");
        int i = left;
        int j = top;
        int k = tabPane.getWidth() - right - left;
        int m = tabPane.getHeight() - top - bottom;
        int n;
        if ((paramInt1 == 2) || (paramInt1 == 4))
        {
          n = calculateTabAreaWidth(paramInt1, runCount, maxTabWidth);
          if (paramInt1 == 2) {
            i += n - bottom;
          }
          k -= n - bottom;
        }
        else
        {
          n = calculateTabAreaHeight(paramInt1, runCount, maxTabHeight);
          if (paramInt1 == 1) {
            j += n - bottom;
          }
          m -= n - bottom;
        }
        paintRotatedSkin(paramGraphics, localSkin, paramInt1, i, j, k, m, null);
        return;
      }
    }
    super.paintContentBorder(paramGraphics, paramInt1, paramInt2);
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    if (XPStyle.getXP() == null) {
      super.paintTabBackground(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean);
    }
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      int i = tabPane.getTabCount();
      int j = getRunForTab(i, paramInt2);
      TMSchema.Part localPart;
      if (tabRuns[j] == paramInt2)
      {
        localPart = TMSchema.Part.TABP_TABITEMLEFTEDGE;
      }
      else if ((i > 1) && (lastTabInRun(i, j) == paramInt2))
      {
        localPart = TMSchema.Part.TABP_TABITEMRIGHTEDGE;
        if (paramBoolean) {
          if ((paramInt1 == 1) || (paramInt1 == 3)) {
            paramInt5++;
          } else {
            paramInt6++;
          }
        }
      }
      else
      {
        localPart = TMSchema.Part.TABP_TABITEM;
      }
      TMSchema.State localState = TMSchema.State.NORMAL;
      if (paramBoolean) {
        localState = TMSchema.State.SELECTED;
      } else if (paramInt2 == getRolloverTab()) {
        localState = TMSchema.State.HOT;
      }
      paintRotatedSkin(paramGraphics, localXPStyle.getSkin(tabPane, localPart), paramInt1, paramInt3, paramInt4, paramInt5, paramInt6, localState);
    }
    else
    {
      super.paintTabBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean);
    }
  }
  
  private void paintRotatedSkin(Graphics paramGraphics, XPStyle.Skin paramSkin, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, TMSchema.State paramState)
  {
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
    localGraphics2D.translate(paramInt2, paramInt3);
    switch (paramInt1)
    {
    case 4: 
      localGraphics2D.translate(paramInt4, 0);
      localGraphics2D.rotate(Math.toRadians(90.0D));
      paramSkin.paintSkin(localGraphics2D, 0, 0, paramInt5, paramInt4, paramState);
      break;
    case 2: 
      localGraphics2D.scale(-1.0D, 1.0D);
      localGraphics2D.rotate(Math.toRadians(90.0D));
      paramSkin.paintSkin(localGraphics2D, 0, 0, paramInt5, paramInt4, paramState);
      break;
    case 3: 
      localGraphics2D.translate(0, paramInt5);
      localGraphics2D.scale(-1.0D, 1.0D);
      localGraphics2D.rotate(Math.toRadians(180.0D));
      paramSkin.paintSkin(localGraphics2D, 0, 0, paramInt4, paramInt5, paramState);
      break;
    case 1: 
    default: 
      paramSkin.paintSkin(localGraphics2D, 0, 0, paramInt4, paramInt5, paramState);
    }
    localGraphics2D.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */