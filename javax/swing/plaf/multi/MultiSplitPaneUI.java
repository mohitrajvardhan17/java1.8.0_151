package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;

public class MultiSplitPaneUI
  extends SplitPaneUI
{
  protected Vector uis = new Vector();
  
  public MultiSplitPaneUI() {}
  
  public ComponentUI[] getUIs()
  {
    return MultiLookAndFeel.uisToArray(uis);
  }
  
  public void resetToPreferredSizes(JSplitPane paramJSplitPane)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((SplitPaneUI)uis.elementAt(i)).resetToPreferredSizes(paramJSplitPane);
    }
  }
  
  public void setDividerLocation(JSplitPane paramJSplitPane, int paramInt)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((SplitPaneUI)uis.elementAt(i)).setDividerLocation(paramJSplitPane, paramInt);
    }
  }
  
  public int getDividerLocation(JSplitPane paramJSplitPane)
  {
    int i = ((SplitPaneUI)uis.elementAt(0)).getDividerLocation(paramJSplitPane);
    for (int j = 1; j < uis.size(); j++) {
      ((SplitPaneUI)uis.elementAt(j)).getDividerLocation(paramJSplitPane);
    }
    return i;
  }
  
  public int getMinimumDividerLocation(JSplitPane paramJSplitPane)
  {
    int i = ((SplitPaneUI)uis.elementAt(0)).getMinimumDividerLocation(paramJSplitPane);
    for (int j = 1; j < uis.size(); j++) {
      ((SplitPaneUI)uis.elementAt(j)).getMinimumDividerLocation(paramJSplitPane);
    }
    return i;
  }
  
  public int getMaximumDividerLocation(JSplitPane paramJSplitPane)
  {
    int i = ((SplitPaneUI)uis.elementAt(0)).getMaximumDividerLocation(paramJSplitPane);
    for (int j = 1; j < uis.size(); j++) {
      ((SplitPaneUI)uis.elementAt(j)).getMaximumDividerLocation(paramJSplitPane);
    }
    return i;
  }
  
  public void finishedPaintingChildren(JSplitPane paramJSplitPane, Graphics paramGraphics)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((SplitPaneUI)uis.elementAt(i)).finishedPaintingChildren(paramJSplitPane, paramGraphics);
    }
  }
  
  public boolean contains(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    boolean bool = ((ComponentUI)uis.elementAt(0)).contains(paramJComponent, paramInt1, paramInt2);
    for (int i = 1; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).contains(paramJComponent, paramInt1, paramInt2);
    }
    return bool;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).update(paramGraphics, paramJComponent);
    }
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    MultiSplitPaneUI localMultiSplitPaneUI = new MultiSplitPaneUI();
    return MultiLookAndFeel.createUIs(localMultiSplitPaneUI, uis, paramJComponent);
  }
  
  public void installUI(JComponent paramJComponent)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).installUI(paramJComponent);
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).uninstallUI(paramJComponent);
    }
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).paint(paramGraphics, paramJComponent);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = ((ComponentUI)uis.elementAt(0)).getPreferredSize(paramJComponent);
    for (int i = 1; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).getPreferredSize(paramJComponent);
    }
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Dimension localDimension = ((ComponentUI)uis.elementAt(0)).getMinimumSize(paramJComponent);
    for (int i = 1; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).getMinimumSize(paramJComponent);
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Dimension localDimension = ((ComponentUI)uis.elementAt(0)).getMaximumSize(paramJComponent);
    for (int i = 1; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).getMaximumSize(paramJComponent);
    }
    return localDimension;
  }
  
  public int getAccessibleChildrenCount(JComponent paramJComponent)
  {
    int i = ((ComponentUI)uis.elementAt(0)).getAccessibleChildrenCount(paramJComponent);
    for (int j = 1; j < uis.size(); j++) {
      ((ComponentUI)uis.elementAt(j)).getAccessibleChildrenCount(paramJComponent);
    }
    return i;
  }
  
  public Accessible getAccessibleChild(JComponent paramJComponent, int paramInt)
  {
    Accessible localAccessible = ((ComponentUI)uis.elementAt(0)).getAccessibleChild(paramJComponent, paramInt);
    for (int i = 1; i < uis.size(); i++) {
      ((ComponentUI)uis.elementAt(i)).getAccessibleChild(paramJComponent, paramInt);
    }
    return localAccessible;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\multi\MultiSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */