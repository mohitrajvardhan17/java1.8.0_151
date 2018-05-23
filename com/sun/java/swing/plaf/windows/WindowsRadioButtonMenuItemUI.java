package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

public class WindowsRadioButtonMenuItemUI
  extends BasicRadioButtonMenuItemUI
{
  final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor()
  {
    public JMenuItem getMenuItem()
    {
      return menuItem;
    }
    
    public TMSchema.State getState(JMenuItem paramAnonymousJMenuItem)
    {
      return WindowsMenuItemUI.getState(this, paramAnonymousJMenuItem);
    }
    
    public TMSchema.Part getPart(JMenuItem paramAnonymousJMenuItem)
    {
      return WindowsMenuItemUI.getPart(this, paramAnonymousJMenuItem);
    }
  };
  
  public WindowsRadioButtonMenuItemUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsRadioButtonMenuItemUI();
  }
  
  protected void paintBackground(Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor)
  {
    if (WindowsMenuItemUI.isVistaPainting())
    {
      WindowsMenuItemUI.paintBackground(accessor, paramGraphics, paramJMenuItem, paramColor);
      return;
    }
    super.paintBackground(paramGraphics, paramJMenuItem, paramColor);
  }
  
  protected void paintText(Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString)
  {
    if (WindowsMenuItemUI.isVistaPainting())
    {
      WindowsMenuItemUI.paintText(accessor, paramGraphics, paramJMenuItem, paramRectangle, paramString);
      return;
    }
    ButtonModel localButtonModel = paramJMenuItem.getModel();
    Color localColor = paramGraphics.getColor();
    if ((localButtonModel.isEnabled()) && (localButtonModel.isArmed())) {
      paramGraphics.setColor(selectionForeground);
    }
    WindowsGraphicsUtils.paintText(paramGraphics, paramJMenuItem, paramRectangle, paramString, 0);
    paramGraphics.setColor(localColor);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsRadioButtonMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */