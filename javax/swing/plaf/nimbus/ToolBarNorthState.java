package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarNorthState
  extends State
{
  ToolBarNorthState()
  {
    super("North");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JToolBar)) && (NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)paramJComponent) == "North");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ToolBarNorthState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */