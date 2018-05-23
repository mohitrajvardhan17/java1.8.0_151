package java.util;

class JumboEnumSet<E extends Enum<E>>
  extends EnumSet<E>
{
  private static final long serialVersionUID = 334349849919042784L;
  private long[] elements;
  private int size = 0;
  
  JumboEnumSet(Class<E> paramClass, Enum<?>[] paramArrayOfEnum)
  {
    super(paramClass, paramArrayOfEnum);
    elements = new long[paramArrayOfEnum.length + 63 >>> 6];
  }
  
  void addRange(E paramE1, E paramE2)
  {
    int i = paramE1.ordinal() >>> 6;
    int j = paramE2.ordinal() >>> 6;
    if (i == j)
    {
      elements[i] = (-1L >>> paramE1.ordinal() - paramE2.ordinal() - 1 << paramE1.ordinal());
    }
    else
    {
      elements[i] = (-1L << paramE1.ordinal());
      for (int k = i + 1; k < j; k++) {
        elements[k] = -1L;
      }
      elements[j] = (-1L >>> 63 - paramE2.ordinal());
    }
    size = (paramE2.ordinal() - paramE1.ordinal() + 1);
  }
  
  void addAll()
  {
    for (int i = 0; i < elements.length; i++) {
      elements[i] = -1L;
    }
    elements[(elements.length - 1)] >>>= -universe.length;
    size = universe.length;
  }
  
  void complement()
  {
    for (int i = 0; i < elements.length; i++) {
      elements[i] ^= 0xFFFFFFFFFFFFFFFF;
    }
    elements[(elements.length - 1)] &= -1L >>> -universe.length;
    size = (universe.length - size);
  }
  
  public Iterator<E> iterator()
  {
    return new EnumSetIterator();
  }
  
  public int size()
  {
    return size;
  }
  
  public boolean isEmpty()
  {
    return size == 0;
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
    int i = ((Enum)paramObject).ordinal();
    return (elements[(i >>> 6)] & 1L << i) != 0L;
  }
  
  public boolean add(E paramE)
  {
    typeCheck(paramE);
    int i = paramE.ordinal();
    int j = i >>> 6;
    long l = elements[j];
    elements[j] |= 1L << i;
    boolean bool = elements[j] != l;
    if (bool) {
      size += 1;
    }
    return bool;
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
    int i = ((Enum)paramObject).ordinal();
    int j = i >>> 6;
    long l = elements[j];
    elements[j] &= (1L << i ^ 0xFFFFFFFFFFFFFFFF);
    boolean bool = elements[j] != l;
    if (bool) {
      size -= 1;
    }
    return bool;
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    if (!(paramCollection instanceof JumboEnumSet)) {
      return super.containsAll(paramCollection);
    }
    JumboEnumSet localJumboEnumSet = (JumboEnumSet)paramCollection;
    if (elementType != elementType) {
      return localJumboEnumSet.isEmpty();
    }
    for (int i = 0; i < elements.length; i++) {
      if ((elements[i] & (elements[i] ^ 0xFFFFFFFFFFFFFFFF)) != 0L) {
        return false;
      }
    }
    return true;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    if (!(paramCollection instanceof JumboEnumSet)) {
      return super.addAll(paramCollection);
    }
    JumboEnumSet localJumboEnumSet = (JumboEnumSet)paramCollection;
    if (elementType != elementType)
    {
      if (localJumboEnumSet.isEmpty()) {
        return false;
      }
      throw new ClassCastException(elementType + " != " + elementType);
    }
    for (int i = 0; i < elements.length; i++) {
      elements[i] |= elements[i];
    }
    return recalculateSize();
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    if (!(paramCollection instanceof JumboEnumSet)) {
      return super.removeAll(paramCollection);
    }
    JumboEnumSet localJumboEnumSet = (JumboEnumSet)paramCollection;
    if (elementType != elementType) {
      return false;
    }
    for (int i = 0; i < elements.length; i++) {
      elements[i] &= (elements[i] ^ 0xFFFFFFFFFFFFFFFF);
    }
    return recalculateSize();
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    if (!(paramCollection instanceof JumboEnumSet)) {
      return super.retainAll(paramCollection);
    }
    JumboEnumSet localJumboEnumSet = (JumboEnumSet)paramCollection;
    if (elementType != elementType)
    {
      i = size != 0 ? 1 : 0;
      clear();
      return i;
    }
    for (int i = 0; i < elements.length; i++) {
      elements[i] &= elements[i];
    }
    return recalculateSize();
  }
  
  public void clear()
  {
    Arrays.fill(elements, 0L);
    size = 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof JumboEnumSet)) {
      return super.equals(paramObject);
    }
    JumboEnumSet localJumboEnumSet = (JumboEnumSet)paramObject;
    if (elementType != elementType) {
      return (size == 0) && (size == 0);
    }
    return Arrays.equals(elements, elements);
  }
  
  private boolean recalculateSize()
  {
    int i = size;
    size = 0;
    for (long l : elements) {
      size += Long.bitCount(l);
    }
    return size != i;
  }
  
  public EnumSet<E> clone()
  {
    JumboEnumSet localJumboEnumSet = (JumboEnumSet)super.clone();
    elements = ((long[])elements.clone());
    return localJumboEnumSet;
  }
  
  private class EnumSetIterator<E extends Enum<E>>
    implements Iterator<E>
  {
    long unseen = elements[0];
    int unseenIndex = 0;
    long lastReturned = 0L;
    int lastReturnedIndex = 0;
    
    EnumSetIterator() {}
    
    public boolean hasNext()
    {
      while ((unseen == 0L) && (unseenIndex < elements.length - 1)) {
        unseen = elements[(++unseenIndex)];
      }
      return unseen != 0L;
    }
    
    public E next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      lastReturned = (unseen & -unseen);
      lastReturnedIndex = unseenIndex;
      unseen -= lastReturned;
      return universe[((lastReturnedIndex << 6) + Long.numberOfTrailingZeros(lastReturned))];
    }
    
    public void remove()
    {
      if (lastReturned == 0L) {
        throw new IllegalStateException();
      }
      long l = elements[lastReturnedIndex];
      elements[lastReturnedIndex] &= (lastReturned ^ 0xFFFFFFFFFFFFFFFF);
      if (l != elements[lastReturnedIndex]) {
        JumboEnumSet.access$110(JumboEnumSet.this);
      }
      lastReturned = 0L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\JumboEnumSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */