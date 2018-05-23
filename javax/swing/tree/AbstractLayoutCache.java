package javax.swing.tree;

import java.awt.Rectangle;
import java.util.Enumeration;
import javax.swing.event.TreeModelEvent;

public abstract class AbstractLayoutCache
  implements RowMapper
{
  protected NodeDimensions nodeDimensions;
  protected TreeModel treeModel;
  protected TreeSelectionModel treeSelectionModel;
  protected boolean rootVisible;
  protected int rowHeight;
  
  public AbstractLayoutCache() {}
  
  public void setNodeDimensions(NodeDimensions paramNodeDimensions)
  {
    nodeDimensions = paramNodeDimensions;
  }
  
  public NodeDimensions getNodeDimensions()
  {
    return nodeDimensions;
  }
  
  public void setModel(TreeModel paramTreeModel)
  {
    treeModel = paramTreeModel;
  }
  
  public TreeModel getModel()
  {
    return treeModel;
  }
  
  public void setRootVisible(boolean paramBoolean)
  {
    rootVisible = paramBoolean;
  }
  
  public boolean isRootVisible()
  {
    return rootVisible;
  }
  
  public void setRowHeight(int paramInt)
  {
    rowHeight = paramInt;
  }
  
  public int getRowHeight()
  {
    return rowHeight;
  }
  
  public void setSelectionModel(TreeSelectionModel paramTreeSelectionModel)
  {
    if (treeSelectionModel != null) {
      treeSelectionModel.setRowMapper(null);
    }
    treeSelectionModel = paramTreeSelectionModel;
    if (treeSelectionModel != null) {
      treeSelectionModel.setRowMapper(this);
    }
  }
  
  public TreeSelectionModel getSelectionModel()
  {
    return treeSelectionModel;
  }
  
  public int getPreferredHeight()
  {
    int i = getRowCount();
    if (i > 0)
    {
      Rectangle localRectangle = getBounds(getPathForRow(i - 1), null);
      if (localRectangle != null) {
        return y + height;
      }
    }
    return 0;
  }
  
  public int getPreferredWidth(Rectangle paramRectangle)
  {
    int i = getRowCount();
    if (i > 0)
    {
      TreePath localTreePath;
      int j;
      if (paramRectangle == null)
      {
        localTreePath = getPathForRow(0);
        j = Integer.MAX_VALUE;
      }
      else
      {
        localTreePath = getPathClosestTo(x, y);
        j = height + y;
      }
      Enumeration localEnumeration = getVisiblePathsFrom(localTreePath);
      if ((localEnumeration != null) && (localEnumeration.hasMoreElements()))
      {
        Rectangle localRectangle = getBounds((TreePath)localEnumeration.nextElement(), null);
        int k;
        if (localRectangle != null)
        {
          k = x + width;
          if (y >= j) {
            return k;
          }
        }
        else
        {
          k = 0;
        }
        while ((localRectangle != null) && (localEnumeration.hasMoreElements()))
        {
          localRectangle = getBounds((TreePath)localEnumeration.nextElement(), localRectangle);
          if ((localRectangle != null) && (y < j)) {
            k = Math.max(k, x + width);
          } else {
            localRectangle = null;
          }
        }
        return k;
      }
    }
    return 0;
  }
  
  public abstract boolean isExpanded(TreePath paramTreePath);
  
  public abstract Rectangle getBounds(TreePath paramTreePath, Rectangle paramRectangle);
  
  public abstract TreePath getPathForRow(int paramInt);
  
  public abstract int getRowForPath(TreePath paramTreePath);
  
  public abstract TreePath getPathClosestTo(int paramInt1, int paramInt2);
  
  public abstract Enumeration<TreePath> getVisiblePathsFrom(TreePath paramTreePath);
  
  public abstract int getVisibleChildCount(TreePath paramTreePath);
  
  public abstract void setExpandedState(TreePath paramTreePath, boolean paramBoolean);
  
  public abstract boolean getExpandedState(TreePath paramTreePath);
  
  public abstract int getRowCount();
  
  public abstract void invalidateSizes();
  
  public abstract void invalidatePathBounds(TreePath paramTreePath);
  
  public abstract void treeNodesChanged(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeNodesInserted(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeNodesRemoved(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeStructureChanged(TreeModelEvent paramTreeModelEvent);
  
  public int[] getRowsForPaths(TreePath[] paramArrayOfTreePath)
  {
    if (paramArrayOfTreePath == null) {
      return null;
    }
    int i = paramArrayOfTreePath.length;
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++) {
      arrayOfInt[j] = getRowForPath(paramArrayOfTreePath[j]);
    }
    return arrayOfInt;
  }
  
  protected Rectangle getNodeDimensions(Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean, Rectangle paramRectangle)
  {
    NodeDimensions localNodeDimensions = getNodeDimensions();
    if (localNodeDimensions != null) {
      return localNodeDimensions.getNodeDimensions(paramObject, paramInt1, paramInt2, paramBoolean, paramRectangle);
    }
    return null;
  }
  
  protected boolean isFixedRowHeight()
  {
    return rowHeight > 0;
  }
  
  public static abstract class NodeDimensions
  {
    public NodeDimensions() {}
    
    public abstract Rectangle getNodeDimensions(Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean, Rectangle paramRectangle);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\AbstractLayoutCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */