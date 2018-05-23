package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import sun.swing.SwingUtilities2;

public class MotifPopupMenuUI
  extends BasicPopupMenuUI
{
  private static Border border = null;
  private Font titleFont = null;
  
  public MotifPopupMenuUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifPopupMenuUI();
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    LayoutManager localLayoutManager = paramJComponent.getLayout();
    Dimension localDimension = localLayoutManager.preferredLayoutSize(paramJComponent);
    String str = ((JPopupMenu)paramJComponent).getLabel();
    if (titleFont == null)
    {
      localObject = UIManager.getLookAndFeelDefaults();
      titleFont = ((UIDefaults)localObject).getFont("PopupMenu.font");
    }
    Object localObject = paramJComponent.getFontMetrics(titleFont);
    int i = 0;
    if (str != null) {
      i += SwingUtilities2.stringWidth(paramJComponent, (FontMetrics)localObject, str);
    }
    if (width < i)
    {
      width = (i + 8);
      Insets localInsets = paramJComponent.getInsets();
      if (localInsets != null) {
        width += left + right;
      }
      if (border != null)
      {
        localInsets = border.getBorderInsets(paramJComponent);
        width += left + right;
      }
      return localDimension;
    }
    return null;
  }
  
  protected ChangeListener createChangeListener(JPopupMenu paramJPopupMenu)
  {
    new ChangeListener()
    {
      public void stateChanged(ChangeEvent paramAnonymousChangeEvent) {}
    };
  }
  
  public boolean isPopupTrigger(MouseEvent paramMouseEvent)
  {
    return (paramMouseEvent.getID() == 501) && ((paramMouseEvent.getModifiers() & 0x4) != 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifPopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */