package java.awt.event;

import java.util.EventListener;

public abstract interface InputMethodListener
  extends EventListener
{
  public abstract void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent);
  
  public abstract void caretPositionChanged(InputMethodEvent paramInputMethodEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\InputMethodListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */