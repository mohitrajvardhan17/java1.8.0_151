package java.sql;

public abstract interface RowId
{
  public abstract boolean equals(Object paramObject);
  
  public abstract byte[] getBytes();
  
  public abstract String toString();
  
  public abstract int hashCode();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\RowId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */