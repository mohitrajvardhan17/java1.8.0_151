package java.awt.event;

import java.util.EventListener;

public abstract interface FocusListener
  extends EventListener
{
  public abstract void focusGained(FocusEvent paramFocusEvent);
  
  public abstract void focusLost(FocusEvent paramFocusEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\FocusListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */