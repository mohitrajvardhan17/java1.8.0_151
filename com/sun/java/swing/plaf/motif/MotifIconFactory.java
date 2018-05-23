package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class MotifIconFactory
  implements Serializable
{
  private static Icon checkBoxIcon;
  private static Icon radioButtonIcon;
  private static Icon menuItemCheckIcon;
  private static Icon menuItemArrowIcon;
  private static Icon menuArrowIcon;
  
  public MotifIconFactory() {}
  
  public static Icon getMenuItemCheckIcon()
  {
    return null;
  }
  
  public static Icon getMenuItemArrowIcon()
  {
    if (menuItemArrowIcon == null) {
      menuItemArrowIcon = new MenuItemArrowIcon(null);
    }
    return menuItemArrowIcon;
  }
  
  public static Icon getMenuArrowIcon()
  {
    if (menuArrowIcon == null) {
      menuArrowIcon = new MenuArrowIcon(null);
    }
    return menuArrowIcon;
  }
  
  public static Icon getCheckBoxIcon()
  {
    if (checkBoxIcon == null) {
      checkBoxIcon = new CheckBoxIcon(null);
    }
    return checkBoxIcon;
  }
  
  public static Icon getRadioButtonIcon()
  {
    if (radioButtonIcon == null) {
      radioButtonIcon = new RadioButtonIcon(null);
    }
    return radioButtonIcon;
  }
  
  private static class CheckBoxIcon
    implements Icon, UIResource, Serializable
  {
    static final int csize = 13;
    private Color control = UIManager.getColor("control");
    private Color foreground = UIManager.getColor("CheckBox.foreground");
    private Color shadow = UIManager.getColor("controlShadow");
    private Color highlight = UIManager.getColor("controlHighlight");
    private Color lightShadow = UIManager.getColor("controlLightShadow");
    
    private CheckBoxIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      boolean bool1 = false;
      if ((localAbstractButton instanceof JCheckBox)) {
        bool1 = ((JCheckBox)localAbstractButton).isBorderPaintedFlat();
      }
      boolean bool2 = localButtonModel.isPressed();
      boolean bool3 = localButtonModel.isArmed();
      boolean bool4 = localButtonModel.isEnabled();
      boolean bool5 = localButtonModel.isSelected();
      int i = ((bool2) && (!bool3) && (bool5)) || ((bool2) && (bool3) && (!bool5)) ? 1 : 0;
      int j = ((bool2) && (!bool3) && (!bool5)) || ((bool2) && (bool3) && (bool5)) ? 1 : 0;
      int k = ((!bool2) && (bool3) && (bool5)) || ((!bool2) && (!bool3) && (bool5)) ? 1 : 0;
      if (bool1)
      {
        paramGraphics.setColor(shadow);
        paramGraphics.drawRect(paramInt1 + 2, paramInt2, 12, 12);
        if ((j != 0) || (i != 0))
        {
          paramGraphics.setColor(control);
          paramGraphics.fillRect(paramInt1 + 3, paramInt2 + 1, 11, 11);
        }
      }
      if (i != 0) {
        drawCheckBezel(paramGraphics, paramInt1, paramInt2, 13, true, false, false, bool1);
      } else if (j != 0) {
        drawCheckBezel(paramGraphics, paramInt1, paramInt2, 13, true, true, false, bool1);
      } else if (k != 0) {
        drawCheckBezel(paramGraphics, paramInt1, paramInt2, 13, false, false, true, bool1);
      } else if (!bool1) {
        drawCheckBezelOut(paramGraphics, paramInt1, paramInt2, 13);
      }
    }
    
    public int getIconWidth()
    {
      return 13;
    }
    
    public int getIconHeight()
    {
      return 13;
    }
    
    public void drawCheckBezelOut(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
    {
      Color localColor1 = UIManager.getColor("controlShadow");
      int i = paramInt3;
      int j = paramInt3;
      Color localColor2 = paramGraphics.getColor();
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(highlight);
      paramGraphics.drawLine(0, 0, 0, j - 1);
      paramGraphics.drawLine(1, 0, i - 1, 0);
      paramGraphics.setColor(shadow);
      paramGraphics.drawLine(1, j - 1, i - 1, j - 1);
      paramGraphics.drawLine(i - 1, j - 1, i - 1, 1);
      paramGraphics.translate(-paramInt1, -paramInt2);
      paramGraphics.setColor(localColor2);
    }
    
    public void drawCheckBezel(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    {
      Color localColor = paramGraphics.getColor();
      paramGraphics.translate(paramInt1, paramInt2);
      if (!paramBoolean4)
      {
        if (paramBoolean2)
        {
          paramGraphics.setColor(control);
          paramGraphics.fillRect(1, 1, paramInt3 - 2, paramInt3 - 2);
          paramGraphics.setColor(shadow);
        }
        else
        {
          paramGraphics.setColor(lightShadow);
          paramGraphics.fillRect(0, 0, paramInt3, paramInt3);
          paramGraphics.setColor(highlight);
        }
        paramGraphics.drawLine(1, paramInt3 - 1, paramInt3 - 2, paramInt3 - 1);
        if (paramBoolean1)
        {
          paramGraphics.drawLine(2, paramInt3 - 2, paramInt3 - 3, paramInt3 - 2);
          paramGraphics.drawLine(paramInt3 - 2, 2, paramInt3 - 2, paramInt3 - 1);
          if (paramBoolean2) {
            paramGraphics.setColor(highlight);
          } else {
            paramGraphics.setColor(shadow);
          }
          paramGraphics.drawLine(1, 2, 1, paramInt3 - 2);
          paramGraphics.drawLine(1, 1, paramInt3 - 3, 1);
          if (paramBoolean2) {
            paramGraphics.setColor(shadow);
          } else {
            paramGraphics.setColor(highlight);
          }
        }
        paramGraphics.drawLine(paramInt3 - 1, 1, paramInt3 - 1, paramInt3 - 1);
        if (paramBoolean2) {
          paramGraphics.setColor(highlight);
        } else {
          paramGraphics.setColor(shadow);
        }
        paramGraphics.drawLine(0, 1, 0, paramInt3 - 1);
        paramGraphics.drawLine(0, 0, paramInt3 - 1, 0);
      }
      if (paramBoolean3)
      {
        paramGraphics.setColor(foreground);
        paramGraphics.drawLine(paramInt3 - 2, 1, paramInt3 - 2, 2);
        paramGraphics.drawLine(paramInt3 - 3, 2, paramInt3 - 3, 3);
        paramGraphics.drawLine(paramInt3 - 4, 3, paramInt3 - 4, 4);
        paramGraphics.drawLine(paramInt3 - 5, 4, paramInt3 - 5, 6);
        paramGraphics.drawLine(paramInt3 - 6, 5, paramInt3 - 6, 8);
        paramGraphics.drawLine(paramInt3 - 7, 6, paramInt3 - 7, 10);
        paramGraphics.drawLine(paramInt3 - 8, 7, paramInt3 - 8, 10);
        paramGraphics.drawLine(paramInt3 - 9, 6, paramInt3 - 9, 9);
        paramGraphics.drawLine(paramInt3 - 10, 5, paramInt3 - 10, 8);
        paramGraphics.drawLine(paramInt3 - 11, 5, paramInt3 - 11, 7);
        paramGraphics.drawLine(paramInt3 - 12, 6, paramInt3 - 12, 6);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
      paramGraphics.setColor(localColor);
    }
  }
  
  private static class MenuArrowIcon
    implements Icon, UIResource, Serializable
  {
    private Color focus = UIManager.getColor("windowBorder");
    private Color shadow = UIManager.getColor("controlShadow");
    private Color highlight = UIManager.getColor("controlHighlight");
    
    private MenuArrowIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      int i = getIconWidth();
      int j = getIconHeight();
      Color localColor = paramGraphics.getColor();
      if (localButtonModel.isSelected())
      {
        if (MotifGraphicsUtils.isLeftToRight(paramComponent))
        {
          paramGraphics.setColor(shadow);
          paramGraphics.fillRect(paramInt1 + 1, paramInt2 + 1, 2, j);
          paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 2, paramInt1 + 4, paramInt2 + 2);
          paramGraphics.drawLine(paramInt1 + 6, paramInt2 + 3, paramInt1 + 6, paramInt2 + 3);
          paramGraphics.drawLine(paramInt1 + 8, paramInt2 + 4, paramInt1 + 8, paramInt2 + 5);
          paramGraphics.setColor(focus);
          paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, 2, j - 2);
          paramGraphics.fillRect(paramInt1 + 4, paramInt2 + 3, 2, j - 4);
          paramGraphics.fillRect(paramInt1 + 6, paramInt2 + 4, 2, j - 6);
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(paramInt1 + 2, paramInt2 + j, paramInt1 + 2, paramInt2 + j);
          paramGraphics.drawLine(paramInt1 + 4, paramInt2 + j - 1, paramInt1 + 4, paramInt2 + j - 1);
          paramGraphics.drawLine(paramInt1 + 6, paramInt2 + j - 2, paramInt1 + 6, paramInt2 + j - 2);
          paramGraphics.drawLine(paramInt1 + 8, paramInt2 + j - 4, paramInt1 + 8, paramInt2 + j - 3);
        }
        else
        {
          paramGraphics.setColor(highlight);
          paramGraphics.fillRect(paramInt1 + 7, paramInt2 + 1, 2, 10);
          paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 9, paramInt1 + 5, paramInt2 + 9);
          paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 8, paramInt1 + 3, paramInt2 + 8);
          paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 6, paramInt1 + 1, paramInt2 + 7);
          paramGraphics.setColor(focus);
          paramGraphics.fillRect(paramInt1 + 6, paramInt2 + 2, 2, 8);
          paramGraphics.fillRect(paramInt1 + 4, paramInt2 + 3, 2, 6);
          paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 4, 2, 4);
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 4, paramInt1 + 1, paramInt2 + 5);
          paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 3, paramInt1 + 3, paramInt2 + 3);
          paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 2, paramInt1 + 5, paramInt2 + 2);
          paramGraphics.drawLine(paramInt1 + 7, paramInt2 + 1, paramInt1 + 7, paramInt2 + 1);
        }
      }
      else if (MotifGraphicsUtils.isLeftToRight(paramComponent))
      {
        paramGraphics.setColor(highlight);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 1, paramInt1 + 1, paramInt2 + j);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 1, paramInt1 + 2, paramInt2 + j - 2);
        paramGraphics.fillRect(paramInt1 + 3, paramInt2 + 2, 2, 2);
        paramGraphics.fillRect(paramInt1 + 5, paramInt2 + 3, 2, 2);
        paramGraphics.fillRect(paramInt1 + 7, paramInt2 + 4, 2, 2);
        paramGraphics.setColor(shadow);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + j - 1, paramInt1 + 2, paramInt2 + j);
        paramGraphics.fillRect(paramInt1 + 3, paramInt2 + j - 2, 2, 2);
        paramGraphics.fillRect(paramInt1 + 5, paramInt2 + j - 3, 2, 2);
        paramGraphics.fillRect(paramInt1 + 7, paramInt2 + j - 4, 2, 2);
        paramGraphics.setColor(localColor);
      }
      else
      {
        paramGraphics.setColor(highlight);
        paramGraphics.fillRect(paramInt1 + 1, paramInt2 + 4, 2, 2);
        paramGraphics.fillRect(paramInt1 + 3, paramInt2 + 3, 2, 2);
        paramGraphics.fillRect(paramInt1 + 5, paramInt2 + 2, 2, 2);
        paramGraphics.drawLine(paramInt1 + 7, paramInt2 + 1, paramInt1 + 7, paramInt2 + 2);
        paramGraphics.setColor(shadow);
        paramGraphics.fillRect(paramInt1 + 1, paramInt2 + j - 4, 2, 2);
        paramGraphics.fillRect(paramInt1 + 3, paramInt2 + j - 3, 2, 2);
        paramGraphics.fillRect(paramInt1 + 5, paramInt2 + j - 2, 2, 2);
        paramGraphics.drawLine(paramInt1 + 7, paramInt2 + 3, paramInt1 + 7, paramInt2 + j);
        paramGraphics.drawLine(paramInt1 + 8, paramInt2 + 1, paramInt1 + 8, paramInt2 + j);
        paramGraphics.setColor(localColor);
      }
    }
    
    public int getIconWidth()
    {
      return 10;
    }
    
    public int getIconHeight()
    {
      return 10;
    }
  }
  
  private static class MenuItemArrowIcon
    implements Icon, UIResource, Serializable
  {
    private MenuItemArrowIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return 0;
    }
    
    public int getIconHeight()
    {
      return 0;
    }
  }
  
  private static class MenuItemCheckIcon
    implements Icon, UIResource, Serializable
  {
    private MenuItemCheckIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return 0;
    }
    
    public int getIconHeight()
    {
      return 0;
    }
  }
  
  private static class RadioButtonIcon
    implements Icon, UIResource, Serializable
  {
    private Color dot = UIManager.getColor("activeCaptionBorder");
    private Color highlight = UIManager.getColor("controlHighlight");
    private Color shadow = UIManager.getColor("controlShadow");
    
    private RadioButtonIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      int i = getIconWidth();
      int j = getIconHeight();
      boolean bool1 = localButtonModel.isPressed();
      boolean bool2 = localButtonModel.isArmed();
      boolean bool3 = localButtonModel.isEnabled();
      boolean bool4 = localButtonModel.isSelected();
      int k = ((bool1) && (!bool2) && (bool4)) || ((bool1) && (bool2) && (!bool4)) || ((!bool1) && (bool2) && (bool4)) || ((!bool1) && (!bool2) && (bool4)) ? 1 : 0;
      if (k != 0)
      {
        paramGraphics.setColor(shadow);
        paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 0, paramInt1 + 8, paramInt2 + 0);
        paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 1, paramInt1 + 4, paramInt2 + 1);
        paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 1, paramInt1 + 9, paramInt2 + 1);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 2, paramInt1 + 2, paramInt2 + 2);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 3, paramInt1 + 1, paramInt2 + 3);
        paramGraphics.drawLine(paramInt1, paramInt2 + 4, paramInt1, paramInt2 + 9);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 10, paramInt1 + 1, paramInt2 + 10);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 11, paramInt1 + 2, paramInt2 + 11);
        paramGraphics.setColor(highlight);
        paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 12, paramInt1 + 4, paramInt2 + 12);
        paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 13, paramInt1 + 8, paramInt2 + 13);
        paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 12, paramInt1 + 10, paramInt2 + 12);
        paramGraphics.drawLine(paramInt1 + 11, paramInt2 + 11, paramInt1 + 11, paramInt2 + 11);
        paramGraphics.drawLine(paramInt1 + 12, paramInt2 + 10, paramInt1 + 12, paramInt2 + 10);
        paramGraphics.drawLine(paramInt1 + 13, paramInt2 + 9, paramInt1 + 13, paramInt2 + 4);
        paramGraphics.drawLine(paramInt1 + 12, paramInt2 + 3, paramInt1 + 12, paramInt2 + 3);
        paramGraphics.drawLine(paramInt1 + 11, paramInt2 + 2, paramInt1 + 11, paramInt2 + 2);
        paramGraphics.drawLine(paramInt1 + 10, paramInt2 + 1, paramInt1 + 10, paramInt2 + 1);
        paramGraphics.setColor(dot);
        paramGraphics.fillRect(paramInt1 + 4, paramInt2 + 5, 6, 4);
        paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 4, paramInt1 + 8, paramInt2 + 4);
        paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 9, paramInt1 + 8, paramInt2 + 9);
      }
      else
      {
        paramGraphics.setColor(highlight);
        paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 0, paramInt1 + 8, paramInt2 + 0);
        paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 1, paramInt1 + 4, paramInt2 + 1);
        paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 1, paramInt1 + 9, paramInt2 + 1);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 2, paramInt1 + 2, paramInt2 + 2);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 3, paramInt1 + 1, paramInt2 + 3);
        paramGraphics.drawLine(paramInt1, paramInt2 + 4, paramInt1, paramInt2 + 9);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 10, paramInt1 + 1, paramInt2 + 10);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 11, paramInt1 + 2, paramInt2 + 11);
        paramGraphics.setColor(shadow);
        paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 12, paramInt1 + 4, paramInt2 + 12);
        paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 13, paramInt1 + 8, paramInt2 + 13);
        paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 12, paramInt1 + 10, paramInt2 + 12);
        paramGraphics.drawLine(paramInt1 + 11, paramInt2 + 11, paramInt1 + 11, paramInt2 + 11);
        paramGraphics.drawLine(paramInt1 + 12, paramInt2 + 10, paramInt1 + 12, paramInt2 + 10);
        paramGraphics.drawLine(paramInt1 + 13, paramInt2 + 9, paramInt1 + 13, paramInt2 + 4);
        paramGraphics.drawLine(paramInt1 + 12, paramInt2 + 3, paramInt1 + 12, paramInt2 + 3);
        paramGraphics.drawLine(paramInt1 + 11, paramInt2 + 2, paramInt1 + 11, paramInt2 + 2);
        paramGraphics.drawLine(paramInt1 + 10, paramInt2 + 1, paramInt1 + 10, paramInt2 + 1);
      }
    }
    
    public int getIconWidth()
    {
      return 14;
    }
    
    public int getIconHeight()
    {
      return 14;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */