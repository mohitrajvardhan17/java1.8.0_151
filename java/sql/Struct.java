package java.sql;

import java.util.Map;

public abstract interface Struct
{
  public abstract String getSQLTypeName()
    throws SQLException;
  
  public abstract Object[] getAttributes()
    throws SQLException;
  
  public abstract Object[] getAttributes(Map<String, Class<?>> paramMap)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\Struct.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */