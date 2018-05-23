package javax.sql.rowset;

import java.sql.SQLException;

public abstract interface FilteredRowSet
  extends WebRowSet
{
  public abstract void setFilter(Predicate paramPredicate)
    throws SQLException;
  
  public abstract Predicate getFilter();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\FilteredRowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */