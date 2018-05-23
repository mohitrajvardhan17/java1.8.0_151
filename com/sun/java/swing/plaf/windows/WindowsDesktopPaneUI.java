package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class WindowsDesktopPaneUI
  extends BasicDesktopPaneUI
{
  public WindowsDesktopPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsDesktopPaneUI();
  }
  
  protected void installDesktopManager()
  {
    desktopManager = desktop.getDesktopManager();
    if (desktopManager == null)
    {
      desktopManager = new WindowsDesktopManager();
      desktop.setDesktopManager(desktopManager);
    }
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
  }
  
  protected void installKeyboardActions()
  {
    super.installKeyboardActions();
    if (!desktop.requestDefaultFocus()) {
      desktop.requestFocus();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsDesktopPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */