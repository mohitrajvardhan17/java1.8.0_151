package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeSelectionEvent
  extends EventObject
{
  protected TreePath[] paths;
  protected boolean[] areNew;
  protected TreePath oldLeadSelectionPath;
  protected TreePath newLeadSelectionPath;
  
  public TreeSelectionEvent(Object paramObject, TreePath[] paramArrayOfTreePath, boolean[] paramArrayOfBoolean, TreePath paramTreePath1, TreePath paramTreePath2)
  {
    super(paramObject);
    paths = paramArrayOfTreePath;
    areNew = paramArrayOfBoolean;
    oldLeadSelectionPath = paramTreePath1;
    newLeadSelectionPath = paramTreePath2;
  }
  
  public TreeSelectionEvent(Object paramObject, TreePath paramTreePath1, boolean paramBoolean, TreePath paramTreePath2, TreePath paramTreePath3)
  {
    super(paramObject);
    paths = new TreePath[1];
    paths[0] = paramTreePath1;
    areNew = new boolean[1];
    areNew[0] = paramBoolean;
    oldLeadSelectionPath = paramTreePath2;
    newLeadSelectionPath = paramTreePath3;
  }
  
  public TreePath[] getPaths()
  {
    int i = paths.length;
    TreePath[] arrayOfTreePath = new TreePath[i];
    System.arraycopy(paths, 0, arrayOfTreePath, 0, i);
    return arrayOfTreePath;
  }
  
  public TreePath getPath()
  {
    return paths[0];
  }
  
  public boolean isAddedPath()
  {
    return areNew[0];
  }
  
  public boolean isAddedPath(TreePath paramTreePath)
  {
    for (int i = paths.length - 1; i >= 0; i--) {
      if (paths[i].equals(paramTreePath)) {
        return areNew[i];
      }
    }
    throw new IllegalArgumentException("path is not a path identified by the TreeSelectionEvent");
  }
  
  public boolean isAddedPath(int paramInt)
  {
    if ((paths == null) || (paramInt < 0) || (paramInt >= paths.length)) {
      throw new IllegalArgumentException("index is beyond range of added paths identified by TreeSelectionEvent");
    }
    return areNew[paramInt];
  }
  
  public TreePath getOldLeadSelectionPath()
  {
    return oldLeadSelectionPath;
  }
  
  public TreePath getNewLeadSelectionPath()
  {
    return newLeadSelectionPath;
  }
  
  public Object cloneWithSource(Object paramObject)
  {
    return new TreeSelectionEvent(paramObject, paths, areNew, oldLeadSelectionPath, newLeadSelectionPath);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TreeSelectionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */