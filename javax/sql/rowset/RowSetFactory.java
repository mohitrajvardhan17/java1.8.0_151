package javax.sql.rowset;

import java.sql.SQLException;

public abstract interface RowSetFactory
{
  public abstract CachedRowSet createCachedRowSet()
    throws SQLException;
  
  public abstract FilteredRowSet createFilteredRowSet()
    throws SQLException;
  
  public abstract JdbcRowSet createJdbcRowSet()
    throws SQLException;
  
  public abstract JoinRowSet createJoinRowSet()
    throws SQLException;
  
  public abstract WebRowSet createWebRowSet()
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\RowSetFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */