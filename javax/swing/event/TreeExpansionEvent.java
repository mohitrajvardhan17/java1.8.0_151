package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeExpansionEvent
  extends EventObject
{
  protected TreePath path;
  
  public TreeExpansionEvent(Object paramObject, TreePath paramTreePath)
  {
    super(paramObject);
    path = paramTreePath;
  }
  
  public TreePath getPath()
  {
    return path;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TreeExpansionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */