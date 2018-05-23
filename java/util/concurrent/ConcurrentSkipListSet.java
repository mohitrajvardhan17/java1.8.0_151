package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterator;
import sun.misc.Unsafe;

public class ConcurrentSkipListSet<E>
  extends AbstractSet<E>
  implements NavigableSet<E>, Cloneable, Serializable
{
  private static final long serialVersionUID = -2479143111061671589L;
  private final ConcurrentNavigableMap<E, Object> m;
  private static final Unsafe UNSAFE;
  private static final long mapOffset;
  
  public ConcurrentSkipListSet()
  {
    m = new ConcurrentSkipListMap();
  }
  
  public ConcurrentSkipListSet(Comparator<? super E> paramComparator)
  {
    m = new ConcurrentSkipListMap(paramComparator);
  }
  
  public ConcurrentSkipListSet(Collection<? extends E> paramCollection)
  {
    m = new ConcurrentSkipListMap();
    addAll(paramCollection);
  }
  
  public ConcurrentSkipListSet(SortedSet<E> paramSortedSet)
  {
    m = new ConcurrentSkipListMap(paramSortedSet.comparator());
    addAll(paramSortedSet);
  }
  
  ConcurrentSkipListSet(ConcurrentNavigableMap<E, Object> paramConcurrentNavigableMap)
  {
    m = paramConcurrentNavigableMap;
  }
  
  public ConcurrentSkipListSet<E> clone()
  {
    try
    {
      ConcurrentSkipListSet localConcurrentSkipListSet = (ConcurrentSkipListSet)super.clone();
      localConcurrentSkipListSet.setMap(new ConcurrentSkipListMap(m));
      return localConcurrentSkipListSet;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
  }
  
  public int size()
  {
    return m.size();
  }
  
  public boolean isEmpty()
  {
    return m.isEmpty();
  }
  
  public boolean contains(Object paramObject)
  {
    return m.containsKey(paramObject);
  }
  
  public boolean add(E paramE)
  {
    return m.putIfAbsent(paramE, Boolean.TRUE) == null;
  }
  
  public boolean remove(Object paramObject)
  {
    return m.remove(paramObject, Boolean.TRUE);
  }
  
  public void clear()
  {
    m.clear();
  }
  
  public Iterator<E> iterator()
  {
    return m.navigableKeySet().iterator();
  }
  
  public Iterator<E> descendingIterator()
  {
    return m.descendingKeySet().iterator();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Set)) {
      return false;
    }
    Collection localCollection = (Collection)paramObject;
    try
    {
      return (containsAll(localCollection)) && (localCollection.containsAll(this));
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException) {}
    return false;
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    boolean bool = false;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (remove(localObject)) {
        bool = true;
      }
    }
    return bool;
  }
  
  public E lower(E paramE)
  {
    return (E)m.lowerKey(paramE);
  }
  
  public E floor(E paramE)
  {
    return (E)m.floorKey(paramE);
  }
  
  public E ceiling(E paramE)
  {
    return (E)m.ceilingKey(paramE);
  }
  
  public E higher(E paramE)
  {
    return (E)m.higherKey(paramE);
  }
  
  public E pollFirst()
  {
    Map.Entry localEntry = m.pollFirstEntry();
    return localEntry == null ? null : localEntry.getKey();
  }
  
  public E pollLast()
  {
    Map.Entry localEntry = m.pollLastEntry();
    return localEntry == null ? null : localEntry.getKey();
  }
  
  public Comparator<? super E> comparator()
  {
    return m.comparator();
  }
  
  public E first()
  {
    return (E)m.firstKey();
  }
  
  public E last()
  {
    return (E)m.lastKey();
  }
  
  public NavigableSet<E> subSet(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2)
  {
    return new ConcurrentSkipListSet(m.subMap(paramE1, paramBoolean1, paramE2, paramBoolean2));
  }
  
  public NavigableSet<E> headSet(E paramE, boolean paramBoolean)
  {
    return new ConcurrentSkipListSet(m.headMap(paramE, paramBoolean));
  }
  
  public NavigableSet<E> tailSet(E paramE, boolean paramBoolean)
  {
    return new ConcurrentSkipListSet(m.tailMap(paramE, paramBoolean));
  }
  
  public NavigableSet<E> subSet(E paramE1, E paramE2)
  {
    return subSet(paramE1, true, paramE2, false);
  }
  
  public NavigableSet<E> headSet(E paramE)
  {
    return headSet(paramE, false);
  }
  
  public NavigableSet<E> tailSet(E paramE)
  {
    return tailSet(paramE, true);
  }
  
  public NavigableSet<E> descendingSet()
  {
    return new ConcurrentSkipListSet(m.descendingMap());
  }
  
  public Spliterator<E> spliterator()
  {
    if ((m instanceof ConcurrentSkipListMap)) {
      return ((ConcurrentSkipListMap)m).keySpliterator();
    }
    return (Spliterator)((ConcurrentSkipListMap.SubMap)m).keyIterator();
  }
  
  private void setMap(ConcurrentNavigableMap<E, Object> paramConcurrentNavigableMap)
  {
    UNSAFE.putObjectVolatile(this, mapOffset, paramConcurrentNavigableMap);
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = ConcurrentSkipListSet.class;
      mapOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("m"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ConcurrentSkipListSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */