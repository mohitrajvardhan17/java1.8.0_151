package java.util;

public abstract interface SortedSet<E>
  extends Set<E>
{
  public abstract Comparator<? super E> comparator();
  
  public abstract SortedSet<E> subSet(E paramE1, E paramE2);
  
  public abstract SortedSet<E> headSet(E paramE);
  
  public abstract SortedSet<E> tailSet(E paramE);
  
  public abstract E first();
  
  public abstract E last();
  
  public Spliterator<E> spliterator()
  {
    new Spliterators.IteratorSpliterator(this, 21)
    {
      public Comparator<? super E> getComparator()
      {
        return comparator();
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\SortedSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */