package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class BasicFormattedTextFieldUI
  extends BasicTextFieldUI
{
  public BasicFormattedTextFieldUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicFormattedTextFieldUI();
  }
  
  protected String getPropertyPrefix()
  {
    return "FormattedTextField";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicFormattedTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */