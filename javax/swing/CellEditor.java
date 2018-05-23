package javax.swing;

import java.util.EventObject;
import javax.swing.event.CellEditorListener;

public abstract interface CellEditor
{
  public abstract Object getCellEditorValue();
  
  public abstract boolean isCellEditable(EventObject paramEventObject);
  
  public abstract boolean shouldSelectCell(EventObject paramEventObject);
  
  public abstract boolean stopCellEditing();
  
  public abstract void cancelCellEditing();
  
  public abstract void addCellEditorListener(CellEditorListener paramCellEditorListener);
  
  public abstract void removeCellEditorListener(CellEditorListener paramCellEditorListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\CellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */