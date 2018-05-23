package javax.sql.rowset.spi;

import java.io.Writer;
import java.sql.SQLException;
import javax.sql.RowSetWriter;
import javax.sql.rowset.WebRowSet;

public abstract interface XmlWriter
  extends RowSetWriter
{
  public abstract void writeXML(WebRowSet paramWebRowSet, Writer paramWriter)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\spi\XmlWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */