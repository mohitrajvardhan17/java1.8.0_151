package javax.swing.event;

import java.util.EventListener;

public abstract interface CellEditorListener
  extends EventListener
{
  public abstract void editingStopped(ChangeEvent paramChangeEvent);
  
  public abstract void editingCanceled(ChangeEvent paramChangeEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\CellEditorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */