package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.BitSet;
import javax.sql.RowSetMetaData;

public class InsertRow
  extends BaseRow
  implements Serializable, Cloneable
{
  private BitSet colsInserted;
  private int cols;
  private JdbcRowSetResourceBundle resBundle;
  static final long serialVersionUID = 1066099658102869344L;
  
  public InsertRow(int paramInt)
  {
    origVals = new Object[paramInt];
    colsInserted = new BitSet(paramInt);
    cols = paramInt;
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  protected void markColInserted(int paramInt)
  {
    colsInserted.set(paramInt);
  }
  
  public boolean isCompleteRow(RowSetMetaData paramRowSetMetaData)
    throws SQLException
  {
    for (int i = 0; i < cols; i++) {
      if ((!colsInserted.get(i)) && (paramRowSetMetaData.isNullable(i + 1) == 0)) {
        return false;
      }
    }
    return true;
  }
  
  public void initInsertRow()
  {
    for (int i = 0; i < cols; i++) {
      colsInserted.clear(i);
    }
  }
  
  public Object getColumnObject(int paramInt)
    throws SQLException
  {
    if (!colsInserted.get(paramInt - 1)) {
      throw new SQLException(resBundle.handleGetObject("insertrow.novalue").toString());
    }
    return origVals[(paramInt - 1)];
  }
  
  public void setColumnObject(int paramInt, Object paramObject)
  {
    origVals[(paramInt - 1)] = paramObject;
    markColInserted(paramInt - 1);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\internal\InsertRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */