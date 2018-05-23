package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

public class WindowsToolBarUI
  extends BasicToolBarUI
{
  public WindowsToolBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsToolBarUI();
  }
  
  protected void installDefaults()
  {
    if (XPStyle.getXP() != null) {
      setRolloverBorders(true);
    }
    super.installDefaults();
  }
  
  protected Border createRolloverBorder()
  {
    if (XPStyle.getXP() != null) {
      return new EmptyBorder(3, 3, 3, 3);
    }
    return super.createRolloverBorder();
  }
  
  protected Border createNonRolloverBorder()
  {
    if (XPStyle.getXP() != null) {
      return new EmptyBorder(3, 3, 3, 3);
    }
    return super.createNonRolloverBorder();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      localXPStyle.getSkin(paramJComponent, TMSchema.Part.TP_TOOLBAR).paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), null, true);
    } else {
      super.paint(paramGraphics, paramJComponent);
    }
  }
  
  protected Border getRolloverBorder(AbstractButton paramAbstractButton)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      return localXPStyle.getBorder(paramAbstractButton, WindowsButtonUI.getXPButtonType(paramAbstractButton));
    }
    return super.getRolloverBorder(paramAbstractButton);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsToolBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */