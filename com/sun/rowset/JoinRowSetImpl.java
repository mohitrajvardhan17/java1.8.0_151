package com.sun.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetListener;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.Joinable;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

public class JoinRowSetImpl
  extends WebRowSetImpl
  implements JoinRowSet
{
  private Vector<CachedRowSetImpl> vecRowSetsInJOIN = new Vector();
  private CachedRowSetImpl crsInternal = new CachedRowSetImpl();
  private Vector<Integer> vecJoinType = new Vector();
  private Vector<String> vecTableNames = new Vector();
  private int iMatchKey = -1;
  private String strMatchKey = null;
  boolean[] supportedJOINs = { false, true, false, false, false };
  private WebRowSet wrs;
  static final long serialVersionUID = -5590501621560008453L;
  
  public JoinRowSetImpl()
    throws SQLException
  {
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public void addRowSet(Joinable paramJoinable)
    throws SQLException
  {
    int i = 0;
    int j = 0;
    if (!(paramJoinable instanceof RowSet)) {
      throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.notinstance").toString());
    }
    CachedRowSetImpl localCachedRowSetImpl;
    int i1;
    if ((paramJoinable instanceof JdbcRowSetImpl))
    {
      localCachedRowSetImpl = new CachedRowSetImpl();
      localCachedRowSetImpl.populate((RowSet)paramJoinable);
      if (localCachedRowSetImpl.size() == 0) {
        throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.emptyrowset").toString());
      }
      try
      {
        int k = 0;
        for (int m = 0; (m < paramJoinable.getMatchColumnIndexes().length) && (paramJoinable.getMatchColumnIndexes()[m] != -1); m++) {
          k++;
        }
        int[] arrayOfInt1 = new int[k];
        for (i1 = 0; i1 < k; i1++) {
          arrayOfInt1[i1] = paramJoinable.getMatchColumnIndexes()[i1];
        }
        localCachedRowSetImpl.setMatchColumn(arrayOfInt1);
      }
      catch (SQLException localSQLException1) {}
    }
    else
    {
      localCachedRowSetImpl = (CachedRowSetImpl)paramJoinable;
      if (localCachedRowSetImpl.size() == 0) {
        throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.emptyrowset").toString());
      }
    }
    try
    {
      iMatchKey = localCachedRowSetImpl.getMatchColumnIndexes()[0];
    }
    catch (SQLException localSQLException2)
    {
      i = 1;
    }
    try
    {
      strMatchKey = localCachedRowSetImpl.getMatchColumnNames()[0];
    }
    catch (SQLException localSQLException3)
    {
      j = 1;
    }
    if ((i != 0) && (j != 0)) {
      throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.matchnotset").toString());
    }
    if (i != 0)
    {
      ArrayList localArrayList = new ArrayList();
      for (int n = 0; (n < localCachedRowSetImpl.getMatchColumnNames().length) && ((strMatchKey = localCachedRowSetImpl.getMatchColumnNames()[n]) != null); n++)
      {
        iMatchKey = localCachedRowSetImpl.findColumn(strMatchKey);
        localArrayList.add(Integer.valueOf(iMatchKey));
      }
      int[] arrayOfInt2 = new int[localArrayList.size()];
      for (i1 = 0; i1 < localArrayList.size(); i1++) {
        arrayOfInt2[i1] = ((Integer)localArrayList.get(i1)).intValue();
      }
      localCachedRowSetImpl.setMatchColumn(arrayOfInt2);
    }
    initJOIN(localCachedRowSetImpl);
  }
  
  public void addRowSet(RowSet paramRowSet, int paramInt)
    throws SQLException
  {
    ((CachedRowSetImpl)paramRowSet).setMatchColumn(paramInt);
    addRowSet((Joinable)paramRowSet);
  }
  
  public void addRowSet(RowSet paramRowSet, String paramString)
    throws SQLException
  {
    ((CachedRowSetImpl)paramRowSet).setMatchColumn(paramString);
    addRowSet((Joinable)paramRowSet);
  }
  
  public void addRowSet(RowSet[] paramArrayOfRowSet, int[] paramArrayOfInt)
    throws SQLException
  {
    if (paramArrayOfRowSet.length != paramArrayOfInt.length) {
      throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.numnotequal").toString());
    }
    for (int i = 0; i < paramArrayOfRowSet.length; i++)
    {
      ((CachedRowSetImpl)paramArrayOfRowSet[i]).setMatchColumn(paramArrayOfInt[i]);
      addRowSet((Joinable)paramArrayOfRowSet[i]);
    }
  }
  
  public void addRowSet(RowSet[] paramArrayOfRowSet, String[] paramArrayOfString)
    throws SQLException
  {
    if (paramArrayOfRowSet.length != paramArrayOfString.length) {
      throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.numnotequal").toString());
    }
    for (int i = 0; i < paramArrayOfRowSet.length; i++)
    {
      ((CachedRowSetImpl)paramArrayOfRowSet[i]).setMatchColumn(paramArrayOfString[i]);
      addRowSet((Joinable)paramArrayOfRowSet[i]);
    }
  }
  
  public Collection getRowSets()
    throws SQLException
  {
    return vecRowSetsInJOIN;
  }
  
  public String[] getRowSetNames()
    throws SQLException
  {
    Object[] arrayOfObject = vecTableNames.toArray();
    String[] arrayOfString = new String[arrayOfObject.length];
    for (int i = 0; i < arrayOfObject.length; i++) {
      arrayOfString[i] = arrayOfObject[i].toString();
    }
    return arrayOfString;
  }
  
  public CachedRowSet toCachedRowSet()
    throws SQLException
  {
    return crsInternal;
  }
  
  public boolean supportsCrossJoin()
  {
    return supportedJOINs[0];
  }
  
  public boolean supportsInnerJoin()
  {
    return supportedJOINs[1];
  }
  
  public boolean supportsLeftOuterJoin()
  {
    return supportedJOINs[2];
  }
  
  public boolean supportsRightOuterJoin()
  {
    return supportedJOINs[3];
  }
  
  public boolean supportsFullJoin()
  {
    return supportedJOINs[4];
  }
  
  public void setJoinType(int paramInt)
    throws SQLException
  {
    if ((paramInt >= 0) && (paramInt <= 4))
    {
      if (paramInt != 1) {
        throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.notsupported").toString());
      }
      Integer localInteger = Integer.valueOf(1);
      vecJoinType.add(localInteger);
    }
    else
    {
      throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.notdefined").toString());
    }
  }
  
  private boolean checkforMatchColumn(Joinable paramJoinable)
    throws SQLException
  {
    int[] arrayOfInt = paramJoinable.getMatchColumnIndexes();
    return arrayOfInt.length > 0;
  }
  
  private void initJOIN(CachedRowSet paramCachedRowSet)
    throws SQLException
  {
    try
    {
      CachedRowSetImpl localCachedRowSetImpl1 = (CachedRowSetImpl)paramCachedRowSet;
      CachedRowSetImpl localCachedRowSetImpl2 = new CachedRowSetImpl();
      RowSetMetaDataImpl localRowSetMetaDataImpl = new RowSetMetaDataImpl();
      if (vecRowSetsInJOIN.isEmpty())
      {
        crsInternal = ((CachedRowSetImpl)paramCachedRowSet.createCopy());
        crsInternal.setMetaData((RowSetMetaDataImpl)localCachedRowSetImpl1.getMetaData());
        vecRowSetsInJOIN.add(localCachedRowSetImpl1);
      }
      else
      {
        if (vecRowSetsInJOIN.size() - vecJoinType.size() == 2) {
          setJoinType(1);
        } else if (vecRowSetsInJOIN.size() - vecJoinType.size() != 1) {}
        vecTableNames.add(crsInternal.getTableName());
        vecTableNames.add(localCachedRowSetImpl1.getTableName());
        int i = localCachedRowSetImpl1.size();
        int j = crsInternal.size();
        int k = 0;
        for (int m = 0; (m < crsInternal.getMatchColumnIndexes().length) && (crsInternal.getMatchColumnIndexes()[m] != -1); m++) {
          k++;
        }
        localRowSetMetaDataImpl.setColumnCount(crsInternal.getMetaData().getColumnCount() + localCachedRowSetImpl1.getMetaData().getColumnCount() - k);
        localCachedRowSetImpl2.setMetaData(localRowSetMetaDataImpl);
        crsInternal.beforeFirst();
        localCachedRowSetImpl1.beforeFirst();
        for (m = 1; (m <= j) && (!crsInternal.isAfterLast()); m++) {
          if (crsInternal.next())
          {
            localCachedRowSetImpl1.beforeFirst();
            for (n = 1; (n <= i) && (!localCachedRowSetImpl1.isAfterLast()); n++) {
              if (localCachedRowSetImpl1.next())
              {
                int i1 = 1;
                for (int i2 = 0; i2 < k; i2++) {
                  if (!crsInternal.getObject(crsInternal.getMatchColumnIndexes()[i2]).equals(localCachedRowSetImpl1.getObject(localCachedRowSetImpl1.getMatchColumnIndexes()[i2])))
                  {
                    i1 = 0;
                    break;
                  }
                }
                if (i1 != 0)
                {
                  int i3 = 0;
                  localCachedRowSetImpl2.moveToInsertRow();
                  for (i2 = 1; i2 <= crsInternal.getMetaData().getColumnCount(); i2++)
                  {
                    i1 = 0;
                    for (i4 = 0; i4 < k; i4++) {
                      if (i2 == crsInternal.getMatchColumnIndexes()[i4])
                      {
                        i1 = 1;
                        break;
                      }
                    }
                    if (i1 == 0)
                    {
                      localCachedRowSetImpl2.updateObject(++i3, crsInternal.getObject(i2));
                      localRowSetMetaDataImpl.setColumnName(i3, crsInternal.getMetaData().getColumnName(i2));
                      localRowSetMetaDataImpl.setTableName(i3, crsInternal.getTableName());
                      localRowSetMetaDataImpl.setColumnType(i2, crsInternal.getMetaData().getColumnType(i2));
                      localRowSetMetaDataImpl.setAutoIncrement(i2, crsInternal.getMetaData().isAutoIncrement(i2));
                      localRowSetMetaDataImpl.setCaseSensitive(i2, crsInternal.getMetaData().isCaseSensitive(i2));
                      localRowSetMetaDataImpl.setCatalogName(i2, crsInternal.getMetaData().getCatalogName(i2));
                      localRowSetMetaDataImpl.setColumnDisplaySize(i2, crsInternal.getMetaData().getColumnDisplaySize(i2));
                      localRowSetMetaDataImpl.setColumnLabel(i2, crsInternal.getMetaData().getColumnLabel(i2));
                      localRowSetMetaDataImpl.setColumnType(i2, crsInternal.getMetaData().getColumnType(i2));
                      localRowSetMetaDataImpl.setColumnTypeName(i2, crsInternal.getMetaData().getColumnTypeName(i2));
                      localRowSetMetaDataImpl.setCurrency(i2, crsInternal.getMetaData().isCurrency(i2));
                      localRowSetMetaDataImpl.setNullable(i2, crsInternal.getMetaData().isNullable(i2));
                      localRowSetMetaDataImpl.setPrecision(i2, crsInternal.getMetaData().getPrecision(i2));
                      localRowSetMetaDataImpl.setScale(i2, crsInternal.getMetaData().getScale(i2));
                      localRowSetMetaDataImpl.setSchemaName(i2, crsInternal.getMetaData().getSchemaName(i2));
                      localRowSetMetaDataImpl.setSearchable(i2, crsInternal.getMetaData().isSearchable(i2));
                      localRowSetMetaDataImpl.setSigned(i2, crsInternal.getMetaData().isSigned(i2));
                    }
                    else
                    {
                      localCachedRowSetImpl2.updateObject(++i3, crsInternal.getObject(i2));
                      localRowSetMetaDataImpl.setColumnName(i3, crsInternal.getMetaData().getColumnName(i2));
                      localRowSetMetaDataImpl.setTableName(i3, crsInternal.getTableName() + "#" + localCachedRowSetImpl1.getTableName());
                      localRowSetMetaDataImpl.setColumnType(i2, crsInternal.getMetaData().getColumnType(i2));
                      localRowSetMetaDataImpl.setAutoIncrement(i2, crsInternal.getMetaData().isAutoIncrement(i2));
                      localRowSetMetaDataImpl.setCaseSensitive(i2, crsInternal.getMetaData().isCaseSensitive(i2));
                      localRowSetMetaDataImpl.setCatalogName(i2, crsInternal.getMetaData().getCatalogName(i2));
                      localRowSetMetaDataImpl.setColumnDisplaySize(i2, crsInternal.getMetaData().getColumnDisplaySize(i2));
                      localRowSetMetaDataImpl.setColumnLabel(i2, crsInternal.getMetaData().getColumnLabel(i2));
                      localRowSetMetaDataImpl.setColumnType(i2, crsInternal.getMetaData().getColumnType(i2));
                      localRowSetMetaDataImpl.setColumnTypeName(i2, crsInternal.getMetaData().getColumnTypeName(i2));
                      localRowSetMetaDataImpl.setCurrency(i2, crsInternal.getMetaData().isCurrency(i2));
                      localRowSetMetaDataImpl.setNullable(i2, crsInternal.getMetaData().isNullable(i2));
                      localRowSetMetaDataImpl.setPrecision(i2, crsInternal.getMetaData().getPrecision(i2));
                      localRowSetMetaDataImpl.setScale(i2, crsInternal.getMetaData().getScale(i2));
                      localRowSetMetaDataImpl.setSchemaName(i2, crsInternal.getMetaData().getSchemaName(i2));
                      localRowSetMetaDataImpl.setSearchable(i2, crsInternal.getMetaData().isSearchable(i2));
                      localRowSetMetaDataImpl.setSigned(i2, crsInternal.getMetaData().isSigned(i2));
                    }
                  }
                  for (int i4 = 1; i4 <= localCachedRowSetImpl1.getMetaData().getColumnCount(); i4++)
                  {
                    i1 = 0;
                    for (int i5 = 0; i5 < k; i5++) {
                      if (i4 == localCachedRowSetImpl1.getMatchColumnIndexes()[i5])
                      {
                        i1 = 1;
                        break;
                      }
                    }
                    if (i1 == 0)
                    {
                      localCachedRowSetImpl2.updateObject(++i3, localCachedRowSetImpl1.getObject(i4));
                      localRowSetMetaDataImpl.setColumnName(i3, localCachedRowSetImpl1.getMetaData().getColumnName(i4));
                      localRowSetMetaDataImpl.setTableName(i3, localCachedRowSetImpl1.getTableName());
                      localRowSetMetaDataImpl.setColumnType(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getColumnType(i4));
                      localRowSetMetaDataImpl.setAutoIncrement(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().isAutoIncrement(i4));
                      localRowSetMetaDataImpl.setCaseSensitive(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().isCaseSensitive(i4));
                      localRowSetMetaDataImpl.setCatalogName(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getCatalogName(i4));
                      localRowSetMetaDataImpl.setColumnDisplaySize(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getColumnDisplaySize(i4));
                      localRowSetMetaDataImpl.setColumnLabel(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getColumnLabel(i4));
                      localRowSetMetaDataImpl.setColumnType(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getColumnType(i4));
                      localRowSetMetaDataImpl.setColumnTypeName(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getColumnTypeName(i4));
                      localRowSetMetaDataImpl.setCurrency(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().isCurrency(i4));
                      localRowSetMetaDataImpl.setNullable(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().isNullable(i4));
                      localRowSetMetaDataImpl.setPrecision(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getPrecision(i4));
                      localRowSetMetaDataImpl.setScale(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getScale(i4));
                      localRowSetMetaDataImpl.setSchemaName(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().getSchemaName(i4));
                      localRowSetMetaDataImpl.setSearchable(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().isSearchable(i4));
                      localRowSetMetaDataImpl.setSigned(i2 + i4 - 1, localCachedRowSetImpl1.getMetaData().isSigned(i4));
                    }
                    else
                    {
                      i2--;
                    }
                  }
                  localCachedRowSetImpl2.insertRow();
                  localCachedRowSetImpl2.moveToCurrentRow();
                }
              }
            }
          }
        }
        localCachedRowSetImpl2.setMetaData(localRowSetMetaDataImpl);
        localCachedRowSetImpl2.setOriginal();
        int[] arrayOfInt = new int[k];
        for (int n = 0; n < k; n++) {
          arrayOfInt[n] = crsInternal.getMatchColumnIndexes()[n];
        }
        crsInternal = ((CachedRowSetImpl)localCachedRowSetImpl2.createCopy());
        crsInternal.setMatchColumn(arrayOfInt);
        crsInternal.setMetaData(localRowSetMetaDataImpl);
        vecRowSetsInJOIN.add(localCachedRowSetImpl1);
      }
    }
    catch (SQLException localSQLException)
    {
      localSQLException.printStackTrace();
      throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.initerror").toString() + localSQLException);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      throw new SQLException(resBundle.handleGetObject("joinrowsetimpl.genericerr").toString() + localException);
    }
  }
  
  public String getWhereClause()
    throws SQLException
  {
    String str1 = "Select ";
    String str2 = "";
    String str3 = "";
    int i = vecRowSetsInJOIN.size();
    for (int m = 0; m < i; m++)
    {
      CachedRowSetImpl localCachedRowSetImpl = (CachedRowSetImpl)vecRowSetsInJOIN.get(m);
      int j = localCachedRowSetImpl.getMetaData().getColumnCount();
      str2 = str2.concat(localCachedRowSetImpl.getTableName());
      str3 = str3.concat(str2 + ", ");
      int k = 1;
      while (k < j)
      {
        str1 = str1.concat(str2 + "." + localCachedRowSetImpl.getMetaData().getColumnName(k++));
        str1 = str1.concat(", ");
      }
    }
    str1 = str1.substring(0, str1.lastIndexOf(","));
    str1 = str1.concat(" from ");
    str1 = str1.concat(str3);
    str1 = str1.substring(0, str1.lastIndexOf(","));
    str1 = str1.concat(" where ");
    for (m = 0; m < i; m++)
    {
      str1 = str1.concat(((CachedRowSetImpl)vecRowSetsInJOIN.get(m)).getMatchColumnNames()[0]);
      if (m % 2 != 0) {
        str1 = str1.concat("=");
      } else {
        str1 = str1.concat(" and");
      }
      str1 = str1.concat(" ");
    }
    return str1;
  }
  
  public boolean next()
    throws SQLException
  {
    return crsInternal.next();
  }
  
  public void close()
    throws SQLException
  {
    crsInternal.close();
  }
  
  public boolean wasNull()
    throws SQLException
  {
    return crsInternal.wasNull();
  }
  
  public String getString(int paramInt)
    throws SQLException
  {
    return crsInternal.getString(paramInt);
  }
  
  public boolean getBoolean(int paramInt)
    throws SQLException
  {
    return crsInternal.getBoolean(paramInt);
  }
  
  public byte getByte(int paramInt)
    throws SQLException
  {
    return crsInternal.getByte(paramInt);
  }
  
  public short getShort(int paramInt)
    throws SQLException
  {
    return crsInternal.getShort(paramInt);
  }
  
  public int getInt(int paramInt)
    throws SQLException
  {
    return crsInternal.getInt(paramInt);
  }
  
  public long getLong(int paramInt)
    throws SQLException
  {
    return crsInternal.getLong(paramInt);
  }
  
  public float getFloat(int paramInt)
    throws SQLException
  {
    return crsInternal.getFloat(paramInt);
  }
  
  public double getDouble(int paramInt)
    throws SQLException
  {
    return crsInternal.getDouble(paramInt);
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    return crsInternal.getBigDecimal(paramInt1);
  }
  
  public byte[] getBytes(int paramInt)
    throws SQLException
  {
    return crsInternal.getBytes(paramInt);
  }
  
  public Date getDate(int paramInt)
    throws SQLException
  {
    return crsInternal.getDate(paramInt);
  }
  
  public Time getTime(int paramInt)
    throws SQLException
  {
    return crsInternal.getTime(paramInt);
  }
  
  public Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    return crsInternal.getTimestamp(paramInt);
  }
  
  public InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    return crsInternal.getAsciiStream(paramInt);
  }
  
  @Deprecated
  public InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    return crsInternal.getUnicodeStream(paramInt);
  }
  
  public InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    return crsInternal.getBinaryStream(paramInt);
  }
  
  public String getString(String paramString)
    throws SQLException
  {
    return crsInternal.getString(paramString);
  }
  
  public boolean getBoolean(String paramString)
    throws SQLException
  {
    return crsInternal.getBoolean(paramString);
  }
  
  public byte getByte(String paramString)
    throws SQLException
  {
    return crsInternal.getByte(paramString);
  }
  
  public short getShort(String paramString)
    throws SQLException
  {
    return crsInternal.getShort(paramString);
  }
  
  public int getInt(String paramString)
    throws SQLException
  {
    return crsInternal.getInt(paramString);
  }
  
  public long getLong(String paramString)
    throws SQLException
  {
    return crsInternal.getLong(paramString);
  }
  
  public float getFloat(String paramString)
    throws SQLException
  {
    return crsInternal.getFloat(paramString);
  }
  
  public double getDouble(String paramString)
    throws SQLException
  {
    return crsInternal.getDouble(paramString);
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(String paramString, int paramInt)
    throws SQLException
  {
    return crsInternal.getBigDecimal(paramString);
  }
  
  public byte[] getBytes(String paramString)
    throws SQLException
  {
    return crsInternal.getBytes(paramString);
  }
  
  public Date getDate(String paramString)
    throws SQLException
  {
    return crsInternal.getDate(paramString);
  }
  
  public Time getTime(String paramString)
    throws SQLException
  {
    return crsInternal.getTime(paramString);
  }
  
  public Timestamp getTimestamp(String paramString)
    throws SQLException
  {
    return crsInternal.getTimestamp(paramString);
  }
  
  public InputStream getAsciiStream(String paramString)
    throws SQLException
  {
    return crsInternal.getAsciiStream(paramString);
  }
  
  @Deprecated
  public InputStream getUnicodeStream(String paramString)
    throws SQLException
  {
    return crsInternal.getUnicodeStream(paramString);
  }
  
  public InputStream getBinaryStream(String paramString)
    throws SQLException
  {
    return crsInternal.getBinaryStream(paramString);
  }
  
  public SQLWarning getWarnings()
  {
    return crsInternal.getWarnings();
  }
  
  public void clearWarnings()
  {
    crsInternal.clearWarnings();
  }
  
  public String getCursorName()
    throws SQLException
  {
    return crsInternal.getCursorName();
  }
  
  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    return crsInternal.getMetaData();
  }
  
  public Object getObject(int paramInt)
    throws SQLException
  {
    return crsInternal.getObject(paramInt);
  }
  
  public Object getObject(int paramInt, Map<String, Class<?>> paramMap)
    throws SQLException
  {
    return crsInternal.getObject(paramInt, paramMap);
  }
  
  public Object getObject(String paramString)
    throws SQLException
  {
    return crsInternal.getObject(paramString);
  }
  
  public Object getObject(String paramString, Map<String, Class<?>> paramMap)
    throws SQLException
  {
    return crsInternal.getObject(paramString, paramMap);
  }
  
  public Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    return crsInternal.getCharacterStream(paramInt);
  }
  
  public Reader getCharacterStream(String paramString)
    throws SQLException
  {
    return crsInternal.getCharacterStream(paramString);
  }
  
  public BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    return crsInternal.getBigDecimal(paramInt);
  }
  
  public BigDecimal getBigDecimal(String paramString)
    throws SQLException
  {
    return crsInternal.getBigDecimal(paramString);
  }
  
  public int size()
  {
    return crsInternal.size();
  }
  
  public boolean isBeforeFirst()
    throws SQLException
  {
    return crsInternal.isBeforeFirst();
  }
  
  public boolean isAfterLast()
    throws SQLException
  {
    return crsInternal.isAfterLast();
  }
  
  public boolean isFirst()
    throws SQLException
  {
    return crsInternal.isFirst();
  }
  
  public boolean isLast()
    throws SQLException
  {
    return crsInternal.isLast();
  }
  
  public void beforeFirst()
    throws SQLException
  {
    crsInternal.beforeFirst();
  }
  
  public void afterLast()
    throws SQLException
  {
    crsInternal.afterLast();
  }
  
  public boolean first()
    throws SQLException
  {
    return crsInternal.first();
  }
  
  public boolean last()
    throws SQLException
  {
    return crsInternal.last();
  }
  
  public int getRow()
    throws SQLException
  {
    return crsInternal.getRow();
  }
  
  public boolean absolute(int paramInt)
    throws SQLException
  {
    return crsInternal.absolute(paramInt);
  }
  
  public boolean relative(int paramInt)
    throws SQLException
  {
    return crsInternal.relative(paramInt);
  }
  
  public boolean previous()
    throws SQLException
  {
    return crsInternal.previous();
  }
  
  public int findColumn(String paramString)
    throws SQLException
  {
    return crsInternal.findColumn(paramString);
  }
  
  public boolean rowUpdated()
    throws SQLException
  {
    return crsInternal.rowUpdated();
  }
  
  public boolean columnUpdated(int paramInt)
    throws SQLException
  {
    return crsInternal.columnUpdated(paramInt);
  }
  
  public boolean rowInserted()
    throws SQLException
  {
    return crsInternal.rowInserted();
  }
  
  public boolean rowDeleted()
    throws SQLException
  {
    return crsInternal.rowDeleted();
  }
  
  public void updateNull(int paramInt)
    throws SQLException
  {
    crsInternal.updateNull(paramInt);
  }
  
  public void updateBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    crsInternal.updateBoolean(paramInt, paramBoolean);
  }
  
  public void updateByte(int paramInt, byte paramByte)
    throws SQLException
  {
    crsInternal.updateByte(paramInt, paramByte);
  }
  
  public void updateShort(int paramInt, short paramShort)
    throws SQLException
  {
    crsInternal.updateShort(paramInt, paramShort);
  }
  
  public void updateInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    crsInternal.updateInt(paramInt1, paramInt2);
  }
  
  public void updateLong(int paramInt, long paramLong)
    throws SQLException
  {
    crsInternal.updateLong(paramInt, paramLong);
  }
  
  public void updateFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    crsInternal.updateFloat(paramInt, paramFloat);
  }
  
  public void updateDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    crsInternal.updateDouble(paramInt, paramDouble);
  }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    crsInternal.updateBigDecimal(paramInt, paramBigDecimal);
  }
  
  public void updateString(int paramInt, String paramString)
    throws SQLException
  {
    crsInternal.updateString(paramInt, paramString);
  }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    crsInternal.updateBytes(paramInt, paramArrayOfByte);
  }
  
  public void updateDate(int paramInt, Date paramDate)
    throws SQLException
  {
    crsInternal.updateDate(paramInt, paramDate);
  }
  
  public void updateTime(int paramInt, Time paramTime)
    throws SQLException
  {
    crsInternal.updateTime(paramInt, paramTime);
  }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    crsInternal.updateTimestamp(paramInt, paramTimestamp);
  }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    crsInternal.updateAsciiStream(paramInt1, paramInputStream, paramInt2);
  }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    crsInternal.updateBinaryStream(paramInt1, paramInputStream, paramInt2);
  }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    crsInternal.updateCharacterStream(paramInt1, paramReader, paramInt2);
  }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    crsInternal.updateObject(paramInt1, paramObject, paramInt2);
  }
  
  public void updateObject(int paramInt, Object paramObject)
    throws SQLException
  {
    crsInternal.updateObject(paramInt, paramObject);
  }
  
  public void updateNull(String paramString)
    throws SQLException
  {
    crsInternal.updateNull(paramString);
  }
  
  public void updateBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    crsInternal.updateBoolean(paramString, paramBoolean);
  }
  
  public void updateByte(String paramString, byte paramByte)
    throws SQLException
  {
    crsInternal.updateByte(paramString, paramByte);
  }
  
  public void updateShort(String paramString, short paramShort)
    throws SQLException
  {
    crsInternal.updateShort(paramString, paramShort);
  }
  
  public void updateInt(String paramString, int paramInt)
    throws SQLException
  {
    crsInternal.updateInt(paramString, paramInt);
  }
  
  public void updateLong(String paramString, long paramLong)
    throws SQLException
  {
    crsInternal.updateLong(paramString, paramLong);
  }
  
  public void updateFloat(String paramString, float paramFloat)
    throws SQLException
  {
    crsInternal.updateFloat(paramString, paramFloat);
  }
  
  public void updateDouble(String paramString, double paramDouble)
    throws SQLException
  {
    crsInternal.updateDouble(paramString, paramDouble);
  }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    crsInternal.updateBigDecimal(paramString, paramBigDecimal);
  }
  
  public void updateString(String paramString1, String paramString2)
    throws SQLException
  {
    crsInternal.updateString(paramString1, paramString2);
  }
  
  public void updateBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    crsInternal.updateBytes(paramString, paramArrayOfByte);
  }
  
  public void updateDate(String paramString, Date paramDate)
    throws SQLException
  {
    crsInternal.updateDate(paramString, paramDate);
  }
  
  public void updateTime(String paramString, Time paramTime)
    throws SQLException
  {
    crsInternal.updateTime(paramString, paramTime);
  }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    crsInternal.updateTimestamp(paramString, paramTimestamp);
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    crsInternal.updateAsciiStream(paramString, paramInputStream, paramInt);
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    crsInternal.updateBinaryStream(paramString, paramInputStream, paramInt);
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    crsInternal.updateCharacterStream(paramString, paramReader, paramInt);
  }
  
  public void updateObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    crsInternal.updateObject(paramString, paramObject, paramInt);
  }
  
  public void updateObject(String paramString, Object paramObject)
    throws SQLException
  {
    crsInternal.updateObject(paramString, paramObject);
  }
  
  public void insertRow()
    throws SQLException
  {
    crsInternal.insertRow();
  }
  
  public void updateRow()
    throws SQLException
  {
    crsInternal.updateRow();
  }
  
  public void deleteRow()
    throws SQLException
  {
    crsInternal.deleteRow();
  }
  
  public void refreshRow()
    throws SQLException
  {
    crsInternal.refreshRow();
  }
  
  public void cancelRowUpdates()
    throws SQLException
  {
    crsInternal.cancelRowUpdates();
  }
  
  public void moveToInsertRow()
    throws SQLException
  {
    crsInternal.moveToInsertRow();
  }
  
  public void moveToCurrentRow()
    throws SQLException
  {
    crsInternal.moveToCurrentRow();
  }
  
  public Statement getStatement()
    throws SQLException
  {
    return crsInternal.getStatement();
  }
  
  public Ref getRef(int paramInt)
    throws SQLException
  {
    return crsInternal.getRef(paramInt);
  }
  
  public Blob getBlob(int paramInt)
    throws SQLException
  {
    return crsInternal.getBlob(paramInt);
  }
  
  public Clob getClob(int paramInt)
    throws SQLException
  {
    return crsInternal.getClob(paramInt);
  }
  
  public Array getArray(int paramInt)
    throws SQLException
  {
    return crsInternal.getArray(paramInt);
  }
  
  public Ref getRef(String paramString)
    throws SQLException
  {
    return crsInternal.getRef(paramString);
  }
  
  public Blob getBlob(String paramString)
    throws SQLException
  {
    return crsInternal.getBlob(paramString);
  }
  
  public Clob getClob(String paramString)
    throws SQLException
  {
    return crsInternal.getClob(paramString);
  }
  
  public Array getArray(String paramString)
    throws SQLException
  {
    return crsInternal.getArray(paramString);
  }
  
  public Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return crsInternal.getDate(paramInt, paramCalendar);
  }
  
  public Date getDate(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return crsInternal.getDate(paramString, paramCalendar);
  }
  
  public Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return crsInternal.getTime(paramInt, paramCalendar);
  }
  
  public Time getTime(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return crsInternal.getTime(paramString, paramCalendar);
  }
  
  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return crsInternal.getTimestamp(paramInt, paramCalendar);
  }
  
  public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return crsInternal.getTimestamp(paramString, paramCalendar);
  }
  
  public void setMetaData(RowSetMetaData paramRowSetMetaData)
    throws SQLException
  {
    crsInternal.setMetaData(paramRowSetMetaData);
  }
  
  public ResultSet getOriginal()
    throws SQLException
  {
    return crsInternal.getOriginal();
  }
  
  public ResultSet getOriginalRow()
    throws SQLException
  {
    return crsInternal.getOriginalRow();
  }
  
  public void setOriginalRow()
    throws SQLException
  {
    crsInternal.setOriginalRow();
  }
  
  public int[] getKeyColumns()
    throws SQLException
  {
    return crsInternal.getKeyColumns();
  }
  
  public void setKeyColumns(int[] paramArrayOfInt)
    throws SQLException
  {
    crsInternal.setKeyColumns(paramArrayOfInt);
  }
  
  public void updateRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    crsInternal.updateRef(paramInt, paramRef);
  }
  
  public void updateRef(String paramString, Ref paramRef)
    throws SQLException
  {
    crsInternal.updateRef(paramString, paramRef);
  }
  
  public void updateClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    crsInternal.updateClob(paramInt, paramClob);
  }
  
  public void updateClob(String paramString, Clob paramClob)
    throws SQLException
  {
    crsInternal.updateClob(paramString, paramClob);
  }
  
  public void updateBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    crsInternal.updateBlob(paramInt, paramBlob);
  }
  
  public void updateBlob(String paramString, Blob paramBlob)
    throws SQLException
  {
    crsInternal.updateBlob(paramString, paramBlob);
  }
  
  public void updateArray(int paramInt, Array paramArray)
    throws SQLException
  {
    crsInternal.updateArray(paramInt, paramArray);
  }
  
  public void updateArray(String paramString, Array paramArray)
    throws SQLException
  {
    crsInternal.updateArray(paramString, paramArray);
  }
  
  public void execute()
    throws SQLException
  {
    crsInternal.execute();
  }
  
  public void execute(Connection paramConnection)
    throws SQLException
  {
    crsInternal.execute(paramConnection);
  }
  
  public URL getURL(int paramInt)
    throws SQLException
  {
    return crsInternal.getURL(paramInt);
  }
  
  public URL getURL(String paramString)
    throws SQLException
  {
    return crsInternal.getURL(paramString);
  }
  
  public void writeXml(ResultSet paramResultSet, Writer paramWriter)
    throws SQLException
  {
    wrs = new WebRowSetImpl();
    wrs.populate(paramResultSet);
    wrs.writeXml(paramWriter);
  }
  
  public void writeXml(Writer paramWriter)
    throws SQLException
  {
    createWebRowSet().writeXml(paramWriter);
  }
  
  public void readXml(Reader paramReader)
    throws SQLException
  {
    wrs = new WebRowSetImpl();
    wrs.readXml(paramReader);
    crsInternal = ((CachedRowSetImpl)wrs);
  }
  
  public void readXml(InputStream paramInputStream)
    throws SQLException, IOException
  {
    wrs = new WebRowSetImpl();
    wrs.readXml(paramInputStream);
    crsInternal = ((CachedRowSetImpl)wrs);
  }
  
  public void writeXml(OutputStream paramOutputStream)
    throws SQLException, IOException
  {
    createWebRowSet().writeXml(paramOutputStream);
  }
  
  public void writeXml(ResultSet paramResultSet, OutputStream paramOutputStream)
    throws SQLException, IOException
  {
    wrs = new WebRowSetImpl();
    wrs.populate(paramResultSet);
    wrs.writeXml(paramOutputStream);
  }
  
  private WebRowSet createWebRowSet()
    throws SQLException
  {
    if (wrs != null) {
      return wrs;
    }
    wrs = new WebRowSetImpl();
    crsInternal.beforeFirst();
    wrs.populate(crsInternal);
    return wrs;
  }
  
  public int getJoinType()
    throws SQLException
  {
    if (vecJoinType == null) {
      setJoinType(1);
    }
    Integer localInteger = (Integer)vecJoinType.get(vecJoinType.size() - 1);
    return localInteger.intValue();
  }
  
  public void addRowSetListener(RowSetListener paramRowSetListener)
  {
    crsInternal.addRowSetListener(paramRowSetListener);
  }
  
  public void removeRowSetListener(RowSetListener paramRowSetListener)
  {
    crsInternal.removeRowSetListener(paramRowSetListener);
  }
  
  public Collection<?> toCollection()
    throws SQLException
  {
    return crsInternal.toCollection();
  }
  
  public Collection<?> toCollection(int paramInt)
    throws SQLException
  {
    return crsInternal.toCollection(paramInt);
  }
  
  public Collection<?> toCollection(String paramString)
    throws SQLException
  {
    return crsInternal.toCollection(paramString);
  }
  
  public CachedRowSet createCopySchema()
    throws SQLException
  {
    return crsInternal.createCopySchema();
  }
  
  public void setSyncProvider(String paramString)
    throws SQLException
  {
    crsInternal.setSyncProvider(paramString);
  }
  
  public void acceptChanges()
    throws SyncProviderException
  {
    crsInternal.acceptChanges();
  }
  
  public SyncProvider getSyncProvider()
    throws SQLException
  {
    return crsInternal.getSyncProvider();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\JoinRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */