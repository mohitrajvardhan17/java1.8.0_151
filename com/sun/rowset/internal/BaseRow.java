package com.sun.rowset.internal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class BaseRow
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 4152013523511412238L;
  protected Object[] origVals;
  
  public BaseRow() {}
  
  public Object[] getOrigRow()
  {
    Object[] arrayOfObject = origVals;
    return arrayOfObject == null ? null : Arrays.copyOf(arrayOfObject, arrayOfObject.length);
  }
  
  public abstract Object getColumnObject(int paramInt)
    throws SQLException;
  
  public abstract void setColumnObject(int paramInt, Object paramObject)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\internal\BaseRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */