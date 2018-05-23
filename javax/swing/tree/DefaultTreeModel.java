package javax.swing.tree;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class DefaultTreeModel
  implements Serializable, TreeModel
{
  protected TreeNode root;
  protected EventListenerList listenerList = new EventListenerList();
  protected boolean asksAllowsChildren;
  
  @ConstructorProperties({"root"})
  public DefaultTreeModel(TreeNode paramTreeNode)
  {
    this(paramTreeNode, false);
  }
  
  public DefaultTreeModel(TreeNode paramTreeNode, boolean paramBoolean)
  {
    root = paramTreeNode;
    asksAllowsChildren = paramBoolean;
  }
  
  public void setAsksAllowsChildren(boolean paramBoolean)
  {
    asksAllowsChildren = paramBoolean;
  }
  
  public boolean asksAllowsChildren()
  {
    return asksAllowsChildren;
  }
  
  public void setRoot(TreeNode paramTreeNode)
  {
    TreeNode localTreeNode = root;
    root = paramTreeNode;
    if ((paramTreeNode == null) && (localTreeNode != null)) {
      fireTreeStructureChanged(this, null);
    } else {
      nodeStructureChanged(paramTreeNode);
    }
  }
  
  public Object getRoot()
  {
    return root;
  }
  
  public int getIndexOfChild(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) || (paramObject2 == null)) {
      return -1;
    }
    return ((TreeNode)paramObject1).getIndex((TreeNode)paramObject2);
  }
  
  public Object getChild(Object paramObject, int paramInt)
  {
    return ((TreeNode)paramObject).getChildAt(paramInt);
  }
  
  public int getChildCount(Object paramObject)
  {
    return ((TreeNode)paramObject).getChildCount();
  }
  
  public boolean isLeaf(Object paramObject)
  {
    if (asksAllowsChildren) {
      return !((TreeNode)paramObject).getAllowsChildren();
    }
    return ((TreeNode)paramObject).isLeaf();
  }
  
  public void reload()
  {
    reload(root);
  }
  
  public void valueForPathChanged(TreePath paramTreePath, Object paramObject)
  {
    MutableTreeNode localMutableTreeNode = (MutableTreeNode)paramTreePath.getLastPathComponent();
    localMutableTreeNode.setUserObject(paramObject);
    nodeChanged(localMutableTreeNode);
  }
  
  public void insertNodeInto(MutableTreeNode paramMutableTreeNode1, MutableTreeNode paramMutableTreeNode2, int paramInt)
  {
    paramMutableTreeNode2.insert(paramMutableTreeNode1, paramInt);
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = paramInt;
    nodesWereInserted(paramMutableTreeNode2, arrayOfInt);
  }
  
  public void removeNodeFromParent(MutableTreeNode paramMutableTreeNode)
  {
    MutableTreeNode localMutableTreeNode = (MutableTreeNode)paramMutableTreeNode.getParent();
    if (localMutableTreeNode == null) {
      throw new IllegalArgumentException("node does not have a parent.");
    }
    int[] arrayOfInt = new int[1];
    Object[] arrayOfObject = new Object[1];
    arrayOfInt[0] = localMutableTreeNode.getIndex(paramMutableTreeNode);
    localMutableTreeNode.remove(arrayOfInt[0]);
    arrayOfObject[0] = paramMutableTreeNode;
    nodesWereRemoved(localMutableTreeNode, arrayOfInt, arrayOfObject);
  }
  
  public void nodeChanged(TreeNode paramTreeNode)
  {
    if ((listenerList != null) && (paramTreeNode != null))
    {
      TreeNode localTreeNode = paramTreeNode.getParent();
      if (localTreeNode != null)
      {
        int i = localTreeNode.getIndex(paramTreeNode);
        if (i != -1)
        {
          int[] arrayOfInt = new int[1];
          arrayOfInt[0] = i;
          nodesChanged(localTreeNode, arrayOfInt);
        }
      }
      else if (paramTreeNode == getRoot())
      {
        nodesChanged(paramTreeNode, null);
      }
    }
  }
  
  public void reload(TreeNode paramTreeNode)
  {
    if (paramTreeNode != null) {
      fireTreeStructureChanged(this, getPathToRoot(paramTreeNode), null, null);
    }
  }
  
  public void nodesWereInserted(TreeNode paramTreeNode, int[] paramArrayOfInt)
  {
    if ((listenerList != null) && (paramTreeNode != null) && (paramArrayOfInt != null) && (paramArrayOfInt.length > 0))
    {
      int i = paramArrayOfInt.length;
      Object[] arrayOfObject = new Object[i];
      for (int j = 0; j < i; j++) {
        arrayOfObject[j] = paramTreeNode.getChildAt(paramArrayOfInt[j]);
      }
      fireTreeNodesInserted(this, getPathToRoot(paramTreeNode), paramArrayOfInt, arrayOfObject);
    }
  }
  
  public void nodesWereRemoved(TreeNode paramTreeNode, int[] paramArrayOfInt, Object[] paramArrayOfObject)
  {
    if ((paramTreeNode != null) && (paramArrayOfInt != null)) {
      fireTreeNodesRemoved(this, getPathToRoot(paramTreeNode), paramArrayOfInt, paramArrayOfObject);
    }
  }
  
  public void nodesChanged(TreeNode paramTreeNode, int[] paramArrayOfInt)
  {
    if (paramTreeNode != null) {
      if (paramArrayOfInt != null)
      {
        int i = paramArrayOfInt.length;
        if (i > 0)
        {
          Object[] arrayOfObject = new Object[i];
          for (int j = 0; j < i; j++) {
            arrayOfObject[j] = paramTreeNode.getChildAt(paramArrayOfInt[j]);
          }
          fireTreeNodesChanged(this, getPathToRoot(paramTreeNode), paramArrayOfInt, arrayOfObject);
        }
      }
      else if (paramTreeNode == getRoot())
      {
        fireTreeNodesChanged(this, getPathToRoot(paramTreeNode), null, null);
      }
    }
  }
  
  public void nodeStructureChanged(TreeNode paramTreeNode)
  {
    if (paramTreeNode != null) {
      fireTreeStructureChanged(this, getPathToRoot(paramTreeNode), null, null);
    }
  }
  
  public TreeNode[] getPathToRoot(TreeNode paramTreeNode)
  {
    return getPathToRoot(paramTreeNode, 0);
  }
  
  protected TreeNode[] getPathToRoot(TreeNode paramTreeNode, int paramInt)
  {
    TreeNode[] arrayOfTreeNode;
    if (paramTreeNode == null)
    {
      if (paramInt == 0) {
        return null;
      }
      arrayOfTreeNode = new TreeNode[paramInt];
    }
    else
    {
      paramInt++;
      if (paramTreeNode == root) {
        arrayOfTreeNode = new TreeNode[paramInt];
      } else {
        arrayOfTreeNode = getPathToRoot(paramTreeNode.getParent(), paramInt);
      }
      arrayOfTreeNode[(arrayOfTreeNode.length - paramInt)] = paramTreeNode;
    }
    return arrayOfTreeNode;
  }
  
  public void addTreeModelListener(TreeModelListener paramTreeModelListener)
  {
    listenerList.add(TreeModelListener.class, paramTreeModelListener);
  }
  
  public void removeTreeModelListener(TreeModelListener paramTreeModelListener)
  {
    listenerList.remove(TreeModelListener.class, paramTreeModelListener);
  }
  
  public TreeModelListener[] getTreeModelListeners()
  {
    return (TreeModelListener[])listenerList.getListeners(TreeModelListener.class);
  }
  
  protected void fireTreeNodesChanged(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class)
      {
        if (localTreeModelEvent == null) {
          localTreeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2);
        }
        ((TreeModelListener)arrayOfObject[(i + 1)]).treeNodesChanged(localTreeModelEvent);
      }
    }
  }
  
  protected void fireTreeNodesInserted(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class)
      {
        if (localTreeModelEvent == null) {
          localTreeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2);
        }
        ((TreeModelListener)arrayOfObject[(i + 1)]).treeNodesInserted(localTreeModelEvent);
      }
    }
  }
  
  protected void fireTreeNodesRemoved(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class)
      {
        if (localTreeModelEvent == null) {
          localTreeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2);
        }
        ((TreeModelListener)arrayOfObject[(i + 1)]).treeNodesRemoved(localTreeModelEvent);
      }
    }
  }
  
  protected void fireTreeStructureChanged(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class)
      {
        if (localTreeModelEvent == null) {
          localTreeModelEvent = new TreeModelEvent(paramObject, paramArrayOfObject1, paramArrayOfInt, paramArrayOfObject2);
        }
        ((TreeModelListener)arrayOfObject[(i + 1)]).treeStructureChanged(localTreeModelEvent);
      }
    }
  }
  
  private void fireTreeStructureChanged(Object paramObject, TreePath paramTreePath)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    TreeModelEvent localTreeModelEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeModelListener.class)
      {
        if (localTreeModelEvent == null) {
          localTreeModelEvent = new TreeModelEvent(paramObject, paramTreePath);
        }
        ((TreeModelListener)arrayOfObject[(i + 1)]).treeStructureChanged(localTreeModelEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Vector localVector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if ((root != null) && ((root instanceof Serializable)))
    {
      localVector.addElement("root");
      localVector.addElement(root);
    }
    paramObjectOutputStream.writeObject(localVector);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Vector localVector = (Vector)paramObjectInputStream.readObject();
    int i = 0;
    int j = localVector.size();
    if ((i < j) && (localVector.elementAt(i).equals("root")))
    {
      root = ((TreeNode)localVector.elementAt(++i));
      i++;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\DefaultTreeModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */