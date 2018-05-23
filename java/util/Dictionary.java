package java.util;

public abstract class Dictionary<K, V>
{
  public Dictionary() {}
  
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract Enumeration<K> keys();
  
  public abstract Enumeration<V> elements();
  
  public abstract V get(Object paramObject);
  
  public abstract V put(K paramK, V paramV);
  
  public abstract V remove(Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Dictionary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */