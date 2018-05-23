package sun.awt.util;

import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IdentityLinkedList<E>
  extends AbstractSequentialList<E>
  implements List<E>, Deque<E>
{
  private transient Entry<E> header = new Entry(null, null, null);
  private transient int size = 0;
  
  public IdentityLinkedList()
  {
    header.next = (header.previous = header);
  }
  
  public IdentityLinkedList(Collection<? extends E> paramCollection)
  {
    this();
    addAll(paramCollection);
  }
  
  public E getFirst()
  {
    if (size == 0) {
      throw new NoSuchElementException();
    }
    return (E)header.next.element;
  }
  
  public E getLast()
  {
    if (size == 0) {
      throw new NoSuchElementException();
    }
    return (E)header.previous.element;
  }
  
  public E removeFirst()
  {
    return (E)remove(header.next);
  }
  
  public E removeLast()
  {
    return (E)remove(header.previous);
  }
  
  public void addFirst(E paramE)
  {
    addBefore(paramE, header.next);
  }
  
  public void addLast(E paramE)
  {
    addBefore(paramE, header);
  }
  
  public boolean contains(Object paramObject)
  {
    return indexOf(paramObject) != -1;
  }
  
  public int size()
  {
    return size;
  }
  
  public boolean add(E paramE)
  {
    addBefore(paramE, header);
    return true;
  }
  
  public boolean remove(Object paramObject)
  {
    for (Entry localEntry = header.next; localEntry != header; localEntry = next) {
      if (paramObject == element)
      {
        remove(localEntry);
        return true;
      }
    }
    return false;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    return addAll(size, paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    if ((paramInt < 0) || (paramInt > size)) {
      throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + size);
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    if (i == 0) {
      return false;
    }
    modCount += 1;
    Entry localEntry1 = paramInt == size ? header : entry(paramInt);
    Object localObject = previous;
    for (int j = 0; j < i; j++)
    {
      Entry localEntry2 = new Entry(arrayOfObject[j], localEntry1, (Entry)localObject);
      next = localEntry2;
      localObject = localEntry2;
    }
    previous = ((Entry)localObject);
    size += i;
    return true;
  }
  
  public void clear()
  {
    Entry localEntry;
    for (Object localObject = header.next; localObject != header; localObject = localEntry)
    {
      localEntry = next;
      next = (previous = null);
      element = null;
    }
    header.next = (header.previous = header);
    size = 0;
    modCount += 1;
  }
  
  public E get(int paramInt)
  {
    return (E)entryelement;
  }
  
  public E set(int paramInt, E paramE)
  {
    Entry localEntry = entry(paramInt);
    Object localObject = element;
    element = paramE;
    return (E)localObject;
  }
  
  public void add(int paramInt, E paramE)
  {
    addBefore(paramE, paramInt == size ? header : entry(paramInt));
  }
  
  public E remove(int paramInt)
  {
    return (E)remove(entry(paramInt));
  }
  
  private Entry<E> entry(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= size)) {
      throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + size);
    }
    Entry localEntry = header;
    int i;
    if (paramInt < size >> 1) {
      for (i = 0; i <= paramInt; i++) {
        localEntry = next;
      }
    } else {
      for (i = size; i > paramInt; i--) {
        localEntry = previous;
      }
    }
    return localEntry;
  }
  
  public int indexOf(Object paramObject)
  {
    int i = 0;
    for (Entry localEntry = header.next; localEntry != header; localEntry = next)
    {
      if (paramObject == element) {
        return i;
      }
      i++;
    }
    return -1;
  }
  
  public int lastIndexOf(Object paramObject)
  {
    int i = size;
    for (Entry localEntry = header.previous; localEntry != header; localEntry = previous)
    {
      i--;
      if (paramObject == element) {
        return i;
      }
    }
    return -1;
  }
  
  public E peek()
  {
    if (size == 0) {
      return null;
    }
    return (E)getFirst();
  }
  
  public E element()
  {
    return (E)getFirst();
  }
  
  public E poll()
  {
    if (size == 0) {
      return null;
    }
    return (E)removeFirst();
  }
  
  public E remove()
  {
    return (E)removeFirst();
  }
  
  public boolean offer(E paramE)
  {
    return add(paramE);
  }
  
  public boolean offerFirst(E paramE)
  {
    addFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE)
  {
    addLast(paramE);
    return true;
  }
  
  public E peekFirst()
  {
    if (size == 0) {
      return null;
    }
    return (E)getFirst();
  }
  
  public E peekLast()
  {
    if (size == 0) {
      return null;
    }
    return (E)getLast();
  }
  
  public E pollFirst()
  {
    if (size == 0) {
      return null;
    }
    return (E)removeFirst();
  }
  
  public E pollLast()
  {
    if (size == 0) {
      return null;
    }
    return (E)removeLast();
  }
  
  public void push(E paramE)
  {
    addFirst(paramE);
  }
  
  public E pop()
  {
    return (E)removeFirst();
  }
  
  public boolean removeFirstOccurrence(Object paramObject)
  {
    return remove(paramObject);
  }
  
  public boolean removeLastOccurrence(Object paramObject)
  {
    for (Entry localEntry = header.previous; localEntry != header; localEntry = previous) {
      if (paramObject == element)
      {
        remove(localEntry);
        return true;
      }
    }
    return false;
  }
  
  public ListIterator<E> listIterator(int paramInt)
  {
    return new ListItr(paramInt);
  }
  
  private Entry<E> addBefore(E paramE, Entry<E> paramEntry)
  {
    Entry localEntry = new Entry(paramE, paramEntry, previous);
    previous.next = localEntry;
    next.previous = localEntry;
    size += 1;
    modCount += 1;
    return localEntry;
  }
  
  private E remove(Entry<E> paramEntry)
  {
    if (paramEntry == header) {
      throw new NoSuchElementException();
    }
    Object localObject = element;
    previous.next = next;
    next.previous = previous;
    next = (previous = null);
    element = null;
    size -= 1;
    modCount += 1;
    return (E)localObject;
  }
  
  public Iterator<E> descendingIterator()
  {
    return new DescendingIterator(null);
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[size];
    int i = 0;
    for (Entry localEntry = header.next; localEntry != header; localEntry = next) {
      arrayOfObject[(i++)] = element;
    }
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    if (paramArrayOfT.length < size) {
      paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), size);
    }
    int i = 0;
    T[] arrayOfT = paramArrayOfT;
    for (Entry localEntry = header.next; localEntry != header; localEntry = next) {
      arrayOfT[(i++)] = element;
    }
    if (paramArrayOfT.length > size) {
      paramArrayOfT[size] = null;
    }
    return paramArrayOfT;
  }
  
  private class DescendingIterator
    implements Iterator
  {
    final IdentityLinkedList<E>.ListItr itr = new IdentityLinkedList.ListItr(IdentityLinkedList.this, size());
    
    private DescendingIterator() {}
    
    public boolean hasNext()
    {
      return itr.hasPrevious();
    }
    
    public E next()
    {
      return (E)itr.previous();
    }
    
    public void remove()
    {
      itr.remove();
    }
  }
  
  private static class Entry<E>
  {
    E element;
    Entry<E> next;
    Entry<E> previous;
    
    Entry(E paramE, Entry<E> paramEntry1, Entry<E> paramEntry2)
    {
      element = paramE;
      next = paramEntry1;
      previous = paramEntry2;
    }
  }
  
  private class ListItr
    implements ListIterator<E>
  {
    private IdentityLinkedList.Entry<E> lastReturned = header;
    private IdentityLinkedList.Entry<E> next;
    private int nextIndex;
    private int expectedModCount = modCount;
    
    ListItr(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > size)) {
        throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + size);
      }
      if (paramInt < size >> 1)
      {
        next = header.next;
        for (nextIndex = 0; nextIndex < paramInt; nextIndex += 1) {
          next = next.next;
        }
      }
      next = header;
      for (nextIndex = size; nextIndex > paramInt; nextIndex -= 1) {
        next = next.previous;
      }
    }
    
    public boolean hasNext()
    {
      return nextIndex != size;
    }
    
    public E next()
    {
      checkForComodification();
      if (nextIndex == size) {
        throw new NoSuchElementException();
      }
      lastReturned = next;
      next = next.next;
      nextIndex += 1;
      return (E)lastReturned.element;
    }
    
    public boolean hasPrevious()
    {
      return nextIndex != 0;
    }
    
    public E previous()
    {
      if (nextIndex == 0) {
        throw new NoSuchElementException();
      }
      lastReturned = (next = next.previous);
      nextIndex -= 1;
      checkForComodification();
      return (E)lastReturned.element;
    }
    
    public int nextIndex()
    {
      return nextIndex;
    }
    
    public int previousIndex()
    {
      return nextIndex - 1;
    }
    
    public void remove()
    {
      checkForComodification();
      IdentityLinkedList.Entry localEntry = lastReturned.next;
      try
      {
        IdentityLinkedList.this.remove(lastReturned);
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        throw new IllegalStateException();
      }
      if (next == lastReturned) {
        next = localEntry;
      } else {
        nextIndex -= 1;
      }
      lastReturned = header;
      expectedModCount += 1;
    }
    
    public void set(E paramE)
    {
      if (lastReturned == header) {
        throw new IllegalStateException();
      }
      checkForComodification();
      lastReturned.element = paramE;
    }
    
    public void add(E paramE)
    {
      checkForComodification();
      lastReturned = header;
      IdentityLinkedList.this.addBefore(paramE, next);
      nextIndex += 1;
      expectedModCount += 1;
    }
    
    final void checkForComodification()
    {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\util\IdentityLinkedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */