package javax.swing.table;

import javax.swing.event.TableModelListener;

public abstract interface TableModel
{
  public abstract int getRowCount();
  
  public abstract int getColumnCount();
  
  public abstract String getColumnName(int paramInt);
  
  public abstract Class<?> getColumnClass(int paramInt);
  
  public abstract boolean isCellEditable(int paramInt1, int paramInt2);
  
  public abstract Object getValueAt(int paramInt1, int paramInt2);
  
  public abstract void setValueAt(Object paramObject, int paramInt1, int paramInt2);
  
  public abstract void addTableModelListener(TableModelListener paramTableModelListener);
  
  public abstract void removeTableModelListener(TableModelListener paramTableModelListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\TableModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */