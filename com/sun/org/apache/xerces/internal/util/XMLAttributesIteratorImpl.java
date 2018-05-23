package com.sun.org.apache.xerces.internal.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class XMLAttributesIteratorImpl
  extends XMLAttributesImpl
  implements Iterator
{
  protected int fCurrent = 0;
  protected XMLAttributesImpl.Attribute fLastReturnedItem;
  
  public XMLAttributesIteratorImpl() {}
  
  public boolean hasNext()
  {
    return fCurrent < getLength();
  }
  
  public Object next()
  {
    if (hasNext()) {
      return fLastReturnedItem = fAttributes[(fCurrent++)];
    }
    throw new NoSuchElementException();
  }
  
  public void remove()
  {
    if (fLastReturnedItem == fAttributes[(fCurrent - 1)]) {
      removeAttributeAt(fCurrent--);
    } else {
      throw new IllegalStateException();
    }
  }
  
  public void removeAllAttributes()
  {
    super.removeAllAttributes();
    fCurrent = 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\XMLAttributesIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */