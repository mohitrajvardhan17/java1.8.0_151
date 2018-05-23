package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveCancelButtonListener
  implements ActionListener
{
  private ToolDialog us;
  
  UserSaveCancelButtonListener(ToolDialog paramToolDialog)
  {
    us = paramToolDialog;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    us.setVisible(false);
    us.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\UserSaveCancelButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */