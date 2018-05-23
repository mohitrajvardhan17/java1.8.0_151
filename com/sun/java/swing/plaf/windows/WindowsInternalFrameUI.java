package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class WindowsInternalFrameUI
  extends BasicInternalFrameUI
{
  XPStyle xp = XPStyle.getXP();
  
  public void installDefaults()
  {
    super.installDefaults();
    if (xp != null) {
      frame.setBorder(new XPBorder(null));
    } else {
      frame.setBorder(UIManager.getBorder("InternalFrame.border"));
    }
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    LookAndFeel.installProperty(paramJComponent, "opaque", xp == null ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public void uninstallDefaults()
  {
    frame.setBorder(null);
    super.uninstallDefaults();
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsInternalFrameUI((JInternalFrame)paramJComponent);
  }
  
  public WindowsInternalFrameUI(JInternalFrame paramJInternalFrame)
  {
    super(paramJInternalFrame);
  }
  
  protected DesktopManager createDesktopManager()
  {
    return new WindowsDesktopManager();
  }
  
  protected JComponent createNorthPane(JInternalFrame paramJInternalFrame)
  {
    titlePane = new WindowsInternalFrameTitlePane(paramJInternalFrame);
    return titlePane;
  }
  
  private class XPBorder
    extends AbstractBorder
  {
    private XPStyle.Skin leftSkin = xp.getSkin(frame, TMSchema.Part.WP_FRAMELEFT);
    private XPStyle.Skin rightSkin = xp.getSkin(frame, TMSchema.Part.WP_FRAMERIGHT);
    private XPStyle.Skin bottomSkin = xp.getSkin(frame, TMSchema.Part.WP_FRAMEBOTTOM);
    
    private XPBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      TMSchema.State localState = ((JInternalFrame)paramComponent).isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
      int i = titlePane != null ? titlePane.getSize().height : 0;
      bottomSkin.paintSkin(paramGraphics, 0, paramInt4 - bottomSkin.getHeight(), paramInt3, bottomSkin.getHeight(), localState);
      leftSkin.paintSkin(paramGraphics, 0, i - 1, leftSkin.getWidth(), paramInt4 - i - bottomSkin.getHeight() + 2, localState);
      rightSkin.paintSkin(paramGraphics, paramInt3 - rightSkin.getWidth(), i - 1, rightSkin.getWidth(), paramInt4 - i - bottomSkin.getHeight() + 2, localState);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      top = 4;
      left = leftSkin.getWidth();
      right = rightSkin.getWidth();
      bottom = bottomSkin.getHeight();
      return paramInsets;
    }
    
    public boolean isBorderOpaque()
    {
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */