package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import sun.awt.AppContext;

public class WindowsRadioButtonUI
  extends BasicRadioButtonUI
{
  private static final Object WINDOWS_RADIO_BUTTON_UI_KEY = new Object();
  protected int dashedRectGapX;
  protected int dashedRectGapY;
  protected int dashedRectGapWidth;
  protected int dashedRectGapHeight;
  protected Color focusColor;
  private boolean initialized = false;
  
  public WindowsRadioButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    WindowsRadioButtonUI localWindowsRadioButtonUI = (WindowsRadioButtonUI)localAppContext.get(WINDOWS_RADIO_BUTTON_UI_KEY);
    if (localWindowsRadioButtonUI == null)
    {
      localWindowsRadioButtonUI = new WindowsRadioButtonUI();
      localAppContext.put(WINDOWS_RADIO_BUTTON_UI_KEY, localWindowsRadioButtonUI);
    }
    return localWindowsRadioButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!initialized)
    {
      dashedRectGapX = ((Integer)UIManager.get("Button.dashedRectGapX")).intValue();
      dashedRectGapY = ((Integer)UIManager.get("Button.dashedRectGapY")).intValue();
      dashedRectGapWidth = ((Integer)UIManager.get("Button.dashedRectGapWidth")).intValue();
      dashedRectGapHeight = ((Integer)UIManager.get("Button.dashedRectGapHeight")).intValue();
      focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
      initialized = true;
    }
    if (XPStyle.getXP() != null) {
      LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", Boolean.TRUE);
    }
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    initialized = false;
  }
  
  protected Color getFocusColor()
  {
    return focusColor;
  }
  
  protected void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString)
  {
    WindowsGraphicsUtils.paintText(paramGraphics, paramAbstractButton, paramRectangle, paramString, getTextShiftOffset());
  }
  
  protected void paintFocus(Graphics paramGraphics, Rectangle paramRectangle, Dimension paramDimension)
  {
    paramGraphics.setColor(getFocusColor());
    BasicGraphicsUtils.drawDashedRect(paramGraphics, x, y, width, height);
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */