package javax.swing.event;

import java.util.EventObject;
import javax.swing.table.TableColumnModel;

public class TableColumnModelEvent
  extends EventObject
{
  protected int fromIndex;
  protected int toIndex;
  
  public TableColumnModelEvent(TableColumnModel paramTableColumnModel, int paramInt1, int paramInt2)
  {
    super(paramTableColumnModel);
    fromIndex = paramInt1;
    toIndex = paramInt2;
  }
  
  public int getFromIndex()
  {
    return fromIndex;
  }
  
  public int getToIndex()
  {
    return toIndex;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\TableColumnModelEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */