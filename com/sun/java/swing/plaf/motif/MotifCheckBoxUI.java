package com.sun.java.swing.plaf.motif;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class MotifCheckBoxUI
  extends MotifRadioButtonUI
{
  private static final Object MOTIF_CHECK_BOX_UI_KEY = new Object();
  private static final String propertyPrefix = "CheckBox.";
  private boolean defaults_initialized = false;
  
  public MotifCheckBoxUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MotifCheckBoxUI localMotifCheckBoxUI = (MotifCheckBoxUI)localAppContext.get(MOTIF_CHECK_BOX_UI_KEY);
    if (localMotifCheckBoxUI == null)
    {
      localMotifCheckBoxUI = new MotifCheckBoxUI();
      localAppContext.put(MOTIF_CHECK_BOX_UI_KEY, localMotifCheckBoxUI);
    }
    return localMotifCheckBoxUI;
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
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    defaults_initialized = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifCheckBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */