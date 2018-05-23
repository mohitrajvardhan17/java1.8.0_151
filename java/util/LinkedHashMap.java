package java.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LinkedHashMap<K, V>
  extends HashMap<K, V>
  implements Map<K, V>
{
  private static final long serialVersionUID = 3801124242820219131L;
  transient Entry<K, V> head;
  transient Entry<K, V> tail;
  final boolean accessOrder;
  
  private void linkNodeLast(Entry<K, V> paramEntry)
  {
    Entry localEntry = tail;
    tail = paramEntry;
    if (localEntry == null)
    {
      head = paramEntry;
    }
    else
    {
      before = localEntry;
      after = paramEntry;
    }
  }
  
  private void transferLinks(Entry<K, V> paramEntry1, Entry<K, V> paramEntry2)
  {
    Entry localEntry1 = before = before;
    Entry localEntry2 = after = after;
    if (localEntry1 == null) {
      head = paramEntry2;
    } else {
      after = paramEntry2;
    }
    if (localEntry2 == null) {
      tail = paramEntry2;
    } else {
      before = paramEntry2;
    }
  }
  
  void reinitialize()
  {
    super.reinitialize();
    head = (tail = null);
  }
  
  HashMap.Node<K, V> newNode(int paramInt, K paramK, V paramV, HashMap.Node<K, V> paramNode)
  {
    Entry localEntry = new Entry(paramInt, paramK, paramV, paramNode);
    linkNodeLast(localEntry);
    return localEntry;
  }
  
  HashMap.Node<K, V> replacementNode(HashMap.Node<K, V> paramNode1, HashMap.Node<K, V> paramNode2)
  {
    Entry localEntry1 = (Entry)paramNode1;
    Entry localEntry2 = new Entry(hash, key, value, paramNode2);
    transferLinks(localEntry1, localEntry2);
    return localEntry2;
  }
  
  HashMap.TreeNode<K, V> newTreeNode(int paramInt, K paramK, V paramV, HashMap.Node<K, V> paramNode)
  {
    HashMap.TreeNode localTreeNode = new HashMap.TreeNode(paramInt, paramK, paramV, paramNode);
    linkNodeLast(localTreeNode);
    return localTreeNode;
  }
  
  HashMap.TreeNode<K, V> replacementTreeNode(HashMap.Node<K, V> paramNode1, HashMap.Node<K, V> paramNode2)
  {
    Entry localEntry = (Entry)paramNode1;
    HashMap.TreeNode localTreeNode = new HashMap.TreeNode(hash, key, value, paramNode2);
    transferLinks(localEntry, localTreeNode);
    return localTreeNode;
  }
  
  void afterNodeRemoval(HashMap.Node<K, V> paramNode)
  {
    Entry localEntry1 = (Entry)paramNode;
    Entry localEntry2 = before;
    Entry localEntry3 = after;
    before = (after = null);
    if (localEntry2 == null) {
      head = localEntry3;
    } else {
      after = localEntry3;
    }
    if (localEntry3 == null) {
      tail = localEntry2;
    } else {
      before = localEntry2;
    }
  }
  
  void afterNodeInsertion(boolean paramBoolean)
  {
    Entry localEntry;
    if ((paramBoolean) && ((localEntry = head) != null) && (removeEldestEntry(localEntry)))
    {
      Object localObject = key;
      removeNode(hash(localObject), localObject, null, false, true);
    }
  }
  
  void afterNodeAccess(HashMap.Node<K, V> paramNode)
  {
    Object localObject;
    if ((accessOrder) && ((localObject = tail) != paramNode))
    {
      Entry localEntry1 = (Entry)paramNode;
      Entry localEntry2 = before;
      Entry localEntry3 = after;
      after = null;
      if (localEntry2 == null) {
        head = localEntry3;
      } else {
        after = localEntry3;
      }
      if (localEntry3 != null) {
        before = localEntry2;
      } else {
        localObject = localEntry2;
      }
      if (localObject == null)
      {
        head = localEntry1;
      }
      else
      {
        before = ((Entry)localObject);
        after = localEntry1;
      }
      tail = localEntry1;
      modCount += 1;
    }
  }
  
  void internalWriteEntries(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    for (Entry localEntry = head; localEntry != null; localEntry = after)
    {
      paramObjectOutputStream.writeObject(key);
      paramObjectOutputStream.writeObject(value);
    }
  }
  
  public LinkedHashMap(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
    accessOrder = false;
  }
  
  public LinkedHashMap(int paramInt)
  {
    super(paramInt);
    accessOrder = false;
  }
  
  public LinkedHashMap()
  {
    accessOrder = false;
  }
  
  public LinkedHashMap(Map<? extends K, ? extends V> paramMap)
  {
    accessOrder = false;
    putMapEntries(paramMap, false);
  }
  
  public LinkedHashMap(int paramInt, float paramFloat, boolean paramBoolean)
  {
    super(paramInt, paramFloat);
    accessOrder = paramBoolean;
  }
  
  public boolean containsValue(Object paramObject)
  {
    for (Entry localEntry = head; localEntry != null; localEntry = after)
    {
      Object localObject = value;
      if ((localObject == paramObject) || ((paramObject != null) && (paramObject.equals(localObject)))) {
        return true;
      }
    }
    return false;
  }
  
  public V get(Object paramObject)
  {
    HashMap.Node localNode;
    if ((localNode = getNode(hash(paramObject), paramObject)) == null) {
      return null;
    }
    if (accessOrder) {
      afterNodeAccess(localNode);
    }
    return (V)value;
  }
  
  public V getOrDefault(Object paramObject, V paramV)
  {
    HashMap.Node localNode;
    if ((localNode = getNode(hash(paramObject), paramObject)) == null) {
      return paramV;
    }
    if (accessOrder) {
      afterNodeAccess(localNode);
    }
    return (V)value;
  }
  
  public void clear()
  {
    super.clear();
    head = (tail = null);
  }
  
  protected boolean removeEldestEntry(Map.Entry<K, V> paramEntry)
  {
    return false;
  }
  
  public Set<K> keySet()
  {
    Object localObject = keySet;
    if (localObject == null)
    {
      localObject = new LinkedKeySet();
      keySet = ((Set)localObject);
    }
    return (Set<K>)localObject;
  }
  
  public Collection<V> values()
  {
    Object localObject = values;
    if (localObject == null)
    {
      localObject = new LinkedValues();
      values = ((Collection)localObject);
    }
    return (Collection<V>)localObject;
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    Set localSet;
    return (localSet = entrySet) == null ? (entrySet = new LinkedEntrySet()) : localSet;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer)
  {
    if (paramBiConsumer == null) {
      throw new NullPointerException();
    }
    int i = modCount;
    for (Entry localEntry = head; localEntry != null; localEntry = after) {
      paramBiConsumer.accept(key, value);
    }
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    int i = modCount;
    for (Entry localEntry = head; localEntry != null; localEntry = after) {
      value = paramBiFunction.apply(key, value);
    }
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
  }
  
  static class Entry<K, V>
    extends HashMap.Node<K, V>
  {
    Entry<K, V> before;
    Entry<K, V> after;
    
    Entry(int paramInt, K paramK, V paramV, HashMap.Node<K, V> paramNode)
    {
      super(paramK, paramV, paramNode);
    }
  }
  
  final class LinkedEntryIterator
    extends LinkedHashMap<K, V>.LinkedHashIterator
    implements Iterator<Map.Entry<K, V>>
  {
    LinkedEntryIterator()
    {
      super();
    }
    
    public final Map.Entry<K, V> next()
    {
      return nextNode();
    }
  }
  
  final class LinkedEntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    LinkedEntrySet() {}
    
    public final int size()
    {
      return size;
    }
    
    public final void clear()
    {
      LinkedHashMap.this.clear();
    }
    
    public final Iterator<Map.Entry<K, V>> iterator()
    {
      return new LinkedHashMap.LinkedEntryIterator(LinkedHashMap.this);
    }
    
    public final boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject = localEntry.getKey();
      HashMap.Node localNode = getNode(HashMap.hash(localObject), localObject);
      return (localNode != null) && (localNode.equals(localEntry));
    }
    
    public final boolean remove(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        Object localObject1 = localEntry.getKey();
        Object localObject2 = localEntry.getValue();
        return removeNode(HashMap.hash(localObject1), localObject1, localObject2, true, true) != null;
      }
      return false;
    }
    
    public final Spliterator<Map.Entry<K, V>> spliterator()
    {
      return Spliterators.spliterator(this, 81);
    }
    
    public final void forEach(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i = modCount;
      for (LinkedHashMap.Entry localEntry = head; localEntry != null; localEntry = after) {
        paramConsumer.accept(localEntry);
      }
      if (modCount != i) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  abstract class LinkedHashIterator
  {
    LinkedHashMap.Entry<K, V> next = head;
    LinkedHashMap.Entry<K, V> current = null;
    int expectedModCount = modCount;
    
    LinkedHashIterator() {}
    
    public final boolean hasNext()
    {
      return next != null;
    }
    
    final LinkedHashMap.Entry<K, V> nextNode()
    {
      LinkedHashMap.Entry localEntry = next;
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if (localEntry == null) {
        throw new NoSuchElementException();
      }
      current = localEntry;
      next = after;
      return localEntry;
    }
    
    public final void remove()
    {
      LinkedHashMap.Entry localEntry = current;
      if (localEntry == null) {
        throw new IllegalStateException();
      }
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      current = null;
      Object localObject = key;
      removeNode(HashMap.hash(localObject), localObject, null, false, false);
      expectedModCount = modCount;
    }
  }
  
  final class LinkedKeyIterator
    extends LinkedHashMap<K, V>.LinkedHashIterator
    implements Iterator<K>
  {
    LinkedKeyIterator()
    {
      super();
    }
    
    public final K next()
    {
      return (K)nextNode().getKey();
    }
  }
  
  final class LinkedKeySet
    extends AbstractSet<K>
  {
    LinkedKeySet() {}
    
    public final int size()
    {
      return size;
    }
    
    public final void clear()
    {
      LinkedHashMap.this.clear();
    }
    
    public final Iterator<K> iterator()
    {
      return new LinkedHashMap.LinkedKeyIterator(LinkedHashMap.this);
    }
    
    public final boolean contains(Object paramObject)
    {
      return containsKey(paramObject);
    }
    
    public final boolean remove(Object paramObject)
    {
      return removeNode(HashMap.hash(paramObject), paramObject, null, false, true) != null;
    }
    
    public final Spliterator<K> spliterator()
    {
      return Spliterators.spliterator(this, 81);
    }
    
    public final void forEach(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i = modCount;
      for (LinkedHashMap.Entry localEntry = head; localEntry != null; localEntry = after) {
        paramConsumer.accept(key);
      }
      if (modCount != i) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  final class LinkedValueIterator
    extends LinkedHashMap<K, V>.LinkedHashIterator
    implements Iterator<V>
  {
    LinkedValueIterator()
    {
      super();
    }
    
    public final V next()
    {
      return (V)nextNodevalue;
    }
  }
  
  final class LinkedValues
    extends AbstractCollection<V>
  {
    LinkedValues() {}
    
    public final int size()
    {
      return size;
    }
    
    public final void clear()
    {
      LinkedHashMap.this.clear();
    }
    
    public final Iterator<V> iterator()
    {
      return new LinkedHashMap.LinkedValueIterator(LinkedHashMap.this);
    }
    
    public final boolean contains(Object paramObject)
    {
      return containsValue(paramObject);
    }
    
    public final Spliterator<V> spliterator()
    {
      return Spliterators.spliterator(this, 80);
    }
    
    public final void forEach(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i = modCount;
      for (LinkedHashMap.Entry localEntry = head; localEntry != null; localEntry = after) {
        paramConsumer.accept(value);
      }
      if (modCount != i) {
        throw new ConcurrentModificationException();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\LinkedHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */