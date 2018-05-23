package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StatusOKButtonListener
  implements ActionListener
{
  private ToolDialog sd;
  
  StatusOKButtonListener(ToolDialog paramToolDialog)
  {
    sd = paramToolDialog;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    sd.setVisible(false);
    sd.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\StatusOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */