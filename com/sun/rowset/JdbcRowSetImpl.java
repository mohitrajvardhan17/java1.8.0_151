package com.sun.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.BaseRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.Joinable;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;

public class JdbcRowSetImpl
  extends BaseRowSet
  implements JdbcRowSet, Joinable
{
  private Connection conn;
  private PreparedStatement ps;
  private ResultSet rs;
  private RowSetMetaDataImpl rowsMD;
  private ResultSetMetaData resMD;
  private Vector<Integer> iMatchColumns;
  private Vector<String> strMatchColumns;
  protected transient JdbcRowSetResourceBundle resBundle;
  static final long serialVersionUID = -3591946023893483003L;
  
  public JdbcRowSetImpl()
  {
    conn = null;
    ps = null;
    rs = null;
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    initParams();
    try
    {
      setShowDeleted(false);
    }
    catch (SQLException localSQLException1)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setshowdeleted").toString() + localSQLException1.getLocalizedMessage());
    }
    try
    {
      setQueryTimeout(0);
    }
    catch (SQLException localSQLException2)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setquerytimeout").toString() + localSQLException2.getLocalizedMessage());
    }
    try
    {
      setMaxRows(0);
    }
    catch (SQLException localSQLException3)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setmaxrows").toString() + localSQLException3.getLocalizedMessage());
    }
    try
    {
      setMaxFieldSize(0);
    }
    catch (SQLException localSQLException4)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setmaxfieldsize").toString() + localSQLException4.getLocalizedMessage());
    }
    try
    {
      setEscapeProcessing(true);
    }
    catch (SQLException localSQLException5)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setescapeprocessing").toString() + localSQLException5.getLocalizedMessage());
    }
    try
    {
      setConcurrency(1008);
    }
    catch (SQLException localSQLException6)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setconcurrency").toString() + localSQLException6.getLocalizedMessage());
    }
    setTypeMap(null);
    try
    {
      setType(1004);
    }
    catch (SQLException localSQLException7)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.settype").toString() + localSQLException7.getLocalizedMessage());
    }
    setReadOnly(true);
    try
    {
      setTransactionIsolation(2);
    }
    catch (SQLException localSQLException8)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.settransactionisolation").toString() + localSQLException8.getLocalizedMessage());
    }
    iMatchColumns = new Vector(10);
    for (int i = 0; i < 10; i++) {
      iMatchColumns.add(i, Integer.valueOf(-1));
    }
    strMatchColumns = new Vector(10);
    for (i = 0; i < 10; i++) {
      strMatchColumns.add(i, null);
    }
  }
  
  public JdbcRowSetImpl(Connection paramConnection)
    throws SQLException
  {
    conn = paramConnection;
    ps = null;
    rs = null;
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    initParams();
    setShowDeleted(false);
    setQueryTimeout(0);
    setMaxRows(0);
    setMaxFieldSize(0);
    setParams();
    setReadOnly(true);
    setTransactionIsolation(2);
    setEscapeProcessing(true);
    setTypeMap(null);
    iMatchColumns = new Vector(10);
    for (int i = 0; i < 10; i++) {
      iMatchColumns.add(i, Integer.valueOf(-1));
    }
    strMatchColumns = new Vector(10);
    for (i = 0; i < 10; i++) {
      strMatchColumns.add(i, null);
    }
  }
  
  public JdbcRowSetImpl(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    conn = null;
    ps = null;
    rs = null;
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    initParams();
    setUsername(paramString2);
    setPassword(paramString3);
    setUrl(paramString1);
    setShowDeleted(false);
    setQueryTimeout(0);
    setMaxRows(0);
    setMaxFieldSize(0);
    setParams();
    setReadOnly(true);
    setTransactionIsolation(2);
    setEscapeProcessing(true);
    setTypeMap(null);
    iMatchColumns = new Vector(10);
    for (int i = 0; i < 10; i++) {
      iMatchColumns.add(i, Integer.valueOf(-1));
    }
    strMatchColumns = new Vector(10);
    for (i = 0; i < 10; i++) {
      strMatchColumns.add(i, null);
    }
  }
  
  public JdbcRowSetImpl(ResultSet paramResultSet)
    throws SQLException
  {
    conn = null;
    ps = null;
    rs = paramResultSet;
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    initParams();
    setShowDeleted(false);
    setQueryTimeout(0);
    setMaxRows(0);
    setMaxFieldSize(0);
    setParams();
    setReadOnly(true);
    setTransactionIsolation(2);
    setEscapeProcessing(true);
    setTypeMap(null);
    resMD = rs.getMetaData();
    rowsMD = new RowSetMetaDataImpl();
    initMetaData(rowsMD, resMD);
    iMatchColumns = new Vector(10);
    for (int i = 0; i < 10; i++) {
      iMatchColumns.add(i, Integer.valueOf(-1));
    }
    strMatchColumns = new Vector(10);
    for (i = 0; i < 10; i++) {
      strMatchColumns.add(i, null);
    }
  }
  
  protected void initMetaData(RowSetMetaData paramRowSetMetaData, ResultSetMetaData paramResultSetMetaData)
    throws SQLException
  {
    int i = paramResultSetMetaData.getColumnCount();
    paramRowSetMetaData.setColumnCount(i);
    for (int j = 1; j <= i; j++)
    {
      paramRowSetMetaData.setAutoIncrement(j, paramResultSetMetaData.isAutoIncrement(j));
      paramRowSetMetaData.setCaseSensitive(j, paramResultSetMetaData.isCaseSensitive(j));
      paramRowSetMetaData.setCurrency(j, paramResultSetMetaData.isCurrency(j));
      paramRowSetMetaData.setNullable(j, paramResultSetMetaData.isNullable(j));
      paramRowSetMetaData.setSigned(j, paramResultSetMetaData.isSigned(j));
      paramRowSetMetaData.setSearchable(j, paramResultSetMetaData.isSearchable(j));
      paramRowSetMetaData.setColumnDisplaySize(j, paramResultSetMetaData.getColumnDisplaySize(j));
      paramRowSetMetaData.setColumnLabel(j, paramResultSetMetaData.getColumnLabel(j));
      paramRowSetMetaData.setColumnName(j, paramResultSetMetaData.getColumnName(j));
      paramRowSetMetaData.setSchemaName(j, paramResultSetMetaData.getSchemaName(j));
      paramRowSetMetaData.setPrecision(j, paramResultSetMetaData.getPrecision(j));
      paramRowSetMetaData.setScale(j, paramResultSetMetaData.getScale(j));
      paramRowSetMetaData.setTableName(j, paramResultSetMetaData.getTableName(j));
      paramRowSetMetaData.setCatalogName(j, paramResultSetMetaData.getCatalogName(j));
      paramRowSetMetaData.setColumnType(j, paramResultSetMetaData.getColumnType(j));
      paramRowSetMetaData.setColumnTypeName(j, paramResultSetMetaData.getColumnTypeName(j));
    }
  }
  
  protected void checkState()
    throws SQLException
  {
    if ((conn == null) && (ps == null) && (rs == null)) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.invalstate").toString());
    }
  }
  
  public void execute()
    throws SQLException
  {
    prepare();
    setProperties(ps);
    decodeParams(getParams(), ps);
    rs = ps.executeQuery();
    notifyRowSetChanged();
  }
  
  protected void setProperties(PreparedStatement paramPreparedStatement)
    throws SQLException
  {
    try
    {
      paramPreparedStatement.setEscapeProcessing(getEscapeProcessing());
    }
    catch (SQLException localSQLException1)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setescapeprocessing").toString() + localSQLException1.getLocalizedMessage());
    }
    try
    {
      paramPreparedStatement.setMaxFieldSize(getMaxFieldSize());
    }
    catch (SQLException localSQLException2)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setmaxfieldsize").toString() + localSQLException2.getLocalizedMessage());
    }
    try
    {
      paramPreparedStatement.setMaxRows(getMaxRows());
    }
    catch (SQLException localSQLException3)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setmaxrows").toString() + localSQLException3.getLocalizedMessage());
    }
    try
    {
      paramPreparedStatement.setQueryTimeout(getQueryTimeout());
    }
    catch (SQLException localSQLException4)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.setquerytimeout").toString() + localSQLException4.getLocalizedMessage());
    }
  }
  
  private Connection connect()
    throws SQLException
  {
    if (conn != null) {
      return conn;
    }
    if (getDataSourceName() != null) {
      try
      {
        InitialContext localInitialContext = new InitialContext();
        DataSource localDataSource = (DataSource)localInitialContext.lookup(getDataSourceName());
        if ((getUsername() != null) && (!getUsername().equals(""))) {
          return localDataSource.getConnection(getUsername(), getPassword());
        }
        return localDataSource.getConnection();
      }
      catch (NamingException localNamingException)
      {
        throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.connect").toString());
      }
    }
    if (getUrl() != null) {
      return DriverManager.getConnection(getUrl(), getUsername(), getPassword());
    }
    return null;
  }
  
  protected PreparedStatement prepare()
    throws SQLException
  {
    conn = connect();
    try
    {
      Map localMap = getTypeMap();
      if (localMap != null) {
        conn.setTypeMap(localMap);
      }
      ps = conn.prepareStatement(getCommand(), 1004, 1008);
    }
    catch (SQLException localSQLException)
    {
      System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.prepare").toString() + localSQLException.getLocalizedMessage());
      if (ps != null) {
        ps.close();
      }
      if (conn != null) {
        conn.close();
      }
      throw new SQLException(localSQLException.getMessage());
    }
    return ps;
  }
  
  private void decodeParams(Object[] paramArrayOfObject, PreparedStatement paramPreparedStatement)
    throws SQLException
  {
    Object[] arrayOfObject = null;
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      if ((paramArrayOfObject[i] instanceof Object[]))
      {
        arrayOfObject = (Object[])paramArrayOfObject[i];
        if (arrayOfObject.length == 2)
        {
          if (arrayOfObject[0] == null)
          {
            paramPreparedStatement.setNull(i + 1, ((Integer)arrayOfObject[1]).intValue());
          }
          else if (((arrayOfObject[0] instanceof Date)) || ((arrayOfObject[0] instanceof Time)) || ((arrayOfObject[0] instanceof Timestamp)))
          {
            System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.detecteddate"));
            if ((arrayOfObject[1] instanceof Calendar))
            {
              System.err.println(resBundle.handleGetObject("jdbcrowsetimpl.detectedcalendar"));
              paramPreparedStatement.setDate(i + 1, (Date)arrayOfObject[0], (Calendar)arrayOfObject[1]);
            }
            else
            {
              throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
            }
          }
          else if ((arrayOfObject[0] instanceof Reader))
          {
            paramPreparedStatement.setCharacterStream(i + 1, (Reader)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          }
          else if ((arrayOfObject[1] instanceof Integer))
          {
            paramPreparedStatement.setObject(i + 1, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          }
        }
        else if (arrayOfObject.length == 3)
        {
          if (arrayOfObject[0] == null)
          {
            paramPreparedStatement.setNull(i + 1, ((Integer)arrayOfObject[1]).intValue(), (String)arrayOfObject[2]);
          }
          else
          {
            if ((arrayOfObject[0] instanceof InputStream)) {
              switch (((Integer)arrayOfObject[2]).intValue())
              {
              case 0: 
                paramPreparedStatement.setUnicodeStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                break;
              case 1: 
                paramPreparedStatement.setBinaryStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                break;
              case 2: 
                paramPreparedStatement.setAsciiStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                break;
              default: 
                throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
              }
            }
            if (((arrayOfObject[1] instanceof Integer)) && ((arrayOfObject[2] instanceof Integer))) {
              paramPreparedStatement.setObject(i + 1, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue(), ((Integer)arrayOfObject[2]).intValue());
            } else {
              throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
            }
          }
        }
        else {
          paramPreparedStatement.setObject(i + 1, paramArrayOfObject[i]);
        }
      }
      else
      {
        paramPreparedStatement.setObject(i + 1, paramArrayOfObject[i]);
      }
    }
  }
  
  public boolean next()
    throws SQLException
  {
    checkState();
    boolean bool = rs.next();
    notifyCursorMoved();
    return bool;
  }
  
  public void close()
    throws SQLException
  {
    if (rs != null) {
      rs.close();
    }
    if (ps != null) {
      ps.close();
    }
    if (conn != null) {
      conn.close();
    }
  }
  
  public boolean wasNull()
    throws SQLException
  {
    checkState();
    return rs.wasNull();
  }
  
  public String getString(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getString(paramInt);
  }
  
  public boolean getBoolean(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getBoolean(paramInt);
  }
  
  public byte getByte(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getByte(paramInt);
  }
  
  public short getShort(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getShort(paramInt);
  }
  
  public int getInt(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getInt(paramInt);
  }
  
  public long getLong(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getLong(paramInt);
  }
  
  public float getFloat(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getFloat(paramInt);
  }
  
  public double getDouble(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getDouble(paramInt);
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkState();
    return rs.getBigDecimal(paramInt1, paramInt2);
  }
  
  public byte[] getBytes(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getBytes(paramInt);
  }
  
  public Date getDate(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getDate(paramInt);
  }
  
  public Time getTime(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getTime(paramInt);
  }
  
  public Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getTimestamp(paramInt);
  }
  
  public InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getAsciiStream(paramInt);
  }
  
  @Deprecated
  public InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getUnicodeStream(paramInt);
  }
  
  public InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getBinaryStream(paramInt);
  }
  
  public String getString(String paramString)
    throws SQLException
  {
    return getString(findColumn(paramString));
  }
  
  public boolean getBoolean(String paramString)
    throws SQLException
  {
    return getBoolean(findColumn(paramString));
  }
  
  public byte getByte(String paramString)
    throws SQLException
  {
    return getByte(findColumn(paramString));
  }
  
  public short getShort(String paramString)
    throws SQLException
  {
    return getShort(findColumn(paramString));
  }
  
  public int getInt(String paramString)
    throws SQLException
  {
    return getInt(findColumn(paramString));
  }
  
  public long getLong(String paramString)
    throws SQLException
  {
    return getLong(findColumn(paramString));
  }
  
  public float getFloat(String paramString)
    throws SQLException
  {
    return getFloat(findColumn(paramString));
  }
  
  public double getDouble(String paramString)
    throws SQLException
  {
    return getDouble(findColumn(paramString));
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(String paramString, int paramInt)
    throws SQLException
  {
    return getBigDecimal(findColumn(paramString), paramInt);
  }
  
  public byte[] getBytes(String paramString)
    throws SQLException
  {
    return getBytes(findColumn(paramString));
  }
  
  public Date getDate(String paramString)
    throws SQLException
  {
    return getDate(findColumn(paramString));
  }
  
  public Time getTime(String paramString)
    throws SQLException
  {
    return getTime(findColumn(paramString));
  }
  
  public Timestamp getTimestamp(String paramString)
    throws SQLException
  {
    return getTimestamp(findColumn(paramString));
  }
  
  public InputStream getAsciiStream(String paramString)
    throws SQLException
  {
    return getAsciiStream(findColumn(paramString));
  }
  
  @Deprecated
  public InputStream getUnicodeStream(String paramString)
    throws SQLException
  {
    return getUnicodeStream(findColumn(paramString));
  }
  
  public InputStream getBinaryStream(String paramString)
    throws SQLException
  {
    return getBinaryStream(findColumn(paramString));
  }
  
  public SQLWarning getWarnings()
    throws SQLException
  {
    checkState();
    return rs.getWarnings();
  }
  
  public void clearWarnings()
    throws SQLException
  {
    checkState();
    rs.clearWarnings();
  }
  
  public String getCursorName()
    throws SQLException
  {
    checkState();
    return rs.getCursorName();
  }
  
  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    checkState();
    try
    {
      checkState();
    }
    catch (SQLException localSQLException)
    {
      prepare();
      return ps.getMetaData();
    }
    return rs.getMetaData();
  }
  
  public Object getObject(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getObject(paramInt);
  }
  
  public Object getObject(String paramString)
    throws SQLException
  {
    return getObject(findColumn(paramString));
  }
  
  public int findColumn(String paramString)
    throws SQLException
  {
    checkState();
    return rs.findColumn(paramString);
  }
  
  public Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getCharacterStream(paramInt);
  }
  
  public Reader getCharacterStream(String paramString)
    throws SQLException
  {
    return getCharacterStream(findColumn(paramString));
  }
  
  public BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getBigDecimal(paramInt);
  }
  
  public BigDecimal getBigDecimal(String paramString)
    throws SQLException
  {
    return getBigDecimal(findColumn(paramString));
  }
  
  public boolean isBeforeFirst()
    throws SQLException
  {
    checkState();
    return rs.isBeforeFirst();
  }
  
  public boolean isAfterLast()
    throws SQLException
  {
    checkState();
    return rs.isAfterLast();
  }
  
  public boolean isFirst()
    throws SQLException
  {
    checkState();
    return rs.isFirst();
  }
  
  public boolean isLast()
    throws SQLException
  {
    checkState();
    return rs.isLast();
  }
  
  public void beforeFirst()
    throws SQLException
  {
    checkState();
    rs.beforeFirst();
    notifyCursorMoved();
  }
  
  public void afterLast()
    throws SQLException
  {
    checkState();
    rs.afterLast();
    notifyCursorMoved();
  }
  
  public boolean first()
    throws SQLException
  {
    checkState();
    boolean bool = rs.first();
    notifyCursorMoved();
    return bool;
  }
  
  public boolean last()
    throws SQLException
  {
    checkState();
    boolean bool = rs.last();
    notifyCursorMoved();
    return bool;
  }
  
  public int getRow()
    throws SQLException
  {
    checkState();
    return rs.getRow();
  }
  
  public boolean absolute(int paramInt)
    throws SQLException
  {
    checkState();
    boolean bool = rs.absolute(paramInt);
    notifyCursorMoved();
    return bool;
  }
  
  public boolean relative(int paramInt)
    throws SQLException
  {
    checkState();
    boolean bool = rs.relative(paramInt);
    notifyCursorMoved();
    return bool;
  }
  
  public boolean previous()
    throws SQLException
  {
    checkState();
    boolean bool = rs.previous();
    notifyCursorMoved();
    return bool;
  }
  
  public void setFetchDirection(int paramInt)
    throws SQLException
  {
    checkState();
    rs.setFetchDirection(paramInt);
  }
  
  public int getFetchDirection()
    throws SQLException
  {
    try
    {
      checkState();
    }
    catch (SQLException localSQLException)
    {
      super.getFetchDirection();
    }
    return rs.getFetchDirection();
  }
  
  public void setFetchSize(int paramInt)
    throws SQLException
  {
    checkState();
    rs.setFetchSize(paramInt);
  }
  
  public int getType()
    throws SQLException
  {
    try
    {
      checkState();
    }
    catch (SQLException localSQLException)
    {
      return super.getType();
    }
    if (rs == null) {
      return super.getType();
    }
    int i = rs.getType();
    return i;
  }
  
  public int getConcurrency()
    throws SQLException
  {
    try
    {
      checkState();
    }
    catch (SQLException localSQLException)
    {
      super.getConcurrency();
    }
    return rs.getConcurrency();
  }
  
  public boolean rowUpdated()
    throws SQLException
  {
    checkState();
    return rs.rowUpdated();
  }
  
  public boolean rowInserted()
    throws SQLException
  {
    checkState();
    return rs.rowInserted();
  }
  
  public boolean rowDeleted()
    throws SQLException
  {
    checkState();
    return rs.rowDeleted();
  }
  
  public void updateNull(int paramInt)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateNull(paramInt);
  }
  
  public void updateBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateBoolean(paramInt, paramBoolean);
  }
  
  public void updateByte(int paramInt, byte paramByte)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateByte(paramInt, paramByte);
  }
  
  public void updateShort(int paramInt, short paramShort)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateShort(paramInt, paramShort);
  }
  
  public void updateInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateInt(paramInt1, paramInt2);
  }
  
  public void updateLong(int paramInt, long paramLong)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateLong(paramInt, paramLong);
  }
  
  public void updateFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateFloat(paramInt, paramFloat);
  }
  
  public void updateDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateDouble(paramInt, paramDouble);
  }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateBigDecimal(paramInt, paramBigDecimal);
  }
  
  public void updateString(int paramInt, String paramString)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateString(paramInt, paramString);
  }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateBytes(paramInt, paramArrayOfByte);
  }
  
  public void updateDate(int paramInt, Date paramDate)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateDate(paramInt, paramDate);
  }
  
  public void updateTime(int paramInt, Time paramTime)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateTime(paramInt, paramTime);
  }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateTimestamp(paramInt, paramTimestamp);
  }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateAsciiStream(paramInt1, paramInputStream, paramInt2);
  }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateBinaryStream(paramInt1, paramInputStream, paramInt2);
  }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateCharacterStream(paramInt1, paramReader, paramInt2);
  }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateObject(paramInt1, paramObject, paramInt2);
  }
  
  public void updateObject(int paramInt, Object paramObject)
    throws SQLException
  {
    checkState();
    checkTypeConcurrency();
    rs.updateObject(paramInt, paramObject);
  }
  
  public void updateNull(String paramString)
    throws SQLException
  {
    updateNull(findColumn(paramString));
  }
  
  public void updateBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    updateBoolean(findColumn(paramString), paramBoolean);
  }
  
  public void updateByte(String paramString, byte paramByte)
    throws SQLException
  {
    updateByte(findColumn(paramString), paramByte);
  }
  
  public void updateShort(String paramString, short paramShort)
    throws SQLException
  {
    updateShort(findColumn(paramString), paramShort);
  }
  
  public void updateInt(String paramString, int paramInt)
    throws SQLException
  {
    updateInt(findColumn(paramString), paramInt);
  }
  
  public void updateLong(String paramString, long paramLong)
    throws SQLException
  {
    updateLong(findColumn(paramString), paramLong);
  }
  
  public void updateFloat(String paramString, float paramFloat)
    throws SQLException
  {
    updateFloat(findColumn(paramString), paramFloat);
  }
  
  public void updateDouble(String paramString, double paramDouble)
    throws SQLException
  {
    updateDouble(findColumn(paramString), paramDouble);
  }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    updateBigDecimal(findColumn(paramString), paramBigDecimal);
  }
  
  public void updateString(String paramString1, String paramString2)
    throws SQLException
  {
    updateString(findColumn(paramString1), paramString2);
  }
  
  public void updateBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    updateBytes(findColumn(paramString), paramArrayOfByte);
  }
  
  public void updateDate(String paramString, Date paramDate)
    throws SQLException
  {
    updateDate(findColumn(paramString), paramDate);
  }
  
  public void updateTime(String paramString, Time paramTime)
    throws SQLException
  {
    updateTime(findColumn(paramString), paramTime);
  }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    updateTimestamp(findColumn(paramString), paramTimestamp);
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    updateAsciiStream(findColumn(paramString), paramInputStream, paramInt);
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    updateBinaryStream(findColumn(paramString), paramInputStream, paramInt);
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    updateCharacterStream(findColumn(paramString), paramReader, paramInt);
  }
  
  public void updateObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    updateObject(findColumn(paramString), paramObject, paramInt);
  }
  
  public void updateObject(String paramString, Object paramObject)
    throws SQLException
  {
    updateObject(findColumn(paramString), paramObject);
  }
  
  public void insertRow()
    throws SQLException
  {
    checkState();
    rs.insertRow();
    notifyRowChanged();
  }
  
  public void updateRow()
    throws SQLException
  {
    checkState();
    rs.updateRow();
    notifyRowChanged();
  }
  
  public void deleteRow()
    throws SQLException
  {
    checkState();
    rs.deleteRow();
    notifyRowChanged();
  }
  
  public void refreshRow()
    throws SQLException
  {
    checkState();
    rs.refreshRow();
  }
  
  public void cancelRowUpdates()
    throws SQLException
  {
    checkState();
    rs.cancelRowUpdates();
    notifyRowChanged();
  }
  
  public void moveToInsertRow()
    throws SQLException
  {
    checkState();
    rs.moveToInsertRow();
  }
  
  public void moveToCurrentRow()
    throws SQLException
  {
    checkState();
    rs.moveToCurrentRow();
  }
  
  public Statement getStatement()
    throws SQLException
  {
    if (rs != null) {
      return rs.getStatement();
    }
    return null;
  }
  
  public Object getObject(int paramInt, Map<String, Class<?>> paramMap)
    throws SQLException
  {
    checkState();
    return rs.getObject(paramInt, paramMap);
  }
  
  public Ref getRef(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getRef(paramInt);
  }
  
  public Blob getBlob(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getBlob(paramInt);
  }
  
  public Clob getClob(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getClob(paramInt);
  }
  
  public Array getArray(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getArray(paramInt);
  }
  
  public Object getObject(String paramString, Map<String, Class<?>> paramMap)
    throws SQLException
  {
    return getObject(findColumn(paramString), paramMap);
  }
  
  public Ref getRef(String paramString)
    throws SQLException
  {
    return getRef(findColumn(paramString));
  }
  
  public Blob getBlob(String paramString)
    throws SQLException
  {
    return getBlob(findColumn(paramString));
  }
  
  public Clob getClob(String paramString)
    throws SQLException
  {
    return getClob(findColumn(paramString));
  }
  
  public Array getArray(String paramString)
    throws SQLException
  {
    return getArray(findColumn(paramString));
  }
  
  public Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    checkState();
    return rs.getDate(paramInt, paramCalendar);
  }
  
  public Date getDate(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getDate(findColumn(paramString), paramCalendar);
  }
  
  public Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    checkState();
    return rs.getTime(paramInt, paramCalendar);
  }
  
  public Time getTime(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getTime(findColumn(paramString), paramCalendar);
  }
  
  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    checkState();
    return rs.getTimestamp(paramInt, paramCalendar);
  }
  
  public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getTimestamp(findColumn(paramString), paramCalendar);
  }
  
  public void updateRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    checkState();
    rs.updateRef(paramInt, paramRef);
  }
  
  public void updateRef(String paramString, Ref paramRef)
    throws SQLException
  {
    updateRef(findColumn(paramString), paramRef);
  }
  
  public void updateClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    checkState();
    rs.updateClob(paramInt, paramClob);
  }
  
  public void updateClob(String paramString, Clob paramClob)
    throws SQLException
  {
    updateClob(findColumn(paramString), paramClob);
  }
  
  public void updateBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    checkState();
    rs.updateBlob(paramInt, paramBlob);
  }
  
  public void updateBlob(String paramString, Blob paramBlob)
    throws SQLException
  {
    updateBlob(findColumn(paramString), paramBlob);
  }
  
  public void updateArray(int paramInt, Array paramArray)
    throws SQLException
  {
    checkState();
    rs.updateArray(paramInt, paramArray);
  }
  
  public void updateArray(String paramString, Array paramArray)
    throws SQLException
  {
    updateArray(findColumn(paramString), paramArray);
  }
  
  public URL getURL(int paramInt)
    throws SQLException
  {
    checkState();
    return rs.getURL(paramInt);
  }
  
  public URL getURL(String paramString)
    throws SQLException
  {
    return getURL(findColumn(paramString));
  }
  
  public RowSetWarning getRowSetWarnings()
    throws SQLException
  {
    return null;
  }
  
  public void unsetMatchColumn(int[] paramArrayOfInt)
    throws SQLException
  {
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      int i = Integer.parseInt(((Integer)iMatchColumns.get(j)).toString());
      if (paramArrayOfInt[j] != i) {
        throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.matchcols").toString());
      }
    }
    for (j = 0; j < paramArrayOfInt.length; j++) {
      iMatchColumns.set(j, Integer.valueOf(-1));
    }
  }
  
  public void unsetMatchColumn(String[] paramArrayOfString)
    throws SQLException
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (!paramArrayOfString[i].equals(strMatchColumns.get(i))) {
        throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.matchcols").toString());
      }
    }
    for (i = 0; i < paramArrayOfString.length; i++) {
      strMatchColumns.set(i, null);
    }
  }
  
  public String[] getMatchColumnNames()
    throws SQLException
  {
    String[] arrayOfString = new String[strMatchColumns.size()];
    if (strMatchColumns.get(0) == null) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.setmatchcols").toString());
    }
    strMatchColumns.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public int[] getMatchColumnIndexes()
    throws SQLException
  {
    Integer[] arrayOfInteger = new Integer[iMatchColumns.size()];
    int[] arrayOfInt = new int[iMatchColumns.size()];
    int i = ((Integer)iMatchColumns.get(0)).intValue();
    if (i == -1) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.setmatchcols").toString());
    }
    iMatchColumns.copyInto(arrayOfInteger);
    for (int j = 0; j < arrayOfInteger.length; j++) {
      arrayOfInt[j] = arrayOfInteger[j].intValue();
    }
    return arrayOfInt;
  }
  
  public void setMatchColumn(int[] paramArrayOfInt)
    throws SQLException
  {
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      if (paramArrayOfInt[i] < 0) {
        throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.matchcols1").toString());
      }
    }
    for (i = 0; i < paramArrayOfInt.length; i++) {
      iMatchColumns.add(i, Integer.valueOf(paramArrayOfInt[i]));
    }
  }
  
  public void setMatchColumn(String[] paramArrayOfString)
    throws SQLException
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if ((paramArrayOfString[i] == null) || (paramArrayOfString[i].equals(""))) {
        throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.matchcols2").toString());
      }
    }
    for (i = 0; i < paramArrayOfString.length; i++) {
      strMatchColumns.add(i, paramArrayOfString[i]);
    }
  }
  
  public void setMatchColumn(int paramInt)
    throws SQLException
  {
    if (paramInt < 0) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.matchcols1").toString());
    }
    iMatchColumns.set(0, Integer.valueOf(paramInt));
  }
  
  public void setMatchColumn(String paramString)
    throws SQLException
  {
    if ((paramString == null) || ((paramString = paramString.trim()).equals(""))) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.matchcols2").toString());
    }
    strMatchColumns.set(0, paramString);
  }
  
  public void unsetMatchColumn(int paramInt)
    throws SQLException
  {
    if (!((Integer)iMatchColumns.get(0)).equals(Integer.valueOf(paramInt))) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.unsetmatch").toString());
    }
    if (strMatchColumns.get(0) != null) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.usecolname").toString());
    }
    iMatchColumns.set(0, Integer.valueOf(-1));
  }
  
  public void unsetMatchColumn(String paramString)
    throws SQLException
  {
    paramString = paramString.trim();
    if (!((String)strMatchColumns.get(0)).equals(paramString)) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.unsetmatch").toString());
    }
    if (((Integer)iMatchColumns.get(0)).intValue() > 0) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.usecolid").toString());
    }
    strMatchColumns.set(0, null);
  }
  
  public DatabaseMetaData getDatabaseMetaData()
    throws SQLException
  {
    Connection localConnection = connect();
    return localConnection.getMetaData();
  }
  
  public ParameterMetaData getParameterMetaData()
    throws SQLException
  {
    prepare();
    return ps.getParameterMetaData();
  }
  
  public void commit()
    throws SQLException
  {
    conn.commit();
    if (conn.getHoldability() != 1) {
      rs = null;
    }
  }
  
  public void setAutoCommit(boolean paramBoolean)
    throws SQLException
  {
    if (conn != null)
    {
      conn.setAutoCommit(paramBoolean);
    }
    else
    {
      conn = connect();
      conn.setAutoCommit(paramBoolean);
    }
  }
  
  public boolean getAutoCommit()
    throws SQLException
  {
    return conn.getAutoCommit();
  }
  
  public void rollback()
    throws SQLException
  {
    conn.rollback();
    rs = null;
  }
  
  public void rollback(Savepoint paramSavepoint)
    throws SQLException
  {
    conn.rollback(paramSavepoint);
  }
  
  protected void setParams()
    throws SQLException
  {
    if (rs == null)
    {
      setType(1004);
      setConcurrency(1008);
    }
    else
    {
      setType(rs.getType());
      setConcurrency(rs.getConcurrency());
    }
  }
  
  private void checkTypeConcurrency()
    throws SQLException
  {
    if ((rs.getType() == 1003) || (rs.getConcurrency() == 1007)) {
      throw new SQLException(resBundle.handleGetObject("jdbcrowsetimpl.resnotupd").toString());
    }
  }
  
  protected Connection getConnection()
  {
    return conn;
  }
  
  protected void setConnection(Connection paramConnection)
  {
    conn = paramConnection;
  }
  
  protected PreparedStatement getPreparedStatement()
  {
    return ps;
  }
  
  protected void setPreparedStatement(PreparedStatement paramPreparedStatement)
  {
    ps = paramPreparedStatement;
  }
  
  protected ResultSet getResultSet()
    throws SQLException
  {
    checkState();
    return rs;
  }
  
  protected void setResultSet(ResultSet paramResultSet)
  {
    rs = paramResultSet;
  }
  
  public void setCommand(String paramString)
    throws SQLException
  {
    if (getCommand() != null)
    {
      if (!getCommand().equals(paramString))
      {
        super.setCommand(paramString);
        ps = null;
        rs = null;
      }
    }
    else {
      super.setCommand(paramString);
    }
  }
  
  public void setDataSourceName(String paramString)
    throws SQLException
  {
    if (getDataSourceName() != null)
    {
      if (!getDataSourceName().equals(paramString))
      {
        super.setDataSourceName(paramString);
        conn = null;
        ps = null;
        rs = null;
      }
    }
    else {
      super.setDataSourceName(paramString);
    }
  }
  
  public void setUrl(String paramString)
    throws SQLException
  {
    if (getUrl() != null)
    {
      if (!getUrl().equals(paramString))
      {
        super.setUrl(paramString);
        conn = null;
        ps = null;
        rs = null;
      }
    }
    else {
      super.setUrl(paramString);
    }
  }
  
  public void setUsername(String paramString)
  {
    if (getUsername() != null)
    {
      if (!getUsername().equals(paramString))
      {
        super.setUsername(paramString);
        conn = null;
        ps = null;
        rs = null;
      }
    }
    else {
      super.setUsername(paramString);
    }
  }
  
  public void setPassword(String paramString)
  {
    if (getPassword() != null)
    {
      if (!getPassword().equals(paramString))
      {
        super.setPassword(paramString);
        conn = null;
        ps = null;
        rs = null;
      }
    }
    else {
      super.setPassword(paramString);
    }
  }
  
  public void setType(int paramInt)
    throws SQLException
  {
    int i;
    try
    {
      i = getType();
    }
    catch (SQLException localSQLException)
    {
      i = 0;
    }
    if (i != paramInt) {
      super.setType(paramInt);
    }
  }
  
  public void setConcurrency(int paramInt)
    throws SQLException
  {
    int i;
    try
    {
      i = getConcurrency();
    }
    catch (NullPointerException localNullPointerException)
    {
      i = 0;
    }
    if (i != paramInt) {
      super.setConcurrency(paramInt);
    }
  }
  
  public SQLXML getSQLXML(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public SQLXML getSQLXML(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public RowId getRowId(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public RowId getRowId(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateRowId(int paramInt, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateRowId(String paramString, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public int getHoldability()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public boolean isClosed()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNString(int paramInt, String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNString(String paramString1, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(int paramInt, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(String paramString, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public NClob getNClob(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public NClob getNClob(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public <T> T unwrap(Class<T> paramClass)
    throws SQLException
  {
    return null;
  }
  
  public boolean isWrapperFor(Class<?> paramClass)
    throws SQLException
  {
    return false;
  }
  
  public void setSQLXML(int paramInt, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setSQLXML(String paramString, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setRowId(int paramInt, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setRowId(String paramString, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNString(int paramInt, String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNCharacterStream(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(String paramString, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public Reader getNCharacterStream(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public Reader getNCharacterStream(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateSQLXML(int paramInt, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateSQLXML(String paramString, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public String getNString(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public String getNString(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBlob(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBlob(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBlob(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateCharacterStream(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setURL(int paramInt, URL paramURL)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(int paramInt, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNString(String paramString1, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNCharacterStream(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(String paramString, Clob paramClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setDate(String paramString, Date paramDate)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setDate(String paramString, Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setTime(String paramString, Time paramTime)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setTime(String paramString, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(String paramString, Blob paramBlob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setObject(String paramString, Object paramObject)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setString(String paramString1, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNull(String paramString, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setNull(String paramString1, int paramInt, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setByte(String paramString, byte paramByte)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setShort(String paramString, short paramShort)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setInt(String paramString, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setLong(String paramString, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setFloat(String paramString, float paramFloat)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  public void setDouble(String paramString, double paramDouble)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException) {}
  }
  
  public <T> T getObject(int paramInt, Class<T> paramClass)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported yet.");
  }
  
  public <T> T getObject(String paramString, Class<T> paramClass)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported yet.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\JdbcRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */