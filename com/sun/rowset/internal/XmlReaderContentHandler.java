package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import com.sun.rowset.WebRowSetImpl;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;

public class XmlReaderContentHandler
  extends DefaultHandler
{
  private HashMap<String, Integer> propMap;
  private HashMap<String, Integer> colDefMap;
  private HashMap<String, Integer> dataMap;
  private HashMap<String, Class<?>> typeMap;
  private Vector<Object[]> updates;
  private Vector<String> keyCols;
  private String columnValue;
  private String propertyValue;
  private String metaDataValue;
  private int tag;
  private int state;
  private WebRowSetImpl rs;
  private boolean nullVal;
  private boolean emptyStringVal;
  private RowSetMetaData md;
  private int idx;
  private String lastval;
  private String Key_map;
  private String Value_map;
  private String tempStr;
  private String tempUpdate;
  private String tempCommand;
  private Object[] upd;
  private String[] properties = { "command", "concurrency", "datasource", "escape-processing", "fetch-direction", "fetch-size", "isolation-level", "key-columns", "map", "max-field-size", "max-rows", "query-timeout", "read-only", "rowset-type", "show-deleted", "table-name", "url", "null", "column", "type", "class", "sync-provider", "sync-provider-name", "sync-provider-vendor", "sync-provider-version", "sync-provider-grade", "data-source-lock" };
  private static final int CommandTag = 0;
  private static final int ConcurrencyTag = 1;
  private static final int DatasourceTag = 2;
  private static final int EscapeProcessingTag = 3;
  private static final int FetchDirectionTag = 4;
  private static final int FetchSizeTag = 5;
  private static final int IsolationLevelTag = 6;
  private static final int KeycolsTag = 7;
  private static final int MapTag = 8;
  private static final int MaxFieldSizeTag = 9;
  private static final int MaxRowsTag = 10;
  private static final int QueryTimeoutTag = 11;
  private static final int ReadOnlyTag = 12;
  private static final int RowsetTypeTag = 13;
  private static final int ShowDeletedTag = 14;
  private static final int TableNameTag = 15;
  private static final int UrlTag = 16;
  private static final int PropNullTag = 17;
  private static final int PropColumnTag = 18;
  private static final int PropTypeTag = 19;
  private static final int PropClassTag = 20;
  private static final int SyncProviderTag = 21;
  private static final int SyncProviderNameTag = 22;
  private static final int SyncProviderVendorTag = 23;
  private static final int SyncProviderVersionTag = 24;
  private static final int SyncProviderGradeTag = 25;
  private static final int DataSourceLock = 26;
  private String[] colDef = { "column-count", "column-definition", "column-index", "auto-increment", "case-sensitive", "currency", "nullable", "signed", "searchable", "column-display-size", "column-label", "column-name", "schema-name", "column-precision", "column-scale", "table-name", "catalog-name", "column-type", "column-type-name", "null" };
  private static final int ColumnCountTag = 0;
  private static final int ColumnDefinitionTag = 1;
  private static final int ColumnIndexTag = 2;
  private static final int AutoIncrementTag = 3;
  private static final int CaseSensitiveTag = 4;
  private static final int CurrencyTag = 5;
  private static final int NullableTag = 6;
  private static final int SignedTag = 7;
  private static final int SearchableTag = 8;
  private static final int ColumnDisplaySizeTag = 9;
  private static final int ColumnLabelTag = 10;
  private static final int ColumnNameTag = 11;
  private static final int SchemaNameTag = 12;
  private static final int ColumnPrecisionTag = 13;
  private static final int ColumnScaleTag = 14;
  private static final int MetaTableNameTag = 15;
  private static final int CatalogNameTag = 16;
  private static final int ColumnTypeTag = 17;
  private static final int ColumnTypeNameTag = 18;
  private static final int MetaNullTag = 19;
  private String[] data = { "currentRow", "columnValue", "insertRow", "deleteRow", "insdel", "updateRow", "null", "emptyString" };
  private static final int RowTag = 0;
  private static final int ColTag = 1;
  private static final int InsTag = 2;
  private static final int DelTag = 3;
  private static final int InsDelTag = 4;
  private static final int UpdTag = 5;
  private static final int NullTag = 6;
  private static final int EmptyStringTag = 7;
  private static final int INITIAL = 0;
  private static final int PROPERTIES = 1;
  private static final int METADATA = 2;
  private static final int DATA = 3;
  private JdbcRowSetResourceBundle resBundle;
  
  public XmlReaderContentHandler(RowSet paramRowSet)
  {
    rs = ((WebRowSetImpl)paramRowSet);
    initMaps();
    updates = new Vector();
    columnValue = "";
    propertyValue = "";
    metaDataValue = "";
    nullVal = false;
    idx = 0;
    tempStr = "";
    tempUpdate = "";
    tempCommand = "";
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  private void initMaps()
  {
    propMap = new HashMap();
    int i = properties.length;
    for (int j = 0; j < i; j++) {
      propMap.put(properties[j], Integer.valueOf(j));
    }
    colDefMap = new HashMap();
    i = colDef.length;
    for (j = 0; j < i; j++) {
      colDefMap.put(colDef[j], Integer.valueOf(j));
    }
    dataMap = new HashMap();
    i = data.length;
    for (j = 0; j < i; j++) {
      dataMap.put(data[j], Integer.valueOf(j));
    }
    typeMap = new HashMap();
  }
  
  public void startDocument()
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    String str = "";
    str = paramString2;
    int i;
    switch (getState())
    {
    case 1: 
      tempCommand = "";
      i = ((Integer)propMap.get(str)).intValue();
      if (i == 17) {
        setNullValue(true);
      } else {
        setTag(i);
      }
      break;
    case 2: 
      i = ((Integer)colDefMap.get(str)).intValue();
      if (i == 19) {
        setNullValue(true);
      } else {
        setTag(i);
      }
      break;
    case 3: 
      tempStr = "";
      tempUpdate = "";
      if (dataMap.get(str) == null) {
        i = 6;
      } else if (((Integer)dataMap.get(str)).intValue() == 7) {
        i = 7;
      } else {
        i = ((Integer)dataMap.get(str)).intValue();
      }
      if (i == 6)
      {
        setNullValue(true);
      }
      else if (i == 7)
      {
        setEmptyStringValue(true);
      }
      else
      {
        setTag(i);
        if ((i == 0) || (i == 3) || (i == 2))
        {
          idx = 0;
          try
          {
            rs.moveToInsertRow();
          }
          catch (SQLException localSQLException) {}
        }
      }
      break;
    default: 
      setState(str);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    String str = "";
    str = paramString2;
    int i;
    switch (getState())
    {
    case 1: 
      if (str.equals("properties"))
      {
        state = 0;
      }
      else
      {
        try
        {
          i = ((Integer)propMap.get(str)).intValue();
          switch (i)
          {
          case 7: 
            if (keyCols != null)
            {
              int[] arrayOfInt = new int[keyCols.size()];
              for (int j = 0; j < arrayOfInt.length; j++) {
                arrayOfInt[j] = Integer.parseInt((String)keyCols.elementAt(j));
              }
              rs.setKeyColumns(arrayOfInt);
            }
            break;
          case 20: 
            try
            {
              typeMap.put(Key_map, ReflectUtil.forName(Value_map));
            }
            catch (ClassNotFoundException localClassNotFoundException)
            {
              throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errmap").toString(), new Object[] { localClassNotFoundException.getMessage() }));
            }
          case 8: 
            rs.setTypeMap(typeMap);
            break;
          }
          if (getNullValue())
          {
            setPropertyValue(null);
            setNullValue(false);
          }
          else
          {
            setPropertyValue(propertyValue);
          }
        }
        catch (SQLException localSQLException1)
        {
          throw new SAXException(localSQLException1.getMessage());
        }
        propertyValue = "";
        setTag(-1);
      }
      break;
    case 2: 
      if (str.equals("metadata"))
      {
        try
        {
          rs.setMetaData(md);
          state = 0;
        }
        catch (SQLException localSQLException2)
        {
          throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errmetadata").toString(), new Object[] { localSQLException2.getMessage() }));
        }
      }
      else
      {
        try
        {
          if (getNullValue())
          {
            setMetaDataValue(null);
            setNullValue(false);
          }
          else
          {
            setMetaDataValue(metaDataValue);
          }
        }
        catch (SQLException localSQLException3)
        {
          throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errmetadata").toString(), new Object[] { localSQLException3.getMessage() }));
        }
        metaDataValue = "";
      }
      setTag(-1);
      break;
    case 3: 
      if (str.equals("data"))
      {
        state = 0;
        return;
      }
      if (dataMap.get(str) == null) {
        i = 6;
      } else {
        i = ((Integer)dataMap.get(str)).intValue();
      }
      switch (i)
      {
      case 1: 
        try
        {
          idx += 1;
          if (getNullValue())
          {
            insertValue(null);
            setNullValue(false);
          }
          else
          {
            insertValue(tempStr);
          }
          columnValue = "";
        }
        catch (SQLException localSQLException4)
        {
          throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errinsertval").toString(), new Object[] { localSQLException4.getMessage() }));
        }
      case 0: 
        try
        {
          rs.insertRow();
          rs.moveToCurrentRow();
          rs.next();
          rs.setOriginalRow();
          applyUpdates();
        }
        catch (SQLException localSQLException5)
        {
          throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errconstr").toString(), new Object[] { localSQLException5.getMessage() }));
        }
      case 3: 
        try
        {
          rs.insertRow();
          rs.moveToCurrentRow();
          rs.next();
          rs.setOriginalRow();
          applyUpdates();
          rs.deleteRow();
        }
        catch (SQLException localSQLException6)
        {
          throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errdel").toString(), new Object[] { localSQLException6.getMessage() }));
        }
      case 2: 
        try
        {
          rs.insertRow();
          rs.moveToCurrentRow();
          rs.next();
          applyUpdates();
        }
        catch (SQLException localSQLException7)
        {
          throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errinsert").toString(), new Object[] { localSQLException7.getMessage() }));
        }
      case 4: 
        try
        {
          rs.insertRow();
          rs.moveToCurrentRow();
          rs.next();
          rs.setOriginalRow();
          applyUpdates();
        }
        catch (SQLException localSQLException8)
        {
          throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errinsdel").toString(), new Object[] { localSQLException8.getMessage() }));
        }
      case 5: 
        try
        {
          if (getNullValue())
          {
            insertValue(null);
            setNullValue(false);
          }
          else if (getEmptyStringValue())
          {
            insertValue("");
            setEmptyStringValue(false);
          }
          else
          {
            updates.add(upd);
          }
        }
        catch (SQLException localSQLException9)
        {
          throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errupdate").toString(), new Object[] { localSQLException9.getMessage() }));
        }
      }
      break;
    }
  }
  
  private void applyUpdates()
    throws SAXException
  {
    if (updates.size() > 0)
    {
      try
      {
        Iterator localIterator = updates.iterator();
        while (localIterator.hasNext())
        {
          Object[] arrayOfObject = (Object[])localIterator.next();
          idx = ((Integer)arrayOfObject[0]).intValue();
          if (!lastval.equals(arrayOfObject[1])) {
            insertValue((String)arrayOfObject[1]);
          }
        }
        rs.updateRow();
      }
      catch (SQLException localSQLException)
      {
        throw new SAXException(MessageFormat.format(resBundle.handleGetObject("xmlrch.errupdrow").toString(), new Object[] { localSQLException.getMessage() }));
      }
      updates.removeAllElements();
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      switch (getState())
      {
      case 1: 
        propertyValue = new String(paramArrayOfChar, paramInt1, paramInt2);
        tempCommand = tempCommand.concat(propertyValue);
        propertyValue = tempCommand;
        if (tag == 19) {
          Key_map = propertyValue;
        } else if (tag == 20) {
          Value_map = propertyValue;
        }
        break;
      case 2: 
        if (tag != -1) {
          metaDataValue = new String(paramArrayOfChar, paramInt1, paramInt2);
        }
        break;
      case 3: 
        setDataValue(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    catch (SQLException localSQLException)
    {
      throw new SAXException(resBundle.handleGetObject("xmlrch.chars").toString() + localSQLException.getMessage());
    }
  }
  
  private void setState(String paramString)
    throws SAXException
  {
    if (paramString.equals("webRowSet")) {
      state = 0;
    } else if (paramString.equals("properties"))
    {
      if (state != 1) {
        state = 1;
      } else {
        state = 0;
      }
    }
    else if (paramString.equals("metadata"))
    {
      if (state != 2) {
        state = 2;
      } else {
        state = 0;
      }
    }
    else if (paramString.equals("data")) {
      if (state != 3) {
        state = 3;
      } else {
        state = 0;
      }
    }
  }
  
  private int getState()
  {
    return state;
  }
  
  private void setTag(int paramInt)
  {
    tag = paramInt;
  }
  
  private int getTag()
  {
    return tag;
  }
  
  private void setNullValue(boolean paramBoolean)
  {
    nullVal = paramBoolean;
  }
  
  private boolean getNullValue()
  {
    return nullVal;
  }
  
  private void setEmptyStringValue(boolean paramBoolean)
  {
    emptyStringVal = paramBoolean;
  }
  
  private boolean getEmptyStringValue()
  {
    return emptyStringVal;
  }
  
  private String getStringValue(String paramString)
  {
    return paramString;
  }
  
  private int getIntegerValue(String paramString)
  {
    return Integer.parseInt(paramString);
  }
  
  private boolean getBooleanValue(String paramString)
  {
    return Boolean.valueOf(paramString).booleanValue();
  }
  
  private BigDecimal getBigDecimalValue(String paramString)
  {
    return new BigDecimal(paramString);
  }
  
  private byte getByteValue(String paramString)
  {
    return Byte.parseByte(paramString);
  }
  
  private short getShortValue(String paramString)
  {
    return Short.parseShort(paramString);
  }
  
  private long getLongValue(String paramString)
  {
    return Long.parseLong(paramString);
  }
  
  private float getFloatValue(String paramString)
  {
    return Float.parseFloat(paramString);
  }
  
  private double getDoubleValue(String paramString)
  {
    return Double.parseDouble(paramString);
  }
  
  private byte[] getBinaryValue(String paramString)
  {
    return paramString.getBytes();
  }
  
  private Date getDateValue(String paramString)
  {
    return new Date(getLongValue(paramString));
  }
  
  private Time getTimeValue(String paramString)
  {
    return new Time(getLongValue(paramString));
  }
  
  private Timestamp getTimestampValue(String paramString)
  {
    return new Timestamp(getLongValue(paramString));
  }
  
  private void setPropertyValue(String paramString)
    throws SQLException
  {
    boolean bool = getNullValue();
    String str;
    switch (getTag())
    {
    case 0: 
      if (!bool) {
        rs.setCommand(paramString);
      }
      break;
    case 1: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setConcurrency(getIntegerValue(paramString));
      break;
    case 2: 
      if (bool) {
        rs.setDataSourceName(null);
      } else {
        rs.setDataSourceName(paramString);
      }
      break;
    case 3: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setEscapeProcessing(getBooleanValue(paramString));
      break;
    case 4: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setFetchDirection(getIntegerValue(paramString));
      break;
    case 5: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setFetchSize(getIntegerValue(paramString));
      break;
    case 6: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setTransactionIsolation(getIntegerValue(paramString));
      break;
    case 7: 
      break;
    case 18: 
      if (keyCols == null) {
        keyCols = new Vector();
      }
      keyCols.add(paramString);
      break;
    case 8: 
      break;
    case 9: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setMaxFieldSize(getIntegerValue(paramString));
      break;
    case 10: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setMaxRows(getIntegerValue(paramString));
      break;
    case 11: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setQueryTimeout(getIntegerValue(paramString));
      break;
    case 12: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setReadOnly(getBooleanValue(paramString));
      break;
    case 13: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      str = getStringValue(paramString);
      int i = 0;
      if (str.trim().equals("ResultSet.TYPE_SCROLL_INSENSITIVE")) {
        i = 1004;
      } else if (str.trim().equals("ResultSet.TYPE_SCROLL_SENSITIVE")) {
        i = 1005;
      } else if (str.trim().equals("ResultSet.TYPE_FORWARD_ONLY")) {
        i = 1003;
      }
      rs.setType(i);
      break;
    case 14: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue").toString());
      }
      rs.setShowDeleted(getBooleanValue(paramString));
      break;
    case 15: 
      if (!bool) {
        rs.setTableName(paramString);
      }
      break;
    case 16: 
      if (bool) {
        rs.setUrl(null);
      } else {
        rs.setUrl(paramString);
      }
      break;
    case 22: 
      if (bool)
      {
        rs.setSyncProvider(null);
      }
      else
      {
        str = paramString.substring(0, paramString.indexOf("@") + 1);
        rs.setSyncProvider(str);
      }
      break;
    case 23: 
      break;
    case 24: 
      break;
    case 25: 
      break;
    case 26: 
      break;
    }
  }
  
  private void setMetaDataValue(String paramString)
    throws SQLException
  {
    boolean bool = getNullValue();
    switch (getTag())
    {
    case 0: 
      md = new RowSetMetaDataImpl();
      idx = 0;
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setColumnCount(getIntegerValue(paramString));
      break;
    case 1: 
      break;
    case 2: 
      idx += 1;
      break;
    case 3: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setAutoIncrement(idx, getBooleanValue(paramString));
      break;
    case 4: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setCaseSensitive(idx, getBooleanValue(paramString));
      break;
    case 5: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setCurrency(idx, getBooleanValue(paramString));
      break;
    case 6: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setNullable(idx, getIntegerValue(paramString));
      break;
    case 7: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setSigned(idx, getBooleanValue(paramString));
      break;
    case 8: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setSearchable(idx, getBooleanValue(paramString));
      break;
    case 9: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setColumnDisplaySize(idx, getIntegerValue(paramString));
      break;
    case 10: 
      if (bool) {
        md.setColumnLabel(idx, null);
      } else {
        md.setColumnLabel(idx, paramString);
      }
      break;
    case 11: 
      if (bool) {
        md.setColumnName(idx, null);
      } else {
        md.setColumnName(idx, paramString);
      }
      break;
    case 12: 
      if (bool) {
        md.setSchemaName(idx, null);
      } else {
        md.setSchemaName(idx, paramString);
      }
      break;
    case 13: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setPrecision(idx, getIntegerValue(paramString));
      break;
    case 14: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setScale(idx, getIntegerValue(paramString));
      break;
    case 15: 
      if (bool) {
        md.setTableName(idx, null);
      } else {
        md.setTableName(idx, paramString);
      }
      break;
    case 16: 
      if (bool) {
        md.setCatalogName(idx, null);
      } else {
        md.setCatalogName(idx, paramString);
      }
      break;
    case 17: 
      if (bool) {
        throw new SQLException(resBundle.handleGetObject("xmlrch.badvalue1").toString());
      }
      md.setColumnType(idx, getIntegerValue(paramString));
      break;
    case 18: 
      if (bool) {
        md.setColumnTypeName(idx, null);
      } else {
        md.setColumnTypeName(idx, paramString);
      }
      break;
    }
  }
  
  private void setDataValue(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SQLException
  {
    switch (getTag())
    {
    case 1: 
      columnValue = new String(paramArrayOfChar, paramInt1, paramInt2);
      tempStr = tempStr.concat(columnValue);
      break;
    case 5: 
      upd = new Object[2];
      tempUpdate = tempUpdate.concat(new String(paramArrayOfChar, paramInt1, paramInt2));
      upd[0] = Integer.valueOf(idx);
      upd[1] = tempUpdate;
      lastval = ((String)upd[1]);
      break;
    }
  }
  
  private void insertValue(String paramString)
    throws SQLException
  {
    if (getNullValue())
    {
      rs.updateNull(idx);
      return;
    }
    int i = rs.getMetaData().getColumnType(idx);
    switch (i)
    {
    case -7: 
      rs.updateBoolean(idx, getBooleanValue(paramString));
      break;
    case 16: 
      rs.updateBoolean(idx, getBooleanValue(paramString));
      break;
    case -6: 
    case 5: 
      rs.updateShort(idx, getShortValue(paramString));
      break;
    case 4: 
      rs.updateInt(idx, getIntegerValue(paramString));
      break;
    case -5: 
      rs.updateLong(idx, getLongValue(paramString));
      break;
    case 6: 
    case 7: 
      rs.updateFloat(idx, getFloatValue(paramString));
      break;
    case 8: 
      rs.updateDouble(idx, getDoubleValue(paramString));
      break;
    case 2: 
    case 3: 
      rs.updateObject(idx, getBigDecimalValue(paramString));
      break;
    case -4: 
    case -3: 
    case -2: 
      rs.updateBytes(idx, getBinaryValue(paramString));
      break;
    case 91: 
      rs.updateDate(idx, getDateValue(paramString));
      break;
    case 92: 
      rs.updateTime(idx, getTimeValue(paramString));
      break;
    case 93: 
      rs.updateTimestamp(idx, getTimestampValue(paramString));
      break;
    case -1: 
    case 1: 
    case 12: 
      rs.updateString(idx, getStringValue(paramString));
      break;
    }
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXParseException
  {
    throw paramSAXParseException;
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXParseException
  {
    System.out.println(MessageFormat.format(resBundle.handleGetObject("xmlrch.warning").toString(), new Object[] { paramSAXParseException.getMessage(), Integer.valueOf(paramSAXParseException.getLineNumber()), paramSAXParseException.getSystemId() }));
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) {}
  
  private Row getPresentRow(WebRowSetImpl paramWebRowSetImpl)
    throws SQLException
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\internal\XmlReaderContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */