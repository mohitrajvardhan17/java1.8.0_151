package javax.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface RowSetInternal
{
  public abstract Object[] getParams()
    throws SQLException;
  
  public abstract Connection getConnection()
    throws SQLException;
  
  public abstract void setMetaData(RowSetMetaData paramRowSetMetaData)
    throws SQLException;
  
  public abstract ResultSet getOriginal()
    throws SQLException;
  
  public abstract ResultSet getOriginalRow()
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\RowSetInternal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */