package javax.swing.tree;

import java.util.Enumeration;

public abstract interface TreeNode
{
  public abstract TreeNode getChildAt(int paramInt);
  
  public abstract int getChildCount();
  
  public abstract TreeNode getParent();
  
  public abstract int getIndex(TreeNode paramTreeNode);
  
  public abstract boolean getAllowsChildren();
  
  public abstract boolean isLeaf();
  
  public abstract Enumeration children();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\TreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */