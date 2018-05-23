package sun.security.tools.policytool;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ToolWindowListener
  implements WindowListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  
  ToolWindowListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
  }
  
  public void windowOpened(WindowEvent paramWindowEvent) {}
  
  public void windowClosing(WindowEvent paramWindowEvent)
  {
    ToolDialog localToolDialog = new ToolDialog(PolicyTool.getMessage("Save.Changes"), tool, tw, true);
    localToolDialog.displayUserSave(1);
  }
  
  public void windowClosed(WindowEvent paramWindowEvent)
  {
    System.exit(0);
  }
  
  public void windowIconified(WindowEvent paramWindowEvent) {}
  
  public void windowDeiconified(WindowEvent paramWindowEvent) {}
  
  public void windowActivated(WindowEvent paramWindowEvent) {}
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ToolWindowListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */