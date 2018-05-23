package javax.swing.table;

import java.io.Serializable;
import java.util.Vector;
import javax.swing.event.TableModelEvent;

public class DefaultTableModel
  extends AbstractTableModel
  implements Serializable
{
  protected Vector dataVector;
  protected Vector columnIdentifiers;
  
  public DefaultTableModel()
  {
    this(0, 0);
  }
  
  private static Vector newVector(int paramInt)
  {
    Vector localVector = new Vector(paramInt);
    localVector.setSize(paramInt);
    return localVector;
  }
  
  public DefaultTableModel(int paramInt1, int paramInt2)
  {
    this(newVector(paramInt2), paramInt1);
  }
  
  public DefaultTableModel(Vector paramVector, int paramInt)
  {
    setDataVector(newVector(paramInt), paramVector);
  }
  
  public DefaultTableModel(Object[] paramArrayOfObject, int paramInt)
  {
    this(convertToVector(paramArrayOfObject), paramInt);
  }
  
  public DefaultTableModel(Vector paramVector1, Vector paramVector2)
  {
    setDataVector(paramVector1, paramVector2);
  }
  
  public DefaultTableModel(Object[][] paramArrayOfObject, Object[] paramArrayOfObject1)
  {
    setDataVector(paramArrayOfObject, paramArrayOfObject1);
  }
  
  public Vector getDataVector()
  {
    return dataVector;
  }
  
  private static Vector nonNullVector(Vector paramVector)
  {
    return paramVector != null ? paramVector : new Vector();
  }
  
  public void setDataVector(Vector paramVector1, Vector paramVector2)
  {
    dataVector = nonNullVector(paramVector1);
    columnIdentifiers = nonNullVector(paramVector2);
    justifyRows(0, getRowCount());
    fireTableStructureChanged();
  }
  
  public void setDataVector(Object[][] paramArrayOfObject, Object[] paramArrayOfObject1)
  {
    setDataVector(convertToVector(paramArrayOfObject), convertToVector(paramArrayOfObject1));
  }
  
  public void newDataAvailable(TableModelEvent paramTableModelEvent)
  {
    fireTableChanged(paramTableModelEvent);
  }
  
  private void justifyRows(int paramInt1, int paramInt2)
  {
    dataVector.setSize(getRowCount());
    for (int i = paramInt1; i < paramInt2; i++)
    {
      if (dataVector.elementAt(i) == null) {
        dataVector.setElementAt(new Vector(), i);
      }
      ((Vector)dataVector.elementAt(i)).setSize(getColumnCount());
    }
  }
  
  public void newRowsAdded(TableModelEvent paramTableModelEvent)
  {
    justifyRows(paramTableModelEvent.getFirstRow(), paramTableModelEvent.getLastRow() + 1);
    fireTableChanged(paramTableModelEvent);
  }
  
  public void rowsRemoved(TableModelEvent paramTableModelEvent)
  {
    fireTableChanged(paramTableModelEvent);
  }
  
  public void setNumRows(int paramInt)
  {
    int i = getRowCount();
    if (i == paramInt) {
      return;
    }
    dataVector.setSize(paramInt);
    if (paramInt <= i)
    {
      fireTableRowsDeleted(paramInt, i - 1);
    }
    else
    {
      justifyRows(i, paramInt);
      fireTableRowsInserted(i, paramInt - 1);
    }
  }
  
  public void setRowCount(int paramInt)
  {
    setNumRows(paramInt);
  }
  
  public void addRow(Vector paramVector)
  {
    insertRow(getRowCount(), paramVector);
  }
  
  public void addRow(Object[] paramArrayOfObject)
  {
    addRow(convertToVector(paramArrayOfObject));
  }
  
  public void insertRow(int paramInt, Vector paramVector)
  {
    dataVector.insertElementAt(paramVector, paramInt);
    justifyRows(paramInt, paramInt + 1);
    fireTableRowsInserted(paramInt, paramInt);
  }
  
  public void insertRow(int paramInt, Object[] paramArrayOfObject)
  {
    insertRow(paramInt, convertToVector(paramArrayOfObject));
  }
  
  private static int gcd(int paramInt1, int paramInt2)
  {
    return paramInt2 == 0 ? paramInt1 : gcd(paramInt2, paramInt1 % paramInt2);
  }
  
  private static void rotate(Vector paramVector, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt2 - paramInt1;
    int j = i - paramInt3;
    int k = gcd(i, j);
    for (int m = 0; m < k; m++)
    {
      int n = m;
      Object localObject = paramVector.elementAt(paramInt1 + n);
      for (int i1 = (n + j) % i; i1 != m; i1 = (n + j) % i)
      {
        paramVector.setElementAt(paramVector.elementAt(paramInt1 + i1), paramInt1 + n);
        n = i1;
      }
      paramVector.setElementAt(localObject, paramInt1 + n);
    }
  }
  
  public void moveRow(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt3 - paramInt1;
    int j;
    int k;
    if (i < 0)
    {
      j = paramInt3;
      k = paramInt2;
    }
    else
    {
      j = paramInt1;
      k = paramInt3 + paramInt2 - paramInt1;
    }
    rotate(dataVector, j, k + 1, i);
    fireTableRowsUpdated(j, k);
  }
  
  public void removeRow(int paramInt)
  {
    dataVector.removeElementAt(paramInt);
    fireTableRowsDeleted(paramInt, paramInt);
  }
  
  public void setColumnIdentifiers(Vector paramVector)
  {
    setDataVector(dataVector, paramVector);
  }
  
  public void setColumnIdentifiers(Object[] paramArrayOfObject)
  {
    setColumnIdentifiers(convertToVector(paramArrayOfObject));
  }
  
  public void setColumnCount(int paramInt)
  {
    columnIdentifiers.setSize(paramInt);
    justifyRows(0, getRowCount());
    fireTableStructureChanged();
  }
  
  public void addColumn(Object paramObject)
  {
    addColumn(paramObject, (Vector)null);
  }
  
  public void addColumn(Object paramObject, Vector paramVector)
  {
    columnIdentifiers.addElement(paramObject);
    if (paramVector != null)
    {
      int i = paramVector.size();
      if (i > getRowCount()) {
        dataVector.setSize(i);
      }
      justifyRows(0, getRowCount());
      int j = getColumnCount() - 1;
      for (int k = 0; k < i; k++)
      {
        Vector localVector = (Vector)dataVector.elementAt(k);
        localVector.setElementAt(paramVector.elementAt(k), j);
      }
    }
    else
    {
      justifyRows(0, getRowCount());
    }
    fireTableStructureChanged();
  }
  
  public void addColumn(Object paramObject, Object[] paramArrayOfObject)
  {
    addColumn(paramObject, convertToVector(paramArrayOfObject));
  }
  
  public int getRowCount()
  {
    return dataVector.size();
  }
  
  public int getColumnCount()
  {
    return columnIdentifiers.size();
  }
  
  public String getColumnName(int paramInt)
  {
    Object localObject = null;
    if ((paramInt < columnIdentifiers.size()) && (paramInt >= 0)) {
      localObject = columnIdentifiers.elementAt(paramInt);
    }
    return localObject == null ? super.getColumnName(paramInt) : localObject.toString();
  }
  
  public boolean isCellEditable(int paramInt1, int paramInt2)
  {
    return true;
  }
  
  public Object getValueAt(int paramInt1, int paramInt2)
  {
    Vector localVector = (Vector)dataVector.elementAt(paramInt1);
    return localVector.elementAt(paramInt2);
  }
  
  public void setValueAt(Object paramObject, int paramInt1, int paramInt2)
  {
    Vector localVector = (Vector)dataVector.elementAt(paramInt1);
    localVector.setElementAt(paramObject, paramInt2);
    fireTableCellUpdated(paramInt1, paramInt2);
  }
  
  protected static Vector convertToVector(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    Vector localVector = new Vector(paramArrayOfObject.length);
    for (Object localObject : paramArrayOfObject) {
      localVector.addElement(localObject);
    }
    return localVector;
  }
  
  protected static Vector convertToVector(Object[][] paramArrayOfObject)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    Vector localVector = new Vector(paramArrayOfObject.length);
    for (Object[] arrayOfObject1 : paramArrayOfObject) {
      localVector.addElement(convertToVector(arrayOfObject1));
    }
    return localVector;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\table\DefaultTableModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */