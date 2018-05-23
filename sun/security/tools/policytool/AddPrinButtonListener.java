package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AddPrinButtonListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog td;
  private boolean editPolicyEntry;
  
  AddPrinButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
    td = paramToolDialog;
    editPolicyEntry = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    td.displayPrincipalDialog(editPolicyEntry, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\AddPrinButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */