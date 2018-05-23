package com.sun.rowset;

import com.sun.rowset.internal.WebRowSetXmlReader;
import com.sun.rowset.internal.WebRowSetXmlWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncProvider;

public class WebRowSetImpl
  extends CachedRowSetImpl
  implements WebRowSet
{
  private WebRowSetXmlReader xmlReader;
  private WebRowSetXmlWriter xmlWriter;
  private int curPosBfrWrite;
  private SyncProvider provider;
  static final long serialVersionUID = -8771775154092422943L;
  
  public WebRowSetImpl()
    throws SQLException
  {
    xmlReader = new WebRowSetXmlReader();
    xmlWriter = new WebRowSetXmlWriter();
  }
  
  public WebRowSetImpl(Hashtable paramHashtable)
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
      throw new SQLException(resBundle.handleGetObject("webrowsetimpl.nullhash").toString());
    }
    String str = (String)paramHashtable.get("rowset.provider.classname");
    provider = SyncFactory.getInstance(str);
  }
  
  public void writeXml(ResultSet paramResultSet, Writer paramWriter)
    throws SQLException
  {
    populate(paramResultSet);
    curPosBfrWrite = getRow();
    writeXml(paramWriter);
  }
  
  public void writeXml(Writer paramWriter)
    throws SQLException
  {
    if (xmlWriter != null)
    {
      curPosBfrWrite = getRow();
      xmlWriter.writeXML(this, paramWriter);
    }
    else
    {
      throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
    }
  }
  
  public void readXml(Reader paramReader)
    throws SQLException
  {
    try
    {
      if (paramReader != null)
      {
        xmlReader.readXML(this, paramReader);
        if (curPosBfrWrite == 0) {
          beforeFirst();
        } else {
          absolute(curPosBfrWrite);
        }
      }
      else
      {
        throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
      }
    }
    catch (Exception localException)
    {
      throw new SQLException(localException.getMessage());
    }
  }
  
  public void readXml(InputStream paramInputStream)
    throws SQLException, IOException
  {
    if (paramInputStream != null)
    {
      xmlReader.readXML(this, paramInputStream);
      if (curPosBfrWrite == 0) {
        beforeFirst();
      } else {
        absolute(curPosBfrWrite);
      }
    }
    else
    {
      throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
    }
  }
  
  public void writeXml(OutputStream paramOutputStream)
    throws SQLException, IOException
  {
    if (xmlWriter != null)
    {
      curPosBfrWrite = getRow();
      xmlWriter.writeXML(this, paramOutputStream);
    }
    else
    {
      throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
    }
  }
  
  public void writeXml(ResultSet paramResultSet, OutputStream paramOutputStream)
    throws SQLException, IOException
  {
    populate(paramResultSet);
    curPosBfrWrite = getRow();
    writeXml(paramOutputStream);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\WebRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */