package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator
  implements Iterator
{
  public static final EmptyIterator instance = new EmptyIterator();
  
  private EmptyIterator() {}
  
  public static EmptyIterator getInstance()
  {
    return instance;
  }
  
  public boolean hasNext()
  {
    return false;
  }
  
  public Object next()
    throws NoSuchElementException
  {
    throw new NoSuchElementException();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.emptyIterator"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EmptyIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */