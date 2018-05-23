package javax.management.openmbean;

import java.util.Collection;
import java.util.Set;

public abstract interface TabularData
{
  public abstract TabularType getTabularType();
  
  public abstract Object[] calculateIndex(CompositeData paramCompositeData);
  
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract boolean containsKey(Object[] paramArrayOfObject);
  
  public abstract boolean containsValue(CompositeData paramCompositeData);
  
  public abstract CompositeData get(Object[] paramArrayOfObject);
  
  public abstract void put(CompositeData paramCompositeData);
  
  public abstract CompositeData remove(Object[] paramArrayOfObject);
  
  public abstract void putAll(CompositeData[] paramArrayOfCompositeData);
  
  public abstract void clear();
  
  public abstract Set<?> keySet();
  
  public abstract Collection<?> values();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\TabularData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */