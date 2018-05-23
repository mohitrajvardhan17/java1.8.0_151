package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;
import javax.swing.event.TreeModelEvent;
import sun.swing.SwingUtilities2;

public class FixedHeightLayoutCache
  extends AbstractLayoutCache
{
  private FHTreeStateNode root;
  private int rowCount;
  private Rectangle boundsBuffer = new Rectangle();
  private Hashtable<TreePath, FHTreeStateNode> treePathMapping = new Hashtable();
  private SearchInfo info = new SearchInfo(null);
  private Stack<Stack<TreePath>> tempStacks = new Stack();
  
  public FixedHeightLayoutCache()
  {
    setRowHeight(1);
  }
  
  public void setModel(TreeModel paramTreeModel)
  {
    super.setModel(paramTreeModel);
    rebuild(false);
  }
  
  public void setRootVisible(boolean paramBoolean)
  {
    if (isRootVisible() != paramBoolean)
    {
      super.setRootVisible(paramBoolean);
      if (root != null)
      {
        if (paramBoolean)
        {
          rowCount += 1;
          root.adjustRowBy(1);
        }
        else
        {
          rowCount -= 1;
          root.adjustRowBy(-1);
        }
        visibleNodesChanged();
      }
    }
  }
  
  public void setRowHeight(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("FixedHeightLayoutCache only supports row heights greater than 0");
    }
    if (getRowHeight() != paramInt)
    {
      super.setRowHeight(paramInt);
      visibleNodesChanged();
    }
  }
  
  public int getRowCount()
  {
    return rowCount;
  }
  
  public void invalidatePathBounds(TreePath paramTreePath) {}
  
  public void invalidateSizes()
  {
    visibleNodesChanged();
  }
  
  public boolean isExpanded(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      FHTreeStateNode localFHTreeStateNode = getNodeForPath(paramTreePath, true, false);
      return (localFHTreeStateNode != null) && (localFHTreeStateNode.isExpanded());
    }
    return false;
  }
  
  public Rectangle getBounds(TreePath paramTreePath, Rectangle paramRectangle)
  {
    if (paramTreePath == null) {
      return null;
    }
    FHTreeStateNode localFHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (localFHTreeStateNode != null) {
      return getBounds(localFHTreeStateNode, -1, paramRectangle);
    }
    TreePath localTreePath = paramTreePath.getParentPath();
    localFHTreeStateNode = getNodeForPath(localTreePath, true, false);
    if ((localFHTreeStateNode != null) && (localFHTreeStateNode.isExpanded()))
    {
      int i = treeModel.getIndexOfChild(localTreePath.getLastPathComponent(), paramTreePath.getLastPathComponent());
      if (i != -1) {
        return getBounds(localFHTreeStateNode, i, paramRectangle);
      }
    }
    return null;
  }
  
  public TreePath getPathForRow(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < getRowCount()) && (root.getPathForRow(paramInt, getRowCount(), info))) {
      return info.getPath();
    }
    return null;
  }
  
  public int getRowForPath(TreePath paramTreePath)
  {
    if ((paramTreePath == null) || (root == null)) {
      return -1;
    }
    FHTreeStateNode localFHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (localFHTreeStateNode != null) {
      return localFHTreeStateNode.getRow();
    }
    TreePath localTreePath = paramTreePath.getParentPath();
    localFHTreeStateNode = getNodeForPath(localTreePath, true, false);
    if ((localFHTreeStateNode != null) && (localFHTreeStateNode.isExpanded())) {
      return localFHTreeStateNode.getRowToModelIndex(treeModel.getIndexOfChild(localTreePath.getLastPathComponent(), paramTreePath.getLastPathComponent()));
    }
    return -1;
  }
  
  public TreePath getPathClosestTo(int paramInt1, int paramInt2)
  {
    if (getRowCount() == 0) {
      return null;
    }
    int i = getRowContainingYLocation(paramInt2);
    return getPathForRow(i);
  }
  
  public int getVisibleChildCount(TreePath paramTreePath)
  {
    FHTreeStateNode localFHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (localFHTreeStateNode == null) {
      return 0;
    }
    return localFHTreeStateNode.getTotalChildCount();
  }
  
  public Enumeration<TreePath> getVisiblePathsFrom(TreePath paramTreePath)
  {
    if (paramTreePath == null) {
      return null;
    }
    FHTreeStateNode localFHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    if (localFHTreeStateNode != null) {
      return new VisibleFHTreeStateNodeEnumeration(localFHTreeStateNode);
    }
    TreePath localTreePath = paramTreePath.getParentPath();
    localFHTreeStateNode = getNodeForPath(localTreePath, true, false);
    if ((localFHTreeStateNode != null) && (localFHTreeStateNode.isExpanded())) {
      return new VisibleFHTreeStateNodeEnumeration(localFHTreeStateNode, treeModel.getIndexOfChild(localTreePath.getLastPathComponent(), paramTreePath.getLastPathComponent()));
    }
    return null;
  }
  
  public void setExpandedState(TreePath paramTreePath, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      ensurePathIsExpanded(paramTreePath, true);
    }
    else if (paramTreePath != null)
    {
      TreePath localTreePath = paramTreePath.getParentPath();
      if (localTreePath != null)
      {
        localFHTreeStateNode = getNodeForPath(localTreePath, false, true);
        if (localFHTreeStateNode != null) {
          localFHTreeStateNode.makeVisible();
        }
      }
      FHTreeStateNode localFHTreeStateNode = getNodeForPath(paramTreePath, true, false);
      if (localFHTreeStateNode != null) {
        localFHTreeStateNode.collapse(true);
      }
    }
  }
  
  public boolean getExpandedState(TreePath paramTreePath)
  {
    FHTreeStateNode localFHTreeStateNode = getNodeForPath(paramTreePath, true, false);
    return (localFHTreeStateNode.isVisible()) && (localFHTreeStateNode.isExpanded());
  }
  
  public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
  {
    if (paramTreeModelEvent != null)
    {
      FHTreeStateNode localFHTreeStateNode1 = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      if (localFHTreeStateNode1 != null)
      {
        int i;
        if ((arrayOfInt != null) && ((i = arrayOfInt.length) > 0))
        {
          Object localObject = localFHTreeStateNode1.getUserObject();
          for (int j = 0; j < i; j++)
          {
            FHTreeStateNode localFHTreeStateNode2 = localFHTreeStateNode1.getChildAtModelIndex(arrayOfInt[j]);
            if (localFHTreeStateNode2 != null) {
              localFHTreeStateNode2.setUserObject(treeModel.getChild(localObject, arrayOfInt[j]));
            }
          }
          if ((localFHTreeStateNode1.isVisible()) && (localFHTreeStateNode1.isExpanded())) {
            visibleNodesChanged();
          }
        }
        else if ((localFHTreeStateNode1 == root) && (localFHTreeStateNode1.isVisible()) && (localFHTreeStateNode1.isExpanded()))
        {
          visibleNodesChanged();
        }
      }
    }
  }
  
  public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
  {
    if (paramTreeModelEvent != null)
    {
      FHTreeStateNode localFHTreeStateNode = getNodeForPath(SwingUtilities2.getTreePath(paramTreeModelEvent, getModel()), false, false);
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      int i;
      if ((localFHTreeStateNode != null) && (arrayOfInt != null) && ((i = arrayOfInt.length) > 0))
      {
        boolean bool = (localFHTreeStateNode.isVisible()) && (localFHTreeStateNode.isExpanded());
        for (int j = 0; j < i; j++) {
          localFHTreeStateNode.childInsertedAtModelIndex(arrayOfInt[j], bool);
        }
        if ((bool) && (treeSelectionModel != null)) {
          treeSelectionModel.resetRowSelection();
        }
        if (localFHTreeStateNode.isVisible()) {
          visibleNodesChanged();
        }
      }
    }
  }
  
  public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
  {
    if (paramTreeModelEvent != null)
    {
      TreePath localTreePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
      FHTreeStateNode localFHTreeStateNode = getNodeForPath(localTreePath, false, false);
      int[] arrayOfInt = paramTreeModelEvent.getChildIndices();
      int i;
      if ((localFHTreeStateNode != null) && (arrayOfInt != null) && ((i = arrayOfInt.length) > 0))
      {
        Object[] arrayOfObject = paramTreeModelEvent.getChildren();
        boolean bool = (localFHTreeStateNode.isVisible()) && (localFHTreeStateNode.isExpanded());
        for (int j = i - 1; j >= 0; j--) {
          localFHTreeStateNode.removeChildAtModelIndex(arrayOfInt[j], bool);
        }
        if (bool)
        {
          if (treeSelectionModel != null) {
            treeSelectionModel.resetRowSelection();
          }
          if ((treeModel.getChildCount(localFHTreeStateNode.getUserObject()) == 0) && (localFHTreeStateNode.isLeaf())) {
            localFHTreeStateNode.collapse(false);
          }
          visibleNodesChanged();
        }
        else if (localFHTreeStateNode.isVisible())
        {
          visibleNodesChanged();
        }
      }
    }
  }
  
  public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
  {
    if (paramTreeModelEvent != null)
    {
      TreePath localTreePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
      FHTreeStateNode localFHTreeStateNode1 = getNodeForPath(localTreePath, false, false);
      if ((localFHTreeStateNode1 == root) || ((localFHTreeStateNode1 == null) && (((localTreePath == null) && (treeModel != null) && (treeModel.getRoot() == null)) || ((localTreePath != null) && (localTreePath.getPathCount() <= 1)))))
      {
        rebuild(true);
      }
      else if (localFHTreeStateNode1 != null)
      {
        FHTreeStateNode localFHTreeStateNode2 = (FHTreeStateNode)localFHTreeStateNode1.getParent();
        boolean bool1 = localFHTreeStateNode1.isExpanded();
        boolean bool2 = localFHTreeStateNode1.isVisible();
        int i = localFHTreeStateNode2.getIndex(localFHTreeStateNode1);
        localFHTreeStateNode1.collapse(false);
        localFHTreeStateNode2.remove(i);
        if ((bool2) && (bool1))
        {
          int j = localFHTreeStateNode1.getRow();
          localFHTreeStateNode2.resetChildrenRowsFrom(j, i, localFHTreeStateNode1.getChildIndex());
          localFHTreeStateNode1 = getNodeForPath(localTreePath, false, true);
          localFHTreeStateNode1.expand();
        }
        if ((treeSelectionModel != null) && (bool2) && (bool1)) {
          treeSelectionModel.resetRowSelection();
        }
        if (bool2) {
          visibleNodesChanged();
        }
      }
    }
  }
  
  private void visibleNodesChanged() {}
  
  private Rectangle getBounds(FHTreeStateNode paramFHTreeStateNode, int paramInt, Rectangle paramRectangle)
  {
    int j;
    Object localObject;
    boolean bool;
    int i;
    if (paramInt == -1)
    {
      j = paramFHTreeStateNode.getRow();
      localObject = paramFHTreeStateNode.getUserObject();
      bool = paramFHTreeStateNode.isExpanded();
      i = paramFHTreeStateNode.getLevel();
    }
    else
    {
      j = paramFHTreeStateNode.getRowToModelIndex(paramInt);
      localObject = treeModel.getChild(paramFHTreeStateNode.getUserObject(), paramInt);
      bool = false;
      i = paramFHTreeStateNode.getLevel() + 1;
    }
    Rectangle localRectangle = getNodeDimensions(localObject, j, i, bool, boundsBuffer);
    if (localRectangle == null) {
      return null;
    }
    if (paramRectangle == null) {
      paramRectangle = new Rectangle();
    }
    x = x;
    height = getRowHeight();
    y = (j * height);
    width = width;
    return paramRectangle;
  }
  
  private void adjustRowCountBy(int paramInt)
  {
    rowCount += paramInt;
  }
  
  private void addMapping(FHTreeStateNode paramFHTreeStateNode)
  {
    treePathMapping.put(paramFHTreeStateNode.getTreePath(), paramFHTreeStateNode);
  }
  
  private void removeMapping(FHTreeStateNode paramFHTreeStateNode)
  {
    treePathMapping.remove(paramFHTreeStateNode.getTreePath());
  }
  
  private FHTreeStateNode getMapping(TreePath paramTreePath)
  {
    return (FHTreeStateNode)treePathMapping.get(paramTreePath);
  }
  
  private void rebuild(boolean paramBoolean)
  {
    treePathMapping.clear();
    Object localObject;
    if ((treeModel != null) && ((localObject = treeModel.getRoot()) != null))
    {
      root = createNodeForValue(localObject, 0);
      root.path = new TreePath(localObject);
      addMapping(root);
      if (isRootVisible())
      {
        rowCount = 1;
        root.row = 0;
      }
      else
      {
        rowCount = 0;
        root.row = -1;
      }
      root.expand();
    }
    else
    {
      root = null;
      rowCount = 0;
    }
    if ((paramBoolean) && (treeSelectionModel != null)) {
      treeSelectionModel.clearSelection();
    }
    visibleNodesChanged();
  }
  
  private int getRowContainingYLocation(int paramInt)
  {
    if (getRowCount() == 0) {
      return -1;
    }
    return Math.max(0, Math.min(getRowCount() - 1, paramInt / getRowHeight()));
  }
  
  private boolean ensurePathIsExpanded(TreePath paramTreePath, boolean paramBoolean)
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
        FHTreeStateNode localFHTreeStateNode = getNodeForPath(paramTreePath, false, true);
        if (localFHTreeStateNode != null)
        {
          localFHTreeStateNode.makeVisible();
          if (paramBoolean) {
            localFHTreeStateNode.expand();
          }
          return true;
        }
      }
    }
    return false;
  }
  
  private FHTreeStateNode createNodeForValue(Object paramObject, int paramInt)
  {
    return new FHTreeStateNode(paramObject, paramInt, -1);
  }
  
  private FHTreeStateNode getNodeForPath(TreePath paramTreePath, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramTreePath != null)
    {
      FHTreeStateNode localFHTreeStateNode1 = getMapping(paramTreePath);
      if (localFHTreeStateNode1 != null)
      {
        if ((paramBoolean1) && (!localFHTreeStateNode1.isVisible())) {
          return null;
        }
        return localFHTreeStateNode1;
      }
      if (paramBoolean1) {
        return null;
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
        localFHTreeStateNode1 = null;
        while (paramTreePath != null)
        {
          localFHTreeStateNode1 = getMapping(paramTreePath);
          if (localFHTreeStateNode1 != null)
          {
            while ((localFHTreeStateNode1 != null) && (localStack.size() > 0))
            {
              paramTreePath = (TreePath)localStack.pop();
              localFHTreeStateNode1 = localFHTreeStateNode1.createChildFor(paramTreePath.getLastPathComponent());
            }
            FHTreeStateNode localFHTreeStateNode2 = localFHTreeStateNode1;
            return localFHTreeStateNode2;
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
      return null;
    }
    return null;
  }
  
  private class FHTreeStateNode
    extends DefaultMutableTreeNode
  {
    protected boolean isExpanded;
    protected int childIndex;
    protected int childCount;
    protected int row;
    protected TreePath path;
    
    public FHTreeStateNode(Object paramObject, int paramInt1, int paramInt2)
    {
      super();
      childIndex = paramInt1;
      row = paramInt2;
    }
    
    public void setParent(MutableTreeNode paramMutableTreeNode)
    {
      super.setParent(paramMutableTreeNode);
      if (paramMutableTreeNode != null)
      {
        path = ((FHTreeStateNode)paramMutableTreeNode).getTreePath().pathByAddingChild(getUserObject());
        FixedHeightLayoutCache.this.addMapping(this);
      }
    }
    
    public void remove(int paramInt)
    {
      FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getChildAt(paramInt);
      localFHTreeStateNode.removeFromMapping();
      super.remove(paramInt);
    }
    
    public void setUserObject(Object paramObject)
    {
      super.setUserObject(paramObject);
      if (path != null)
      {
        FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getParent();
        if (localFHTreeStateNode != null) {
          resetChildrenPaths(localFHTreeStateNode.getTreePath());
        } else {
          resetChildrenPaths(null);
        }
      }
    }
    
    public int getChildIndex()
    {
      return childIndex;
    }
    
    public TreePath getTreePath()
    {
      return path;
    }
    
    public FHTreeStateNode getChildAtModelIndex(int paramInt)
    {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        if (getChildAtchildIndex == paramInt) {
          return (FHTreeStateNode)getChildAt(i);
        }
      }
      return null;
    }
    
    public boolean isVisible()
    {
      FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getParent();
      if (localFHTreeStateNode == null) {
        return true;
      }
      return (localFHTreeStateNode.isExpanded()) && (localFHTreeStateNode.isVisible());
    }
    
    public int getRow()
    {
      return row;
    }
    
    public int getRowToModelIndex(int paramInt)
    {
      int i = getRow() + 1;
      int j = i;
      int k = 0;
      int m = getChildCount();
      while (k < m)
      {
        FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getChildAt(k);
        if (childIndex >= paramInt)
        {
          if (childIndex == paramInt) {
            return row;
          }
          if (k == 0) {
            return getRow() + 1 + paramInt;
          }
          return row - (childIndex - paramInt);
        }
        k++;
      }
      return getRow() + 1 + getTotalChildCount() - (childCount - paramInt);
    }
    
    public int getTotalChildCount()
    {
      if (isExpanded())
      {
        FHTreeStateNode localFHTreeStateNode1 = (FHTreeStateNode)getParent();
        int i;
        if ((localFHTreeStateNode1 != null) && ((i = localFHTreeStateNode1.getIndex(this)) + 1 < localFHTreeStateNode1.getChildCount()))
        {
          FHTreeStateNode localFHTreeStateNode2 = (FHTreeStateNode)localFHTreeStateNode1.getChildAt(i + 1);
          return row - row - (childIndex - childIndex);
        }
        int j = childCount;
        for (int k = getChildCount() - 1; k >= 0; k--) {
          j += ((FHTreeStateNode)getChildAt(k)).getTotalChildCount();
        }
        return j;
      }
      return 0;
    }
    
    public boolean isExpanded()
    {
      return isExpanded;
    }
    
    public int getVisibleLevel()
    {
      if (isRootVisible()) {
        return getLevel();
      }
      return getLevel() - 1;
    }
    
    protected void resetChildrenPaths(TreePath paramTreePath)
    {
      FixedHeightLayoutCache.this.removeMapping(this);
      if (paramTreePath == null) {
        path = new TreePath(getUserObject());
      } else {
        path = paramTreePath.pathByAddingChild(getUserObject());
      }
      FixedHeightLayoutCache.this.addMapping(this);
      for (int i = getChildCount() - 1; i >= 0; i--) {
        ((FHTreeStateNode)getChildAt(i)).resetChildrenPaths(path);
      }
    }
    
    protected void removeFromMapping()
    {
      if (path != null)
      {
        FixedHeightLayoutCache.this.removeMapping(this);
        for (int i = getChildCount() - 1; i >= 0; i--) {
          ((FHTreeStateNode)getChildAt(i)).removeFromMapping();
        }
      }
    }
    
    protected FHTreeStateNode createChildFor(Object paramObject)
    {
      int i = treeModel.getIndexOfChild(getUserObject(), paramObject);
      if (i < 0) {
        return null;
      }
      FHTreeStateNode localFHTreeStateNode2 = FixedHeightLayoutCache.this.createNodeForValue(paramObject, i);
      int j;
      if (isVisible()) {
        j = getRowToModelIndex(i);
      } else {
        j = -1;
      }
      row = j;
      int k = 0;
      int m = getChildCount();
      while (k < m)
      {
        FHTreeStateNode localFHTreeStateNode1 = (FHTreeStateNode)getChildAt(k);
        if (childIndex > i)
        {
          insert(localFHTreeStateNode2, k);
          return localFHTreeStateNode2;
        }
        k++;
      }
      add(localFHTreeStateNode2);
      return localFHTreeStateNode2;
    }
    
    protected void adjustRowBy(int paramInt)
    {
      row += paramInt;
      if (isExpanded) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
          ((FHTreeStateNode)getChildAt(i)).adjustRowBy(paramInt);
        }
      }
    }
    
    protected void adjustRowBy(int paramInt1, int paramInt2)
    {
      if (isExpanded) {
        for (int i = getChildCount() - 1; i >= paramInt2; i--) {
          ((FHTreeStateNode)getChildAt(i)).adjustRowBy(paramInt1);
        }
      }
      FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getParent();
      if (localFHTreeStateNode != null) {
        localFHTreeStateNode.adjustRowBy(paramInt1, localFHTreeStateNode.getIndex(this) + 1);
      }
    }
    
    protected void didExpand()
    {
      int i = setRowAndChildren(row);
      FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getParent();
      int j = i - row - 1;
      if (localFHTreeStateNode != null) {
        localFHTreeStateNode.adjustRowBy(j, localFHTreeStateNode.getIndex(this) + 1);
      }
      FixedHeightLayoutCache.this.adjustRowCountBy(j);
    }
    
    protected int setRowAndChildren(int paramInt)
    {
      row = paramInt;
      if (!isExpanded()) {
        return row + 1;
      }
      int i = row + 1;
      int j = 0;
      int k = getChildCount();
      for (int m = 0; m < k; m++)
      {
        FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getChildAt(m);
        i += childIndex - j;
        j = childIndex + 1;
        if (isExpanded) {
          i = localFHTreeStateNode.setRowAndChildren(i);
        } else {
          row = (i++);
        }
      }
      return i + childCount - j;
    }
    
    protected void resetChildrenRowsFrom(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt1;
      int j = paramInt3;
      int k = getChildCount();
      for (int m = paramInt2; m < k; m++)
      {
        localFHTreeStateNode = (FHTreeStateNode)getChildAt(m);
        i += childIndex - j;
        j = childIndex + 1;
        if (isExpanded) {
          i = localFHTreeStateNode.setRowAndChildren(i);
        } else {
          row = (i++);
        }
      }
      i += childCount - j;
      FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getParent();
      if (localFHTreeStateNode != null) {
        localFHTreeStateNode.resetChildrenRowsFrom(i, localFHTreeStateNode.getIndex(this) + 1, childIndex + 1);
      } else {
        rowCount = i;
      }
    }
    
    protected void makeVisible()
    {
      FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getParent();
      if (localFHTreeStateNode != null) {
        localFHTreeStateNode.expandParentAndReceiver();
      }
    }
    
    protected void expandParentAndReceiver()
    {
      FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getParent();
      if (localFHTreeStateNode != null) {
        localFHTreeStateNode.expandParentAndReceiver();
      }
      expand();
    }
    
    protected void expand()
    {
      if ((!isExpanded) && (!isLeaf()))
      {
        boolean bool = isVisible();
        isExpanded = true;
        childCount = treeModel.getChildCount(getUserObject());
        if (bool) {
          didExpand();
        }
        if ((bool) && (treeSelectionModel != null)) {
          treeSelectionModel.resetRowSelection();
        }
      }
    }
    
    protected void collapse(boolean paramBoolean)
    {
      if (isExpanded)
      {
        if ((isVisible()) && (paramBoolean))
        {
          int i = getTotalChildCount();
          isExpanded = false;
          FixedHeightLayoutCache.this.adjustRowCountBy(-i);
          adjustRowBy(-i, 0);
        }
        else
        {
          isExpanded = false;
        }
        if ((paramBoolean) && (isVisible()) && (treeSelectionModel != null)) {
          treeSelectionModel.resetRowSelection();
        }
      }
    }
    
    public boolean isLeaf()
    {
      TreeModel localTreeModel = getModel();
      return localTreeModel != null ? localTreeModel.isLeaf(getUserObject()) : true;
    }
    
    protected void addNode(FHTreeStateNode paramFHTreeStateNode)
    {
      int i = 0;
      int j = paramFHTreeStateNode.getChildIndex();
      int k = 0;
      int m = getChildCount();
      while (k < m)
      {
        if (((FHTreeStateNode)getChildAt(k)).getChildIndex() > j)
        {
          i = 1;
          insert(paramFHTreeStateNode, k);
          k = m;
        }
        k++;
      }
      if (i == 0) {
        add(paramFHTreeStateNode);
      }
    }
    
    protected void removeChildAtModelIndex(int paramInt, boolean paramBoolean)
    {
      FHTreeStateNode localFHTreeStateNode1 = getChildAtModelIndex(paramInt);
      int i;
      if (localFHTreeStateNode1 != null)
      {
        i = localFHTreeStateNode1.getRow();
        int j = getIndex(localFHTreeStateNode1);
        localFHTreeStateNode1.collapse(false);
        remove(j);
        adjustChildIndexs(j, -1);
        childCount -= 1;
        if (paramBoolean) {
          resetChildrenRowsFrom(i, j, paramInt);
        }
      }
      else
      {
        i = getChildCount();
        for (int k = 0; k < i; k++)
        {
          FHTreeStateNode localFHTreeStateNode2 = (FHTreeStateNode)getChildAt(k);
          if (childIndex >= paramInt)
          {
            if (paramBoolean)
            {
              adjustRowBy(-1, k);
              FixedHeightLayoutCache.this.adjustRowCountBy(-1);
            }
            while (k < i)
            {
              getChildAtchildIndex -= 1;
              k++;
            }
            childCount -= 1;
            return;
          }
        }
        if (paramBoolean)
        {
          adjustRowBy(-1, i);
          FixedHeightLayoutCache.this.adjustRowCountBy(-1);
        }
        childCount -= 1;
      }
    }
    
    protected void adjustChildIndexs(int paramInt1, int paramInt2)
    {
      int i = paramInt1;
      int j = getChildCount();
      while (i < j)
      {
        getChildAtchildIndex += paramInt2;
        i++;
      }
    }
    
    protected void childInsertedAtModelIndex(int paramInt, boolean paramBoolean)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++)
      {
        FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getChildAt(j);
        if (childIndex >= paramInt)
        {
          if (paramBoolean)
          {
            adjustRowBy(1, j);
            FixedHeightLayoutCache.this.adjustRowCountBy(1);
          }
          while (j < i)
          {
            getChildAtchildIndex += 1;
            j++;
          }
          childCount += 1;
          return;
        }
      }
      if (paramBoolean)
      {
        adjustRowBy(1, i);
        FixedHeightLayoutCache.this.adjustRowCountBy(1);
      }
      childCount += 1;
    }
    
    protected boolean getPathForRow(int paramInt1, int paramInt2, FixedHeightLayoutCache.SearchInfo paramSearchInfo)
    {
      if (row == paramInt1)
      {
        node = this;
        isNodeParentNode = false;
        childIndex = childIndex;
        return true;
      }
      Object localObject = null;
      int i = 0;
      int j = getChildCount();
      while (i < j)
      {
        FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getChildAt(i);
        if (row > paramInt1)
        {
          if (i == 0)
          {
            node = this;
            isNodeParentNode = true;
            childIndex = (paramInt1 - row - 1);
            return true;
          }
          int k = 1 + row - (childIndex - childIndex);
          if (paramInt1 < k) {
            return ((FHTreeStateNode)localObject).getPathForRow(paramInt1, k, paramSearchInfo);
          }
          node = this;
          isNodeParentNode = true;
          childIndex = (paramInt1 - k + childIndex + 1);
          return true;
        }
        localObject = localFHTreeStateNode;
        i++;
      }
      if (localObject != null)
      {
        i = paramInt2 - (childCount - childIndex) + 1;
        if (paramInt1 < i) {
          return ((FHTreeStateNode)localObject).getPathForRow(paramInt1, i, paramSearchInfo);
        }
        node = this;
        isNodeParentNode = true;
        childIndex = (paramInt1 - i + childIndex + 1);
        return true;
      }
      i = paramInt1 - row - 1;
      if (i >= childCount) {
        return false;
      }
      node = this;
      isNodeParentNode = true;
      childIndex = i;
      return true;
    }
    
    protected int getCountTo(int paramInt)
    {
      int i = paramInt + 1;
      int j = 0;
      int k = getChildCount();
      while (j < k)
      {
        FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getChildAt(j);
        if (childIndex >= paramInt) {
          j = k;
        } else {
          i += localFHTreeStateNode.getTotalChildCount();
        }
        j++;
      }
      if (parent != null) {
        return i + ((FHTreeStateNode)getParent()).getCountTo(childIndex);
      }
      if (!isRootVisible()) {
        return i - 1;
      }
      return i;
    }
    
    protected int getNumExpandedChildrenTo(int paramInt)
    {
      int i = paramInt;
      int j = 0;
      int k = getChildCount();
      while (j < k)
      {
        FHTreeStateNode localFHTreeStateNode = (FHTreeStateNode)getChildAt(j);
        if (childIndex >= paramInt) {
          return i;
        }
        i += localFHTreeStateNode.getTotalChildCount();
        j++;
      }
      return i;
    }
    
    protected void didAdjustTree() {}
  }
  
  private class SearchInfo
  {
    protected FixedHeightLayoutCache.FHTreeStateNode node;
    protected boolean isNodeParentNode;
    protected int childIndex;
    
    private SearchInfo() {}
    
    protected TreePath getPath()
    {
      if (node == null) {
        return null;
      }
      if (isNodeParentNode) {
        return node.getTreePath().pathByAddingChild(treeModel.getChild(node.getUserObject(), childIndex));
      }
      return node.path;
    }
  }
  
  private class VisibleFHTreeStateNodeEnumeration
    implements Enumeration<TreePath>
  {
    protected FixedHeightLayoutCache.FHTreeStateNode parent;
    protected int nextIndex;
    protected int childCount;
    
    protected VisibleFHTreeStateNodeEnumeration(FixedHeightLayoutCache.FHTreeStateNode paramFHTreeStateNode)
    {
      this(paramFHTreeStateNode, -1);
    }
    
    protected VisibleFHTreeStateNodeEnumeration(FixedHeightLayoutCache.FHTreeStateNode paramFHTreeStateNode, int paramInt)
    {
      parent = paramFHTreeStateNode;
      nextIndex = paramInt;
      childCount = treeModel.getChildCount(parent.getUserObject());
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
        FixedHeightLayoutCache.FHTreeStateNode localFHTreeStateNode = parent.getChildAtModelIndex(nextIndex);
        if (localFHTreeStateNode == null) {
          localTreePath = parent.getTreePath().pathByAddingChild(treeModel.getChild(parent.getUserObject(), nextIndex));
        } else {
          localTreePath = localFHTreeStateNode.getTreePath();
        }
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
        FixedHeightLayoutCache.FHTreeStateNode localFHTreeStateNode = (FixedHeightLayoutCache.FHTreeStateNode)parent.getParent();
        if (localFHTreeStateNode != null)
        {
          nextIndex = parent.childIndex;
          parent = localFHTreeStateNode;
          childCount = treeModel.getChildCount(parent.getUserObject());
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
      FixedHeightLayoutCache.FHTreeStateNode localFHTreeStateNode = parent.getChildAtModelIndex(nextIndex);
      if ((localFHTreeStateNode != null) && (localFHTreeStateNode.isExpanded()))
      {
        parent = localFHTreeStateNode;
        nextIndex = -1;
        childCount = treeModel.getChildCount(localFHTreeStateNode.getUserObject());
      }
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\FixedHeightLayoutCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */