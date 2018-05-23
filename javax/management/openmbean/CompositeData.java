package javax.management.openmbean;

import java.util.Collection;

public abstract interface CompositeData
{
  public abstract CompositeType getCompositeType();
  
  public abstract Object get(String paramString);
  
  public abstract Object[] getAll(String[] paramArrayOfString);
  
  public abstract boolean containsKey(String paramString);
  
  public abstract boolean containsValue(Object paramObject);
  
  public abstract Collection<?> values();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\CompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */