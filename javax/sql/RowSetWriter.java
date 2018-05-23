package javax.sql;

import java.sql.SQLException;

public abstract interface RowSetWriter
{
  public abstract boolean writeData(RowSetInternal paramRowSetInternal)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\RowSetWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */