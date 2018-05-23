package java.sql;

import java.util.Map;

public abstract interface Ref
{
  public abstract String getBaseTypeName()
    throws SQLException;
  
  public abstract Object getObject(Map<String, Class<?>> paramMap)
    throws SQLException;
  
  public abstract Object getObject()
    throws SQLException;
  
  public abstract void setObject(Object paramObject)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\Ref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */