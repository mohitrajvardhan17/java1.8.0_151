package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

class EditPermButtonListener
  extends MouseAdapter
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog td;
  private boolean editPolicyEntry;
  
  EditPermButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
    td = paramToolDialog;
    editPolicyEntry = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    JList localJList = (JList)td.getComponent(8);
    int i = localJList.getSelectedIndex();
    if (i < 0)
    {
      tw.displayErrorDialog(td, new Exception(PolicyTool.getMessage("No.permission.selected")));
      return;
    }
    td.displayPermissionDialog(editPolicyEntry, true);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    if (paramMouseEvent.getClickCount() == 2) {
      actionPerformed(null);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\EditPermButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */