package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import sun.awt.AppContext;

public class MotifRadioButtonUI
  extends BasicRadioButtonUI
{
  private static final Object MOTIF_RADIO_BUTTON_UI_KEY = new Object();
  protected Color focusColor;
  private boolean defaults_initialized = false;
  
  public MotifRadioButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MotifRadioButtonUI localMotifRadioButtonUI = (MotifRadioButtonUI)localAppContext.get(MOTIF_RADIO_BUTTON_UI_KEY);
    if (localMotifRadioButtonUI == null)
    {
      localMotifRadioButtonUI = new MotifRadioButtonUI();
      localAppContext.put(MOTIF_RADIO_BUTTON_UI_KEY, localMotifRadioButtonUI);
    }
    return localMotifRadioButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!defaults_initialized)
    {
      focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
      defaults_initialized = true;
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
  
  protected void paintFocus(Graphics paramGraphics, Rectangle paramRectangle, Dimension paramDimension)
  {
    paramGraphics.setColor(getFocusColor());
    paramGraphics.drawRect(0, 0, width - 1, height - 1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */