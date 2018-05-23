package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import sun.swing.SwingUtilities2;

public class VariableHeightLayoutCache
  extends AbstractLayoutCache
{
  private Vector<Object> visibleNodes = new Vector();
  private boolean updateNodeSizes;
  private TreeStateNode root;
  private Rectangle boundsBuffer = new Rectangle();
  private Hashtable<TreePath, TreeStateNode> treePathMapping = new Hashtable();
  private Stack<Stack<TreePath>> tempStacks = new Stack();
  
  public VariableHeightLayoutCache() {}
  
  public void setModel(TreeModel paramTreeModel)
  {
    super.setModel(paramTreeModel);
    rebuild(false);
  }
  
  public void setRootVisible(boolean paramBoolean)
  {
    if ((isRootVisible() != paramBoolean) && (root != null))
    {
      if (paramBoolean)
      {
        root.updatePreferredSize(0);
        visibleNodes.insertElementAt(root, 0);
      }
      else if (visibleNodes.size() > 0)
      {
        visibleNodes.removeElementAt(0);
        if (treeSelectionModel != null) {
          treeSelectionModel.removeSelectionPath(root.getTreePath());
        }
      }
      if (treeSelectionModel != null) {
        treeSelectionModel.resetRowSelection();
      }
      if (getRowCount() > 0) {
        getNode(0).setYOrigin(0);
      }
      updateYLocationsFrom(0);
      visibleNodesChanged();
    }
    super.setRootVisible(paramBoolean);
  }
  
  public void setRowHeight(int paramInt)
  {
    if (paramInt != getRowHeight())
    {
      super.setRowHeight(paramInt);
      invalidateSizes();
      visibleNodesChanged();
    }
  }
  
  public void setNodeDimensions(AbstractLayoutCache.NodeDimensions paramNodeDimensions)
  {
    super.setNodeDimensions(paramNodeDimensions);
    invalidateSizes();
    visibleNodesChanged();
  }
  
  public void setExpandedState(TreePath paramTreePath, boolean paramBoolean)
  {
    if (paramTreePath != null) {
      if (paramBoolean)
      {
        ensurePathIsExpanded(paramTreePath, true);
      }
      else
      {
        TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, false, true);
        if (localTreeStateNode != null)
        {
          localTreeStateNode.makeVisible();
          localTreeStateNode.collapse();
        }
      }
    }
  }
  
  public boolean getExpandedState(TreePath paramTreePath)
  {
    TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, true, false);
    return (localTreeStateNode.isVisible()) && (localTreeStateNode.isExpanded());
  }
  
  public Rectangle getBounds(TreePath paramTreePath, Rectangle paramRectangle)
  {
    TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (localTreeStateNode != null)
    {
      if (updateNodeSizes) {
        updateNodeSizes(false);
      }
      return localTreeStateNode.getNodeBounds(paramRectangle);
    }
    return null;
  }
  
  public TreePath getPathForRow(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < getRowCount())) {
      return getNode(paramInt).getTreePath();
    }
    return null;
  }
  
  public int getRowForPath(TreePath paramTreePath)
  {
    if (paramTreePath == null) {
      return -1;
    }
    TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (localTreeStateNode != null) {
      return localTreeStateNode.getRow();
    }
    return -1;
  }
  
  public int getRowCount()
  {
    return visibleNodes.size();
  }
  
  public void invalidatePathBounds(TreePath paramTreePath)
  {
    TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (localTreeStateNode != null)
    {
      localTreeStateNode.markSizeInvalid();
      if (localTreeStateNode.isVisible()) {
        updateYLocationsFrom(localTreeStateNode.getRow());
      }
    }
  }
  
  public int getPreferredHeight()
  {
    int i = getRowCount();
    if (i > 0)
    {
      TreeStateNode localTreeStateNode = getNode(i - 1);
      return localTreeStateNode.getYOrigin() + localTreeStateNode.getPreferredHeight();
    }
    return 0;
  }
  
  public int getPreferredWidth(Rectangle paramRectangle)
  {
    if (updateNodeSizes) {
      updateNodeSizes(false);
    }
    return getMaxNodeWidth();
  }
  
  public TreePath getPathClosestTo(int paramInt1, int paramInt2)
  {
    if (getRowCount() == 0) {
      return null;
    }
    if (updateNodeSizes) {
      updateNodeSizes(false);
    }
    int i = getRowContainingYLocation(paramInt2);
    return getNode(i).getTreePath();
  }
  
  public Enumeration<TreePath> getVisiblePathsFrom(TreePath paramTreePath)
  {
    TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (localTreeStateNode != null) {
      return new VisibleTreeStateNodeEnumeration(localTreeStateNode);
    }
    return null;
  }
  
  public int getVisibleChildCount(TreePath paramTreePath)
  {
    TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, true, false);
    return localTreeStateNode != null ? localTreeStateNode.getVisibleChildCount() : 0;
  }
  
  public void invalidateSizes()
  {
    if (root != null) {
      root.deepMarkSizeInvalid();
    }
    if ((!isFixedRowHeight()) && (visibleNodes.size() > 0)) {
      updateNodeSizes(true);
    }
  }
  
  public boolean isExpanded(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, true, false);
      return (localTreeStateNode != null) && (localTreeStateNode.isExpanded());
    }
    return false;
  }
  
  public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
  {
    if (paramTreeModelEvent != null)
    {
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      TreeStateNode localTreeStateNode1 = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      if (localTreeStateNode1 != null)
      {
        Object localObject = localTreeStateNode1.getValue();
        localTreeStateNode1.updatePreferredSize();
        int i;
        if ((localTreeStateNode1.hasBeenExpanded()) && (arrayOfInt != null)) {
          for (i = 0; i < arrayOfInt.length; i++)
          {
            TreeStateNode localTreeStateNode2 = (TreeStateNode)localTreeStateNode1.getChildAt(arrayOfInt[i]);
            localTreeStateNode2.setUserObject(treeModel.getChild(localObject, arrayOfInt[i]));
            localTreeStateNode2.updatePreferredSize();
          }
        } else if (localTreeStateNode1 == root) {
          localTreeStateNode1.updatePreferredSize();
        }
        if (!isFixedRowHeight())
        {
          i = localTreeStateNode1.getRow();
          if (i != -1) {
            updateYLocationsFrom(i);
          }
        }
        visibleNodesChanged();
      }
    }
  }
  
  public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
  {
    if (paramTreeModelEvent != null)
    {
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      TreeStateNode localTreeStateNode1 = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      if ((localTreeStateNode1 != null) && (arrayOfInt != null) && (arrayOfInt.length > 0)) {
        if (localTreeStateNode1.hasBeenExpanded())
        {
          int k = localTreeStateNode1.getChildCount();
          Object localObject = localTreeStateNode1.getValue();
          int i = ((localTreeStateNode1 == root) && (!rootVisible)) || ((localTreeStateNode1.getRow() != -1) && (localTreeStateNode1.isExpanded())) ? 1 : 0;
          for (int j = 0; j < arrayOfInt.length; j++) {
            TreeStateNode localTreeStateNode2 = createNodeAt(localTreeStateNode1, arrayOfInt[j]);
          }
          if (k == 0) {
            localTreeStateNode1.updatePreferredSize();
          }
          if (treeSelectionModel != null) {
            treeSelectionModel.resetRowSelection();
          }
          if ((!isFixedRowHeight()) && ((i != 0) || ((k == 0) && (localTreeStateNode1.isVisible()))))
          {
            if (localTreeStateNode1 == root) {
              updateYLocationsFrom(0);
            } else {
              updateYLocationsFrom(localTreeStateNode1.getRow());
            }
            visibleNodesChanged();
          }
          else if (i != 0)
          {
            visibleNodesChanged();
          }
        }
        else if (treeModel.getChildCount(localTreeStateNode1.getValue()) - arrayOfInt.length == 0)
        {
          localTreeStateNode1.updatePreferredSize();
          if ((!isFixedRowHeight()) && (localTreeStateNode1.isVisible())) {
            updateYLocationsFrom(localTreeStateNode1.getRow());
          }
        }
      }
    }
  }
  
  public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
  {
    if (paramTreeModelEvent != null)
    {
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      TreeStateNode localTreeStateNode1 = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      if ((localTreeStateNode1 != null) && (arrayOfInt != null) && (arrayOfInt.length > 0)) {
        if (localTreeStateNode1.hasBeenExpanded())
        {
          int i = ((localTreeStateNode1 == root) && (!rootVisible)) || ((localTreeStateNode1.getRow() != -1) && (localTreeStateNode1.isExpanded())) ? 1 : 0;
          for (int j = arrayOfInt.length - 1; j >= 0; j--)
          {
            TreeStateNode localTreeStateNode2 = (TreeStateNode)localTreeStateNode1.getChildAt(arrayOfInt[j]);
            if (localTreeStateNode2.isExpanded()) {
              localTreeStateNode2.collapse(false);
            }
            if (i != 0)
            {
              int k = localTreeStateNode2.getRow();
              if (k != -1) {
                visibleNodes.removeElementAt(k);
              }
            }
            localTreeStateNode1.remove(arrayOfInt[j]);
          }
          if (localTreeStateNode1.getChildCount() == 0)
          {
            localTreeStateNode1.updatePreferredSize();
            if ((localTreeStateNode1.isExpanded()) && (localTreeStateNode1.isLeaf())) {
              localTreeStateNode1.collapse(false);
            }
          }
          if (treeSelectionModel != null) {
            treeSelectionModel.resetRowSelection();
          }
          if ((!isFixedRowHeight()) && ((i != 0) || ((localTreeStateNode1.getChildCount() == 0) && (localTreeStateNode1.isVisible()))))
          {
            if (localTreeStateNode1 == root)
            {
              if (getRowCount() > 0) {
                getNode(0).setYOrigin(0);
              }
              updateYLocationsFrom(0);
            }
            else
            {
              updateYLocationsFrom(localTreeStateNode1.getRow());
            }
            visibleNodesChanged();
          }
          else if (i != 0)
          {
            visibleNodesChanged();
          }
        }
        else if (treeModel.getChildCount(localTreeStateNode1.getValue()) == 0)
        {
          localTreeStateNode1.updatePreferredSize();
          if ((!isFixedRowHeight()) && (localTreeStateNode1.isVisible())) {
            updateYLocationsFrom(localTreeStateNode1.getRow());
          }
        }
      }
    }
  }
  
  public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
  {
    if (paramTreeModelEvent != null)
    {
      TreePath localTreePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
      TreeStateNode localTreeStateNode1 = getNodeForPath(localTreePath, false, false);
      if ((localTreeStateNode1 == root) || ((localTreeStateNode1 == null) && (((localTreePath == null) && (treeModel != null) && (treeModel.getRoot() == null)) || ((localTreePath != null) && (localTreePath.getPathCount() == 1)))))
      {
        rebuild(true);
      }
      else if (localTreeStateNode1 != null)
      {
        boolean bool = localTreeStateNode1.isExpanded();
        int j = localTreeStateNode1.getRow() != -1 ? 1 : 0;
        TreeStateNode localTreeStateNode3 = (TreeStateNode)localTreeStateNode1.getParent();
        int i = localTreeStateNode3.getIndex(localTreeStateNode1);
        if ((j != 0) && (bool)) {
          localTreeStateNode1.collapse(false);
        }
        if (j != 0) {
          visibleNodes.removeElement(localTreeStateNode1);
        }
        localTreeStateNode1.removeFromParent();
        createNodeAt(localTreeStateNode3, i);
        TreeStateNode localTreeStateNode2 = (TreeStateNode)localTreeStateNode3.getChildAt(i);
        if ((j != 0) && (bool)) {
          localTreeStateNode2.expand(false);
        }
        int k = localTreeStateNode2.getRow();
        if ((!isFixedRowHeight()) && (j != 0))
        {
          if (k == 0) {
            updateYLocationsFrom(k);
          } else {
            updateYLocationsFrom(k - 1);
          }
          visibleNodesChanged();
        }
        else if (j != 0)
        {
          visibleNodesChanged();
        }
      }
    }
  }
  
  private void visibleNodesChanged() {}
  
  private void addMapping(TreeStateNode paramTreeStateNode)
  {
    treePathMapping.put(paramTreeStateNode.getTreePath(), paramTreeStateNode);
  }
  
  private void removeMapping(TreeStateNode paramTreeStateNode)
  {
    treePathMapping.remove(paramTreeStateNode.getTreePath());
  }
  
  private TreeStateNode getMapping(TreePath paramTreePath)
  {
    return (TreeStateNode)treePathMapping.get(paramTreePath);
  }
  
  private Rectangle getBounds(int paramInt, Rectangle paramRectangle)
  {
    if (updateNodeSizes) {
      updateNodeSizes(false);
    }
    if ((paramInt >= 0) && (paramInt < getRowCount())) {
      return getNode(paramInt).getNodeBounds(paramRectangle);
    }
    return null;
  }
  
  private void rebuild(boolean paramBoolean)
  {
    treePathMapping.clear();
    Object localObject;
    if ((treeModel != null) && ((localObject = treeModel.getRoot()) != null))
    {
      root = createNodeForValue(localObject);
      root.path = new TreePath(localObject);
      addMapping(root);
      root.updatePreferredSize(0);
      visibleNodes.removeAllElements();
      if (isRootVisible()) {
        visibleNodes.addElement(root);
      }
      if (!root.isExpanded())
      {
        root.expand();
      }
      else
      {
        Enumeration localEnumeration = root.children();
        while (localEnumeration.hasMoreElements()) {
          visibleNodes.addElement(localEnumeration.nextElement());
        }
        if (!isFixedRowHeight()) {
          updateYLocationsFrom(0);
        }
      }
    }
    else
    {
      visibleNodes.removeAllElements();
      root = null;
    }
    if ((paramBoolean) && (treeSelectionModel != null)) {
      treeSelectionModel.clearSelection();
    }
    visibleNodesChanged();
  }
  
  private TreeStateNode createNodeAt(TreeStateNode paramTreeStateNode, int paramInt)
  {
    Object localObject = treeModel.getChild(paramTreeStateNode.getValue(), paramInt);
    TreeStateNode localTreeStateNode1 = createNodeForValue(localObject);
    paramTreeStateNode.insert(localTreeStateNode1, paramInt);
    localTreeStateNode1.updatePreferredSize(-1);
    int i = paramTreeStateNode == root ? 1 : 0;
    if ((localTreeStateNode1 != null) && (paramTreeStateNode.isExpanded()) && ((paramTreeStateNode.getRow() != -1) || (i != 0)))
    {
      int j;
      if (paramInt == 0)
      {
        if ((i != 0) && (!isRootVisible())) {
          j = 0;
        } else {
          j = paramTreeStateNode.getRow() + 1;
        }
      }
      else if (paramInt == paramTreeStateNode.getChildCount())
      {
        j = paramTreeStateNode.getLastVisibleNode().getRow() + 1;
      }
      else
      {
        TreeStateNode localTreeStateNode2 = (TreeStateNode)paramTreeStateNode.getChildAt(paramInt - 1);
        j = localTreeStateNode2.getLastVisibleNode().getRow() + 1;
      }
      visibleNodes.insertElementAt(localTreeStateNode1, j);
    }
    return localTreeStateNode1;
  }
  
  private TreeStateNode getNodeForPath(TreePath paramTreePath, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramTreePath != null)
    {
      TreeStateNode localTreeStateNode1 = getMapping(paramTreePath);
      if (localTreeStateNode1 != null)
      {
        if ((paramBoolean1) && (!localTreeStateNode1.isVisible())) {
          return null;
        }
        return localTreeStateNode1;
      }
      Stack localStack;
      if (tempStacks.size() == 0) {
        localStack = new Stack();
      } else {
        localStack = (Stack)tempStacks.pop();
      }
      try
      {
        localStack.push(paramTreePath);
        paramTreePath = paramTreePath.getParentPath();
        localTreeStateNode1 = null;
        while (paramTreePath != null)
        {
          localTreeStateNode1 = getMapping(paramTreePath);
          if (localTreeStateNode1 != null)
          {
            while ((localTreeStateNode1 != null) && (localStack.size() > 0))
            {
              paramTreePath = (TreePath)localStack.pop();
              localTreeStateNode1.getLoadedChildren(paramBoolean2);
              int i = treeModel.getIndexOfChild(localTreeStateNode1.getUserObject(), paramTreePath.getLastPathComponent());
              if ((i == -1) || (i >= localTreeStateNode1.getChildCount()) || ((paramBoolean1) && (!localTreeStateNode1.isVisible()))) {
                localTreeStateNode1 = null;
              } else {
                localTreeStateNode1 = (TreeStateNode)localTreeStateNode1.getChildAt(i);
              }
            }
            TreeStateNode localTreeStateNode2 = localTreeStateNode1;
            return localTreeStateNode2;
          }
          localStack.push(paramTreePath);
          paramTreePath = paramTreePath.getParentPath();
        }
      }
      finally
      {
        localStack.removeAllElements();
        tempStacks.push(localStack);
      }
    }
    return null;
  }
  
  private void updateYLocationsFrom(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < getRowCount()))
    {
      TreeStateNode localTreeStateNode = getNode(paramInt);
      int k = localTreeStateNode.getYOrigin() + localTreeStateNode.getPreferredHeight();
      int i = paramInt + 1;
      int j = visibleNodes.size();
      while (i < j)
      {
        localTreeStateNode = (TreeStateNode)visibleNodes.elementAt(i);
        localTreeStateNode.setYOrigin(k);
        k += localTreeStateNode.getPreferredHeight();
        i++;
      }
    }
  }
  
  private void updateNodeSizes(boolean paramBoolean)
  {
    updateNodeSizes = false;
    int j;
    int i = j = 0;
    int k = visibleNodes.size();
    while (j < k)
    {
      TreeStateNode localTreeStateNode = (TreeStateNode)visibleNodes.elementAt(j);
      localTreeStateNode.setYOrigin(i);
      if ((paramBoolean) || (!localTreeStateNode.hasValidSize())) {
        localTreeStateNode.updatePreferredSize(j);
      }
      i += localTreeStateNode.getPreferredHeight();
      j++;
    }
  }
  
  private int getRowContainingYLocation(int paramInt)
  {
    if (isFixedRowHeight())
    {
      if (getRowCount() == 0) {
        return -1;
      }
      return Math.max(0, Math.min(getRowCount() - 1, paramInt / getRowHeight()));
    }
    int i;
    if ((i = getRowCount()) <= 0) {
      return -1;
    }
    int m;
    int k = m = 0;
    while (m < i)
    {
      k = (i - m) / 2 + m;
      TreeStateNode localTreeStateNode = (TreeStateNode)visibleNodes.elementAt(k);
      int n = localTreeStateNode.getYOrigin();
      int j = n + localTreeStateNode.getPreferredHeight();
      if (paramInt < n)
      {
        i = k - 1;
      }
      else
      {
        if (paramInt < j) {
          break;
        }
        m = k + 1;
      }
    }
    if (m == i)
    {
      k = m;
      if (k >= getRowCount()) {
        k = getRowCount() - 1;
      }
    }
    return k;
  }
  
  private void ensurePathIsExpanded(TreePath paramTreePath, boolean paramBoolean)
  {
    if (paramTreePath != null)
    {
      if (treeModel.isLeaf(paramTreePath.getLastPathComponent()))
      {
        paramTreePath = paramTreePath.getParentPath();
        paramBoolean = true;
      }
      if (paramTreePath != null)
      {
        TreeStateNode localTreeStateNode = getNodeForPath(paramTreePath, false, true);
        if (localTreeStateNode != null)
        {
          localTreeStateNode.makeVisible();
          if (paramBoolean) {
            localTreeStateNode.expand();
          }
        }
      }
    }
  }
  
  private TreeStateNode getNode(int paramInt)
  {
    return (TreeStateNode)visibleNodes.elementAt(paramInt);
  }
  
  private int getMaxNodeWidth()
  {
    int i = 0;
    for (int k = getRowCount() - 1; k >= 0; k--)
    {
      TreeStateNode localTreeStateNode = getNode(k);
      int j = localTreeStateNode.getPreferredWidth() + localTreeStateNode.getXOrigin();
      if (j > i) {
        i = j;
      }
    }
    return i;
  }
  
  private TreeStateNode createNodeForValue(Object paramObject)
  {
    return new TreeStateNode(paramObject);
  }
  
  private class TreeStateNode
    extends DefaultMutableTreeNode
  {
    protected int preferredWidth;
    protected int preferredHeight;
    protected int xOrigin;
    protected int yOrigin;
    protected boolean expanded;
    protected boolean hasBeenExpanded;
    protected TreePath path;
    
    public TreeStateNode(Object paramObject)
    {
      super();
    }
    
    public void setParent(MutableTreeNode paramMutableTreeNode)
    {
      super.setParent(paramMutableTreeNode);
      if (paramMutableTreeNode != null)
      {
        path = ((TreeStateNode)paramMutableTreeNode).getTreePath().pathByAddingChild(getUserObject());
        VariableHeightLayoutCache.this.addMapping(this);
      }
    }
    
    public void remove(int paramInt)
    {
      TreeStateNode localTreeStateNode = (TreeStateNode)getChildAt(paramInt);
      localTreeStateNode.removeFromMapping();
      super.remove(paramInt);
    }
    
    public void setUserObject(Object paramObject)
    {
      super.setUserObject(paramObject);
      if (path != null)
      {
        TreeStateNode localTreeStateNode = (TreeStateNode)getParent();
        if (localTreeStateNode != null) {
          resetChildrenPaths(localTreeStateNode.getTreePath());
        } else {
          resetChildrenPaths(null);
        }
      }
    }
    
    public Enumeration children()
    {
      if (!isExpanded()) {
        return DefaultMutableTreeNode.EMPTY_ENUMERATION;
      }
      return super.children();
    }
    
    public boolean isLeaf()
    {
      return getModel().isLeaf(getValue());
    }
    
    public Rectangle getNodeBounds(Rectangle paramRectangle)
    {
      if (paramRectangle == null)
      {
        paramRectangle = new Rectangle(getXOrigin(), getYOrigin(), getPreferredWidth(), getPreferredHeight());
      }
      else
      {
        x = getXOrigin();
        y = getYOrigin();
        width = getPreferredWidth();
        height = getPreferredHeight();
      }
      return paramRectangle;
    }
    
    public int getXOrigin()
    {
      if (!hasValidSize()) {
        updatePreferredSize(getRow());
      }
      return xOrigin;
    }
    
    public int getYOrigin()
    {
      if (isFixedRowHeight())
      {
        int i = getRow();
        if (i == -1) {
          return -1;
        }
        return getRowHeight() * i;
      }
      return yOrigin;
    }
    
    public int getPreferredHeight()
    {
      if (isFixedRowHeight()) {
        return getRowHeight();
      }
      if (!hasValidSize()) {
        updatePreferredSize(getRow());
      }
      return preferredHeight;
    }
    
    public int getPreferredWidth()
    {
      if (!hasValidSize()) {
        updatePreferredSize(getRow());
      }
      return preferredWidth;
    }
    
    public boolean hasValidSize()
    {
      return preferredHeight != 0;
    }
    
    public int getRow()
    {
      return visibleNodes.indexOf(this);
    }
    
    public boolean hasBeenExpanded()
    {
      return hasBeenExpanded;
    }
    
    public boolean isExpanded()
    {
      return expanded;
    }
    
    public TreeStateNode getLastVisibleNode()
    {
      for (TreeStateNode localTreeStateNode = this; (localTreeStateNode.isExpanded()) && (localTreeStateNode.getChildCount() > 0); localTreeStateNode = (TreeStateNode)localTreeStateNode.getLastChild()) {}
      return localTreeStateNode;
    }
    
    public boolean isVisible()
    {
      if (this == root) {
        return true;
      }
      TreeStateNode localTreeStateNode = (TreeStateNode)getParent();
      return (localTreeStateNode != null) && (localTreeStateNode.isExpanded()) && (localTreeStateNode.isVisible());
    }
    
    public int getModelChildCount()
    {
      if (hasBeenExpanded) {
        return super.getChildCount();
      }
      return getModel().getChildCount(getValue());
    }
    
    public int getVisibleChildCount()
    {
      int i = 0;
      if (isExpanded())
      {
        int j = getChildCount();
        i += j;
        for (int k = 0; k < j; k++) {
          i += ((TreeStateNode)getChildAt(k)).getVisibleChildCount();
        }
      }
      return i;
    }
    
    public void toggleExpanded()
    {
      if (isExpanded()) {
        collapse();
      } else {
        expand();
      }
    }
    
    public void makeVisible()
    {
      TreeStateNode localTreeStateNode = (TreeStateNode)getParent();
      if (localTreeStateNode != null) {
        localTreeStateNode.expandParentAndReceiver();
      }
    }
    
    public void expand()
    {
      expand(true);
    }
    
    public void collapse()
    {
      collapse(true);
    }
    
    public Object getValue()
    {
      return getUserObject();
    }
    
    public TreePath getTreePath()
    {
      return path;
    }
    
    protected void resetChildrenPaths(TreePath paramTreePath)
    {
      VariableHeightLayoutCache.this.removeMapping(this);
      if (paramTreePath == null) {
        path = new TreePath(getUserObject());
      } else {
        path = paramTreePath.pathByAddingChild(getUserObject());
      }
      VariableHeightLayoutCache.this.addMapping(this);
      for (int i = getChildCount() - 1; i >= 0; i--) {
        ((TreeStateNode)getChildAt(i)).resetChildrenPaths(path);
      }
    }
    
    protected void setYOrigin(int paramInt)
    {
      yOrigin = paramInt;
    }
    
    protected void shiftYOriginBy(int paramInt)
    {
      yOrigin += paramInt;
    }
    
    protected void updatePreferredSize()
    {
      updatePreferredSize(getRow());
    }
    
    protected void updatePreferredSize(int paramInt)
    {
      Rectangle localRectangle = getNodeDimensions(getUserObject(), paramInt, getLevel(), isExpanded(), boundsBuffer);
      if (localRectangle == null)
      {
        xOrigin = 0;
        preferredWidth = (preferredHeight = 0);
        updateNodeSizes = true;
      }
      else if (height == 0)
      {
        xOrigin = 0;
        preferredWidth = (preferredHeight = 0);
        updateNodeSizes = true;
      }
      else
      {
        xOrigin = x;
        preferredWidth = width;
        if (isFixedRowHeight()) {
          preferredHeight = getRowHeight();
        } else {
          preferredHeight = height;
        }
      }
    }
    
    protected void markSizeInvalid()
    {
      preferredHeight = 0;
    }
    
    protected void deepMarkSizeInvalid()
    {
      markSizeInvalid();
      for (int i = getChildCount() - 1; i >= 0; i--) {
        ((TreeStateNode)getChildAt(i)).deepMarkSizeInvalid();
      }
    }
    
    protected Enumeration getLoadedChildren(boolean paramBoolean)
    {
      if ((!paramBoolean) || (hasBeenExpanded)) {
        return super.children();
      }
      Object localObject = getValue();
      TreeModel localTreeModel = getModel();
      int i = localTreeModel.getChildCount(localObject);
      hasBeenExpanded = true;
      int j = getRow();
      int k;
      TreeStateNode localTreeStateNode;
      if (j == -1)
      {
        for (k = 0; k < i; k++)
        {
          localTreeStateNode = VariableHeightLayoutCache.this.createNodeForValue(localTreeModel.getChild(localObject, k));
          add(localTreeStateNode);
          localTreeStateNode.updatePreferredSize(-1);
        }
      }
      else
      {
        j++;
        for (k = 0; k < i; k++)
        {
          localTreeStateNode = VariableHeightLayoutCache.this.createNodeForValue(localTreeModel.getChild(localObject, k));
          add(localTreeStateNode);
          localTreeStateNode.updatePreferredSize(j++);
        }
      }
      return super.children();
    }
    
    protected void didAdjustTree() {}
    
    protected void expandParentAndReceiver()
    {
      TreeStateNode localTreeStateNode = (TreeStateNode)getParent();
      if (localTreeStateNode != null) {
        localTreeStateNode.expandParentAndReceiver();
      }
      expand();
    }
    
    protected void expand(boolean paramBoolean)
    {
      if ((!isExpanded()) && (!isLeaf()))
      {
        boolean bool = isFixedRowHeight();
        int i = getPreferredHeight();
        TreeStateNode localTreeStateNode1 = getRow();
        expanded = true;
        updatePreferredSize(localTreeStateNode1);
        TreeStateNode localTreeStateNode4;
        int m;
        if (!hasBeenExpanded)
        {
          localObject = getValue();
          TreeModel localTreeModel = getModel();
          int k = localTreeModel.getChildCount(localObject);
          hasBeenExpanded = true;
          if (localTreeStateNode1 == -1)
          {
            for (localTreeStateNode4 = 0; localTreeStateNode4 < k; localTreeStateNode4++)
            {
              localTreeStateNode2 = VariableHeightLayoutCache.this.createNodeForValue(localTreeModel.getChild(localObject, localTreeStateNode4));
              add(localTreeStateNode2);
              localTreeStateNode2.updatePreferredSize(-1);
            }
          }
          else
          {
            localTreeStateNode4 = localTreeStateNode1 + 1;
            for (m = 0; m < k; m++)
            {
              localTreeStateNode2 = VariableHeightLayoutCache.this.createNodeForValue(localTreeModel.getChild(localObject, m));
              add(localTreeStateNode2);
              localTreeStateNode2.updatePreferredSize(localTreeStateNode4);
            }
          }
        }
        TreeStateNode localTreeStateNode2 = localTreeStateNode1;
        Object localObject = preorderEnumeration();
        ((Enumeration)localObject).nextElement();
        int j;
        if (bool) {
          j = 0;
        } else if ((this == root) && (!isRootVisible())) {
          j = 0;
        } else {
          j = getYOrigin() + getPreferredHeight();
        }
        TreeStateNode localTreeStateNode3;
        if (!bool) {
          while (((Enumeration)localObject).hasMoreElements())
          {
            localTreeStateNode3 = (TreeStateNode)((Enumeration)localObject).nextElement();
            if ((!updateNodeSizes) && (!localTreeStateNode3.hasValidSize())) {
              localTreeStateNode3.updatePreferredSize(localTreeStateNode2 + 1);
            }
            localTreeStateNode3.setYOrigin(j);
            j += localTreeStateNode3.getPreferredHeight();
            visibleNodes.insertElementAt(localTreeStateNode3, ++localTreeStateNode2);
          }
        }
        while (((Enumeration)localObject).hasMoreElements())
        {
          localTreeStateNode3 = (TreeStateNode)((Enumeration)localObject).nextElement();
          visibleNodes.insertElementAt(localTreeStateNode3, ++localTreeStateNode2);
        }
        if ((paramBoolean) && ((localTreeStateNode1 != localTreeStateNode2) || (getPreferredHeight() != i)))
        {
          if (!bool)
          {
            localTreeStateNode2++;
            if (localTreeStateNode2 < getRowCount())
            {
              m = j - (getYOrigin() + getPreferredHeight()) + (getPreferredHeight() - i);
              for (localTreeStateNode4 = visibleNodes.size() - 1; localTreeStateNode4 >= localTreeStateNode2; localTreeStateNode4--) {
                ((TreeStateNode)visibleNodes.elementAt(localTreeStateNode4)).shiftYOriginBy(m);
              }
            }
          }
          didAdjustTree();
          VariableHeightLayoutCache.this.visibleNodesChanged();
        }
        if (treeSelectionModel != null) {
          treeSelectionModel.resetRowSelection();
        }
      }
    }
    
    protected void collapse(boolean paramBoolean)
    {
      if (isExpanded())
      {
        Enumeration localEnumeration = preorderEnumeration();
        localEnumeration.nextElement();
        int i = 0;
        boolean bool = isFixedRowHeight();
        int j;
        if (bool) {
          j = 0;
        } else {
          j = getPreferredHeight() + getYOrigin();
        }
        int k = getPreferredHeight();
        int m = j;
        int n = getRow();
        TreeStateNode localTreeStateNode;
        if (!bool) {
          while (localEnumeration.hasMoreElements())
          {
            localTreeStateNode = (TreeStateNode)localEnumeration.nextElement();
            if (localTreeStateNode.isVisible())
            {
              i++;
              j = localTreeStateNode.getYOrigin() + localTreeStateNode.getPreferredHeight();
            }
          }
        }
        while (localEnumeration.hasMoreElements())
        {
          localTreeStateNode = (TreeStateNode)localEnumeration.nextElement();
          if (localTreeStateNode.isVisible()) {
            i++;
          }
        }
        for (int i1 = i + n; i1 > n; i1--) {
          visibleNodes.removeElementAt(i1);
        }
        expanded = false;
        if (n == -1) {
          markSizeInvalid();
        } else if (paramBoolean) {
          updatePreferredSize(n);
        }
        if ((n != -1) && (paramBoolean) && ((i > 0) || (k != getPreferredHeight())))
        {
          m += getPreferredHeight() - k;
          if ((!bool) && (n + 1 < getRowCount()) && (m != j))
          {
            int i3 = m - j;
            i1 = n + 1;
            int i2 = visibleNodes.size();
            while (i1 < i2)
            {
              ((TreeStateNode)visibleNodes.elementAt(i1)).shiftYOriginBy(i3);
              i1++;
            }
          }
          didAdjustTree();
          VariableHeightLayoutCache.this.visibleNodesChanged();
        }
        if ((treeSelectionModel != null) && (i > 0) && (n != -1)) {
          treeSelectionModel.resetRowSelection();
        }
      }
    }
    
    protected void removeFromMapping()
    {
      if (path != null)
      {
        VariableHeightLayoutCache.this.removeMapping(this);
        for (int i = getChildCount() - 1; i >= 0; i--) {
          ((TreeStateNode)getChildAt(i)).removeFromMapping();
        }
      }
    }
  }
  
  private class VisibleTreeStateNodeEnumeration
    implements Enumeration<TreePath>
  {
    protected VariableHeightLayoutCache.TreeStateNode parent;
    protected int nextIndex;
    protected int childCount;
    
    protected VisibleTreeStateNodeEnumeration(VariableHeightLayoutCache.TreeStateNode paramTreeStateNode)
    {
      this(paramTreeStateNode, -1);
    }
    
    protected VisibleTreeStateNodeEnumeration(VariableHeightLayoutCache.TreeStateNode paramTreeStateNode, int paramInt)
    {
      parent = paramTreeStateNode;
      nextIndex = paramInt;
      childCount = parent.getChildCount();
    }
    
    public boolean hasMoreElements()
    {
      return parent != null;
    }
    
    public TreePath nextElement()
    {
      if (!hasMoreElements()) {
        throw new NoSuchElementException("No more visible paths");
      }
      TreePath localTreePath;
      if (nextIndex == -1)
      {
        localTreePath = parent.getTreePath();
      }
      else
      {
        VariableHeightLayoutCache.TreeStateNode localTreeStateNode = (VariableHeightLayoutCache.TreeStateNode)parent.getChildAt(nextIndex);
        localTreePath = localTreeStateNode.getTreePath();
      }
      updateNextObject();
      return localTreePath;
    }
    
    protected void updateNextObject()
    {
      if (!updateNextIndex()) {
        findNextValidParent();
      }
    }
    
    protected boolean findNextValidParent()
    {
      if (parent == root)
      {
        parent = null;
        return false;
      }
      while (parent != null)
      {
        VariableHeightLayoutCache.TreeStateNode localTreeStateNode = (VariableHeightLayoutCache.TreeStateNode)parent.getParent();
        if (localTreeStateNode != null)
        {
          nextIndex = localTreeStateNode.getIndex(parent);
          parent = localTreeStateNode;
          childCount = parent.getChildCount();
          if (updateNextIndex()) {
            return true;
          }
        }
        else
        {
          parent = null;
        }
      }
      return false;
    }
    
    protected boolean updateNextIndex()
    {
      if ((nextIndex == -1) && (!parent.isExpanded())) {
        return false;
      }
      if (childCount == 0) {
        return false;
      }
      if (++nextIndex >= childCount) {
        return false;
      }
      VariableHeightLayoutCache.TreeStateNode localTreeStateNode = (VariableHeightLayoutCache.TreeStateNode)parent.getChildAt(nextIndex);
      if ((localTreeStateNode != null) && (localTreeStateNode.isExpanded()))
      {
        parent = localTreeStateNode;
        nextIndex = -1;
        childCount = localTreeStateNode.getChildCount();
      }
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\VariableHeightLayoutCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */