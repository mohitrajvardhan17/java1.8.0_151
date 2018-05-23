package com.sun.java.swing.plaf.windows;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class WindowsCheckBoxUI
  extends WindowsRadioButtonUI
{
  private static final Object WINDOWS_CHECK_BOX_UI_KEY = new Object();
  private static final String propertyPrefix = "CheckBox.";
  private boolean defaults_initialized = false;
  
  public WindowsCheckBoxUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    WindowsCheckBoxUI localWindowsCheckBoxUI = (WindowsCheckBoxUI)localAppContext.get(WINDOWS_CHECK_BOX_UI_KEY);
    if (localWindowsCheckBoxUI == null)
    {
      localWindowsCheckBoxUI = new WindowsCheckBoxUI();
      localAppContext.put(WINDOWS_CHECK_BOX_UI_KEY, localWindowsCheckBoxUI);
    }
    return localWindowsCheckBoxUI;
  }
  
  public String getPropertyPrefix()
  {
    return "CheckBox.";
  }
  
  public void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!defaults_initialized)
    {
      icon = UIManager.getIcon(getPropertyPrefix() + "icon");
      defaults_initialized = true;
    }
  }
  
  public void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    defaults_initialized = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsCheckBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */