package javax.swing.event;

import java.util.EventListener;

public abstract interface TableColumnModelListener
  extends EventListener
{
  public abstract void columnAdded(TableColumnModelEvent paramTableColumnModelEvent);
  
  public abstract void columnRemoved(TableColumnModelEvent paramTableColumnModelEvent);
  
  public abstract void columnMoved(TableColumnModelEvent paramTableColumnModelEvent);
  
  public abstract void columnMarginChanged(ChangeEvent paramChangeEvent);
  
  public abstract void columnSelectionChanged(ListSelectionEvent paramListSelectionEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TableColumnModelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */