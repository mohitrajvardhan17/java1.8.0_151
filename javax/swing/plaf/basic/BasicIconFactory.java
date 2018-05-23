package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;

public class BasicIconFactory
  implements Serializable
{
  private static Icon frame_icon;
  private static Icon checkBoxIcon;
  private static Icon radioButtonIcon;
  private static Icon checkBoxMenuItemIcon;
  private static Icon radioButtonMenuItemIcon;
  private static Icon menuItemCheckIcon;
  private static Icon menuItemArrowIcon;
  private static Icon menuArrowIcon;
  
  public BasicIconFactory() {}
  
  public static Icon getMenuItemCheckIcon()
  {
    if (menuItemCheckIcon == null) {
      menuItemCheckIcon = new MenuItemCheckIcon(null);
    }
    return menuItemCheckIcon;
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
  
  public static Icon getCheckBoxMenuItemIcon()
  {
    if (checkBoxMenuItemIcon == null) {
      checkBoxMenuItemIcon = new CheckBoxMenuItemIcon(null);
    }
    return checkBoxMenuItemIcon;
  }
  
  public static Icon getRadioButtonMenuItemIcon()
  {
    if (radioButtonMenuItemIcon == null) {
      radioButtonMenuItemIcon = new RadioButtonMenuItemIcon(null);
    }
    return radioButtonMenuItemIcon;
  }
  
  public static Icon createEmptyFrameIcon()
  {
    if (frame_icon == null) {
      frame_icon = new EmptyFrameIcon(null);
    }
    return frame_icon;
  }
  
  private static class CheckBoxIcon
    implements Icon, Serializable
  {
    static final int csize = 13;
    
    private CheckBoxIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return 13;
    }
    
    public int getIconHeight()
    {
      return 13;
    }
  }
  
  private static class CheckBoxMenuItemIcon
    implements Icon, UIResource, Serializable
  {
    private CheckBoxMenuItemIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      boolean bool = localButtonModel.isSelected();
      if (bool)
      {
        paramGraphics.drawLine(paramInt1 + 7, paramInt2 + 1, paramInt1 + 7, paramInt2 + 3);
        paramGraphics.drawLine(paramInt1 + 6, paramInt2 + 2, paramInt1 + 6, paramInt2 + 4);
        paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 3, paramInt1 + 5, paramInt2 + 5);
        paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 4, paramInt1 + 4, paramInt2 + 6);
        paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 5, paramInt1 + 3, paramInt2 + 7);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 4, paramInt1 + 2, paramInt2 + 6);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 3, paramInt1 + 1, paramInt2 + 5);
      }
    }
    
    public int getIconWidth()
    {
      return 9;
    }
    
    public int getIconHeight()
    {
      return 9;
    }
  }
  
  private static class EmptyFrameIcon
    implements Icon, Serializable
  {
    int height = 16;
    int width = 14;
    
    private EmptyFrameIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return width;
    }
    
    public int getIconHeight()
    {
      return height;
    }
  }
  
  private static class MenuArrowIcon
    implements Icon, UIResource, Serializable
  {
    private MenuArrowIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      Polygon localPolygon = new Polygon();
      localPolygon.addPoint(paramInt1, paramInt2);
      localPolygon.addPoint(paramInt1 + getIconWidth(), paramInt2 + getIconHeight() / 2);
      localPolygon.addPoint(paramInt1, paramInt2 + getIconHeight());
      paramGraphics.fillPolygon(localPolygon);
    }
    
    public int getIconWidth()
    {
      return 4;
    }
    
    public int getIconHeight()
    {
      return 8;
    }
  }
  
  private static class MenuItemArrowIcon
    implements Icon, UIResource, Serializable
  {
    private MenuItemArrowIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return 4;
    }
    
    public int getIconHeight()
    {
      return 8;
    }
  }
  
  private static class MenuItemCheckIcon
    implements Icon, UIResource, Serializable
  {
    private MenuItemCheckIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return 9;
    }
    
    public int getIconHeight()
    {
      return 9;
    }
  }
  
  private static class RadioButtonIcon
    implements Icon, UIResource, Serializable
  {
    private RadioButtonIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return 13;
    }
    
    public int getIconHeight()
    {
      return 13;
    }
  }
  
  private static class RadioButtonMenuItemIcon
    implements Icon, UIResource, Serializable
  {
    private RadioButtonMenuItemIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      if (localAbstractButton.isSelected() == true) {
        paramGraphics.fillOval(paramInt1 + 1, paramInt2 + 1, getIconWidth(), getIconHeight());
      }
    }
    
    public int getIconWidth()
    {
      return 6;
    }
    
    public int getIconHeight()
    {
      return 6;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */