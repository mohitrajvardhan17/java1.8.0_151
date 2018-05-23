package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class PolicyListListener
  extends MouseAdapter
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  
  PolicyListListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), tool, tw, true);
    localToolDialog.displayPolicyEntryDialog(true);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    if (paramMouseEvent.getClickCount() == 2) {
      actionPerformed(null);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PolicyListListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */