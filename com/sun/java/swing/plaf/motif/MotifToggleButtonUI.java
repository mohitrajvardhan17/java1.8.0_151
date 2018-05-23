package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import sun.awt.AppContext;

public class MotifToggleButtonUI
  extends BasicToggleButtonUI
{
  private static final Object MOTIF_TOGGLE_BUTTON_UI_KEY = new Object();
  protected Color selectColor;
  private boolean defaults_initialized = false;
  
  public MotifToggleButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MotifToggleButtonUI localMotifToggleButtonUI = (MotifToggleButtonUI)localAppContext.get(MOTIF_TOGGLE_BUTTON_UI_KEY);
    if (localMotifToggleButtonUI == null)
    {
      localMotifToggleButtonUI = new MotifToggleButtonUI();
      localAppContext.put(MOTIF_TOGGLE_BUTTON_UI_KEY, localMotifToggleButtonUI);
    }
    return localMotifToggleButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!defaults_initialized)
    {
      selectColor = UIManager.getColor(getPropertyPrefix() + "select");
      defaults_initialized = true;
    }
    LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.FALSE);
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    defaults_initialized = false;
  }
  
  protected Color getSelectColor()
  {
    return selectColor;
  }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton)
  {
    if (paramAbstractButton.isContentAreaFilled())
    {
      Color localColor = paramGraphics.getColor();
      Dimension localDimension = paramAbstractButton.getSize();
      Insets localInsets1 = paramAbstractButton.getInsets();
      Insets localInsets2 = paramAbstractButton.getMargin();
      if ((paramAbstractButton.getBackground() instanceof UIResource)) {
        paramGraphics.setColor(getSelectColor());
      }
      paramGraphics.fillRect(left - left, top - top, width - (left - left) - (right - right), height - (top - top) - (bottom - bottom));
      paramGraphics.setColor(localColor);
    }
  }
  
  public Insets getInsets(JComponent paramJComponent)
  {
    Border localBorder = paramJComponent.getBorder();
    Insets localInsets = localBorder != null ? localBorder.getBorderInsets(paramJComponent) : new Insets(0, 0, 0, 0);
    return localInsets;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */