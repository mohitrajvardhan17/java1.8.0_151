package javax.sql.rowset;

import java.sql.SQLException;
import javax.sql.RowSet;

public abstract interface Predicate
{
  public abstract boolean evaluate(RowSet paramRowSet);
  
  public abstract boolean evaluate(Object paramObject, int paramInt)
    throws SQLException;
  
  public abstract boolean evaluate(Object paramObject, String paramString)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\Predicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */