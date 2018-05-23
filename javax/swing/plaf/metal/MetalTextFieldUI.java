package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class MetalTextFieldUI
  extends BasicTextFieldUI
{
  public MetalTextFieldUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalTextFieldUI();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */