package java.sql;

public abstract interface Wrapper
{
  public abstract <T> T unwrap(Class<T> paramClass)
    throws SQLException;
  
  public abstract boolean isWrapperFor(Class<?> paramClass)
    throws SQLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\Wrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */