package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

class ProgressBarFinishedState
  extends State
{
  ProgressBarFinishedState()
  {
    super("Finished");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JProgressBar)) && (((JProgressBar)paramJComponent).getPercentComplete() == 1.0D);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ProgressBarFinishedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */