package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

public class MetalTreeUI
  extends BasicTreeUI
{
  private static Color lineColor;
  private static final String LINE_STYLE = "JTree.lineStyle";
  private static final String LEG_LINE_STYLE_STRING = "Angled";
  private static final String HORIZ_STYLE_STRING = "Horizontal";
  private static final String NO_STYLE_STRING = "None";
  private static final int LEG_LINE_STYLE = 2;
  private static final int HORIZ_LINE_STYLE = 1;
  private static final int NO_LINE_STYLE = 0;
  private int lineStyle = 2;
  private PropertyChangeListener lineStyleListener = new LineListener();
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalTreeUI();
  }
  
  public MetalTreeUI() {}
  
  protected int getHorizontalLegBuffer()
  {
    return 3;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    lineColor = UIManager.getColor("Tree.line");
    Object localObject = paramJComponent.getClientProperty("JTree.lineStyle");
    decodeLineStyle(localObject);
    paramJComponent.addPropertyChangeListener(lineStyleListener);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    paramJComponent.removePropertyChangeListener(lineStyleListener);
    super.uninstallUI(paramJComponent);
  }
  
  protected void decodeLineStyle(Object paramObject)
  {
    if ((paramObject == null) || (paramObject.equals("Angled"))) {
      lineStyle = 2;
    } else if (paramObject.equals("None")) {
      lineStyle = 0;
    } else if (paramObject.equals("Horizontal")) {
      lineStyle = 1;
    }
  }
  
  protected boolean isLocationInExpandControl(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((tree != null) && (!isLeaf(paramInt1)))
    {
      int i;
      if (getExpandedIcon() != null) {
        i = getExpandedIcon().getIconWidth() + 6;
      } else {
        i = 8;
      }
      Insets localInsets = tree.getInsets();
      int j = localInsets != null ? left : 0;
      j += (paramInt2 + depthOffset - 1) * totalChildIndent + getLeftChildIndent() - i / 2;
      int k = j + i;
      return (paramInt3 >= j) && (paramInt3 <= k);
    }
    return false;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    super.paint(paramGraphics, paramJComponent);
    if ((lineStyle == 1) && (!largeModel)) {
      paintHorizontalSeparators(paramGraphics, paramJComponent);
    }
  }
  
  protected void paintHorizontalSeparators(Graphics paramGraphics, JComponent paramJComponent)
  {
    paramGraphics.setColor(lineColor);
    Rectangle localRectangle1 = paramGraphics.getClipBounds();
    int i = getRowForPath(tree, getClosestPathForLocation(tree, 0, y));
    int j = getRowForPath(tree, getClosestPathForLocation(tree, 0, y + height - 1));
    if ((i <= -1) || (j <= -1)) {
      return;
    }
    for (int k = i; k <= j; k++)
    {
      TreePath localTreePath = getPathForRow(tree, k);
      if ((localTreePath != null) && (localTreePath.getPathCount() == 2))
      {
        Rectangle localRectangle2 = getPathBounds(tree, getPathForRow(tree, k));
        if (localRectangle2 != null) {
          paramGraphics.drawLine(x, y, x + width, y);
        }
      }
    }
  }
  
  protected void paintVerticalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle, Insets paramInsets, TreePath paramTreePath)
  {
    if (lineStyle == 2) {
      super.paintVerticalPartOfLeg(paramGraphics, paramRectangle, paramInsets, paramTreePath);
    }
  }
  
  protected void paintHorizontalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (lineStyle == 2) {
      super.paintHorizontalPartOfLeg(paramGraphics, paramRectangle1, paramInsets, paramRectangle2, paramTreePath, paramInt, paramBoolean1, paramBoolean2, paramBoolean3);
    }
  }
  
  class LineListener
    implements PropertyChangeListener
  {
    LineListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str.equals("JTree.lineStyle")) {
        decodeLineStyle(paramPropertyChangeEvent.getNewValue());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */