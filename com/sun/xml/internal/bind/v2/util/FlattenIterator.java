package com.sun.xml.internal.bind.v2.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class FlattenIterator<T>
  implements Iterator<T>
{
  private final Iterator<? extends Map<?, ? extends T>> parent;
  private Iterator<? extends T> child = null;
  private T next;
  
  public FlattenIterator(Iterable<? extends Map<?, ? extends T>> paramIterable)
  {
    parent = paramIterable.iterator();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean hasNext()
  {
    getNext();
    return next != null;
  }
  
  public T next()
  {
    Object localObject = next;
    next = null;
    if (localObject == null) {
      throw new NoSuchElementException();
    }
    return (T)localObject;
  }
  
  private void getNext()
  {
    if (next != null) {
      return;
    }
    if ((child != null) && (child.hasNext()))
    {
      next = child.next();
      return;
    }
    if (parent.hasNext())
    {
      child = ((Map)parent.next()).values().iterator();
      getNext();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\util\FlattenIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */