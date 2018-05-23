package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import sun.awt.AppContext;

public class WindowsButtonUI
  extends BasicButtonUI
{
  protected int dashedRectGapX;
  protected int dashedRectGapY;
  protected int dashedRectGapWidth;
  protected int dashedRectGapHeight;
  protected Color focusColor;
  private boolean defaults_initialized = false;
  private static final Object WINDOWS_BUTTON_UI_KEY = new Object();
  private Rectangle viewRect = new Rectangle();
  
  public WindowsButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    WindowsButtonUI localWindowsButtonUI = (WindowsButtonUI)localAppContext.get(WINDOWS_BUTTON_UI_KEY);
    if (localWindowsButtonUI == null)
    {
      localWindowsButtonUI = new WindowsButtonUI();
      localAppContext.put(WINDOWS_BUTTON_UI_KEY, localWindowsButtonUI);
    }
    return localWindowsButtonUI;
  }
  
  protected void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!defaults_initialized)
    {
      localObject = getPropertyPrefix();
      dashedRectGapX = UIManager.getInt((String)localObject + "dashedRectGapX");
      dashedRectGapY = UIManager.getInt((String)localObject + "dashedRectGapY");
      dashedRectGapWidth = UIManager.getInt((String)localObject + "dashedRectGapWidth");
      dashedRectGapHeight = UIManager.getInt((String)localObject + "dashedRectGapHeight");
      focusColor = UIManager.getColor((String)localObject + "focus");
      defaults_initialized = true;
    }
    Object localObject = XPStyle.getXP();
    if (localObject != null)
    {
      paramAbstractButton.setBorder(((XPStyle)localObject).getBorder(paramAbstractButton, getXPButtonType(paramAbstractButton)));
      LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", Boolean.TRUE);
    }
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    defaults_initialized = false;
  }
  
  protected Color getFocusColor()
  {
    return focusColor;
  }
  
  protected void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString)
  {
    WindowsGraphicsUtils.paintText(paramGraphics, paramAbstractButton, paramRectangle, paramString, getTextShiftOffset());
  }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3)
  {
    int i = paramAbstractButton.getWidth();
    int j = paramAbstractButton.getHeight();
    paramGraphics.setColor(getFocusColor());
    BasicGraphicsUtils.drawDashedRect(paramGraphics, dashedRectGapX, dashedRectGapY, i - dashedRectGapWidth, j - dashedRectGapHeight);
  }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton)
  {
    setTextShiftOffset();
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = super.getPreferredSize(paramJComponent);
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    if ((localDimension != null) && (localAbstractButton.isFocusPainted()))
    {
      if (width % 2 == 0) {
        width += 1;
      }
      if (height % 2 == 0) {
        height += 1;
      }
    }
    return localDimension;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (XPStyle.getXP() != null) {
      paintXPButtonBackground(paramGraphics, paramJComponent);
    }
    super.paint(paramGraphics, paramJComponent);
  }
  
  static TMSchema.Part getXPButtonType(AbstractButton paramAbstractButton)
  {
    if ((paramAbstractButton instanceof JCheckBox)) {
      return TMSchema.Part.BP_CHECKBOX;
    }
    if ((paramAbstractButton instanceof JRadioButton)) {
      return TMSchema.Part.BP_RADIOBUTTON;
    }
    boolean bool = paramAbstractButton.getParent() instanceof JToolBar;
    return bool ? TMSchema.Part.TP_BUTTON : TMSchema.Part.BP_PUSHBUTTON;
  }
  
  static TMSchema.State getXPButtonState(AbstractButton paramAbstractButton)
  {
    TMSchema.Part localPart = getXPButtonType(paramAbstractButton);
    ButtonModel localButtonModel = paramAbstractButton.getModel();
    TMSchema.State localState = TMSchema.State.NORMAL;
    switch (localPart)
    {
    case BP_RADIOBUTTON: 
    case BP_CHECKBOX: 
      if (!localButtonModel.isEnabled()) {
        localState = localButtonModel.isSelected() ? TMSchema.State.CHECKEDDISABLED : TMSchema.State.UNCHECKEDDISABLED;
      } else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
        localState = localButtonModel.isSelected() ? TMSchema.State.CHECKEDPRESSED : TMSchema.State.UNCHECKEDPRESSED;
      } else if (localButtonModel.isRollover()) {
        localState = localButtonModel.isSelected() ? TMSchema.State.CHECKEDHOT : TMSchema.State.UNCHECKEDHOT;
      } else {
        localState = localButtonModel.isSelected() ? TMSchema.State.CHECKEDNORMAL : TMSchema.State.UNCHECKEDNORMAL;
      }
      break;
    case BP_PUSHBUTTON: 
    case TP_BUTTON: 
      boolean bool = paramAbstractButton.getParent() instanceof JToolBar;
      if (bool)
      {
        if ((localButtonModel.isArmed()) && (localButtonModel.isPressed())) {
          localState = TMSchema.State.PRESSED;
        } else if (!localButtonModel.isEnabled()) {
          localState = TMSchema.State.DISABLED;
        } else if ((localButtonModel.isSelected()) && (localButtonModel.isRollover())) {
          localState = TMSchema.State.HOTCHECKED;
        } else if (localButtonModel.isSelected()) {
          localState = TMSchema.State.CHECKED;
        } else if (localButtonModel.isRollover()) {
          localState = TMSchema.State.HOT;
        } else if (paramAbstractButton.hasFocus()) {
          localState = TMSchema.State.HOT;
        }
      }
      else if (((localButtonModel.isArmed()) && (localButtonModel.isPressed())) || (localButtonModel.isSelected())) {
        localState = TMSchema.State.PRESSED;
      } else if (!localButtonModel.isEnabled()) {
        localState = TMSchema.State.DISABLED;
      } else if ((localButtonModel.isRollover()) || (localButtonModel.isPressed())) {
        localState = TMSchema.State.HOT;
      } else if (((paramAbstractButton instanceof JButton)) && (((JButton)paramAbstractButton).isDefaultButton())) {
        localState = TMSchema.State.DEFAULTED;
      } else if (paramAbstractButton.hasFocus()) {
        localState = TMSchema.State.HOT;
      }
      break;
    default: 
      localState = TMSchema.State.NORMAL;
    }
    return localState;
  }
  
  static void paintXPButtonBackground(Graphics paramGraphics, JComponent paramJComponent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    XPStyle localXPStyle = XPStyle.getXP();
    TMSchema.Part localPart = getXPButtonType(localAbstractButton);
    if ((localAbstractButton.isContentAreaFilled()) && (localXPStyle != null))
    {
      XPStyle.Skin localSkin = localXPStyle.getSkin(localAbstractButton, localPart);
      TMSchema.State localState = getXPButtonState(localAbstractButton);
      Dimension localDimension = paramJComponent.getSize();
      int i = 0;
      int j = 0;
      int k = width;
      int m = height;
      Border localBorder = paramJComponent.getBorder();
      Insets localInsets;
      if (localBorder != null) {
        localInsets = getOpaqueInsets(localBorder, paramJComponent);
      } else {
        localInsets = paramJComponent.getInsets();
      }
      if (localInsets != null)
      {
        i += left;
        j += top;
        k -= left + right;
        m -= top + bottom;
      }
      localSkin.paintSkin(paramGraphics, i, j, k, m, localState);
    }
  }
  
  private static Insets getOpaqueInsets(Border paramBorder, Component paramComponent)
  {
    if (paramBorder == null) {
      return null;
    }
    if (paramBorder.isBorderOpaque()) {
      return paramBorder.getBorderInsets(paramComponent);
    }
    if ((paramBorder instanceof CompoundBorder))
    {
      CompoundBorder localCompoundBorder = (CompoundBorder)paramBorder;
      Insets localInsets1 = getOpaqueInsets(localCompoundBorder.getOutsideBorder(), paramComponent);
      if ((localInsets1 != null) && (localInsets1.equals(localCompoundBorder.getOutsideBorder().getBorderInsets(paramComponent))))
      {
        Insets localInsets2 = getOpaqueInsets(localCompoundBorder.getInsideBorder(), paramComponent);
        if (localInsets2 == null) {
          return localInsets1;
        }
        return new Insets(top + top, left + left, bottom + bottom, right + right);
      }
      return localInsets1;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */