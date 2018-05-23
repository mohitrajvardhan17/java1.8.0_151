package java.util;

class RegularEnumSet<E extends Enum<E>>
  extends EnumSet<E>
{
  private static final long serialVersionUID = 3411599620347842686L;
  private long elements = 0L;
  
  RegularEnumSet(Class<E> paramClass, Enum<?>[] paramArrayOfEnum)
  {
    super(paramClass, paramArrayOfEnum);
  }
  
  void addRange(E paramE1, E paramE2)
  {
    elements = (-1L >>> paramE1.ordinal() - paramE2.ordinal() - 1 << paramE1.ordinal());
  }
  
  void addAll()
  {
    if (universe.length != 0) {
      elements = (-1L >>> -universe.length);
    }
  }
  
  void complement()
  {
    if (universe.length != 0)
    {
      elements ^= 0xFFFFFFFFFFFFFFFF;
      elements &= -1L >>> -universe.length;
    }
  }
  
  public Iterator<E> iterator()
  {
    return new EnumSetIterator();
  }
  
  public int size()
  {
    return Long.bitCount(elements);
  }
  
  public boolean isEmpty()
  {
    return elements == 0L;
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    Class localClass = paramObject.getClass();
    if ((localClass != elementType) && (localClass.getSuperclass() != elementType)) {
      return false;
    }
    return (elements & 1L << ((Enum)paramObject).ordinal()) != 0L;
  }
  
  public boolean add(E paramE)
  {
    typeCheck(paramE);
    long l = elements;
    elements |= 1L << paramE.ordinal();
    return elements != l;
  }
  
  public boolean remove(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    Class localClass = paramObject.getClass();
    if ((localClass != elementType) && (localClass.getSuperclass() != elementType)) {
      return false;
    }
    long l = elements;
    elements &= (1L << ((Enum)paramObject).ordinal() ^ 0xFFFFFFFFFFFFFFFF);
    return elements != l;
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    if (!(paramCollection instanceof RegularEnumSet)) {
      return super.containsAll(paramCollection);
    }
    RegularEnumSet localRegularEnumSet = (RegularEnumSet)paramCollection;
    if (elementType != elementType) {
      return localRegularEnumSet.isEmpty();
    }
    return (elements & (elements ^ 0xFFFFFFFFFFFFFFFF)) == 0L;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    if (!(paramCollection instanceof RegularEnumSet)) {
      return super.addAll(paramCollection);
    }
    RegularEnumSet localRegularEnumSet = (RegularEnumSet)paramCollection;
    if (elementType != elementType)
    {
      if (localRegularEnumSet.isEmpty()) {
        return false;
      }
      throw new ClassCastException(elementType + " != " + elementType);
    }
    long l = elements;
    elements |= elements;
    return elements != l;
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    if (!(paramCollection instanceof RegularEnumSet)) {
      return super.removeAll(paramCollection);
    }
    RegularEnumSet localRegularEnumSet = (RegularEnumSet)paramCollection;
    if (elementType != elementType) {
      return false;
    }
    long l = elements;
    elements &= (elements ^ 0xFFFFFFFFFFFFFFFF);
    return elements != l;
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    if (!(paramCollection instanceof RegularEnumSet)) {
      return super.retainAll(paramCollection);
    }
    RegularEnumSet localRegularEnumSet = (RegularEnumSet)paramCollection;
    if (elementType != elementType)
    {
      boolean bool = elements != 0L;
      elements = 0L;
      return bool;
    }
    long l = elements;
    elements &= elements;
    return elements != l;
  }
  
  public void clear()
  {
    elements = 0L;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof RegularEnumSet)) {
      return super.equals(paramObject);
    }
    RegularEnumSet localRegularEnumSet = (RegularEnumSet)paramObject;
    if (elementType != elementType) {
      return (elements == 0L) && (elements == 0L);
    }
    return elements == elements;
  }
  
  private class EnumSetIterator<E extends Enum<E>>
    implements Iterator<E>
  {
    long unseen = elements;
    long lastReturned = 0L;
    
    EnumSetIterator() {}
    
    public boolean hasNext()
    {
      return unseen != 0L;
    }
    
    public E next()
    {
      if (unseen == 0L) {
        throw new NoSuchElementException();
      }
      lastReturned = (unseen & -unseen);
      unseen -= lastReturned;
      return universe[Long.numberOfTrailingZeros(lastReturned)];
    }
    
    public void remove()
    {
      if (lastReturned == 0L) {
        throw new IllegalStateException();
      }
      elements = (elements & (lastReturned ^ 0xFFFFFFFFFFFFFFFF));
      lastReturned = 0L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\RegularEnumSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */