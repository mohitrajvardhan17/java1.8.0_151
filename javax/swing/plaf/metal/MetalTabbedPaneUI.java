package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI.TabbedPaneLayout;

public class MetalTabbedPaneUI
  extends BasicTabbedPaneUI
{
  protected int minTabWidth = 40;
  private Color unselectedBackground;
  protected Color tabAreaBackground;
  protected Color selectColor;
  protected Color selectHighlight;
  private boolean tabsOpaque = true;
  private boolean ocean;
  private Color oceanSelectedBorderColor;
  
  public MetalTabbedPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalTabbedPaneUI();
  }
  
  protected LayoutManager createLayoutManager()
  {
    if (tabPane.getTabLayoutPolicy() == 1) {
      return super.createLayoutManager();
    }
    return new TabbedPaneLayout();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    tabAreaBackground = UIManager.getColor("TabbedPane.tabAreaBackground");
    selectColor = UIManager.getColor("TabbedPane.selected");
    selectHighlight = UIManager.getColor("TabbedPane.selectHighlight");
    tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
    unselectedBackground = UIManager.getColor("TabbedPane.unselectedBackground");
    ocean = MetalLookAndFeel.usingOcean();
    if (ocean) {
      oceanSelectedBorderColor = UIManager.getColor("TabbedPane.borderHightlightColor");
    }
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    int i = paramInt4 + (paramInt6 - 1);
    int j = paramInt3 + (paramInt5 - 1);
    switch (paramInt1)
    {
    case 2: 
      paintLeftTabBorder(paramInt2, paramGraphics, paramInt3, paramInt4, paramInt5, paramInt6, i, j, paramBoolean);
      break;
    case 3: 
      paintBottomTabBorder(paramInt2, paramGraphics, paramInt3, paramInt4, paramInt5, paramInt6, i, j, paramBoolean);
      break;
    case 4: 
      paintRightTabBorder(paramInt2, paramGraphics, paramInt3, paramInt4, paramInt5, paramInt6, i, j, paramBoolean);
      break;
    case 1: 
    default: 
      paintTopTabBorder(paramInt2, paramGraphics, paramInt3, paramInt4, paramInt5, paramInt6, i, j, paramBoolean);
    }
  }
  
  protected void paintTopTabBorder(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean)
  {
    int i = getRunForTab(tabPane.getTabCount(), paramInt1);
    int j = lastTabInRun(tabPane.getTabCount(), i);
    int k = tabRuns[i];
    boolean bool = MetalUtils.isLeftToRight(tabPane);
    int m = tabPane.getSelectedIndex();
    int n = paramInt5 - 1;
    int i1 = paramInt4 - 1;
    if (shouldFillGap(i, paramInt1, paramInt2, paramInt3))
    {
      paramGraphics.translate(paramInt2, paramInt3);
      if (bool)
      {
        paramGraphics.setColor(getColorForGap(i, paramInt2, paramInt3 + 1));
        paramGraphics.fillRect(1, 0, 5, 3);
        paramGraphics.fillRect(1, 3, 2, 2);
      }
      else
      {
        paramGraphics.setColor(getColorForGap(i, paramInt2 + paramInt4 - 1, paramInt3 + 1));
        paramGraphics.fillRect(i1 - 5, 0, 5, 3);
        paramGraphics.fillRect(i1 - 2, 3, 2, 2);
      }
      paramGraphics.translate(-paramInt2, -paramInt3);
    }
    paramGraphics.translate(paramInt2, paramInt3);
    if ((ocean) && (paramBoolean)) {
      paramGraphics.setColor(oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(darkShadow);
    }
    if (bool)
    {
      paramGraphics.drawLine(1, 5, 6, 0);
      paramGraphics.drawLine(6, 0, i1, 0);
      if (paramInt1 == j) {
        paramGraphics.drawLine(i1, 1, i1, n);
      }
      if ((ocean) && (paramInt1 - 1 == m) && (i == getRunForTab(tabPane.getTabCount(), m))) {
        paramGraphics.setColor(oceanSelectedBorderColor);
      }
      if (paramInt1 != tabRuns[(runCount - 1)])
      {
        if ((ocean) && (paramBoolean))
        {
          paramGraphics.drawLine(0, 6, 0, n);
          paramGraphics.setColor(darkShadow);
          paramGraphics.drawLine(0, 0, 0, 5);
        }
        else
        {
          paramGraphics.drawLine(0, 0, 0, n);
        }
      }
      else {
        paramGraphics.drawLine(0, 6, 0, n);
      }
    }
    else
    {
      paramGraphics.drawLine(i1 - 1, 5, i1 - 6, 0);
      paramGraphics.drawLine(i1 - 6, 0, 0, 0);
      if (paramInt1 == j) {
        paramGraphics.drawLine(0, 1, 0, n);
      }
      if ((ocean) && (paramInt1 - 1 == m) && (i == getRunForTab(tabPane.getTabCount(), m)))
      {
        paramGraphics.setColor(oceanSelectedBorderColor);
        paramGraphics.drawLine(i1, 0, i1, n);
      }
      else if ((ocean) && (paramBoolean))
      {
        paramGraphics.drawLine(i1, 6, i1, n);
        if (paramInt1 != 0)
        {
          paramGraphics.setColor(darkShadow);
          paramGraphics.drawLine(i1, 0, i1, 5);
        }
      }
      else if (paramInt1 != tabRuns[(runCount - 1)])
      {
        paramGraphics.drawLine(i1, 0, i1, n);
      }
      else
      {
        paramGraphics.drawLine(i1, 6, i1, n);
      }
    }
    paramGraphics.setColor(paramBoolean ? selectHighlight : highlight);
    if (bool)
    {
      paramGraphics.drawLine(1, 6, 6, 1);
      paramGraphics.drawLine(6, 1, paramInt1 == j ? i1 - 1 : i1, 1);
      paramGraphics.drawLine(1, 6, 1, n);
      if ((paramInt1 == k) && (paramInt1 != tabRuns[(runCount - 1)]))
      {
        if (tabPane.getSelectedIndex() == tabRuns[(i + 1)]) {
          paramGraphics.setColor(selectHighlight);
        } else {
          paramGraphics.setColor(highlight);
        }
        paramGraphics.drawLine(1, 0, 1, 4);
      }
    }
    else
    {
      paramGraphics.drawLine(i1 - 1, 6, i1 - 6, 1);
      paramGraphics.drawLine(i1 - 6, 1, 1, 1);
      if (paramInt1 == j) {
        paramGraphics.drawLine(1, 1, 1, n);
      } else {
        paramGraphics.drawLine(0, 1, 0, n);
      }
    }
    paramGraphics.translate(-paramInt2, -paramInt3);
  }
  
  protected boolean shouldFillGap(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = false;
    if (!tabsOpaque) {
      return false;
    }
    if (paramInt1 == runCount - 2)
    {
      Rectangle localRectangle1 = getTabBounds(tabPane, tabPane.getTabCount() - 1);
      Rectangle localRectangle2 = getTabBounds(tabPane, paramInt2);
      int i;
      if (MetalUtils.isLeftToRight(tabPane))
      {
        i = x + width - 1;
        if (i > x + 2) {
          return true;
        }
      }
      else
      {
        i = x;
        int j = x + width - 1;
        if (i < j - 2) {
          return true;
        }
      }
    }
    else
    {
      bool = paramInt1 != runCount - 1;
    }
    return bool;
  }
  
  protected Color getColorForGap(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = tabPane.getSelectedIndex();
    int j = tabRuns[(paramInt1 + 1)];
    int k = lastTabInRun(tabPane.getTabCount(), paramInt1 + 1);
    int m = -1;
    for (int n = j; n <= k; n++)
    {
      Rectangle localRectangle = getTabBounds(tabPane, n);
      int i1 = x;
      int i2 = x + width - 1;
      if (MetalUtils.isLeftToRight(tabPane))
      {
        if ((i1 <= paramInt2) && (i2 - 4 > paramInt2)) {
          return i == n ? selectColor : getUnselectedBackgroundAt(n);
        }
      }
      else if ((i1 + 4 < paramInt2) && (i2 >= paramInt2)) {
        return i == n ? selectColor : getUnselectedBackgroundAt(n);
      }
    }
    return tabPane.getBackground();
  }
  
  protected void paintLeftTabBorder(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean)
  {
    int i = tabPane.getTabCount();
    int j = getRunForTab(i, paramInt1);
    int k = lastTabInRun(i, j);
    int m = tabRuns[j];
    paramGraphics.translate(paramInt2, paramInt3);
    int n = paramInt5 - 1;
    int i1 = paramInt4 - 1;
    if ((paramInt1 != m) && (tabsOpaque))
    {
      paramGraphics.setColor(tabPane.getSelectedIndex() == paramInt1 - 1 ? selectColor : getUnselectedBackgroundAt(paramInt1 - 1));
      paramGraphics.fillRect(2, 0, 4, 3);
      paramGraphics.drawLine(2, 3, 2, 3);
    }
    if (ocean) {
      paramGraphics.setColor(paramBoolean ? selectHighlight : MetalLookAndFeel.getWhite());
    } else {
      paramGraphics.setColor(paramBoolean ? selectHighlight : highlight);
    }
    paramGraphics.drawLine(1, 6, 6, 1);
    paramGraphics.drawLine(1, 6, 1, n);
    paramGraphics.drawLine(6, 1, i1, 1);
    if (paramInt1 != m)
    {
      if (tabPane.getSelectedIndex() == paramInt1 - 1) {
        paramGraphics.setColor(selectHighlight);
      } else {
        paramGraphics.setColor(ocean ? MetalLookAndFeel.getWhite() : highlight);
      }
      paramGraphics.drawLine(1, 0, 1, 4);
    }
    if (ocean)
    {
      if (paramBoolean) {
        paramGraphics.setColor(oceanSelectedBorderColor);
      } else {
        paramGraphics.setColor(darkShadow);
      }
    }
    else {
      paramGraphics.setColor(darkShadow);
    }
    paramGraphics.drawLine(1, 5, 6, 0);
    paramGraphics.drawLine(6, 0, i1, 0);
    if (paramInt1 == k) {
      paramGraphics.drawLine(0, n, i1, n);
    }
    if (ocean)
    {
      if (tabPane.getSelectedIndex() == paramInt1 - 1)
      {
        paramGraphics.drawLine(0, 5, 0, n);
        paramGraphics.setColor(oceanSelectedBorderColor);
        paramGraphics.drawLine(0, 0, 0, 5);
      }
      else if (paramBoolean)
      {
        paramGraphics.drawLine(0, 6, 0, n);
        if (paramInt1 != 0)
        {
          paramGraphics.setColor(darkShadow);
          paramGraphics.drawLine(0, 0, 0, 5);
        }
      }
      else if (paramInt1 != m)
      {
        paramGraphics.drawLine(0, 0, 0, n);
      }
      else
      {
        paramGraphics.drawLine(0, 6, 0, n);
      }
    }
    else if (paramInt1 != m) {
      paramGraphics.drawLine(0, 0, 0, n);
    } else {
      paramGraphics.drawLine(0, 6, 0, n);
    }
    paramGraphics.translate(-paramInt2, -paramInt3);
  }
  
  protected void paintBottomTabBorder(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean)
  {
    int i = tabPane.getTabCount();
    int j = getRunForTab(i, paramInt1);
    int k = lastTabInRun(i, j);
    int m = tabRuns[j];
    boolean bool = MetalUtils.isLeftToRight(tabPane);
    int n = paramInt5 - 1;
    int i1 = paramInt4 - 1;
    if (shouldFillGap(j, paramInt1, paramInt2, paramInt3))
    {
      paramGraphics.translate(paramInt2, paramInt3);
      if (bool)
      {
        paramGraphics.setColor(getColorForGap(j, paramInt2, paramInt3));
        paramGraphics.fillRect(1, n - 4, 3, 5);
        paramGraphics.fillRect(4, n - 1, 2, 2);
      }
      else
      {
        paramGraphics.setColor(getColorForGap(j, paramInt2 + paramInt4 - 1, paramInt3));
        paramGraphics.fillRect(i1 - 3, n - 3, 3, 4);
        paramGraphics.fillRect(i1 - 5, n - 1, 2, 2);
        paramGraphics.drawLine(i1 - 1, n - 4, i1 - 1, n - 4);
      }
      paramGraphics.translate(-paramInt2, -paramInt3);
    }
    paramGraphics.translate(paramInt2, paramInt3);
    if ((ocean) && (paramBoolean)) {
      paramGraphics.setColor(oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(darkShadow);
    }
    if (bool)
    {
      paramGraphics.drawLine(1, n - 5, 6, n);
      paramGraphics.drawLine(6, n, i1, n);
      if (paramInt1 == k) {
        paramGraphics.drawLine(i1, 0, i1, n);
      }
      if ((ocean) && (paramBoolean))
      {
        paramGraphics.drawLine(0, 0, 0, n - 6);
        if (((j == 0) && (paramInt1 != 0)) || ((j > 0) && (paramInt1 != tabRuns[(j - 1)])))
        {
          paramGraphics.setColor(darkShadow);
          paramGraphics.drawLine(0, n - 5, 0, n);
        }
      }
      else
      {
        if ((ocean) && (paramInt1 == tabPane.getSelectedIndex() + 1)) {
          paramGraphics.setColor(oceanSelectedBorderColor);
        }
        if (paramInt1 != tabRuns[(runCount - 1)]) {
          paramGraphics.drawLine(0, 0, 0, n);
        } else {
          paramGraphics.drawLine(0, 0, 0, n - 6);
        }
      }
    }
    else
    {
      paramGraphics.drawLine(i1 - 1, n - 5, i1 - 6, n);
      paramGraphics.drawLine(i1 - 6, n, 0, n);
      if (paramInt1 == k) {
        paramGraphics.drawLine(0, 0, 0, n);
      }
      if ((ocean) && (paramInt1 == tabPane.getSelectedIndex() + 1))
      {
        paramGraphics.setColor(oceanSelectedBorderColor);
        paramGraphics.drawLine(i1, 0, i1, n);
      }
      else if ((ocean) && (paramBoolean))
      {
        paramGraphics.drawLine(i1, 0, i1, n - 6);
        if (paramInt1 != m)
        {
          paramGraphics.setColor(darkShadow);
          paramGraphics.drawLine(i1, n - 5, i1, n);
        }
      }
      else if (paramInt1 != tabRuns[(runCount - 1)])
      {
        paramGraphics.drawLine(i1, 0, i1, n);
      }
      else
      {
        paramGraphics.drawLine(i1, 0, i1, n - 6);
      }
    }
    paramGraphics.setColor(paramBoolean ? selectHighlight : highlight);
    if (bool)
    {
      paramGraphics.drawLine(1, n - 6, 6, n - 1);
      paramGraphics.drawLine(1, 0, 1, n - 6);
      if ((paramInt1 == m) && (paramInt1 != tabRuns[(runCount - 1)]))
      {
        if (tabPane.getSelectedIndex() == tabRuns[(j + 1)]) {
          paramGraphics.setColor(selectHighlight);
        } else {
          paramGraphics.setColor(highlight);
        }
        paramGraphics.drawLine(1, n - 4, 1, n);
      }
    }
    else if (paramInt1 == k)
    {
      paramGraphics.drawLine(1, 0, 1, n - 1);
    }
    else
    {
      paramGraphics.drawLine(0, 0, 0, n - 1);
    }
    paramGraphics.translate(-paramInt2, -paramInt3);
  }
  
  protected void paintRightTabBorder(int paramInt1, Graphics paramGraphics, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean)
  {
    int i = tabPane.getTabCount();
    int j = getRunForTab(i, paramInt1);
    int k = lastTabInRun(i, j);
    int m = tabRuns[j];
    paramGraphics.translate(paramInt2, paramInt3);
    int n = paramInt5 - 1;
    int i1 = paramInt4 - 1;
    if ((paramInt1 != m) && (tabsOpaque))
    {
      paramGraphics.setColor(tabPane.getSelectedIndex() == paramInt1 - 1 ? selectColor : getUnselectedBackgroundAt(paramInt1 - 1));
      paramGraphics.fillRect(i1 - 5, 0, 5, 3);
      paramGraphics.fillRect(i1 - 2, 3, 2, 2);
    }
    paramGraphics.setColor(paramBoolean ? selectHighlight : highlight);
    paramGraphics.drawLine(i1 - 6, 1, i1 - 1, 6);
    paramGraphics.drawLine(0, 1, i1 - 6, 1);
    if (!paramBoolean) {
      paramGraphics.drawLine(0, 1, 0, n);
    }
    if ((ocean) && (paramBoolean)) {
      paramGraphics.setColor(oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(darkShadow);
    }
    if (paramInt1 == k) {
      paramGraphics.drawLine(0, n, i1, n);
    }
    if ((ocean) && (tabPane.getSelectedIndex() == paramInt1 - 1)) {
      paramGraphics.setColor(oceanSelectedBorderColor);
    }
    paramGraphics.drawLine(i1 - 6, 0, i1, 6);
    paramGraphics.drawLine(0, 0, i1 - 6, 0);
    if ((ocean) && (paramBoolean))
    {
      paramGraphics.drawLine(i1, 6, i1, n);
      if (paramInt1 != m)
      {
        paramGraphics.setColor(darkShadow);
        paramGraphics.drawLine(i1, 0, i1, 5);
      }
    }
    else if ((ocean) && (tabPane.getSelectedIndex() == paramInt1 - 1))
    {
      paramGraphics.setColor(oceanSelectedBorderColor);
      paramGraphics.drawLine(i1, 0, i1, 6);
      paramGraphics.setColor(darkShadow);
      paramGraphics.drawLine(i1, 6, i1, n);
    }
    else if (paramInt1 != m)
    {
      paramGraphics.drawLine(i1, 0, i1, n);
    }
    else
    {
      paramGraphics.drawLine(i1, 6, i1, n);
    }
    paramGraphics.translate(-paramInt2, -paramInt3);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (paramJComponent.isOpaque())
    {
      paramGraphics.setColor(tabAreaBackground);
      paramGraphics.fillRect(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    }
    paint(paramGraphics, paramJComponent);
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    int i = paramInt6 / 2;
    if (paramBoolean) {
      paramGraphics.setColor(selectColor);
    } else {
      paramGraphics.setColor(getUnselectedBackgroundAt(paramInt2));
    }
    if (MetalUtils.isLeftToRight(tabPane)) {
      switch (paramInt1)
      {
      case 2: 
        paramGraphics.fillRect(paramInt3 + 5, paramInt4 + 1, paramInt5 - 5, paramInt6 - 1);
        paramGraphics.fillRect(paramInt3 + 2, paramInt4 + 4, 3, paramInt6 - 4);
        break;
      case 3: 
        paramGraphics.fillRect(paramInt3 + 2, paramInt4, paramInt5 - 2, paramInt6 - 4);
        paramGraphics.fillRect(paramInt3 + 5, paramInt4 + (paramInt6 - 1) - 3, paramInt5 - 5, 3);
        break;
      case 4: 
        paramGraphics.fillRect(paramInt3, paramInt4 + 2, paramInt5 - 4, paramInt6 - 2);
        paramGraphics.fillRect(paramInt3 + (paramInt5 - 1) - 3, paramInt4 + 5, 3, paramInt6 - 5);
        break;
      case 1: 
      default: 
        paramGraphics.fillRect(paramInt3 + 4, paramInt4 + 2, paramInt5 - 1 - 3, paramInt6 - 1 - 1);
        paramGraphics.fillRect(paramInt3 + 2, paramInt4 + 5, 2, paramInt6 - 5);
        break;
      }
    } else {
      switch (paramInt1)
      {
      case 2: 
        paramGraphics.fillRect(paramInt3 + 5, paramInt4 + 1, paramInt5 - 5, paramInt6 - 1);
        paramGraphics.fillRect(paramInt3 + 2, paramInt4 + 4, 3, paramInt6 - 4);
        break;
      case 3: 
        paramGraphics.fillRect(paramInt3, paramInt4, paramInt5 - 5, paramInt6 - 1);
        paramGraphics.fillRect(paramInt3 + (paramInt5 - 1) - 4, paramInt4, 4, paramInt6 - 5);
        paramGraphics.fillRect(paramInt3 + (paramInt5 - 1) - 4, paramInt4 + (paramInt6 - 1) - 4, 2, 2);
        break;
      case 4: 
        paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 5, paramInt6 - 1);
        paramGraphics.fillRect(paramInt3 + (paramInt5 - 1) - 3, paramInt4 + 5, 3, paramInt6 - 5);
        break;
      case 1: 
      default: 
        paramGraphics.fillRect(paramInt3, paramInt4 + 2, paramInt5 - 1 - 3, paramInt6 - 1 - 1);
        paramGraphics.fillRect(paramInt3 + (paramInt5 - 1) - 3, paramInt4 + 5, 3, paramInt6 - 3);
      }
    }
  }
  
  protected int getTabLabelShiftX(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return 0;
  }
  
  protected int getTabLabelShiftY(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return 0;
  }
  
  protected int getBaselineOffset()
  {
    return 0;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    int i = tabPane.getTabPlacement();
    Insets localInsets = paramJComponent.getInsets();
    Dimension localDimension = paramJComponent.getSize();
    if (tabPane.isOpaque())
    {
      if ((!paramJComponent.isBackgroundSet()) && (tabAreaBackground != null)) {
        paramGraphics.setColor(tabAreaBackground);
      } else {
        paramGraphics.setColor(paramJComponent.getBackground());
      }
      switch (i)
      {
      case 2: 
        paramGraphics.fillRect(left, top, calculateTabAreaWidth(i, runCount, maxTabWidth), height - bottom - top);
        break;
      case 3: 
        int j = calculateTabAreaHeight(i, runCount, maxTabHeight);
        paramGraphics.fillRect(left, height - bottom - j, width - left - right, j);
        break;
      case 4: 
        int k = calculateTabAreaWidth(i, runCount, maxTabWidth);
        paramGraphics.fillRect(width - right - k, top, k, height - top - bottom);
        break;
      case 1: 
      default: 
        paramGraphics.fillRect(left, top, width - right - left, calculateTabAreaHeight(i, runCount, maxTabHeight));
        paintHighlightBelowTab();
      }
    }
    super.paint(paramGraphics, paramJComponent);
  }
  
  protected void paintHighlightBelowTab() {}
  
  protected void paintFocusIndicator(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2, boolean paramBoolean)
  {
    if ((tabPane.hasFocus()) && (paramBoolean))
    {
      Rectangle localRectangle = paramArrayOfRectangle[paramInt2];
      boolean bool1 = isLastInRun(paramInt2);
      paramGraphics.setColor(focus);
      paramGraphics.translate(x, y);
      int i = width - 1;
      int j = height - 1;
      boolean bool2 = MetalUtils.isLeftToRight(tabPane);
      switch (paramInt1)
      {
      case 4: 
        paramGraphics.drawLine(i - 6, 2, i - 2, 6);
        paramGraphics.drawLine(1, 2, i - 6, 2);
        paramGraphics.drawLine(i - 2, 6, i - 2, j);
        paramGraphics.drawLine(1, 2, 1, j);
        paramGraphics.drawLine(1, j, i - 2, j);
        break;
      case 3: 
        if (bool2)
        {
          paramGraphics.drawLine(2, j - 6, 6, j - 2);
          paramGraphics.drawLine(6, j - 2, i, j - 2);
          paramGraphics.drawLine(2, 0, 2, j - 6);
          paramGraphics.drawLine(2, 0, i, 0);
          paramGraphics.drawLine(i, 0, i, j - 2);
        }
        else
        {
          paramGraphics.drawLine(i - 2, j - 6, i - 6, j - 2);
          paramGraphics.drawLine(i - 2, 0, i - 2, j - 6);
          if (bool1)
          {
            paramGraphics.drawLine(2, j - 2, i - 6, j - 2);
            paramGraphics.drawLine(2, 0, i - 2, 0);
            paramGraphics.drawLine(2, 0, 2, j - 2);
          }
          else
          {
            paramGraphics.drawLine(1, j - 2, i - 6, j - 2);
            paramGraphics.drawLine(1, 0, i - 2, 0);
            paramGraphics.drawLine(1, 0, 1, j - 2);
          }
        }
        break;
      case 2: 
        paramGraphics.drawLine(2, 6, 6, 2);
        paramGraphics.drawLine(2, 6, 2, j - 1);
        paramGraphics.drawLine(6, 2, i, 2);
        paramGraphics.drawLine(i, 2, i, j - 1);
        paramGraphics.drawLine(2, j - 1, i, j - 1);
        break;
      case 1: 
      default: 
        if (bool2)
        {
          paramGraphics.drawLine(2, 6, 6, 2);
          paramGraphics.drawLine(2, 6, 2, j - 1);
          paramGraphics.drawLine(6, 2, i, 2);
          paramGraphics.drawLine(i, 2, i, j - 1);
          paramGraphics.drawLine(2, j - 1, i, j - 1);
        }
        else
        {
          paramGraphics.drawLine(i - 2, 6, i - 6, 2);
          paramGraphics.drawLine(i - 2, 6, i - 2, j - 1);
          if (bool1)
          {
            paramGraphics.drawLine(i - 6, 2, 2, 2);
            paramGraphics.drawLine(2, 2, 2, j - 1);
            paramGraphics.drawLine(i - 2, j - 1, 2, j - 1);
          }
          else
          {
            paramGraphics.drawLine(i - 6, 2, 1, 2);
            paramGraphics.drawLine(1, 2, 1, j - 1);
            paramGraphics.drawLine(i - 2, j - 1, 1, j - 1);
          }
        }
        break;
      }
      paramGraphics.translate(-x, -y);
    }
  }
  
  protected void paintContentBorderTopEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    boolean bool1 = MetalUtils.isLeftToRight(tabPane);
    int i = paramInt3 + paramInt5 - 1;
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    if (ocean) {
      paramGraphics.setColor(oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(selectHighlight);
    }
    if ((paramInt1 != 1) || (paramInt2 < 0) || (y + height + 1 < paramInt4) || (x < paramInt3) || (x > paramInt3 + paramInt5))
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      if ((ocean) && (paramInt1 == 1))
      {
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        paramGraphics.drawLine(paramInt3, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
      }
    }
    else
    {
      boolean bool2 = isLastInRun(paramInt2);
      if ((bool1) || (bool2)) {
        paramGraphics.drawLine(paramInt3, paramInt4, x + 1, paramInt4);
      } else {
        paramGraphics.drawLine(paramInt3, paramInt4, x, paramInt4);
      }
      if (x + width < i - 1)
      {
        if ((bool1) && (!bool2)) {
          paramGraphics.drawLine(x + width, paramInt4, i - 1, paramInt4);
        } else {
          paramGraphics.drawLine(x + width - 1, paramInt4, i - 1, paramInt4);
        }
      }
      else
      {
        paramGraphics.setColor(shadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      }
      if (ocean)
      {
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        if ((bool1) || (bool2)) {
          paramGraphics.drawLine(paramInt3, paramInt4 + 1, x + 1, paramInt4 + 1);
        } else {
          paramGraphics.drawLine(paramInt3, paramInt4 + 1, x, paramInt4 + 1);
        }
        if (x + width < i - 1)
        {
          if ((bool1) && (!bool2)) {
            paramGraphics.drawLine(x + width, paramInt4 + 1, i - 1, paramInt4 + 1);
          } else {
            paramGraphics.drawLine(x + width - 1, paramInt4 + 1, i - 1, paramInt4 + 1);
          }
        }
        else
        {
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
        }
      }
    }
  }
  
  protected void paintContentBorderBottomEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    boolean bool1 = MetalUtils.isLeftToRight(tabPane);
    int i = paramInt4 + paramInt6 - 1;
    int j = paramInt3 + paramInt5 - 1;
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(darkShadow);
    if ((paramInt1 != 3) || (paramInt2 < 0) || (y - 1 > paramInt6) || (x < paramInt3) || (x > paramInt3 + paramInt5))
    {
      if ((ocean) && (paramInt1 == 3)) {
        paramGraphics.setColor(oceanSelectedBorderColor);
      }
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
    else
    {
      boolean bool2 = isLastInRun(paramInt2);
      if (ocean) {
        paramGraphics.setColor(oceanSelectedBorderColor);
      }
      if ((bool1) || (bool2)) {
        paramGraphics.drawLine(paramInt3, i, x, i);
      } else {
        paramGraphics.drawLine(paramInt3, i, x - 1, i);
      }
      if (x + width < paramInt3 + paramInt5 - 2) {
        if ((bool1) && (!bool2)) {
          paramGraphics.drawLine(x + width, i, j, i);
        } else {
          paramGraphics.drawLine(x + width - 1, i, j, i);
        }
      }
    }
  }
  
  protected void paintContentBorderLeftEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    if (ocean) {
      paramGraphics.setColor(oceanSelectedBorderColor);
    } else {
      paramGraphics.setColor(selectHighlight);
    }
    if ((paramInt1 != 2) || (paramInt2 < 0) || (x + width + 1 < paramInt3) || (y < paramInt4) || (y > paramInt4 + paramInt6))
    {
      paramGraphics.drawLine(paramInt3, paramInt4 + 1, paramInt3, paramInt4 + paramInt6 - 2);
      if ((ocean) && (paramInt1 == 2))
      {
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        paramGraphics.drawLine(paramInt3 + 1, paramInt4, paramInt3 + 1, paramInt4 + paramInt6 - 2);
      }
    }
    else
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, y + 1);
      if (y + height < paramInt4 + paramInt6 - 2) {
        paramGraphics.drawLine(paramInt3, y + height + 1, paramInt3, paramInt4 + paramInt6 + 2);
      }
      if (ocean)
      {
        paramGraphics.setColor(MetalLookAndFeel.getWhite());
        paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, y + 1);
        if (y + height < paramInt4 + paramInt6 - 2) {
          paramGraphics.drawLine(paramInt3 + 1, y + height + 1, paramInt3 + 1, paramInt4 + paramInt6 + 2);
        }
      }
    }
  }
  
  protected void paintContentBorderRightEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, calcRect);
    paramGraphics.setColor(darkShadow);
    if ((paramInt1 != 4) || (paramInt2 < 0) || (x - 1 > paramInt5) || (y < paramInt4) || (y > paramInt4 + paramInt6))
    {
      if ((ocean) && (paramInt1 == 4)) {
        paramGraphics.setColor(oceanSelectedBorderColor);
      }
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
    else
    {
      if (ocean) {
        paramGraphics.setColor(oceanSelectedBorderColor);
      }
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, y);
      if (y + height < paramInt4 + paramInt6 - 2) {
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, y + height, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
      }
    }
  }
  
  protected int calculateMaxTabHeight(int paramInt)
  {
    FontMetrics localFontMetrics = getFontMetrics();
    int i = localFontMetrics.getHeight();
    int j = 0;
    for (int k = 0; k < tabPane.getTabCount(); k++)
    {
      Icon localIcon = tabPane.getIconAt(k);
      if ((localIcon != null) && (localIcon.getIconHeight() > i))
      {
        j = 1;
        break;
      }
    }
    return super.calculateMaxTabHeight(paramInt) - (j != 0 ? tabInsets.top + tabInsets.bottom : 0);
  }
  
  protected int getTabRunOverlay(int paramInt)
  {
    if ((paramInt == 2) || (paramInt == 4))
    {
      int i = calculateMaxTabHeight(paramInt);
      return i / 2;
    }
    return 0;
  }
  
  protected boolean shouldRotateTabRuns(int paramInt1, int paramInt2)
  {
    return false;
  }
  
  protected boolean shouldPadTabRun(int paramInt1, int paramInt2)
  {
    return (runCount > 1) && (paramInt2 < runCount - 1);
  }
  
  private boolean isLastInRun(int paramInt)
  {
    int i = getRunForTab(tabPane.getTabCount(), paramInt);
    int j = lastTabInRun(tabPane.getTabCount(), i);
    return paramInt == j;
  }
  
  private Color getUnselectedBackgroundAt(int paramInt)
  {
    Color localColor = tabPane.getBackgroundAt(paramInt);
    if (((localColor instanceof UIResource)) && (unselectedBackground != null)) {
      return unselectedBackground;
    }
    return localColor;
  }
  
  int getRolloverTabIndex()
  {
    return getRolloverTab();
  }
  
  public class TabbedPaneLayout
    extends BasicTabbedPaneUI.TabbedPaneLayout
  {
    public TabbedPaneLayout()
    {
      super();
    }
    
    protected void normalizeTabRuns(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((paramInt1 == 1) || (paramInt1 == 3)) {
        super.normalizeTabRuns(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    protected void rotateTabRuns(int paramInt1, int paramInt2) {}
    
    protected void padSelectedTab(int paramInt1, int paramInt2) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */