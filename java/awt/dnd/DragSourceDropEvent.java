package java.awt.dnd;

public class DragSourceDropEvent
  extends DragSourceEvent
{
  private static final long serialVersionUID = -5571321229470821891L;
  private boolean dropSuccess;
  private int dropAction = 0;
  
  public DragSourceDropEvent(DragSourceContext paramDragSourceContext, int paramInt, boolean paramBoolean)
  {
    super(paramDragSourceContext);
    dropSuccess = paramBoolean;
    dropAction = paramInt;
  }
  
  public DragSourceDropEvent(DragSourceContext paramDragSourceContext, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3)
  {
    super(paramDragSourceContext, paramInt2, paramInt3);
    dropSuccess = paramBoolean;
    dropAction = paramInt1;
  }
  
  public DragSourceDropEvent(DragSourceContext paramDragSourceContext)
  {
    super(paramDragSourceContext);
    dropSuccess = false;
  }
  
  public boolean getDropSuccess()
  {
    return dropSuccess;
  }
  
  public int getDropAction()
  {
    return dropAction;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DragSourceDropEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */