package javax.swing.tree;

import javax.swing.event.TreeModelListener;

public abstract interface TreeModel
{
  public abstract Object getRoot();
  
  public abstract Object getChild(Object paramObject, int paramInt);
  
  public abstract int getChildCount(Object paramObject);
  
  public abstract boolean isLeaf(Object paramObject);
  
  public abstract void valueForPathChanged(TreePath paramTreePath, Object paramObject);
  
  public abstract int getIndexOfChild(Object paramObject1, Object paramObject2);
  
  public abstract void addTreeModelListener(TreeModelListener paramTreeModelListener);
  
  public abstract void removeTreeModelListener(TreeModelListener paramTreeModelListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\TreeModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */