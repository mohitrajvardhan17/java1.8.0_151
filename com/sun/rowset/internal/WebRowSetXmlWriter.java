package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.XmlWriter;

public class WebRowSetXmlWriter
  implements XmlWriter, Serializable
{
  private transient Writer writer;
  private Stack<String> stack;
  private JdbcRowSetResourceBundle resBundle;
  static final long serialVersionUID = 7163134986189677641L;
  
  public WebRowSetXmlWriter()
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
  
  public void writeXML(WebRowSet paramWebRowSet, Writer paramWriter)
    throws SQLException
  {
    stack = new Stack();
    writer = paramWriter;
    writeRowSet(paramWebRowSet);
  }
  
  public void writeXML(WebRowSet paramWebRowSet, OutputStream paramOutputStream)
    throws SQLException
  {
    stack = new Stack();
    writer = new OutputStreamWriter(paramOutputStream);
    writeRowSet(paramWebRowSet);
  }
  
  private void writeRowSet(WebRowSet paramWebRowSet)
    throws SQLException
  {
    try
    {
      startHeader();
      writeProperties(paramWebRowSet);
      writeMetaData(paramWebRowSet);
      writeData(paramWebRowSet);
      endHeader();
    }
    catch (IOException localIOException)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.ioex").toString(), new Object[] { localIOException.getMessage() }));
    }
  }
  
  private void startHeader()
    throws IOException
  {
    setTag("webRowSet");
    writer.write("<?xml version=\"1.0\"?>\n");
    writer.write("<webRowSet xmlns=\"http://java.sun.com/xml/ns/jdbc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
    writer.write("xsi:schemaLocation=\"http://java.sun.com/xml/ns/jdbc http://java.sun.com/xml/ns/jdbc/webrowset.xsd\">\n");
  }
  
  private void endHeader()
    throws IOException
  {
    endTag("webRowSet");
  }
  
  private void writeProperties(WebRowSet paramWebRowSet)
    throws IOException
  {
    beginSection("properties");
    try
    {
      propString("command", processSpecialCharacters(paramWebRowSet.getCommand()));
      propInteger("concurrency", paramWebRowSet.getConcurrency());
      propString("datasource", paramWebRowSet.getDataSourceName());
      propBoolean("escape-processing", paramWebRowSet.getEscapeProcessing());
      try
      {
        propInteger("fetch-direction", paramWebRowSet.getFetchDirection());
      }
      catch (SQLException localSQLException1) {}
      propInteger("fetch-size", paramWebRowSet.getFetchSize());
      propInteger("isolation-level", paramWebRowSet.getTransactionIsolation());
      beginSection("key-columns");
      int[] arrayOfInt = paramWebRowSet.getKeyColumns();
      for (int i = 0; (arrayOfInt != null) && (i < arrayOfInt.length); i++) {
        propInteger("column", arrayOfInt[i]);
      }
      endSection("key-columns");
      beginSection("map");
      Map localMap = paramWebRowSet.getTypeMap();
      if (localMap != null)
      {
        Iterator localIterator = localMap.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localObject = (Map.Entry)localIterator.next();
          propString("type", (String)((Map.Entry)localObject).getKey());
          propString("class", ((Class)((Map.Entry)localObject).getValue()).getName());
        }
      }
      endSection("map");
      propInteger("max-field-size", paramWebRowSet.getMaxFieldSize());
      propInteger("max-rows", paramWebRowSet.getMaxRows());
      propInteger("query-timeout", paramWebRowSet.getQueryTimeout());
      propBoolean("read-only", paramWebRowSet.isReadOnly());
      int j = paramWebRowSet.getType();
      Object localObject = "";
      if (j == 1003) {
        localObject = "ResultSet.TYPE_FORWARD_ONLY";
      } else if (j == 1004) {
        localObject = "ResultSet.TYPE_SCROLL_INSENSITIVE";
      } else if (j == 1005) {
        localObject = "ResultSet.TYPE_SCROLL_SENSITIVE";
      }
      propString("rowset-type", (String)localObject);
      propBoolean("show-deleted", paramWebRowSet.getShowDeleted());
      propString("table-name", paramWebRowSet.getTableName());
      propString("url", paramWebRowSet.getUrl());
      beginSection("sync-provider");
      String str1 = paramWebRowSet.getSyncProvider().toString();
      String str2 = str1.substring(0, paramWebRowSet.getSyncProvider().toString().indexOf("@"));
      propString("sync-provider-name", str2);
      propString("sync-provider-vendor", "Oracle Corporation");
      propString("sync-provider-version", "1.0");
      propInteger("sync-provider-grade", paramWebRowSet.getSyncProvider().getProviderGrade());
      propInteger("data-source-lock", paramWebRowSet.getSyncProvider().getDataSourceLock());
      endSection("sync-provider");
    }
    catch (SQLException localSQLException2)
    {
      throw new IOException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), new Object[] { localSQLException2.getMessage() }));
    }
    endSection("properties");
  }
  
  private void writeMetaData(WebRowSet paramWebRowSet)
    throws IOException
  {
    beginSection("metadata");
    try
    {
      ResultSetMetaData localResultSetMetaData = paramWebRowSet.getMetaData();
      int i = localResultSetMetaData.getColumnCount();
      propInteger("column-count", i);
      for (int j = 1; j <= i; j++)
      {
        beginSection("column-definition");
        propInteger("column-index", j);
        propBoolean("auto-increment", localResultSetMetaData.isAutoIncrement(j));
        propBoolean("case-sensitive", localResultSetMetaData.isCaseSensitive(j));
        propBoolean("currency", localResultSetMetaData.isCurrency(j));
        propInteger("nullable", localResultSetMetaData.isNullable(j));
        propBoolean("signed", localResultSetMetaData.isSigned(j));
        propBoolean("searchable", localResultSetMetaData.isSearchable(j));
        propInteger("column-display-size", localResultSetMetaData.getColumnDisplaySize(j));
        propString("column-label", localResultSetMetaData.getColumnLabel(j));
        propString("column-name", localResultSetMetaData.getColumnName(j));
        propString("schema-name", localResultSetMetaData.getSchemaName(j));
        propInteger("column-precision", localResultSetMetaData.getPrecision(j));
        propInteger("column-scale", localResultSetMetaData.getScale(j));
        propString("table-name", localResultSetMetaData.getTableName(j));
        propString("catalog-name", localResultSetMetaData.getCatalogName(j));
        propInteger("column-type", localResultSetMetaData.getColumnType(j));
        propString("column-type-name", localResultSetMetaData.getColumnTypeName(j));
        endSection("column-definition");
      }
    }
    catch (SQLException localSQLException)
    {
      throw new IOException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), new Object[] { localSQLException.getMessage() }));
    }
    endSection("metadata");
  }
  
  private void writeData(WebRowSet paramWebRowSet)
    throws IOException
  {
    try
    {
      ResultSetMetaData localResultSetMetaData = paramWebRowSet.getMetaData();
      int i = localResultSetMetaData.getColumnCount();
      beginSection("data");
      paramWebRowSet.beforeFirst();
      paramWebRowSet.setShowDeleted(true);
      while (paramWebRowSet.next())
      {
        if ((paramWebRowSet.rowDeleted()) && (paramWebRowSet.rowInserted())) {
          beginSection("modifyRow");
        } else if (paramWebRowSet.rowDeleted()) {
          beginSection("deleteRow");
        } else if (paramWebRowSet.rowInserted()) {
          beginSection("insertRow");
        } else {
          beginSection("currentRow");
        }
        for (int j = 1; j <= i; j++) {
          if (paramWebRowSet.columnUpdated(j))
          {
            ResultSet localResultSet = paramWebRowSet.getOriginalRow();
            localResultSet.next();
            beginTag("columnValue");
            writeValue(j, (RowSet)localResultSet);
            endTag("columnValue");
            beginTag("updateRow");
            writeValue(j, paramWebRowSet);
            endTag("updateRow");
          }
          else
          {
            beginTag("columnValue");
            writeValue(j, paramWebRowSet);
            endTag("columnValue");
          }
        }
        endSection();
      }
      endSection("data");
    }
    catch (SQLException localSQLException)
    {
      throw new IOException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), new Object[] { localSQLException.getMessage() }));
    }
  }
  
  private void writeValue(int paramInt, RowSet paramRowSet)
    throws IOException
  {
    try
    {
      int i = paramRowSet.getMetaData().getColumnType(paramInt);
      switch (i)
      {
      case -7: 
      case 16: 
        boolean bool = paramRowSet.getBoolean(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeBoolean(bool);
        }
        break;
      case -6: 
      case 5: 
        short s = paramRowSet.getShort(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeShort(s);
        }
        break;
      case 4: 
        int j = paramRowSet.getInt(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeInteger(j);
        }
        break;
      case -5: 
        long l = paramRowSet.getLong(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeLong(l);
        }
        break;
      case 6: 
      case 7: 
        float f = paramRowSet.getFloat(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeFloat(f);
        }
        break;
      case 8: 
        double d = paramRowSet.getDouble(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeDouble(d);
        }
        break;
      case 2: 
      case 3: 
        writeBigDecimal(paramRowSet.getBigDecimal(paramInt));
        break;
      case -4: 
      case -3: 
      case -2: 
        break;
      case 91: 
        Date localDate = paramRowSet.getDate(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeLong(localDate.getTime());
        }
        break;
      case 92: 
        Time localTime = paramRowSet.getTime(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeLong(localTime.getTime());
        }
        break;
      case 93: 
        Timestamp localTimestamp = paramRowSet.getTimestamp(paramInt);
        if (paramRowSet.wasNull()) {
          writeNull();
        } else {
          writeLong(localTimestamp.getTime());
        }
        break;
      case -1: 
      case 1: 
      case 12: 
        writeStringData(paramRowSet.getString(paramInt));
        break;
      default: 
        System.out.println(resBundle.handleGetObject("wsrxmlwriter.notproper").toString());
      }
    }
    catch (SQLException localSQLException)
    {
      throw new IOException(resBundle.handleGetObject("wrsxmlwriter.failedwrite").toString() + localSQLException.getMessage());
    }
  }
  
  private void beginSection(String paramString)
    throws IOException
  {
    setTag(paramString);
    writeIndent(stack.size());
    writer.write("<" + paramString + ">\n");
  }
  
  private void endSection(String paramString)
    throws IOException
  {
    writeIndent(stack.size());
    String str = getTag();
    if (str.indexOf("webRowSet") != -1) {
      str = "webRowSet";
    }
    if (paramString.equals(str)) {
      writer.write("</" + str + ">\n");
    }
    writer.flush();
  }
  
  private void endSection()
    throws IOException
  {
    writeIndent(stack.size());
    String str = getTag();
    writer.write("</" + str + ">\n");
    writer.flush();
  }
  
  private void beginTag(String paramString)
    throws IOException
  {
    setTag(paramString);
    writeIndent(stack.size());
    writer.write("<" + paramString + ">");
  }
  
  private void endTag(String paramString)
    throws IOException
  {
    String str = getTag();
    if (paramString.equals(str)) {
      writer.write("</" + str + ">\n");
    }
    writer.flush();
  }
  
  private void emptyTag(String paramString)
    throws IOException
  {
    writer.write("<" + paramString + "/>");
  }
  
  private void setTag(String paramString)
  {
    stack.push(paramString);
  }
  
  private String getTag()
  {
    return (String)stack.pop();
  }
  
  private void writeNull()
    throws IOException
  {
    emptyTag("null");
  }
  
  private void writeStringData(String paramString)
    throws IOException
  {
    if (paramString == null)
    {
      writeNull();
    }
    else if (paramString.equals(""))
    {
      writeEmptyString();
    }
    else
    {
      paramString = processSpecialCharacters(paramString);
      writer.write(paramString);
    }
  }
  
  private void writeString(String paramString)
    throws IOException
  {
    if (paramString != null) {
      writer.write(paramString);
    } else {
      writeNull();
    }
  }
  
  private void writeShort(short paramShort)
    throws IOException
  {
    writer.write(Short.toString(paramShort));
  }
  
  private void writeLong(long paramLong)
    throws IOException
  {
    writer.write(Long.toString(paramLong));
  }
  
  private void writeInteger(int paramInt)
    throws IOException
  {
    writer.write(Integer.toString(paramInt));
  }
  
  private void writeBoolean(boolean paramBoolean)
    throws IOException
  {
    writer.write(Boolean.valueOf(paramBoolean).toString());
  }
  
  private void writeFloat(float paramFloat)
    throws IOException
  {
    writer.write(Float.toString(paramFloat));
  }
  
  private void writeDouble(double paramDouble)
    throws IOException
  {
    writer.write(Double.toString(paramDouble));
  }
  
  private void writeBigDecimal(BigDecimal paramBigDecimal)
    throws IOException
  {
    if (paramBigDecimal != null) {
      writer.write(paramBigDecimal.toString());
    } else {
      emptyTag("null");
    }
  }
  
  private void writeIndent(int paramInt)
    throws IOException
  {
    for (int i = 1; i < paramInt; i++) {
      writer.write("  ");
    }
  }
  
  private void propString(String paramString1, String paramString2)
    throws IOException
  {
    beginTag(paramString1);
    writeString(paramString2);
    endTag(paramString1);
  }
  
  private void propInteger(String paramString, int paramInt)
    throws IOException
  {
    beginTag(paramString);
    writeInteger(paramInt);
    endTag(paramString);
  }
  
  private void propBoolean(String paramString, boolean paramBoolean)
    throws IOException
  {
    beginTag(paramString);
    writeBoolean(paramBoolean);
    endTag(paramString);
  }
  
  private void writeEmptyString()
    throws IOException
  {
    emptyTag("emptyString");
  }
  
  public boolean writeData(RowSetInternal paramRowSetInternal)
  {
    return false;
  }
  
  private String processSpecialCharacters(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    char[] arrayOfChar = paramString.toCharArray();
    String str = "";
    for (int i = 0; i < arrayOfChar.length; i++) {
      if (arrayOfChar[i] == '&') {
        str = str.concat("&amp;");
      } else if (arrayOfChar[i] == '<') {
        str = str.concat("&lt;");
      } else if (arrayOfChar[i] == '>') {
        str = str.concat("&gt;");
      } else if (arrayOfChar[i] == '\'') {
        str = str.concat("&apos;");
      } else if (arrayOfChar[i] == '"') {
        str = str.concat("&quot;");
      } else {
        str = str.concat(String.valueOf(arrayOfChar[i]));
      }
    }
    paramString = str;
    return paramString;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\internal\WebRowSetXmlWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */