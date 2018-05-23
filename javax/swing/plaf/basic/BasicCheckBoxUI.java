package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class BasicCheckBoxUI
  extends BasicRadioButtonUI
{
  private static final Object BASIC_CHECK_BOX_UI_KEY = new Object();
  private static final String propertyPrefix = "CheckBox.";
  
  public BasicCheckBoxUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    BasicCheckBoxUI localBasicCheckBoxUI = (BasicCheckBoxUI)localAppContext.get(BASIC_CHECK_BOX_UI_KEY);
    if (localBasicCheckBoxUI == null)
    {
      localBasicCheckBoxUI = new BasicCheckBoxUI();
      localAppContext.put(BASIC_CHECK_BOX_UI_KEY, localBasicCheckBoxUI);
    }
    return localBasicCheckBoxUI;
  }
  
  public String getPropertyPrefix()
  {
    return "CheckBox.";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicCheckBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */