package javax.swing.tree;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class TreePath
  implements Serializable
{
  private TreePath parentPath;
  private Object lastPathComponent;
  
  @ConstructorProperties({"path"})
  public TreePath(Object[] paramArrayOfObject)
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) {
      throw new IllegalArgumentException("path in TreePath must be non null and not empty.");
    }
    lastPathComponent = paramArrayOfObject[(paramArrayOfObject.length - 1)];
    if (lastPathComponent == null) {
      throw new IllegalArgumentException("Last path component must be non-null");
    }
    if (paramArrayOfObject.length > 1) {
      parentPath = new TreePath(paramArrayOfObject, paramArrayOfObject.length - 1);
    }
  }
  
  public TreePath(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("path in TreePath must be non null.");
    }
    lastPathComponent = paramObject;
    parentPath = null;
  }
  
  protected TreePath(TreePath paramTreePath, Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("path in TreePath must be non null.");
    }
    parentPath = paramTreePath;
    lastPathComponent = paramObject;
  }
  
  protected TreePath(Object[] paramArrayOfObject, int paramInt)
  {
    lastPathComponent = paramArrayOfObject[(paramInt - 1)];
    if (lastPathComponent == null) {
      throw new IllegalArgumentException("Path elements must be non-null");
    }
    if (paramInt > 1) {
      parentPath = new TreePath(paramArrayOfObject, paramInt - 1);
    }
  }
  
  protected TreePath() {}
  
  public Object[] getPath()
  {
    int i = getPathCount();
    Object[] arrayOfObject = new Object[i--];
    for (TreePath localTreePath = this; localTreePath != null; localTreePath = localTreePath.getParentPath()) {
      arrayOfObject[(i--)] = localTreePath.getLastPathComponent();
    }
    return arrayOfObject;
  }
  
  public Object getLastPathComponent()
  {
    return lastPathComponent;
  }
  
  public int getPathCount()
  {
    int i = 0;
    for (TreePath localTreePath = this; localTreePath != null; localTreePath = localTreePath.getParentPath()) {
      i++;
    }
    return i;
  }
  
  public Object getPathComponent(int paramInt)
  {
    int i = getPathCount();
    if ((paramInt < 0) || (paramInt >= i)) {
      throw new IllegalArgumentException("Index " + paramInt + " is out of the specified range");
    }
    TreePath localTreePath = this;
    for (int j = i - 1; j != paramInt; j--) {
      localTreePath = localTreePath.getParentPath();
    }
    return localTreePath.getLastPathComponent();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof TreePath))
    {
      TreePath localTreePath1 = (TreePath)paramObject;
      if (getPathCount() != localTreePath1.getPathCount()) {
        return false;
      }
      for (TreePath localTreePath2 = this; localTreePath2 != null; localTreePath2 = localTreePath2.getParentPath())
      {
        if (!localTreePath2.getLastPathComponent().equals(localTreePath1.getLastPathComponent())) {
          return false;
        }
        localTreePath1 = localTreePath1.getParentPath();
      }
      return true;
    }
    return false;
  }
  
  public int hashCode()
  {
    return getLastPathComponent().hashCode();
  }
  
  public boolean isDescendant(TreePath paramTreePath)
  {
    if (paramTreePath == this) {
      return true;
    }
    if (paramTreePath != null)
    {
      int i = getPathCount();
      int j = paramTreePath.getPathCount();
      if (j < i) {
        return false;
      }
      while (j-- > i) {
        paramTreePath = paramTreePath.getParentPath();
      }
      return equals(paramTreePath);
    }
    return false;
  }
  
  public TreePath pathByAddingChild(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException("Null child not allowed");
    }
    return new TreePath(this, paramObject);
  }
  
  public TreePath getParentPath()
  {
    return parentPath;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("[");
    int i = 0;
    int j = getPathCount();
    while (i < j)
    {
      if (i > 0) {
        localStringBuffer.append(", ");
      }
      localStringBuffer.append(getPathComponent(i));
      i++;
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\TreePath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */