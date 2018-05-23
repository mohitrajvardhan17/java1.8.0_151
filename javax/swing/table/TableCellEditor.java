package javax.swing.table;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.JTable;

public abstract interface TableCellEditor
  extends CellEditor
{
  public abstract Component getTableCellEditorComponent(JTable paramJTable, Object paramObject, boolean paramBoolean, int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\TableCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */