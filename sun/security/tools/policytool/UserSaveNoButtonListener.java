package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveNoButtonListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog us;
  private int select;
  
  UserSaveNoButtonListener(ToolDialog paramToolDialog, PolicyTool paramPolicyTool, ToolWindow paramToolWindow, int paramInt)
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
    us.userSaveContinue(tool, tw, us, select);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\UserSaveNoButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */