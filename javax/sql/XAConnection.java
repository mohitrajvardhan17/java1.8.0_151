package javax.sql;

import java.sql.SQLException;
import javax.transaction.xa.XAResource;

public abstract interface XAConnection
  extends PooledConnection
{
  public abstract XAResource getXAResource()
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\XAConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */