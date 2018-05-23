package javax.swing.tree;

public abstract interface MutableTreeNode
  extends TreeNode
{
  public abstract void insert(MutableTreeNode paramMutableTreeNode, int paramInt);
  
  public abstract void remove(int paramInt);
  
  public abstract void remove(MutableTreeNode paramMutableTreeNode);
  
  public abstract void setUserObject(Object paramObject);
  
  public abstract void removeFromParent();
  
  public abstract void setParent(MutableTreeNode paramMutableTreeNode);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\MutableTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */