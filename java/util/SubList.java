package java.util;

class SubList<E>
  extends AbstractList<E>
{
  private final AbstractList<E> l;
  private final int offset;
  private int size;
  
  SubList(AbstractList<E> paramAbstractList, int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IndexOutOfBoundsException("fromIndex = " + paramInt1);
    }
    if (paramInt2 > paramAbstractList.size()) {
      throw new IndexOutOfBoundsException("toIndex = " + paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("fromIndex(" + paramInt1 + ") > toIndex(" + paramInt2 + ")");
    }
    l = paramAbstractList;
    offset = paramInt1;
    size = (paramInt2 - paramInt1);
    modCount = l.modCount;
  }
  
  public E set(int paramInt, E paramE)
  {
    rangeCheck(paramInt);
    checkForComodification();
    return (E)l.set(paramInt + offset, paramE);
  }
  
  public E get(int paramInt)
  {
    rangeCheck(paramInt);
    checkForComodification();
    return (E)l.get(paramInt + offset);
  }
  
  public int size()
  {
    checkForComodification();
    return size;
  }
  
  public void add(int paramInt, E paramE)
  {
    rangeCheckForAdd(paramInt);
    checkForComodification();
    l.add(paramInt + offset, paramE);
    modCount = l.modCount;
    size += 1;
  }
  
  public E remove(int paramInt)
  {
    rangeCheck(paramInt);
    checkForComodification();
    Object localObject = l.remove(paramInt + offset);
    modCount = l.modCount;
    size -= 1;
    return (E)localObject;
  }
  
  protected void removeRange(int paramInt1, int paramInt2)
  {
    checkForComodification();
    l.removeRange(paramInt1 + offset, paramInt2 + offset);
    modCount = l.modCount;
    size -= paramInt2 - paramInt1;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    return addAll(size, paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    rangeCheckForAdd(paramInt);
    int i = paramCollection.size();
    if (i == 0) {
      return false;
    }
    checkForComodification();
    l.addAll(offset + paramInt, paramCollection);
    modCount = l.modCount;
    size += i;
    return true;
  }
  
  public Iterator<E> iterator()
  {
    return listIterator();
  }
  
  public ListIterator<E> listIterator(final int paramInt)
  {
    checkForComodification();
    rangeCheckForAdd(paramInt);
    new ListIterator()
    {
      private final ListIterator<E> i = l.listIterator(paramInt + offset);
      
      public boolean hasNext()
      {
        return nextIndex() < size;
      }
      
      public E next()
      {
        if (hasNext()) {
          return (E)i.next();
        }
        throw new NoSuchElementException();
      }
      
      public boolean hasPrevious()
      {
        return previousIndex() >= 0;
      }
      
      public E previous()
      {
        if (hasPrevious()) {
          return (E)i.previous();
        }
        throw new NoSuchElementException();
      }
      
      public int nextIndex()
      {
        return i.nextIndex() - offset;
      }
      
      public int previousIndex()
      {
        return i.previousIndex() - offset;
      }
      
      public void remove()
      {
        i.remove();
        modCount = l.modCount;
        SubList.access$210(SubList.this);
      }
      
      public void set(E paramAnonymousE)
      {
        i.set(paramAnonymousE);
      }
      
      public void add(E paramAnonymousE)
      {
        i.add(paramAnonymousE);
        modCount = l.modCount;
        SubList.access$208(SubList.this);
      }
    };
  }
  
  public List<E> subList(int paramInt1, int paramInt2)
  {
    return new SubList(this, paramInt1, paramInt2);
  }
  
  private void rangeCheck(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= size)) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  private void rangeCheckForAdd(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > size)) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  private String outOfBoundsMsg(int paramInt)
  {
    return "Index: " + paramInt + ", Size: " + size;
  }
  
  private void checkForComodification()
  {
    if (modCount != l.modCount) {
      throw new ConcurrentModificationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\SubList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */