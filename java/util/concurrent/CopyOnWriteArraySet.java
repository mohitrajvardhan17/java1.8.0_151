package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CopyOnWriteArraySet<E>
  extends AbstractSet<E>
  implements Serializable
{
  private static final long serialVersionUID = 5457747651344034263L;
  private final CopyOnWriteArrayList<E> al;
  
  public CopyOnWriteArraySet()
  {
    al = new CopyOnWriteArrayList();
  }
  
  public CopyOnWriteArraySet(Collection<? extends E> paramCollection)
  {
    if (paramCollection.getClass() == CopyOnWriteArraySet.class)
    {
      CopyOnWriteArraySet localCopyOnWriteArraySet = (CopyOnWriteArraySet)paramCollection;
      al = new CopyOnWriteArrayList(al);
    }
    else
    {
      al = new CopyOnWriteArrayList();
      al.addAllAbsent(paramCollection);
    }
  }
  
  public int size()
  {
    return al.size();
  }
  
  public boolean isEmpty()
  {
    return al.isEmpty();
  }
  
  public boolean contains(Object paramObject)
  {
    return al.contains(paramObject);
  }
  
  public Object[] toArray()
  {
    return al.toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    return al.toArray(paramArrayOfT);
  }
  
  public void clear()
  {
    al.clear();
  }
  
  public boolean remove(Object paramObject)
  {
    return al.remove(paramObject);
  }
  
  public boolean add(E paramE)
  {
    return al.addIfAbsent(paramE);
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    return al.containsAll(paramCollection);
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    return al.addAllAbsent(paramCollection) > 0;
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    return al.removeAll(paramCollection);
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    return al.retainAll(paramCollection);
  }
  
  public Iterator<E> iterator()
  {
    return al.iterator();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Set)) {
      return false;
    }
    Set localSet = (Set)paramObject;
    Iterator localIterator = localSet.iterator();
    Object[] arrayOfObject = al.getArray();
    int i = arrayOfObject.length;
    boolean[] arrayOfBoolean = new boolean[i];
    int j = 0;
    if (localIterator.hasNext())
    {
      j++;
      if (j > i) {
        return false;
      }
      Object localObject = localIterator.next();
      for (int k = 0;; k++)
      {
        if (k >= i) {
          break label129;
        }
        if ((arrayOfBoolean[k] == 0) && (eq(localObject, arrayOfObject[k])))
        {
          arrayOfBoolean[k] = true;
          break;
        }
      }
      label129:
      return false;
    }
    return j == i;
  }
  
  public boolean removeIf(Predicate<? super E> paramPredicate)
  {
    return al.removeIf(paramPredicate);
  }
  
  public void forEach(Consumer<? super E> paramConsumer)
  {
    al.forEach(paramConsumer);
  }
  
  public Spliterator<E> spliterator()
  {
    return Spliterators.spliterator(al.getArray(), 1025);
  }
  
  private static boolean eq(Object paramObject1, Object paramObject2)
  {
    return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\CopyOnWriteArraySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */