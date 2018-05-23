package javax.swing.plaf.nimbus;

import java.awt.Container;
import javax.swing.JComboBox;
import javax.swing.JComponent;

class ComboBoxArrowButtonEditableState
  extends State
{
  ComboBoxArrowButtonEditableState()
  {
    super("Editable");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    Container localContainer = paramJComponent.getParent();
    return ((localContainer instanceof JComboBox)) && (((JComboBox)localContainer).isEditable());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ComboBoxArrowButtonEditableState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */