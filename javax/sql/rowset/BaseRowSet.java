package javax.sql.rowset;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialRef;

public abstract class BaseRowSet
  implements Serializable, Cloneable
{
  public static final int UNICODE_STREAM_PARAM = 0;
  public static final int BINARY_STREAM_PARAM = 1;
  public static final int ASCII_STREAM_PARAM = 2;
  protected InputStream binaryStream;
  protected InputStream unicodeStream;
  protected InputStream asciiStream;
  protected Reader charStream;
  private String command;
  private String URL;
  private String dataSource;
  private transient String username;
  private transient String password;
  private int rowSetType = 1004;
  private boolean showDeleted = false;
  private int queryTimeout = 0;
  private int maxRows = 0;
  private int maxFieldSize = 0;
  private int concurrency = 1008;
  private boolean readOnly;
  private boolean escapeProcessing = true;
  private int isolation;
  private int fetchDir = 1000;
  private int fetchSize = 0;
  private Map<String, Class<?>> map;
  private Vector<RowSetListener> listeners = new Vector();
  private Hashtable<Integer, Object> params;
  static final long serialVersionUID = 4886719666485113312L;
  
  public BaseRowSet() {}
  
  protected void initParams()
  {
    params = new Hashtable();
  }
  
  public void addRowSetListener(RowSetListener paramRowSetListener)
  {
    listeners.add(paramRowSetListener);
  }
  
  public void removeRowSetListener(RowSetListener paramRowSetListener)
  {
    listeners.remove(paramRowSetListener);
  }
  
  private void checkforRowSetInterface()
    throws SQLException
  {
    if (!(this instanceof RowSet)) {
      throw new SQLException("The class extending abstract class BaseRowSet must implement javax.sql.RowSet or one of it's sub-interfaces.");
    }
  }
  
  protected void notifyCursorMoved()
    throws SQLException
  {
    checkforRowSetInterface();
    if (!listeners.isEmpty())
    {
      RowSetEvent localRowSetEvent = new RowSetEvent((RowSet)this);
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext())
      {
        RowSetListener localRowSetListener = (RowSetListener)localIterator.next();
        localRowSetListener.cursorMoved(localRowSetEvent);
      }
    }
  }
  
  protected void notifyRowChanged()
    throws SQLException
  {
    checkforRowSetInterface();
    if (!listeners.isEmpty())
    {
      RowSetEvent localRowSetEvent = new RowSetEvent((RowSet)this);
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext())
      {
        RowSetListener localRowSetListener = (RowSetListener)localIterator.next();
        localRowSetListener.rowChanged(localRowSetEvent);
      }
    }
  }
  
  protected void notifyRowSetChanged()
    throws SQLException
  {
    checkforRowSetInterface();
    if (!listeners.isEmpty())
    {
      RowSetEvent localRowSetEvent = new RowSetEvent((RowSet)this);
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext())
      {
        RowSetListener localRowSetListener = (RowSetListener)localIterator.next();
        localRowSetListener.rowSetChanged(localRowSetEvent);
      }
    }
  }
  
  public String getCommand()
  {
    return command;
  }
  
  public void setCommand(String paramString)
    throws SQLException
  {
    if (paramString == null)
    {
      command = null;
    }
    else
    {
      if (paramString.length() == 0) {
        throw new SQLException("Invalid command string detected. Cannot be of length less than 0");
      }
      if (params == null) {
        throw new SQLException("Set initParams() before setCommand");
      }
      params.clear();
      command = paramString;
    }
  }
  
  public String getUrl()
    throws SQLException
  {
    return URL;
  }
  
  public void setUrl(String paramString)
    throws SQLException
  {
    if (paramString == null)
    {
      paramString = null;
    }
    else
    {
      if (paramString.length() < 1) {
        throw new SQLException("Invalid url string detected. Cannot be of length less than 1");
      }
      URL = paramString;
    }
    dataSource = null;
  }
  
  public String getDataSourceName()
  {
    return dataSource;
  }
  
  public void setDataSourceName(String paramString)
    throws SQLException
  {
    if (paramString == null)
    {
      dataSource = null;
    }
    else
    {
      if (paramString.equals("")) {
        throw new SQLException("DataSource name cannot be empty string");
      }
      dataSource = paramString;
    }
    URL = null;
  }
  
  public String getUsername()
  {
    return username;
  }
  
  public void setUsername(String paramString)
  {
    if (paramString == null) {
      username = null;
    } else {
      username = paramString;
    }
  }
  
  public String getPassword()
  {
    return password;
  }
  
  public void setPassword(String paramString)
  {
    if (paramString == null) {
      password = null;
    } else {
      password = paramString;
    }
  }
  
  public void setType(int paramInt)
    throws SQLException
  {
    if ((paramInt != 1003) && (paramInt != 1004) && (paramInt != 1005)) {
      throw new SQLException("Invalid type of RowSet set. Must be either ResultSet.TYPE_FORWARD_ONLY or ResultSet.TYPE_SCROLL_INSENSITIVE or ResultSet.TYPE_SCROLL_SENSITIVE.");
    }
    rowSetType = paramInt;
  }
  
  public int getType()
    throws SQLException
  {
    return rowSetType;
  }
  
  public void setConcurrency(int paramInt)
    throws SQLException
  {
    if ((paramInt != 1007) && (paramInt != 1008)) {
      throw new SQLException("Invalid concurrency set. Must be either ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE.");
    }
    concurrency = paramInt;
  }
  
  public boolean isReadOnly()
  {
    return readOnly;
  }
  
  public void setReadOnly(boolean paramBoolean)
  {
    readOnly = paramBoolean;
  }
  
  public int getTransactionIsolation()
  {
    return isolation;
  }
  
  public void setTransactionIsolation(int paramInt)
    throws SQLException
  {
    if ((paramInt != 0) && (paramInt != 2) && (paramInt != 1) && (paramInt != 4) && (paramInt != 8)) {
      throw new SQLException("Invalid transaction isolation set. Must be either Connection.TRANSACTION_NONE or Connection.TRANSACTION_READ_UNCOMMITTED or Connection.TRANSACTION_READ_COMMITTED or Connection.RRANSACTION_REPEATABLE_READ or Connection.TRANSACTION_SERIALIZABLE");
    }
    isolation = paramInt;
  }
  
  public Map<String, Class<?>> getTypeMap()
  {
    return map;
  }
  
  public void setTypeMap(Map<String, Class<?>> paramMap)
  {
    map = paramMap;
  }
  
  public int getMaxFieldSize()
    throws SQLException
  {
    return maxFieldSize;
  }
  
  public void setMaxFieldSize(int paramInt)
    throws SQLException
  {
    if (paramInt < 0) {
      throw new SQLException("Invalid max field size set. Cannot be of value: " + paramInt);
    }
    maxFieldSize = paramInt;
  }
  
  public int getMaxRows()
    throws SQLException
  {
    return maxRows;
  }
  
  public void setMaxRows(int paramInt)
    throws SQLException
  {
    if (paramInt < 0) {
      throw new SQLException("Invalid max row size set. Cannot be of value: " + paramInt);
    }
    if (paramInt < getFetchSize()) {
      throw new SQLException("Invalid max row size set. Cannot be less than the fetchSize.");
    }
    maxRows = paramInt;
  }
  
  public void setEscapeProcessing(boolean paramBoolean)
    throws SQLException
  {
    escapeProcessing = paramBoolean;
  }
  
  public int getQueryTimeout()
    throws SQLException
  {
    return queryTimeout;
  }
  
  public void setQueryTimeout(int paramInt)
    throws SQLException
  {
    if (paramInt < 0) {
      throw new SQLException("Invalid query timeout value set. Cannot be of value: " + paramInt);
    }
    queryTimeout = paramInt;
  }
  
  public boolean getShowDeleted()
    throws SQLException
  {
    return showDeleted;
  }
  
  public void setShowDeleted(boolean paramBoolean)
    throws SQLException
  {
    showDeleted = paramBoolean;
  }
  
  public boolean getEscapeProcessing()
    throws SQLException
  {
    return escapeProcessing;
  }
  
  public void setFetchDirection(int paramInt)
    throws SQLException
  {
    if (((getType() == 1003) && (paramInt != 1000)) || ((paramInt != 1000) && (paramInt != 1001) && (paramInt != 1002))) {
      throw new SQLException("Invalid Fetch Direction");
    }
    fetchDir = paramInt;
  }
  
  public int getFetchDirection()
    throws SQLException
  {
    return fetchDir;
  }
  
  public void setFetchSize(int paramInt)
    throws SQLException
  {
    if ((getMaxRows() == 0) && (paramInt >= 0))
    {
      fetchSize = paramInt;
      return;
    }
    if ((paramInt < 0) || (paramInt > getMaxRows())) {
      throw new SQLException("Invalid fetch size set. Cannot be of value: " + paramInt);
    }
    fetchSize = paramInt;
  }
  
  public int getFetchSize()
    throws SQLException
  {
    return fetchSize;
  }
  
  public int getConcurrency()
    throws SQLException
  {
    return concurrency;
  }
  
  private void checkParamIndex(int paramInt)
    throws SQLException
  {
    if (paramInt < 1) {
      throw new SQLException("Invalid Parameter Index");
    }
  }
  
  public void setNull(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = null;
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    if (params == null) {
      throw new SQLException("Set initParams() before setNull");
    }
    params.put(Integer.valueOf(paramInt1 - 1), arrayOfObject);
  }
  
  public void setNull(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = null;
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    arrayOfObject[2] = paramString;
    if (params == null) {
      throw new SQLException("Set initParams() before setNull");
    }
    params.put(Integer.valueOf(paramInt1 - 1), arrayOfObject);
  }
  
  public void setBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setNull");
    }
    params.put(Integer.valueOf(paramInt - 1), Boolean.valueOf(paramBoolean));
  }
  
  public void setByte(int paramInt, byte paramByte)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setByte");
    }
    params.put(Integer.valueOf(paramInt - 1), Byte.valueOf(paramByte));
  }
  
  public void setShort(int paramInt, short paramShort)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setShort");
    }
    params.put(Integer.valueOf(paramInt - 1), Short.valueOf(paramShort));
  }
  
  public void setInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    if (params == null) {
      throw new SQLException("Set initParams() before setInt");
    }
    params.put(Integer.valueOf(paramInt1 - 1), Integer.valueOf(paramInt2));
  }
  
  public void setLong(int paramInt, long paramLong)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setLong");
    }
    params.put(Integer.valueOf(paramInt - 1), Long.valueOf(paramLong));
  }
  
  public void setFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setFloat");
    }
    params.put(Integer.valueOf(paramInt - 1), Float.valueOf(paramFloat));
  }
  
  public void setDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setDouble");
    }
    params.put(Integer.valueOf(paramInt - 1), Double.valueOf(paramDouble));
  }
  
  public void setBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setBigDecimal");
    }
    params.put(Integer.valueOf(paramInt - 1), paramBigDecimal);
  }
  
  public void setString(int paramInt, String paramString)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setString");
    }
    params.put(Integer.valueOf(paramInt - 1), paramString);
  }
  
  public void setBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setBytes");
    }
    params.put(Integer.valueOf(paramInt - 1), paramArrayOfByte);
  }
  
  public void setDate(int paramInt, Date paramDate)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setDate");
    }
    params.put(Integer.valueOf(paramInt - 1), paramDate);
  }
  
  public void setTime(int paramInt, Time paramTime)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setTime");
    }
    params.put(Integer.valueOf(paramInt - 1), paramTime);
  }
  
  public void setTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setTimestamp");
    }
    params.put(Integer.valueOf(paramInt - 1), paramTimestamp);
  }
  
  public void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = paramInputStream;
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    arrayOfObject[2] = Integer.valueOf(2);
    if (params == null) {
      throw new SQLException("Set initParams() before setAsciiStream");
    }
    params.put(Integer.valueOf(paramInt1 - 1), arrayOfObject);
  }
  
  public void setAsciiStream(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = paramInputStream;
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    arrayOfObject[2] = Integer.valueOf(1);
    if (params == null) {
      throw new SQLException("Set initParams() before setBinaryStream");
    }
    params.put(Integer.valueOf(paramInt1 - 1), arrayOfObject);
  }
  
  public void setBinaryStream(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  @Deprecated
  public void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = paramInputStream;
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    arrayOfObject[2] = Integer.valueOf(0);
    if (params == null) {
      throw new SQLException("Set initParams() before setUnicodeStream");
    }
    params.put(Integer.valueOf(paramInt1 - 1), arrayOfObject);
  }
  
  public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramReader;
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    if (params == null) {
      throw new SQLException("Set initParams() before setCharacterStream");
    }
    params.put(Integer.valueOf(paramInt1 - 1), arrayOfObject);
  }
  
  public void setCharacterStream(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = paramObject;
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    arrayOfObject[2] = Integer.valueOf(paramInt3);
    if (params == null) {
      throw new SQLException("Set initParams() before setObject");
    }
    params.put(Integer.valueOf(paramInt1 - 1), arrayOfObject);
  }
  
  public void setObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramObject;
    arrayOfObject[1] = Integer.valueOf(paramInt2);
    if (params == null) {
      throw new SQLException("Set initParams() before setObject");
    }
    params.put(Integer.valueOf(paramInt1 - 1), arrayOfObject);
  }
  
  public void setObject(int paramInt, Object paramObject)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setObject");
    }
    params.put(Integer.valueOf(paramInt - 1), paramObject);
  }
  
  public void setRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setRef");
    }
    params.put(Integer.valueOf(paramInt - 1), new SerialRef(paramRef));
  }
  
  public void setBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setBlob");
    }
    params.put(Integer.valueOf(paramInt - 1), new SerialBlob(paramBlob));
  }
  
  public void setClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setClob");
    }
    params.put(Integer.valueOf(paramInt - 1), new SerialClob(paramClob));
  }
  
  public void setArray(int paramInt, Array paramArray)
    throws SQLException
  {
    checkParamIndex(paramInt);
    if (params == null) {
      throw new SQLException("Set initParams() before setArray");
    }
    params.put(Integer.valueOf(paramInt - 1), new SerialArray(paramArray));
  }
  
  public void setDate(int paramInt, Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    checkParamIndex(paramInt);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramDate;
    arrayOfObject[1] = paramCalendar;
    if (params == null) {
      throw new SQLException("Set initParams() before setDate");
    }
    params.put(Integer.valueOf(paramInt - 1), arrayOfObject);
  }
  
  public void setTime(int paramInt, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    checkParamIndex(paramInt);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramTime;
    arrayOfObject[1] = paramCalendar;
    if (params == null) {
      throw new SQLException("Set initParams() before setTime");
    }
    params.put(Integer.valueOf(paramInt - 1), arrayOfObject);
  }
  
  public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    checkParamIndex(paramInt);
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramTimestamp;
    arrayOfObject[1] = paramCalendar;
    if (params == null) {
      throw new SQLException("Set initParams() before setTimestamp");
    }
    params.put(Integer.valueOf(paramInt - 1), arrayOfObject);
  }
  
  public void clearParameters()
    throws SQLException
  {
    params.clear();
  }
  
  public Object[] getParams()
    throws SQLException
  {
    if (params == null)
    {
      initParams();
      arrayOfObject = new Object[params.size()];
      return arrayOfObject;
    }
    Object[] arrayOfObject = new Object[params.size()];
    for (int i = 0; i < params.size(); i++)
    {
      arrayOfObject[i] = params.get(Integer.valueOf(i));
      if (arrayOfObject[i] == null) {
        throw new SQLException("missing parameter: " + (i + 1));
      }
    }
    return arrayOfObject;
  }
  
  public void setNull(String paramString, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNull(String paramString1, int paramInt, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setByte(String paramString, byte paramByte)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setShort(String paramString, short paramShort)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setInt(String paramString, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setLong(String paramString, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setFloat(String paramString, float paramFloat)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setDouble(String paramString, double paramDouble)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setString(String paramString1, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNCharacterStream(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setObject(String paramString, Object paramObject)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBlob(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBlob(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBlob(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBlob(String paramString, Blob paramBlob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setBlob(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setClob(String paramString, Clob paramClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setDate(String paramString, Date paramDate)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setDate(String paramString, Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setTime(String paramString, Time paramTime)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setTime(String paramString, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setSQLXML(int paramInt, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setSQLXML(String paramString, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setRowId(int paramInt, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setRowId(String paramString, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNString(int paramInt, String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNString(String paramString1, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNCharacterStream(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNClob(String paramString, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNClob(int paramInt, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setNClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
  
  public void setURL(int paramInt, URL paramURL)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Feature not supported");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\BaseRowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */