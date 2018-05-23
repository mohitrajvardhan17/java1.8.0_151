package sun.awt;

import java.awt.Component;

public abstract interface RequestFocusController
{
  public abstract boolean acceptRequestFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\RequestFocusController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */