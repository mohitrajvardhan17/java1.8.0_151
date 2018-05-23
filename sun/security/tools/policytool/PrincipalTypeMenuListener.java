package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.accessibility.AccessibleContext;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PrincipalTypeMenuListener
  implements ItemListener
{
  private ToolDialog td;
  
  PrincipalTypeMenuListener(ToolDialog paramToolDialog)
  {
    td = paramToolDialog;
  }
  
  public void itemStateChanged(ItemEvent paramItemEvent)
  {
    if (paramItemEvent.getStateChange() == 2) {
      return;
    }
    JComboBox localJComboBox = (JComboBox)td.getComponent(1);
    JTextField localJTextField1 = (JTextField)td.getComponent(2);
    JTextField localJTextField2 = (JTextField)td.getComponent(4);
    localJComboBox.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)paramItemEvent.getItem()));
    if (((String)paramItemEvent.getItem()).equals(ToolDialog.PRIN_TYPE))
    {
      if ((localJTextField1.getText() != null) && (localJTextField1.getText().length() > 0))
      {
        localPrin = ToolDialog.getPrin(localJTextField1.getText(), true);
        localJComboBox.setSelectedItem(CLASS);
      }
      return;
    }
    if (localJTextField1.getText().indexOf((String)paramItemEvent.getItem()) == -1) {
      localJTextField2.setText("");
    }
    Prin localPrin = ToolDialog.getPrin((String)paramItemEvent.getItem(), false);
    if (localPrin != null) {
      localJTextField1.setText(FULL_CLASS);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PrincipalTypeMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */