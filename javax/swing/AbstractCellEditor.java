package javax.swing;

import java.io.Serializable;
import java.util.EventObject;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

public abstract class AbstractCellEditor
  implements CellEditor, Serializable
{
  protected EventListenerList listenerList = new EventListenerList();
  protected transient ChangeEvent changeEvent = null;
  
  public AbstractCellEditor() {}
  
  public boolean isCellEditable(EventObject paramEventObject)
  {
    return true;
  }
  
  public boolean shouldSelectCell(EventObject paramEventObject)
  {
    return true;
  }
  
  public boolean stopCellEditing()
  {
    fireEditingStopped();
    return true;
  }
  
  public void cancelCellEditing()
  {
    fireEditingCanceled();
  }
  
  public void addCellEditorListener(CellEditorListener paramCellEditorListener)
  {
    listenerList.add(CellEditorListener.class, paramCellEditorListener);
  }
  
  public void removeCellEditorListener(CellEditorListener paramCellEditorListener)
  {
    listenerList.remove(CellEditorListener.class, paramCellEditorListener);
  }
  
  public CellEditorListener[] getCellEditorListeners()
  {
    return (CellEditorListener[])listenerList.getListeners(CellEditorListener.class);
  }
  
  protected void fireEditingStopped()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == CellEditorListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((CellEditorListener)arrayOfObject[(i + 1)]).editingStopped(changeEvent);
      }
    }
  }
  
  protected void fireEditingCanceled()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == CellEditorListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((CellEditorListener)arrayOfObject[(i + 1)]).editingCanceled(changeEvent);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\AbstractCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */