package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class TableHeaderRendererSortedState
  extends State
{
  TableHeaderRendererSortedState()
  {
    super("Sorted");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    String str = (String)paramJComponent.getClientProperty("Table.sortOrder");
    return (str != null) && (("ASCENDING".equals(str)) || ("DESCENDING".equals(str)));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\TableHeaderRendererSortedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */