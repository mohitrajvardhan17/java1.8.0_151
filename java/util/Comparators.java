package java.util;

import java.io.Serializable;

class Comparators
{
  private Comparators()
  {
    throw new AssertionError("no instances");
  }
  
  static enum NaturalOrderComparator
    implements Comparator<Comparable<Object>>
  {
    INSTANCE;
    
    private NaturalOrderComparator() {}
    
    public int compare(Comparable<Object> paramComparable1, Comparable<Object> paramComparable2)
    {
      return paramComparable1.compareTo(paramComparable2);
    }
    
    public Comparator<Comparable<Object>> reversed()
    {
      return Comparator.reverseOrder();
    }
  }
  
  static final class NullComparator<T>
    implements Comparator<T>, Serializable
  {
    private static final long serialVersionUID = -7569533591570686392L;
    private final boolean nullFirst;
    private final Comparator<T> real;
    
    NullComparator(boolean paramBoolean, Comparator<? super T> paramComparator)
    {
      nullFirst = paramBoolean;
      real = paramComparator;
    }
    
    public int compare(T paramT1, T paramT2)
    {
      if (paramT1 == null) {
        return nullFirst ? -1 : paramT2 == null ? 0 : 1;
      }
      if (paramT2 == null) {
        return nullFirst ? 1 : -1;
      }
      return real == null ? 0 : real.compare(paramT1, paramT2);
    }
    
    public Comparator<T> thenComparing(Comparator<? super T> paramComparator)
    {
      Objects.requireNonNull(paramComparator);
      return new NullComparator(nullFirst, real == null ? paramComparator : real.thenComparing(paramComparator));
    }
    
    public Comparator<T> reversed()
    {
      return new NullComparator(!nullFirst, real == null ? null : real.reversed());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Comparators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */