package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

public class MultiTextUI
  extends TextUI
{
  protected Vector uis = new Vector();
  
  public MultiTextUI() {}
  
  public ComponentUI[] getUIs()
  {
    return MultiLookAndFeel.uisToArray(uis);
  }
  
  public String getToolTipText(JTextComponent paramJTextComponent, Point paramPoint)
  {
    String str = ((TextUI)uis.elementAt(0)).getToolTipText(paramJTextComponent, paramPoint);
    for (int i = 1; i < uis.size(); i++) {
      ((TextUI)uis.elementAt(i)).getToolTipText(paramJTextComponent, paramPoint);
    }
    return str;
  }
  
  public Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt)
    throws BadLocationException
  {
    Rectangle localRectangle = ((TextUI)uis.elementAt(0)).modelToView(paramJTextComponent, paramInt);
    for (int i = 1; i < uis.size(); i++) {
      ((TextUI)uis.elementAt(i)).modelToView(paramJTextComponent, paramInt);
    }
    return localRectangle;
  }
  
  public Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt, Position.Bias paramBias)
    throws BadLocationException
  {
    Rectangle localRectangle = ((TextUI)uis.elementAt(0)).modelToView(paramJTextComponent, paramInt, paramBias);
    for (int i = 1; i < uis.size(); i++) {
      ((TextUI)uis.elementAt(i)).modelToView(paramJTextComponent, paramInt, paramBias);
    }
    return localRectangle;
  }
  
  public int viewToModel(JTextComponent paramJTextComponent, Point paramPoint)
  {
    int i = ((TextUI)uis.elementAt(0)).viewToModel(paramJTextComponent, paramPoint);
    for (int j = 1; j < uis.size(); j++) {
      ((TextUI)uis.elementAt(j)).viewToModel(paramJTextComponent, paramPoint);
    }
    return i;
  }
  
  public int viewToModel(JTextComponent paramJTextComponent, Point paramPoint, Position.Bias[] paramArrayOfBias)
  {
    int i = ((TextUI)uis.elementAt(0)).viewToModel(paramJTextComponent, paramPoint, paramArrayOfBias);
    for (int j = 1; j < uis.size(); j++) {
      ((TextUI)uis.elementAt(j)).viewToModel(paramJTextComponent, paramPoint, paramArrayOfBias);
    }
    return i;
  }
  
  public int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    int i = ((TextUI)uis.elementAt(0)).getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
    for (int j = 1; j < uis.size(); j++) {
      ((TextUI)uis.elementAt(j)).getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
    }
    return i;
  }
  
  public void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((TextUI)uis.elementAt(i)).damageRange(paramJTextComponent, paramInt1, paramInt2);
    }
  }
  
  public void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2, Position.Bias paramBias1, Position.Bias paramBias2)
  {
    for (int i = 0; i < uis.size(); i++) {
      ((TextUI)uis.elementAt(i)).damageRange(paramJTextComponent, paramInt1, paramInt2, paramBias1, paramBias2);
    }
  }
  
  public EditorKit getEditorKit(JTextComponent paramJTextComponent)
  {
    EditorKit localEditorKit = ((TextUI)uis.elementAt(0)).getEditorKit(paramJTextComponent);
    for (int i = 1; i < uis.size(); i++) {
      ((TextUI)uis.elementAt(i)).getEditorKit(paramJTextComponent);
    }
    return localEditorKit;
  }
  
  public View getRootView(JTextComponent paramJTextComponent)
  {
    View localView = ((TextUI)uis.elementAt(0)).getRootView(paramJTextComponent);
    for (int i = 1; i < uis.size(); i++) {
      ((TextUI)uis.elementAt(i)).getRootView(paramJTextComponent);
    }
    return localView;
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
    MultiTextUI localMultiTextUI = new MultiTextUI();
    return MultiLookAndFeel.createUIs(localMultiTextUI, uis, paramJComponent);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\multi\MultiTextUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */