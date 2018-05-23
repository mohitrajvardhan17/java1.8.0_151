package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.Collator;
import javax.accessibility.AccessibleContext;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionMenuListener
  implements ItemListener
{
  private ToolDialog td;
  
  PermissionMenuListener(ToolDialog paramToolDialog)
  {
    td = paramToolDialog;
  }
  
  public void itemStateChanged(ItemEvent paramItemEvent)
  {
    if (paramItemEvent.getStateChange() == 2) {
      return;
    }
    JComboBox localJComboBox1 = (JComboBox)td.getComponent(1);
    JComboBox localJComboBox2 = (JComboBox)td.getComponent(3);
    JComboBox localJComboBox3 = (JComboBox)td.getComponent(5);
    JTextField localJTextField1 = (JTextField)td.getComponent(4);
    JTextField localJTextField2 = (JTextField)td.getComponent(6);
    JTextField localJTextField3 = (JTextField)td.getComponent(2);
    JTextField localJTextField4 = (JTextField)td.getComponent(8);
    localJComboBox1.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)paramItemEvent.getItem()));
    if (PolicyTool.collator.compare((String)paramItemEvent.getItem(), ToolDialog.PERM) == 0)
    {
      if ((localJTextField3.getText() != null) && (localJTextField3.getText().length() > 0))
      {
        localPerm = ToolDialog.getPerm(localJTextField3.getText(), true);
        if (localPerm != null) {
          localJComboBox1.setSelectedItem(CLASS);
        }
      }
      return;
    }
    if (localJTextField3.getText().indexOf((String)paramItemEvent.getItem()) == -1)
    {
      localJTextField1.setText("");
      localJTextField2.setText("");
      localJTextField4.setText("");
    }
    Perm localPerm = ToolDialog.getPerm((String)paramItemEvent.getItem(), false);
    if (localPerm == null) {
      localJTextField3.setText("");
    } else {
      localJTextField3.setText(FULL_CLASS);
    }
    td.setPermissionNames(localPerm, localJComboBox2, localJTextField1);
    td.setPermissionActions(localPerm, localJComboBox3, localJTextField2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PermissionMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */