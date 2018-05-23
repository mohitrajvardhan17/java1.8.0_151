package java.awt.event;

import java.util.EventListener;

public abstract interface WindowFocusListener
  extends EventListener
{
  public abstract void windowGainedFocus(WindowEvent paramWindowEvent);
  
  public abstract void windowLostFocus(WindowEvent paramWindowEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\WindowFocusListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */