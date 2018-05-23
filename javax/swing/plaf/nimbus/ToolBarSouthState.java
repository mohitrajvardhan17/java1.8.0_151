package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarSouthState
  extends State
{
  ToolBarSouthState()
  {
    super("South");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JToolBar)) && (NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)paramJComponent) == "South");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ToolBarSouthState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */