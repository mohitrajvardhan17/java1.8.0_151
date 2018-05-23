package java.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

public abstract interface SQLXML
{
  public abstract void free()
    throws SQLException;
  
  public abstract InputStream getBinaryStream()
    throws SQLException;
  
  public abstract OutputStream setBinaryStream()
    throws SQLException;
  
  public abstract Reader getCharacterStream()
    throws SQLException;
  
  public abstract Writer setCharacterStream()
    throws SQLException;
  
  public abstract String getString()
    throws SQLException;
  
  public abstract void setString(String paramString)
    throws SQLException;
  
  public abstract <T extends Source> T getSource(Class<T> paramClass)
    throws SQLException;
  
  public abstract <T extends Result> T setResult(Class<T> paramClass)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLXML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */