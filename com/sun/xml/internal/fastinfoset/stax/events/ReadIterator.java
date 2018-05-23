package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;

public class ReadIterator
  implements Iterator
{
  Iterator iterator = EmptyIterator.getInstance();
  
  public ReadIterator() {}
  
  public ReadIterator(Iterator paramIterator)
  {
    if (paramIterator != null) {
      iterator = paramIterator;
    }
  }
  
  public boolean hasNext()
  {
    return iterator.hasNext();
  }
  
  public Object next()
  {
    return iterator.next();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.readonlyList"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\ReadIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */