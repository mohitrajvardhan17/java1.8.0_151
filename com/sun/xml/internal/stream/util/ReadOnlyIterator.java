package com.sun.xml.internal.stream.util;

import java.util.Iterator;

public class ReadOnlyIterator
  implements Iterator
{
  Iterator iterator = null;
  
  public ReadOnlyIterator() {}
  
  public ReadOnlyIterator(Iterator paramIterator)
  {
    iterator = paramIterator;
  }
  
  public boolean hasNext()
  {
    if (iterator != null) {
      return iterator.hasNext();
    }
    return false;
  }
  
  public Object next()
  {
    if (iterator != null) {
      return iterator.next();
    }
    return null;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException("Remove operation is not supported");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\util\ReadOnlyIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */