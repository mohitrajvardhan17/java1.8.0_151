package javax.swing.event;

import java.util.EventListener;

public abstract interface TreeModelListener
  extends EventListener
{
  public abstract void treeNodesChanged(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeNodesInserted(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeNodesRemoved(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeStructureChanged(TreeModelEvent paramTreeModelEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TreeModelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */