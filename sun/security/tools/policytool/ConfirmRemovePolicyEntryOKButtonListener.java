package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JList;

class ConfirmRemovePolicyEntryOKButtonListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog us;
  
  ConfirmRemovePolicyEntryOKButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
    us = paramToolDialog;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    JList localJList = (JList)tw.getComponent(3);
    int i = localJList.getSelectedIndex();
    PolicyEntry[] arrayOfPolicyEntry = tool.getEntry();
    tool.removeEntry(arrayOfPolicyEntry[i]);
    DefaultListModel localDefaultListModel = new DefaultListModel();
    localJList = new JList(localDefaultListModel);
    localJList.setVisibleRowCount(15);
    localJList.setSelectionMode(0);
    localJList.addMouseListener(new PolicyListListener(tool, tw));
    arrayOfPolicyEntry = tool.getEntry();
    if (arrayOfPolicyEntry != null) {
      for (int j = 0; j < arrayOfPolicyEntry.length; j++) {
        localDefaultListModel.addElement(arrayOfPolicyEntry[j].headerToString());
      }
    }
    tw.replacePolicyList(localJList);
    us.setVisible(false);
    us.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ConfirmRemovePolicyEntryOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */