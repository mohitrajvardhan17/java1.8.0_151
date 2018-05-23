package javax.sql.rowset;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import javax.sql.RowSetMetaData;

public class RowSetMetaDataImpl
  implements RowSetMetaData, Serializable
{
  private int colCount;
  private ColInfo[] colInfo;
  static final long serialVersionUID = 6893806403181801867L;
  
  public RowSetMetaDataImpl() {}
  
  private void checkColRange(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > colCount)) {
      throw new SQLException("Invalid column index :" + paramInt);
    }
  }
  
  private void checkColType(int paramInt)
    throws SQLException
  {
    try
    {
      Class localClass = Types.class;
      Field[] arrayOfField = localClass.getFields();
      int i = 0;
      for (int j = 0; j < arrayOfField.length; j++)
      {
        i = arrayOfField[j].getInt(localClass);
        if (i == paramInt) {
          return;
        }
      }
    }
    catch (Exception localException)
    {
      throw new SQLException(localException.getMessage());
    }
    throw new SQLException("Invalid SQL type for column");
  }
  
  public void setColumnCount(int paramInt)
    throws SQLException
  {
    if (paramInt <= 0) {
      throw new SQLException("Invalid column count. Cannot be less or equal to zero");
    }
    colCount = paramInt;
    if (colCount != Integer.MAX_VALUE)
    {
      colInfo = new ColInfo[colCount + 1];
      for (int i = 1; i <= colCount; i++) {
        colInfo[i] = new ColInfo(null);
      }
    }
  }
  
  public void setAutoIncrement(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkColRange(paramInt);
    colInfo[paramInt].autoIncrement = paramBoolean;
  }
  
  public void setCaseSensitive(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkColRange(paramInt);
    colInfo[paramInt].caseSensitive = paramBoolean;
  }
  
  public void setSearchable(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkColRange(paramInt);
    colInfo[paramInt].searchable = paramBoolean;
  }
  
  public void setCurrency(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkColRange(paramInt);
    colInfo[paramInt].currency = paramBoolean;
  }
  
  public void setNullable(int paramInt1, int paramInt2)
    throws SQLException
  {
    if ((paramInt2 < 0) || (paramInt2 > 2)) {
      throw new SQLException("Invalid nullable constant set. Must be either columnNoNulls, columnNullable or columnNullableUnknown");
    }
    checkColRange(paramInt1);
    colInfo[paramInt1].nullable = paramInt2;
  }
  
  public void setSigned(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkColRange(paramInt);
    colInfo[paramInt].signed = paramBoolean;
  }
  
  public void setColumnDisplaySize(int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramInt2 < 0) {
      throw new SQLException("Invalid column display size. Cannot be less than zero");
    }
    checkColRange(paramInt1);
    colInfo[paramInt1].columnDisplaySize = paramInt2;
  }
  
  public void setColumnLabel(int paramInt, String paramString)
    throws SQLException
  {
    checkColRange(paramInt);
    if (paramString != null) {
      colInfo[paramInt].columnLabel = paramString;
    } else {
      colInfo[paramInt].columnLabel = "";
    }
  }
  
  public void setColumnName(int paramInt, String paramString)
    throws SQLException
  {
    checkColRange(paramInt);
    if (paramString != null) {
      colInfo[paramInt].columnName = paramString;
    } else {
      colInfo[paramInt].columnName = "";
    }
  }
  
  public void setSchemaName(int paramInt, String paramString)
    throws SQLException
  {
    checkColRange(paramInt);
    if (paramString != null) {
      colInfo[paramInt].schemaName = paramString;
    } else {
      colInfo[paramInt].schemaName = "";
    }
  }
  
  public void setPrecision(int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramInt2 < 0) {
      throw new SQLException("Invalid precision value. Cannot be less than zero");
    }
    checkColRange(paramInt1);
    colInfo[paramInt1].colPrecision = paramInt2;
  }
  
  public void setScale(int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramInt2 < 0) {
      throw new SQLException("Invalid scale size. Cannot be less than zero");
    }
    checkColRange(paramInt1);
    colInfo[paramInt1].colScale = paramInt2;
  }
  
  public void setTableName(int paramInt, String paramString)
    throws SQLException
  {
    checkColRange(paramInt);
    if (paramString != null) {
      colInfo[paramInt].tableName = paramString;
    } else {
      colInfo[paramInt].tableName = "";
    }
  }
  
  public void setCatalogName(int paramInt, String paramString)
    throws SQLException
  {
    checkColRange(paramInt);
    if (paramString != null) {
      colInfo[paramInt].catName = paramString;
    } else {
      colInfo[paramInt].catName = "";
    }
  }
  
  public void setColumnType(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkColType(paramInt2);
    checkColRange(paramInt1);
    colInfo[paramInt1].colType = paramInt2;
  }
  
  public void setColumnTypeName(int paramInt, String paramString)
    throws SQLException
  {
    checkColRange(paramInt);
    if (paramString != null) {
      colInfo[paramInt].colTypeName = paramString;
    } else {
      colInfo[paramInt].colTypeName = "";
    }
  }
  
  public int getColumnCount()
    throws SQLException
  {
    return colCount;
  }
  
  public boolean isAutoIncrement(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].autoIncrement;
  }
  
  public boolean isCaseSensitive(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].caseSensitive;
  }
  
  public boolean isSearchable(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].searchable;
  }
  
  public boolean isCurrency(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].currency;
  }
  
  public int isNullable(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].nullable;
  }
  
  public boolean isSigned(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].signed;
  }
  
  public int getColumnDisplaySize(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].columnDisplaySize;
  }
  
  public String getColumnLabel(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].columnLabel;
  }
  
  public String getColumnName(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].columnName;
  }
  
  public String getSchemaName(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    String str = "";
    if (colInfo[paramInt].schemaName != null) {
      str = colInfo[paramInt].schemaName;
    }
    return str;
  }
  
  public int getPrecision(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].colPrecision;
  }
  
  public int getScale(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].colScale;
  }
  
  public String getTableName(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].tableName;
  }
  
  public String getCatalogName(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    String str = "";
    if (colInfo[paramInt].catName != null) {
      str = colInfo[paramInt].catName;
    }
    return str;
  }
  
  public int getColumnType(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].colType;
  }
  
  public String getColumnTypeName(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].colTypeName;
  }
  
  public boolean isReadOnly(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].readOnly;
  }
  
  public boolean isWritable(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return colInfo[paramInt].writable;
  }
  
  public boolean isDefinitelyWritable(int paramInt)
    throws SQLException
  {
    checkColRange(paramInt);
    return true;
  }
  
  public String getColumnClassName(int paramInt)
    throws SQLException
  {
    String str = String.class.getName();
    int i = getColumnType(paramInt);
    switch (i)
    {
    case 2: 
    case 3: 
      str = BigDecimal.class.getName();
      break;
    case -7: 
      str = Boolean.class.getName();
      break;
    case -6: 
      str = Byte.class.getName();
      break;
    case 5: 
      str = Short.class.getName();
      break;
    case 4: 
      str = Integer.class.getName();
      break;
    case -5: 
      str = Long.class.getName();
      break;
    case 7: 
      str = Float.class.getName();
      break;
    case 6: 
    case 8: 
      str = Double.class.getName();
      break;
    case -4: 
    case -3: 
    case -2: 
      str = "byte[]";
      break;
    case 91: 
      str = Date.class.getName();
      break;
    case 92: 
      str = Time.class.getName();
      break;
    case 93: 
      str = Timestamp.class.getName();
      break;
    case 2004: 
      str = Blob.class.getName();
      break;
    case 2005: 
      str = Clob.class.getName();
    }
    return str;
  }
  
  public <T> T unwrap(Class<T> paramClass)
    throws SQLException
  {
    if (isWrapperFor(paramClass)) {
      return (T)paramClass.cast(this);
    }
    throw new SQLException("unwrap failed for:" + paramClass);
  }
  
  public boolean isWrapperFor(Class<?> paramClass)
    throws SQLException
  {
    return paramClass.isInstance(this);
  }
  
  private class ColInfo
    implements Serializable
  {
    public boolean autoIncrement;
    public boolean caseSensitive;
    public boolean currency;
    public int nullable;
    public boolean signed;
    public boolean searchable;
    public int columnDisplaySize;
    public String columnLabel;
    public String columnName;
    public String schemaName;
    public int colPrecision;
    public int colScale;
    public String tableName = "";
    public String catName;
    public int colType;
    public String colTypeName;
    public boolean readOnly = false;
    public boolean writable = true;
    static final long serialVersionUID = 5490834817919311283L;
    
    private ColInfo() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\RowSetMetaDataImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */