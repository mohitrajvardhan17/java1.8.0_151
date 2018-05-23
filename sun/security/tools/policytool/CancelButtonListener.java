package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CancelButtonListener
  implements ActionListener
{
  private ToolDialog td;
  
  CancelButtonListener(ToolDialog paramToolDialog)
  {
    td = paramToolDialog;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    td.setVisible(false);
    td.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\CancelButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */