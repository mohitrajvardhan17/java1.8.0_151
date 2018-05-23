package javax.sql;

import java.sql.SQLException;

public abstract interface ConnectionPoolDataSource
  extends CommonDataSource
{
  public abstract PooledConnection getPooledConnection()
    throws SQLException;
  
  public abstract PooledConnection getPooledConnection(String paramString1, String paramString2)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\ConnectionPoolDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */