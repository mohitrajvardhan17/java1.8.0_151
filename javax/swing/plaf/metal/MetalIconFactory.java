package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import sun.swing.CachedPainter;

public class MetalIconFactory
  implements Serializable
{
  private static Icon fileChooserDetailViewIcon;
  private static Icon fileChooserHomeFolderIcon;
  private static Icon fileChooserListViewIcon;
  private static Icon fileChooserNewFolderIcon;
  private static Icon fileChooserUpFolderIcon;
  private static Icon internalFrameAltMaximizeIcon;
  private static Icon internalFrameCloseIcon;
  private static Icon internalFrameDefaultMenuIcon;
  private static Icon internalFrameMaximizeIcon;
  private static Icon internalFrameMinimizeIcon;
  private static Icon radioButtonIcon;
  private static Icon treeComputerIcon;
  private static Icon treeFloppyDriveIcon;
  private static Icon treeHardDriveIcon;
  private static Icon menuArrowIcon;
  private static Icon menuItemArrowIcon;
  private static Icon checkBoxMenuItemIcon;
  private static Icon radioButtonMenuItemIcon;
  private static Icon checkBoxIcon;
  private static Icon oceanHorizontalSliderThumb;
  private static Icon oceanVerticalSliderThumb;
  public static final boolean DARK = false;
  public static final boolean LIGHT = true;
  private static final Dimension folderIcon16Size = new Dimension(16, 16);
  private static final Dimension fileIcon16Size = new Dimension(16, 16);
  private static final Dimension treeControlSize = new Dimension(18, 18);
  private static final Dimension menuArrowIconSize = new Dimension(4, 8);
  private static final Dimension menuCheckIconSize = new Dimension(10, 10);
  private static final int xOff = 4;
  
  public MetalIconFactory() {}
  
  public static Icon getFileChooserDetailViewIcon()
  {
    if (fileChooserDetailViewIcon == null) {
      fileChooserDetailViewIcon = new FileChooserDetailViewIcon(null);
    }
    return fileChooserDetailViewIcon;
  }
  
  public static Icon getFileChooserHomeFolderIcon()
  {
    if (fileChooserHomeFolderIcon == null) {
      fileChooserHomeFolderIcon = new FileChooserHomeFolderIcon(null);
    }
    return fileChooserHomeFolderIcon;
  }
  
  public static Icon getFileChooserListViewIcon()
  {
    if (fileChooserListViewIcon == null) {
      fileChooserListViewIcon = new FileChooserListViewIcon(null);
    }
    return fileChooserListViewIcon;
  }
  
  public static Icon getFileChooserNewFolderIcon()
  {
    if (fileChooserNewFolderIcon == null) {
      fileChooserNewFolderIcon = new FileChooserNewFolderIcon(null);
    }
    return fileChooserNewFolderIcon;
  }
  
  public static Icon getFileChooserUpFolderIcon()
  {
    if (fileChooserUpFolderIcon == null) {
      fileChooserUpFolderIcon = new FileChooserUpFolderIcon(null);
    }
    return fileChooserUpFolderIcon;
  }
  
  public static Icon getInternalFrameAltMaximizeIcon(int paramInt)
  {
    return new InternalFrameAltMaximizeIcon(paramInt);
  }
  
  public static Icon getInternalFrameCloseIcon(int paramInt)
  {
    return new InternalFrameCloseIcon(paramInt);
  }
  
  public static Icon getInternalFrameDefaultMenuIcon()
  {
    if (internalFrameDefaultMenuIcon == null) {
      internalFrameDefaultMenuIcon = new InternalFrameDefaultMenuIcon(null);
    }
    return internalFrameDefaultMenuIcon;
  }
  
  public static Icon getInternalFrameMaximizeIcon(int paramInt)
  {
    return new InternalFrameMaximizeIcon(paramInt);
  }
  
  public static Icon getInternalFrameMinimizeIcon(int paramInt)
  {
    return new InternalFrameMinimizeIcon(paramInt);
  }
  
  public static Icon getRadioButtonIcon()
  {
    if (radioButtonIcon == null) {
      radioButtonIcon = new RadioButtonIcon(null);
    }
    return radioButtonIcon;
  }
  
  public static Icon getCheckBoxIcon()
  {
    if (checkBoxIcon == null) {
      checkBoxIcon = new CheckBoxIcon(null);
    }
    return checkBoxIcon;
  }
  
  public static Icon getTreeComputerIcon()
  {
    if (treeComputerIcon == null) {
      treeComputerIcon = new TreeComputerIcon(null);
    }
    return treeComputerIcon;
  }
  
  public static Icon getTreeFloppyDriveIcon()
  {
    if (treeFloppyDriveIcon == null) {
      treeFloppyDriveIcon = new TreeFloppyDriveIcon(null);
    }
    return treeFloppyDriveIcon;
  }
  
  public static Icon getTreeFolderIcon()
  {
    return new TreeFolderIcon();
  }
  
  public static Icon getTreeHardDriveIcon()
  {
    if (treeHardDriveIcon == null) {
      treeHardDriveIcon = new TreeHardDriveIcon(null);
    }
    return treeHardDriveIcon;
  }
  
  public static Icon getTreeLeafIcon()
  {
    return new TreeLeafIcon();
  }
  
  public static Icon getTreeControlIcon(boolean paramBoolean)
  {
    return new TreeControlIcon(paramBoolean);
  }
  
  public static Icon getMenuArrowIcon()
  {
    if (menuArrowIcon == null) {
      menuArrowIcon = new MenuArrowIcon(null);
    }
    return menuArrowIcon;
  }
  
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
  
  public static Icon getHorizontalSliderThumbIcon()
  {
    if (MetalLookAndFeel.usingOcean())
    {
      if (oceanHorizontalSliderThumb == null) {
        oceanHorizontalSliderThumb = new OceanHorizontalSliderThumbIcon();
      }
      return oceanHorizontalSliderThumb;
    }
    return new HorizontalSliderThumbIcon();
  }
  
  public static Icon getVerticalSliderThumbIcon()
  {
    if (MetalLookAndFeel.usingOcean())
    {
      if (oceanVerticalSliderThumb == null) {
        oceanVerticalSliderThumb = new OceanVerticalSliderThumbIcon();
      }
      return oceanVerticalSliderThumb;
    }
    return new VerticalSliderThumbIcon();
  }
  
  private static class CheckBoxIcon
    implements Icon, UIResource, Serializable
  {
    private CheckBoxIcon() {}
    
    protected int getControlSize()
    {
      return 13;
    }
    
    private void paintOceanIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      ButtonModel localButtonModel = ((JCheckBox)paramComponent).getModel();
      paramGraphics.translate(paramInt1, paramInt2);
      int i = getIconWidth();
      int j = getIconHeight();
      if (localButtonModel.isEnabled())
      {
        if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
          paramGraphics.fillRect(0, 0, i, j);
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.fillRect(0, 0, i, 2);
          paramGraphics.fillRect(0, 2, 2, j - 2);
          paramGraphics.fillRect(i - 1, 1, 1, j - 1);
          paramGraphics.fillRect(1, j - 1, i - 2, 1);
        }
        else if (localButtonModel.isRollover())
        {
          MetalUtils.drawGradient(paramComponent, paramGraphics, "CheckBox.gradient", 0, 0, i, j, true);
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawRect(0, 0, i - 1, j - 1);
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
          paramGraphics.drawRect(1, 1, i - 3, j - 3);
          paramGraphics.drawRect(2, 2, i - 5, j - 5);
        }
        else
        {
          MetalUtils.drawGradient(paramComponent, paramGraphics, "CheckBox.gradient", 0, 0, i, j, true);
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawRect(0, 0, i - 1, j - 1);
        }
        paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        paramGraphics.drawRect(0, 0, i - 1, j - 1);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
      if (localButtonModel.isSelected()) {
        drawCheck(paramComponent, paramGraphics, paramInt1, paramInt2);
      }
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      if (MetalLookAndFeel.usingOcean())
      {
        paintOceanIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
        return;
      }
      ButtonModel localButtonModel = ((JCheckBox)paramComponent).getModel();
      int i = getControlSize();
      if (localButtonModel.isEnabled())
      {
        if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
          paramGraphics.fillRect(paramInt1, paramInt2, i - 1, i - 1);
          MetalUtils.drawPressed3DBorder(paramGraphics, paramInt1, paramInt2, i, i);
        }
        else
        {
          MetalUtils.drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, i, i);
        }
        paramGraphics.setColor(paramComponent.getForeground());
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
        paramGraphics.drawRect(paramInt1, paramInt2, i - 2, i - 2);
      }
      if (localButtonModel.isSelected()) {
        drawCheck(paramComponent, paramGraphics, paramInt1, paramInt2);
      }
    }
    
    protected void drawCheck(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      int i = getControlSize();
      paramGraphics.fillRect(paramInt1 + 3, paramInt2 + 5, 2, i - 8);
      paramGraphics.drawLine(paramInt1 + (i - 4), paramInt2 + 3, paramInt1 + 5, paramInt2 + (i - 6));
      paramGraphics.drawLine(paramInt1 + (i - 4), paramInt2 + 4, paramInt1 + 5, paramInt2 + (i - 5));
    }
    
    public int getIconWidth()
    {
      return getControlSize();
    }
    
    public int getIconHeight()
    {
      return getControlSize();
    }
  }
  
  private static class CheckBoxMenuItemIcon
    implements Icon, UIResource, Serializable
  {
    private CheckBoxMenuItemIcon() {}
    
    public void paintOceanIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      ButtonModel localButtonModel = ((JMenuItem)paramComponent).getModel();
      boolean bool1 = localButtonModel.isSelected();
      boolean bool2 = localButtonModel.isEnabled();
      boolean bool3 = localButtonModel.isPressed();
      boolean bool4 = localButtonModel.isArmed();
      paramGraphics.translate(paramInt1, paramInt2);
      if (bool2)
      {
        MetalUtils.drawGradient(paramComponent, paramGraphics, "CheckBoxMenuItem.gradient", 1, 1, 7, 7, true);
        if ((bool3) || (bool4))
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
          paramGraphics.drawLine(0, 0, 8, 0);
          paramGraphics.drawLine(0, 0, 0, 8);
          paramGraphics.drawLine(8, 2, 8, 8);
          paramGraphics.drawLine(2, 8, 8, 8);
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
          paramGraphics.drawLine(9, 1, 9, 9);
          paramGraphics.drawLine(1, 9, 9, 9);
        }
        else
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawLine(0, 0, 8, 0);
          paramGraphics.drawLine(0, 0, 0, 8);
          paramGraphics.drawLine(8, 2, 8, 8);
          paramGraphics.drawLine(2, 8, 8, 8);
          paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
          paramGraphics.drawLine(9, 1, 9, 9);
          paramGraphics.drawLine(1, 9, 9, 9);
        }
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        paramGraphics.drawRect(0, 0, 8, 8);
      }
      if (bool1)
      {
        if (bool2)
        {
          if ((bool4) || (((paramComponent instanceof JMenu)) && (bool1))) {
            paramGraphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
          } else {
            paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
          }
        }
        else {
          paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        }
        paramGraphics.drawLine(2, 2, 2, 6);
        paramGraphics.drawLine(3, 2, 3, 6);
        paramGraphics.drawLine(4, 4, 8, 0);
        paramGraphics.drawLine(4, 5, 9, 0);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      if (MetalLookAndFeel.usingOcean())
      {
        paintOceanIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
        return;
      }
      JMenuItem localJMenuItem = (JMenuItem)paramComponent;
      ButtonModel localButtonModel = localJMenuItem.getModel();
      boolean bool1 = localButtonModel.isSelected();
      boolean bool2 = localButtonModel.isEnabled();
      boolean bool3 = localButtonModel.isPressed();
      boolean bool4 = localButtonModel.isArmed();
      paramGraphics.translate(paramInt1, paramInt2);
      if (bool2)
      {
        if ((bool3) || (bool4))
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
          paramGraphics.drawLine(0, 0, 8, 0);
          paramGraphics.drawLine(0, 0, 0, 8);
          paramGraphics.drawLine(8, 2, 8, 8);
          paramGraphics.drawLine(2, 8, 8, 8);
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
          paramGraphics.drawLine(1, 1, 7, 1);
          paramGraphics.drawLine(1, 1, 1, 7);
          paramGraphics.drawLine(9, 1, 9, 9);
          paramGraphics.drawLine(1, 9, 9, 9);
        }
        else
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawLine(0, 0, 8, 0);
          paramGraphics.drawLine(0, 0, 0, 8);
          paramGraphics.drawLine(8, 2, 8, 8);
          paramGraphics.drawLine(2, 8, 8, 8);
          paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
          paramGraphics.drawLine(1, 1, 7, 1);
          paramGraphics.drawLine(1, 1, 1, 7);
          paramGraphics.drawLine(9, 1, 9, 9);
          paramGraphics.drawLine(1, 9, 9, 9);
        }
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        paramGraphics.drawRect(0, 0, 8, 8);
      }
      if (bool1)
      {
        if (bool2)
        {
          if ((localButtonModel.isArmed()) || (((paramComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
            paramGraphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
          } else {
            paramGraphics.setColor(localJMenuItem.getForeground());
          }
        }
        else {
          paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        }
        paramGraphics.drawLine(2, 2, 2, 6);
        paramGraphics.drawLine(3, 2, 3, 6);
        paramGraphics.drawLine(4, 4, 8, 0);
        paramGraphics.drawLine(4, 5, 9, 0);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return menuCheckIconSizewidth;
    }
    
    public int getIconHeight()
    {
      return menuCheckIconSizeheight;
    }
  }
  
  private static class FileChooserDetailViewIcon
    implements Icon, UIResource, Serializable
  {
    private FileChooserDetailViewIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(2, 2, 5, 2);
      paramGraphics.drawLine(2, 3, 2, 7);
      paramGraphics.drawLine(3, 7, 6, 7);
      paramGraphics.drawLine(6, 6, 6, 3);
      paramGraphics.drawLine(2, 10, 5, 10);
      paramGraphics.drawLine(2, 11, 2, 15);
      paramGraphics.drawLine(3, 15, 6, 15);
      paramGraphics.drawLine(6, 14, 6, 11);
      paramGraphics.drawLine(8, 5, 15, 5);
      paramGraphics.drawLine(8, 13, 15, 13);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.drawRect(3, 3, 2, 3);
      paramGraphics.drawRect(3, 11, 2, 3);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      paramGraphics.drawLine(4, 4, 4, 5);
      paramGraphics.drawLine(4, 12, 4, 13);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 18;
    }
    
    public int getIconHeight()
    {
      return 18;
    }
  }
  
  private static class FileChooserHomeFolderIcon
    implements Icon, UIResource, Serializable
  {
    private FileChooserHomeFolderIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(8, 1, 1, 8);
      paramGraphics.drawLine(8, 1, 15, 8);
      paramGraphics.drawLine(11, 2, 11, 3);
      paramGraphics.drawLine(12, 2, 12, 4);
      paramGraphics.drawLine(3, 7, 3, 15);
      paramGraphics.drawLine(13, 7, 13, 15);
      paramGraphics.drawLine(4, 15, 12, 15);
      paramGraphics.drawLine(6, 9, 6, 14);
      paramGraphics.drawLine(10, 9, 10, 14);
      paramGraphics.drawLine(7, 9, 9, 9);
      paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      paramGraphics.fillRect(8, 2, 1, 1);
      paramGraphics.fillRect(7, 3, 3, 1);
      paramGraphics.fillRect(6, 4, 5, 1);
      paramGraphics.fillRect(5, 5, 7, 1);
      paramGraphics.fillRect(4, 6, 9, 2);
      paramGraphics.drawLine(9, 12, 9, 12);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.drawLine(4, 8, 12, 8);
      paramGraphics.fillRect(4, 9, 2, 6);
      paramGraphics.fillRect(11, 9, 2, 6);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 18;
    }
    
    public int getIconHeight()
    {
      return 18;
    }
  }
  
  private static class FileChooserListViewIcon
    implements Icon, UIResource, Serializable
  {
    private FileChooserListViewIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(2, 2, 5, 2);
      paramGraphics.drawLine(2, 3, 2, 7);
      paramGraphics.drawLine(3, 7, 6, 7);
      paramGraphics.drawLine(6, 6, 6, 3);
      paramGraphics.drawLine(10, 2, 13, 2);
      paramGraphics.drawLine(10, 3, 10, 7);
      paramGraphics.drawLine(11, 7, 14, 7);
      paramGraphics.drawLine(14, 6, 14, 3);
      paramGraphics.drawLine(2, 10, 5, 10);
      paramGraphics.drawLine(2, 11, 2, 15);
      paramGraphics.drawLine(3, 15, 6, 15);
      paramGraphics.drawLine(6, 14, 6, 11);
      paramGraphics.drawLine(10, 10, 13, 10);
      paramGraphics.drawLine(10, 11, 10, 15);
      paramGraphics.drawLine(11, 15, 14, 15);
      paramGraphics.drawLine(14, 14, 14, 11);
      paramGraphics.drawLine(8, 5, 8, 5);
      paramGraphics.drawLine(16, 5, 16, 5);
      paramGraphics.drawLine(8, 13, 8, 13);
      paramGraphics.drawLine(16, 13, 16, 13);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.drawRect(3, 3, 2, 3);
      paramGraphics.drawRect(11, 3, 2, 3);
      paramGraphics.drawRect(3, 11, 2, 3);
      paramGraphics.drawRect(11, 11, 2, 3);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      paramGraphics.drawLine(4, 4, 4, 5);
      paramGraphics.drawLine(12, 4, 12, 5);
      paramGraphics.drawLine(4, 12, 4, 13);
      paramGraphics.drawLine(12, 12, 12, 13);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 18;
    }
    
    public int getIconHeight()
    {
      return 18;
    }
  }
  
  private static class FileChooserNewFolderIcon
    implements Icon, UIResource, Serializable
  {
    private FileChooserNewFolderIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.fillRect(3, 5, 12, 9);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(1, 6, 1, 14);
      paramGraphics.drawLine(2, 14, 15, 14);
      paramGraphics.drawLine(15, 13, 15, 5);
      paramGraphics.drawLine(2, 5, 9, 5);
      paramGraphics.drawLine(10, 6, 14, 6);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      paramGraphics.drawLine(2, 6, 2, 13);
      paramGraphics.drawLine(3, 6, 9, 6);
      paramGraphics.drawLine(10, 7, 14, 7);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      paramGraphics.drawLine(11, 3, 15, 3);
      paramGraphics.drawLine(10, 4, 15, 4);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 18;
    }
    
    public int getIconHeight()
    {
      return 18;
    }
  }
  
  private static class FileChooserUpFolderIcon
    implements Icon, UIResource, Serializable
  {
    private FileChooserUpFolderIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.fillRect(3, 5, 12, 9);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(1, 6, 1, 14);
      paramGraphics.drawLine(2, 14, 15, 14);
      paramGraphics.drawLine(15, 13, 15, 5);
      paramGraphics.drawLine(2, 5, 9, 5);
      paramGraphics.drawLine(10, 6, 14, 6);
      paramGraphics.drawLine(8, 13, 8, 16);
      paramGraphics.drawLine(8, 9, 8, 9);
      paramGraphics.drawLine(7, 10, 9, 10);
      paramGraphics.drawLine(6, 11, 10, 11);
      paramGraphics.drawLine(5, 12, 11, 12);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      paramGraphics.drawLine(2, 6, 2, 13);
      paramGraphics.drawLine(3, 6, 9, 6);
      paramGraphics.drawLine(10, 7, 14, 7);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      paramGraphics.drawLine(11, 3, 15, 3);
      paramGraphics.drawLine(10, 4, 15, 4);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 18;
    }
    
    public int getIconHeight()
    {
      return 18;
    }
  }
  
  public static class FileIcon16
    implements Icon, Serializable
  {
    MetalIconFactory.ImageCacher imageCacher;
    
    public FileIcon16() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      GraphicsConfiguration localGraphicsConfiguration = paramComponent.getGraphicsConfiguration();
      if (imageCacher == null) {
        imageCacher = new MetalIconFactory.ImageCacher();
      }
      Object localObject = imageCacher.getImage(localGraphicsConfiguration);
      if (localObject == null)
      {
        if (localGraphicsConfiguration != null) {
          localObject = localGraphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), 2);
        } else {
          localObject = new BufferedImage(getIconWidth(), getIconHeight(), 2);
        }
        Graphics localGraphics = ((Image)localObject).getGraphics();
        paintMe(paramComponent, localGraphics);
        localGraphics.dispose();
        imageCacher.cacheImage((Image)localObject, localGraphicsConfiguration);
      }
      paramGraphics.drawImage((Image)localObject, paramInt1, paramInt2 + getShift(), null);
    }
    
    private void paintMe(Component paramComponent, Graphics paramGraphics)
    {
      int i = fileIcon16Sizewidth - 1;
      int j = fileIcon16Sizeheight - 1;
      paramGraphics.setColor(MetalLookAndFeel.getWindowBackground());
      paramGraphics.fillRect(4, 2, 9, 12);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(2, 0, 2, j);
      paramGraphics.drawLine(2, 0, i - 4, 0);
      paramGraphics.drawLine(2, j, i - 1, j);
      paramGraphics.drawLine(i - 1, 6, i - 1, j);
      paramGraphics.drawLine(i - 6, 2, i - 2, 6);
      paramGraphics.drawLine(i - 5, 1, i - 4, 1);
      paramGraphics.drawLine(i - 3, 2, i - 3, 3);
      paramGraphics.drawLine(i - 2, 4, i - 2, 5);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.drawLine(3, 1, 3, j - 1);
      paramGraphics.drawLine(3, 1, i - 6, 1);
      paramGraphics.drawLine(i - 2, 7, i - 2, j - 1);
      paramGraphics.drawLine(i - 5, 2, i - 3, 4);
      paramGraphics.drawLine(3, j - 1, i - 2, j - 1);
    }
    
    public int getShift()
    {
      return 0;
    }
    
    public int getAdditionalHeight()
    {
      return 0;
    }
    
    public int getIconWidth()
    {
      return fileIcon16Sizewidth;
    }
    
    public int getIconHeight()
    {
      return fileIcon16Sizeheight + getAdditionalHeight();
    }
  }
  
  public static class FolderIcon16
    implements Icon, Serializable
  {
    MetalIconFactory.ImageCacher imageCacher;
    
    public FolderIcon16() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      GraphicsConfiguration localGraphicsConfiguration = paramComponent.getGraphicsConfiguration();
      if (imageCacher == null) {
        imageCacher = new MetalIconFactory.ImageCacher();
      }
      Object localObject = imageCacher.getImage(localGraphicsConfiguration);
      if (localObject == null)
      {
        if (localGraphicsConfiguration != null) {
          localObject = localGraphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), 2);
        } else {
          localObject = new BufferedImage(getIconWidth(), getIconHeight(), 2);
        }
        Graphics localGraphics = ((Image)localObject).getGraphics();
        paintMe(paramComponent, localGraphics);
        localGraphics.dispose();
        imageCacher.cacheImage((Image)localObject, localGraphicsConfiguration);
      }
      paramGraphics.drawImage((Image)localObject, paramInt1, paramInt2 + getShift(), null);
    }
    
    private void paintMe(Component paramComponent, Graphics paramGraphics)
    {
      int i = folderIcon16Sizewidth - 1;
      int j = folderIcon16Sizeheight - 1;
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      paramGraphics.drawLine(i - 5, 3, i, 3);
      paramGraphics.drawLine(i - 6, 4, i, 4);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.fillRect(2, 7, 13, 8);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
      paramGraphics.drawLine(i - 6, 5, i - 1, 5);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(0, 6, 0, j);
      paramGraphics.drawLine(1, 5, i - 7, 5);
      paramGraphics.drawLine(i - 6, 6, i - 1, 6);
      paramGraphics.drawLine(i, 5, i, j);
      paramGraphics.drawLine(0, j, i, j);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      paramGraphics.drawLine(1, 6, 1, j - 1);
      paramGraphics.drawLine(1, 6, i - 7, 6);
      paramGraphics.drawLine(i - 6, 7, i - 1, 7);
    }
    
    public int getShift()
    {
      return 0;
    }
    
    public int getAdditionalHeight()
    {
      return 0;
    }
    
    public int getIconWidth()
    {
      return folderIcon16Sizewidth;
    }
    
    public int getIconHeight()
    {
      return folderIcon16Sizeheight + getAdditionalHeight();
    }
  }
  
  private static class HorizontalSliderThumbIcon
    implements Icon, Serializable, UIResource
  {
    protected static MetalBumps controlBumps;
    protected static MetalBumps primaryBumps;
    
    public HorizontalSliderThumbIcon()
    {
      controlBumps = new MetalBumps(10, 6, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlInfo(), MetalLookAndFeel.getControl());
      primaryBumps = new MetalBumps(10, 6, MetalLookAndFeel.getPrimaryControl(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlShadow());
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      if (paramComponent.hasFocus()) {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      } else {
        paramGraphics.setColor(paramComponent.isEnabled() ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
      }
      paramGraphics.drawLine(1, 0, 13, 0);
      paramGraphics.drawLine(0, 1, 0, 8);
      paramGraphics.drawLine(14, 1, 14, 8);
      paramGraphics.drawLine(1, 9, 7, 15);
      paramGraphics.drawLine(7, 15, 14, 8);
      if (paramComponent.hasFocus()) {
        paramGraphics.setColor(paramComponent.getForeground());
      } else {
        paramGraphics.setColor(MetalLookAndFeel.getControl());
      }
      paramGraphics.fillRect(1, 1, 13, 8);
      paramGraphics.drawLine(2, 9, 12, 9);
      paramGraphics.drawLine(3, 10, 11, 10);
      paramGraphics.drawLine(4, 11, 10, 11);
      paramGraphics.drawLine(5, 12, 9, 12);
      paramGraphics.drawLine(6, 13, 8, 13);
      paramGraphics.drawLine(7, 14, 7, 14);
      if (paramComponent.isEnabled()) {
        if (paramComponent.hasFocus()) {
          primaryBumps.paintIcon(paramComponent, paramGraphics, 2, 2);
        } else {
          controlBumps.paintIcon(paramComponent, paramGraphics, 2, 2);
        }
      }
      if (paramComponent.isEnabled())
      {
        paramGraphics.setColor(paramComponent.hasFocus() ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControlHighlight());
        paramGraphics.drawLine(1, 1, 13, 1);
        paramGraphics.drawLine(1, 1, 1, 8);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 15;
    }
    
    public int getIconHeight()
    {
      return 16;
    }
  }
  
  static class ImageCacher
  {
    Vector<ImageGcPair> images = new Vector(1, 1);
    ImageGcPair currentImageGcPair;
    
    ImageCacher() {}
    
    Image getImage(GraphicsConfiguration paramGraphicsConfiguration)
    {
      if ((currentImageGcPair == null) || (!currentImageGcPair.hasSameConfiguration(paramGraphicsConfiguration)))
      {
        Iterator localIterator = images.iterator();
        while (localIterator.hasNext())
        {
          ImageGcPair localImageGcPair = (ImageGcPair)localIterator.next();
          if (localImageGcPair.hasSameConfiguration(paramGraphicsConfiguration))
          {
            currentImageGcPair = localImageGcPair;
            return image;
          }
        }
        return null;
      }
      return currentImageGcPair.image;
    }
    
    void cacheImage(Image paramImage, GraphicsConfiguration paramGraphicsConfiguration)
    {
      ImageGcPair localImageGcPair = new ImageGcPair(paramImage, paramGraphicsConfiguration);
      images.addElement(localImageGcPair);
      currentImageGcPair = localImageGcPair;
    }
    
    class ImageGcPair
    {
      Image image;
      GraphicsConfiguration gc;
      
      ImageGcPair(Image paramImage, GraphicsConfiguration paramGraphicsConfiguration)
      {
        image = paramImage;
        gc = paramGraphicsConfiguration;
      }
      
      boolean hasSameConfiguration(GraphicsConfiguration paramGraphicsConfiguration)
      {
        return ((paramGraphicsConfiguration != null) && (paramGraphicsConfiguration.equals(gc))) || ((paramGraphicsConfiguration == null) && (gc == null));
      }
    }
  }
  
  private static class InternalFrameAltMaximizeIcon
    implements Icon, UIResource, Serializable
  {
    int iconSize = 16;
    
    public InternalFrameAltMaximizeIcon(int paramInt)
    {
      iconSize = paramInt;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      JButton localJButton = (JButton)paramComponent;
      ButtonModel localButtonModel = localJButton.getModel();
      ColorUIResource localColorUIResource1 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource localColorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      Object localObject = MetalLookAndFeel.getPrimaryControlDarkShadow();
      ColorUIResource localColorUIResource3 = MetalLookAndFeel.getBlack();
      ColorUIResource localColorUIResource4 = MetalLookAndFeel.getWhite();
      ColorUIResource localColorUIResource5 = MetalLookAndFeel.getWhite();
      if (localJButton.getClientProperty("paintActive") != Boolean.TRUE)
      {
        localColorUIResource1 = MetalLookAndFeel.getControl();
        localColorUIResource2 = localColorUIResource1;
        localObject = MetalLookAndFeel.getControlDarkShadow();
        if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
        {
          localColorUIResource2 = MetalLookAndFeel.getControlShadow();
          localColorUIResource4 = localColorUIResource2;
          localObject = localColorUIResource3;
        }
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localColorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        localColorUIResource4 = localColorUIResource2;
        localObject = localColorUIResource3;
      }
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.fillRect(0, 0, iconSize, iconSize);
      paramGraphics.setColor(localColorUIResource2);
      paramGraphics.fillRect(3, 6, iconSize - 9, iconSize - 9);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawRect(1, 5, iconSize - 8, iconSize - 8);
      paramGraphics.drawLine(1, iconSize - 2, 1, iconSize - 2);
      paramGraphics.setColor(localColorUIResource5);
      paramGraphics.drawRect(2, 6, iconSize - 7, iconSize - 7);
      paramGraphics.setColor(localColorUIResource4);
      paramGraphics.drawRect(3, 7, iconSize - 9, iconSize - 9);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.drawRect(2, 6, iconSize - 8, iconSize - 8);
      paramGraphics.setColor(localColorUIResource4);
      paramGraphics.drawLine(iconSize - 6, 8, iconSize - 6, 8);
      paramGraphics.drawLine(iconSize - 9, 6, iconSize - 7, 8);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.drawLine(3, iconSize - 3, 3, iconSize - 3);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawLine(iconSize - 6, 9, iconSize - 6, 9);
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.drawLine(iconSize - 9, 5, iconSize - 9, 5);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.fillRect(iconSize - 7, 3, 3, 5);
      paramGraphics.drawLine(iconSize - 6, 5, iconSize - 3, 2);
      paramGraphics.drawLine(iconSize - 6, 6, iconSize - 2, 2);
      paramGraphics.drawLine(iconSize - 6, 7, iconSize - 3, 7);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawLine(iconSize - 8, 2, iconSize - 7, 2);
      paramGraphics.drawLine(iconSize - 8, 3, iconSize - 8, 7);
      paramGraphics.drawLine(iconSize - 6, 4, iconSize - 3, 1);
      paramGraphics.drawLine(iconSize - 4, 6, iconSize - 3, 6);
      paramGraphics.setColor(localColorUIResource5);
      paramGraphics.drawLine(iconSize - 6, 3, iconSize - 6, 3);
      paramGraphics.drawLine(iconSize - 4, 5, iconSize - 2, 3);
      paramGraphics.drawLine(iconSize - 4, 8, iconSize - 3, 8);
      paramGraphics.drawLine(iconSize - 2, 8, iconSize - 2, 7);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return iconSize;
    }
    
    public int getIconHeight()
    {
      return iconSize;
    }
  }
  
  private static class InternalFrameCloseIcon
    implements Icon, UIResource, Serializable
  {
    int iconSize = 16;
    
    public InternalFrameCloseIcon(int paramInt)
    {
      iconSize = paramInt;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      JButton localJButton = (JButton)paramComponent;
      ButtonModel localButtonModel = localJButton.getModel();
      ColorUIResource localColorUIResource1 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource localColorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      Object localObject = MetalLookAndFeel.getPrimaryControlDarkShadow();
      ColorUIResource localColorUIResource3 = MetalLookAndFeel.getBlack();
      ColorUIResource localColorUIResource4 = MetalLookAndFeel.getWhite();
      ColorUIResource localColorUIResource5 = MetalLookAndFeel.getWhite();
      if (localJButton.getClientProperty("paintActive") != Boolean.TRUE)
      {
        localColorUIResource1 = MetalLookAndFeel.getControl();
        localColorUIResource2 = localColorUIResource1;
        localObject = MetalLookAndFeel.getControlDarkShadow();
        if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
        {
          localColorUIResource2 = MetalLookAndFeel.getControlShadow();
          localColorUIResource4 = localColorUIResource2;
          localObject = localColorUIResource3;
        }
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localColorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        localColorUIResource4 = localColorUIResource2;
        localObject = localColorUIResource3;
      }
      int i = iconSize / 2;
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.fillRect(0, 0, iconSize, iconSize);
      paramGraphics.setColor(localColorUIResource2);
      paramGraphics.fillRect(3, 3, iconSize - 6, iconSize - 6);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawRect(1, 1, iconSize - 3, iconSize - 3);
      paramGraphics.drawRect(2, 2, iconSize - 5, iconSize - 5);
      paramGraphics.setColor(localColorUIResource5);
      paramGraphics.drawRect(2, 2, iconSize - 3, iconSize - 3);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.drawRect(2, 2, iconSize - 4, iconSize - 4);
      paramGraphics.drawLine(3, iconSize - 3, 3, iconSize - 3);
      paramGraphics.drawLine(iconSize - 3, 3, iconSize - 3, 3);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawLine(4, 5, 5, 4);
      paramGraphics.drawLine(4, iconSize - 6, iconSize - 6, 4);
      paramGraphics.setColor(localColorUIResource4);
      paramGraphics.drawLine(6, iconSize - 5, iconSize - 5, 6);
      paramGraphics.drawLine(i, i + 2, i + 2, i);
      paramGraphics.drawLine(iconSize - 5, iconSize - 5, iconSize - 4, iconSize - 5);
      paramGraphics.drawLine(iconSize - 5, iconSize - 4, iconSize - 5, iconSize - 4);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.drawLine(5, 5, iconSize - 6, iconSize - 6);
      paramGraphics.drawLine(6, 5, iconSize - 5, iconSize - 6);
      paramGraphics.drawLine(5, 6, iconSize - 6, iconSize - 5);
      paramGraphics.drawLine(5, iconSize - 5, iconSize - 5, 5);
      paramGraphics.drawLine(5, iconSize - 6, iconSize - 6, 5);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return iconSize;
    }
    
    public int getIconHeight()
    {
      return iconSize;
    }
  }
  
  private static class InternalFrameDefaultMenuIcon
    implements Icon, UIResource, Serializable
  {
    private InternalFrameDefaultMenuIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      ColorUIResource localColorUIResource1 = MetalLookAndFeel.getWindowBackground();
      ColorUIResource localColorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource localColorUIResource3 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(localColorUIResource2);
      paramGraphics.fillRect(0, 0, 16, 16);
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.fillRect(2, 6, 13, 9);
      paramGraphics.drawLine(2, 2, 2, 2);
      paramGraphics.drawLine(5, 2, 5, 2);
      paramGraphics.drawLine(8, 2, 8, 2);
      paramGraphics.drawLine(11, 2, 11, 2);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawRect(1, 1, 13, 13);
      paramGraphics.drawLine(1, 0, 14, 0);
      paramGraphics.drawLine(15, 1, 15, 14);
      paramGraphics.drawLine(1, 15, 14, 15);
      paramGraphics.drawLine(0, 1, 0, 14);
      paramGraphics.drawLine(2, 5, 13, 5);
      paramGraphics.drawLine(3, 3, 3, 3);
      paramGraphics.drawLine(6, 3, 6, 3);
      paramGraphics.drawLine(9, 3, 9, 3);
      paramGraphics.drawLine(12, 3, 12, 3);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 16;
    }
    
    public int getIconHeight()
    {
      return 16;
    }
  }
  
  private static class InternalFrameMaximizeIcon
    implements Icon, UIResource, Serializable
  {
    protected int iconSize = 16;
    
    public InternalFrameMaximizeIcon(int paramInt)
    {
      iconSize = paramInt;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      JButton localJButton = (JButton)paramComponent;
      ButtonModel localButtonModel = localJButton.getModel();
      ColorUIResource localColorUIResource1 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource localColorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      Object localObject = MetalLookAndFeel.getPrimaryControlDarkShadow();
      ColorUIResource localColorUIResource3 = MetalLookAndFeel.getBlack();
      ColorUIResource localColorUIResource4 = MetalLookAndFeel.getWhite();
      ColorUIResource localColorUIResource5 = MetalLookAndFeel.getWhite();
      if (localJButton.getClientProperty("paintActive") != Boolean.TRUE)
      {
        localColorUIResource1 = MetalLookAndFeel.getControl();
        localColorUIResource2 = localColorUIResource1;
        localObject = MetalLookAndFeel.getControlDarkShadow();
        if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
        {
          localColorUIResource2 = MetalLookAndFeel.getControlShadow();
          localColorUIResource4 = localColorUIResource2;
          localObject = localColorUIResource3;
        }
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localColorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        localColorUIResource4 = localColorUIResource2;
        localObject = localColorUIResource3;
      }
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.fillRect(0, 0, iconSize, iconSize);
      paramGraphics.setColor(localColorUIResource2);
      paramGraphics.fillRect(3, 7, iconSize - 10, iconSize - 10);
      paramGraphics.setColor(localColorUIResource4);
      paramGraphics.drawRect(3, 7, iconSize - 10, iconSize - 10);
      paramGraphics.setColor(localColorUIResource5);
      paramGraphics.drawRect(2, 6, iconSize - 7, iconSize - 7);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawRect(1, 5, iconSize - 7, iconSize - 7);
      paramGraphics.drawRect(2, 6, iconSize - 9, iconSize - 9);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.drawRect(2, 6, iconSize - 8, iconSize - 8);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawLine(3, iconSize - 5, iconSize - 9, 7);
      paramGraphics.drawLine(iconSize - 6, 4, iconSize - 5, 3);
      paramGraphics.drawLine(iconSize - 7, 1, iconSize - 7, 2);
      paramGraphics.drawLine(iconSize - 6, 1, iconSize - 2, 1);
      paramGraphics.setColor(localColorUIResource4);
      paramGraphics.drawLine(5, iconSize - 4, iconSize - 8, 9);
      paramGraphics.setColor(localColorUIResource5);
      paramGraphics.drawLine(iconSize - 6, 3, iconSize - 4, 5);
      paramGraphics.drawLine(iconSize - 4, 5, iconSize - 4, 6);
      paramGraphics.drawLine(iconSize - 2, 7, iconSize - 1, 7);
      paramGraphics.drawLine(iconSize - 1, 2, iconSize - 1, 6);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.drawLine(3, iconSize - 4, iconSize - 3, 2);
      paramGraphics.drawLine(3, iconSize - 3, iconSize - 2, 2);
      paramGraphics.drawLine(4, iconSize - 3, 5, iconSize - 3);
      paramGraphics.drawLine(iconSize - 7, 8, iconSize - 7, 9);
      paramGraphics.drawLine(iconSize - 6, 2, iconSize - 4, 2);
      paramGraphics.drawRect(iconSize - 3, 3, 1, 3);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return iconSize;
    }
    
    public int getIconHeight()
    {
      return iconSize;
    }
  }
  
  private static class InternalFrameMinimizeIcon
    implements Icon, UIResource, Serializable
  {
    int iconSize = 16;
    
    public InternalFrameMinimizeIcon(int paramInt)
    {
      iconSize = paramInt;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      JButton localJButton = (JButton)paramComponent;
      ButtonModel localButtonModel = localJButton.getModel();
      ColorUIResource localColorUIResource1 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource localColorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      Object localObject = MetalLookAndFeel.getPrimaryControlDarkShadow();
      ColorUIResource localColorUIResource3 = MetalLookAndFeel.getBlack();
      ColorUIResource localColorUIResource4 = MetalLookAndFeel.getWhite();
      ColorUIResource localColorUIResource5 = MetalLookAndFeel.getWhite();
      if (localJButton.getClientProperty("paintActive") != Boolean.TRUE)
      {
        localColorUIResource1 = MetalLookAndFeel.getControl();
        localColorUIResource2 = localColorUIResource1;
        localObject = MetalLookAndFeel.getControlDarkShadow();
        if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
        {
          localColorUIResource2 = MetalLookAndFeel.getControlShadow();
          localColorUIResource4 = localColorUIResource2;
          localObject = localColorUIResource3;
        }
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localColorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        localColorUIResource4 = localColorUIResource2;
        localObject = localColorUIResource3;
      }
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.fillRect(0, 0, iconSize, iconSize);
      paramGraphics.setColor(localColorUIResource2);
      paramGraphics.fillRect(4, 11, iconSize - 13, iconSize - 13);
      paramGraphics.setColor(localColorUIResource5);
      paramGraphics.drawRect(2, 10, iconSize - 10, iconSize - 11);
      paramGraphics.setColor(localColorUIResource4);
      paramGraphics.drawRect(3, 10, iconSize - 12, iconSize - 12);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawRect(1, 8, iconSize - 10, iconSize - 10);
      paramGraphics.drawRect(2, 9, iconSize - 12, iconSize - 12);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.drawRect(2, 9, iconSize - 11, iconSize - 11);
      paramGraphics.drawLine(iconSize - 10, 10, iconSize - 10, 10);
      paramGraphics.drawLine(3, iconSize - 3, 3, iconSize - 3);
      paramGraphics.setColor((Color)localObject);
      paramGraphics.fillRect(iconSize - 7, 3, 3, 5);
      paramGraphics.drawLine(iconSize - 6, 5, iconSize - 3, 2);
      paramGraphics.drawLine(iconSize - 6, 6, iconSize - 2, 2);
      paramGraphics.drawLine(iconSize - 6, 7, iconSize - 3, 7);
      paramGraphics.setColor(localColorUIResource3);
      paramGraphics.drawLine(iconSize - 8, 2, iconSize - 7, 2);
      paramGraphics.drawLine(iconSize - 8, 3, iconSize - 8, 7);
      paramGraphics.drawLine(iconSize - 6, 4, iconSize - 3, 1);
      paramGraphics.drawLine(iconSize - 4, 6, iconSize - 3, 6);
      paramGraphics.setColor(localColorUIResource5);
      paramGraphics.drawLine(iconSize - 6, 3, iconSize - 6, 3);
      paramGraphics.drawLine(iconSize - 4, 5, iconSize - 2, 3);
      paramGraphics.drawLine(iconSize - 7, 8, iconSize - 3, 8);
      paramGraphics.drawLine(iconSize - 2, 8, iconSize - 2, 7);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return iconSize;
    }
    
    public int getIconHeight()
    {
      return iconSize;
    }
  }
  
  private static class MenuArrowIcon
    implements Icon, UIResource, Serializable
  {
    private MenuArrowIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      JMenuItem localJMenuItem = (JMenuItem)paramComponent;
      ButtonModel localButtonModel = localJMenuItem.getModel();
      paramGraphics.translate(paramInt1, paramInt2);
      if (!localButtonModel.isEnabled()) {
        paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
      } else if ((localButtonModel.isArmed()) || (((paramComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
        paramGraphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
      } else {
        paramGraphics.setColor(localJMenuItem.getForeground());
      }
      if (MetalUtils.isLeftToRight(localJMenuItem))
      {
        paramGraphics.drawLine(0, 0, 0, 7);
        paramGraphics.drawLine(1, 1, 1, 6);
        paramGraphics.drawLine(2, 2, 2, 5);
        paramGraphics.drawLine(3, 3, 3, 4);
      }
      else
      {
        paramGraphics.drawLine(4, 0, 4, 7);
        paramGraphics.drawLine(3, 1, 3, 6);
        paramGraphics.drawLine(2, 2, 2, 5);
        paramGraphics.drawLine(1, 3, 1, 4);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return menuArrowIconSizewidth;
    }
    
    public int getIconHeight()
    {
      return menuArrowIconSizeheight;
    }
  }
  
  private static class MenuItemArrowIcon
    implements Icon, UIResource, Serializable
  {
    private MenuItemArrowIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return menuArrowIconSizewidth;
    }
    
    public int getIconHeight()
    {
      return menuArrowIconSizeheight;
    }
  }
  
  private static class OceanHorizontalSliderThumbIcon
    extends CachedPainter
    implements Icon, Serializable, UIResource
  {
    private static Polygon THUMB_SHAPE = new Polygon(new int[] { 0, 14, 14, 7, 0 }, new int[] { 0, 0, 8, 15, 8 }, 5);
    
    OceanHorizontalSliderThumbIcon()
    {
      super();
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      if (!(paramGraphics instanceof Graphics2D)) {
        return;
      }
      paint(paramComponent, paramGraphics, paramInt1, paramInt2, getIconWidth(), getIconHeight(), new Object[] { Boolean.valueOf(paramComponent.hasFocus()), Boolean.valueOf(paramComponent.isEnabled()), MetalLookAndFeel.getCurrentTheme() });
    }
    
    protected Image createImage(Component paramComponent, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, Object[] paramArrayOfObject)
    {
      if (paramGraphicsConfiguration == null) {
        return new BufferedImage(paramInt1, paramInt2, 2);
      }
      return paramGraphicsConfiguration.createCompatibleImage(paramInt1, paramInt2, 2);
    }
    
    protected void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      boolean bool1 = ((Boolean)paramArrayOfObject[0]).booleanValue();
      boolean bool2 = ((Boolean)paramArrayOfObject[1]).booleanValue();
      Rectangle localRectangle = localGraphics2D.getClipBounds();
      localGraphics2D.clip(THUMB_SHAPE);
      if (!bool2)
      {
        localGraphics2D.setColor(MetalLookAndFeel.getControl());
        localGraphics2D.fillRect(1, 1, 13, 14);
      }
      else if (bool1)
      {
        MetalUtils.drawGradient(paramComponent, localGraphics2D, "Slider.focusGradient", 1, 1, 13, 14, true);
      }
      else
      {
        MetalUtils.drawGradient(paramComponent, localGraphics2D, "Slider.gradient", 1, 1, 13, 14, true);
      }
      localGraphics2D.setClip(localRectangle);
      if (bool1) {
        localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      } else {
        localGraphics2D.setColor(bool2 ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
      }
      localGraphics2D.drawLine(1, 0, 13, 0);
      localGraphics2D.drawLine(0, 1, 0, 8);
      localGraphics2D.drawLine(14, 1, 14, 8);
      localGraphics2D.drawLine(1, 9, 7, 15);
      localGraphics2D.drawLine(7, 15, 14, 8);
      if ((bool1) && (bool2))
      {
        localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControl());
        localGraphics2D.fillRect(1, 1, 13, 1);
        localGraphics2D.fillRect(1, 2, 1, 7);
        localGraphics2D.fillRect(13, 2, 1, 7);
        localGraphics2D.drawLine(2, 9, 7, 14);
        localGraphics2D.drawLine(8, 13, 12, 9);
      }
    }
    
    public int getIconWidth()
    {
      return 15;
    }
    
    public int getIconHeight()
    {
      return 16;
    }
  }
  
  private static class OceanVerticalSliderThumbIcon
    extends CachedPainter
    implements Icon, Serializable, UIResource
  {
    private static Polygon LTR_THUMB_SHAPE = new Polygon(new int[] { 0, 8, 15, 8, 0 }, new int[] { 0, 0, 7, 14, 14 }, 5);
    private static Polygon RTL_THUMB_SHAPE = new Polygon(new int[] { 15, 15, 7, 0, 7 }, new int[] { 0, 14, 14, 7, 0 }, 5);
    
    OceanVerticalSliderThumbIcon()
    {
      super();
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      if (!(paramGraphics instanceof Graphics2D)) {
        return;
      }
      paint(paramComponent, paramGraphics, paramInt1, paramInt2, getIconWidth(), getIconHeight(), new Object[] { Boolean.valueOf(MetalUtils.isLeftToRight(paramComponent)), Boolean.valueOf(paramComponent.hasFocus()), Boolean.valueOf(paramComponent.isEnabled()), MetalLookAndFeel.getCurrentTheme() });
    }
    
    protected void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      boolean bool1 = ((Boolean)paramArrayOfObject[0]).booleanValue();
      boolean bool2 = ((Boolean)paramArrayOfObject[1]).booleanValue();
      boolean bool3 = ((Boolean)paramArrayOfObject[2]).booleanValue();
      Rectangle localRectangle = localGraphics2D.getClipBounds();
      if (bool1) {
        localGraphics2D.clip(LTR_THUMB_SHAPE);
      } else {
        localGraphics2D.clip(RTL_THUMB_SHAPE);
      }
      if (!bool3)
      {
        localGraphics2D.setColor(MetalLookAndFeel.getControl());
        localGraphics2D.fillRect(1, 1, 14, 14);
      }
      else if (bool2)
      {
        MetalUtils.drawGradient(paramComponent, localGraphics2D, "Slider.focusGradient", 1, 1, 14, 14, false);
      }
      else
      {
        MetalUtils.drawGradient(paramComponent, localGraphics2D, "Slider.gradient", 1, 1, 14, 14, false);
      }
      localGraphics2D.setClip(localRectangle);
      if (bool2) {
        localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      } else {
        localGraphics2D.setColor(bool3 ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
      }
      if (bool1)
      {
        localGraphics2D.drawLine(1, 0, 8, 0);
        localGraphics2D.drawLine(0, 1, 0, 13);
        localGraphics2D.drawLine(1, 14, 8, 14);
        localGraphics2D.drawLine(9, 1, 15, 7);
        localGraphics2D.drawLine(9, 13, 15, 7);
      }
      else
      {
        localGraphics2D.drawLine(7, 0, 14, 0);
        localGraphics2D.drawLine(15, 1, 15, 13);
        localGraphics2D.drawLine(7, 14, 14, 14);
        localGraphics2D.drawLine(0, 7, 6, 1);
        localGraphics2D.drawLine(0, 7, 6, 13);
      }
      if ((bool2) && (bool3))
      {
        localGraphics2D.setColor(MetalLookAndFeel.getPrimaryControl());
        if (bool1)
        {
          localGraphics2D.drawLine(1, 1, 8, 1);
          localGraphics2D.drawLine(1, 1, 1, 13);
          localGraphics2D.drawLine(1, 13, 8, 13);
          localGraphics2D.drawLine(9, 2, 14, 7);
          localGraphics2D.drawLine(9, 12, 14, 7);
        }
        else
        {
          localGraphics2D.drawLine(7, 1, 14, 1);
          localGraphics2D.drawLine(14, 1, 14, 13);
          localGraphics2D.drawLine(7, 13, 14, 13);
          localGraphics2D.drawLine(1, 7, 7, 1);
          localGraphics2D.drawLine(1, 7, 7, 13);
        }
      }
    }
    
    public int getIconWidth()
    {
      return 16;
    }
    
    public int getIconHeight()
    {
      return 15;
    }
    
    protected Image createImage(Component paramComponent, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, Object[] paramArrayOfObject)
    {
      if (paramGraphicsConfiguration == null) {
        return new BufferedImage(paramInt1, paramInt2, 2);
      }
      return paramGraphicsConfiguration.createCompatibleImage(paramInt1, paramInt2, 2);
    }
  }
  
  public static class PaletteCloseIcon
    implements Icon, UIResource, Serializable
  {
    int iconSize = 7;
    
    public PaletteCloseIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      JButton localJButton = (JButton)paramComponent;
      ButtonModel localButtonModel = localJButton.getModel();
      ColorUIResource localColorUIResource2 = MetalLookAndFeel.getPrimaryControlHighlight();
      ColorUIResource localColorUIResource3 = MetalLookAndFeel.getPrimaryControlInfo();
      ColorUIResource localColorUIResource1;
      if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
        localColorUIResource1 = localColorUIResource3;
      } else {
        localColorUIResource1 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      }
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.drawLine(0, 1, 5, 6);
      paramGraphics.drawLine(1, 0, 6, 5);
      paramGraphics.drawLine(1, 1, 6, 6);
      paramGraphics.drawLine(6, 1, 1, 6);
      paramGraphics.drawLine(5, 0, 0, 5);
      paramGraphics.drawLine(5, 1, 1, 5);
      paramGraphics.setColor(localColorUIResource2);
      paramGraphics.drawLine(6, 2, 5, 3);
      paramGraphics.drawLine(2, 6, 3, 5);
      paramGraphics.drawLine(6, 6, 6, 6);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return iconSize;
    }
    
    public int getIconHeight()
    {
      return iconSize;
    }
  }
  
  private static class RadioButtonIcon
    implements Icon, UIResource, Serializable
  {
    private RadioButtonIcon() {}
    
    public void paintOceanIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      ButtonModel localButtonModel = ((JRadioButton)paramComponent).getModel();
      boolean bool = localButtonModel.isEnabled();
      int i = (bool) && (localButtonModel.isPressed()) && (localButtonModel.isArmed()) ? 1 : 0;
      int j = (bool) && (localButtonModel.isRollover()) ? 1 : 0;
      paramGraphics.translate(paramInt1, paramInt2);
      if ((bool) && (i == 0))
      {
        MetalUtils.drawGradient(paramComponent, paramGraphics, "RadioButton.gradient", 1, 1, 10, 10, true);
        paramGraphics.setColor(paramComponent.getBackground());
        paramGraphics.fillRect(1, 1, 1, 1);
        paramGraphics.fillRect(10, 1, 1, 1);
        paramGraphics.fillRect(1, 10, 1, 1);
        paramGraphics.fillRect(10, 10, 1, 1);
      }
      else if ((i != 0) || (!bool))
      {
        if (i != 0) {
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
        } else {
          paramGraphics.setColor(MetalLookAndFeel.getControl());
        }
        paramGraphics.fillRect(2, 2, 8, 8);
        paramGraphics.fillRect(4, 1, 4, 1);
        paramGraphics.fillRect(4, 10, 4, 1);
        paramGraphics.fillRect(1, 4, 1, 4);
        paramGraphics.fillRect(10, 4, 1, 4);
      }
      if (!bool) {
        paramGraphics.setColor(MetalLookAndFeel.getInactiveControlTextColor());
      } else {
        paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      }
      paramGraphics.drawLine(4, 0, 7, 0);
      paramGraphics.drawLine(8, 1, 9, 1);
      paramGraphics.drawLine(10, 2, 10, 3);
      paramGraphics.drawLine(11, 4, 11, 7);
      paramGraphics.drawLine(10, 8, 10, 9);
      paramGraphics.drawLine(9, 10, 8, 10);
      paramGraphics.drawLine(7, 11, 4, 11);
      paramGraphics.drawLine(3, 10, 2, 10);
      paramGraphics.drawLine(1, 9, 1, 8);
      paramGraphics.drawLine(0, 7, 0, 4);
      paramGraphics.drawLine(1, 3, 1, 2);
      paramGraphics.drawLine(2, 1, 3, 1);
      if (i != 0)
      {
        paramGraphics.fillRect(1, 4, 1, 4);
        paramGraphics.fillRect(2, 2, 1, 2);
        paramGraphics.fillRect(3, 2, 1, 1);
        paramGraphics.fillRect(4, 1, 4, 1);
      }
      else if (j != 0)
      {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
        paramGraphics.fillRect(4, 1, 4, 2);
        paramGraphics.fillRect(8, 2, 2, 2);
        paramGraphics.fillRect(9, 4, 2, 4);
        paramGraphics.fillRect(8, 8, 2, 2);
        paramGraphics.fillRect(4, 9, 4, 2);
        paramGraphics.fillRect(2, 8, 2, 2);
        paramGraphics.fillRect(1, 4, 2, 4);
        paramGraphics.fillRect(2, 2, 2, 2);
      }
      if (localButtonModel.isSelected())
      {
        if (bool) {
          paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
        } else {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        }
        paramGraphics.fillRect(4, 4, 4, 4);
        paramGraphics.drawLine(4, 3, 7, 3);
        paramGraphics.drawLine(8, 4, 8, 7);
        paramGraphics.drawLine(7, 8, 4, 8);
        paramGraphics.drawLine(3, 7, 3, 4);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      if (MetalLookAndFeel.usingOcean())
      {
        paintOceanIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
        return;
      }
      JRadioButton localJRadioButton = (JRadioButton)paramComponent;
      ButtonModel localButtonModel = localJRadioButton.getModel();
      boolean bool = localButtonModel.isSelected();
      Color localColor = paramComponent.getBackground();
      Object localObject1 = paramComponent.getForeground();
      ColorUIResource localColorUIResource1 = MetalLookAndFeel.getControlShadow();
      ColorUIResource localColorUIResource2 = MetalLookAndFeel.getControlDarkShadow();
      Object localObject2 = MetalLookAndFeel.getControlHighlight();
      Object localObject3 = MetalLookAndFeel.getControlHighlight();
      Object localObject4 = localColor;
      if (!localButtonModel.isEnabled())
      {
        localObject2 = localObject3 = localColor;
        localColorUIResource2 = localObject1 = localColorUIResource1;
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localObject2 = localObject4 = localColorUIResource1;
      }
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor((Color)localObject4);
      paramGraphics.fillRect(2, 2, 9, 9);
      paramGraphics.setColor(localColorUIResource2);
      paramGraphics.drawLine(4, 0, 7, 0);
      paramGraphics.drawLine(8, 1, 9, 1);
      paramGraphics.drawLine(10, 2, 10, 3);
      paramGraphics.drawLine(11, 4, 11, 7);
      paramGraphics.drawLine(10, 8, 10, 9);
      paramGraphics.drawLine(9, 10, 8, 10);
      paramGraphics.drawLine(7, 11, 4, 11);
      paramGraphics.drawLine(3, 10, 2, 10);
      paramGraphics.drawLine(1, 9, 1, 8);
      paramGraphics.drawLine(0, 7, 0, 4);
      paramGraphics.drawLine(1, 3, 1, 2);
      paramGraphics.drawLine(2, 1, 3, 1);
      paramGraphics.setColor((Color)localObject2);
      paramGraphics.drawLine(2, 9, 2, 8);
      paramGraphics.drawLine(1, 7, 1, 4);
      paramGraphics.drawLine(2, 2, 2, 3);
      paramGraphics.drawLine(2, 2, 3, 2);
      paramGraphics.drawLine(4, 1, 7, 1);
      paramGraphics.drawLine(8, 2, 9, 2);
      paramGraphics.setColor((Color)localObject3);
      paramGraphics.drawLine(10, 1, 10, 1);
      paramGraphics.drawLine(11, 2, 11, 3);
      paramGraphics.drawLine(12, 4, 12, 7);
      paramGraphics.drawLine(11, 8, 11, 9);
      paramGraphics.drawLine(10, 10, 10, 10);
      paramGraphics.drawLine(9, 11, 8, 11);
      paramGraphics.drawLine(7, 12, 4, 12);
      paramGraphics.drawLine(3, 11, 2, 11);
      if (bool)
      {
        paramGraphics.setColor((Color)localObject1);
        paramGraphics.fillRect(4, 4, 4, 4);
        paramGraphics.drawLine(4, 3, 7, 3);
        paramGraphics.drawLine(8, 4, 8, 7);
        paramGraphics.drawLine(7, 8, 4, 8);
        paramGraphics.drawLine(3, 7, 3, 4);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
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
    
    public void paintOceanIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      ButtonModel localButtonModel = ((JMenuItem)paramComponent).getModel();
      boolean bool1 = localButtonModel.isSelected();
      boolean bool2 = localButtonModel.isEnabled();
      boolean bool3 = localButtonModel.isPressed();
      boolean bool4 = localButtonModel.isArmed();
      paramGraphics.translate(paramInt1, paramInt2);
      if (bool2)
      {
        MetalUtils.drawGradient(paramComponent, paramGraphics, "RadioButtonMenuItem.gradient", 1, 1, 7, 7, true);
        if ((bool3) || (bool4)) {
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
        } else {
          paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
        }
        paramGraphics.drawLine(2, 9, 7, 9);
        paramGraphics.drawLine(9, 2, 9, 7);
        paramGraphics.drawLine(8, 8, 8, 8);
        if ((bool3) || (bool4)) {
          paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
        } else {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        }
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
      }
      paramGraphics.drawLine(2, 0, 6, 0);
      paramGraphics.drawLine(2, 8, 6, 8);
      paramGraphics.drawLine(0, 2, 0, 6);
      paramGraphics.drawLine(8, 2, 8, 6);
      paramGraphics.drawLine(1, 1, 1, 1);
      paramGraphics.drawLine(7, 1, 7, 1);
      paramGraphics.drawLine(1, 7, 1, 7);
      paramGraphics.drawLine(7, 7, 7, 7);
      if (bool1)
      {
        if (bool2)
        {
          if ((bool4) || (((paramComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
            paramGraphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
          } else {
            paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
          }
        }
        else {
          paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        }
        paramGraphics.drawLine(3, 2, 5, 2);
        paramGraphics.drawLine(2, 3, 6, 3);
        paramGraphics.drawLine(2, 4, 6, 4);
        paramGraphics.drawLine(2, 5, 6, 5);
        paramGraphics.drawLine(3, 6, 5, 6);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      if (MetalLookAndFeel.usingOcean())
      {
        paintOceanIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
        return;
      }
      JMenuItem localJMenuItem = (JMenuItem)paramComponent;
      ButtonModel localButtonModel = localJMenuItem.getModel();
      boolean bool1 = localButtonModel.isSelected();
      boolean bool2 = localButtonModel.isEnabled();
      boolean bool3 = localButtonModel.isPressed();
      boolean bool4 = localButtonModel.isArmed();
      paramGraphics.translate(paramInt1, paramInt2);
      if (bool2)
      {
        if ((bool3) || (bool4))
        {
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
          paramGraphics.drawLine(3, 1, 8, 1);
          paramGraphics.drawLine(2, 9, 7, 9);
          paramGraphics.drawLine(1, 3, 1, 8);
          paramGraphics.drawLine(9, 2, 9, 7);
          paramGraphics.drawLine(2, 2, 2, 2);
          paramGraphics.drawLine(8, 8, 8, 8);
          paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
          paramGraphics.drawLine(2, 0, 6, 0);
          paramGraphics.drawLine(2, 8, 6, 8);
          paramGraphics.drawLine(0, 2, 0, 6);
          paramGraphics.drawLine(8, 2, 8, 6);
          paramGraphics.drawLine(1, 1, 1, 1);
          paramGraphics.drawLine(7, 1, 7, 1);
          paramGraphics.drawLine(1, 7, 1, 7);
          paramGraphics.drawLine(7, 7, 7, 7);
        }
        else
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
          paramGraphics.drawLine(3, 1, 8, 1);
          paramGraphics.drawLine(2, 9, 7, 9);
          paramGraphics.drawLine(1, 3, 1, 8);
          paramGraphics.drawLine(9, 2, 9, 7);
          paramGraphics.drawLine(2, 2, 2, 2);
          paramGraphics.drawLine(8, 8, 8, 8);
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawLine(2, 0, 6, 0);
          paramGraphics.drawLine(2, 8, 6, 8);
          paramGraphics.drawLine(0, 2, 0, 6);
          paramGraphics.drawLine(8, 2, 8, 6);
          paramGraphics.drawLine(1, 1, 1, 1);
          paramGraphics.drawLine(7, 1, 7, 1);
          paramGraphics.drawLine(1, 7, 1, 7);
          paramGraphics.drawLine(7, 7, 7, 7);
        }
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        paramGraphics.drawLine(2, 0, 6, 0);
        paramGraphics.drawLine(2, 8, 6, 8);
        paramGraphics.drawLine(0, 2, 0, 6);
        paramGraphics.drawLine(8, 2, 8, 6);
        paramGraphics.drawLine(1, 1, 1, 1);
        paramGraphics.drawLine(7, 1, 7, 1);
        paramGraphics.drawLine(1, 7, 1, 7);
        paramGraphics.drawLine(7, 7, 7, 7);
      }
      if (bool1)
      {
        if (bool2)
        {
          if ((localButtonModel.isArmed()) || (((paramComponent instanceof JMenu)) && (localButtonModel.isSelected()))) {
            paramGraphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
          } else {
            paramGraphics.setColor(localJMenuItem.getForeground());
          }
        }
        else {
          paramGraphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        }
        paramGraphics.drawLine(3, 2, 5, 2);
        paramGraphics.drawLine(2, 3, 6, 3);
        paramGraphics.drawLine(2, 4, 6, 4);
        paramGraphics.drawLine(2, 5, 6, 5);
        paramGraphics.drawLine(3, 6, 5, 6);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return menuCheckIconSizewidth;
    }
    
    public int getIconHeight()
    {
      return menuCheckIconSizeheight;
    }
  }
  
  private static class TreeComputerIcon
    implements Icon, UIResource, Serializable
  {
    private TreeComputerIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.fillRect(5, 4, 6, 4);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(2, 2, 2, 8);
      paramGraphics.drawLine(13, 2, 13, 8);
      paramGraphics.drawLine(3, 1, 12, 1);
      paramGraphics.drawLine(12, 9, 12, 9);
      paramGraphics.drawLine(3, 9, 3, 9);
      paramGraphics.drawLine(4, 4, 4, 7);
      paramGraphics.drawLine(5, 3, 10, 3);
      paramGraphics.drawLine(11, 4, 11, 7);
      paramGraphics.drawLine(5, 8, 10, 8);
      paramGraphics.drawLine(1, 10, 14, 10);
      paramGraphics.drawLine(14, 10, 14, 14);
      paramGraphics.drawLine(1, 14, 14, 14);
      paramGraphics.drawLine(1, 10, 1, 14);
      paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      paramGraphics.drawLine(6, 12, 8, 12);
      paramGraphics.drawLine(10, 12, 12, 12);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 16;
    }
    
    public int getIconHeight()
    {
      return 16;
    }
  }
  
  public static class TreeControlIcon
    implements Icon, Serializable
  {
    protected boolean isLight;
    MetalIconFactory.ImageCacher imageCacher;
    transient boolean cachedOrientation = true;
    
    public TreeControlIcon(boolean paramBoolean)
    {
      isLight = paramBoolean;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      GraphicsConfiguration localGraphicsConfiguration = paramComponent.getGraphicsConfiguration();
      if (imageCacher == null) {
        imageCacher = new MetalIconFactory.ImageCacher();
      }
      Object localObject = imageCacher.getImage(localGraphicsConfiguration);
      if ((localObject == null) || (cachedOrientation != MetalUtils.isLeftToRight(paramComponent)))
      {
        cachedOrientation = MetalUtils.isLeftToRight(paramComponent);
        if (localGraphicsConfiguration != null) {
          localObject = localGraphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), 2);
        } else {
          localObject = new BufferedImage(getIconWidth(), getIconHeight(), 2);
        }
        Graphics localGraphics = ((Image)localObject).getGraphics();
        paintMe(paramComponent, localGraphics, paramInt1, paramInt2);
        localGraphics.dispose();
        imageCacher.cacheImage((Image)localObject, localGraphicsConfiguration);
      }
      if (MetalUtils.isLeftToRight(paramComponent))
      {
        if (isLight) {
          paramGraphics.drawImage((Image)localObject, paramInt1 + 5, paramInt2 + 3, paramInt1 + 18, paramInt2 + 13, 4, 3, 17, 13, null);
        } else {
          paramGraphics.drawImage((Image)localObject, paramInt1 + 5, paramInt2 + 3, paramInt1 + 18, paramInt2 + 17, 4, 3, 17, 17, null);
        }
      }
      else if (isLight) {
        paramGraphics.drawImage((Image)localObject, paramInt1 + 3, paramInt2 + 3, paramInt1 + 16, paramInt2 + 13, 4, 3, 17, 13, null);
      } else {
        paramGraphics.drawImage((Image)localObject, paramInt1 + 3, paramInt2 + 3, paramInt1 + 16, paramInt2 + 17, 4, 3, 17, 17, null);
      }
    }
    
    public void paintMe(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      int i = MetalUtils.isLeftToRight(paramComponent) ? 0 : 4;
      paramGraphics.drawLine(i + 4, 6, i + 4, 9);
      paramGraphics.drawLine(i + 5, 5, i + 5, 5);
      paramGraphics.drawLine(i + 6, 4, i + 9, 4);
      paramGraphics.drawLine(i + 10, 5, i + 10, 5);
      paramGraphics.drawLine(i + 11, 6, i + 11, 9);
      paramGraphics.drawLine(i + 10, 10, i + 10, 10);
      paramGraphics.drawLine(i + 6, 11, i + 9, 11);
      paramGraphics.drawLine(i + 5, 10, i + 5, 10);
      paramGraphics.drawLine(i + 7, 7, i + 8, 7);
      paramGraphics.drawLine(i + 7, 8, i + 8, 8);
      if (isLight)
      {
        if (MetalUtils.isLeftToRight(paramComponent))
        {
          paramGraphics.drawLine(12, 7, 15, 7);
          paramGraphics.drawLine(12, 8, 15, 8);
        }
        else
        {
          paramGraphics.drawLine(4, 7, 7, 7);
          paramGraphics.drawLine(4, 8, 7, 8);
        }
      }
      else
      {
        paramGraphics.drawLine(i + 7, 12, i + 7, 15);
        paramGraphics.drawLine(i + 8, 12, i + 8, 15);
      }
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      paramGraphics.drawLine(i + 5, 6, i + 5, 9);
      paramGraphics.drawLine(i + 6, 5, i + 9, 5);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
      paramGraphics.drawLine(i + 6, 6, i + 6, 6);
      paramGraphics.drawLine(i + 9, 6, i + 9, 6);
      paramGraphics.drawLine(i + 6, 9, i + 6, 9);
      paramGraphics.drawLine(i + 10, 6, i + 10, 9);
      paramGraphics.drawLine(i + 6, 10, i + 9, 10);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.drawLine(i + 6, 7, i + 6, 8);
      paramGraphics.drawLine(i + 7, 6, i + 8, 6);
      paramGraphics.drawLine(i + 9, 7, i + 9, 7);
      paramGraphics.drawLine(i + 7, 9, i + 7, 9);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      paramGraphics.drawLine(i + 8, 9, i + 9, 9);
      paramGraphics.drawLine(i + 9, 8, i + 9, 8);
    }
    
    public int getIconWidth()
    {
      return treeControlSizewidth;
    }
    
    public int getIconHeight()
    {
      return treeControlSizeheight;
    }
  }
  
  private static class TreeFloppyDriveIcon
    implements Icon, UIResource, Serializable
  {
    private TreeFloppyDriveIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
      paramGraphics.fillRect(2, 2, 12, 12);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(1, 1, 13, 1);
      paramGraphics.drawLine(14, 2, 14, 14);
      paramGraphics.drawLine(1, 14, 14, 14);
      paramGraphics.drawLine(1, 1, 1, 14);
      paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      paramGraphics.fillRect(5, 2, 6, 5);
      paramGraphics.drawLine(4, 8, 11, 8);
      paramGraphics.drawLine(3, 9, 3, 13);
      paramGraphics.drawLine(12, 9, 12, 13);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      paramGraphics.fillRect(8, 3, 2, 3);
      paramGraphics.fillRect(4, 9, 8, 5);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
      paramGraphics.drawLine(5, 10, 9, 10);
      paramGraphics.drawLine(5, 12, 8, 12);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 16;
    }
    
    public int getIconHeight()
    {
      return 16;
    }
  }
  
  public static class TreeFolderIcon
    extends MetalIconFactory.FolderIcon16
  {
    public TreeFolderIcon() {}
    
    public int getShift()
    {
      return -1;
    }
    
    public int getAdditionalHeight()
    {
      return 2;
    }
  }
  
  private static class TreeHardDriveIcon
    implements Icon, UIResource, Serializable
  {
    private TreeHardDriveIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      paramGraphics.drawLine(1, 4, 1, 5);
      paramGraphics.drawLine(2, 3, 3, 3);
      paramGraphics.drawLine(4, 2, 11, 2);
      paramGraphics.drawLine(12, 3, 13, 3);
      paramGraphics.drawLine(14, 4, 14, 5);
      paramGraphics.drawLine(12, 6, 13, 6);
      paramGraphics.drawLine(4, 7, 11, 7);
      paramGraphics.drawLine(2, 6, 3, 6);
      paramGraphics.drawLine(1, 7, 1, 8);
      paramGraphics.drawLine(2, 9, 3, 9);
      paramGraphics.drawLine(4, 10, 11, 10);
      paramGraphics.drawLine(12, 9, 13, 9);
      paramGraphics.drawLine(14, 7, 14, 8);
      paramGraphics.drawLine(1, 10, 1, 11);
      paramGraphics.drawLine(2, 12, 3, 12);
      paramGraphics.drawLine(4, 13, 11, 13);
      paramGraphics.drawLine(12, 12, 13, 12);
      paramGraphics.drawLine(14, 10, 14, 11);
      paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
      paramGraphics.drawLine(7, 6, 7, 6);
      paramGraphics.drawLine(9, 6, 9, 6);
      paramGraphics.drawLine(10, 5, 10, 5);
      paramGraphics.drawLine(11, 6, 11, 6);
      paramGraphics.drawLine(12, 5, 13, 5);
      paramGraphics.drawLine(13, 4, 13, 4);
      paramGraphics.drawLine(7, 9, 7, 9);
      paramGraphics.drawLine(9, 9, 9, 9);
      paramGraphics.drawLine(10, 8, 10, 8);
      paramGraphics.drawLine(11, 9, 11, 9);
      paramGraphics.drawLine(12, 8, 13, 8);
      paramGraphics.drawLine(13, 7, 13, 7);
      paramGraphics.drawLine(7, 12, 7, 12);
      paramGraphics.drawLine(9, 12, 9, 12);
      paramGraphics.drawLine(10, 11, 10, 11);
      paramGraphics.drawLine(11, 12, 11, 12);
      paramGraphics.drawLine(12, 11, 13, 11);
      paramGraphics.drawLine(13, 10, 13, 10);
      paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
      paramGraphics.drawLine(4, 3, 5, 3);
      paramGraphics.drawLine(7, 3, 9, 3);
      paramGraphics.drawLine(11, 3, 11, 3);
      paramGraphics.drawLine(2, 4, 6, 4);
      paramGraphics.drawLine(8, 4, 8, 4);
      paramGraphics.drawLine(2, 5, 3, 5);
      paramGraphics.drawLine(4, 6, 4, 6);
      paramGraphics.drawLine(2, 7, 3, 7);
      paramGraphics.drawLine(2, 8, 3, 8);
      paramGraphics.drawLine(4, 9, 4, 9);
      paramGraphics.drawLine(2, 10, 3, 10);
      paramGraphics.drawLine(2, 11, 3, 11);
      paramGraphics.drawLine(4, 12, 4, 12);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 16;
    }
    
    public int getIconHeight()
    {
      return 16;
    }
  }
  
  public static class TreeLeafIcon
    extends MetalIconFactory.FileIcon16
  {
    public TreeLeafIcon() {}
    
    public int getShift()
    {
      return 2;
    }
    
    public int getAdditionalHeight()
    {
      return 4;
    }
  }
  
  private static class VerticalSliderThumbIcon
    implements Icon, Serializable, UIResource
  {
    protected static MetalBumps controlBumps;
    protected static MetalBumps primaryBumps;
    
    public VerticalSliderThumbIcon()
    {
      controlBumps = new MetalBumps(6, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlInfo(), MetalLookAndFeel.getControl());
      primaryBumps = new MetalBumps(6, 10, MetalLookAndFeel.getPrimaryControl(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlShadow());
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      boolean bool = MetalUtils.isLeftToRight(paramComponent);
      paramGraphics.translate(paramInt1, paramInt2);
      if (paramComponent.hasFocus()) {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      } else {
        paramGraphics.setColor(paramComponent.isEnabled() ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
      }
      if (bool)
      {
        paramGraphics.drawLine(1, 0, 8, 0);
        paramGraphics.drawLine(0, 1, 0, 13);
        paramGraphics.drawLine(1, 14, 8, 14);
        paramGraphics.drawLine(9, 1, 15, 7);
        paramGraphics.drawLine(9, 13, 15, 7);
      }
      else
      {
        paramGraphics.drawLine(7, 0, 14, 0);
        paramGraphics.drawLine(15, 1, 15, 13);
        paramGraphics.drawLine(7, 14, 14, 14);
        paramGraphics.drawLine(0, 7, 6, 1);
        paramGraphics.drawLine(0, 7, 6, 13);
      }
      if (paramComponent.hasFocus()) {
        paramGraphics.setColor(paramComponent.getForeground());
      } else {
        paramGraphics.setColor(MetalLookAndFeel.getControl());
      }
      if (bool)
      {
        paramGraphics.fillRect(1, 1, 8, 13);
        paramGraphics.drawLine(9, 2, 9, 12);
        paramGraphics.drawLine(10, 3, 10, 11);
        paramGraphics.drawLine(11, 4, 11, 10);
        paramGraphics.drawLine(12, 5, 12, 9);
        paramGraphics.drawLine(13, 6, 13, 8);
        paramGraphics.drawLine(14, 7, 14, 7);
      }
      else
      {
        paramGraphics.fillRect(7, 1, 8, 13);
        paramGraphics.drawLine(6, 3, 6, 12);
        paramGraphics.drawLine(5, 4, 5, 11);
        paramGraphics.drawLine(4, 5, 4, 10);
        paramGraphics.drawLine(3, 6, 3, 9);
        paramGraphics.drawLine(2, 7, 2, 8);
      }
      int i = bool ? 2 : 8;
      if (paramComponent.isEnabled()) {
        if (paramComponent.hasFocus()) {
          primaryBumps.paintIcon(paramComponent, paramGraphics, i, 2);
        } else {
          controlBumps.paintIcon(paramComponent, paramGraphics, i, 2);
        }
      }
      if (paramComponent.isEnabled())
      {
        paramGraphics.setColor(paramComponent.hasFocus() ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControlHighlight());
        if (bool)
        {
          paramGraphics.drawLine(1, 1, 8, 1);
          paramGraphics.drawLine(1, 1, 1, 13);
        }
        else
        {
          paramGraphics.drawLine(8, 1, 14, 1);
          paramGraphics.drawLine(1, 7, 7, 1);
        }
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public int getIconWidth()
    {
      return 16;
    }
    
    public int getIconHeight()
    {
      return 15;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */