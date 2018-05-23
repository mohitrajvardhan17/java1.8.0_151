package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ErrorOKButtonListener
  implements ActionListener
{
  private ToolDialog ed;
  
  ErrorOKButtonListener(ToolDialog paramToolDialog)
  {
    ed = paramToolDialog;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    ed.setVisible(false);
    ed.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ErrorOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */