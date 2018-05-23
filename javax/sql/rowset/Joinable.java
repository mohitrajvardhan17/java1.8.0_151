package javax.sql.rowset;

import java.sql.SQLException;

public abstract interface Joinable
{
  public abstract void setMatchColumn(int paramInt)
    throws SQLException;
  
  public abstract void setMatchColumn(int[] paramArrayOfInt)
    throws SQLException;
  
  public abstract void setMatchColumn(String paramString)
    throws SQLException;
  
  public abstract void setMatchColumn(String[] paramArrayOfString)
    throws SQLException;
  
  public abstract int[] getMatchColumnIndexes()
    throws SQLException;
  
  public abstract String[] getMatchColumnNames()
    throws SQLException;
  
  public abstract void unsetMatchColumn(int paramInt)
    throws SQLException;
  
  public abstract void unsetMatchColumn(int[] paramArrayOfInt)
    throws SQLException;
  
  public abstract void unsetMatchColumn(String paramString)
    throws SQLException;
  
  public abstract void unsetMatchColumn(String[] paramArrayOfString)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\Joinable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */