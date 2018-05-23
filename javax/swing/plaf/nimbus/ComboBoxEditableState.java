package javax.swing.plaf.nimbus;

import javax.swing.JComboBox;
import javax.swing.JComponent;

class ComboBoxEditableState
  extends State
{
  ComboBoxEditableState()
  {
    super("Editable");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JComboBox)) && (((JComboBox)paramJComponent).isEditable());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ComboBoxEditableState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */