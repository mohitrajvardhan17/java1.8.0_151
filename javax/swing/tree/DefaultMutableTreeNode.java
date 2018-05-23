package javax.swing.tree;

import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

public class DefaultMutableTreeNode
  implements Cloneable, MutableTreeNode, Serializable
{
  private static final long serialVersionUID = -4298474751201349152L;
  public static final Enumeration<TreeNode> EMPTY_ENUMERATION = ;
  protected MutableTreeNode parent = null;
  protected Vector children;
  protected transient Object userObject;
  protected boolean allowsChildren;
  
  public DefaultMutableTreeNode()
  {
    this(null);
  }
  
  public DefaultMutableTreeNode(Object paramObject)
  {
    this(paramObject, true);
  }
  
  public DefaultMutableTreeNode(Object paramObject, boolean paramBoolean)
  {
    allowsChildren = paramBoolean;
    userObject = paramObject;
  }
  
  public void insert(MutableTreeNode paramMutableTreeNode, int paramInt)
  {
    if (!allowsChildren) {
      throw new IllegalStateException("node does not allow children");
    }
    if (paramMutableTreeNode == null) {
      throw new IllegalArgumentException("new child is null");
    }
    if (isNodeAncestor(paramMutableTreeNode)) {
      throw new IllegalArgumentException("new child is an ancestor");
    }
    MutableTreeNode localMutableTreeNode = (MutableTreeNode)paramMutableTreeNode.getParent();
    if (localMutableTreeNode != null) {
      localMutableTreeNode.remove(paramMutableTreeNode);
    }
    paramMutableTreeNode.setParent(this);
    if (children == null) {
      children = new Vector();
    }
    children.insertElementAt(paramMutableTreeNode, paramInt);
  }
  
  public void remove(int paramInt)
  {
    MutableTreeNode localMutableTreeNode = (MutableTreeNode)getChildAt(paramInt);
    children.removeElementAt(paramInt);
    localMutableTreeNode.setParent(null);
  }
  
  @Transient
  public void setParent(MutableTreeNode paramMutableTreeNode)
  {
    parent = paramMutableTreeNode;
  }
  
  public TreeNode getParent()
  {
    return parent;
  }
  
  public TreeNode getChildAt(int paramInt)
  {
    if (children == null) {
      throw new ArrayIndexOutOfBoundsException("node has no children");
    }
    return (TreeNode)children.elementAt(paramInt);
  }
  
  public int getChildCount()
  {
    if (children == null) {
      return 0;
    }
    return children.size();
  }
  
  public int getIndex(TreeNode paramTreeNode)
  {
    if (paramTreeNode == null) {
      throw new IllegalArgumentException("argument is null");
    }
    if (!isNodeChild(paramTreeNode)) {
      return -1;
    }
    return children.indexOf(paramTreeNode);
  }
  
  public Enumeration children()
  {
    if (children == null) {
      return EMPTY_ENUMERATION;
    }
    return children.elements();
  }
  
  public void setAllowsChildren(boolean paramBoolean)
  {
    if (paramBoolean != allowsChildren)
    {
      allowsChildren = paramBoolean;
      if (!allowsChildren) {
        removeAllChildren();
      }
    }
  }
  
  public boolean getAllowsChildren()
  {
    return allowsChildren;
  }
  
  public void setUserObject(Object paramObject)
  {
    userObject = paramObject;
  }
  
  public Object getUserObject()
  {
    return userObject;
  }
  
  public void removeFromParent()
  {
    MutableTreeNode localMutableTreeNode = (MutableTreeNode)getParent();
    if (localMutableTreeNode != null) {
      localMutableTreeNode.remove(this);
    }
  }
  
  public void remove(MutableTreeNode paramMutableTreeNode)
  {
    if (paramMutableTreeNode == null) {
      throw new IllegalArgumentException("argument is null");
    }
    if (!isNodeChild(paramMutableTreeNode)) {
      throw new IllegalArgumentException("argument is not a child");
    }
    remove(getIndex(paramMutableTreeNode));
  }
  
  public void removeAllChildren()
  {
    for (int i = getChildCount() - 1; i >= 0; i--) {
      remove(i);
    }
  }
  
  public void add(MutableTreeNode paramMutableTreeNode)
  {
    if ((paramMutableTreeNode != null) && (paramMutableTreeNode.getParent() == this)) {
      insert(paramMutableTreeNode, getChildCount() - 1);
    } else {
      insert(paramMutableTreeNode, getChildCount());
    }
  }
  
  public boolean isNodeAncestor(TreeNode paramTreeNode)
  {
    if (paramTreeNode == null) {
      return false;
    }
    Object localObject = this;
    do
    {
      if (localObject == paramTreeNode) {
        return true;
      }
    } while ((localObject = ((TreeNode)localObject).getParent()) != null);
    return false;
  }
  
  public boolean isNodeDescendant(DefaultMutableTreeNode paramDefaultMutableTreeNode)
  {
    if (paramDefaultMutableTreeNode == null) {
      return false;
    }
    return paramDefaultMutableTreeNode.isNodeAncestor(this);
  }
  
  public TreeNode getSharedAncestor(DefaultMutableTreeNode paramDefaultMutableTreeNode)
  {
    if (paramDefaultMutableTreeNode == this) {
      return this;
    }
    if (paramDefaultMutableTreeNode == null) {
      return null;
    }
    int i = getLevel();
    int j = paramDefaultMutableTreeNode.getLevel();
    int k;
    Object localObject1;
    Object localObject2;
    if (j > i)
    {
      k = j - i;
      localObject1 = paramDefaultMutableTreeNode;
      localObject2 = this;
    }
    else
    {
      k = i - j;
      localObject1 = this;
      localObject2 = paramDefaultMutableTreeNode;
    }
    while (k > 0)
    {
      localObject1 = ((TreeNode)localObject1).getParent();
      k--;
    }
    do
    {
      if (localObject1 == localObject2) {
        return (TreeNode)localObject1;
      }
      localObject1 = ((TreeNode)localObject1).getParent();
      localObject2 = ((TreeNode)localObject2).getParent();
    } while (localObject1 != null);
    if ((localObject1 != null) || (localObject2 != null)) {
      throw new Error("nodes should be null");
    }
    return null;
  }
  
  public boolean isNodeRelated(DefaultMutableTreeNode paramDefaultMutableTreeNode)
  {
    return (paramDefaultMutableTreeNode != null) && (getRoot() == paramDefaultMutableTreeNode.getRoot());
  }
  
  public int getDepth()
  {
    Object localObject = null;
    Enumeration localEnumeration = breadthFirstEnumeration();
    while (localEnumeration.hasMoreElements()) {
      localObject = localEnumeration.nextElement();
    }
    if (localObject == null) {
      throw new Error("nodes should be null");
    }
    return ((DefaultMutableTreeNode)localObject).getLevel() - getLevel();
  }
  
  public int getLevel()
  {
    int i = 0;
    Object localObject = this;
    while ((localObject = ((TreeNode)localObject).getParent()) != null) {
      i++;
    }
    return i;
  }
  
  public TreeNode[] getPath()
  {
    return getPathToRoot(this, 0);
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
      arrayOfTreeNode = getPathToRoot(paramTreeNode.getParent(), paramInt);
      arrayOfTreeNode[(arrayOfTreeNode.length - paramInt)] = paramTreeNode;
    }
    return arrayOfTreeNode;
  }
  
  public Object[] getUserObjectPath()
  {
    TreeNode[] arrayOfTreeNode = getPath();
    Object[] arrayOfObject = new Object[arrayOfTreeNode.length];
    for (int i = 0; i < arrayOfTreeNode.length; i++) {
      arrayOfObject[i] = ((DefaultMutableTreeNode)arrayOfTreeNode[i]).getUserObject();
    }
    return arrayOfObject;
  }
  
  public TreeNode getRoot()
  {
    Object localObject1 = this;
    Object localObject2;
    do
    {
      localObject2 = localObject1;
      localObject1 = ((TreeNode)localObject1).getParent();
    } while (localObject1 != null);
    return (TreeNode)localObject2;
  }
  
  public boolean isRoot()
  {
    return getParent() == null;
  }
  
  public DefaultMutableTreeNode getNextNode()
  {
    if (getChildCount() == 0)
    {
      DefaultMutableTreeNode localDefaultMutableTreeNode1 = getNextSibling();
      if (localDefaultMutableTreeNode1 == null) {
        for (DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();; localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)localDefaultMutableTreeNode2.getParent())
        {
          if (localDefaultMutableTreeNode2 == null) {
            return null;
          }
          localDefaultMutableTreeNode1 = localDefaultMutableTreeNode2.getNextSibling();
          if (localDefaultMutableTreeNode1 != null) {
            return localDefaultMutableTreeNode1;
          }
        }
      }
      return localDefaultMutableTreeNode1;
    }
    return (DefaultMutableTreeNode)getChildAt(0);
  }
  
  public DefaultMutableTreeNode getPreviousNode()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    if (localDefaultMutableTreeNode2 == null) {
      return null;
    }
    DefaultMutableTreeNode localDefaultMutableTreeNode1 = getPreviousSibling();
    if (localDefaultMutableTreeNode1 != null)
    {
      if (localDefaultMutableTreeNode1.getChildCount() == 0) {
        return localDefaultMutableTreeNode1;
      }
      return localDefaultMutableTreeNode1.getLastLeaf();
    }
    return localDefaultMutableTreeNode2;
  }
  
  public Enumeration preorderEnumeration()
  {
    return new PreorderEnumeration(this);
  }
  
  public Enumeration postorderEnumeration()
  {
    return new PostorderEnumeration(this);
  }
  
  public Enumeration breadthFirstEnumeration()
  {
    return new BreadthFirstEnumeration(this);
  }
  
  public Enumeration depthFirstEnumeration()
  {
    return postorderEnumeration();
  }
  
  public Enumeration pathFromAncestorEnumeration(TreeNode paramTreeNode)
  {
    return new PathBetweenNodesEnumeration(paramTreeNode, this);
  }
  
  public boolean isNodeChild(TreeNode paramTreeNode)
  {
    boolean bool;
    if (paramTreeNode == null) {
      bool = false;
    } else if (getChildCount() == 0) {
      bool = false;
    } else {
      bool = paramTreeNode.getParent() == this;
    }
    return bool;
  }
  
  public TreeNode getFirstChild()
  {
    if (getChildCount() == 0) {
      throw new NoSuchElementException("node has no children");
    }
    return getChildAt(0);
  }
  
  public TreeNode getLastChild()
  {
    if (getChildCount() == 0) {
      throw new NoSuchElementException("node has no children");
    }
    return getChildAt(getChildCount() - 1);
  }
  
  public TreeNode getChildAfter(TreeNode paramTreeNode)
  {
    if (paramTreeNode == null) {
      throw new IllegalArgumentException("argument is null");
    }
    int i = getIndex(paramTreeNode);
    if (i == -1) {
      throw new IllegalArgumentException("node is not a child");
    }
    if (i < getChildCount() - 1) {
      return getChildAt(i + 1);
    }
    return null;
  }
  
  public TreeNode getChildBefore(TreeNode paramTreeNode)
  {
    if (paramTreeNode == null) {
      throw new IllegalArgumentException("argument is null");
    }
    int i = getIndex(paramTreeNode);
    if (i == -1) {
      throw new IllegalArgumentException("argument is not a child");
    }
    if (i > 0) {
      return getChildAt(i - 1);
    }
    return null;
  }
  
  public boolean isNodeSibling(TreeNode paramTreeNode)
  {
    boolean bool;
    if (paramTreeNode == null)
    {
      bool = false;
    }
    else if (paramTreeNode == this)
    {
      bool = true;
    }
    else
    {
      TreeNode localTreeNode = getParent();
      bool = (localTreeNode != null) && (localTreeNode == paramTreeNode.getParent());
      if ((bool) && (!((DefaultMutableTreeNode)getParent()).isNodeChild(paramTreeNode))) {
        throw new Error("sibling has different parent");
      }
    }
    return bool;
  }
  
  public int getSiblingCount()
  {
    TreeNode localTreeNode = getParent();
    if (localTreeNode == null) {
      return 1;
    }
    return localTreeNode.getChildCount();
  }
  
  public DefaultMutableTreeNode getNextSibling()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    DefaultMutableTreeNode localDefaultMutableTreeNode1;
    if (localDefaultMutableTreeNode2 == null) {
      localDefaultMutableTreeNode1 = null;
    } else {
      localDefaultMutableTreeNode1 = (DefaultMutableTreeNode)localDefaultMutableTreeNode2.getChildAfter(this);
    }
    if ((localDefaultMutableTreeNode1 != null) && (!isNodeSibling(localDefaultMutableTreeNode1))) {
      throw new Error("child of parent is not a sibling");
    }
    return localDefaultMutableTreeNode1;
  }
  
  public DefaultMutableTreeNode getPreviousSibling()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    DefaultMutableTreeNode localDefaultMutableTreeNode1;
    if (localDefaultMutableTreeNode2 == null) {
      localDefaultMutableTreeNode1 = null;
    } else {
      localDefaultMutableTreeNode1 = (DefaultMutableTreeNode)localDefaultMutableTreeNode2.getChildBefore(this);
    }
    if ((localDefaultMutableTreeNode1 != null) && (!isNodeSibling(localDefaultMutableTreeNode1))) {
      throw new Error("child of parent is not a sibling");
    }
    return localDefaultMutableTreeNode1;
  }
  
  public boolean isLeaf()
  {
    return getChildCount() == 0;
  }
  
  public DefaultMutableTreeNode getFirstLeaf()
  {
    for (DefaultMutableTreeNode localDefaultMutableTreeNode = this; !localDefaultMutableTreeNode.isLeaf(); localDefaultMutableTreeNode = (DefaultMutableTreeNode)localDefaultMutableTreeNode.getFirstChild()) {}
    return localDefaultMutableTreeNode;
  }
  
  public DefaultMutableTreeNode getLastLeaf()
  {
    for (DefaultMutableTreeNode localDefaultMutableTreeNode = this; !localDefaultMutableTreeNode.isLeaf(); localDefaultMutableTreeNode = (DefaultMutableTreeNode)localDefaultMutableTreeNode.getLastChild()) {}
    return localDefaultMutableTreeNode;
  }
  
  public DefaultMutableTreeNode getNextLeaf()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    if (localDefaultMutableTreeNode2 == null) {
      return null;
    }
    DefaultMutableTreeNode localDefaultMutableTreeNode1 = getNextSibling();
    if (localDefaultMutableTreeNode1 != null) {
      return localDefaultMutableTreeNode1.getFirstLeaf();
    }
    return localDefaultMutableTreeNode2.getNextLeaf();
  }
  
  public DefaultMutableTreeNode getPreviousLeaf()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)getParent();
    if (localDefaultMutableTreeNode2 == null) {
      return null;
    }
    DefaultMutableTreeNode localDefaultMutableTreeNode1 = getPreviousSibling();
    if (localDefaultMutableTreeNode1 != null) {
      return localDefaultMutableTreeNode1.getLastLeaf();
    }
    return localDefaultMutableTreeNode2.getPreviousLeaf();
  }
  
  public int getLeafCount()
  {
    int i = 0;
    Enumeration localEnumeration = breadthFirstEnumeration();
    while (localEnumeration.hasMoreElements())
    {
      TreeNode localTreeNode = (TreeNode)localEnumeration.nextElement();
      if (localTreeNode.isLeaf()) {
        i++;
      }
    }
    if (i < 1) {
      throw new Error("tree has zero leaves");
    }
    return i;
  }
  
  public String toString()
  {
    if (userObject == null) {
      return "";
    }
    return userObject.toString();
  }
  
  public Object clone()
  {
    DefaultMutableTreeNode localDefaultMutableTreeNode;
    try
    {
      localDefaultMutableTreeNode = (DefaultMutableTreeNode)super.clone();
      children = null;
      parent = null;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new Error(localCloneNotSupportedException.toString());
    }
    return localDefaultMutableTreeNode;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    Object[] arrayOfObject;
    if ((userObject != null) && ((userObject instanceof Serializable)))
    {
      arrayOfObject = new Object[2];
      arrayOfObject[0] = "userObject";
      arrayOfObject[1] = userObject;
    }
    else
    {
      arrayOfObject = new Object[0];
    }
    paramObjectOutputStream.writeObject(arrayOfObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
    if ((arrayOfObject.length > 0) && (arrayOfObject[0].equals("userObject"))) {
      userObject = arrayOfObject[1];
    }
  }
  
  final class BreadthFirstEnumeration
    implements Enumeration<TreeNode>
  {
    protected Queue queue;
    
    public BreadthFirstEnumeration(TreeNode paramTreeNode)
    {
      Vector localVector = new Vector(1);
      localVector.addElement(paramTreeNode);
      queue = new Queue();
      queue.enqueue(localVector.elements());
    }
    
    public boolean hasMoreElements()
    {
      return (!queue.isEmpty()) && (((Enumeration)queue.firstObject()).hasMoreElements());
    }
    
    public TreeNode nextElement()
    {
      Enumeration localEnumeration1 = (Enumeration)queue.firstObject();
      TreeNode localTreeNode = (TreeNode)localEnumeration1.nextElement();
      Enumeration localEnumeration2 = localTreeNode.children();
      if (!localEnumeration1.hasMoreElements()) {
        queue.dequeue();
      }
      if (localEnumeration2.hasMoreElements()) {
        queue.enqueue(localEnumeration2);
      }
      return localTreeNode;
    }
    
    final class Queue
    {
      QNode head;
      QNode tail;
      
      Queue() {}
      
      public void enqueue(Object paramObject)
      {
        if (head == null)
        {
          head = (tail = new QNode(paramObject, null));
        }
        else
        {
          tail.next = new QNode(paramObject, null);
          tail = tail.next;
        }
      }
      
      public Object dequeue()
      {
        if (head == null) {
          throw new NoSuchElementException("No more elements");
        }
        Object localObject = head.object;
        QNode localQNode = head;
        head = head.next;
        if (head == null) {
          tail = null;
        } else {
          next = null;
        }
        return localObject;
      }
      
      public Object firstObject()
      {
        if (head == null) {
          throw new NoSuchElementException("No more elements");
        }
        return head.object;
      }
      
      public boolean isEmpty()
      {
        return head == null;
      }
      
      final class QNode
      {
        public Object object;
        public QNode next;
        
        public QNode(Object paramObject, QNode paramQNode)
        {
          object = paramObject;
          next = paramQNode;
        }
      }
    }
  }
  
  final class PathBetweenNodesEnumeration
    implements Enumeration<TreeNode>
  {
    protected Stack<TreeNode> stack;
    
    public PathBetweenNodesEnumeration(TreeNode paramTreeNode1, TreeNode paramTreeNode2)
    {
      if ((paramTreeNode1 == null) || (paramTreeNode2 == null)) {
        throw new IllegalArgumentException("argument is null");
      }
      stack = new Stack();
      stack.push(paramTreeNode2);
      TreeNode localTreeNode = paramTreeNode2;
      while (localTreeNode != paramTreeNode1)
      {
        localTreeNode = localTreeNode.getParent();
        if ((localTreeNode == null) && (paramTreeNode2 != paramTreeNode1)) {
          throw new IllegalArgumentException("node " + paramTreeNode1 + " is not an ancestor of " + paramTreeNode2);
        }
        stack.push(localTreeNode);
      }
    }
    
    public boolean hasMoreElements()
    {
      return stack.size() > 0;
    }
    
    public TreeNode nextElement()
    {
      try
      {
        return (TreeNode)stack.pop();
      }
      catch (EmptyStackException localEmptyStackException)
      {
        throw new NoSuchElementException("No more elements");
      }
    }
  }
  
  final class PostorderEnumeration
    implements Enumeration<TreeNode>
  {
    protected TreeNode root;
    protected Enumeration<TreeNode> children;
    protected Enumeration<TreeNode> subtree;
    
    public PostorderEnumeration(TreeNode paramTreeNode)
    {
      root = paramTreeNode;
      children = root.children();
      subtree = DefaultMutableTreeNode.EMPTY_ENUMERATION;
    }
    
    public boolean hasMoreElements()
    {
      return root != null;
    }
    
    public TreeNode nextElement()
    {
      TreeNode localTreeNode;
      if (subtree.hasMoreElements())
      {
        localTreeNode = (TreeNode)subtree.nextElement();
      }
      else if (children.hasMoreElements())
      {
        subtree = new PostorderEnumeration(DefaultMutableTreeNode.this, (TreeNode)children.nextElement());
        localTreeNode = (TreeNode)subtree.nextElement();
      }
      else
      {
        localTreeNode = root;
        root = null;
      }
      return localTreeNode;
    }
  }
  
  private final class PreorderEnumeration
    implements Enumeration<TreeNode>
  {
    private final Stack<Enumeration> stack = new Stack();
    
    public PreorderEnumeration(TreeNode paramTreeNode)
    {
      Vector localVector = new Vector(1);
      localVector.addElement(paramTreeNode);
      stack.push(localVector.elements());
    }
    
    public boolean hasMoreElements()
    {
      return (!stack.empty()) && (((Enumeration)stack.peek()).hasMoreElements());
    }
    
    public TreeNode nextElement()
    {
      Enumeration localEnumeration1 = (Enumeration)stack.peek();
      TreeNode localTreeNode = (TreeNode)localEnumeration1.nextElement();
      Enumeration localEnumeration2 = localTreeNode.children();
      if (!localEnumeration1.hasMoreElements()) {
        stack.pop();
      }
      if (localEnumeration2.hasMoreElements()) {
        stack.push(localEnumeration2);
      }
      return localTreeNode;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\DefaultMutableTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */