package javax.swing.plaf.metal;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class MetalCheckBoxUI
  extends MetalRadioButtonUI
{
  private static final Object METAL_CHECK_BOX_UI_KEY = new Object();
  private static final String propertyPrefix = "CheckBox.";
  private boolean defaults_initialized = false;
  
  public MetalCheckBoxUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MetalCheckBoxUI localMetalCheckBoxUI = (MetalCheckBoxUI)localAppContext.get(METAL_CHECK_BOX_UI_KEY);
    if (localMetalCheckBoxUI == null)
    {
      localMetalCheckBoxUI = new MetalCheckBoxUI();
      localAppContext.put(METAL_CHECK_BOX_UI_KEY, localMetalCheckBoxUI);
    }
    return localMetalCheckBoxUI;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalCheckBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */