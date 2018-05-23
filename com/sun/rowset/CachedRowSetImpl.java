package com.sun.rowset;

import com.sun.rowset.internal.BaseRow;
import com.sun.rowset.internal.CachedRowSetReader;
import com.sun.rowset.internal.CachedRowSetWriter;
import com.sun.rowset.internal.InsertRow;
import com.sun.rowset.internal.Row;
import com.sun.rowset.providers.RIOptimisticProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetInternal;
import javax.sql.RowSetMetaData;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.BaseRowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.serial.SQLInputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialRef;
import javax.sql.rowset.serial.SerialStruct;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.TransactionalWriter;
import sun.reflect.misc.ReflectUtil;

public class CachedRowSetImpl
  extends BaseRowSet
  implements RowSet, RowSetInternal, Serializable, Cloneable, CachedRowSet
{
  private SyncProvider provider;
  private RowSetReader rowSetReader;
  private RowSetWriter rowSetWriter;
  private transient Connection conn;
  private transient ResultSetMetaData RSMD;
  private RowSetMetaDataImpl RowSetMD;
  private int[] keyCols;
  private String tableName;
  private Vector<Object> rvh;
  private int cursorPos;
  private int absolutePos;
  private int numDeleted;
  private int numRows;
  private InsertRow insertRow;
  private boolean onInsertRow;
  private int currentRow;
  private boolean lastValueNull;
  private SQLWarning sqlwarn;
  private String strMatchColumn = "";
  private int iMatchColumn = -1;
  private RowSetWarning rowsetWarning;
  private String DEFAULT_SYNC_PROVIDER = "com.sun.rowset.providers.RIOptimisticProvider";
  private boolean dbmslocatorsUpdateCopy;
  private transient ResultSet resultSet;
  private int endPos;
  private int prevEndPos;
  private int startPos;
  private int startPrev;
  private int pageSize;
  private int maxRowsreached;
  private boolean pagenotend = true;
  private boolean onFirstPage;
  private boolean onLastPage;
  private int populatecallcount;
  private int totalRows;
  private boolean callWithCon;
  private CachedRowSetReader crsReader;
  private Vector<Integer> iMatchColumns;
  private Vector<String> strMatchColumns;
  private boolean tXWriter = false;
  private TransactionalWriter tWriter = null;
  protected transient JdbcRowSetResourceBundle resBundle;
  private boolean updateOnInsert;
  static final long serialVersionUID = 1884577171200622428L;
  
  public CachedRowSetImpl()
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
    provider = SyncFactory.getInstance(DEFAULT_SYNC_PROVIDER);
    if (!(provider instanceof RIOptimisticProvider)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidp").toString());
    }
    rowSetReader = ((CachedRowSetReader)provider.getRowSetReader());
    rowSetWriter = ((CachedRowSetWriter)provider.getRowSetWriter());
    initParams();
    initContainer();
    initProperties();
    onInsertRow = false;
    insertRow = null;
    sqlwarn = new SQLWarning();
    rowsetWarning = new RowSetWarning();
  }
  
  public CachedRowSetImpl(Hashtable paramHashtable)
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
    if (paramHashtable == null) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.nullhash").toString());
    }
    String str = (String)paramHashtable.get("rowset.provider.classname");
    provider = SyncFactory.getInstance(str);
    rowSetReader = provider.getRowSetReader();
    rowSetWriter = provider.getRowSetWriter();
    initParams();
    initContainer();
    initProperties();
  }
  
  private void initContainer()
  {
    rvh = new Vector(100);
    cursorPos = 0;
    absolutePos = 0;
    numRows = 0;
    numDeleted = 0;
  }
  
  private void initProperties()
    throws SQLException
  {
    if (resBundle == null) {
      try
      {
        resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
    }
    setShowDeleted(false);
    setQueryTimeout(0);
    setMaxRows(0);
    setMaxFieldSize(0);
    setType(1004);
    setConcurrency(1008);
    if ((rvh.size() > 0) && (!isReadOnly())) {
      setReadOnly(false);
    } else {
      setReadOnly(true);
    }
    setTransactionIsolation(2);
    setEscapeProcessing(true);
    checkTransactionalWriter();
    iMatchColumns = new Vector(10);
    for (int i = 0; i < 10; i++) {
      iMatchColumns.add(i, Integer.valueOf(-1));
    }
    strMatchColumns = new Vector(10);
    for (i = 0; i < 10; i++) {
      strMatchColumns.add(i, null);
    }
  }
  
  private void checkTransactionalWriter()
  {
    if (rowSetWriter != null)
    {
      Class localClass = rowSetWriter.getClass();
      if (localClass != null)
      {
        Class[] arrayOfClass = localClass.getInterfaces();
        for (int i = 0; i < arrayOfClass.length; i++) {
          if (arrayOfClass[i].getName().indexOf("TransactionalWriter") > 0)
          {
            tXWriter = true;
            establishTransactionalWriter();
          }
        }
      }
    }
  }
  
  private void establishTransactionalWriter()
  {
    tWriter = ((TransactionalWriter)provider.getRowSetWriter());
  }
  
  public void setCommand(String paramString)
    throws SQLException
  {
    super.setCommand(paramString);
    if (!buildTableName(paramString).equals("")) {
      setTableName(buildTableName(paramString));
    }
  }
  
  public void populate(ResultSet paramResultSet)
    throws SQLException
  {
    Map localMap = getTypeMap();
    if (paramResultSet == null) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.populate").toString());
    }
    resultSet = paramResultSet;
    RSMD = paramResultSet.getMetaData();
    RowSetMD = new RowSetMetaDataImpl();
    initMetaData(RowSetMD, RSMD);
    RSMD = null;
    int j = RowSetMD.getColumnCount();
    int m = getMaxRows();
    int i = 0;
    Row localRow = null;
    while (paramResultSet.next())
    {
      localRow = new Row(j);
      if ((i > m) && (m > 0)) {
        rowsetWarning.setNextWarning(new RowSetWarning("Populating rows setting has exceeded max row setting"));
      }
      for (int k = 1; k <= j; k++)
      {
        Object localObject;
        if ((localMap == null) || (localMap.isEmpty())) {
          localObject = paramResultSet.getObject(k);
        } else {
          localObject = paramResultSet.getObject(k, localMap);
        }
        if ((localObject instanceof Struct)) {
          localObject = new SerialStruct((Struct)localObject, localMap);
        } else if ((localObject instanceof SQLData)) {
          localObject = new SerialStruct((SQLData)localObject, localMap);
        } else if ((localObject instanceof Blob)) {
          localObject = new SerialBlob((Blob)localObject);
        } else if ((localObject instanceof Clob)) {
          localObject = new SerialClob((Clob)localObject);
        } else if ((localObject instanceof Array)) {
          if (localMap != null) {
            localObject = new SerialArray((Array)localObject, localMap);
          } else {
            localObject = new SerialArray((Array)localObject);
          }
        }
        localRow.initColumnObject(k, localObject);
      }
      i++;
      rvh.add(localRow);
    }
    numRows = i;
    notifyRowSetChanged();
  }
  
  private void initMetaData(RowSetMetaDataImpl paramRowSetMetaDataImpl, ResultSetMetaData paramResultSetMetaData)
    throws SQLException
  {
    int i = paramResultSetMetaData.getColumnCount();
    paramRowSetMetaDataImpl.setColumnCount(i);
    for (int j = 1; j <= i; j++)
    {
      paramRowSetMetaDataImpl.setAutoIncrement(j, paramResultSetMetaData.isAutoIncrement(j));
      if (paramResultSetMetaData.isAutoIncrement(j)) {
        updateOnInsert = true;
      }
      paramRowSetMetaDataImpl.setCaseSensitive(j, paramResultSetMetaData.isCaseSensitive(j));
      paramRowSetMetaDataImpl.setCurrency(j, paramResultSetMetaData.isCurrency(j));
      paramRowSetMetaDataImpl.setNullable(j, paramResultSetMetaData.isNullable(j));
      paramRowSetMetaDataImpl.setSigned(j, paramResultSetMetaData.isSigned(j));
      paramRowSetMetaDataImpl.setSearchable(j, paramResultSetMetaData.isSearchable(j));
      int k = paramResultSetMetaData.getColumnDisplaySize(j);
      if (k < 0) {
        k = 0;
      }
      paramRowSetMetaDataImpl.setColumnDisplaySize(j, k);
      paramRowSetMetaDataImpl.setColumnLabel(j, paramResultSetMetaData.getColumnLabel(j));
      paramRowSetMetaDataImpl.setColumnName(j, paramResultSetMetaData.getColumnName(j));
      paramRowSetMetaDataImpl.setSchemaName(j, paramResultSetMetaData.getSchemaName(j));
      int m = paramResultSetMetaData.getPrecision(j);
      if (m < 0) {
        m = 0;
      }
      paramRowSetMetaDataImpl.setPrecision(j, m);
      int n = paramResultSetMetaData.getScale(j);
      if (n < 0) {
        n = 0;
      }
      paramRowSetMetaDataImpl.setScale(j, n);
      paramRowSetMetaDataImpl.setTableName(j, paramResultSetMetaData.getTableName(j));
      paramRowSetMetaDataImpl.setCatalogName(j, paramResultSetMetaData.getCatalogName(j));
      paramRowSetMetaDataImpl.setColumnType(j, paramResultSetMetaData.getColumnType(j));
      paramRowSetMetaDataImpl.setColumnTypeName(j, paramResultSetMetaData.getColumnTypeName(j));
    }
    if (conn != null) {
      dbmslocatorsUpdateCopy = conn.getMetaData().locatorsUpdateCopy();
    }
  }
  
  public void execute(Connection paramConnection)
    throws SQLException
  {
    setConnection(paramConnection);
    if (getPageSize() != 0)
    {
      crsReader = ((CachedRowSetReader)provider.getRowSetReader());
      crsReader.setStartPosition(1);
      callWithCon = true;
      crsReader.readData(this);
    }
    else
    {
      rowSetReader.readData(this);
    }
    RowSetMD = ((RowSetMetaDataImpl)getMetaData());
    if (paramConnection != null) {
      dbmslocatorsUpdateCopy = paramConnection.getMetaData().locatorsUpdateCopy();
    }
  }
  
  private void setConnection(Connection paramConnection)
  {
    conn = paramConnection;
  }
  
  public void acceptChanges()
    throws SyncProviderException
  {
    if (onInsertRow == true) {
      throw new SyncProviderException(resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
    }
    int i = cursorPos;
    int j = 0;
    boolean bool = false;
    try
    {
      if (rowSetWriter != null)
      {
        i = cursorPos;
        bool = rowSetWriter.writeData(this);
        cursorPos = i;
      }
      if (tXWriter) {
        if (!bool)
        {
          tWriter = ((TransactionalWriter)rowSetWriter);
          tWriter.rollback();
          j = 0;
        }
        else
        {
          tWriter = ((TransactionalWriter)rowSetWriter);
          if ((tWriter instanceof CachedRowSetWriter)) {
            ((CachedRowSetWriter)tWriter).commit(this, updateOnInsert);
          } else {
            tWriter.commit();
          }
          j = 1;
        }
      }
      if (j == 1) {
        setOriginal();
      } else if (j == 0) {
        throw new SyncProviderException(resBundle.handleGetObject("cachedrowsetimpl.accfailed").toString());
      }
    }
    catch (SyncProviderException localSyncProviderException)
    {
      throw localSyncProviderException;
    }
    catch (SQLException localSQLException)
    {
      localSQLException.printStackTrace();
      throw new SyncProviderException(localSQLException.getMessage());
    }
    catch (SecurityException localSecurityException)
    {
      throw new SyncProviderException(localSecurityException.getMessage());
    }
  }
  
  public void acceptChanges(Connection paramConnection)
    throws SyncProviderException
  {
    setConnection(paramConnection);
    acceptChanges();
  }
  
  public void restoreOriginal()
    throws SQLException
  {
    Iterator localIterator = rvh.iterator();
    while (localIterator.hasNext())
    {
      Row localRow = (Row)localIterator.next();
      if (localRow.getInserted() == true)
      {
        localIterator.remove();
        numRows -= 1;
      }
      else
      {
        if (localRow.getDeleted() == true) {
          localRow.clearDeleted();
        }
        if (localRow.getUpdated() == true) {
          localRow.clearUpdated();
        }
      }
    }
    cursorPos = 0;
    notifyRowSetChanged();
  }
  
  public void release()
    throws SQLException
  {
    initContainer();
    notifyRowSetChanged();
  }
  
  public void undoDelete()
    throws SQLException
  {
    if (!getShowDeleted()) {
      return;
    }
    checkCursor();
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
    }
    Row localRow = (Row)getCurrentRow();
    if (localRow.getDeleted() == true)
    {
      localRow.clearDeleted();
      numDeleted -= 1;
      notifyRowChanged();
    }
  }
  
  public void undoInsert()
    throws SQLException
  {
    checkCursor();
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
    }
    Row localRow = (Row)getCurrentRow();
    if (localRow.getInserted() == true)
    {
      rvh.remove(cursorPos - 1);
      numRows -= 1;
      notifyRowChanged();
    }
    else
    {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.illegalop").toString());
    }
  }
  
  public void undoUpdate()
    throws SQLException
  {
    moveToCurrentRow();
    undoDelete();
    undoInsert();
  }
  
  public RowSet createShared()
    throws SQLException
  {
    RowSet localRowSet;
    try
    {
      localRowSet = (RowSet)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new SQLException(localCloneNotSupportedException.getMessage());
    }
    return localRowSet;
  }
  
  protected Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
  
  public CachedRowSet createCopy()
    throws SQLException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
      localObjectOutputStream.writeObject(this);
    }
    catch (IOException localIOException1)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { localIOException1.getMessage() }));
    }
    ObjectInputStream localObjectInputStream;
    try
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
      localObjectInputStream = new ObjectInputStream(localByteArrayInputStream);
    }
    catch (StreamCorruptedException localStreamCorruptedException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { localStreamCorruptedException.getMessage() }));
    }
    catch (IOException localIOException2)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { localIOException2.getMessage() }));
    }
    try
    {
      CachedRowSetImpl localCachedRowSetImpl = (CachedRowSetImpl)localObjectInputStream.readObject();
      resBundle = resBundle;
      return localCachedRowSetImpl;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { localClassNotFoundException.getMessage() }));
    }
    catch (OptionalDataException localOptionalDataException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { localOptionalDataException.getMessage() }));
    }
    catch (IOException localIOException3)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { localIOException3.getMessage() }));
    }
  }
  
  public CachedRowSet createCopySchema()
    throws SQLException
  {
    int i = numRows;
    numRows = 0;
    CachedRowSet localCachedRowSet = createCopy();
    numRows = i;
    return localCachedRowSet;
  }
  
  public CachedRowSet createCopyNoConstraints()
    throws SQLException
  {
    CachedRowSetImpl localCachedRowSetImpl = (CachedRowSetImpl)createCopy();
    localCachedRowSetImpl.initProperties();
    try
    {
      localCachedRowSetImpl.unsetMatchColumn(localCachedRowSetImpl.getMatchColumnIndexes());
    }
    catch (SQLException localSQLException1) {}
    try
    {
      localCachedRowSetImpl.unsetMatchColumn(localCachedRowSetImpl.getMatchColumnNames());
    }
    catch (SQLException localSQLException2) {}
    return localCachedRowSetImpl;
  }
  
  public Collection<?> toCollection()
    throws SQLException
  {
    TreeMap localTreeMap = new TreeMap();
    for (int i = 0; i < numRows; i++) {
      localTreeMap.put(Integer.valueOf(i), rvh.get(i));
    }
    return localTreeMap.values();
  }
  
  public Collection<?> toCollection(int paramInt)
    throws SQLException
  {
    int i = numRows;
    Vector localVector = new Vector(i);
    CachedRowSetImpl localCachedRowSetImpl = (CachedRowSetImpl)createCopy();
    while (i != 0)
    {
      localCachedRowSetImpl.next();
      localVector.add(localCachedRowSetImpl.getObject(paramInt));
      i--;
    }
    return localVector;
  }
  
  public Collection<?> toCollection(String paramString)
    throws SQLException
  {
    return toCollection(getColIdxByName(paramString));
  }
  
  public SyncProvider getSyncProvider()
    throws SQLException
  {
    return provider;
  }
  
  public void setSyncProvider(String paramString)
    throws SQLException
  {
    provider = SyncFactory.getInstance(paramString);
    rowSetReader = provider.getRowSetReader();
    rowSetWriter = provider.getRowSetWriter();
  }
  
  public void execute()
    throws SQLException
  {
    execute(null);
  }
  
  public boolean next()
    throws SQLException
  {
    if ((cursorPos < 0) || (cursorPos >= numRows + 1)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
    }
    boolean bool = internalNext();
    notifyCursorMoved();
    return bool;
  }
  
  protected boolean internalNext()
    throws SQLException
  {
    boolean bool = false;
    do
    {
      if (cursorPos < numRows)
      {
        cursorPos += 1;
        bool = true;
      }
      else if (cursorPos == numRows)
      {
        cursorPos += 1;
        bool = false;
        break;
      }
    } while ((!getShowDeleted()) && (rowDeleted() == true));
    if (bool == true) {
      absolutePos += 1;
    } else {
      absolutePos = 0;
    }
    return bool;
  }
  
  public void close()
    throws SQLException
  {
    cursorPos = 0;
    absolutePos = 0;
    numRows = 0;
    numDeleted = 0;
    initProperties();
    rvh.clear();
  }
  
  public boolean wasNull()
    throws SQLException
  {
    return lastValueNull;
  }
  
  private void setLastValueNull(boolean paramBoolean)
  {
    lastValueNull = paramBoolean;
  }
  
  private void checkIndex(int paramInt)
    throws SQLException
  {
    if ((paramInt < 1) || (paramInt > RowSetMD.getColumnCount())) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString());
    }
  }
  
  private void checkCursor()
    throws SQLException
  {
    if ((isAfterLast() == true) || (isBeforeFirst() == true)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
    }
  }
  
  private int getColIdxByName(String paramString)
    throws SQLException
  {
    RowSetMD = ((RowSetMetaDataImpl)getMetaData());
    int i = RowSetMD.getColumnCount();
    for (int j = 1; j <= i; j++)
    {
      String str = RowSetMD.getColumnName(j);
      if ((str != null) && (paramString.equalsIgnoreCase(str))) {
        return j;
      }
    }
    throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalcolnm").toString());
  }
  
  protected BaseRow getCurrentRow()
  {
    if (onInsertRow == true) {
      return insertRow;
    }
    return (BaseRow)rvh.get(cursorPos - 1);
  }
  
  protected void removeCurrentRow()
  {
    ((Row)getCurrentRow()).setDeleted();
    rvh.remove(cursorPos - 1);
    numRows -= 1;
  }
  
  public String getString(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    return localObject.toString();
  }
  
  public boolean getBoolean(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return false;
    }
    if ((localObject instanceof Boolean)) {
      return ((Boolean)localObject).booleanValue();
    }
    try
    {
      return Double.compare(Double.parseDouble(localObject.toString()), 0.0D) != 0;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.boolfail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
    }
  }
  
  public byte getByte(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return 0;
    }
    try
    {
      return Byte.valueOf(localObject.toString()).byteValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.bytefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
    }
  }
  
  public short getShort(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return 0;
    }
    try
    {
      return Short.valueOf(localObject.toString().trim()).shortValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.shortfail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
    }
  }
  
  public int getInt(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return 0;
    }
    try
    {
      return Integer.valueOf(localObject.toString().trim()).intValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.intfail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
    }
  }
  
  public long getLong(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return 0L;
    }
    try
    {
      return Long.valueOf(localObject.toString().trim()).longValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.longfail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
    }
  }
  
  public float getFloat(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return 0.0F;
    }
    try
    {
      return new Float(localObject.toString()).floatValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.floatfail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
    }
  }
  
  public double getDouble(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return 0.0D;
    }
    try
    {
      return new Double(localObject.toString().trim()).doubleValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.doublefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
    }
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkIndex(paramInt1);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt1);
    if (localObject == null)
    {
      setLastValueNull(true);
      return new BigDecimal(0);
    }
    BigDecimal localBigDecimal1 = getBigDecimal(paramInt1);
    BigDecimal localBigDecimal2 = localBigDecimal1.setScale(paramInt2);
    return localBigDecimal2;
  }
  
  public byte[] getBytes(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (!isBinary(RowSetMD.getColumnType(paramInt))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    return (byte[])getCurrentRow().getColumnObject(paramInt);
  }
  
  public java.sql.Date getDate(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    long l;
    switch (RowSetMD.getColumnType(paramInt))
    {
    case 91: 
      l = ((java.sql.Date)localObject).getTime();
      return new java.sql.Date(l);
    case 93: 
      l = ((Timestamp)localObject).getTime();
      return new java.sql.Date(l);
    case -1: 
    case 1: 
    case 12: 
      try
      {
        DateFormat localDateFormat = DateFormat.getDateInstance();
        return (java.sql.Date)localDateFormat.parse(localObject.toString());
      }
      catch (ParseException localParseException)
      {
        throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
      }
    }
    throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
  }
  
  public Time getTime(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    switch (RowSetMD.getColumnType(paramInt))
    {
    case 92: 
      return (Time)localObject;
    case 93: 
      long l = ((Timestamp)localObject).getTime();
      return new Time(l);
    case -1: 
    case 1: 
    case 12: 
      try
      {
        DateFormat localDateFormat = DateFormat.getTimeInstance();
        return (Time)localDateFormat.parse(localObject.toString());
      }
      catch (ParseException localParseException)
      {
        throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
      }
    }
    throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
  }
  
  public Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    long l;
    switch (RowSetMD.getColumnType(paramInt))
    {
    case 93: 
      return (Timestamp)localObject;
    case 92: 
      l = ((Time)localObject).getTime();
      return new Timestamp(l);
    case 91: 
      l = ((java.sql.Date)localObject).getTime();
      return new Timestamp(l);
    case -1: 
    case 1: 
    case 12: 
      try
      {
        DateFormat localDateFormat = DateFormat.getTimeInstance();
        return (Timestamp)localDateFormat.parse(localObject.toString());
      }
      catch (ParseException localParseException)
      {
        throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
      }
    }
    throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
  }
  
  public InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    asciiStream = null;
    checkIndex(paramInt);
    checkCursor();
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      lastValueNull = true;
      return null;
    }
    try
    {
      if (isString(RowSetMD.getColumnType(paramInt))) {
        asciiStream = new ByteArrayInputStream(((String)localObject).getBytes("ASCII"));
      } else {
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      }
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new SQLException(localUnsupportedEncodingException.getMessage());
    }
    return asciiStream;
  }
  
  @Deprecated
  public InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    unicodeStream = null;
    checkIndex(paramInt);
    checkCursor();
    if ((!isBinary(RowSetMD.getColumnType(paramInt))) && (!isString(RowSetMD.getColumnType(paramInt)))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      lastValueNull = true;
      return null;
    }
    unicodeStream = new StringBufferInputStream(localObject.toString());
    return unicodeStream;
  }
  
  public InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    binaryStream = null;
    checkIndex(paramInt);
    checkCursor();
    if (!isBinary(RowSetMD.getColumnType(paramInt))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      lastValueNull = true;
      return null;
    }
    binaryStream = new ByteArrayInputStream((byte[])localObject);
    return binaryStream;
  }
  
  public String getString(String paramString)
    throws SQLException
  {
    return getString(getColIdxByName(paramString));
  }
  
  public boolean getBoolean(String paramString)
    throws SQLException
  {
    return getBoolean(getColIdxByName(paramString));
  }
  
  public byte getByte(String paramString)
    throws SQLException
  {
    return getByte(getColIdxByName(paramString));
  }
  
  public short getShort(String paramString)
    throws SQLException
  {
    return getShort(getColIdxByName(paramString));
  }
  
  public int getInt(String paramString)
    throws SQLException
  {
    return getInt(getColIdxByName(paramString));
  }
  
  public long getLong(String paramString)
    throws SQLException
  {
    return getLong(getColIdxByName(paramString));
  }
  
  public float getFloat(String paramString)
    throws SQLException
  {
    return getFloat(getColIdxByName(paramString));
  }
  
  public double getDouble(String paramString)
    throws SQLException
  {
    return getDouble(getColIdxByName(paramString));
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(String paramString, int paramInt)
    throws SQLException
  {
    return getBigDecimal(getColIdxByName(paramString), paramInt);
  }
  
  public byte[] getBytes(String paramString)
    throws SQLException
  {
    return getBytes(getColIdxByName(paramString));
  }
  
  public java.sql.Date getDate(String paramString)
    throws SQLException
  {
    return getDate(getColIdxByName(paramString));
  }
  
  public Time getTime(String paramString)
    throws SQLException
  {
    return getTime(getColIdxByName(paramString));
  }
  
  public Timestamp getTimestamp(String paramString)
    throws SQLException
  {
    return getTimestamp(getColIdxByName(paramString));
  }
  
  public InputStream getAsciiStream(String paramString)
    throws SQLException
  {
    return getAsciiStream(getColIdxByName(paramString));
  }
  
  @Deprecated
  public InputStream getUnicodeStream(String paramString)
    throws SQLException
  {
    return getUnicodeStream(getColIdxByName(paramString));
  }
  
  public InputStream getBinaryStream(String paramString)
    throws SQLException
  {
    return getBinaryStream(getColIdxByName(paramString));
  }
  
  public SQLWarning getWarnings()
  {
    return sqlwarn;
  }
  
  public void clearWarnings()
  {
    sqlwarn = null;
  }
  
  public String getCursorName()
    throws SQLException
  {
    throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.posupdate").toString());
  }
  
  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    return RowSetMD;
  }
  
  public Object getObject(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    if ((localObject instanceof Struct))
    {
      Struct localStruct = (Struct)localObject;
      Map localMap = getTypeMap();
      Class localClass = (Class)localMap.get(localStruct.getSQLTypeName());
      if (localClass != null)
      {
        SQLData localSQLData = null;
        try
        {
          localSQLData = (SQLData)ReflectUtil.newInstance(localClass);
        }
        catch (Exception localException)
        {
          throw new SQLException("Unable to Instantiate: ", localException);
        }
        Object[] arrayOfObject = localStruct.getAttributes(localMap);
        SQLInputImpl localSQLInputImpl = new SQLInputImpl(arrayOfObject, localMap);
        localSQLData.readSQL(localSQLInputImpl, localStruct.getSQLTypeName());
        return localSQLData;
      }
    }
    return localObject;
  }
  
  public Object getObject(String paramString)
    throws SQLException
  {
    return getObject(getColIdxByName(paramString));
  }
  
  public int findColumn(String paramString)
    throws SQLException
  {
    return getColIdxByName(paramString);
  }
  
  public Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject;
    if (isBinary(RowSetMD.getColumnType(paramInt)))
    {
      localObject = getCurrentRow().getColumnObject(paramInt);
      if (localObject == null)
      {
        lastValueNull = true;
        return null;
      }
      charStream = new InputStreamReader(new ByteArrayInputStream((byte[])localObject));
    }
    else if (isString(RowSetMD.getColumnType(paramInt)))
    {
      localObject = getCurrentRow().getColumnObject(paramInt);
      if (localObject == null)
      {
        lastValueNull = true;
        return null;
      }
      charStream = new StringReader(localObject.toString());
    }
    else
    {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    return charStream;
  }
  
  public Reader getCharacterStream(String paramString)
    throws SQLException
  {
    return getCharacterStream(getColIdxByName(paramString));
  }
  
  public BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    try
    {
      return new BigDecimal(localObject.toString().trim());
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.doublefail").toString(), new Object[] { localObject.toString().trim(), Integer.valueOf(paramInt) }));
    }
  }
  
  public BigDecimal getBigDecimal(String paramString)
    throws SQLException
  {
    return getBigDecimal(getColIdxByName(paramString));
  }
  
  public int size()
  {
    return numRows;
  }
  
  public boolean isBeforeFirst()
    throws SQLException
  {
    return (cursorPos == 0) && (numRows > 0);
  }
  
  public boolean isAfterLast()
    throws SQLException
  {
    return (cursorPos == numRows + 1) && (numRows > 0);
  }
  
  public boolean isFirst()
    throws SQLException
  {
    int i = cursorPos;
    int j = absolutePos;
    internalFirst();
    if (cursorPos == i) {
      return true;
    }
    cursorPos = i;
    absolutePos = j;
    return false;
  }
  
  public boolean isLast()
    throws SQLException
  {
    int i = cursorPos;
    int j = absolutePos;
    boolean bool = getShowDeleted();
    setShowDeleted(true);
    internalLast();
    if (cursorPos == i)
    {
      setShowDeleted(bool);
      return true;
    }
    setShowDeleted(bool);
    cursorPos = i;
    absolutePos = j;
    return false;
  }
  
  public void beforeFirst()
    throws SQLException
  {
    if (getType() == 1003) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.beforefirst").toString());
    }
    cursorPos = 0;
    absolutePos = 0;
    notifyCursorMoved();
  }
  
  public void afterLast()
    throws SQLException
  {
    if (numRows > 0)
    {
      cursorPos = (numRows + 1);
      absolutePos = 0;
      notifyCursorMoved();
    }
  }
  
  public boolean first()
    throws SQLException
  {
    if (getType() == 1003) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.first").toString());
    }
    boolean bool = internalFirst();
    notifyCursorMoved();
    return bool;
  }
  
  protected boolean internalFirst()
    throws SQLException
  {
    boolean bool = false;
    if (numRows > 0)
    {
      cursorPos = 1;
      if ((!getShowDeleted()) && (rowDeleted() == true)) {
        bool = internalNext();
      } else {
        bool = true;
      }
    }
    if (bool == true) {
      absolutePos = 1;
    } else {
      absolutePos = 0;
    }
    return bool;
  }
  
  public boolean last()
    throws SQLException
  {
    if (getType() == 1003) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.last").toString());
    }
    boolean bool = internalLast();
    notifyCursorMoved();
    return bool;
  }
  
  protected boolean internalLast()
    throws SQLException
  {
    boolean bool = false;
    if (numRows > 0)
    {
      cursorPos = numRows;
      if ((!getShowDeleted()) && (rowDeleted() == true)) {
        bool = internalPrevious();
      } else {
        bool = true;
      }
    }
    if (bool == true) {
      absolutePos = (numRows - numDeleted);
    } else {
      absolutePos = 0;
    }
    return bool;
  }
  
  public int getRow()
    throws SQLException
  {
    if ((numRows > 0) && (cursorPos > 0) && (cursorPos < numRows + 1) && (!getShowDeleted()) && (!rowDeleted())) {
      return absolutePos;
    }
    if (getShowDeleted() == true) {
      return cursorPos;
    }
    return 0;
  }
  
  public boolean absolute(int paramInt)
    throws SQLException
  {
    if ((paramInt == 0) || (getType() == 1003)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.absolute").toString());
    }
    if (paramInt > 0)
    {
      if (paramInt > numRows)
      {
        afterLast();
        return false;
      }
      if (absolutePos <= 0) {
        internalFirst();
      }
    }
    else
    {
      if (cursorPos + paramInt < 0)
      {
        beforeFirst();
        return false;
      }
      if (absolutePos >= 0) {
        internalLast();
      }
    }
    while (absolutePos != paramInt) {
      if (absolutePos < paramInt)
      {
        if (!internalNext()) {
          break;
        }
      }
      else if (!internalPrevious()) {
        break;
      }
    }
    notifyCursorMoved();
    return (!isAfterLast()) && (!isBeforeFirst());
  }
  
  public boolean relative(int paramInt)
    throws SQLException
  {
    if ((numRows == 0) || (isBeforeFirst()) || (isAfterLast()) || (getType() == 1003)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.relative").toString());
    }
    if (paramInt == 0) {
      return true;
    }
    int i;
    if (paramInt > 0)
    {
      if (cursorPos + paramInt > numRows) {
        afterLast();
      } else {
        for (i = 0; (i < paramInt) && (internalNext()); i++) {}
      }
    }
    else if (cursorPos + paramInt < 0) {
      beforeFirst();
    } else {
      for (i = paramInt; (i < 0) && (internalPrevious()); i++) {}
    }
    notifyCursorMoved();
    return (!isAfterLast()) && (!isBeforeFirst());
  }
  
  public boolean previous()
    throws SQLException
  {
    if (getType() == 1003) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.last").toString());
    }
    if ((cursorPos < 0) || (cursorPos > numRows + 1)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
    }
    boolean bool = internalPrevious();
    notifyCursorMoved();
    return bool;
  }
  
  protected boolean internalPrevious()
    throws SQLException
  {
    boolean bool = false;
    do
    {
      if (cursorPos > 1)
      {
        cursorPos -= 1;
        bool = true;
      }
      else if (cursorPos == 1)
      {
        cursorPos -= 1;
        bool = false;
        break;
      }
    } while ((!getShowDeleted()) && (rowDeleted() == true));
    if (bool == true) {
      absolutePos -= 1;
    } else {
      absolutePos = 0;
    }
    return bool;
  }
  
  public boolean rowUpdated()
    throws SQLException
  {
    checkCursor();
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
    }
    return ((Row)getCurrentRow()).getUpdated();
  }
  
  public boolean columnUpdated(int paramInt)
    throws SQLException
  {
    checkCursor();
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
    }
    return ((Row)getCurrentRow()).getColUpdated(paramInt - 1);
  }
  
  public boolean columnUpdated(String paramString)
    throws SQLException
  {
    return columnUpdated(getColIdxByName(paramString));
  }
  
  public boolean rowInserted()
    throws SQLException
  {
    checkCursor();
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
    }
    return ((Row)getCurrentRow()).getInserted();
  }
  
  public boolean rowDeleted()
    throws SQLException
  {
    if ((isAfterLast() == true) || (isBeforeFirst() == true) || (onInsertRow == true)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
    }
    return ((Row)getCurrentRow()).getDeleted();
  }
  
  private boolean isNumeric(int paramInt)
  {
    switch (paramInt)
    {
    case -7: 
    case -6: 
    case -5: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
      return true;
    }
    return false;
  }
  
  private boolean isString(int paramInt)
  {
    switch (paramInt)
    {
    case -1: 
    case 1: 
    case 12: 
      return true;
    }
    return false;
  }
  
  private boolean isBinary(int paramInt)
  {
    switch (paramInt)
    {
    case -4: 
    case -3: 
    case -2: 
      return true;
    }
    return false;
  }
  
  private boolean isTemporal(int paramInt)
  {
    switch (paramInt)
    {
    case 91: 
    case 92: 
    case 93: 
      return true;
    }
    return false;
  }
  
  private boolean isBoolean(int paramInt)
  {
    switch (paramInt)
    {
    case -7: 
    case 16: 
      return true;
    }
    return false;
  }
  
  private Object convertNumeric(Object paramObject, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramInt1 == paramInt2) {
      return paramObject;
    }
    if ((!isNumeric(paramInt2)) && (!isString(paramInt2))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    }
    try
    {
      switch (paramInt2)
      {
      case -7: 
        Integer localInteger = Integer.valueOf(paramObject.toString().trim());
        return localInteger.equals(Integer.valueOf(0)) ? Boolean.valueOf(false) : Boolean.valueOf(true);
      case -6: 
        return Byte.valueOf(paramObject.toString().trim());
      case 5: 
        return Short.valueOf(paramObject.toString().trim());
      case 4: 
        return Integer.valueOf(paramObject.toString().trim());
      case -5: 
        return Long.valueOf(paramObject.toString().trim());
      case 2: 
      case 3: 
        return new BigDecimal(paramObject.toString().trim());
      case 6: 
      case 7: 
        return new Float(paramObject.toString().trim());
      case 8: 
        return new Double(paramObject.toString().trim());
      case -1: 
      case 1: 
      case 12: 
        return paramObject.toString();
      }
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    }
  }
  
  private Object convertTemporal(Object paramObject, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramInt1 == paramInt2) {
      return paramObject;
    }
    if ((isNumeric(paramInt2) == true) || ((!isString(paramInt2)) && (!isTemporal(paramInt2)))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    try
    {
      switch (paramInt2)
      {
      case 91: 
        if (paramInt1 == 93) {
          return new java.sql.Date(((Timestamp)paramObject).getTime());
        }
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      case 93: 
        if (paramInt1 == 92) {
          return new Timestamp(((Time)paramObject).getTime());
        }
        return new Timestamp(((java.sql.Date)paramObject).getTime());
      case 92: 
        if (paramInt1 == 93) {
          return new Time(((Timestamp)paramObject).getTime());
        }
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      case -1: 
      case 1: 
      case 12: 
        return paramObject.toString();
      }
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
  }
  
  private Object convertBoolean(Object paramObject, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramInt1 == paramInt2) {
      return paramObject;
    }
    if ((isNumeric(paramInt2) == true) || ((!isString(paramInt2)) && (!isBoolean(paramInt2)))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    try
    {
      switch (paramInt2)
      {
      case -7: 
        Integer localInteger = Integer.valueOf(paramObject.toString().trim());
        return localInteger.equals(Integer.valueOf(0)) ? Boolean.valueOf(false) : Boolean.valueOf(true);
      case 16: 
        return Boolean.valueOf(paramObject.toString().trim());
      }
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    }
  }
  
  public void updateNull(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    BaseRow localBaseRow = getCurrentRow();
    localBaseRow.setColumnObject(paramInt, null);
  }
  
  public void updateBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertBoolean(Boolean.valueOf(paramBoolean), -7, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateByte(int paramInt, byte paramByte)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertNumeric(Byte.valueOf(paramByte), -6, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateShort(int paramInt, short paramShort)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertNumeric(Short.valueOf(paramShort), 5, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkIndex(paramInt1);
    checkCursor();
    Object localObject = convertNumeric(Integer.valueOf(paramInt2), 4, RowSetMD.getColumnType(paramInt1));
    getCurrentRow().setColumnObject(paramInt1, localObject);
  }
  
  public void updateLong(int paramInt, long paramLong)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertNumeric(Long.valueOf(paramLong), -5, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertNumeric(Float.valueOf(paramFloat), 7, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertNumeric(Double.valueOf(paramDouble), 8, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertNumeric(paramBigDecimal, 2, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateString(int paramInt, String paramString)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    getCurrentRow().setColumnObject(paramInt, paramString);
  }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (!isBinary(RowSetMD.getColumnType(paramInt))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    getCurrentRow().setColumnObject(paramInt, paramArrayOfByte);
  }
  
  public void updateDate(int paramInt, java.sql.Date paramDate)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertTemporal(paramDate, 91, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateTime(int paramInt, Time paramTime)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertTemporal(paramTime, 92, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    Object localObject = convertTemporal(paramTimestamp, 93, RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, localObject);
  }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkIndex(paramInt1);
    checkCursor();
    if ((!isString(RowSetMD.getColumnType(paramInt1))) && (!isBinary(RowSetMD.getColumnType(paramInt1)))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    byte[] arrayOfByte = new byte[paramInt2];
    try
    {
      int i = 0;
      do
      {
        i += paramInputStream.read(arrayOfByte, i, paramInt2 - i);
      } while (i != paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.asciistream").toString());
    }
    String str = new String(arrayOfByte);
    getCurrentRow().setColumnObject(paramInt1, str);
  }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkIndex(paramInt1);
    checkCursor();
    if (!isBinary(RowSetMD.getColumnType(paramInt1))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    byte[] arrayOfByte = new byte[paramInt2];
    try
    {
      int i = 0;
      do
      {
        i += paramInputStream.read(arrayOfByte, i, paramInt2 - i);
      } while (i != -1);
    }
    catch (IOException localIOException)
    {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
    }
    getCurrentRow().setColumnObject(paramInt1, arrayOfByte);
  }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    checkIndex(paramInt1);
    checkCursor();
    if ((!isString(RowSetMD.getColumnType(paramInt1))) && (!isBinary(RowSetMD.getColumnType(paramInt1)))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    char[] arrayOfChar = new char[paramInt2];
    try
    {
      int i = 0;
      do
      {
        i += paramReader.read(arrayOfChar, i, paramInt2 - i);
      } while (i != paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
    }
    String str = new String(arrayOfChar);
    getCurrentRow().setColumnObject(paramInt1, str);
  }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    checkIndex(paramInt1);
    checkCursor();
    int i = RowSetMD.getColumnType(paramInt1);
    if ((i == 3) || (i == 2)) {
      ((BigDecimal)paramObject).setScale(paramInt2);
    }
    getCurrentRow().setColumnObject(paramInt1, paramObject);
  }
  
  public void updateObject(int paramInt, Object paramObject)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    getCurrentRow().setColumnObject(paramInt, paramObject);
  }
  
  public void updateNull(String paramString)
    throws SQLException
  {
    updateNull(getColIdxByName(paramString));
  }
  
  public void updateBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    updateBoolean(getColIdxByName(paramString), paramBoolean);
  }
  
  public void updateByte(String paramString, byte paramByte)
    throws SQLException
  {
    updateByte(getColIdxByName(paramString), paramByte);
  }
  
  public void updateShort(String paramString, short paramShort)
    throws SQLException
  {
    updateShort(getColIdxByName(paramString), paramShort);
  }
  
  public void updateInt(String paramString, int paramInt)
    throws SQLException
  {
    updateInt(getColIdxByName(paramString), paramInt);
  }
  
  public void updateLong(String paramString, long paramLong)
    throws SQLException
  {
    updateLong(getColIdxByName(paramString), paramLong);
  }
  
  public void updateFloat(String paramString, float paramFloat)
    throws SQLException
  {
    updateFloat(getColIdxByName(paramString), paramFloat);
  }
  
  public void updateDouble(String paramString, double paramDouble)
    throws SQLException
  {
    updateDouble(getColIdxByName(paramString), paramDouble);
  }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    updateBigDecimal(getColIdxByName(paramString), paramBigDecimal);
  }
  
  public void updateString(String paramString1, String paramString2)
    throws SQLException
  {
    updateString(getColIdxByName(paramString1), paramString2);
  }
  
  public void updateBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    updateBytes(getColIdxByName(paramString), paramArrayOfByte);
  }
  
  public void updateDate(String paramString, java.sql.Date paramDate)
    throws SQLException
  {
    updateDate(getColIdxByName(paramString), paramDate);
  }
  
  public void updateTime(String paramString, Time paramTime)
    throws SQLException
  {
    updateTime(getColIdxByName(paramString), paramTime);
  }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    updateTimestamp(getColIdxByName(paramString), paramTimestamp);
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    updateAsciiStream(getColIdxByName(paramString), paramInputStream, paramInt);
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    updateBinaryStream(getColIdxByName(paramString), paramInputStream, paramInt);
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    updateCharacterStream(getColIdxByName(paramString), paramReader, paramInt);
  }
  
  public void updateObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    updateObject(getColIdxByName(paramString), paramObject, paramInt);
  }
  
  public void updateObject(String paramString, Object paramObject)
    throws SQLException
  {
    updateObject(getColIdxByName(paramString), paramObject);
  }
  
  public void insertRow()
    throws SQLException
  {
    if ((!onInsertRow) || (!insertRow.isCompleteRow(RowSetMD))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.failedins").toString());
    }
    Object[] arrayOfObject = getParams();
    for (int j = 0; j < arrayOfObject.length; j++) {
      insertRow.setColumnObject(j + 1, arrayOfObject[j]);
    }
    Row localRow = new Row(RowSetMD.getColumnCount(), insertRow.getOrigRow());
    localRow.setInserted();
    int i;
    if ((currentRow >= numRows) || (currentRow < 0)) {
      i = numRows;
    } else {
      i = currentRow;
    }
    rvh.add(i, localRow);
    numRows += 1;
    notifyRowChanged();
  }
  
  public void updateRow()
    throws SQLException
  {
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.updateins").toString());
    }
    ((Row)getCurrentRow()).setUpdated();
    notifyRowChanged();
  }
  
  public void deleteRow()
    throws SQLException
  {
    checkCursor();
    ((Row)getCurrentRow()).setDeleted();
    numDeleted += 1;
    notifyRowChanged();
  }
  
  public void refreshRow()
    throws SQLException
  {
    checkCursor();
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
    }
    Row localRow = (Row)getCurrentRow();
    localRow.clearUpdated();
  }
  
  public void cancelRowUpdates()
    throws SQLException
  {
    checkCursor();
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString());
    }
    Row localRow = (Row)getCurrentRow();
    if (localRow.getUpdated() == true)
    {
      localRow.clearUpdated();
      notifyRowChanged();
    }
  }
  
  public void moveToInsertRow()
    throws SQLException
  {
    if (getConcurrency() == 1007) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.movetoins").toString());
    }
    if (insertRow == null)
    {
      if (RowSetMD == null) {
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.movetoins1").toString());
      }
      int i = RowSetMD.getColumnCount();
      if (i > 0) {
        insertRow = new InsertRow(i);
      } else {
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.movetoins2").toString());
      }
    }
    onInsertRow = true;
    currentRow = cursorPos;
    cursorPos = -1;
    insertRow.initInsertRow();
  }
  
  public void moveToCurrentRow()
    throws SQLException
  {
    if (!onInsertRow) {
      return;
    }
    cursorPos = currentRow;
    onInsertRow = false;
  }
  
  public Statement getStatement()
    throws SQLException
  {
    return null;
  }
  
  public Object getObject(int paramInt, Map<String, Class<?>> paramMap)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    if ((localObject instanceof Struct))
    {
      Struct localStruct = (Struct)localObject;
      Class localClass = (Class)paramMap.get(localStruct.getSQLTypeName());
      if (localClass != null)
      {
        SQLData localSQLData = null;
        try
        {
          localSQLData = (SQLData)ReflectUtil.newInstance(localClass);
        }
        catch (Exception localException)
        {
          throw new SQLException("Unable to Instantiate: ", localException);
        }
        Object[] arrayOfObject = localStruct.getAttributes(paramMap);
        SQLInputImpl localSQLInputImpl = new SQLInputImpl(arrayOfObject, paramMap);
        localSQLData.readSQL(localSQLInputImpl, localStruct.getSQLTypeName());
        return localSQLData;
      }
    }
    return localObject;
  }
  
  public Ref getRef(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (RowSetMD.getColumnType(paramInt) != 2006) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    setLastValueNull(false);
    Ref localRef = (Ref)getCurrentRow().getColumnObject(paramInt);
    if (localRef == null)
    {
      setLastValueNull(true);
      return null;
    }
    return localRef;
  }
  
  public Blob getBlob(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (RowSetMD.getColumnType(paramInt) != 2004)
    {
      System.out.println(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.type").toString(), new Object[] { Integer.valueOf(RowSetMD.getColumnType(paramInt)) }));
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    setLastValueNull(false);
    Blob localBlob = (Blob)getCurrentRow().getColumnObject(paramInt);
    if (localBlob == null)
    {
      setLastValueNull(true);
      return null;
    }
    return localBlob;
  }
  
  public Clob getClob(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (RowSetMD.getColumnType(paramInt) != 2005)
    {
      System.out.println(MessageFormat.format(resBundle.handleGetObject("cachedrowsetimpl.type").toString(), new Object[] { Integer.valueOf(RowSetMD.getColumnType(paramInt)) }));
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    setLastValueNull(false);
    Clob localClob = (Clob)getCurrentRow().getColumnObject(paramInt);
    if (localClob == null)
    {
      setLastValueNull(true);
      return null;
    }
    return localClob;
  }
  
  public Array getArray(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (RowSetMD.getColumnType(paramInt) != 2003) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    setLastValueNull(false);
    Array localArray = (Array)getCurrentRow().getColumnObject(paramInt);
    if (localArray == null)
    {
      setLastValueNull(true);
      return null;
    }
    return localArray;
  }
  
  public Object getObject(String paramString, Map<String, Class<?>> paramMap)
    throws SQLException
  {
    return getObject(getColIdxByName(paramString), paramMap);
  }
  
  public Ref getRef(String paramString)
    throws SQLException
  {
    return getRef(getColIdxByName(paramString));
  }
  
  public Blob getBlob(String paramString)
    throws SQLException
  {
    return getBlob(getColIdxByName(paramString));
  }
  
  public Clob getClob(String paramString)
    throws SQLException
  {
    return getClob(getColIdxByName(paramString));
  }
  
  public Array getArray(String paramString)
    throws SQLException
  {
    return getArray(getColIdxByName(paramString));
  }
  
  public java.sql.Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    localObject = convertTemporal(localObject, RowSetMD.getColumnType(paramInt), 91);
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime((java.util.Date)localObject);
    paramCalendar.set(1, localCalendar.get(1));
    paramCalendar.set(2, localCalendar.get(2));
    paramCalendar.set(5, localCalendar.get(5));
    return new java.sql.Date(paramCalendar.getTime().getTime());
  }
  
  public java.sql.Date getDate(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getDate(getColIdxByName(paramString), paramCalendar);
  }
  
  public Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    localObject = convertTemporal(localObject, RowSetMD.getColumnType(paramInt), 92);
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime((java.util.Date)localObject);
    paramCalendar.set(11, localCalendar.get(11));
    paramCalendar.set(12, localCalendar.get(12));
    paramCalendar.set(13, localCalendar.get(13));
    return new Time(paramCalendar.getTime().getTime());
  }
  
  public Time getTime(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getTime(getColIdxByName(paramString), paramCalendar);
  }
  
  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object localObject = getCurrentRow().getColumnObject(paramInt);
    if (localObject == null)
    {
      setLastValueNull(true);
      return null;
    }
    localObject = convertTemporal(localObject, RowSetMD.getColumnType(paramInt), 93);
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime((java.util.Date)localObject);
    paramCalendar.set(1, localCalendar.get(1));
    paramCalendar.set(2, localCalendar.get(2));
    paramCalendar.set(5, localCalendar.get(5));
    paramCalendar.set(11, localCalendar.get(11));
    paramCalendar.set(12, localCalendar.get(12));
    paramCalendar.set(13, localCalendar.get(13));
    return new Timestamp(paramCalendar.getTime().getTime());
  }
  
  public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getTimestamp(getColIdxByName(paramString), paramCalendar);
  }
  
  public Connection getConnection()
    throws SQLException
  {
    return conn;
  }
  
  public void setMetaData(RowSetMetaData paramRowSetMetaData)
    throws SQLException
  {
    RowSetMD = ((RowSetMetaDataImpl)paramRowSetMetaData);
  }
  
  public ResultSet getOriginal()
    throws SQLException
  {
    CachedRowSetImpl localCachedRowSetImpl = new CachedRowSetImpl();
    RowSetMD = RowSetMD;
    numRows = numRows;
    cursorPos = 0;
    int i = RowSetMD.getColumnCount();
    Iterator localIterator = rvh.iterator();
    while (localIterator.hasNext())
    {
      Row localRow = new Row(i, ((Row)localIterator.next()).getOrigRow());
      rvh.add(localRow);
    }
    return localCachedRowSetImpl;
  }
  
  public ResultSet getOriginalRow()
    throws SQLException
  {
    CachedRowSetImpl localCachedRowSetImpl = new CachedRowSetImpl();
    RowSetMD = RowSetMD;
    numRows = 1;
    cursorPos = 0;
    localCachedRowSetImpl.setTypeMap(getTypeMap());
    Row localRow = new Row(RowSetMD.getColumnCount(), getCurrentRow().getOrigRow());
    rvh.add(localRow);
    return localCachedRowSetImpl;
  }
  
  public void setOriginalRow()
    throws SQLException
  {
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
    }
    Row localRow = (Row)getCurrentRow();
    makeRowOriginal(localRow);
    if (localRow.getDeleted() == true) {
      removeCurrentRow();
    }
  }
  
  private void makeRowOriginal(Row paramRow)
  {
    if (paramRow.getInserted() == true) {
      paramRow.clearInserted();
    }
    if (paramRow.getUpdated() == true) {
      paramRow.moveCurrentToOrig();
    }
  }
  
  public void setOriginal()
    throws SQLException
  {
    Iterator localIterator = rvh.iterator();
    while (localIterator.hasNext())
    {
      Row localRow = (Row)localIterator.next();
      makeRowOriginal(localRow);
      if (localRow.getDeleted() == true)
      {
        localIterator.remove();
        numRows -= 1;
      }
    }
    numDeleted = 0;
    notifyRowSetChanged();
  }
  
  public String getTableName()
    throws SQLException
  {
    return tableName;
  }
  
  public void setTableName(String paramString)
    throws SQLException
  {
    if (paramString == null) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.tablename").toString());
    }
    tableName = paramString;
  }
  
  public int[] getKeyColumns()
    throws SQLException
  {
    int[] arrayOfInt = keyCols;
    return arrayOfInt == null ? null : Arrays.copyOf(arrayOfInt, arrayOfInt.length);
  }
  
  public void setKeyColumns(int[] paramArrayOfInt)
    throws SQLException
  {
    int i = 0;
    if (RowSetMD != null)
    {
      i = RowSetMD.getColumnCount();
      if (paramArrayOfInt.length > i) {
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.keycols").toString());
      }
    }
    keyCols = new int[paramArrayOfInt.length];
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      if ((RowSetMD != null) && ((paramArrayOfInt[j] <= 0) || (paramArrayOfInt[j] > i))) {
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString() + paramArrayOfInt[j]);
      }
      keyCols[j] = paramArrayOfInt[j];
    }
  }
  
  public void updateRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    getCurrentRow().setColumnObject(paramInt, new SerialRef(paramRef));
  }
  
  public void updateRef(String paramString, Ref paramRef)
    throws SQLException
  {
    updateRef(getColIdxByName(paramString), paramRef);
  }
  
  public void updateClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (dbmslocatorsUpdateCopy) {
      getCurrentRow().setColumnObject(paramInt, new SerialClob(paramClob));
    } else {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
    }
  }
  
  public void updateClob(String paramString, Clob paramClob)
    throws SQLException
  {
    updateClob(getColIdxByName(paramString), paramClob);
  }
  
  public void updateBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (dbmslocatorsUpdateCopy) {
      getCurrentRow().setColumnObject(paramInt, new SerialBlob(paramBlob));
    } else {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
    }
  }
  
  public void updateBlob(String paramString, Blob paramBlob)
    throws SQLException
  {
    updateBlob(getColIdxByName(paramString), paramBlob);
  }
  
  public void updateArray(int paramInt, Array paramArray)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    getCurrentRow().setColumnObject(paramInt, new SerialArray(paramArray));
  }
  
  public void updateArray(String paramString, Array paramArray)
    throws SQLException
  {
    updateArray(getColIdxByName(paramString), paramArray);
  }
  
  public URL getURL(int paramInt)
    throws SQLException
  {
    checkIndex(paramInt);
    checkCursor();
    if (RowSetMD.getColumnType(paramInt) != 70) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    }
    setLastValueNull(false);
    URL localURL = (URL)getCurrentRow().getColumnObject(paramInt);
    if (localURL == null)
    {
      setLastValueNull(true);
      return null;
    }
    return localURL;
  }
  
  public URL getURL(String paramString)
    throws SQLException
  {
    return getURL(getColIdxByName(paramString));
  }
  
  public RowSetWarning getRowSetWarnings()
  {
    try
    {
      notifyCursorMoved();
    }
    catch (SQLException localSQLException) {}
    return rowsetWarning;
  }
  
  private String buildTableName(String paramString)
    throws SQLException
  {
    Object localObject1 = "";
    paramString = paramString.trim();
    if (paramString.toLowerCase().startsWith("select"))
    {
      int i = paramString.toLowerCase().indexOf("from");
      int j = paramString.indexOf(",", i);
      if (j == -1)
      {
        localObject1 = paramString.substring(i + "from".length(), paramString.length()).trim();
        Object localObject2 = localObject1;
        int k = ((String)localObject2).toLowerCase().indexOf("where");
        if (k != -1) {
          localObject2 = ((String)localObject2).substring(0, k).trim();
        }
        localObject1 = localObject2;
      }
    }
    else if ((paramString.toLowerCase().startsWith("insert")) || (!paramString.toLowerCase().startsWith("update"))) {}
    return (String)localObject1;
  }
  
  public void commit()
    throws SQLException
  {
    conn.commit();
  }
  
  public void rollback()
    throws SQLException
  {
    conn.rollback();
  }
  
  public void rollback(Savepoint paramSavepoint)
    throws SQLException
  {
    conn.rollback(paramSavepoint);
  }
  
  public void unsetMatchColumn(int[] paramArrayOfInt)
    throws SQLException
  {
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      int i = Integer.parseInt(((Integer)iMatchColumns.get(j)).toString());
      if (paramArrayOfInt[j] != i) {
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString());
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
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString());
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
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString());
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
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString());
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
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString());
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
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString());
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
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString());
    }
    iMatchColumns.set(0, Integer.valueOf(paramInt));
  }
  
  public void setMatchColumn(String paramString)
    throws SQLException
  {
    if ((paramString == null) || ((paramString = paramString.trim()).equals(""))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString());
    }
    strMatchColumns.set(0, paramString);
  }
  
  public void unsetMatchColumn(int paramInt)
    throws SQLException
  {
    if (!((Integer)iMatchColumns.get(0)).equals(Integer.valueOf(paramInt))) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString());
    }
    if (strMatchColumns.get(0) != null) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.unsetmatch1").toString());
    }
    iMatchColumns.set(0, Integer.valueOf(-1));
  }
  
  public void unsetMatchColumn(String paramString)
    throws SQLException
  {
    paramString = paramString.trim();
    if (!((String)strMatchColumns.get(0)).equals(paramString)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString());
    }
    if (((Integer)iMatchColumns.get(0)).intValue() > 0) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.unsetmatch2").toString());
    }
    strMatchColumns.set(0, null);
  }
  
  public void rowSetPopulated(RowSetEvent paramRowSetEvent, int paramInt)
    throws SQLException
  {
    if ((paramInt < 0) || (paramInt < getFetchSize())) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.numrows").toString());
    }
    if (size() % paramInt == 0)
    {
      RowSetEvent localRowSetEvent = new RowSetEvent(this);
      paramRowSetEvent = localRowSetEvent;
      notifyRowSetChanged();
    }
  }
  
  public void populate(ResultSet paramResultSet, int paramInt)
    throws SQLException
  {
    Map localMap = getTypeMap();
    cursorPos = 0;
    if (populatecallcount == 0)
    {
      if (paramInt < 0) {
        throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.startpos").toString());
      }
      if (getMaxRows() == 0)
      {
        paramResultSet.absolute(paramInt);
        while (paramResultSet.next()) {
          totalRows += 1;
        }
        totalRows += 1;
      }
      startPos = paramInt;
    }
    populatecallcount += 1;
    resultSet = paramResultSet;
    if ((endPos - startPos >= getMaxRows()) && (getMaxRows() > 0))
    {
      endPos = prevEndPos;
      pagenotend = false;
      return;
    }
    if (((maxRowsreached != getMaxRows()) || (maxRowsreached != totalRows)) && (pagenotend)) {
      startPrev = (paramInt - getPageSize());
    }
    if (pageSize == 0)
    {
      prevEndPos = endPos;
      endPos = (paramInt + getMaxRows());
    }
    else
    {
      prevEndPos = endPos;
      endPos = (paramInt + getPageSize());
    }
    if (paramInt == 1) {
      resultSet.beforeFirst();
    } else {
      resultSet.absolute(paramInt - 1);
    }
    if (pageSize == 0) {
      rvh = new Vector(getMaxRows());
    } else {
      rvh = new Vector(getPageSize());
    }
    if (paramResultSet == null) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.populate").toString());
    }
    RSMD = paramResultSet.getMetaData();
    RowSetMD = new RowSetMetaDataImpl();
    initMetaData(RowSetMD, RSMD);
    RSMD = null;
    int j = RowSetMD.getColumnCount();
    int m = getMaxRows();
    int i = 0;
    Row localRow = null;
    if ((!paramResultSet.next()) && (m == 0))
    {
      endPos = prevEndPos;
      pagenotend = false;
      return;
    }
    paramResultSet.previous();
    while (paramResultSet.next())
    {
      localRow = new Row(j);
      if (pageSize == 0)
      {
        if ((i >= m) && (m > 0))
        {
          rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
          break;
        }
      }
      else if ((i >= pageSize) || ((maxRowsreached >= m) && (m > 0)))
      {
        rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
        break;
      }
      for (int k = 1; k <= j; k++)
      {
        Object localObject;
        if (localMap == null) {
          localObject = paramResultSet.getObject(k);
        } else {
          localObject = paramResultSet.getObject(k, localMap);
        }
        if ((localObject instanceof Struct)) {
          localObject = new SerialStruct((Struct)localObject, localMap);
        } else if ((localObject instanceof SQLData)) {
          localObject = new SerialStruct((SQLData)localObject, localMap);
        } else if ((localObject instanceof Blob)) {
          localObject = new SerialBlob((Blob)localObject);
        } else if ((localObject instanceof Clob)) {
          localObject = new SerialClob((Clob)localObject);
        } else if ((localObject instanceof Array)) {
          localObject = new SerialArray((Array)localObject, localMap);
        }
        localRow.initColumnObject(k, localObject);
      }
      i++;
      maxRowsreached += 1;
      rvh.add(localRow);
    }
    numRows = i;
    notifyRowSetChanged();
  }
  
  public boolean nextPage()
    throws SQLException
  {
    if (populatecallcount == 0) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString());
    }
    onFirstPage = false;
    if (callWithCon)
    {
      crsReader.setStartPosition(endPos);
      crsReader.readData(this);
      resultSet = null;
    }
    else
    {
      populate(resultSet, endPos);
    }
    return pagenotend;
  }
  
  public void setPageSize(int paramInt)
    throws SQLException
  {
    if (paramInt < 0) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.pagesize").toString());
    }
    if ((paramInt > getMaxRows()) && (getMaxRows() != 0)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.pagesize1").toString());
    }
    pageSize = paramInt;
  }
  
  public int getPageSize()
  {
    return pageSize;
  }
  
  public boolean previousPage()
    throws SQLException
  {
    int i = getPageSize();
    int j = maxRowsreached;
    if (populatecallcount == 0) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString());
    }
    if ((!callWithCon) && (resultSet.getType() == 1003)) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.fwdonly").toString());
    }
    pagenotend = true;
    if (startPrev < startPos)
    {
      onFirstPage = true;
      return false;
    }
    if (onFirstPage) {
      return false;
    }
    int k = j % i;
    if (k == 0)
    {
      maxRowsreached -= 2 * i;
      if (callWithCon)
      {
        crsReader.setStartPosition(startPrev);
        crsReader.readData(this);
        resultSet = null;
      }
      else
      {
        populate(resultSet, startPrev);
      }
      return true;
    }
    maxRowsreached -= i + k;
    if (callWithCon)
    {
      crsReader.setStartPosition(startPrev);
      crsReader.readData(this);
      resultSet = null;
    }
    else
    {
      populate(resultSet, startPrev);
    }
    return true;
  }
  
  public void setRowInserted(boolean paramBoolean)
    throws SQLException
  {
    checkCursor();
    if (onInsertRow == true) {
      throw new SQLException(resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString());
    }
    if (paramBoolean) {
      ((Row)getCurrentRow()).setInserted();
    } else {
      ((Row)getCurrentRow()).clearInserted();
    }
  }
  
  public SQLXML getSQLXML(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public SQLXML getSQLXML(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public RowId getRowId(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public RowId getRowId(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateRowId(int paramInt, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateRowId(String paramString, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public int getHoldability()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public boolean isClosed()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateNString(int paramInt, String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateNString(String paramString1, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateNClob(int paramInt, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateNClob(String paramString, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public NClob getNClob(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public NClob getNClob(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
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
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void setSQLXML(String paramString, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void setRowId(int paramInt, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void setRowId(String paramString, RowId paramRowId)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void setNCharacterStream(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(String paramString, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public Reader getNCharacterStream(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public Reader getNCharacterStream(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateSQLXML(int paramInt, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateSQLXML(String paramString, SQLXML paramSQLXML)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public String getNString(int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public String getNString(String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString());
  }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBlob(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBlob(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBlob(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateNClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {}
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {}
  
  public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {}
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {}
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateCharacterStream(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {}
  
  public void setURL(int paramInt, URL paramURL)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNClob(int paramInt, NClob paramNClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNString(int paramInt, String paramString)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNString(String paramString1, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNCharacterStream(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(String paramString, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(String paramString, Clob paramClob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setDate(String paramString, java.sql.Date paramDate)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setDate(String paramString, java.sql.Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setTime(String paramString, Time paramTime)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setTime(String paramString, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(int paramInt, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setClob(int paramInt, Reader paramReader, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(int paramInt, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(int paramInt, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(String paramString, InputStream paramInputStream, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(String paramString, Blob paramBlob)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBlob(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setObject(String paramString, Object paramObject)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setCharacterStream(String paramString, Reader paramReader)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setString(String paramString1, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNull(String paramString, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setNull(String paramString1, int paramInt, String paramString2)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setByte(String paramString, byte paramByte)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setShort(String paramString, short paramShort)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setInt(String paramString, int paramInt)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setLong(String paramString, long paramLong)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setFloat(String paramString, float paramFloat)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
  }
  
  public void setDouble(String paramString, double paramDouble)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException(resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString());
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\CachedRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */