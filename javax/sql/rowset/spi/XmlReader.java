package javax.sql.rowset.spi;

import java.io.Reader;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.rowset.WebRowSet;

public abstract interface XmlReader
  extends RowSetReader
{
  public abstract void readXML(WebRowSet paramWebRowSet, Reader paramReader)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\spi\XmlReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */