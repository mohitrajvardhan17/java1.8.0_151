package javax.swing.plaf;

import javax.swing.JOptionPane;

public abstract class OptionPaneUI
  extends ComponentUI
{
  public OptionPaneUI() {}
  
  public abstract void selectInitialValue(JOptionPane paramJOptionPane);
  
  public abstract boolean containsCustomComponents(JOptionPane paramJOptionPane);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\OptionPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */