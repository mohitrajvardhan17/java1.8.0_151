package com.sun.xml.internal.bind.v2.util;

import com.sun.xml.internal.bind.v2.runtime.Name;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class QNameMap<V>
{
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final int MAXIMUM_CAPACITY = 1073741824;
  transient Entry<V>[] table = new Entry[16];
  transient int size;
  private int threshold = 12;
  private static final float DEFAULT_LOAD_FACTOR = 0.75F;
  private Set<Entry<V>> entrySet = null;
  
  public QNameMap() {}
  
  public void put(String paramString1, String paramString2, V paramV)
  {
    assert (paramString2 != null);
    assert (paramString1 != null);
    assert (paramString2 == paramString2.intern());
    assert (paramString1 == paramString1.intern());
    int i = hash(paramString2);
    int j = indexFor(i, table.length);
    for (Entry localEntry = table[j]; localEntry != null; localEntry = next) {
      if ((hash == i) && (paramString2 == localName) && (paramString1 == nsUri))
      {
        value = paramV;
        return;
      }
    }
    addEntry(i, paramString1, paramString2, paramV, j);
  }
  
  public void put(QName paramQName, V paramV)
  {
    put(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramV);
  }
  
  public void put(Name paramName, V paramV)
  {
    put(nsUri, localName, paramV);
  }
  
  public V get(String paramString1, String paramString2)
  {
    Entry localEntry = getEntry(paramString1, paramString2);
    if (localEntry == null) {
      return null;
    }
    return (V)value;
  }
  
  public V get(QName paramQName)
  {
    return (V)get(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public int size()
  {
    return size;
  }
  
  public QNameMap<V> putAll(QNameMap<? extends V> paramQNameMap)
  {
    int i = paramQNameMap.size();
    if (i == 0) {
      return this;
    }
    if (i > threshold)
    {
      int j = i;
      if (j > 1073741824) {
        j = 1073741824;
      }
      int k = table.length;
      while (k < j) {
        k <<= 1;
      }
      if (k > table.length) {
        resize(k);
      }
    }
    Iterator localIterator = paramQNameMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      put(nsUri, localName, localEntry.getValue());
    }
    return this;
  }
  
  private static int hash(String paramString)
  {
    int i = paramString.hashCode();
    i += (i << 9 ^ 0xFFFFFFFF);
    i ^= i >>> 14;
    i += (i << 4);
    i ^= i >>> 10;
    return i;
  }
  
  private static int indexFor(int paramInt1, int paramInt2)
  {
    return paramInt1 & paramInt2 - 1;
  }
  
  private void addEntry(int paramInt1, String paramString1, String paramString2, V paramV, int paramInt2)
  {
    Entry localEntry = table[paramInt2];
    table[paramInt2] = new Entry(paramInt1, paramString1, paramString2, paramV, localEntry);
    if (size++ >= threshold) {
      resize(2 * table.length);
    }
  }
  
  private void resize(int paramInt)
  {
    Entry[] arrayOfEntry1 = table;
    int i = arrayOfEntry1.length;
    if (i == 1073741824)
    {
      threshold = Integer.MAX_VALUE;
      return;
    }
    Entry[] arrayOfEntry2 = new Entry[paramInt];
    transfer(arrayOfEntry2);
    table = arrayOfEntry2;
    threshold = paramInt;
  }
  
  private void transfer(Entry<V>[] paramArrayOfEntry)
  {
    Entry[] arrayOfEntry = table;
    int i = paramArrayOfEntry.length;
    for (int j = 0; j < arrayOfEntry.length; j++)
    {
      Object localObject = arrayOfEntry[j];
      if (localObject != null)
      {
        arrayOfEntry[j] = null;
        do
        {
          Entry localEntry = next;
          int k = indexFor(hash, i);
          next = paramArrayOfEntry[k];
          paramArrayOfEntry[k] = localObject;
          localObject = localEntry;
        } while (localObject != null);
      }
    }
  }
  
  public Entry<V> getOne()
  {
    for (Entry localEntry : table) {
      if (localEntry != null) {
        return localEntry;
      }
    }
    return null;
  }
  
  public Collection<QName> keySet()
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      localHashSet.add(localEntry.createQName());
    }
    return localHashSet;
  }
  
  public boolean containsKey(String paramString1, String paramString2)
  {
    return getEntry(paramString1, paramString2) != null;
  }
  
  public boolean isEmpty()
  {
    return size == 0;
  }
  
  public Set<Entry<V>> entrySet()
  {
    Set localSet = entrySet;
    return localSet != null ? localSet : (entrySet = new EntrySet(null));
  }
  
  private Iterator<Entry<V>> newEntryIterator()
  {
    return new EntryIterator(null);
  }
  
  private Entry<V> getEntry(String paramString1, String paramString2)
  {
    assert (paramString1 == paramString1.intern());
    assert (paramString2 == paramString2.intern());
    int i = hash(paramString2);
    int j = indexFor(i, table.length);
    for (Entry localEntry = table[j]; (localEntry != null) && ((paramString2 != localName) || (paramString1 != nsUri)); localEntry = next) {}
    return localEntry;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('{');
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      if (localStringBuilder.length() > 1) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append('[');
      localStringBuilder.append(localEntry);
      localStringBuilder.append(']');
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public static final class Entry<V>
  {
    public final String nsUri;
    public final String localName;
    V value;
    final int hash;
    Entry<V> next;
    
    Entry(int paramInt, String paramString1, String paramString2, V paramV, Entry<V> paramEntry)
    {
      value = paramV;
      next = paramEntry;
      nsUri = paramString1;
      localName = paramString2;
      hash = paramInt;
    }
    
    public QName createQName()
    {
      return new QName(nsUri, localName);
    }
    
    public V getValue()
    {
      return (V)value;
    }
    
    public V setValue(V paramV)
    {
      Object localObject = value;
      value = paramV;
      return (V)localObject;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Entry)) {
        return false;
      }
      Entry localEntry = (Entry)paramObject;
      String str1 = nsUri;
      String str2 = nsUri;
      String str3 = localName;
      String str4 = localName;
      if ((str1 == str2) || ((str1 != null) && (str1.equals(str2)) && ((str3 == str4) || ((str3 != null) && (str3.equals(str4))))))
      {
        Object localObject1 = getValue();
        Object localObject2 = localEntry.getValue();
        if ((localObject1 == localObject2) || ((localObject1 != null) && (localObject1.equals(localObject2)))) {
          return true;
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      return localName.hashCode() ^ (value == null ? 0 : value.hashCode());
    }
    
    public String toString()
    {
      return '"' + nsUri + "\",\"" + localName + "\"=" + getValue();
    }
  }
  
  private class EntryIterator
    extends QNameMap<V>.HashIterator<QNameMap.Entry<V>>
  {
    private EntryIterator()
    {
      super();
    }
    
    public QNameMap.Entry<V> next()
    {
      return nextEntry();
    }
  }
  
  private class EntrySet
    extends AbstractSet<QNameMap.Entry<V>>
  {
    private EntrySet() {}
    
    public Iterator<QNameMap.Entry<V>> iterator()
    {
      return QNameMap.this.newEntryIterator();
    }
    
    public boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof QNameMap.Entry)) {
        return false;
      }
      QNameMap.Entry localEntry1 = (QNameMap.Entry)paramObject;
      QNameMap.Entry localEntry2 = QNameMap.this.getEntry(nsUri, localName);
      return (localEntry2 != null) && (localEntry2.equals(localEntry1));
    }
    
    public boolean remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public int size()
    {
      return size;
    }
  }
  
  private abstract class HashIterator<E>
    implements Iterator<E>
  {
    QNameMap.Entry<V> next;
    int index;
    
    HashIterator()
    {
      QNameMap.Entry[] arrayOfEntry = table;
      int i = arrayOfEntry.length;
      QNameMap.Entry localEntry = null;
      while ((size != 0) && (i > 0) && ((localEntry = arrayOfEntry[(--i)]) == null)) {}
      next = localEntry;
      index = i;
    }
    
    public boolean hasNext()
    {
      return next != null;
    }
    
    QNameMap.Entry<V> nextEntry()
    {
      QNameMap.Entry localEntry1 = next;
      if (localEntry1 == null) {
        throw new NoSuchElementException();
      }
      QNameMap.Entry localEntry2 = next;
      QNameMap.Entry[] arrayOfEntry = table;
      int i = index;
      while ((localEntry2 == null) && (i > 0)) {
        localEntry2 = arrayOfEntry[(--i)];
      }
      index = i;
      next = localEntry2;
      return localEntry1;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\util\QNameMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */