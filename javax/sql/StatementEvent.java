package javax.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EventObject;

public class StatementEvent
  extends EventObject
{
  static final long serialVersionUID = -8089573731826608315L;
  private SQLException exception;
  private PreparedStatement statement;
  
  public StatementEvent(PooledConnection paramPooledConnection, PreparedStatement paramPreparedStatement)
  {
    super(paramPooledConnection);
    statement = paramPreparedStatement;
    exception = null;
  }
  
  public StatementEvent(PooledConnection paramPooledConnection, PreparedStatement paramPreparedStatement, SQLException paramSQLException)
  {
    super(paramPooledConnection);
    statement = paramPreparedStatement;
    exception = paramSQLException;
  }
  
  public PreparedStatement getStatement()
  {
    return statement;
  }
  
  public SQLException getSQLException()
  {
    return exception;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\StatementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */