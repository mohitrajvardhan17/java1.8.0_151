package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

class SplitPaneDividerVerticalState
  extends State
{
  SplitPaneDividerVerticalState()
  {
    super("Vertical");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JSplitPane)) && (((JSplitPane)paramJComponent).getOrientation() == 1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\SplitPaneDividerVerticalState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */