package sun.security.util;

class NullCache<K, V>
  extends Cache<K, V>
{
  static final Cache<Object, Object> INSTANCE = new NullCache();
  
  private NullCache() {}
  
  public int size()
  {
    return 0;
  }
  
  public void clear() {}
  
  public void put(K paramK, V paramV) {}
  
  public V get(Object paramObject)
  {
    return null;
  }
  
  public void remove(Object paramObject) {}
  
  public void setCapacity(int paramInt) {}
  
  public void setTimeout(int paramInt) {}
  
  public void accept(Cache.CacheVisitor<K, V> paramCacheVisitor) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\NullCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */