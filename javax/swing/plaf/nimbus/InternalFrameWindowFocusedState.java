package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

class InternalFrameWindowFocusedState
  extends State
{
  InternalFrameWindowFocusedState()
  {
    super("WindowFocused");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JInternalFrame)) && (((JInternalFrame)paramJComponent).isSelected());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\InternalFrameWindowFocusedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */