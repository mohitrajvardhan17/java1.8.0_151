package javax.sql.rowset;

import java.sql.SQLException;
import java.util.Collection;
import javax.sql.RowSet;

public abstract interface JoinRowSet
  extends WebRowSet
{
  public static final int CROSS_JOIN = 0;
  public static final int INNER_JOIN = 1;
  public static final int LEFT_OUTER_JOIN = 2;
  public static final int RIGHT_OUTER_JOIN = 3;
  public static final int FULL_JOIN = 4;
  
  public abstract void addRowSet(Joinable paramJoinable)
    throws SQLException;
  
  public abstract void addRowSet(RowSet paramRowSet, int paramInt)
    throws SQLException;
  
  public abstract void addRowSet(RowSet paramRowSet, String paramString)
    throws SQLException;
  
  public abstract void addRowSet(RowSet[] paramArrayOfRowSet, int[] paramArrayOfInt)
    throws SQLException;
  
  public abstract void addRowSet(RowSet[] paramArrayOfRowSet, String[] paramArrayOfString)
    throws SQLException;
  
  public abstract Collection<?> getRowSets()
    throws SQLException;
  
  public abstract String[] getRowSetNames()
    throws SQLException;
  
  public abstract CachedRowSet toCachedRowSet()
    throws SQLException;
  
  public abstract boolean supportsCrossJoin();
  
  public abstract boolean supportsInnerJoin();
  
  public abstract boolean supportsLeftOuterJoin();
  
  public abstract boolean supportsRightOuterJoin();
  
  public abstract boolean supportsFullJoin();
  
  public abstract void setJoinType(int paramInt)
    throws SQLException;
  
  public abstract String getWhereClause()
    throws SQLException;
  
  public abstract int getJoinType()
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\JoinRowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */