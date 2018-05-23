package javax.sql;

import java.sql.SQLException;
import java.util.EventObject;

public class ConnectionEvent
  extends EventObject
{
  private SQLException ex = null;
  static final long serialVersionUID = -4843217645290030002L;
  
  public ConnectionEvent(PooledConnection paramPooledConnection)
  {
    super(paramPooledConnection);
  }
  
  public ConnectionEvent(PooledConnection paramPooledConnection, SQLException paramSQLException)
  {
    super(paramPooledConnection);
    ex = paramSQLException;
  }
  
  public SQLException getSQLException()
  {
    return ex;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\ConnectionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */