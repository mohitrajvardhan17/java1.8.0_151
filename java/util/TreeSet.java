package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TreeSet<E>
  extends AbstractSet<E>
  implements NavigableSet<E>, Cloneable, Serializable
{
  private transient NavigableMap<E, Object> m;
  private static final Object PRESENT = new Object();
  private static final long serialVersionUID = -2479143000061671589L;
  
  TreeSet(NavigableMap<E, Object> paramNavigableMap)
  {
    m = paramNavigableMap;
  }
  
  public TreeSet()
  {
    this(new TreeMap());
  }
  
  public TreeSet(Comparator<? super E> paramComparator)
  {
    this(new TreeMap(paramComparator));
  }
  
  public TreeSet(Collection<? extends E> paramCollection)
  {
    this();
    addAll(paramCollection);
  }
  
  public TreeSet(SortedSet<E> paramSortedSet)
  {
    this(paramSortedSet.comparator());
    addAll(paramSortedSet);
  }
  
  public Iterator<E> iterator()
  {
    return m.navigableKeySet().iterator();
  }
  
  public Iterator<E> descendingIterator()
  {
    return m.descendingKeySet().iterator();
  }
  
  public NavigableSet<E> descendingSet()
  {
    return new TreeSet(m.descendingMap());
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
    return m.put(paramE, PRESENT) == null;
  }
  
  public boolean remove(Object paramObject)
  {
    return m.remove(paramObject) == PRESENT;
  }
  
  public void clear()
  {
    m.clear();
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    if ((m.size() == 0) && (paramCollection.size() > 0) && ((paramCollection instanceof SortedSet)) && ((m instanceof TreeMap)))
    {
      SortedSet localSortedSet = (SortedSet)paramCollection;
      TreeMap localTreeMap = (TreeMap)m;
      Comparator localComparator1 = localSortedSet.comparator();
      Comparator localComparator2 = localTreeMap.comparator();
      if ((localComparator1 == localComparator2) || ((localComparator1 != null) && (localComparator1.equals(localComparator2))))
      {
        localTreeMap.addAllForTreeSet(localSortedSet, PRESENT);
        return true;
      }
    }
    return super.addAll(paramCollection);
  }
  
  public NavigableSet<E> subSet(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2)
  {
    return new TreeSet(m.subMap(paramE1, paramBoolean1, paramE2, paramBoolean2));
  }
  
  public NavigableSet<E> headSet(E paramE, boolean paramBoolean)
  {
    return new TreeSet(m.headMap(paramE, paramBoolean));
  }
  
  public NavigableSet<E> tailSet(E paramE, boolean paramBoolean)
  {
    return new TreeSet(m.tailMap(paramE, paramBoolean));
  }
  
  public SortedSet<E> subSet(E paramE1, E paramE2)
  {
    return subSet(paramE1, true, paramE2, false);
  }
  
  public SortedSet<E> headSet(E paramE)
  {
    return headSet(paramE, false);
  }
  
  public SortedSet<E> tailSet(E paramE)
  {
    return tailSet(paramE, true);
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
  
  public Object clone()
  {
    TreeSet localTreeSet;
    try
    {
      localTreeSet = (TreeSet)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
    m = new TreeMap(m);
    return localTreeSet;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(m.comparator());
    paramObjectOutputStream.writeInt(m.size());
    Iterator localIterator = m.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramObjectOutputStream.writeObject(localObject);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Comparator localComparator = (Comparator)paramObjectInputStream.readObject();
    TreeMap localTreeMap = new TreeMap(localComparator);
    m = localTreeMap;
    int i = paramObjectInputStream.readInt();
    localTreeMap.readTreeSet(i, paramObjectInputStream, PRESENT);
  }
  
  public Spliterator<E> spliterator()
  {
    return TreeMap.keySpliteratorFor(m);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\TreeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */