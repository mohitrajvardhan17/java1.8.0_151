package sun.security.tools.policytool;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ChildWindowListener
  implements WindowListener
{
  private ToolDialog td;
  
  ChildWindowListener(ToolDialog paramToolDialog)
  {
    td = paramToolDialog;
  }
  
  public void windowOpened(WindowEvent paramWindowEvent) {}
  
  public void windowClosing(WindowEvent paramWindowEvent)
  {
    td.setVisible(false);
    td.dispose();
  }
  
  public void windowClosed(WindowEvent paramWindowEvent) {}
  
  public void windowIconified(WindowEvent paramWindowEvent) {}
  
  public void windowDeiconified(WindowEvent paramWindowEvent) {}
  
  public void windowActivated(WindowEvent paramWindowEvent) {}
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ChildWindowListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */