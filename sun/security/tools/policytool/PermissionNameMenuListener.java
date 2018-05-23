package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.accessibility.AccessibleContext;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionNameMenuListener
  implements ItemListener
{
  private ToolDialog td;
  
  PermissionNameMenuListener(ToolDialog paramToolDialog)
  {
    td = paramToolDialog;
  }
  
  public void itemStateChanged(ItemEvent paramItemEvent)
  {
    if (paramItemEvent.getStateChange() == 2) {
      return;
    }
    JComboBox localJComboBox = (JComboBox)td.getComponent(3);
    localJComboBox.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)paramItemEvent.getItem()));
    if (((String)paramItemEvent.getItem()).indexOf(ToolDialog.PERM_NAME) != -1) {
      return;
    }
    JTextField localJTextField = (JTextField)td.getComponent(4);
    localJTextField.setText((String)paramItemEvent.getItem());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PermissionNameMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */