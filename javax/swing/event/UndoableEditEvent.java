package javax.swing.event;

import java.util.EventObject;
import javax.swing.undo.UndoableEdit;

public class UndoableEditEvent
  extends EventObject
{
  private UndoableEdit myEdit;
  
  public UndoableEditEvent(Object paramObject, UndoableEdit paramUndoableEdit)
  {
    super(paramObject);
    myEdit = paramUndoableEdit;
  }
  
  public UndoableEdit getEdit()
  {
    return myEdit;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\UndoableEditEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */