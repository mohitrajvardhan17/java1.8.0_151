package javax.sql;

import java.sql.SQLException;

public abstract interface RowSetReader
{
  public abstract void readData(RowSetInternal paramRowSetInternal)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\RowSetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */