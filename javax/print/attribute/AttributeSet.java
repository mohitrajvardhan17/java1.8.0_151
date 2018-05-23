package javax.print.attribute;

public abstract interface AttributeSet
{
  public abstract Attribute get(Class<?> paramClass);
  
  public abstract boolean add(Attribute paramAttribute);
  
  public abstract boolean remove(Class<?> paramClass);
  
  public abstract boolean remove(Attribute paramAttribute);
  
  public abstract boolean containsKey(Class<?> paramClass);
  
  public abstract boolean containsValue(Attribute paramAttribute);
  
  public abstract boolean addAll(AttributeSet paramAttributeSet);
  
  public abstract int size();
  
  public abstract Attribute[] toArray();
  
  public abstract void clear();
  
  public abstract boolean isEmpty();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\AttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */