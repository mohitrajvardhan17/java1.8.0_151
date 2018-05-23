package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JTextField;

class ChangeKeyStoreOKButtonListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog td;
  
  ChangeKeyStoreOKButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
    td = paramToolDialog;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    String str1 = ((JTextField)td.getComponent(1)).getText().trim();
    String str2 = ((JTextField)td.getComponent(3)).getText().trim();
    String str3 = ((JTextField)td.getComponent(5)).getText().trim();
    String str4 = ((JTextField)td.getComponent(7)).getText().trim();
    try
    {
      tool.openKeyStore(str1.length() == 0 ? null : str1, str2.length() == 0 ? null : str2, str3.length() == 0 ? null : str3, str4.length() == 0 ? null : str4);
      tool.modified = true;
    }
    catch (Exception localException)
    {
      MessageFormat localMessageFormat = new MessageFormat(PolicyTool.getMessage("Unable.to.open.KeyStore.ex.toString."));
      Object[] arrayOfObject = { localException.toString() };
      tw.displayErrorDialog(td, localMessageFormat.format(arrayOfObject));
      return;
    }
    td.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ChangeKeyStoreOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */