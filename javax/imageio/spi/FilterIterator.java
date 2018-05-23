package javax.imageio.spi;

import java.util.Iterator;
import java.util.NoSuchElementException;

class FilterIterator<T>
  implements Iterator<T>
{
  private Iterator<T> iter;
  private ServiceRegistry.Filter filter;
  private T next = null;
  
  public FilterIterator(Iterator<T> paramIterator, ServiceRegistry.Filter paramFilter)
  {
    iter = paramIterator;
    filter = paramFilter;
    advance();
  }
  
  private void advance()
  {
    while (iter.hasNext())
    {
      Object localObject = iter.next();
      if (filter.filter(localObject))
      {
        next = localObject;
        return;
      }
    }
    next = null;
  }
  
  public boolean hasNext()
  {
    return next != null;
  }
  
  public T next()
  {
    if (next == null) {
      throw new NoSuchElementException();
    }
    Object localObject = next;
    advance();
    return (T)localObject;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\FilterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */