package com.sun.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Hashtable;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.Predicate;

public class FilteredRowSetImpl
  extends WebRowSetImpl
  implements Serializable, Cloneable, FilteredRowSet
{
  private Predicate p;
  private boolean onInsertRow = false;
  static final long serialVersionUID = 6178454588413509360L;
  
  public FilteredRowSetImpl()
    throws SQLException
  {}
  
  public FilteredRowSetImpl(Hashtable paramHashtable)
    throws SQLException
  {
    super(paramHashtable);
  }
  
  public void setFilter(Predicate paramPredicate)
    throws SQLException
  {
    p = paramPredicate;
  }
  
  public Predicate getFilter()
  {
    return p;
  }
  
  protected boolean internalNext()
    throws SQLException
  {
    boolean bool = false;
    for (int i = getRow(); i <= size(); i++)
    {
      bool = super.internalNext();
      if ((!bool) || (p == null)) {
        return bool;
      }
      if (p.evaluate(this)) {
        break;
      }
    }
    return bool;
  }
  
  protected boolean internalPrevious()
    throws SQLException
  {
    boolean bool = false;
    for (int i = getRow(); i > 0; i--)
    {
      bool = super.internalPrevious();
      if (p == null) {
        return bool;
      }
      if (p.evaluate(this)) {
        break;
      }
    }
    return bool;
  }
  
  protected boolean internalFirst()
    throws SQLException
  {
    boolean bool = super.internalFirst();
    if (p == null) {
      return bool;
    }
    while ((bool) && (!p.evaluate(this))) {
      bool = super.internalNext();
    }
    return bool;
  }
  
  protected boolean internalLast()
    throws SQLException
  {
    boolean bool = super.internalLast();
    if (p == null) {
      return bool;
    }
    while ((bool) && (!p.evaluate(this))) {
      bool = super.internalPrevious();
    }
    return bool;
  }
  
  public boolean relative(int paramInt)
    throws SQLException
  {
    boolean bool2 = false;
    boolean bool3 = false;
    if (getType() == 1003) {
      throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.relative").toString());
    }
    int i;
    boolean bool1;
    if (paramInt > 0)
    {
      for (i = 0; i < paramInt; i++)
      {
        if (isAfterLast()) {
          return false;
        }
        bool2 = internalNext();
      }
      bool1 = bool2;
    }
    else
    {
      for (i = paramInt; i < 0; i++)
      {
        if (isBeforeFirst()) {
          return false;
        }
        bool3 = internalPrevious();
      }
      bool1 = bool3;
    }
    if (paramInt != 0) {
      notifyCursorMoved();
    }
    return bool1;
  }
  
  public boolean absolute(int paramInt)
    throws SQLException
  {
    boolean bool2 = false;
    if ((paramInt == 0) || (getType() == 1003)) {
      throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.absolute").toString());
    }
    int i;
    boolean bool1;
    if (paramInt > 0)
    {
      bool2 = internalFirst();
      for (i = 0; i < paramInt - 1; i++)
      {
        if (isAfterLast()) {
          return false;
        }
        bool2 = internalNext();
      }
      bool1 = bool2;
    }
    else
    {
      bool2 = internalLast();
      for (i = paramInt; i + 1 < 0; i++)
      {
        if (isBeforeFirst()) {
          return false;
        }
        bool2 = internalPrevious();
      }
      bool1 = bool2;
    }
    notifyCursorMoved();
    return bool1;
  }
  
  public void moveToInsertRow()
    throws SQLException
  {
    onInsertRow = true;
    super.moveToInsertRow();
  }
  
  public void updateInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(Integer.valueOf(paramInt2), paramInt1);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateInt(paramInt1, paramInt2);
  }
  
  public void updateInt(String paramString, int paramInt)
    throws SQLException
  {
    updateInt(findColumn(paramString), paramInt);
  }
  
  public void updateBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(Boolean.valueOf(paramBoolean), paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateBoolean(paramInt, paramBoolean);
  }
  
  public void updateBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    updateBoolean(findColumn(paramString), paramBoolean);
  }
  
  public void updateByte(int paramInt, byte paramByte)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(Byte.valueOf(paramByte), paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateByte(paramInt, paramByte);
  }
  
  public void updateByte(String paramString, byte paramByte)
    throws SQLException
  {
    updateByte(findColumn(paramString), paramByte);
  }
  
  public void updateShort(int paramInt, short paramShort)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(Short.valueOf(paramShort), paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateShort(paramInt, paramShort);
  }
  
  public void updateShort(String paramString, short paramShort)
    throws SQLException
  {
    updateShort(findColumn(paramString), paramShort);
  }
  
  public void updateLong(int paramInt, long paramLong)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(Long.valueOf(paramLong), paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateLong(paramInt, paramLong);
  }
  
  public void updateLong(String paramString, long paramLong)
    throws SQLException
  {
    updateLong(findColumn(paramString), paramLong);
  }
  
  public void updateFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(Float.valueOf(paramFloat), paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateFloat(paramInt, paramFloat);
  }
  
  public void updateFloat(String paramString, float paramFloat)
    throws SQLException
  {
    updateFloat(findColumn(paramString), paramFloat);
  }
  
  public void updateDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(Double.valueOf(paramDouble), paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateDouble(paramInt, paramDouble);
  }
  
  public void updateDouble(String paramString, double paramDouble)
    throws SQLException
  {
    updateDouble(findColumn(paramString), paramDouble);
  }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramBigDecimal, paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateBigDecimal(paramInt, paramBigDecimal);
  }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    updateBigDecimal(findColumn(paramString), paramBigDecimal);
  }
  
  public void updateString(int paramInt, String paramString)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramString, paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateString(paramInt, paramString);
  }
  
  public void updateString(String paramString1, String paramString2)
    throws SQLException
  {
    updateString(findColumn(paramString1), paramString2);
  }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    String str = "";
    Byte[] arrayOfByte = new Byte[paramArrayOfByte.length];
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      arrayOfByte[i] = Byte.valueOf(paramArrayOfByte[i]);
      str = str.concat(arrayOfByte[i].toString());
    }
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(str, paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateBytes(paramInt, paramArrayOfByte);
  }
  
  public void updateBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    updateBytes(findColumn(paramString), paramArrayOfByte);
  }
  
  public void updateDate(int paramInt, Date paramDate)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramDate, paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateDate(paramInt, paramDate);
  }
  
  public void updateDate(String paramString, Date paramDate)
    throws SQLException
  {
    updateDate(findColumn(paramString), paramDate);
  }
  
  public void updateTime(int paramInt, Time paramTime)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramTime, paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateTime(paramInt, paramTime);
  }
  
  public void updateTime(String paramString, Time paramTime)
    throws SQLException
  {
    updateTime(findColumn(paramString), paramTime);
  }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramTimestamp, paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateTimestamp(paramInt, paramTimestamp);
  }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    updateTimestamp(findColumn(paramString), paramTimestamp);
  }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramInputStream, paramInt1);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateAsciiStream(paramInt1, paramInputStream, paramInt2);
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    updateAsciiStream(findColumn(paramString), paramInputStream, paramInt);
  }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramReader, paramInt1);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateCharacterStream(paramInt1, paramReader, paramInt2);
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    updateCharacterStream(findColumn(paramString), paramReader, paramInt);
  }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramInputStream, paramInt1);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateBinaryStream(paramInt1, paramInputStream, paramInt2);
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    updateBinaryStream(findColumn(paramString), paramInputStream, paramInt);
  }
  
  public void updateObject(int paramInt, Object paramObject)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramObject, paramInt);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateObject(paramInt, paramObject);
  }
  
  public void updateObject(String paramString, Object paramObject)
    throws SQLException
  {
    updateObject(findColumn(paramString), paramObject);
  }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    if ((onInsertRow) && (p != null))
    {
      boolean bool = p.evaluate(paramObject, paramInt1);
      if (!bool) {
        throw new SQLException(resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
      }
    }
    super.updateObject(paramInt1, paramObject, paramInt2);
  }
  
  public void updateObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    updateObject(findColumn(paramString), paramObject, paramInt);
  }
  
  public void insertRow()
    throws SQLException
  {
    onInsertRow = false;
    super.insertRow();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\FilteredRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */