package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JTextField;

class UserSaveYesButtonListener
  implements ActionListener
{
  private ToolDialog us;
  private PolicyTool tool;
  private ToolWindow tw;
  private int select;
  
  UserSaveYesButtonListener(ToolDialog paramToolDialog, PolicyTool paramPolicyTool, ToolWindow paramToolWindow, int paramInt)
  {
    us = paramToolDialog;
    tool = paramPolicyTool;
    tw = paramToolWindow;
    select = paramInt;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    us.setVisible(false);
    us.dispose();
    try
    {
      String str = ((JTextField)tw.getComponent(1)).getText();
      if ((str == null) || (str.equals("")))
      {
        us.displaySaveAsDialog(select);
      }
      else
      {
        tool.savePolicy(str);
        MessageFormat localMessageFormat = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
        Object[] arrayOfObject = { str };
        tw.displayStatusDialog(null, localMessageFormat.format(arrayOfObject));
        us.userSaveContinue(tool, tw, us, select);
      }
    }
    catch (Exception localException)
    {
      tw.displayErrorDialog(null, localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\UserSaveYesButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */