package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeModelEvent
  extends EventObject
{
  protected TreePath path;
  protected int[] childIndices;
  protected Object[] children;
  
  public TreeModelEvent(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    this(paramObject, paramArrayOfObject1 == null ? null : new TreePath(paramArrayOfObject1), paramArrayOfInt, paramArrayOfObject2);
  }
  
  public TreeModelEvent(Object paramObject, TreePath paramTreePath, int[] paramArrayOfInt, Object[] paramArrayOfObject)
  {
    super(paramObject);
    path = paramTreePath;
    childIndices = paramArrayOfInt;
    children = paramArrayOfObject;
  }
  
  public TreeModelEvent(Object paramObject, Object[] paramArrayOfObject)
  {
    this(paramObject, paramArrayOfObject == null ? null : new TreePath(paramArrayOfObject));
  }
  
  public TreeModelEvent(Object paramObject, TreePath paramTreePath)
  {
    super(paramObject);
    path = paramTreePath;
    childIndices = new int[0];
  }
  
  public TreePath getTreePath()
  {
    return path;
  }
  
  public Object[] getPath()
  {
    if (path != null) {
      return path.getPath();
    }
    return null;
  }
  
  public Object[] getChildren()
  {
    if (children != null)
    {
      int i = children.length;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(children, 0, arrayOfObject, 0, i);
      return arrayOfObject;
    }
    return null;
  }
  
  public int[] getChildIndices()
  {
    if (childIndices != null)
    {
      int i = childIndices.length;
      int[] arrayOfInt = new int[i];
      System.arraycopy(childIndices, 0, arrayOfInt, 0, i);
      return arrayOfInt;
    }
    return null;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getClass().getName() + " " + Integer.toString(hashCode()));
    if (path != null) {
      localStringBuffer.append(" path " + path);
    }
    int i;
    if (childIndices != null)
    {
      localStringBuffer.append(" indices [ ");
      for (i = 0; i < childIndices.length; i++) {
        localStringBuffer.append(Integer.toString(childIndices[i]) + " ");
      }
      localStringBuffer.append("]");
    }
    if (children != null)
    {
      localStringBuffer.append(" children [ ");
      for (i = 0; i < children.length; i++) {
        localStringBuffer.append(children[i] + " ");
      }
      localStringBuffer.append("]");
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TreeModelEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */