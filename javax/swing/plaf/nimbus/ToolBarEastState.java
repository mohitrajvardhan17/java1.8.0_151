package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarEastState
  extends State
{
  ToolBarEastState()
  {
    super("East");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return ((paramJComponent instanceof JToolBar)) && (NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)paramJComponent) == "East");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ToolBarEastState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */