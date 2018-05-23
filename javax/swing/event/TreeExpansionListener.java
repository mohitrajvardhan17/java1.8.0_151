package javax.swing.event;

import java.util.EventListener;

public abstract interface TreeExpansionListener
  extends EventListener
{
  public abstract void treeExpanded(TreeExpansionEvent paramTreeExpansionEvent);
  
  public abstract void treeCollapsed(TreeExpansionEvent paramTreeExpansionEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TreeExpansionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */