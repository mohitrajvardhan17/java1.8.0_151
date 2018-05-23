package java.sql;

public abstract interface SQLData
{
  public abstract String getSQLTypeName()
    throws SQLException;
  
  public abstract void readSQL(SQLInput paramSQLInput, String paramString)
    throws SQLException;
  
  public abstract void writeSQL(SQLOutput paramSQLOutput)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */