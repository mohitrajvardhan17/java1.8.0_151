package com.sun.rowset.internal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.BitSet;

public class Row
  extends BaseRow
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 5047859032611314762L;
  private Object[] currentVals;
  private BitSet colsChanged;
  private boolean deleted;
  private boolean updated;
  private boolean inserted;
  private int numCols;
  
  public Row(int paramInt)
  {
    origVals = new Object[paramInt];
    currentVals = new Object[paramInt];
    colsChanged = new BitSet(paramInt);
    numCols = paramInt;
  }
  
  public Row(int paramInt, Object[] paramArrayOfObject)
  {
    origVals = new Object[paramInt];
    System.arraycopy(paramArrayOfObject, 0, origVals, 0, paramInt);
    currentVals = new Object[paramInt];
    colsChanged = new BitSet(paramInt);
    numCols = paramInt;
  }
  
  public void initColumnObject(int paramInt, Object paramObject)
  {
    origVals[(paramInt - 1)] = paramObject;
  }
  
  public void setColumnObject(int paramInt, Object paramObject)
  {
    currentVals[(paramInt - 1)] = paramObject;
    setColUpdated(paramInt - 1);
  }
  
  public Object getColumnObject(int paramInt)
    throws SQLException
  {
    if (getColUpdated(paramInt - 1)) {
      return currentVals[(paramInt - 1)];
    }
    return origVals[(paramInt - 1)];
  }
  
  public boolean getColUpdated(int paramInt)
  {
    return colsChanged.get(paramInt);
  }
  
  public void setDeleted()
  {
    deleted = true;
  }
  
  public boolean getDeleted()
  {
    return deleted;
  }
  
  public void clearDeleted()
  {
    deleted = false;
  }
  
  public void setInserted()
  {
    inserted = true;
  }
  
  public boolean getInserted()
  {
    return inserted;
  }
  
  public void clearInserted()
  {
    inserted = false;
  }
  
  public boolean getUpdated()
  {
    return updated;
  }
  
  public void setUpdated()
  {
    for (int i = 0; i < numCols; i++) {
      if (getColUpdated(i) == true)
      {
        updated = true;
        return;
      }
    }
  }
  
  private void setColUpdated(int paramInt)
  {
    colsChanged.set(paramInt);
  }
  
  public void clearUpdated()
  {
    updated = false;
    for (int i = 0; i < numCols; i++)
    {
      currentVals[i] = null;
      colsChanged.clear(i);
    }
  }
  
  public void moveCurrentToOrig()
  {
    for (int i = 0; i < numCols; i++) {
      if (getColUpdated(i) == true)
      {
        origVals[i] = currentVals[i];
        currentVals[i] = null;
        colsChanged.clear(i);
      }
    }
    updated = false;
  }
  
  public BaseRow getCurrentRow()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\internal\Row.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */