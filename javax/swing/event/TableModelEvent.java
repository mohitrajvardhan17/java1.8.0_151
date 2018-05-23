package javax.swing.event;

import java.util.EventObject;
import javax.swing.table.TableModel;

public class TableModelEvent
  extends EventObject
{
  public static final int INSERT = 1;
  public static final int UPDATE = 0;
  public static final int DELETE = -1;
  public static final int HEADER_ROW = -1;
  public static final int ALL_COLUMNS = -1;
  protected int type;
  protected int firstRow;
  protected int lastRow;
  protected int column;
  
  public TableModelEvent(TableModel paramTableModel)
  {
    this(paramTableModel, 0, Integer.MAX_VALUE, -1, 0);
  }
  
  public TableModelEvent(TableModel paramTableModel, int paramInt)
  {
    this(paramTableModel, paramInt, paramInt, -1, 0);
  }
  
  public TableModelEvent(TableModel paramTableModel, int paramInt1, int paramInt2)
  {
    this(paramTableModel, paramInt1, paramInt2, -1, 0);
  }
  
  public TableModelEvent(TableModel paramTableModel, int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramTableModel, paramInt1, paramInt2, paramInt3, 0);
  }
  
  public TableModelEvent(TableModel paramTableModel, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super(paramTableModel);
    firstRow = paramInt1;
    lastRow = paramInt2;
    column = paramInt3;
    type = paramInt4;
  }
  
  public int getFirstRow()
  {
    return firstRow;
  }
  
  public int getLastRow()
  {
    return lastRow;
  }
  
  public int getColumn()
  {
    return column;
  }
  
  public int getType()
  {
    return type;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TableModelEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */