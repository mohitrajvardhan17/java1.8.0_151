package java.util;

public abstract class AbstractSet<E>
  extends AbstractCollection<E>
  implements Set<E>
{
  protected AbstractSet() {}
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Set)) {
      return false;
    }
    Collection localCollection = (Collection)paramObject;
    if (localCollection.size() != size()) {
      return false;
    }
    try
    {
      return containsAll(localCollection);
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException) {}
    return false;
  }
  
  public int hashCode()
  {
    int i = 0;
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (localObject != null) {
        i += localObject.hashCode();
      }
    }
    return i;
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    Objects.requireNonNull(paramCollection);
    boolean bool = false;
    Iterator localIterator;
    if (size() > paramCollection.size())
    {
      localIterator = paramCollection.iterator();
      while (localIterator.hasNext()) {
        bool |= remove(localIterator.next());
      }
    }
    else
    {
      localIterator = iterator();
      while (localIterator.hasNext()) {
        if (paramCollection.contains(localIterator.next()))
        {
          localIterator.remove();
          bool = true;
        }
      }
    }
    return bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\AbstractSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */