package javax.swing.event;

import java.util.EventListener;
import javax.swing.tree.ExpandVetoException;

public abstract interface TreeWillExpandListener
  extends EventListener
{
  public abstract void treeWillExpand(TreeExpansionEvent paramTreeExpansionEvent)
    throws ExpandVetoException;
  
  public abstract void treeWillCollapse(TreeExpansionEvent paramTreeExpansionEvent)
    throws ExpandVetoException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TreeWillExpandListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */