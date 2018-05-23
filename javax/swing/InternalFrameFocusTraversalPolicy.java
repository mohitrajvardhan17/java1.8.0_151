package javax.swing;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;

public abstract class InternalFrameFocusTraversalPolicy
  extends FocusTraversalPolicy
{
  public InternalFrameFocusTraversalPolicy() {}
  
  public Component getInitialComponent(JInternalFrame paramJInternalFrame)
  {
    return getDefaultComponent(paramJInternalFrame);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\InternalFrameFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */