package javax.swing.event;

import java.util.EventObject;
import javax.swing.RowSorter;

public class RowSorterEvent
  extends EventObject
{
  private Type type;
  private int[] oldViewToModel;
  
  public RowSorterEvent(RowSorter paramRowSorter)
  {
    this(paramRowSorter, Type.SORT_ORDER_CHANGED, null);
  }
  
  public RowSorterEvent(RowSorter paramRowSorter, Type paramType, int[] paramArrayOfInt)
  {
    super(paramRowSorter);
    if (paramType == null) {
      throw new IllegalArgumentException("type must be non-null");
    }
    type = paramType;
    oldViewToModel = paramArrayOfInt;
  }
  
  public RowSorter getSource()
  {
    return (RowSorter)super.getSource();
  }
  
  public Type getType()
  {
    return type;
  }
  
  public int convertPreviousRowIndexToModel(int paramInt)
  {
    if ((oldViewToModel != null) && (paramInt >= 0) && (paramInt < oldViewToModel.length)) {
      return oldViewToModel[paramInt];
    }
    return -1;
  }
  
  public int getPreviousRowCount()
  {
    return oldViewToModel == null ? 0 : oldViewToModel.length;
  }
  
  public static enum Type
  {
    SORT_ORDER_CHANGED,  SORTED;
    
    private Type() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\RowSorterEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */