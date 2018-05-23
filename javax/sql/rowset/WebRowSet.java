package javax.sql.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface WebRowSet
  extends CachedRowSet
{
  public static final String PUBLIC_XML_SCHEMA = "--//Oracle Corporation//XSD Schema//EN";
  public static final String SCHEMA_SYSTEM_ID = "http://java.sun.com/xml/ns/jdbc/webrowset.xsd";
  
  public abstract void readXml(Reader paramReader)
    throws SQLException;
  
  public abstract void readXml(InputStream paramInputStream)
    throws SQLException, IOException;
  
  public abstract void writeXml(ResultSet paramResultSet, Writer paramWriter)
    throws SQLException;
  
  public abstract void writeXml(ResultSet paramResultSet, OutputStream paramOutputStream)
    throws SQLException, IOException;
  
  public abstract void writeXml(Writer paramWriter)
    throws SQLException;
  
  public abstract void writeXml(OutputStream paramOutputStream)
    throws SQLException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\WebRowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */