package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class RemovePrinButtonListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog td;
  private boolean edit;
  
  RemovePrinButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
    td = paramToolDialog;
    edit = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    TaggedList localTaggedList = (TaggedList)td.getComponent(6);
    int i = localTaggedList.getSelectedIndex();
    if (i < 0)
    {
      tw.displayErrorDialog(td, new Exception(PolicyTool.getMessage("No.principal.selected")));
      return;
    }
    localTaggedList.removeTaggedItem(i);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\RemovePrinButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */