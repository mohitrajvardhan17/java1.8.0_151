package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class BasicTextPaneUI
  extends BasicEditorPaneUI
{
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTextPaneUI();
  }
  
  public BasicTextPaneUI() {}
  
  protected String getPropertyPrefix()
  {
    return "TextPane";
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicTextPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */