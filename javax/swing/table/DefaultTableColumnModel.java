package javax.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

public class DefaultTableColumnModel
  implements TableColumnModel, PropertyChangeListener, ListSelectionListener, Serializable
{
  protected Vector<TableColumn> tableColumns = new Vector();
  protected ListSelectionModel selectionModel;
  protected int columnMargin;
  protected EventListenerList listenerList = new EventListenerList();
  protected transient ChangeEvent changeEvent = null;
  protected boolean columnSelectionAllowed;
  protected int totalColumnWidth;
  
  public DefaultTableColumnModel()
  {
    setSelectionModel(createSelectionModel());
    setColumnMargin(1);
    invalidateWidthCache();
    setColumnSelectionAllowed(false);
  }
  
  public void addColumn(TableColumn paramTableColumn)
  {
    if (paramTableColumn == null) {
      throw new IllegalArgumentException("Object is null");
    }
    tableColumns.addElement(paramTableColumn);
    paramTableColumn.addPropertyChangeListener(this);
    invalidateWidthCache();
    fireColumnAdded(new TableColumnModelEvent(this, 0, getColumnCount() - 1));
  }
  
  public void removeColumn(TableColumn paramTableColumn)
  {
    int i = tableColumns.indexOf(paramTableColumn);
    if (i != -1)
    {
      if (selectionModel != null) {
        selectionModel.removeIndexInterval(i, i);
      }
      paramTableColumn.removePropertyChangeListener(this);
      tableColumns.removeElementAt(i);
      invalidateWidthCache();
      fireColumnRemoved(new TableColumnModelEvent(this, i, 0));
    }
  }
  
  public void moveColumn(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= getColumnCount()) || (paramInt2 < 0) || (paramInt2 >= getColumnCount())) {
      throw new IllegalArgumentException("moveColumn() - Index out of range");
    }
    if (paramInt1 == paramInt2)
    {
      fireColumnMoved(new TableColumnModelEvent(this, paramInt1, paramInt2));
      return;
    }
    TableColumn localTableColumn = (TableColumn)tableColumns.elementAt(paramInt1);
    tableColumns.removeElementAt(paramInt1);
    boolean bool = selectionModel.isSelectedIndex(paramInt1);
    selectionModel.removeIndexInterval(paramInt1, paramInt1);
    tableColumns.insertElementAt(localTableColumn, paramInt2);
    selectionModel.insertIndexInterval(paramInt2, 1, true);
    if (bool) {
      selectionModel.addSelectionInterval(paramInt2, paramInt2);
    } else {
      selectionModel.removeSelectionInterval(paramInt2, paramInt2);
    }
    fireColumnMoved(new TableColumnModelEvent(this, paramInt1, paramInt2));
  }
  
  public void setColumnMargin(int paramInt)
  {
    if (paramInt != columnMargin)
    {
      columnMargin = paramInt;
      fireColumnMarginChanged();
    }
  }
  
  public int getColumnCount()
  {
    return tableColumns.size();
  }
  
  public Enumeration<TableColumn> getColumns()
  {
    return tableColumns.elements();
  }
  
  public int getColumnIndex(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("Identifier is null");
    }
    Enumeration localEnumeration = getColumns();
    for (int i = 0; localEnumeration.hasMoreElements(); i++)
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      if (paramObject.equals(localTableColumn.getIdentifier())) {
        return i;
      }
    }
    throw new IllegalArgumentException("Identifier not found");
  }
  
  public TableColumn getColumn(int paramInt)
  {
    return (TableColumn)tableColumns.elementAt(paramInt);
  }
  
  public int getColumnMargin()
  {
    return columnMargin;
  }
  
  public int getColumnIndexAtX(int paramInt)
  {
    if (paramInt < 0) {
      return -1;
    }
    int i = getColumnCount();
    for (int j = 0; j < i; j++)
    {
      paramInt -= getColumn(j).getWidth();
      if (paramInt < 0) {
        return j;
      }
    }
    return -1;
  }
  
  public int getTotalColumnWidth()
  {
    if (totalColumnWidth == -1) {
      recalcWidthCache();
    }
    return totalColumnWidth;
  }
  
  public void setSelectionModel(ListSelectionModel paramListSelectionModel)
  {
    if (paramListSelectionModel == null) {
      throw new IllegalArgumentException("Cannot set a null SelectionModel");
    }
    ListSelectionModel localListSelectionModel = selectionModel;
    if (paramListSelectionModel != localListSelectionModel)
    {
      if (localListSelectionModel != null) {
        localListSelectionModel.removeListSelectionListener(this);
      }
      selectionModel = paramListSelectionModel;
      paramListSelectionModel.addListSelectionListener(this);
    }
  }
  
  public ListSelectionModel getSelectionModel()
  {
    return selectionModel;
  }
  
  public void setColumnSelectionAllowed(boolean paramBoolean)
  {
    columnSelectionAllowed = paramBoolean;
  }
  
  public boolean getColumnSelectionAllowed()
  {
    return columnSelectionAllowed;
  }
  
  public int[] getSelectedColumns()
  {
    if (selectionModel != null)
    {
      int i = selectionModel.getMinSelectionIndex();
      int j = selectionModel.getMaxSelectionIndex();
      if ((i == -1) || (j == -1)) {
        return new int[0];
      }
      int[] arrayOfInt1 = new int[1 + (j - i)];
      int k = 0;
      for (int m = i; m <= j; m++) {
        if (selectionModel.isSelectedIndex(m)) {
          arrayOfInt1[(k++)] = m;
        }
      }
      int[] arrayOfInt2 = new int[k];
      System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, k);
      return arrayOfInt2;
    }
    return new int[0];
  }
  
  public int getSelectedColumnCount()
  {
    if (selectionModel != null)
    {
      int i = selectionModel.getMinSelectionIndex();
      int j = selectionModel.getMaxSelectionIndex();
      int k = 0;
      for (int m = i; m <= j; m++) {
        if (selectionModel.isSelectedIndex(m)) {
          k++;
        }
      }
      return k;
    }
    return 0;
  }
  
  public void addColumnModelListener(TableColumnModelListener paramTableColumnModelListener)
  {
    listenerList.add(TableColumnModelListener.class, paramTableColumnModelListener);
  }
  
  public void removeColumnModelListener(TableColumnModelListener paramTableColumnModelListener)
  {
    listenerList.remove(TableColumnModelListener.class, paramTableColumnModelListener);
  }
  
  public TableColumnModelListener[] getColumnModelListeners()
  {
    return (TableColumnModelListener[])listenerList.getListeners(TableColumnModelListener.class);
  }
  
  protected void fireColumnAdded(TableColumnModelEvent paramTableColumnModelEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class) {
        ((TableColumnModelListener)arrayOfObject[(i + 1)]).columnAdded(paramTableColumnModelEvent);
      }
    }
  }
  
  protected void fireColumnRemoved(TableColumnModelEvent paramTableColumnModelEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class) {
        ((TableColumnModelListener)arrayOfObject[(i + 1)]).columnRemoved(paramTableColumnModelEvent);
      }
    }
  }
  
  protected void fireColumnMoved(TableColumnModelEvent paramTableColumnModelEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class) {
        ((TableColumnModelListener)arrayOfObject[(i + 1)]).columnMoved(paramTableColumnModelEvent);
      }
    }
  }
  
  protected void fireColumnSelectionChanged(ListSelectionEvent paramListSelectionEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class) {
        ((TableColumnModelListener)arrayOfObject[(i + 1)]).columnSelectionChanged(paramListSelectionEvent);
      }
    }
  }
  
  protected void fireColumnMarginChanged()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((TableColumnModelListener)arrayOfObject[(i + 1)]).columnMarginChanged(changeEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    if ((str == "width") || (str == "preferredWidth"))
    {
      invalidateWidthCache();
      fireColumnMarginChanged();
    }
  }
  
  public void valueChanged(ListSelectionEvent paramListSelectionEvent)
  {
    fireColumnSelectionChanged(paramListSelectionEvent);
  }
  
  protected ListSelectionModel createSelectionModel()
  {
    return new DefaultListSelectionModel();
  }
  
  protected void recalcWidthCache()
  {
    Enumeration localEnumeration = getColumns();
    for (totalColumnWidth = 0; localEnumeration.hasMoreElements(); totalColumnWidth += ((TableColumn)localEnumeration.nextElement()).getWidth()) {}
  }
  
  private void invalidateWidthCache()
  {
    totalColumnWidth = -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\DefaultTableColumnModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */