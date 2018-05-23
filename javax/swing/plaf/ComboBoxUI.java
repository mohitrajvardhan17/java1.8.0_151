package javax.swing.plaf;

import javax.swing.JComboBox;

public abstract class ComboBoxUI
  extends ComponentUI
{
  public ComboBoxUI() {}
  
  public abstract void setPopupVisible(JComboBox paramJComboBox, boolean paramBoolean);
  
  public abstract boolean isPopupVisible(JComboBox paramJComboBox);
  
  public abstract boolean isFocusTraversable(JComboBox paramJComboBox);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\ComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */