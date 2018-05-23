package java.util;

public abstract class AbstractSequentialList<E>
  extends AbstractList<E>
{
  protected AbstractSequentialList() {}
  
  public E get(int paramInt)
  {
    try
    {
      return (E)listIterator(paramInt).next();
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
  }
  
  public E set(int paramInt, E paramE)
  {
    try
    {
      ListIterator localListIterator = listIterator(paramInt);
      Object localObject = localListIterator.next();
      localListIterator.set(paramE);
      return (E)localObject;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
  }
  
  public void add(int paramInt, E paramE)
  {
    try
    {
      listIterator(paramInt).add(paramE);
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
  }
  
  public E remove(int paramInt)
  {
    try
    {
      ListIterator localListIterator = listIterator(paramInt);
      Object localObject = localListIterator.next();
      localListIterator.remove();
      return (E)localObject;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    try
    {
      boolean bool = false;
      ListIterator localListIterator = listIterator(paramInt);
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        localListIterator.add(localIterator.next());
        bool = true;
      }
      return bool;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
  }
  
  public Iterator<E> iterator()
  {
    return listIterator();
  }
  
  public abstract ListIterator<E> listIterator(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\AbstractSequentialList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */