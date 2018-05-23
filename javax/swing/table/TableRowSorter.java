package javax.swing.table;

import java.text.Collator;
import java.util.Comparator;
import javax.swing.DefaultRowSorter;
import javax.swing.DefaultRowSorter.ModelWrapper;

public class TableRowSorter<M extends TableModel>
  extends DefaultRowSorter<M, Integer>
{
  private static final Comparator COMPARABLE_COMPARATOR = new ComparableComparator(null);
  private M tableModel;
  private TableStringConverter stringConverter;
  
  public TableRowSorter()
  {
    this(null);
  }
  
  public TableRowSorter(M paramM)
  {
    setModel(paramM);
  }
  
  public void setModel(M paramM)
  {
    tableModel = paramM;
    setModelWrapper(new TableRowSorterModelWrapper(null));
  }
  
  public void setStringConverter(TableStringConverter paramTableStringConverter)
  {
    stringConverter = paramTableStringConverter;
  }
  
  public TableStringConverter getStringConverter()
  {
    return stringConverter;
  }
  
  public Comparator<?> getComparator(int paramInt)
  {
    Comparator localComparator = super.getComparator(paramInt);
    if (localComparator != null) {
      return localComparator;
    }
    Class localClass = ((TableModel)getModel()).getColumnClass(paramInt);
    if (localClass == String.class) {
      return Collator.getInstance();
    }
    if (Comparable.class.isAssignableFrom(localClass)) {
      return COMPARABLE_COMPARATOR;
    }
    return Collator.getInstance();
  }
  
  protected boolean useToString(int paramInt)
  {
    Comparator localComparator = super.getComparator(paramInt);
    if (localComparator != null) {
      return false;
    }
    Class localClass = ((TableModel)getModel()).getColumnClass(paramInt);
    if (localClass == String.class) {
      return false;
    }
    return !Comparable.class.isAssignableFrom(localClass);
  }
  
  private static class ComparableComparator
    implements Comparator
  {
    private ComparableComparator() {}
    
    public int compare(Object paramObject1, Object paramObject2)
    {
      return ((Comparable)paramObject1).compareTo(paramObject2);
    }
  }
  
  private class TableRowSorterModelWrapper
    extends DefaultRowSorter.ModelWrapper<M, Integer>
  {
    private TableRowSorterModelWrapper() {}
    
    public M getModel()
    {
      return tableModel;
    }
    
    public int getColumnCount()
    {
      return tableModel == null ? 0 : tableModel.getColumnCount();
    }
    
    public int getRowCount()
    {
      return tableModel == null ? 0 : tableModel.getRowCount();
    }
    
    public Object getValueAt(int paramInt1, int paramInt2)
    {
      return tableModel.getValueAt(paramInt1, paramInt2);
    }
    
    public String getStringValueAt(int paramInt1, int paramInt2)
    {
      TableStringConverter localTableStringConverter = getStringConverter();
      if (localTableStringConverter != null)
      {
        localObject = localTableStringConverter.toString(tableModel, paramInt1, paramInt2);
        if (localObject != null) {
          return (String)localObject;
        }
        return "";
      }
      Object localObject = getValueAt(paramInt1, paramInt2);
      if (localObject == null) {
        return "";
      }
      String str = localObject.toString();
      if (str == null) {
        return "";
      }
      return str;
    }
    
    public Integer getIdentifier(int paramInt)
    {
      return Integer.valueOf(paramInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\TableRowSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */