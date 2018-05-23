package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.accessibility.AccessibleContext;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionActionsMenuListener
  implements ItemListener
{
  private ToolDialog td;
  
  PermissionActionsMenuListener(ToolDialog paramToolDialog)
  {
    td = paramToolDialog;
  }
  
  public void itemStateChanged(ItemEvent paramItemEvent)
  {
    if (paramItemEvent.getStateChange() == 2) {
      return;
    }
    JComboBox localJComboBox = (JComboBox)td.getComponent(5);
    localJComboBox.getAccessibleContext().setAccessibleName((String)paramItemEvent.getItem());
    if (((String)paramItemEvent.getItem()).indexOf(ToolDialog.PERM_ACTIONS) != -1) {
      return;
    }
    JTextField localJTextField = (JTextField)td.getComponent(6);
    if ((localJTextField.getText() == null) || (localJTextField.getText().equals(""))) {
      localJTextField.setText((String)paramItemEvent.getItem());
    } else if (localJTextField.getText().indexOf((String)paramItemEvent.getItem()) == -1) {
      localJTextField.setText(localJTextField.getText() + ", " + (String)paramItemEvent.getItem());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PermissionActionsMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */