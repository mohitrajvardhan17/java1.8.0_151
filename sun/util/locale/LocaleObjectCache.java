package sun.util.locale;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class LocaleObjectCache<K, V>
{
  private ConcurrentMap<K, CacheEntry<K, V>> map;
  private ReferenceQueue<V> queue = new ReferenceQueue();
  
  public LocaleObjectCache()
  {
    this(16, 0.75F, 16);
  }
  
  public LocaleObjectCache(int paramInt1, float paramFloat, int paramInt2)
  {
    map = new ConcurrentHashMap(paramInt1, paramFloat, paramInt2);
  }
  
  public V get(K paramK)
  {
    Object localObject1 = null;
    cleanStaleEntries();
    CacheEntry localCacheEntry1 = (CacheEntry)map.get(paramK);
    if (localCacheEntry1 != null) {
      localObject1 = localCacheEntry1.get();
    }
    if (localObject1 == null)
    {
      Object localObject2 = createObject(paramK);
      paramK = normalizeKey(paramK);
      if ((paramK == null) || (localObject2 == null)) {
        return null;
      }
      CacheEntry localCacheEntry2 = new CacheEntry(paramK, localObject2, queue);
      localCacheEntry1 = (CacheEntry)map.putIfAbsent(paramK, localCacheEntry2);
      if (localCacheEntry1 == null)
      {
        localObject1 = localObject2;
      }
      else
      {
        localObject1 = localCacheEntry1.get();
        if (localObject1 == null)
        {
          map.put(paramK, localCacheEntry2);
          localObject1 = localObject2;
        }
      }
    }
    return (V)localObject1;
  }
  
  protected V put(K paramK, V paramV)
  {
    CacheEntry localCacheEntry1 = new CacheEntry(paramK, paramV, queue);
    CacheEntry localCacheEntry2 = (CacheEntry)map.put(paramK, localCacheEntry1);
    return localCacheEntry2 == null ? null : localCacheEntry2.get();
  }
  
  private void cleanStaleEntries()
  {
    CacheEntry localCacheEntry;
    while ((localCacheEntry = (CacheEntry)queue.poll()) != null) {
      map.remove(localCacheEntry.getKey());
    }
  }
  
  protected abstract V createObject(K paramK);
  
  protected K normalizeKey(K paramK)
  {
    return paramK;
  }
  
  private static class CacheEntry<K, V>
    extends SoftReference<V>
  {
    private K key;
    
    CacheEntry(K paramK, V paramV, ReferenceQueue<V> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      key = paramK;
    }
    
    K getKey()
    {
      return (K)key;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\LocaleObjectCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */