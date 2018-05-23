package javax.sql;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public abstract interface CommonDataSource
{
  public abstract PrintWriter getLogWriter()
    throws SQLException;
  
  public abstract void setLogWriter(PrintWriter paramPrintWriter)
    throws SQLException;
  
  public abstract void setLoginTimeout(int paramInt)
    throws SQLException;
  
  public abstract int getLoginTimeout()
    throws SQLException;
  
  public abstract Logger getParentLogger()
    throws SQLFeatureNotSupportedException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\CommonDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */