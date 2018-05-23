package java.util;

public abstract class AbstractList<E>
  extends AbstractCollection<E>
  implements List<E>
{
  protected transient int modCount = 0;
  
  protected AbstractList() {}
  
  public boolean add(E paramE)
  {
    add(size(), paramE);
    return true;
  }
  
  public abstract E get(int paramInt);
  
  public E set(int paramInt, E paramE)
  {
    throw new UnsupportedOperationException();
  }
  
  public void add(int paramInt, E paramE)
  {
    throw new UnsupportedOperationException();
  }
  
  public E remove(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  public int indexOf(Object paramObject)
  {
    ListIterator localListIterator = listIterator();
    if (paramObject == null)
    {
      do
      {
        if (!localListIterator.hasNext()) {
          break;
        }
      } while (localListIterator.next() != null);
      return localListIterator.previousIndex();
    }
    while (localListIterator.hasNext()) {
      if (paramObject.equals(localListIterator.next())) {
        return localListIterator.previousIndex();
      }
    }
    return -1;
  }
  
  public int lastIndexOf(Object paramObject)
  {
    ListIterator localListIterator = listIterator(size());
    if (paramObject == null)
    {
      do
      {
        if (!localListIterator.hasPrevious()) {
          break;
        }
      } while (localListIterator.previous() != null);
      return localListIterator.nextIndex();
    }
    while (localListIterator.hasPrevious()) {
      if (paramObject.equals(localListIterator.previous())) {
        return localListIterator.nextIndex();
      }
    }
    return -1;
  }
  
  public void clear()
  {
    removeRange(0, size());
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    rangeCheckForAdd(paramInt);
    boolean bool = false;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      add(paramInt++, localObject);
      bool = true;
    }
    return bool;
  }
  
  public Iterator<E> iterator()
  {
    return new Itr(null);
  }
  
  public ListIterator<E> listIterator()
  {
    return listIterator(0);
  }
  
  public ListIterator<E> listIterator(int paramInt)
  {
    rangeCheckForAdd(paramInt);
    return new ListItr(paramInt);
  }
  
  public List<E> subList(int paramInt1, int paramInt2)
  {
    return (this instanceof RandomAccess) ? new RandomAccessSubList(this, paramInt1, paramInt2) : new SubList(this, paramInt1, paramInt2);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof List)) {
      return false;
    }
    ListIterator localListIterator1 = listIterator();
    ListIterator localListIterator2 = ((List)paramObject).listIterator();
    while ((localListIterator1.hasNext()) && (localListIterator2.hasNext()))
    {
      Object localObject1 = localListIterator1.next();
      Object localObject2 = localListIterator2.next();
      if (localObject1 == null ? localObject2 != null : !localObject1.equals(localObject2)) {
        return false;
      }
    }
    return (!localListIterator1.hasNext()) && (!localListIterator2.hasNext());
  }
  
  public int hashCode()
  {
    int i = 1;
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      i = 31 * i + (localObject == null ? 0 : localObject.hashCode());
    }
    return i;
  }
  
  protected void removeRange(int paramInt1, int paramInt2)
  {
    ListIterator localListIterator = listIterator(paramInt1);
    int i = 0;
    int j = paramInt2 - paramInt1;
    while (i < j)
    {
      localListIterator.next();
      localListIterator.remove();
      i++;
    }
  }
  
  private void rangeCheckForAdd(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > size())) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  private String outOfBoundsMsg(int paramInt)
  {
    return "Index: " + paramInt + ", Size: " + size();
  }
  
  private class Itr
    implements Iterator<E>
  {
    int cursor = 0;
    int lastRet = -1;
    int expectedModCount = modCount;
    
    private Itr() {}
    
    public boolean hasNext()
    {
      return cursor != size();
    }
    
    public E next()
    {
      checkForComodification();
      try
      {
        int i = cursor;
        Object localObject = get(i);
        lastRet = i;
        cursor = (i + 1);
        return (E)localObject;
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        checkForComodification();
        throw new NoSuchElementException();
      }
    }
    
    public void remove()
    {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      checkForComodification();
      try
      {
        remove(lastRet);
        if (lastRet < cursor) {
          cursor -= 1;
        }
        lastRet = -1;
        expectedModCount = modCount;
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        throw new ConcurrentModificationException();
      }
    }
    
    final void checkForComodification()
    {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  private class ListItr
    extends AbstractList<E>.Itr
    implements ListIterator<E>
  {
    ListItr(int paramInt)
    {
      super(null);
      cursor = paramInt;
    }
    
    public boolean hasPrevious()
    {
      return cursor != 0;
    }
    
    public E previous()
    {
      checkForComodification();
      try
      {
        int i = cursor - 1;
        Object localObject = get(i);
        lastRet = (cursor = i);
        return (E)localObject;
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        checkForComodification();
        throw new NoSuchElementException();
      }
    }
    
    public int nextIndex()
    {
      return cursor;
    }
    
    public int previousIndex()
    {
      return cursor - 1;
    }
    
    public void set(E paramE)
    {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      checkForComodification();
      try
      {
        set(lastRet, paramE);
        expectedModCount = modCount;
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        throw new ConcurrentModificationException();
      }
    }
    
    public void add(E paramE)
    {
      checkForComodification();
      try
      {
        int i = cursor;
        add(i, paramE);
        lastRet = -1;
        cursor = (i + 1);
        expectedModCount = modCount;
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        throw new ConcurrentModificationException();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\AbstractList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */