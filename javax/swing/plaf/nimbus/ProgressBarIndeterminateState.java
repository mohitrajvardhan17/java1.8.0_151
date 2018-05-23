package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

class ProgressBarIndeterminateState
  extends State
{
  ProgressBarIndeterminateState()
  {
    super("Indeterminate");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JProgressBar)) && (((JProgressBar)paramJComponent).isIndeterminate());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ProgressBarIndeterminateState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */