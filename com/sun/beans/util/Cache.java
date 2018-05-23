package com.sun.beans.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Objects;

public abstract class Cache<K, V>
{
  private static final int MAXIMUM_CAPACITY = 1073741824;
  private final boolean identity;
  private final Kind keyKind;
  private final Kind valueKind;
  private final ReferenceQueue<Object> queue = new ReferenceQueue();
  private volatile Cache<K, V>[].CacheEntry<K, V> table = newTable(8);
  private int threshold = 6;
  private int size;
  
  public abstract V create(K paramK);
  
  public Cache(Kind paramKind1, Kind paramKind2)
  {
    this(paramKind1, paramKind2, false);
  }
  
  public Cache(Kind paramKind1, Kind paramKind2, boolean paramBoolean)
  {
    Objects.requireNonNull(paramKind1, "keyKind");
    Objects.requireNonNull(paramKind2, "valueKind");
    keyKind = paramKind1;
    valueKind = paramKind2;
    identity = paramBoolean;
  }
  
  public final V get(K paramK)
  {
    Objects.requireNonNull(paramK, "key");
    removeStaleEntries();
    int i = hash(paramK);
    CacheEntry[] arrayOfCacheEntry = table;
    Object localObject1 = getEntryValue(paramK, i, arrayOfCacheEntry[index(i, arrayOfCacheEntry)]);
    if (localObject1 != null) {
      return (V)localObject1;
    }
    synchronized (queue)
    {
      localObject1 = getEntryValue(paramK, i, table[index(i, table)]);
      if (localObject1 != null) {
        return (V)localObject1;
      }
      Object localObject2 = create(paramK);
      Objects.requireNonNull(localObject2, "value");
      int j = index(i, table);
      table[j] = new CacheEntry(i, paramK, localObject2, table[j], null);
      if (++size >= threshold) {
        if (table.length == 1073741824)
        {
          threshold = Integer.MAX_VALUE;
        }
        else
        {
          removeStaleEntries();
          arrayOfCacheEntry = newTable(table.length << 1);
          transfer(table, arrayOfCacheEntry);
          if (size >= threshold / 2)
          {
            table = arrayOfCacheEntry;
            threshold <<= 1;
          }
          else
          {
            transfer(arrayOfCacheEntry, table);
          }
          removeStaleEntries();
        }
      }
      return (V)localObject2;
    }
  }
  
  public final void remove(K paramK)
  {
    if (paramK != null) {
      synchronized (queue)
      {
        removeStaleEntries();
        int i = hash(paramK);
        int j = index(i, table);
        Object localObject1 = table[j];
        CacheEntry localCacheEntry;
        for (Object localObject2 = localObject1; localObject2 != null; localObject2 = localCacheEntry)
        {
          localCacheEntry = next;
          if (((CacheEntry)localObject2).matches(i, paramK))
          {
            if (localObject2 == localObject1) {
              table[j] = localCacheEntry;
            } else {
              next = localCacheEntry;
            }
            ((CacheEntry)localObject2).unlink();
            break;
          }
          localObject1 = localObject2;
        }
      }
    }
  }
  
  public final void clear()
  {
    synchronized (queue)
    {
      int i = table.length;
      while (0 < i--)
      {
        CacheEntry localCacheEntry;
        for (Object localObject1 = table[i]; localObject1 != null; localObject1 = localCacheEntry)
        {
          localCacheEntry = next;
          ((CacheEntry)localObject1).unlink();
        }
        table[i] = null;
      }
      while (null != queue.poll()) {}
    }
  }
  
  private int hash(Object paramObject)
  {
    if (identity)
    {
      i = System.identityHashCode(paramObject);
      return (i << 1) - (i << 8);
    }
    int i = paramObject.hashCode();
    i ^= i >>> 20 ^ i >>> 12;
    return i ^ i >>> 7 ^ i >>> 4;
  }
  
  private static int index(int paramInt, Object[] paramArrayOfObject)
  {
    return paramInt & paramArrayOfObject.length - 1;
  }
  
  private Cache<K, V>[].CacheEntry<K, V> newTable(int paramInt)
  {
    return (CacheEntry[])new CacheEntry[paramInt];
  }
  
  private V getEntryValue(K paramK, int paramInt, Cache<K, V>.CacheEntry<K, V> paramCache)
  {
    while (paramCache != null)
    {
      if (paramCache.matches(paramInt, paramK)) {
        return (V)value.getReferent();
      }
      paramCache = next;
    }
    return null;
  }
  
  private void removeStaleEntries()
  {
    Reference localReference = queue.poll();
    if (localReference != null) {
      synchronized (queue)
      {
        do
        {
          if ((localReference instanceof Ref))
          {
            Ref localRef = (Ref)localReference;
            CacheEntry localCacheEntry1 = (CacheEntry)localRef.getOwner();
            if (localCacheEntry1 != null)
            {
              int i = index(hash, table);
              Object localObject1 = table[i];
              CacheEntry localCacheEntry2;
              for (Object localObject2 = localObject1; localObject2 != null; localObject2 = localCacheEntry2)
              {
                localCacheEntry2 = next;
                if (localObject2 == localCacheEntry1)
                {
                  if (localObject2 == localObject1) {
                    table[i] = localCacheEntry2;
                  } else {
                    next = localCacheEntry2;
                  }
                  ((CacheEntry)localObject2).unlink();
                  break;
                }
                localObject1 = localObject2;
              }
            }
          }
          localReference = queue.poll();
        } while (localReference != null);
      }
    }
  }
  
  private void transfer(Cache<K, V>[].CacheEntry<K, V> paramArrayOfCache1, Cache<K, V>[].CacheEntry<K, V> paramArrayOfCache2)
  {
    int i = paramArrayOfCache1.length;
    while (0 < i--)
    {
      Object localObject = paramArrayOfCache1[i];
      paramArrayOfCache1[i] = null;
      while (localObject != null)
      {
        CacheEntry localCacheEntry = next;
        if ((key.isStale()) || (value.isStale()))
        {
          ((CacheEntry)localObject).unlink();
        }
        else
        {
          int j = index(hash, paramArrayOfCache2);
          next = paramArrayOfCache2[j];
          paramArrayOfCache2[j] = localObject;
        }
        localObject = localCacheEntry;
      }
    }
  }
  
  private final class CacheEntry<K, V>
  {
    private final int hash;
    private final Cache.Ref<K> key;
    private final Cache.Ref<V> value;
    private volatile Cache<K, V>.CacheEntry<K, V> next;
    
    private CacheEntry(K paramK, V paramV, Cache<K, V>.CacheEntry<K, V> paramCache)
    {
      hash = paramK;
      key = keyKind.create(this, paramV, queue);
      value = valueKind.create(this, paramCache, queue);
      CacheEntry localCacheEntry;
      next = localCacheEntry;
    }
    
    private boolean matches(int paramInt, Object paramObject)
    {
      if (hash != paramInt) {
        return false;
      }
      Object localObject = key.getReferent();
      return (localObject == paramObject) || ((!identity) && (localObject != null) && (localObject.equals(paramObject)));
    }
    
    private void unlink()
    {
      next = null;
      key.removeOwner();
      value.removeOwner();
      Cache.access$1110(Cache.this);
    }
  }
  
  public static abstract enum Kind
  {
    STRONG,  SOFT,  WEAK;
    
    private Kind() {}
    
    abstract <T> Cache.Ref<T> create(Object paramObject, T paramT, ReferenceQueue<? super T> paramReferenceQueue);
    
    private static final class Soft<T>
      extends SoftReference<T>
      implements Cache.Ref<T>
    {
      private Object owner;
      
      private Soft(Object paramObject, T paramT, ReferenceQueue<? super T> paramReferenceQueue)
      {
        super(paramReferenceQueue);
        owner = paramObject;
      }
      
      public Object getOwner()
      {
        return owner;
      }
      
      public T getReferent()
      {
        return (T)get();
      }
      
      public boolean isStale()
      {
        return null == get();
      }
      
      public void removeOwner()
      {
        owner = null;
      }
    }
    
    private static final class Strong<T>
      implements Cache.Ref<T>
    {
      private Object owner;
      private final T referent;
      
      private Strong(Object paramObject, T paramT)
      {
        owner = paramObject;
        referent = paramT;
      }
      
      public Object getOwner()
      {
        return owner;
      }
      
      public T getReferent()
      {
        return (T)referent;
      }
      
      public boolean isStale()
      {
        return false;
      }
      
      public void removeOwner()
      {
        owner = null;
      }
    }
    
    private static final class Weak<T>
      extends WeakReference<T>
      implements Cache.Ref<T>
    {
      private Object owner;
      
      private Weak(Object paramObject, T paramT, ReferenceQueue<? super T> paramReferenceQueue)
      {
        super(paramReferenceQueue);
        owner = paramObject;
      }
      
      public Object getOwner()
      {
        return owner;
      }
      
      public T getReferent()
      {
        return (T)get();
      }
      
      public boolean isStale()
      {
        return null == get();
      }
      
      public void removeOwner()
      {
        owner = null;
      }
    }
  }
  
  private static abstract interface Ref<T>
  {
    public abstract Object getOwner();
    
    public abstract T getReferent();
    
    public abstract boolean isStale();
    
    public abstract void removeOwner();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\util\Cache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */