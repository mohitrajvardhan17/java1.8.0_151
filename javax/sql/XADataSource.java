package javax.sql;

import java.sql.SQLException;

public abstract interface XADataSource
  extends CommonDataSource
{
  public abstract XAConnection getXAConnection()
    throws SQLException;
  
  public abstract XAConnection getXAConnection(String paramString1, String paramString2)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\XADataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */