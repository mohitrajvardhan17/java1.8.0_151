package com.sun.java.util.jar.pack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

final class FixedList<E>
  implements List<E>
{
  private final ArrayList<E> flist;
  
  protected FixedList(int paramInt)
  {
    flist = new ArrayList(paramInt);
    for (int i = 0; i < paramInt; i++) {
      flist.add(null);
    }
  }
  
  public int size()
  {
    return flist.size();
  }
  
  public boolean isEmpty()
  {
    return flist.isEmpty();
  }
  
  public boolean contains(Object paramObject)
  {
    return flist.contains(paramObject);
  }
  
  public Iterator<E> iterator()
  {
    return flist.iterator();
  }
  
  public Object[] toArray()
  {
    return flist.toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    return flist.toArray(paramArrayOfT);
  }
  
  public boolean add(E paramE)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public boolean remove(Object paramObject)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    return flist.containsAll(paramCollection);
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public boolean removeAll(Collection<?> paramCollection)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public boolean retainAll(Collection<?> paramCollection)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public void clear()
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public E get(int paramInt)
  {
    return (E)flist.get(paramInt);
  }
  
  public E set(int paramInt, E paramE)
  {
    return (E)flist.set(paramInt, paramE);
  }
  
  public void add(int paramInt, E paramE)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public E remove(int paramInt)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("operation not permitted");
  }
  
  public int indexOf(Object paramObject)
  {
    return flist.indexOf(paramObject);
  }
  
  public int lastIndexOf(Object paramObject)
  {
    return flist.lastIndexOf(paramObject);
  }
  
  public ListIterator<E> listIterator()
  {
    return flist.listIterator();
  }
  
  public ListIterator<E> listIterator(int paramInt)
  {
    return flist.listIterator(paramInt);
  }
  
  public List<E> subList(int paramInt1, int paramInt2)
  {
    return flist.subList(paramInt1, paramInt2);
  }
  
  public String toString()
  {
    return "FixedList{plist=" + flist + '}';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\FixedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */