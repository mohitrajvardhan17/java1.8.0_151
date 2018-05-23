package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class EnumMap<K extends Enum<K>, V>
  extends AbstractMap<K, V>
  implements Serializable, Cloneable
{
  private final Class<K> keyType;
  private transient K[] keyUniverse;
  private transient Object[] vals;
  private transient int size = 0;
  private static final Object NULL = new Object()
  {
    public int hashCode()
    {
      return 0;
    }
    
    public String toString()
    {
      return "java.util.EnumMap.NULL";
    }
  };
  private static final Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum[0];
  private transient Set<Map.Entry<K, V>> entrySet;
  private static final long serialVersionUID = 458661240069192865L;
  
  private Object maskNull(Object paramObject)
  {
    return paramObject == null ? NULL : paramObject;
  }
  
  private V unmaskNull(Object paramObject)
  {
    return paramObject == NULL ? null : paramObject;
  }
  
  public EnumMap(Class<K> paramClass)
  {
    keyType = paramClass;
    keyUniverse = getKeyUniverse(paramClass);
    vals = new Object[keyUniverse.length];
  }
  
  public EnumMap(EnumMap<K, ? extends V> paramEnumMap)
  {
    keyType = keyType;
    keyUniverse = keyUniverse;
    vals = ((Object[])vals.clone());
    size = size;
  }
  
  public EnumMap(Map<K, ? extends V> paramMap)
  {
    if ((paramMap instanceof EnumMap))
    {
      EnumMap localEnumMap = (EnumMap)paramMap;
      keyType = keyType;
      keyUniverse = keyUniverse;
      vals = ((Object[])vals.clone());
      size = size;
    }
    else
    {
      if (paramMap.isEmpty()) {
        throw new IllegalArgumentException("Specified map is empty");
      }
      keyType = ((Enum)paramMap.keySet().iterator().next()).getDeclaringClass();
      keyUniverse = getKeyUniverse(keyType);
      vals = new Object[keyUniverse.length];
      putAll(paramMap);
    }
  }
  
  public int size()
  {
    return size;
  }
  
  public boolean containsValue(Object paramObject)
  {
    paramObject = maskNull(paramObject);
    for (Object localObject : vals) {
      if (paramObject.equals(localObject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return (isValidKey(paramObject)) && (vals[((Enum)paramObject).ordinal()] != null);
  }
  
  private boolean containsMapping(Object paramObject1, Object paramObject2)
  {
    return (isValidKey(paramObject1)) && (maskNull(paramObject2).equals(vals[((Enum)paramObject1).ordinal()]));
  }
  
  public V get(Object paramObject)
  {
    return (V)(isValidKey(paramObject) ? unmaskNull(vals[((Enum)paramObject).ordinal()]) : null);
  }
  
  public V put(K paramK, V paramV)
  {
    typeCheck(paramK);
    int i = paramK.ordinal();
    Object localObject = vals[i];
    vals[i] = maskNull(paramV);
    if (localObject == null) {
      size += 1;
    }
    return (V)unmaskNull(localObject);
  }
  
  public V remove(Object paramObject)
  {
    if (!isValidKey(paramObject)) {
      return null;
    }
    int i = ((Enum)paramObject).ordinal();
    Object localObject = vals[i];
    vals[i] = null;
    if (localObject != null) {
      size -= 1;
    }
    return (V)unmaskNull(localObject);
  }
  
  private boolean removeMapping(Object paramObject1, Object paramObject2)
  {
    if (!isValidKey(paramObject1)) {
      return false;
    }
    int i = ((Enum)paramObject1).ordinal();
    if (maskNull(paramObject2).equals(vals[i]))
    {
      vals[i] = null;
      size -= 1;
      return true;
    }
    return false;
  }
  
  private boolean isValidKey(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    Class localClass = paramObject.getClass();
    return (localClass == keyType) || (localClass.getSuperclass() == keyType);
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    if ((paramMap instanceof EnumMap))
    {
      EnumMap localEnumMap = (EnumMap)paramMap;
      if (keyType != keyType)
      {
        if (localEnumMap.isEmpty()) {
          return;
        }
        throw new ClassCastException(keyType + " != " + keyType);
      }
      for (int i = 0; i < keyUniverse.length; i++)
      {
        Object localObject = vals[i];
        if (localObject != null)
        {
          if (vals[i] == null) {
            size += 1;
          }
          vals[i] = localObject;
        }
      }
    }
    else
    {
      super.putAll(paramMap);
    }
  }
  
  public void clear()
  {
    Arrays.fill(vals, null);
    size = 0;
  }
  
  public Set<K> keySet()
  {
    Object localObject = keySet;
    if (localObject == null)
    {
      localObject = new KeySet(null);
      keySet = ((Set)localObject);
    }
    return (Set<K>)localObject;
  }
  
  public Collection<V> values()
  {
    Object localObject = values;
    if (localObject == null)
    {
      localObject = new Values(null);
      values = ((Collection)localObject);
    }
    return (Collection<V>)localObject;
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    Set localSet = entrySet;
    if (localSet != null) {
      return localSet;
    }
    return entrySet = new EntrySet(null);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof EnumMap)) {
      return equals((EnumMap)paramObject);
    }
    if (!(paramObject instanceof Map)) {
      return false;
    }
    Map localMap = (Map)paramObject;
    if (size != localMap.size()) {
      return false;
    }
    for (int i = 0; i < keyUniverse.length; i++) {
      if (null != vals[i])
      {
        Enum localEnum = keyUniverse[i];
        Object localObject = unmaskNull(vals[i]);
        if (null == localObject)
        {
          if ((null != localMap.get(localEnum)) || (!localMap.containsKey(localEnum))) {
            return false;
          }
        }
        else if (!localObject.equals(localMap.get(localEnum))) {
          return false;
        }
      }
    }
    return true;
  }
  
  private boolean equals(EnumMap<?, ?> paramEnumMap)
  {
    if (keyType != keyType) {
      return (size == 0) && (size == 0);
    }
    for (int i = 0; i < keyUniverse.length; i++)
    {
      Object localObject1 = vals[i];
      Object localObject2 = vals[i];
      if ((localObject2 != localObject1) && ((localObject2 == null) || (!localObject2.equals(localObject1)))) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < keyUniverse.length; j++) {
      if (null != vals[j]) {
        i += entryHashCode(j);
      }
    }
    return i;
  }
  
  private int entryHashCode(int paramInt)
  {
    return keyUniverse[paramInt].hashCode() ^ vals[paramInt].hashCode();
  }
  
  public EnumMap<K, V> clone()
  {
    EnumMap localEnumMap = null;
    try
    {
      localEnumMap = (EnumMap)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError();
    }
    vals = ((Object[])vals.clone());
    entrySet = null;
    return localEnumMap;
  }
  
  private void typeCheck(K paramK)
  {
    Class localClass = paramK.getClass();
    if ((localClass != keyType) && (localClass.getSuperclass() != keyType)) {
      throw new ClassCastException(localClass + " != " + keyType);
    }
  }
  
  private static <K extends Enum<K>> K[] getKeyUniverse(Class<K> paramClass)
  {
    return SharedSecrets.getJavaLangAccess().getEnumConstantsShared(paramClass);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(size);
    int i = size;
    for (int j = 0; i > 0; j++) {
      if (null != vals[j])
      {
        paramObjectOutputStream.writeObject(keyUniverse[j]);
        paramObjectOutputStream.writeObject(unmaskNull(vals[j]));
        i--;
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    keyUniverse = getKeyUniverse(keyType);
    vals = new Object[keyUniverse.length];
    int i = paramObjectInputStream.readInt();
    for (int j = 0; j < i; j++)
    {
      Enum localEnum = (Enum)paramObjectInputStream.readObject();
      Object localObject = paramObjectInputStream.readObject();
      put(localEnum, localObject);
    }
  }
  
  private class EntryIterator
    extends EnumMap<K, V>.EnumMapIterator<Map.Entry<K, V>>
  {
    private EnumMap<K, V>.EntryIterator.Entry lastReturnedEntry;
    
    private EntryIterator()
    {
      super(null);
    }
    
    public Map.Entry<K, V> next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      lastReturnedEntry = new Entry(index++, null);
      return lastReturnedEntry;
    }
    
    public void remove()
    {
      lastReturnedIndex = (null == lastReturnedEntry ? -1 : lastReturnedEntry.index);
      super.remove();
      lastReturnedEntry.index = lastReturnedIndex;
      lastReturnedEntry = null;
    }
    
    private class Entry
      implements Map.Entry<K, V>
    {
      private int index;
      
      private Entry(int paramInt)
      {
        index = paramInt;
      }
      
      public K getKey()
      {
        checkIndexForEntryUse();
        return keyUniverse[index];
      }
      
      public V getValue()
      {
        checkIndexForEntryUse();
        return (V)EnumMap.this.unmaskNull(vals[index]);
      }
      
      public V setValue(V paramV)
      {
        checkIndexForEntryUse();
        Object localObject = EnumMap.this.unmaskNull(vals[index]);
        vals[index] = EnumMap.this.maskNull(paramV);
        return (V)localObject;
      }
      
      public boolean equals(Object paramObject)
      {
        if (index < 0) {
          return paramObject == this;
        }
        if (!(paramObject instanceof Map.Entry)) {
          return false;
        }
        Map.Entry localEntry = (Map.Entry)paramObject;
        Object localObject1 = EnumMap.this.unmaskNull(vals[index]);
        Object localObject2 = localEntry.getValue();
        return (localEntry.getKey() == keyUniverse[index]) && ((localObject1 == localObject2) || ((localObject1 != null) && (localObject1.equals(localObject2))));
      }
      
      public int hashCode()
      {
        if (index < 0) {
          return super.hashCode();
        }
        return EnumMap.this.entryHashCode(index);
      }
      
      public String toString()
      {
        if (index < 0) {
          return super.toString();
        }
        return keyUniverse[index] + "=" + EnumMap.this.unmaskNull(vals[index]);
      }
      
      private void checkIndexForEntryUse()
      {
        if (index < 0) {
          throw new IllegalStateException("Entry was removed");
        }
      }
    }
  }
  
  private class EntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new EnumMap.EntryIterator(EnumMap.this, null);
    }
    
    public boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return EnumMap.this.containsMapping(localEntry.getKey(), localEntry.getValue());
    }
    
    public boolean remove(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return EnumMap.this.removeMapping(localEntry.getKey(), localEntry.getValue());
    }
    
    public int size()
    {
      return size;
    }
    
    public void clear()
    {
      EnumMap.this.clear();
    }
    
    public Object[] toArray()
    {
      return fillEntryArray(new Object[size]);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      int i = size();
      if (paramArrayOfT.length < i) {
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
      }
      if (paramArrayOfT.length > i) {
        paramArrayOfT[i] = null;
      }
      return (Object[])fillEntryArray(paramArrayOfT);
    }
    
    private Object[] fillEntryArray(Object[] paramArrayOfObject)
    {
      int i = 0;
      for (int j = 0; j < vals.length; j++) {
        if (vals[j] != null) {
          paramArrayOfObject[(i++)] = new AbstractMap.SimpleEntry(keyUniverse[j], EnumMap.this.unmaskNull(vals[j]));
        }
      }
      return paramArrayOfObject;
    }
  }
  
  private abstract class EnumMapIterator<T>
    implements Iterator<T>
  {
    int index = 0;
    int lastReturnedIndex = -1;
    
    private EnumMapIterator() {}
    
    public boolean hasNext()
    {
      while ((index < vals.length) && (vals[index] == null)) {
        index += 1;
      }
      return index != vals.length;
    }
    
    public void remove()
    {
      checkLastReturnedIndex();
      if (vals[lastReturnedIndex] != null)
      {
        vals[lastReturnedIndex] = null;
        EnumMap.access$210(EnumMap.this);
      }
      lastReturnedIndex = -1;
    }
    
    private void checkLastReturnedIndex()
    {
      if (lastReturnedIndex < 0) {
        throw new IllegalStateException();
      }
    }
  }
  
  private class KeyIterator
    extends EnumMap<K, V>.EnumMapIterator<K>
  {
    private KeyIterator()
    {
      super(null);
    }
    
    public K next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      lastReturnedIndex = (index++);
      return keyUniverse[lastReturnedIndex];
    }
  }
  
  private class KeySet
    extends AbstractSet<K>
  {
    private KeySet() {}
    
    public Iterator<K> iterator()
    {
      return new EnumMap.KeyIterator(EnumMap.this, null);
    }
    
    public int size()
    {
      return size;
    }
    
    public boolean contains(Object paramObject)
    {
      return containsKey(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      int i = size;
      remove(paramObject);
      return size != i;
    }
    
    public void clear()
    {
      EnumMap.this.clear();
    }
  }
  
  private class ValueIterator
    extends EnumMap<K, V>.EnumMapIterator<V>
  {
    private ValueIterator()
    {
      super(null);
    }
    
    public V next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      lastReturnedIndex = (index++);
      return (V)EnumMap.this.unmaskNull(vals[lastReturnedIndex]);
    }
  }
  
  private class Values
    extends AbstractCollection<V>
  {
    private Values() {}
    
    public Iterator<V> iterator()
    {
      return new EnumMap.ValueIterator(EnumMap.this, null);
    }
    
    public int size()
    {
      return size;
    }
    
    public boolean contains(Object paramObject)
    {
      return containsValue(paramObject);
    }
    
    public boolean remove(Object paramObject)
    {
      paramObject = EnumMap.this.maskNull(paramObject);
      for (int i = 0; i < vals.length; i++) {
        if (paramObject.equals(vals[i]))
        {
          vals[i] = null;
          EnumMap.access$210(EnumMap.this);
          return true;
        }
      }
      return false;
    }
    
    public void clear()
    {
      EnumMap.this.clear();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\EnumMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */