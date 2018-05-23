package java.util;

public abstract interface ListIterator<E>
  extends Iterator<E>
{
  public abstract boolean hasNext();
  
  public abstract E next();
  
  public abstract boolean hasPrevious();
  
  public abstract E previous();
  
  public abstract int nextIndex();
  
  public abstract int previousIndex();
  
  public abstract void remove();
  
  public abstract void set(E paramE);
  
  public abstract void add(E paramE);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\ListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */