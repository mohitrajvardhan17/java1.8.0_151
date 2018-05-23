package javax.sql.rowset.spi;

import java.sql.SQLException;
import java.sql.Savepoint;
import javax.sql.RowSetWriter;

public abstract interface TransactionalWriter
  extends RowSetWriter
{
  public abstract void commit()
    throws SQLException;
  
  public abstract void rollback()
    throws SQLException;
  
  public abstract void rollback(Savepoint paramSavepoint)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\spi\TransactionalWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */