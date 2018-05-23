package com.sun.java.swing.plaf.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class WindowsDesktopIconUI
  extends BasicDesktopIconUI
{
  private int width;
  
  public WindowsDesktopIconUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsDesktopIconUI();
  }
  
  public void installDefaults()
  {
    super.installDefaults();
    width = UIManager.getInt("DesktopIcon.width");
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    paramJComponent.setOpaque(XPStyle.getXP() == null);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    WindowsInternalFrameTitlePane localWindowsInternalFrameTitlePane = (WindowsInternalFrameTitlePane)iconPane;
    super.uninstallUI(paramJComponent);
    localWindowsInternalFrameTitlePane.uninstallListeners();
  }
  
  protected void installComponents()
  {
    iconPane = new WindowsInternalFrameTitlePane(frame);
    desktopIcon.setLayout(new BorderLayout());
    desktopIcon.add(iconPane, "Center");
    if (XPStyle.getXP() != null) {
      desktopIcon.setBorder(null);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return getMinimumSize(paramJComponent);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Dimension localDimension = super.getMinimumSize(paramJComponent);
    width = width;
    return localDimension;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */