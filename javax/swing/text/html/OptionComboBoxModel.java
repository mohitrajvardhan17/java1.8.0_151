package javax.swing.text.html;

import java.io.Serializable;
import javax.swing.DefaultComboBoxModel;

class OptionComboBoxModel<E>
  extends DefaultComboBoxModel<E>
  implements Serializable
{
  private Option selectedOption = null;
  
  OptionComboBoxModel() {}
  
  public void setInitialSelection(Option paramOption)
  {
    selectedOption = paramOption;
  }
  
  public Option getInitialSelection()
  {
    return selectedOption;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\OptionComboBoxModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */