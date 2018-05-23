package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreePath;

public class MultiTreeUI
  extends TreeUI
{
  protected Vector uis = new Vector();
  
  public MultiTreeUI() {}
  
  public ComponentUI[] getUIs()
  {
    return MultiLookAndFeel.uisToArray(uis);
  }
  
  public Rectangle getPathBounds(JTree paramJTree, TreePath paramTreePath)
  {
    Rectangle localRectangle = ((TreeUI)uis.elementAt(0)).getPathBounds(paramJTree, paramTreePath);
    for (int i = 1; i < uis.size(); i++) {
      ((TreeUI)uis.elementAt(i)).getPathBounds(paramJTree, paramTreePath);
    }
    return localRectangle;
  }
  
  public TreePath getPathForRow(JTree paramJTree, int paramInt)
  {
    TreePath localTreePath = ((TreeUI)uis.elementAt(0)).getPathForRow(paramJTree, paramInt);
    for (int i = 1; i < uis.size(); i++) {
      ((TreeUI)uis.elementAt(i)).getPathForRow(paramJTree, paramInt);
    }
    return localTreePath;
  }
  
  public int getRowForPath(JTree paramJTree, TreePath paramTreePath)
  {
    int i = ((TreeUI)uis.elementAt(0)).getRowForPath(paramJTree, paramTreePath);
    for (int j = 1; j < uis.size(); j++) {
      ((TreeUI)uis.elementAt(j)).getRowForPath(paramJTree, paramTreePath);
    }
    return i;
  }
  
  public int getRowCount(JTree paramJTree)
  {
    int i = ((TreeUI)uis.elementAt(0)).getRowCount(paramJTree);
    for (int j = 1; j < uis.size(); j++) {
      ((TreeUI)uis.elementAt(j)).getRowCount(paramJTree);
    }
    return i;
  }
  
  public TreePath getClosestPathForLocation(JTree paramJTree, int paramInt1, int paramInt2)
  {
    TreePath localTreePath = ((TreeUI)uis.elementAt(0)).getClosestPathForLocation(paramJTree, paramInt1, paramInt2);
    for (int i = 1; i < uis.size(); i++) {
      ((TreeUI)uis.elementAt(i)).getClosestPathForLocation(paramJTree, paramInt1, paramInt2);
    }
    return localTreePath;
  }
  
  public boolean isEditing(JTree paramJTree)
  {
    boolean bool = ((TreeUI)uis.elementAt(0)).isEditing(paramJTree);
    for (int i = 1; i < uis.size(); i++) {
      ((TreeUI)uis.elementAt(i)).isEditing(paramJTree);
    }
    return bool;
  }
  
  public boolean stopEditing(JTree paramJTree)
  {
    boolean bool = ((TreeUI)uis.elementAt(0)).stopEditing(paramJTree);
    for (int i = 1; i < uis.size(); i++) {
      ((TreeUI)uis.elementAt(i)).stopEditing(paramJTree);
    }
    return bool;
  }
  
  public void cancelEditing(JTree paramJTree)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((TreeUI)uis.elementAt(i)).cancelEditing(paramJTree);
    }
  }
  
  public void startEditingAtPath(JTree paramJTree, TreePath paramTreePath)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((TreeUI)uis.elementAt(i)).startEditingAtPath(paramJTree, paramTreePath);
    }
  }
  
  public TreePath getEditingPath(JTree paramJTree)
  {
    TreePath localTreePath = ((TreeUI)uis.elementAt(0)).getEditingPath(paramJTree);
    for (int i = 1; i < uis.size(); i++) {
      ((TreeUI)uis.elementAt(i)).getEditingPath(paramJTree);
    }
    return localTreePath;
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
    MultiTreeUI localMultiTreeUI = new MultiTreeUI();
    return MultiLookAndFeel.createUIs(localMultiTreeUI, uis, paramJComponent);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\multi\MultiTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */