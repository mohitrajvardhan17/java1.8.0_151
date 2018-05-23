package java.util;

public abstract interface NavigableSet<E>
  extends SortedSet<E>
{
  public abstract E lower(E paramE);
  
  public abstract E floor(E paramE);
  
  public abstract E ceiling(E paramE);
  
  public abstract E higher(E paramE);
  
  public abstract E pollFirst();
  
  public abstract E pollLast();
  
  public abstract Iterator<E> iterator();
  
  public abstract NavigableSet<E> descendingSet();
  
  public abstract Iterator<E> descendingIterator();
  
  public abstract NavigableSet<E> subSet(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2);
  
  public abstract NavigableSet<E> headSet(E paramE, boolean paramBoolean);
  
  public abstract NavigableSet<E> tailSet(E paramE, boolean paramBoolean);
  
  public abstract SortedSet<E> subSet(E paramE1, E paramE2);
  
  public abstract SortedSet<E> headSet(E paramE);
  
  public abstract SortedSet<E> tailSet(E paramE);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\NavigableSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */