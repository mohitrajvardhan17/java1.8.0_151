package java.awt.event;

import java.util.EventListener;

public abstract interface WindowListener
  extends EventListener
{
  public abstract void windowOpened(WindowEvent paramWindowEvent);
  
  public abstract void windowClosing(WindowEvent paramWindowEvent);
  
  public abstract void windowClosed(WindowEvent paramWindowEvent);
  
  public abstract void windowIconified(WindowEvent paramWindowEvent);
  
  public abstract void windowDeiconified(WindowEvent paramWindowEvent);
  
  public abstract void windowActivated(WindowEvent paramWindowEvent);
  
  public abstract void windowDeactivated(WindowEvent paramWindowEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\WindowListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */