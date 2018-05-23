package javax.swing.table;

import java.util.Enumeration;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableColumnModelListener;

public abstract interface TableColumnModel
{
  public abstract void addColumn(TableColumn paramTableColumn);
  
  public abstract void removeColumn(TableColumn paramTableColumn);
  
  public abstract void moveColumn(int paramInt1, int paramInt2);
  
  public abstract void setColumnMargin(int paramInt);
  
  public abstract int getColumnCount();
  
  public abstract Enumeration<TableColumn> getColumns();
  
  public abstract int getColumnIndex(Object paramObject);
  
  public abstract TableColumn getColumn(int paramInt);
  
  public abstract int getColumnMargin();
  
  public abstract int getColumnIndexAtX(int paramInt);
  
  public abstract int getTotalColumnWidth();
  
  public abstract void setColumnSelectionAllowed(boolean paramBoolean);
  
  public abstract boolean getColumnSelectionAllowed();
  
  public abstract int[] getSelectedColumns();
  
  public abstract int getSelectedColumnCount();
  
  public abstract void setSelectionModel(ListSelectionModel paramListSelectionModel);
  
  public abstract ListSelectionModel getSelectionModel();
  
  public abstract void addColumnModelListener(TableColumnModelListener paramTableColumnModelListener);
  
  public abstract void removeColumnModelListener(TableColumnModelListener paramTableColumnModelListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\TableColumnModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */