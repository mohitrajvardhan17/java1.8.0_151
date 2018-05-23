package java.awt.dnd;

import java.util.EventObject;

public class DropTargetEvent
  extends EventObject
{
  private static final long serialVersionUID = 2821229066521922993L;
  protected DropTargetContext context;
  
  public DropTargetEvent(DropTargetContext paramDropTargetContext)
  {
    super(paramDropTargetContext.getDropTarget());
    context = paramDropTargetContext;
  }
  
  public DropTargetContext getDropTargetContext()
  {
    return context;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DropTargetEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */