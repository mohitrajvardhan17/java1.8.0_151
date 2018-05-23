package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import sun.misc.JavaOISAccess;
import sun.misc.SharedSecrets;

public class Hashtable<K, V>
  extends Dictionary<K, V>
  implements Map<K, V>, Cloneable, Serializable
{
  private transient Entry<?, ?>[] table;
  private transient int count;
  private int threshold;
  private float loadFactor;
  private transient int modCount = 0;
  private static final long serialVersionUID = 1421746759512286392L;
  private static final int MAX_ARRAY_SIZE = 2147483639;
  private volatile transient Set<K> keySet;
  private volatile transient Set<Map.Entry<K, V>> entrySet;
  private volatile transient Collection<V> values;
  private static final int KEYS = 0;
  private static final int VALUES = 1;
  private static final int ENTRIES = 2;
  
  public Hashtable(int paramInt, float paramFloat)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    }
    if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
      throw new IllegalArgumentException("Illegal Load: " + paramFloat);
    }
    if (paramInt == 0) {
      paramInt = 1;
    }
    loadFactor = paramFloat;
    table = new Entry[paramInt];
    threshold = ((int)Math.min(paramInt * paramFloat, 2.14748365E9F));
  }
  
  public Hashtable(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public Hashtable()
  {
    this(11, 0.75F);
  }
  
  public Hashtable(Map<? extends K, ? extends V> paramMap)
  {
    this(Math.max(2 * paramMap.size(), 11), 0.75F);
    putAll(paramMap);
  }
  
  public synchronized int size()
  {
    return count;
  }
  
  public synchronized boolean isEmpty()
  {
    return count == 0;
  }
  
  public synchronized Enumeration<K> keys()
  {
    return getEnumeration(0);
  }
  
  public synchronized Enumeration<V> elements()
  {
    return getEnumeration(1);
  }
  
  public synchronized boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    Entry[] arrayOfEntry = table;
    int i = arrayOfEntry.length;
    while (i-- > 0) {
      for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = next) {
        if (value.equals(paramObject)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean containsValue(Object paramObject)
  {
    return contains(paramObject);
  }
  
  public synchronized boolean containsKey(Object paramObject)
  {
    Entry[] arrayOfEntry = table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
      if ((hash == i) && (key.equals(paramObject))) {
        return true;
      }
    }
    return false;
  }
  
  public synchronized V get(Object paramObject)
  {
    Entry[] arrayOfEntry = table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
      if ((hash == i) && (key.equals(paramObject))) {
        return (V)value;
      }
    }
    return null;
  }
  
  protected void rehash()
  {
    int i = table.length;
    Entry[] arrayOfEntry1 = table;
    int j = (i << 1) + 1;
    if (j - 2147483639 > 0)
    {
      if (i == 2147483639) {
        return;
      }
      j = 2147483639;
    }
    Entry[] arrayOfEntry2 = new Entry[j];
    modCount += 1;
    threshold = ((int)Math.min(j * loadFactor, 2.14748365E9F));
    table = arrayOfEntry2;
    int k = i;
    while (k-- > 0)
    {
      Entry localEntry1 = arrayOfEntry1[k];
      while (localEntry1 != null)
      {
        Entry localEntry2 = localEntry1;
        localEntry1 = next;
        int m = (hash & 0x7FFFFFFF) % j;
        next = arrayOfEntry2[m];
        arrayOfEntry2[m] = localEntry2;
      }
    }
  }
  
  private void addEntry(int paramInt1, K paramK, V paramV, int paramInt2)
  {
    modCount += 1;
    Entry[] arrayOfEntry = table;
    if (count >= threshold)
    {
      rehash();
      arrayOfEntry = table;
      paramInt1 = paramK.hashCode();
      paramInt2 = (paramInt1 & 0x7FFFFFFF) % arrayOfEntry.length;
    }
    Entry localEntry = arrayOfEntry[paramInt2];
    arrayOfEntry[paramInt2] = new Entry(paramInt1, paramK, paramV, localEntry);
    count += 1;
  }
  
  public synchronized V put(K paramK, V paramV)
  {
    if (paramV == null) {
      throw new NullPointerException();
    }
    Entry[] arrayOfEntry = table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
      if ((hash == i) && (key.equals(paramK)))
      {
        Object localObject = value;
        value = paramV;
        return (V)localObject;
      }
    }
    addEntry(i, paramK, paramV, j);
    return null;
  }
  
  public synchronized V remove(Object paramObject)
  {
    Entry[] arrayOfEntry = table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry localEntry1 = arrayOfEntry[j];
    Entry localEntry2 = null;
    while (localEntry1 != null)
    {
      if ((hash == i) && (key.equals(paramObject)))
      {
        modCount += 1;
        if (localEntry2 != null) {
          next = next;
        } else {
          arrayOfEntry[j] = next;
        }
        count -= 1;
        Object localObject = value;
        value = null;
        return (V)localObject;
      }
      localEntry2 = localEntry1;
      localEntry1 = next;
    }
    return null;
  }
  
  public synchronized void putAll(Map<? extends K, ? extends V> paramMap)
  {
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public synchronized void clear()
  {
    Entry[] arrayOfEntry = table;
    modCount += 1;
    int i = arrayOfEntry.length;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      arrayOfEntry[i] = null;
    }
    count = 0;
  }
  
  public synchronized Object clone()
  {
    try
    {
      Hashtable localHashtable = (Hashtable)super.clone();
      table = new Entry[table.length];
      int i = table.length;
      while (i-- > 0) {
        table[i] = (table[i] != null ? (Entry)table[i].clone() : null);
      }
      keySet = null;
      entrySet = null;
      values = null;
      modCount = 0;
      return localHashtable;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public synchronized String toString()
  {
    int i = size() - 1;
    if (i == -1) {
      return "{}";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = entrySet().iterator();
    localStringBuilder.append('{');
    for (int j = 0;; j++)
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject1 = localEntry.getKey();
      Object localObject2 = localEntry.getValue();
      localStringBuilder.append(localObject1 == this ? "(this Map)" : localObject1.toString());
      localStringBuilder.append('=');
      localStringBuilder.append(localObject2 == this ? "(this Map)" : localObject2.toString());
      if (j == i) {
        return '}';
      }
      localStringBuilder.append(", ");
    }
  }
  
  private <T> Enumeration<T> getEnumeration(int paramInt)
  {
    if (count == 0) {
      return Collections.emptyEnumeration();
    }
    return new Enumerator(paramInt, false);
  }
  
  private <T> Iterator<T> getIterator(int paramInt)
  {
    if (count == 0) {
      return Collections.emptyIterator();
    }
    return new Enumerator(paramInt, true);
  }
  
  public Set<K> keySet()
  {
    if (keySet == null) {
      keySet = Collections.synchronizedSet(new KeySet(null), this);
    }
    return keySet;
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    if (entrySet == null) {
      entrySet = Collections.synchronizedSet(new EntrySet(null), this);
    }
    return entrySet;
  }
  
  public Collection<V> values()
  {
    if (values == null) {
      values = Collections.synchronizedCollection(new ValueCollection(null), this);
    }
    return values;
  }
  
  public synchronized boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Map)) {
      return false;
    }
    Map localMap = (Map)paramObject;
    if (localMap.size() != size()) {
      return false;
    }
    try
    {
      Iterator localIterator = entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject1 = localEntry.getKey();
        Object localObject2 = localEntry.getValue();
        if (localObject2 == null)
        {
          if ((localMap.get(localObject1) != null) || (!localMap.containsKey(localObject1))) {
            return false;
          }
        }
        else if (!localObject2.equals(localMap.get(localObject1))) {
          return false;
        }
      }
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    catch (NullPointerException localNullPointerException)
    {
      return false;
    }
    return true;
  }
  
  public synchronized int hashCode()
  {
    int i = 0;
    if ((count == 0) || (loadFactor < 0.0F)) {
      return i;
    }
    loadFactor = (-loadFactor);
    Entry[] arrayOfEntry1 = table;
    Entry[] arrayOfEntry2 = arrayOfEntry1;
    int j = arrayOfEntry2.length;
    for (int k = 0; k < j; k++) {
      for (Entry localEntry = arrayOfEntry2[k]; localEntry != null; localEntry = next) {
        i += localEntry.hashCode();
      }
    }
    loadFactor = (-loadFactor);
    return i;
  }
  
  public synchronized V getOrDefault(Object paramObject, V paramV)
  {
    Object localObject = get(paramObject);
    return null == localObject ? paramV : localObject;
  }
  
  public synchronized void forEach(BiConsumer<? super K, ? super V> paramBiConsumer)
  {
    Objects.requireNonNull(paramBiConsumer);
    int i = modCount;
    Entry[] arrayOfEntry1 = table;
    for (Entry localEntry : arrayOfEntry1) {
      while (localEntry != null)
      {
        paramBiConsumer.accept(key, value);
        localEntry = next;
        if (i != modCount) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }
  
  public synchronized void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    Objects.requireNonNull(paramBiFunction);
    int i = modCount;
    Entry[] arrayOfEntry1 = (Entry[])table;
    for (Entry localEntry : arrayOfEntry1) {
      while (localEntry != null)
      {
        value = Objects.requireNonNull(paramBiFunction.apply(key, value));
        localEntry = next;
        if (i != modCount) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }
  
  public synchronized V putIfAbsent(K paramK, V paramV)
  {
    Objects.requireNonNull(paramV);
    Entry[] arrayOfEntry = table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
      if ((hash == i) && (key.equals(paramK)))
      {
        Object localObject = value;
        if (localObject == null) {
          value = paramV;
        }
        return (V)localObject;
      }
    }
    addEntry(i, paramK, paramV, j);
    return null;
  }
  
  public synchronized boolean remove(Object paramObject1, Object paramObject2)
  {
    Objects.requireNonNull(paramObject2);
    Entry[] arrayOfEntry = table;
    int i = paramObject1.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry localEntry1 = arrayOfEntry[j];
    Entry localEntry2 = null;
    while (localEntry1 != null)
    {
      if ((hash == i) && (key.equals(paramObject1)) && (value.equals(paramObject2)))
      {
        modCount += 1;
        if (localEntry2 != null) {
          next = next;
        } else {
          arrayOfEntry[j] = next;
        }
        count -= 1;
        value = null;
        return true;
      }
      localEntry2 = localEntry1;
      localEntry1 = next;
    }
    return false;
  }
  
  public synchronized boolean replace(K paramK, V paramV1, V paramV2)
  {
    Objects.requireNonNull(paramV1);
    Objects.requireNonNull(paramV2);
    Entry[] arrayOfEntry = table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
      if ((hash == i) && (key.equals(paramK)))
      {
        if (value.equals(paramV1))
        {
          value = paramV2;
          return true;
        }
        return false;
      }
    }
    return false;
  }
  
  public synchronized V replace(K paramK, V paramV)
  {
    Objects.requireNonNull(paramV);
    Entry[] arrayOfEntry = table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
      if ((hash == i) && (key.equals(paramK)))
      {
        Object localObject = value;
        value = paramV;
        return (V)localObject;
      }
    }
    return null;
  }
  
  public synchronized V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction)
  {
    Objects.requireNonNull(paramFunction);
    Entry[] arrayOfEntry = table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
      if ((hash == i) && (key.equals(paramK))) {
        return (V)value;
      }
    }
    Object localObject = paramFunction.apply(paramK);
    if (localObject != null) {
      addEntry(i, paramK, localObject, j);
    }
    return (V)localObject;
  }
  
  public synchronized V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    Objects.requireNonNull(paramBiFunction);
    Entry[] arrayOfEntry = table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry localEntry1 = arrayOfEntry[j];
    Entry localEntry2 = null;
    while (localEntry1 != null)
    {
      if ((hash == i) && (key.equals(paramK)))
      {
        Object localObject = paramBiFunction.apply(paramK, value);
        if (localObject == null)
        {
          modCount += 1;
          if (localEntry2 != null) {
            next = next;
          } else {
            arrayOfEntry[j] = next;
          }
          count -= 1;
        }
        else
        {
          value = localObject;
        }
        return (V)localObject;
      }
      localEntry2 = localEntry1;
      localEntry1 = next;
    }
    return null;
  }
  
  public synchronized V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    Objects.requireNonNull(paramBiFunction);
    Entry[] arrayOfEntry = table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry localEntry = arrayOfEntry[j];
    Object localObject1 = null;
    while (localEntry != null)
    {
      if ((hash == i) && (Objects.equals(key, paramK)))
      {
        Object localObject2 = paramBiFunction.apply(paramK, value);
        if (localObject2 == null)
        {
          modCount += 1;
          if (localObject1 != null) {
            next = next;
          } else {
            arrayOfEntry[j] = next;
          }
          count -= 1;
        }
        else
        {
          value = localObject2;
        }
        return (V)localObject2;
      }
      localObject1 = localEntry;
      localEntry = next;
    }
    localObject1 = paramBiFunction.apply(paramK, null);
    if (localObject1 != null) {
      addEntry(i, paramK, localObject1, j);
    }
    return (V)localObject1;
  }
  
  public synchronized V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction)
  {
    Objects.requireNonNull(paramBiFunction);
    Entry[] arrayOfEntry = table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry localEntry1 = arrayOfEntry[j];
    Entry localEntry2 = null;
    while (localEntry1 != null)
    {
      if ((hash == i) && (key.equals(paramK)))
      {
        Object localObject = paramBiFunction.apply(value, paramV);
        if (localObject == null)
        {
          modCount += 1;
          if (localEntry2 != null) {
            next = next;
          } else {
            arrayOfEntry[j] = next;
          }
          count -= 1;
        }
        else
        {
          value = localObject;
        }
        return (V)localObject;
      }
      localEntry2 = localEntry1;
      localEntry1 = next;
    }
    if (paramV != null) {
      addEntry(i, paramK, paramV, j);
    }
    return paramV;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Entry localEntry1 = null;
    synchronized (this)
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeInt(table.length);
      paramObjectOutputStream.writeInt(count);
      for (int i = 0; i < table.length; i++) {
        for (Entry localEntry2 = table[i]; localEntry2 != null; localEntry2 = next) {
          localEntry1 = new Entry(0, key, value, localEntry1);
        }
      }
    }
    while (localEntry1 != null)
    {
      paramObjectOutputStream.writeObject(key);
      paramObjectOutputStream.writeObject(value);
      localEntry1 = next;
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if ((loadFactor <= 0.0F) || (Float.isNaN(loadFactor))) {
      throw new StreamCorruptedException("Illegal Load: " + loadFactor);
    }
    int i = paramObjectInputStream.readInt();
    int j = paramObjectInputStream.readInt();
    if (j < 0) {
      throw new StreamCorruptedException("Illegal # of Elements: " + j);
    }
    i = Math.max(i, (int)(j / loadFactor) + 1);
    int k = (int)((j + j / 20) / loadFactor) + 3;
    if ((k > j) && ((k & 0x1) == 0)) {
      k--;
    }
    k = Math.min(k, i);
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Map.Entry[].class, k);
    table = new Entry[k];
    threshold = ((int)Math.min(k * loadFactor, 2.14748365E9F));
    count = 0;
    while (j > 0)
    {
      Object localObject1 = paramObjectInputStream.readObject();
      Object localObject2 = paramObjectInputStream.readObject();
      reconstitutionPut(table, localObject1, localObject2);
      j--;
    }
  }
  
  private void reconstitutionPut(Entry<?, ?>[] paramArrayOfEntry, K paramK, V paramV)
    throws StreamCorruptedException
  {
    if (paramV == null) {
      throw new StreamCorruptedException();
    }
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % paramArrayOfEntry.length;
    for (Object localObject = paramArrayOfEntry[j]; localObject != null; localObject = next) {
      if ((hash == i) && (key.equals(paramK))) {
        throw new StreamCorruptedException();
      }
    }
    localObject = paramArrayOfEntry[j];
    paramArrayOfEntry[j] = new Entry(i, paramK, paramV, (Entry)localObject);
    count += 1;
  }
  
  private static class Entry<K, V>
    implements Map.Entry<K, V>
  {
    final int hash;
    final K key;
    V value;
    Entry<K, V> next;
    
    protected Entry(int paramInt, K paramK, V paramV, Entry<K, V> paramEntry)
    {
      hash = paramInt;
      key = paramK;
      value = paramV;
      next = paramEntry;
    }
    
    protected Object clone()
    {
      return new Entry(hash, key, value, next == null ? null : (Entry)next.clone());
    }
    
    public K getKey()
    {
      return (K)key;
    }
    
    public V getValue()
    {
      return (V)value;
    }
    
    public V setValue(V paramV)
    {
      if (paramV == null) {
        throw new NullPointerException();
      }
      Object localObject = value;
      value = paramV;
      return (V)localObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return (key == null ? localEntry.getKey() == null : key.equals(localEntry.getKey())) && (value == null ? localEntry.getValue() == null : value.equals(localEntry.getValue()));
    }
    
    public int hashCode()
    {
      return hash ^ Objects.hashCode(value);
    }
    
    public String toString()
    {
      return key.toString() + "=" + value.toString();
    }
  }
  
  private class EntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return Hashtable.this.getIterator(2);
    }
    
    public boolean add(Map.Entry<K, V> paramEntry)
    {
      return super.add(paramEntry);
    }
    
    public boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject = localEntry.getKey();
      Hashtable.Entry[] arrayOfEntry = table;
      int i = localObject.hashCode();
      int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
      for (Hashtable.Entry localEntry1 = arrayOfEntry[j]; localEntry1 != null; localEntry1 = next) {
        if ((hash == i) && (localEntry1.equals(localEntry))) {
          return true;
        }
      }
      return false;
    }
    
    public boolean remove(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject = localEntry.getKey();
      Hashtable.Entry[] arrayOfEntry = table;
      int i = localObject.hashCode();
      int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
      Hashtable.Entry localEntry1 = arrayOfEntry[j];
      Hashtable.Entry localEntry2 = null;
      while (localEntry1 != null)
      {
        if ((hash == i) && (localEntry1.equals(localEntry)))
        {
          Hashtable.access$508(Hashtable.this);
          if (localEntry2 != null) {
            next = next;
          } else {
            arrayOfEntry[j] = next;
          }
          Hashtable.access$210(Hashtable.this);
          value = null;
          return true;
        }
        localEntry2 = localEntry1;
        localEntry1 = next;
      }
      return false;
    }
    
    public int size()
    {
      return count;
    }
    
    public void clear()
    {
      Hashtable.this.clear();
    }
  }
  
  private class Enumerator<T>
    implements Enumeration<T>, Iterator<T>
  {
    Hashtable.Entry<?, ?>[] table = table;
    int index = table.length;
    Hashtable.Entry<?, ?> entry;
    Hashtable.Entry<?, ?> lastReturned;
    int type;
    boolean iterator;
    protected int expectedModCount = modCount;
    
    Enumerator(int paramInt, boolean paramBoolean)
    {
      type = paramInt;
      iterator = paramBoolean;
    }
    
    public boolean hasMoreElements()
    {
      Hashtable.Entry localEntry = entry;
      int i = index;
      Hashtable.Entry[] arrayOfEntry = table;
      while ((localEntry == null) && (i > 0)) {
        localEntry = arrayOfEntry[(--i)];
      }
      entry = localEntry;
      index = i;
      return localEntry != null;
    }
    
    public T nextElement()
    {
      Hashtable.Entry localEntry1 = entry;
      int i = index;
      Hashtable.Entry[] arrayOfEntry = table;
      while ((localEntry1 == null) && (i > 0)) {
        localEntry1 = arrayOfEntry[(--i)];
      }
      entry = localEntry1;
      index = i;
      if (localEntry1 != null)
      {
        Hashtable.Entry localEntry2 = lastReturned = entry;
        entry = next;
        return (T)(type == 1 ? value : type == 0 ? key : localEntry2);
      }
      throw new NoSuchElementException("Hashtable Enumerator");
    }
    
    public boolean hasNext()
    {
      return hasMoreElements();
    }
    
    public T next()
    {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      return (T)nextElement();
    }
    
    public void remove()
    {
      if (!iterator) {
        throw new UnsupportedOperationException();
      }
      if (lastReturned == null) {
        throw new IllegalStateException("Hashtable Enumerator");
      }
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      synchronized (Hashtable.this)
      {
        Hashtable.Entry[] arrayOfEntry = table;
        int i = (lastReturned.hash & 0x7FFFFFFF) % arrayOfEntry.length;
        Hashtable.Entry localEntry1 = arrayOfEntry[i];
        Hashtable.Entry localEntry2 = null;
        while (localEntry1 != null)
        {
          if (localEntry1 == lastReturned)
          {
            Hashtable.access$508(Hashtable.this);
            expectedModCount += 1;
            if (localEntry2 == null) {
              arrayOfEntry[i] = next;
            } else {
              next = next;
            }
            Hashtable.access$210(Hashtable.this);
            lastReturned = null;
            return;
          }
          localEntry2 = localEntry1;
          localEntry1 = next;
        }
        throw new ConcurrentModificationException();
      }
    }
  }
  
  private class KeySet
    extends AbstractSet<K>
  {
    private KeySet() {}
    
    public Iterator<K> iterator()
    {
      return Hashtable.this.getIterator(0);
    }
    
    public int size()
    {
      return count;
    }
    
    public boolean contains(Object paramObject)
    {
      return containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      return remove(paramObject) != null;
    }
    
    public void clear()
    {
      Hashtable.this.clear();
    }
  }
  
  private class ValueCollection
    extends AbstractCollection<V>
  {
    private ValueCollection() {}
    
    public Iterator<V> iterator()
    {
      return Hashtable.this.getIterator(1);
    }
    
    public int size()
    {
      return count;
    }
    
    public boolean contains(Object paramObject)
    {
      return containsValue(paramObject);
    }
    
    public void clear()
    {
      Hashtable.this.clear();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Hashtable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */