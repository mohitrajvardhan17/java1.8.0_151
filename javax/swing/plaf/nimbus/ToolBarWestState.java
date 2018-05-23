package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarWestState
  extends State
{
  ToolBarWestState()
  {
    super("West");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JToolBar)) && (NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)paramJComponent) == "West");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ToolBarWestState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */