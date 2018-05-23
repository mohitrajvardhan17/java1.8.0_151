package sun.security.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class MemoryCache<K, V>
  extends Cache<K, V>
{
  private static final float LOAD_FACTOR = 0.75F;
  private static final boolean DEBUG = false;
  private final Map<K, CacheEntry<K, V>> cacheMap;
  private int maxSize;
  private long lifetime;
  private final ReferenceQueue<V> queue;
  
  public MemoryCache(boolean paramBoolean, int paramInt)
  {
    this(paramBoolean, paramInt, 0);
  }
  
  public MemoryCache(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    maxSize = paramInt1;
    lifetime = (paramInt2 * 1000);
    if (paramBoolean) {
      queue = new ReferenceQueue();
    } else {
      queue = null;
    }
    int i = (int)(paramInt1 / 0.75F) + 1;
    cacheMap = new LinkedHashMap(i, 0.75F, true);
  }
  
  private void emptyQueue()
  {
    if (queue == null) {
      return;
    }
    int i = cacheMap.size();
    for (;;)
    {
      CacheEntry localCacheEntry1 = (CacheEntry)queue.poll();
      if (localCacheEntry1 == null) {
        break;
      }
      Object localObject = localCacheEntry1.getKey();
      if (localObject != null)
      {
        CacheEntry localCacheEntry2 = (CacheEntry)cacheMap.remove(localObject);
        if ((localCacheEntry2 != null) && (localCacheEntry1 != localCacheEntry2)) {
          cacheMap.put(localObject, localCacheEntry2);
        }
      }
    }
  }
  
  private void expungeExpiredEntries()
  {
    emptyQueue();
    if (lifetime == 0L) {
      return;
    }
    int i = 0;
    long l = System.currentTimeMillis();
    Iterator localIterator = cacheMap.values().iterator();
    while (localIterator.hasNext())
    {
      CacheEntry localCacheEntry = (CacheEntry)localIterator.next();
      if (!localCacheEntry.isValid(l))
      {
        localIterator.remove();
        i++;
      }
    }
  }
  
  public synchronized int size()
  {
    expungeExpiredEntries();
    return cacheMap.size();
  }
  
  public synchronized void clear()
  {
    if (queue != null)
    {
      Iterator localIterator = cacheMap.values().iterator();
      while (localIterator.hasNext())
      {
        CacheEntry localCacheEntry = (CacheEntry)localIterator.next();
        localCacheEntry.invalidate();
      }
      while (queue.poll() != null) {}
    }
    cacheMap.clear();
  }
  
  public synchronized void put(K paramK, V paramV)
  {
    emptyQueue();
    long l = lifetime == 0L ? 0L : System.currentTimeMillis() + lifetime;
    CacheEntry localCacheEntry1 = newEntry(paramK, paramV, l, queue);
    CacheEntry localCacheEntry2 = (CacheEntry)cacheMap.put(paramK, localCacheEntry1);
    if (localCacheEntry2 != null)
    {
      localCacheEntry2.invalidate();
      return;
    }
    if ((maxSize > 0) && (cacheMap.size() > maxSize))
    {
      expungeExpiredEntries();
      if (cacheMap.size() > maxSize)
      {
        Iterator localIterator = cacheMap.values().iterator();
        CacheEntry localCacheEntry3 = (CacheEntry)localIterator.next();
        localIterator.remove();
        localCacheEntry3.invalidate();
      }
    }
  }
  
  public synchronized V get(Object paramObject)
  {
    emptyQueue();
    CacheEntry localCacheEntry = (CacheEntry)cacheMap.get(paramObject);
    if (localCacheEntry == null) {
      return null;
    }
    long l = lifetime == 0L ? 0L : System.currentTimeMillis();
    if (!localCacheEntry.isValid(l))
    {
      cacheMap.remove(paramObject);
      return null;
    }
    return (V)localCacheEntry.getValue();
  }
  
  public synchronized void remove(Object paramObject)
  {
    emptyQueue();
    CacheEntry localCacheEntry = (CacheEntry)cacheMap.remove(paramObject);
    if (localCacheEntry != null) {
      localCacheEntry.invalidate();
    }
  }
  
  public synchronized void setCapacity(int paramInt)
  {
    expungeExpiredEntries();
    if ((paramInt > 0) && (cacheMap.size() > paramInt))
    {
      Iterator localIterator = cacheMap.values().iterator();
      for (int i = cacheMap.size() - paramInt; i > 0; i--)
      {
        CacheEntry localCacheEntry = (CacheEntry)localIterator.next();
        localIterator.remove();
        localCacheEntry.invalidate();
      }
    }
    maxSize = (paramInt > 0 ? paramInt : 0);
  }
  
  public synchronized void setTimeout(int paramInt)
  {
    emptyQueue();
    lifetime = (paramInt > 0 ? paramInt * 1000L : 0L);
  }
  
  public synchronized void accept(Cache.CacheVisitor<K, V> paramCacheVisitor)
  {
    expungeExpiredEntries();
    Map localMap = getCachedEntries();
    paramCacheVisitor.visit(localMap);
  }
  
  private Map<K, V> getCachedEntries()
  {
    HashMap localHashMap = new HashMap(cacheMap.size());
    Iterator localIterator = cacheMap.values().iterator();
    while (localIterator.hasNext())
    {
      CacheEntry localCacheEntry = (CacheEntry)localIterator.next();
      localHashMap.put(localCacheEntry.getKey(), localCacheEntry.getValue());
    }
    return localHashMap;
  }
  
  protected CacheEntry<K, V> newEntry(K paramK, V paramV, long paramLong, ReferenceQueue<V> paramReferenceQueue)
  {
    if (paramReferenceQueue != null) {
      return new SoftCacheEntry(paramK, paramV, paramLong, paramReferenceQueue);
    }
    return new HardCacheEntry(paramK, paramV, paramLong);
  }
  
  private static abstract interface CacheEntry<K, V>
  {
    public abstract boolean isValid(long paramLong);
    
    public abstract void invalidate();
    
    public abstract K getKey();
    
    public abstract V getValue();
  }
  
  private static class HardCacheEntry<K, V>
    implements MemoryCache.CacheEntry<K, V>
  {
    private K key;
    private V value;
    private long expirationTime;
    
    HardCacheEntry(K paramK, V paramV, long paramLong)
    {
      key = paramK;
      value = paramV;
      expirationTime = paramLong;
    }
    
    public K getKey()
    {
      return (K)key;
    }
    
    public V getValue()
    {
      return (V)value;
    }
    
    public boolean isValid(long paramLong)
    {
      boolean bool = paramLong <= expirationTime;
      if (!bool) {
        invalidate();
      }
      return bool;
    }
    
    public void invalidate()
    {
      key = null;
      value = null;
      expirationTime = -1L;
    }
  }
  
  private static class SoftCacheEntry<K, V>
    extends SoftReference<V>
    implements MemoryCache.CacheEntry<K, V>
  {
    private K key;
    private long expirationTime;
    
    SoftCacheEntry(K paramK, V paramV, long paramLong, ReferenceQueue<V> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      key = paramK;
      expirationTime = paramLong;
    }
    
    public K getKey()
    {
      return (K)key;
    }
    
    public V getValue()
    {
      return (V)get();
    }
    
    public boolean isValid(long paramLong)
    {
      boolean bool = (paramLong <= expirationTime) && (get() != null);
      if (!bool) {
        invalidate();
      }
      return bool;
    }
    
    public void invalidate()
    {
      clear();
      key = null;
      expirationTime = -1L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\MemoryCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */