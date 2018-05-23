package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import javax.swing.JList;

class MainWindowListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  
  MainWindowListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    Object localObject;
    if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Add.Policy.Entry") == 0)
    {
      localObject = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), tool, tw, true);
      ((ToolDialog)localObject).displayPolicyEntryDialog(false);
    }
    else
    {
      int i;
      ToolDialog localToolDialog;
      if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Remove.Policy.Entry") == 0)
      {
        localObject = (JList)tw.getComponent(3);
        i = ((JList)localObject).getSelectedIndex();
        if (i < 0)
        {
          tw.displayErrorDialog(null, new Exception(PolicyTool.getMessage("No.Policy.Entry.selected")));
          return;
        }
        localToolDialog = new ToolDialog(PolicyTool.getMessage("Remove.Policy.Entry"), tool, tw, true);
        localToolDialog.displayConfirmRemovePolicyEntry();
      }
      else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Edit.Policy.Entry") == 0)
      {
        localObject = (JList)tw.getComponent(3);
        i = ((JList)localObject).getSelectedIndex();
        if (i < 0)
        {
          tw.displayErrorDialog(null, new Exception(PolicyTool.getMessage("No.Policy.Entry.selected")));
          return;
        }
        localToolDialog = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), tool, tw, true);
        localToolDialog.displayPolicyEntryDialog(true);
      }
      else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Edit") == 0)
      {
        localObject = new ToolDialog(PolicyTool.getMessage("KeyStore"), tool, tw, true);
        ((ToolDialog)localObject).keyStoreDialog(0);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\MainWindowListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */